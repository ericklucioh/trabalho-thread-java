package thread.ui.controller;

import model.CharByCharFinder;
import model.Finder;
import model.LineByLineFinder;
import model.RegexFinder;
import thread.search.SearchResult;
import thread.ui.model.DatasetSelectionOption;
import thread.ui.model.SearchHistoryEntry;
import thread.ui.model.SearchRequest;
import thread.ui.model.SearchResultViewModel;
import thread.ui.model.SearchStorageOption;
import thread.ui.model.SearchStrategyOption;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashSet;
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
    private static final List<String> SAMPLE_NAMES = List.of(
            "Ana",
            "Bruno",
            "Carlos",
            "Daniela",
            "Eduardo",
            "Fernanda",
            "Gabriel",
            "Helena",
            "Igor",
            "Juliana",
            "Kaique",
            "Larissa",
            "Marcos",
            "Nathalia",
            "Otavio",
            "Patricia",
            "Rafael",
            "Sabrina",
            "Thiago",
            "Vanessa"
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
        Finder finder = finderFor(request.searchStrategy());
        List<Path> files = collectFiles(selectedRoots);
        List<SearchResult> matches = searchFiles(
                files,
                target,
                finder,
                request.searchStorage(),
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

    private Finder finderFor(SearchStrategyOption option) {
        if (option == null) {
            return new LineByLineFinder();
        }

        return switch (option) {
            case LINE_BY_LINE -> new LineByLineFinder();
            case CHAR_BY_CHAR -> new CharByCharFinder();
            case REGEX -> new RegexFinder();
        };
    }

    private List<SearchResult> searchFiles(
            List<Path> files,
            String target,
            Finder finder,
            SearchStorageOption storage,
            int threads,
            boolean specialMode
    ) {
        if (files.isEmpty()) {
            return List.of();
        }

        int workerCount = Math.max(1, Math.min(threads, files.size()));
        if (workerCount == 1) {
            List<SearchResult> results = new ArrayList<>();
            for (Path file : files) {
                results.addAll(scanFile(file, target, finder, storage));
                if (!specialMode && !results.isEmpty()) {
                    break;
                }
            }
            return results;
        }

        ExecutorService executor = Executors.newFixedThreadPool(workerCount);
        try {
            List<Callable<List<SearchResult>>> tasks = files.stream()
                    .map(file -> (Callable<List<SearchResult>>) () -> scanFile(file, target, finder, storage))
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

    private List<SearchResult> scanFile(
            Path file,
            String target,
            Finder finder,
            SearchStorageOption storage
    ) {
        return switch (storage == null ? SearchStorageOption.IN_FILE : storage) {
            case IN_FILE -> scanFileFromDisk(file, target, finder);
            case IN_MEMORY_LIST_OF_FILES_OF_LINE -> scanFileFromLinesInMemory(file, target, finder);
            case IN_MEMORY_LIST_OF_FILES -> scanFileFromWholeFileInMemory(file, target, finder);
        };
    }

    private List<SearchResult> scanFileFromDisk(Path file, String target, Finder finder) {
        List<SearchResult> results = new ArrayList<>();
        try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
            final long[] lineNumber = {0};
            lines.forEach(line -> {
                lineNumber[0]++;
                if (finder.matches(line, target)) {
                    results.add(new SearchResult(line, file, lineNumber[0]));
                }
            });
            return results;
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao ler arquivo: " + file, exception);
        }
    }

    private List<SearchResult> scanFileFromLinesInMemory(Path file, String target, Finder finder) {
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            List<SearchResult> results = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (finder.matches(line, target)) {
                    results.add(new SearchResult(line, file, i + 1));
                }
            }
            return results;
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao ler arquivo: " + file, exception);
        }
    }

    private List<SearchResult> scanFileFromWholeFileInMemory(Path file, String target, Finder finder) {
        try {
            String content = Files.readString(file, StandardCharsets.UTF_8);
            String[] lines = content.split("\\R", -1);
            List<SearchResult> results = new ArrayList<>();
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (finder.matches(line, target)) {
                    results.add(new SearchResult(line, file, i + 1));
                }
            }
            return results;
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao ler arquivo: " + file, exception);
        }
    }

    private List<Path> collectFiles(List<Path> roots) {
        Set<Path> files = new LinkedHashSet<>();
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

    private String formatElapsed(long elapsedNanos) {
        return String.format(Locale.ROOT, "%.3f ms", elapsedNanos / 1_000_000.0);
    }

    private String normalize(String text) {
        return text == null ? "" : text.trim();
    }
}
