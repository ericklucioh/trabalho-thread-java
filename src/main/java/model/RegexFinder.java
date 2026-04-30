package model;

import java.util.regex.Pattern;

public final class RegexFinder implements Finder {
    @Override
    public boolean matches(String text, String target) {
        if (text == null || target == null) {
            return false;
        }

        return Pattern.compile(target).matcher(text).find();
    }
}
