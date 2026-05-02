package thread.search;

import thread.search.core.SearchEngine;
import thread.search.storage.DirectFileSearchStorage;
import thread.search.strategy.LineByLineSearchStrategy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SequentialSearchService implements SearchService {
    private final SearchEngine engine = new SearchEngine(
            new DirectFileSearchStorage(),
            new LineByLineSearchStrategy()
    );

    @Override
    public List<SearchResult> search(List<Path> datasetDirectories, String target) {
        List<SearchResult> results = new ArrayList<>();

        for (Path directory : datasetDirectories) {
            scanDirectory(directory, target, results);
        }

        return results;
    }

    private void scanDirectory(Path directory, String target, List<SearchResult> results) {
        if (!Files.exists(directory)) {
            throw new IllegalArgumentException("Diretorio nao encontrado: " + directory);
        }

        try {
            try (var stream = Files.walk(directory)) {
                stream.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".txt"))
                        .sorted(Comparator.naturalOrder())
                        .forEach(path -> scanFile(path, target, results));
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao ler diretorio: " + directory, exception);
        }
    }

    private void scanFile(Path file, String target, List<SearchResult> results) {
        results.addAll(engine.search(file, target));
    }
}
