package thread.search.core;

import thread.search.SearchResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class SearchEngine {
    private final SearchStorage storage;
    private final SearchStrategy strategy;

    public SearchEngine(SearchStorage storage, SearchStrategy strategy) {
        this.storage = Objects.requireNonNull(storage, "storage");
        this.strategy = Objects.requireNonNull(strategy, "strategy");
    }

    public List<SearchResult> search(Path path, String target) {
        try {
            SearchFile file = storage.open(path);
            return search(file, target);
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao preparar arquivo: " + path, exception);
        }
    }

    public List<SearchResult> search(SearchFile file, String target) {
        try {
            return strategy.search(file, target);
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao processar arquivo: " + file.path(), exception);
        }
    }
}
