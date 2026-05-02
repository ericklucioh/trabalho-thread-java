package thread.search.strategy;

import thread.search.SearchResult;
import thread.search.core.SearchFile;
import thread.search.core.SearchStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class RegexSearchStrategy implements SearchStrategy {
    @Override
    public List<SearchResult> search(SearchFile file, String target) throws IOException {
        if (target == null) {
            return List.of();
        }

        Pattern pattern = Pattern.compile(target);
        List<SearchResult> results = new ArrayList<>();

        file.forEachLine((line, lineNumber) -> {
            if (line != null && pattern.matcher(line).find()) {
                results.add(new SearchResult(line, file.path(), lineNumber));
            }
        });

        return results;
    }
}
