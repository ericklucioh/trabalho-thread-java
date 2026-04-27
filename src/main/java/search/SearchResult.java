package thread.search;

import java.nio.file.Path;

public record SearchResult(String match, Path file, long lineNumber) {
}
