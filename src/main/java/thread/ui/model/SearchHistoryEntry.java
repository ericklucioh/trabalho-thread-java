package thread.ui.model;

public record SearchHistoryEntry(
        String targetName,
        String datasetsText,
        String matchedName,
        String fileName,
        int lineNumber,
        String elapsedText,
        SearchStrategyOption searchStrategy,
        SearchStorageOption searchStorage,
        int threads,
        boolean specialMode,
        String status
) {
}
