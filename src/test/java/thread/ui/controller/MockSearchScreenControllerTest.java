package thread.ui.controller;

import org.junit.jupiter.api.Test;
import thread.ui.model.DatasetSelectionOption;
import thread.ui.model.SearchRequest;
import thread.ui.model.SearchStorageOption;
import thread.ui.model.SearchStrategyOption;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MockSearchScreenControllerTest {

    @Test
    void sampleNamesHasTwentyItems() {
        MockSearchScreenController controller = new MockSearchScreenController();

        assertEquals(20, controller.sampleNames().size());
    }

    @Test
    void datasetSelectionsHasTwoItems() {
        MockSearchScreenController controller = new MockSearchScreenController();

        assertEquals(2, controller.datasetSelections().size());
    }

    @Test
    void storageSelectionsHasThreeItems() {
        MockSearchScreenController controller = new MockSearchScreenController();

        assertEquals(3, controller.searchStorageOptions().size());
    }

    @Test
    void searchGeneratesFoundResultForNonBlankText() {
        MockSearchScreenController controller = new MockSearchScreenController();

        var result = controller.search(new SearchRequest(
                "Maria",
                List.of(DatasetSelectionOption.DATASET_G),
                SearchStrategyOption.REGEX,
                SearchStorageOption.IN_FILE,
                1,
                false
        ));

        assertTrue(result.found());
        assertEquals("Maria", result.targetName());
        assertEquals("dataset_g", result.datasetsText());
        assertEquals(SearchStrategyOption.REGEX, result.searchStrategy());
        assertEquals(SearchStorageOption.IN_FILE, result.searchStorage());
        assertEquals(1, result.threads());
        assertFalse(result.specialMode());
        assertFalse(result.fileName().isBlank());
        assertTrue(controller.history().get(0).status().equals("Encontrado"));
    }

    @Test
    void blankTargetGeneratesNotFoundResult() {
        MockSearchScreenController controller = new MockSearchScreenController();

        var result = controller.search(new SearchRequest(
                "   ",
                List.of(DatasetSelectionOption.DATASET_G, DatasetSelectionOption.DATASET_P),
                SearchStrategyOption.LINE_BY_LINE,
                SearchStorageOption.IN_MEMORY_LIST_OF_FILES,
                12,
                true
        ));

        assertFalse(result.found());
        assertEquals("Informe um nome para simular a busca.", result.message());
        assertEquals("dataset_g, dataset_p", result.datasetsText());
        assertEquals("Nao encontrado", controller.history().get(0).status());
    }
}
