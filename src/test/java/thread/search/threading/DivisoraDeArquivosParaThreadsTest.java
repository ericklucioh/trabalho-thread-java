package thread.search.threading;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import thread.search.core.SearchChunk;
import thread.search.core.SearchFile;
import thread.search.core.SearchStorage;
import thread.search.storage.DirectFileSearchStorage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DivisoraDeArquivosParaThreadsTest {
    @TempDir
    Path tempDir;

    @Test
    void dividesSingleFileIntoContiguousChunksWithoutCrossingFileBoundary() throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.writeString(file, "A\nB\nC\nD\nE\n", StandardCharsets.UTF_8);

        List<SearchChunk> chunks = DivisoraDeArquivosParaThreads.dividir(
                List.of(file),
                2,
                new DirectFileSearchStorage()
        );

        assertEquals(2, chunks.size());
        assertEquals(1L, chunks.get(0).firstLineNumber());
        assertEquals(3L, chunks.get(0).lastLineNumber());
        assertEquals(4L, chunks.get(1).firstLineNumber());
        assertEquals(5L, chunks.get(1).lastLineNumber());
    }

    @Test
    void usesProvidedLineCountsWithoutCountingFileTwice() {
        Path file = tempDir.resolve("names.txt");
        AtomicInteger forEachLineCalls = new AtomicInteger();

        SearchStorage storage = path -> new SearchFile() {
            @Override
            public Path path() {
                return path;
            }

            @Override
            public void forEachLine(thread.search.core.LineConsumer consumer) throws IOException {
                forEachLineCalls.incrementAndGet();
                consumer.accept("A", 1);
                consumer.accept("B", 2);
                consumer.accept("C", 3);
                consumer.accept("D", 4);
            }
        };

        List<SearchChunk> chunks = DivisoraDeArquivosParaThreads.dividir(
                List.of(file),
                2,
                storage,
                Map.of(file, 4L)
        );

        assertEquals(1, forEachLineCalls.get());
        assertEquals(2, chunks.size());
    }
}
