package thread.search.storage;

import thread.search.core.LineConsumer;
import thread.search.core.SearchFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class InMemoryLinesSearchFile implements SearchFile {
    private final Path path;
    private final List<String> lines;

    public InMemoryLinesSearchFile(Path path) throws IOException {
        this.path = path;
        this.lines = Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    @Override
    public Path path() {
        return path;
    }

    @Override
    public void forEachLine(LineConsumer consumer) throws IOException {
        for (int i = 0; i < lines.size(); i++) {
            consumer.accept(lines.get(i), i + 1L);
        }
    }
}
