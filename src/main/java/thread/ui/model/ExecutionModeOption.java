package thread.ui.model;

public enum ExecutionModeOption {
    SEQUENTIAL("Sequencial", "Processa a busca em uma unica passagem."),
    PARALLEL("Paralela", "Processa a busca com trabalho dividido em threads.");

    private final String label;
    private final String description;

    ExecutionModeOption(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String label() {
        return label;
    }

    public String description() {
        return description;
    }

    @Override
    public String toString() {
        return label;
    }
}
