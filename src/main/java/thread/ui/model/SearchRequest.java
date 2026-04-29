package thread.ui.model;

import java.util.List;

public record SearchRequest(
        String targetName,
        List<DatasetSelectionOption> datasets,
        ExecutionModeOption executionMode,
        SearchStrategyOption searchStrategy
) {
}
