package thread.ui.controller;

import thread.ui.model.DatasetSelectionOption;
import thread.ui.model.SearchHistoryEntry;
import thread.ui.model.SearchRequest;
import thread.ui.model.SearchResultViewModel;
import thread.ui.model.SearchStorageOption;
import thread.ui.model.SearchStrategyOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class MockSearchScreenController implements SearchScreenController {
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

    private final List<SearchHistoryEntry> history = new ArrayList<>();
    private final AtomicInteger runCounter = new AtomicInteger();

    public MockSearchScreenController() {
        history.add(new SearchHistoryEntry(
                "Ana",
                "dataset_g",
                "Ana",
                "dataset_g/a1.txt",
                4,
                "12.4 ms",
                SearchStrategyOption.LINE_BY_LINE,
                SearchStorageOption.IN_FILE,
                1,
                false,
                "Encontrado"
        ));
        history.add(new SearchHistoryEntry(
                "Rafael",
                "dataset_p",
                "Rafael",
                "dataset_p/arq_3.txt",
                18,
                "9.8 ms",
                SearchStrategyOption.CHAR_BY_CHAR,
                SearchStorageOption.IN_MEMORY_LIST_OF_FILES_OF_LINE,
                12,
                false,
                "Encontrado"
        ));
        history.add(new SearchHistoryEntry(
                "Xavier",
                "dataset_g, dataset_p",
                "-",
                "-",
                0,
                "14.1 ms",
                SearchStrategyOption.REGEX,
                SearchStorageOption.IN_MEMORY_LIST_OF_FILES,
                1,
                true,
                "Nao encontrado"
        ));
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

        String targetName = normalize(request.targetName());
        if (targetName.isBlank()) {
            SearchResultViewModel result = new SearchResultViewModel(
                    false,
                    "",
                    datasetsText(request),
                    "",
                    "",
                    0,
                    buildElapsedText(runCounter.incrementAndGet()),
                    "Informe um nome para simular a busca.",
                    request.searchStrategy(),
                    request.searchStorage(),
                    request.threads(),
                    request.specialMode()
            );
            addHistory(result, request, "Nao encontrado");
            return result;
        }

        int run = runCounter.incrementAndGet();
        boolean found = !targetName.equalsIgnoreCase("inexistente");
        String matchedName = found ? targetName : "";
        String fileName = found ? mockFileName(targetName) : "-";
        int lineNumber = found ? mockLineNumber(targetName) : 0;
        String elapsedText = buildElapsedText(run);
        String message = found
                ? "Busca concluida com sucesso."
                : "Nenhum resultado foi localizado.";

        SearchResultViewModel result = new SearchResultViewModel(
                found,
                targetName,
                datasetsText(request),
                matchedName,
                fileName,
                lineNumber,
                elapsedText,
                message,
                request.searchStrategy(),
                request.searchStorage(),
                request.threads(),
                request.specialMode()
        );
        addHistory(result, request, found ? "Encontrado" : "Nao encontrado");
        return result;
    }

    @Override
    public List<SearchHistoryEntry> history() {
        return Collections.unmodifiableList(new ArrayList<>(history));
    }

    private void addHistory(SearchResultViewModel result, SearchRequest request, String status) {
        history.add(0, new SearchHistoryEntry(
                result.targetName(),
                result.datasetsText(),
                result.matchedName().isBlank() ? "-" : result.matchedName(),
                result.fileName().isBlank() ? "-" : result.fileName(),
                result.lineNumber(),
                result.elapsedText(),
                request.searchStrategy(),
                request.searchStorage(),
                request.threads(),
                request.specialMode(),
                status
        ));
    }

    private String buildElapsedText(int run) {
        double elapsedMs = 8.0 + (run * 1.7);
        return String.format(Locale.ROOT, "%.1f ms", elapsedMs);
    }

    private String mockFileName(String targetName) {
        int index = Math.abs(targetName.toLowerCase(Locale.ROOT).hashCode()) % 7 + 1;
        return "dataset_g/a" + index + ".txt";
    }

    private int mockLineNumber(String targetName) {
        return Math.abs(targetName.hashCode()) % 40 + 1;
    }

    private String datasetsText(SearchRequest request) {
        List<DatasetSelectionOption> datasets = request.datasets();
        if (datasets == null || datasets.isEmpty()) {
            return "nenhum dataset";
        }

        return datasets.stream()
                .map(DatasetSelectionOption::label)
                .reduce((left, right) -> left + ", " + right)
                .orElse("nenhum dataset");
    }

    private String normalize(String text) {
        return text == null ? "" : text.trim();
    }
}
