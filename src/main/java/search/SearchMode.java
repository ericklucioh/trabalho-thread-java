package thread.search;

public enum SearchMode {
    SYNC,
    PARALLEL;

    public static SearchMode from(String rawMode) {
        return switch (rawMode.toLowerCase()) {
            case "sync", "sequencial", "sequential" -> SYNC;
            case "parallel", "paralelo" -> PARALLEL;
            default -> throw new IllegalArgumentException("Modo invalido: " + rawMode);
        };
    }
}
