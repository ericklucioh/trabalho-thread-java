package thread.search;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SequentialSearchService implements SearchService {

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
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.contains(target)) {
                    results.add(new SearchResult(line, file, i + 1));
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao ler arquivo: " + file, exception);
        }
    }
}
