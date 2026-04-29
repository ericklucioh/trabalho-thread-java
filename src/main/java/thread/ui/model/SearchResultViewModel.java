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
        ExecutionModeOption executionMode,
        SearchStrategyOption searchStrategy
) {
}
