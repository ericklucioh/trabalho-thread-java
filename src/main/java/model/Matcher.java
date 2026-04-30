package model;

public interface Matcher {
    MatcherResult regexMatch(String target, String file);

    MatcherResult lineByLineMatch(String target, String file);

    MatcherResult charByCharMatch(String target, String file);
}
