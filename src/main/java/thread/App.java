package thread;

import model.CharByCharFinder;
import model.Dataset;
import model.DatasetType;
import model.Executor;
import model.Finder;
import model.InMemory;
import model.LineByLineFinder;
import model.Result;
import model.RegexFinder;
import thread.search.ParallelSearchService;
import thread.search.SearchMode;
import thread.search.SearchResult;
import thread.search.SearchService;
import thread.search.SequentialSearchService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class App {

    private App() {
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }

        List<Dataset> datasets = buildDatasets();
        List<Path> datasetDirectories = datasets.stream()
                .map(dataset -> Path.of(dataset.folder()))
                .toList();
        Executor executor = buildExecutor();

        SearchMode mode = SearchMode.from(args[0]);
        String target = args[1];
        SearchService searchService = mode == SearchMode.PARALLEL
                ? new ParallelSearchService()
                : new SequentialSearchService();

        long startedAt = System.nanoTime();
        List<SearchResult> results = searchService.search(datasetDirectories, target);
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

    private static Executor buildExecutor() {
        return new Executor(buildFinder(), InMemory.NONE);
    }

    private static Finder buildFinder() {
        String rawFinder = System.getenv().getOrDefault("FINDER_MODE", "line_by_line").toLowerCase();
        return switch (rawFinder) {
            case "line_by_line", "line", "line-by-line" -> new LineByLineFinder();
            case "char_by_char", "char", "char-by-char" -> new CharByCharFinder();
            case "regex" -> new RegexFinder();
            default -> throw new IllegalArgumentException("Finder invalido: " + rawFinder);
        };
    }

    private static List<Dataset> buildDatasets() {
        List<Dataset> datasets = new ArrayList<>();
        datasets.add(new DatasetSpec(
                DatasetType.G,
                System.getenv().getOrDefault("DATASET_G_DIR", "/datasets/dataset_g"),
                "dataset_g",
                10_000
        ));
        datasets.add(new DatasetSpec(
                DatasetType.P,
                System.getenv().getOrDefault("DATASET_P_DIR", "/datasets/dataset_p"),
                "dataset_p",
                10_000
        ));
        return List.copyOf(datasets);
    }

    private static void printUsage() {
        System.out.println("Uso: java -jar app.jar <sync|parallel> <nome>");
    }

    private record DatasetSpec(
            DatasetType type,
            String folder,
            String fileStructName,
            int numOfLines
    ) implements Dataset {

        @Override
        public Result findName(String name) {
            throw new UnsupportedOperationException(
                    "Busca ainda nao implementada para o dataset " + type
            );
        }
    }
}
