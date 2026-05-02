package thread.search.storage;

import thread.search.core.SearchFile;
import thread.search.core.SearchStorage;

import java.nio.file.Path;

public final class DirectFileSearchStorage implements SearchStorage {
    @Override
    public SearchFile open(Path path) {
        return new DirectFileSearchFile(path);
    }
}
