package thread.ui.model;

public record SearchResultViewModel(
        boolean found,
        String targetName,
        String datasetsText,
        String matchedName,
        String fileName,
        int lineNumber,
        String elapsedText,
        String message,
        SearchStrategyOption searchStrategy,
        SearchStorageOption searchStorage,
        int threads,
        boolean specialMode
) {
}
