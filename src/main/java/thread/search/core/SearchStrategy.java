package thread.search.core;

import thread.search.SearchResult;

import java.io.IOException;
import java.util.List;

public interface SearchStrategy {
    List<SearchResult> search(SearchFile file, String target) throws IOException;
}
