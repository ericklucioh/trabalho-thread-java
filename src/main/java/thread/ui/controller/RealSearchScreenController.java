package thread.ui.controller;

import thread.search.SearchResult;
import thread.search.core.SearchEngine;
import thread.search.core.SearchChunk;
import thread.search.core.SearchStrategy;
import thread.search.core.SearchStorage;
import thread.search.storage.DirectFileSearchStorage;
import thread.search.storage.InMemoryLinesSearchStorage;
import thread.search.storage.InMemoryTextSearchStorage;
import thread.search.threading.DivisoraDeArquivosParaThreads;
import thread.search.strategy.CharByCharSearchStrategy;
import thread.search.strategy.LineByLineSearchStrategy;
import thread.search.strategy.RegexSearchStrategy;
import thread.ui.model.DatasetSelectionOption;
import thread.ui.model.SearchHistoryEntry;
import thread.ui.model.SearchRequest;
import thread.ui.model.SearchResultViewModel;
import thread.ui.model.SearchStorageOption;
import thread.ui.model.SearchStrategyOption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

public final class RealSearchScreenController implements SearchScreenController {
    private static final long DATASET_G_LINE_COUNT = 10_000L;
    private static final long DATASET_P_LINE_COUNT = 1_000L;
    private static final List<String> SAMPLE_NAMES = List.of(
            "Sharon Sullivan",
            "Tracy Bass",
            "Charles Holloway",
            "Carolyn Hale",
            "Danielle Hall",
            "Stacy Porter",
            "Penny Black"
    );

    private final Map<DatasetSelectionOption, Path> datasetRoots;
    private final List<SearchHistoryEntry> history = new ArrayList<>();

    public RealSearchScreenController() {
        this(List.of(Path.of("./dataset_g"), Path.of("./dataset_p")));
    }

    public RealSearchScreenController(List<Path> datasetRoots) {
        Objects.requireNonNull(datasetRoots, "datasetRoots");
        this.datasetRoots = buildDatasetRootMap(datasetRoots);
    }

    @Override
    public List<String> sampleNames() {
        return SAMPLE_NAMES;
    }

    @Override
    public List<DatasetSelectionOption> datasetSelections() {
        return List.of(DatasetSelectionOption.values());
    }

    @Override
    public List<SearchStrategyOption> searchStrategies() {
        return List.of(SearchStrategyOption.values());
    }

    @Override
    public List<SearchStorageOption> searchStorageOptions() {
        return List.of(SearchStorageOption.values());
    }

