package thread.search.storage;

import thread.search.core.LineConsumer;
import thread.search.core.SearchFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class InMemoryTextSearchFile implements SearchFile {
    private final Path path;
    private final String content;

    public InMemoryTextSearchFile(Path path) throws IOException {
        this.path = path;
        this.content = Files.readString(path, StandardCharsets.UTF_8);
    }

    @Override
    public Path path() {
        return path;
    }

    @Override
    public void forEachLine(LineConsumer consumer) throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            String line;
            long lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                consumer.accept(line, lineNumber);
            }
        }
    }
}
