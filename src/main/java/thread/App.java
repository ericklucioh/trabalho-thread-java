package thread;

import thread.search.ParallelSearchService;
import thread.search.SearchMode;
import thread.search.SearchResult;
import thread.search.SearchService;
import thread.search.SequentialSearchService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class App {

    private App() {
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }

        SearchMode mode = SearchMode.from(args[0]);
        String target = args[1];
        SearchService searchService = mode == SearchMode.PARALLEL
                ? new ParallelSearchService()
                : new SequentialSearchService();

        long startedAt = System.nanoTime();
        List<SearchResult> results = searchService.search(buildDatasetDirectories(), target);
        long elapsedNanos = System.nanoTime() - startedAt;

        if (results.isEmpty()) {
            System.out.printf("Nome '%s' nao encontrado.%n", target);
        } else {
            for (SearchResult result : results) {
                System.out.printf(
                        "Nome encontrado: %s | arquivo: %s | linha: %d%n",
                        result.match(),
                        result.file(),
                        result.lineNumber()
                );
            }
        }

        System.out.printf("Modo: %s | tempo: %.3f ms%n", mode.name().toLowerCase(), elapsedNanos / 1_000_000.0);
        writeResultsCsv(mode, target, results, elapsedNanos);
    }

    private static List<Path> buildDatasetDirectories() {
        String datasetGDir = requiredEnv("DATASET_G_DIR");
        String datasetPDir = requiredEnv("DATASET_P_DIR");

        List<Path> datasetDirectories = new ArrayList<>();
        datasetDirectories.add(Path.of(datasetGDir));
        datasetDirectories.add(Path.of(datasetPDir));
        return List.copyOf(datasetDirectories);
    }

    private static String requiredEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Variavel de ambiente obrigatoria ausente: " + name);
        }
        return value;
    }

    private static void printUsage() {
        System.out.println("Uso: java -jar app.jar <sync|parallel> <nome>");
        System.out.println("As variaveis DATASET_G_DIR e DATASET_P_DIR devem estar definidas no ambiente.");
    }

    private static void writeResultsCsv(
            SearchMode mode,
            String target,
            List<SearchResult> results,
            long elapsedNanos
    ) {
        try {
            Path resultsDir = Path.of(requiredEnv("RESULT_DIR"));
            Path resultsCsv = resultsDir.resolve("search-results.csv");
            Files.createDirectories(resultsDir);
            List<String> lines = new ArrayList<>();
            lines.add("mode;target;match;file;line;elapsed_ms");

            if (results.isEmpty()) {
                lines.add(csvRow(mode.name(), target, "NOT_FOUND", "", "", elapsedNanos));
            } else {
                for (SearchResult result : results) {
                    lines.add(csvRow(
                            mode.name(),
                            target,
                            result.match(),
                            result.file().toString(),
                            Long.toString(result.lineNumber()),
                            elapsedNanos
                    ));
                }
            }

            Files.write(
                    resultsCsv,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );
            System.out.printf("CSV gerado em: %s%n", resultsCsv.toAbsolutePath());
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao gerar CSV de resultados", exception);
        }
    }

    private static String csvRow(
            String mode,
            String target,
            String match,
            String file,
            String line,
            long elapsedNanos
    ) {
        return String.join(
                ";",
                escapeCsv(mode),
                escapeCsv(target),
                escapeCsv(match),
                escapeCsv(file),
                escapeCsv(line),
                String.format(Locale.ROOT, "%.3f", elapsedNanos / 1_000_000.0)
        );
    }

    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        if (value.contains(";") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }
}
