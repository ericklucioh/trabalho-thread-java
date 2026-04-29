package thread.ui.model;

public enum DatasetSelectionOption {
    G("Dataset G", "./dataset_g"),
    P("Dataset P", "./dataset_p");

    private final String label;
    private final String path;

    DatasetSelectionOption(String label, String path) {
        this.label = label;
        this.path = path;
    }

    public String label() {
        return label;
    }

    public String path() {
        return path;
    }

    @Override
    public String toString() {
        return label;
    }
}
