package thread.ui.view;

import thread.ui.controller.SearchScreenController;
import thread.ui.model.DatasetSelectionOption;
import thread.ui.model.ExecutionModeOption;
import thread.ui.model.SearchHistoryEntry;
import thread.ui.model.SearchRequest;
import thread.ui.model.SearchResultViewModel;
import thread.ui.model.SearchStrategyOption;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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
    private final Map<JRadioButton, ExecutionModeOption> executionButtons = new LinkedHashMap<>();
    private final Map<JRadioButton, SearchStrategyOption> strategyButtons = new LinkedHashMap<>();

    public SearchScreenFrame(SearchScreenController controller) {
        super("Tela de busca");
        this.controller = controller;
        this.targetField = new JTextField(24);

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

        JPanel optionsGrid = new JPanel(new GridLayout(1, 2, 12, 12));
        optionsGrid.setOpaque(false);
        optionsGrid.add(buildDatasetPanel());
        optionsGrid.add(buildExecutionModePanel());

        content.add(optionsGrid, BorderLayout.CENTER);
        content.add(buildStrategyPanel(), BorderLayout.SOUTH);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private Component buildDatasetPanel() {
        JPanel panel = createCardPanel("Datasets");
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

    private Component buildExecutionModePanel() {
        JPanel panel = createCardPanel("Forma de execucao");
        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);

        ButtonGroup group = new ButtonGroup();
        JPanel buttons = new JPanel(new GridLayout(0, 1, 6, 6));
        buttons.setOpaque(false);

        for (ExecutionModeOption option : controller.executionModes()) {
            JRadioButton button = new JRadioButton(option.label());
            button.setOpaque(false);
            button.setToolTipText(option.description());
            if (executionButtons.isEmpty()) {
                button.setSelected(true);
            }
            group.add(button);
            executionButtons.put(button, option);
            buttons.add(button);
        }

        content.add(new JLabel("Selecione uma forma de execucao"), BorderLayout.NORTH);
        content.add(buttons, BorderLayout.CENTER);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private Component buildStrategyPanel() {
        JPanel panel = createCardPanel("Estrategia de busca");
        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);

        ButtonGroup group = new ButtonGroup();
        JPanel buttons = new JPanel(new GridLayout(0, 1, 6, 6));
        buttons.setOpaque(false);

        for (SearchStrategyOption option : controller.searchStrategies()) {
            JRadioButton button = new JRadioButton(option.label());
            button.setOpaque(false);
            button.setToolTipText(option.description());
            if (strategyButtons.isEmpty()) {
                button.setSelected(true);
            }
            group.add(button);
            strategyButtons.put(button, option);
            buttons.add(button);
        }

        content.add(new JLabel("Selecione uma estrategia de busca"), BorderLayout.NORTH);
        content.add(buttons, BorderLayout.CENTER);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(110, 110, 110)),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(new Color(242, 242, 242));

        JLabel label = new JLabel("A busca abre uma janela de resultado e o historico abre uma tabela com dados mock.");
        panel.add(label, BorderLayout.CENTER);
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
                selectedExecutionMode(),
                selectedSearchStrategy()
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

    private ExecutionModeOption selectedExecutionMode() {
        return executionButtons.entrySet().stream()
                .filter(entry -> entry.getKey().isSelected())
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(ExecutionModeOption.SEQUENTIAL);
    }

    private SearchStrategyOption selectedSearchStrategy() {
        return strategyButtons.entrySet().stream()
                .filter(entry -> entry.getKey().isSelected())
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(SearchStrategyOption.LINE_BY_LINE);
    }

    private List<DatasetSelectionOption> selectedDatasets() {
        return datasetCheckboxes.entrySet().stream()
                .filter(entry -> entry.getKey().isSelected())
                .map(Map.Entry::getValue)
                .toList();
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
