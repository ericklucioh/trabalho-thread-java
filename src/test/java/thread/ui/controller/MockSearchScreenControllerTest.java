package thread.ui.controller;

import org.junit.jupiter.api.Test;
import thread.ui.model.DatasetSelectionOption;
import thread.ui.model.ExecutionModeOption;
import thread.ui.model.SearchRequest;
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
    void searchGeneratesFoundResultForNonBlankText() {
        MockSearchScreenController controller = new MockSearchScreenController();

        var result = controller.search(new SearchRequest(
                "Maria",
                List.of(DatasetSelectionOption.G),
                ExecutionModeOption.PARALLEL,
                SearchStrategyOption.REGEX
        ));

        assertTrue(result.found());
        assertEquals("Maria", result.targetName());
        assertEquals("Dataset G", result.datasetsText());
        assertEquals(ExecutionModeOption.PARALLEL, result.executionMode());
        assertEquals(SearchStrategyOption.REGEX, result.searchStrategy());
        assertFalse(result.fileName().isBlank());
        assertTrue(controller.history().get(0).status().equals("Encontrado"));
    }

    @Test
    void blankTargetGeneratesNotFoundResult() {
        MockSearchScreenController controller = new MockSearchScreenController();

        var result = controller.search(new SearchRequest(
                "   ",
                List.of(DatasetSelectionOption.G, DatasetSelectionOption.P),
                ExecutionModeOption.SEQUENTIAL,
                SearchStrategyOption.LINE_BY_LINE
        ));

        assertFalse(result.found());
        assertEquals("Informe um nome para simular a busca.", result.message());
        assertEquals("Nao encontrado", controller.history().get(0).status());
    }
}
