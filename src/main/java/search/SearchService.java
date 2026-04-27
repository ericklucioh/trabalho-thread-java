package thread.search;

import java.nio.file.Path;
import java.util.List;

public interface SearchService {
    List<SearchResult> search(List<Path> datasetDirectories, String target);
}
