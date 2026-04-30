package thread.ui.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import thread.ui.model.DatasetSelectionOption;
import thread.ui.model.SearchRequest;
import thread.ui.model.SearchResultViewModel;
import thread.ui.model.SearchStorageOption;
import thread.ui.model.SearchStrategyOption;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RealSearchScreenControllerTest {
    @TempDir
    Path tempDir;

    @Test
    void searchFindsNameAcrossSelectedDatasets() throws IOException {
        Path datasetG = tempDir.resolve("dataset_g");
        Path datasetP = tempDir.resolve("dataset_p");
        Files.createDirectories(datasetG);
        Files.createDirectories(datasetP);
        Files.writeString(datasetG.resolve("a.txt"), "Ana\nMaria\nJoao\n", StandardCharsets.UTF_8);
        Files.writeString(datasetP.resolve("b.txt"), "Pedro\nMaria\n", StandardCharsets.UTF_8);

        RealSearchScreenController controller = new RealSearchScreenController(List.of(datasetG, datasetP));
        SearchResultViewModel result = controller.search(new SearchRequest(
                "Maria",
                List.of(DatasetSelectionOption.DATASET_G, DatasetSelectionOption.DATASET_P),
                SearchStrategyOption.LINE_BY_LINE,
                SearchStorageOption.IN_FILE,
                1,
                false
        ));

        assertTrue(result.found());
        assertEquals("Maria", result.targetName());
        assertEquals("dataset_g, dataset_p", result.datasetsText());
        assertEquals("Maria", result.matchedName());
        assertTrue(result.fileName().endsWith("a.txt"));
        assertEquals(2, result.lineNumber());
        assertEquals("Encontrado", controller.history().get(0).status());
    }

    @Test
    void regexStrategyMatchesPattern() throws IOException {
        Path datasetG = tempDir.resolve("dataset_g");
        Path datasetP = tempDir.resolve("dataset_p");
        Files.createDirectories(datasetG);
        Files.createDirectories(datasetP);
        Files.writeString(datasetG.resolve("a.txt"), "Ana\nMaria\n", StandardCharsets.UTF_8);
        Files.writeString(datasetP.resolve("b.txt"), "Pedro\n", StandardCharsets.UTF_8);

        RealSearchScreenController controller = new RealSearchScreenController(List.of(datasetG, datasetP));
        SearchResultViewModel result = controller.search(new SearchRequest(
                "Mar.*",
                List.of(DatasetSelectionOption.DATASET_G),
                SearchStrategyOption.REGEX,
                SearchStorageOption.IN_MEMORY_LIST_OF_FILES_OF_LINE,
                4,
                true
        ));

        assertTrue(result.found());
        assertEquals("Maria", result.matchedName());
        assertEquals("dataset_g", result.datasetsText());
        assertEquals(SearchStrategyOption.REGEX, result.searchStrategy());
        assertFalse(result.message().isBlank());
        assertEquals("Encontrado", controller.history().get(0).status());
    }
}
