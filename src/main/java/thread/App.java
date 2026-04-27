package thread;

import thread.search.ParallelSearchService;
import thread.search.SearchMode;
import thread.search.SearchResult;
import thread.search.SearchService;
import thread.search.SequentialSearchService;

import java.nio.file.Path;
import java.util.List;

public final class App {

    private static final List<Path> DATASET_DIRECTORIES = List.of(
            Path.of(System.getenv().getOrDefault("DATASET_G_DIR", "/datasets/dataset_g")),
            Path.of(System.getenv().getOrDefault("DATASET_P_DIR", "/datasets/dataset_p"))
    );

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
        List<SearchResult> results = searchService.search(DATASET_DIRECTORIES, target);
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
    }

    private static void printUsage() {
        System.out.println("Uso: java -jar app.jar <sync|parallel> <nome>");
    }
}
