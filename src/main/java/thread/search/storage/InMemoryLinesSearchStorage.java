package thread.search.storage;

import thread.search.core.SearchFile;
import thread.search.core.SearchStorage;

import java.io.IOException;
import java.nio.file.Path;

public final class InMemoryLinesSearchStorage implements SearchStorage {
    @Override
    public SearchFile open(Path path) throws IOException {
        return new InMemoryLinesSearchFile(path);
    }
}
