package thread.search.strategy;

import thread.search.SearchResult;
import thread.search.core.SearchFile;
import thread.search.core.SearchStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class LineByLineSearchStrategy implements SearchStrategy {
    @Override
    public List<SearchResult> search(SearchFile file, String target) throws IOException {
        List<SearchResult> results = new ArrayList<>();

        file.forEachLine((line, lineNumber) -> {
            if (line != null && target != null && line.contains(target)) {
                results.add(new SearchResult(line, file.path(), lineNumber));
            }
        });

        return results;
    }
}
