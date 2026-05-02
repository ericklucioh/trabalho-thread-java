package thread.search.core;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class SearchChunk implements SearchFile {
    private final Path path;
    private final long firstLineNumber;
    private final List<String> lines;

    public SearchChunk(Path path, long firstLineNumber, List<String> lines) {
        this.path = Objects.requireNonNull(path, "path");
        this.firstLineNumber = firstLineNumber;
        this.lines = List.copyOf(Objects.requireNonNull(lines, "lines"));
    }

    @Override
    public Path path() {
        return path;
    }

    public long firstLineNumber() {
        return firstLineNumber;
    }

    public long lastLineNumber() {
        return lines.isEmpty() ? firstLineNumber - 1 : firstLineNumber + lines.size() - 1;
    }

    @Override
    public void forEachLine(LineConsumer consumer) throws IOException {
        for (int i = 0; i < lines.size(); i++) {
            consumer.accept(lines.get(i), firstLineNumber + i);
        }
    }
}
