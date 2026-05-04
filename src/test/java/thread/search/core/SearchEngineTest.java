package thread.search.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import thread.search.SearchResult;
import thread.search.storage.DirectFileSearchStorage;
import thread.search.storage.InMemoryLinesSearchStorage;
import thread.search.storage.InMemoryTextSearchStorage;
import thread.search.strategy.CharByCharSearchStrategy;
import thread.search.strategy.LineByLineSearchStrategy;
import thread.search.strategy.RegexSearchStrategy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchEngineTest {
    @TempDir
    Path tempDir;

    @Test
    void directFileLineByLineSearchFindsTarget() throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.writeString(file, "Ana\nMaria\nJoao\n", StandardCharsets.UTF_8);

        SearchEngine engine = new SearchEngine(new DirectFileSearchStorage(), new LineByLineSearchStrategy());
        List<SearchResult> results = engine.search(file, "Maria");

        assertFalse(results.isEmpty());
        assertEquals("Maria", results.get(0).match());
        assertEquals(2L, results.get(0).lineNumber());
    }

    @Test
    void inMemoryLinesRegexSearchFindsTarget() throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.writeString(file, "Ana\nMaria\nJoao\n", StandardCharsets.UTF_8);

        SearchEngine engine = new SearchEngine(new InMemoryLinesSearchStorage(), new RegexSearchStrategy());
        List<SearchResult> results = engine.search(file, "Mar.*");

        assertFalse(results.isEmpty());
        assertEquals("Maria", results.get(0).match());
        assertTrue(results.get(0).file().endsWith("names.txt"));
    }

    @Test
    void inMemoryTextCharByCharSearchFindsTarget() throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.writeString(file, "Ana\nMaria\nJoao\n", StandardCharsets.UTF_8);

        SearchEngine engine = new SearchEngine(new InMemoryTextSearchStorage(), new CharByCharSearchStrategy());
        List<SearchResult> results = engine.search(file, "Maria");

        assertFalse(results.isEmpty());
        assertEquals(2L, results.get(0).lineNumber());
    }

    @Test
    void charByCharSearchStopsOnFirstMismatchInLine() throws IOException {
        Path file = tempDir.resolve("names.txt");
        Files.writeString(file, "XMaria\nMaria\n", StandardCharsets.UTF_8);

        SearchEngine engine = new SearchEngine(new InMemoryTextSearchStorage(), new CharByCharSearchStrategy());
        List<SearchResult> results = engine.search(file, "Maria");

        assertEquals(1, results.size());
        assertEquals(2L, results.get(0).lineNumber());
        assertEquals("Maria", results.get(0).match());
    }
}
