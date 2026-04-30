package thread.ui.model;

public enum SearchStorageOption {
    IN_FILE("in_file"),
    IN_MEMORY_LIST_OF_FILES_OF_LINE("in_memory_list_of_files_of_line"),
    IN_MEMORY_LIST_OF_FILES("in_memory_list_of_files");

    private final String label;

    SearchStorageOption(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
