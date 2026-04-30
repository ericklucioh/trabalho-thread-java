package thread.ui.view;

import thread.ui.controller.SearchScreenController;
import thread.ui.model.DatasetSelectionOption;
import thread.ui.model.SearchHistoryEntry;
import thread.ui.model.SearchRequest;
import thread.ui.model.SearchResultViewModel;
import thread.ui.model.SearchStorageOption;
import thread.ui.model.SearchStrategyOption;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SearchScreenFrame extends JFrame {
    private final SearchScreenController controller;
    private final JTextField targetField;
    private final Map<JCheckBox, DatasetSelectionOption> datasetCheckboxes = new LinkedHashMap<>();
    private final JComboBox<SearchStrategyOption> strategySelect;
    private final JComboBox<SearchStorageOption> storageSelect;
    private final JSpinner threadCountSpinner;
    private final JCheckBox specialModeCheckBox;

    public SearchScreenFrame(SearchScreenController controller) {
        super("Tela de busca");
        this.controller = controller;
        this.targetField = new JTextField(24);
        this.strategySelect = new JComboBox<>(controller.searchStrategies().toArray(SearchStrategyOption[]::new));
        this.storageSelect = new JComboBox<>(controller.searchStorageOptions().toArray(SearchStorageOption[]::new));
        this.threadCountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 500, 1));
        this.specialModeCheckBox = new JCheckBox("is_special_mode");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 780));
        setLocationRelativeTo(null);
        setContentPane(buildRoot());
        pack();
    }

    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(236, 236, 236));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBody(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 12));
        header.setBorder(createPanelBorder());
        header.setBackground(new Color(224, 224, 224));

        JLabel title = new JLabel("Busca de nomes");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchBar.setOpaque(false);
        searchBar.add(new JLabel("Nome"));
        searchBar.add(targetField);

        JButton searchButton = createPrimaryButton("Buscar");
        searchButton.addActionListener(event -> openSearchResult());

        JButton historyButton = createPrimaryButton("Mostrar resultados");
        historyButton.addActionListener(event -> openHistoryWindow());

        searchBar.add(searchButton);
        searchBar.add(historyButton);

        header.add(title, BorderLayout.WEST);
        header.add(searchBar, BorderLayout.EAST);
        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(12, 12));
        body.setOpaque(false);
        body.add(buildExamplesPanel(), BorderLayout.WEST);
        body.add(buildConfigPanel(), BorderLayout.CENTER);
        return body;
    }

    private Component buildExamplesPanel() {
        JPanel panel = createCardPanel("Nomes de exemplo");
        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setOpaque(false);

        JList<String> nameList = new JList<>(controller.sampleNames().toArray(String[]::new));
        nameList.setVisibleRowCount(20);
        nameList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                String selected = nameList.getSelectedValue();
                if (selected != null) {
                    targetField.setText(selected);
                }
            }
        });

        JLabel hint = new JLabel("Clique em um nome para preencher o campo");
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 12f));

        content.add(hint, BorderLayout.NORTH);
        content.add(new JScrollPane(nameList), BorderLayout.CENTER);
        panel.add(content, BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(250, 0));
        return panel;
    }

    private Component buildConfigPanel() {
        JPanel panel = createCardPanel("Configuracoes da busca");
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setOpaque(false);

        JPanel optionsGrid = new JPanel(new GridLayout(3, 2, 12, 12));
        optionsGrid.setOpaque(false);
        optionsGrid.add(buildDatasetPanel());
        optionsGrid.add(buildStrategyPanel());
        optionsGrid.add(buildStoragePanel());
        optionsGrid.add(buildThreadsPanel());
        optionsGrid.add(buildSpecialModePanel());

        content.add(optionsGrid, BorderLayout.CENTER);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private Component buildDatasetPanel() {
        JPanel panel = createCardPanel("Dataset");
        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);

        JPanel checkboxes = new JPanel(new GridLayout(0, 1, 6, 6));
        checkboxes.setOpaque(false);

        for (DatasetSelectionOption option : controller.datasetSelections()) {
            JCheckBox checkBox = new JCheckBox(option.label(), true);
            checkBox.setOpaque(false);
            datasetCheckboxes.put(checkBox, option);
            checkboxes.add(checkBox);
        }

        content.add(new JLabel("Escolha um ou os dois datasets"), BorderLayout.NORTH);
        content.add(checkboxes, BorderLayout.CENTER);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private Component buildStrategyPanel() {
        JPanel panel = createCardPanel("1. line by line, char by char, regex");
        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);

        content.add(new JLabel("Escolha o algoritmo de busca"), BorderLayout.NORTH);
        content.add(strategySelect, BorderLayout.CENTER);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private Component buildStoragePanel() {
        JPanel panel = createCardPanel("2. in_file / in_memory...");
        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);

        content.add(new JLabel("Escolha onde a busca vai operar"), BorderLayout.NORTH);
        content.add(storageSelect, BorderLayout.CENTER);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private Component buildThreadsPanel() {
        JPanel panel = createCardPanel("3. number of threads");
        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setOpaque(false);
        row.add(new JLabel("Number of threads"));
        row.add(threadCountSpinner);

        content.add(new JLabel("1 a 500, onde 1 = sem paralelismo"), BorderLayout.NORTH);
        content.add(row, BorderLayout.CENTER);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private Component buildSpecialModePanel() {
        JPanel panel = createCardPanel("4. bool is_special_mode");
        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);

        specialModeCheckBox.setOpaque(false);
        content.add(new JLabel("Ativa o modo especial"), BorderLayout.NORTH);
        content.add(specialModeCheckBox, BorderLayout.CENTER);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        footer.setOpaque(false);
        footer.add(new JLabel("Interface desacoplada do motor real de busca"));
        return footer;
    }

    private void openSearchResult() {
        SearchRequest request = new SearchRequest(
                targetField.getText(),
                selectedDatasets(),
                selectedSearchStrategy(),
                selectedStorage(),
                selectedThreads(),
                specialModeCheckBox.isSelected()
        );
        SearchResultViewModel result = controller.search(request);
        SearchResultDialog dialog = new SearchResultDialog(this, result);
        dialog.setVisible(true);
    }

    private void openHistoryWindow() {
        List<SearchHistoryEntry> entries = controller.history();
        SearchHistoryFrame frame = new SearchHistoryFrame(entries);
        frame.setVisible(true);
    }

    private SearchStrategyOption selectedSearchStrategy() {
        return (SearchStrategyOption) strategySelect.getSelectedItem();
    }

    private List<DatasetSelectionOption> selectedDatasets() {
        return datasetCheckboxes.entrySet().stream()
                .filter(entry -> entry.getKey().isSelected())
                .map(Map.Entry::getValue)
                .toList();
    }

    private SearchStorageOption selectedStorage() {
        return (SearchStorageOption) storageSelect.getSelectedItem();
    }

    private int selectedThreads() {
        Object value = threadCountSpinner.getValue();
        return value instanceof Number number ? number.intValue() : 1;
    }

    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBorder(createPanelBorder());
        panel.setBackground(new Color(250, 250, 250));
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel(title);
        label.setBorder(new EmptyBorder(0, 0, 8, 0));
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(label, BorderLayout.NORTH);
        return panel;
    }

    private javax.swing.border.Border createPanelBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 90, 90)),
                new EmptyBorder(12, 12, 12, 12)
        );
    }

    private JButton createPrimaryButton(String label) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        return button;
    }
}
