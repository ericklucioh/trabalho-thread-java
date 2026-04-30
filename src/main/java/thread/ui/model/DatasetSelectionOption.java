package thread.ui.model;

public enum DatasetSelectionOption {
    DATASET_G("dataset_g", "./dataset_g"),
    DATASET_P("dataset_p", "./dataset_p");

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
