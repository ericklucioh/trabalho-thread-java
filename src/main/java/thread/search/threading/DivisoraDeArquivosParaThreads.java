package thread.search.threading;

import thread.search.core.SearchChunk;
import thread.search.core.SearchFile;
import thread.search.core.SearchStorage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DivisoraDeArquivosParaThreads {
    private DivisoraDeArquivosParaThreads() {
    }

    public static List<SearchChunk> dividir(List<Path> arquivos, int threads, SearchStorage storage) {
        Objects.requireNonNull(arquivos, "arquivos");
        Objects.requireNonNull(storage, "storage");

        if (arquivos.isEmpty()) {
            return List.of();
        }

        List<FileProfile> profiles = new ArrayList<>();
        long totalLines = 0;

        for (Path arquivo : arquivos) {
            SearchFile file = open(storage, arquivo);
            long lineCount = countLines(file);
            profiles.add(new FileProfile(arquivo, file));
            totalLines += lineCount;
        }

        if (totalLines == 0) {
            return List.of();
        }

        int effectiveThreads = Math.max(1, Math.min(threads, (int) Math.min(Integer.MAX_VALUE, totalLines)));
        long chunkSize = Math.max(1L, (long) Math.ceil((double) totalLines / effectiveThreads));

        List<SearchChunk> chunks = new ArrayList<>();
        for (FileProfile profile : profiles) {
            chunks.addAll(chunkFile(profile.file(), profile.path(), chunkSize));
        }

        return List.copyOf(chunks);
    }

    private static SearchFile open(SearchStorage storage, Path arquivo) {
        try {
            return storage.open(arquivo);
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao abrir arquivo: " + arquivo, exception);
        }
    }

    private static long countLines(SearchFile file) {
        long[] count = {0};
        try {
            file.forEachLine((line, lineNumber) -> count[0]++);
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao contar linhas de: " + file.path(), exception);
        }
        return count[0];
    }

    private static List<SearchChunk> chunkFile(SearchFile file, Path path, long chunkSize) {
        List<SearchChunk> chunks = new ArrayList<>();
        List<String> buffer = new ArrayList<>();
        long[] startLine = {1};

        try {
            file.forEachLine((line, lineNumber) -> {
                if (buffer.isEmpty()) {
                    startLine[0] = lineNumber;
                }
                buffer.add(line);
                if (buffer.size() >= chunkSize) {
                    chunks.add(new SearchChunk(path, startLine[0], buffer));
                    buffer.clear();
                }
            });
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao dividir arquivo: " + path, exception);
        }

        if (!buffer.isEmpty()) {
            chunks.add(new SearchChunk(path, startLine[0], buffer));
        }

        return chunks;
    }

    private record FileProfile(Path path, SearchFile file) {
    }
}
