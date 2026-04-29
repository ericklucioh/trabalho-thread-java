package thread.ui.model;

public enum SearchStrategyOption {
    LINE_BY_LINE("Leitura direta", "Compara cada linha sem estruturas intermediarias."),
    IN_MEMORY("Busca em memoria", "Carrega os dados antes de comparar."),
    REGEX("Regex", "Aplica expressao regular sobre o texto."),
    CHAR_BY_CHAR("Caractere a caractere", "Compara a sequencia de forma manual."),
    TWO_PHASE("Duas fases", "Filtra candidatos e depois valida o texto completo.");

    private final String label;
    private final String description;

    SearchStrategyOption(String label, String description) {
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
