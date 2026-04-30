package thread.ui.view;

import thread.ui.model.SearchHistoryEntry;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

public final class SearchHistoryFrame extends JFrame {
    private final DefaultTableModel tableModel;

    public SearchHistoryFrame(List<SearchHistoryEntry> entries) {
        super("Historico de resultados");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new Dimension(1100, 480));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(246, 246, 246));

        JLabel title = new JLabel("Historico de execucoes", SwingConstants.LEFT);
        title.setOpaque(true);
        title.setBackground(new Color(225, 225, 225));
        title.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        tableModel = new DefaultTableModel(
                new Object[]{"Nome", "Dataset", "Resultado", "Arquivo", "Linha", "Tempo", "Estrategia", "Storage", "Threads", "is_special_mode", "Status"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);

        root.add(title, BorderLayout.NORTH);
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        setContentPane(root);
        refresh(entries);
    }

    public void refresh(List<SearchHistoryEntry> entries) {
        tableModel.setRowCount(0);
        for (SearchHistoryEntry entry : entries) {
            tableModel.addRow(new Object[]{
                    value(entry.targetName()),
                    value(entry.datasetsText()),
                    value(entry.matchedName()),
                    value(entry.fileName()),
                    entry.lineNumber() <= 0 ? "-" : Integer.toString(entry.lineNumber()),
                    value(entry.elapsedText()),
                    entry.searchStrategy().label(),
                    entry.searchStorage().label(),
                    Integer.toString(entry.threads()),
                    Boolean.toString(entry.specialMode()),
                    value(entry.status())
            });
        }
    }

    private String value(String text) {
        return text == null || text.isBlank() ? "-" : text;
    }
}
