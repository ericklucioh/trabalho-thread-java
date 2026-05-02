package thread.search.core;

import java.io.IOException;

@FunctionalInterface
public interface LineConsumer {
    void accept(String line, long lineNumber) throws IOException;
}
