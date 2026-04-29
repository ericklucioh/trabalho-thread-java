package model;

public final class LineByLineFinder implements Finder {
    @Override
    public boolean matches(String text, String target) {
        return text != null && target != null && text.contains(target);
    }
}
