package model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class InFileMatcher implements Matcher {

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
        int readLines = 0;

        try (var lines = Files.lines(path, StandardCharsets.UTF_8)) {
            var iterator = lines.iterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                readLines++;
                if (matcher.matches(line)) {
                    return new MatcherResult(true, readLines, file, readLines);
                }
            }
            return new MatcherResult(false, readLines, file, -1);
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