    @Override
    public SearchResultViewModel search(SearchRequest request) {
        Objects.requireNonNull(request, "request");

        String target = normalize(request.targetName());
        String datasetsText = datasetsText(request.datasets());

        if (target.isBlank()) {
            return recordResult(
                    new SearchResultViewModel(
                            false,
                            "",
                            datasetsText,
                            "",
                            "",
                            0,
                            "0.000 ms",
                            "Informe um nome para pesquisar.",
                            request.searchStrategy(),
                            request.searchStorage(),
                            request.threads(),
                            request.specialMode()
                    ),
                    "Nao encontrado"
            );
        }

        List<Path> selectedRoots = selectedDatasetRoots(request.datasets());
        if (selectedRoots.isEmpty()) {
            return recordResult(
                    new SearchResultViewModel(
                            false,
                            target,
                            datasetsText,
                            "",
                            "",
                            0,
                            "0.000 ms",
                            "Selecione ao menos um dataset.",
                            request.searchStrategy(),
                            request.searchStorage(),
                            request.threads(),
                            request.specialMode()
                    ),
                    "Nao encontrado"
            );
        }

        try {
            if (request.searchStrategy() == SearchStrategyOption.REGEX) {
                validateRegex(target);
            }
        } catch (PatternSyntaxException exception) {
            return recordResult(
                    new SearchResultViewModel(
                            false,
                            target,
                            datasetsText,
                            "",
                            "",
                            0,
                            "0.000 ms",
                            "Expressao regular invalida: " + exception.getDescription(),
                            request.searchStrategy(),
                            request.searchStorage(),
                            request.threads(),
                            request.specialMode()
                    ),
                    "Nao encontrado"
            );
        }

        long startedAt = System.nanoTime();
        SearchStorage storage = storageFor(request.searchStorage());
        SearchEngine engine = engineFor(storage, request.searchStrategy());
        List<Path> files = collectFiles(selectedRoots);
        List<SearchResult> matches = request.threads() <= 1 && !request.specialMode()
                ? searchSequentially(files, target, engine)
                : searchChunks(
                        DivisoraDeArquivosParaThreads.dividir(
                                files,
                                request.threads(),
                                storage,
                                knownLineCounts(request.datasets(), files)
                        ),
                        target,
                        engine,
                        request.threads(),
                        request.specialMode()
                );
        long elapsedNanos = System.nanoTime() - startedAt;

        matches.sort(Comparator
                .comparing((SearchResult result) -> result.file().toString())
                .thenComparingLong(SearchResult::lineNumber));

        boolean found = !matches.isEmpty();
        SearchResult firstMatch = found ? matches.get(0) : null;
        String message = found
                ? request.specialMode()
                ? String.format(Locale.ROOT, "Busca concluida com sucesso. %d ocorrencia(s) encontradas.", matches.size())
                : "Busca concluida com sucesso."
                : "Nenhum resultado foi localizado.";

        return recordResult(
                new SearchResultViewModel(
                        found,
                        target,
                        datasetsText,
                        found ? firstMatch.match() : "",
                        found ? firstMatch.file().toString() : "",
                        found ? (int) firstMatch.lineNumber() : 0,
                        formatElapsed(elapsedNanos),
                        message,
                        request.searchStrategy(),
                        request.searchStorage(),
                        request.threads(),
                        request.specialMode()
                ),
                found ? "Encontrado" : "Nao encontrado"
        );
    }

