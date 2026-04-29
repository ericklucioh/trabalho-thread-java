package thread.ui.model;

public record SearchHistoryEntry(
        String targetName,
        String datasetsText,
        String matchedName,
        String fileName,
        int lineNumber,
        String elapsedText,
        ExecutionModeOption executionMode,
        SearchStrategyOption searchStrategy,
        String status
) {
}
