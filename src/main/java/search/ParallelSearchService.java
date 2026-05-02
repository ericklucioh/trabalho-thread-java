package thread.search;

import thread.search.core.SearchEngine;
import thread.search.core.SearchChunk;
import thread.search.storage.DirectFileSearchStorage;
import thread.search.threading.DivisoraDeArquivosParaThreads;
import thread.search.strategy.LineByLineSearchStrategy;

import java.io.IOException;
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
    private final SearchEngine engine = new SearchEngine(
            new DirectFileSearchStorage(),
            new LineByLineSearchStrategy()
    );

    @Override
    public List<SearchResult> search(List<Path> datasetDirectories, String target) {
        List<Path> files = new ArrayList<>();

        for (Path directory : datasetDirectories) {
            collectFiles(directory, files);
        }

        List<SearchChunk> chunks = DivisoraDeArquivosParaThreads.dividir(
                files,
                Runtime.getRuntime().availableProcessors(),
                new DirectFileSearchStorage()
        );

        if (chunks.isEmpty()) {
            return List.of();
        }

        int workerCount = Math.max(1, Math.min(Runtime.getRuntime().availableProcessors(), chunks.size()));
        ExecutorService executor = Executors.newFixedThreadPool(workerCount);

        try {
            List<Callable<List<SearchResult>>> tasks = chunks.stream()
                    .map(chunk -> (Callable<List<SearchResult>>) () -> scanChunk(chunk, target))
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

    private List<SearchResult> scanChunk(SearchChunk chunk, String target) {
        return engine.search(chunk, target);
    }
}
