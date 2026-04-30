package model;

public record MatcherResult(boolean success, int numOfReadedLines, String file, int line) {
}
