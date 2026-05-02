package thread.search.core;

import java.io.IOException;
import java.nio.file.Path;

public interface SearchFile {
    Path path();

    void forEachLine(LineConsumer consumer) throws IOException;
}
