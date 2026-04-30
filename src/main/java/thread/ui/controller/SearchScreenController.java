package thread.ui.controller;

import thread.ui.model.DatasetSelectionOption;
import thread.ui.model.SearchHistoryEntry;
import thread.ui.model.SearchRequest;
import thread.ui.model.SearchResultViewModel;
import thread.ui.model.SearchStorageOption;
import thread.ui.model.SearchStrategyOption;

import java.util.List;

public interface SearchScreenController {
    List<String> sampleNames();

    List<DatasetSelectionOption> datasetSelections();

    List<SearchStrategyOption> searchStrategies();

    List<SearchStorageOption> searchStorageOptions();

    SearchResultViewModel search(SearchRequest request);

    List<SearchHistoryEntry> history();
}