    @Override
    public List<SearchHistoryEntry> history() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }

    private SearchResultViewModel recordResult(SearchResultViewModel result, String status) {
        history.add(0, new SearchHistoryEntry(
                result.targetName(),
                result.datasetsText(),
                result.matchedName().isBlank() ? "-" : result.matchedName(),
                result.fileName().isBlank() ? "-" : result.fileName(),
                result.lineNumber(),
                result.elapsedText(),
                result.searchStrategy(),
                result.searchStorage(),
                result.threads(),
                result.specialMode(),
                status
        ));
        return result;
    }

    private List<SearchResult> searchChunks(
            List<SearchChunk> chunks,
            String target,
            SearchEngine engine,
            int threads,
            boolean specialMode
    ) {
        if (chunks.isEmpty()) {
            return List.of();
        }

        int workerCount = Math.max(1, Math.min(threads, chunks.size()));
        if (workerCount == 1) {
            List<SearchResult> results = new ArrayList<>();
            for (SearchChunk chunk : chunks) {
                results.addAll(scanChunk(chunk, target, engine));
                if (!specialMode && !results.isEmpty()) {
                    break;
                }
            }
            return results;
        }

        ExecutorService executor = Executors.newFixedThreadPool(workerCount);
        try {
            List<Callable<List<SearchResult>>> tasks = chunks.stream()
                    .map(chunk -> (Callable<List<SearchResult>>) () -> scanChunk(chunk, target, engine))
                    .toList();

            List<Future<List<SearchResult>>> futures = executor.invokeAll(tasks);
            List<SearchResult> results = new ArrayList<>();
            for (Future<List<SearchResult>> future : futures) {
                results.addAll(future.get());
            }
            return results;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Busca interrompida", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Falha na busca", exception.getCause());
        } finally {
            executor.shutdownNow();
        }
    }

    private List<SearchResult> searchSequentially(
            List<Path> files,
            String target,
            SearchEngine engine
    ) {
        List<SearchResult> results = new ArrayList<>();
        for (Path file : files) {
            results.addAll(engine.search(file, target));
            if (!results.isEmpty()) {
                break;
            }
        }
        return results;
    }

    private List<SearchResult> scanChunk(
            SearchChunk chunk,
            String target,
            SearchEngine engine
    ) {
        return engine.search(chunk, target);
    }

    private List<Path> collectFiles(List<Path> roots) {
        Set<Path> files = new java.util.LinkedHashSet<>();
        for (Path root : roots) {
            if (!Files.exists(root)) {
                throw new IllegalArgumentException("Diretorio nao encontrado: " + root);
            }

            try (Stream<Path> stream = Files.walk(root)) {
                stream.filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".txt"))
                        .sorted(Comparator.naturalOrder())
                        .forEach(files::add);
            } catch (IOException exception) {
                throw new IllegalStateException("Falha ao ler diretorio: " + root, exception);
            }
        }

        return List.copyOf(files);
    }

    private List<Path> selectedDatasetRoots(List<DatasetSelectionOption> datasets) {
        if (datasets == null || datasets.isEmpty()) {
            return List.of();
        }

        List<Path> selectedRoots = new ArrayList<>();
        for (DatasetSelectionOption option : datasets) {
            Path root = datasetRoots.getOrDefault(option, Path.of(option.path()));
            selectedRoots.add(root);
        }
        return List.copyOf(selectedRoots);
    }

    private Map<DatasetSelectionOption, Path> buildDatasetRootMap(List<Path> roots) {
        Map<DatasetSelectionOption, Path> mappedRoots = new EnumMap<>(DatasetSelectionOption.class);
        DatasetSelectionOption[] options = DatasetSelectionOption.values();
        for (int i = 0; i < options.length && i < roots.size(); i++) {
            mappedRoots.put(options[i], roots.get(i));
        }
        return Collections.unmodifiableMap(mappedRoots);
    }

    private Map<Path, Long> knownLineCounts(List<DatasetSelectionOption> datasets, List<Path> files) {
        Map<Path, Long> counts = new LinkedHashMap<>();
        for (Path file : files) {
            counts.put(file, knownLineCount(datasets, file));
        }
        return counts;
    }

    private long knownLineCount(List<DatasetSelectionOption> datasets, Path file) {
        if (datasets != null) {
            for (DatasetSelectionOption option : datasets) {
                Path root = datasetRoots.getOrDefault(option, Path.of(option.path()));
                if (file.startsWith(root)) {
                    return switch (option) {
                        case DATASET_G -> DATASET_G_LINE_COUNT;
                        case DATASET_P -> DATASET_P_LINE_COUNT;
                    };
                }
            }
        }

        throw new IllegalStateException("Nao foi possivel determinar a quantidade de linhas para: " + file);
    }

    private String datasetsText(List<DatasetSelectionOption> datasets) {
        if (datasets == null || datasets.isEmpty()) {
            return "nenhum dataset";
        }

        return datasets.stream()
                .map(DatasetSelectionOption::label)
                .reduce((left, right) -> left + ", " + right)
                .orElse("nenhum dataset");
    }

    private void validateRegex(String target) {
        java.util.regex.Pattern.compile(target);
    }

    private SearchEngine engineFor(SearchStorage storage, SearchStrategyOption strategyOption) {
        SearchStrategy strategy = strategyFor(strategyOption);
        return new SearchEngine(storage, strategy);
    }

    private SearchStorage storageFor(SearchStorageOption option) {
        if (option == null) {
            return new DirectFileSearchStorage();
        }

        return switch (option) {
            case IN_FILE -> new DirectFileSearchStorage();
            case IN_MEMORY_LIST_OF_FILES_OF_LINE -> new InMemoryLinesSearchStorage();
            case IN_MEMORY_LIST_OF_FILES -> new InMemoryTextSearchStorage();
        };
    }

    private SearchStrategy strategyFor(SearchStrategyOption option) {
        if (option == null) {
            return new LineByLineSearchStrategy();
        }

        return switch (option) {
            case LINE_BY_LINE -> new LineByLineSearchStrategy();
            case CHAR_BY_CHAR -> new CharByCharSearchStrategy();
            case REGEX -> new RegexSearchStrategy();
        };
    }

    private String formatElapsed(long elapsedNanos) {
        return String.format(Locale.ROOT, "%.3f ms", elapsedNanos / 1_000_000.0);
    }

    private String normalize(String text) {
        return text == null ? "" : text.trim();
    }
}
