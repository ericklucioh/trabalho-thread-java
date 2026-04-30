package thread.ui.model;

import java.util.List;

public record SearchRequest(
        String targetName,
        List<DatasetSelectionOption> datasets,
        SearchStrategyOption searchStrategy,
        SearchStorageOption searchStorage,
        int threads,
        boolean specialMode
) {
}
