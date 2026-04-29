package model;

public final class Executor {
    private final Finder finder;
    private final InMemory inMemory;

    public Executor(Finder finder, InMemory inMemory) {
        this.finder = finder == null ? new LineByLineFinder() : finder;
        this.inMemory = inMemory == null ? InMemory.NONE : inMemory;
    }

    public Finder finder() {
        return finder;
    }

    public InMemory inMemory() {
        return inMemory;
    }

    public boolean matches(String text, String target) {
        return finder.matches(text, target);
    }
}
