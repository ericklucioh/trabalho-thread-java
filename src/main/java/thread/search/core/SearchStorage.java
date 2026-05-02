package thread.search.core;

import java.io.IOException;
import java.nio.file.Path;

public interface SearchStorage {
    SearchFile open(Path path) throws IOException;
}
