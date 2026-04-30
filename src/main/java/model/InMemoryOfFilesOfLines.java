package model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

public class InMemoryOfFilesOfLines implements Matcher {

    @Override
    public MatcherResult regexMatch(String target, String file) {
        Pattern pattern = Pattern.compile(target);
        return scan(file, line -> pattern.matcher(line).find());
    }

    @Override
    public MatcherResult lineByLineMatch(String target, String file) {
        return scan(file, line -> line.contains(target));
    }

    @Override
    public MatcherResult charByCharMatch(String target, String file) {
        return scan(file, line -> containsByCharacters(line, target));
    }

    private MatcherResult scan(String file, LineMatcher matcher) {
        Path path = Path.of(file);

        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (matcher.matches(line)) {
                    int lineNumber = i + 1;
                    return new MatcherResult(true, lineNumber, file, lineNumber);
                }
            }
            return new MatcherResult(false, lines.size(), file, -1);
        } catch (IOException exception) {
            throw new IllegalStateException("Falha ao ler arquivo: " + file, exception);
        }
    }

    private boolean containsByCharacters(String line, String target) {
        if (line == null || target == null) {
            return false;
        }

        if (target.isEmpty()) {
            return true;
        }

        if (target.length() > line.length()) {
            return false;
        }

        for (int i = 0; i <= line.length() - target.length(); i++) {
            int j = 0;
            while (j < target.length() && line.charAt(i + j) == target.charAt(j)) {
                j++;
            }
            if (j == target.length()) {
                return true;
            }
        }

        return false;
    }

    @FunctionalInterface
    private interface LineMatcher {
        boolean matches(String line);
    }
}
