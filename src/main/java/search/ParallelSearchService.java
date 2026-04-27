package thread.search;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class ParallelSearchService implements SearchService {

    @Override
    public List<SearchResult> search(List<Path> datasetDirectories, String target) {
        List<Path> files = new ArrayList<>();

        for (Path directory : datasetDirectories) {
            collectFiles(directory, files);
        }

        ExecutorService executor = Executors.newFixedThreadPool(
                Math.max(1, Runtime.getRuntime().availableProcessors())
        );

        try {
            List<Callable<List<SearchResult>>> tasks = files.stream()
                    .map(file -> (Callable<List<SearchResult>>) () -> scanFile(file, target))
                    .toList();

            List<Future<List<SearchResult>>> futures = executor.invokeAll(tasks);
            List<SearchResult> results = new ArrayList<>();

            for (Future<List<SearchResult>> future : futures) {
                results.addAll(future.get());
            }

            return results;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Busca paralela interrompida", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Falha na busca paralela", exception.getCause());
        } finally {
            executor.shutdownNow();
        }
    }

    private void collectFiles(Path directory, List<Path> files) {
        if (!Files.exists(directory)) {
            throw new IllegalArgumentException("Diretorio nao encontrado: " + directory);
        }

        try {
            try (var stream = Files.walk(directory)) {
                stream.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".txt"))
                        .sorted(Comparator.naturalOrder())
                        .forEach(files::add);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao ler diretorio: " + directory, exception);
        }
    }

    private List<SearchResult> scanFile(Path file, String target) throws IOException {
        List<SearchResult> results = new ArrayList<>();
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains(target)) {
                results.add(new SearchResult(line, file, i + 1));
            }
        }

        return results;
    }
}
