package thread.search.strategy;

import thread.search.SearchResult;
import thread.search.core.SearchFile;
import thread.search.core.SearchStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CharByCharSearchStrategy implements SearchStrategy {
    @Override
    public List<SearchResult> search(SearchFile file, String target) throws IOException {
        List<SearchResult> results = new ArrayList<>();

        file.forEachLine((line, lineNumber) -> {
            if (matchesByCharacters(line, target)) {
                results.add(new SearchResult(line, file.path(), lineNumber));
            }
        });

        return results;
    }

    private boolean matchesByCharacters(String text, String target) {
        if (text == null || target == null) {
            return false;
        }

        if (target.isEmpty()) {
            return true;
        }

        if (target.length() > text.length()) {
            return false;
        }

        for (int i = 0; i <= text.length() - target.length(); i++) {
            int j = 0;
            while (j < target.length() && text.charAt(i + j) == target.charAt(j)) {
                j++;
            }

            if (j == target.length()) {
                return true;
            }
        }

        return false;
    }
}
