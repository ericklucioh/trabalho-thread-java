package thread.ui.view;

import thread.ui.model.SearchResultViewModel;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Window;

public final class SearchResultDialog extends JDialog {
    public SearchResultDialog(JFrame owner, SearchResultViewModel result) {
        super(owner, "Resultado da busca", ModalityType.APPLICATION_MODAL);
        buildUi(result);
    }

    private void buildUi(SearchResultViewModel result) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(248, 248, 248));

        JLabel headline = new JLabel(
                result.found() ? "Resultado encontrado" : "Resultado nao encontrado",
                SwingConstants.CENTER
        );
        headline.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                new EmptyBorder(12, 12, 12, 12)
        ));
        headline.setOpaque(true);
        headline.setBackground(result.found() ? new Color(220, 240, 220) : new Color(245, 220, 220));
        headline.setForeground(new Color(30, 30, 30));

        JPanel grid = new JPanel(new GridLayout(0, 2, 8, 8));
        grid.setOpaque(false);
        grid.add(new JLabel("Mensagem:"));
        grid.add(new JLabel(result.message()));
        grid.add(new JLabel("Nome buscado:"));
        grid.add(new JLabel(defaultText(result.targetName())));
        grid.add(new JLabel("Datasets:"));
        grid.add(new JLabel(defaultText(result.datasetsText())));
        grid.add(new JLabel("Nome retornado:"));
        grid.add(new JLabel(defaultText(result.matchedName())));
        grid.add(new JLabel("Arquivo:"));
        grid.add(new JLabel(defaultText(result.fileName())));
        grid.add(new JLabel("Linha:"));
        grid.add(new JLabel(result.lineNumber() <= 0 ? "-" : Integer.toString(result.lineNumber())));
        grid.add(new JLabel("Tempo:"));
        grid.add(new JLabel(defaultText(result.elapsedText())));
        grid.add(new JLabel("Forma de execucao:"));
        grid.add(new JLabel(result.executionMode().label()));
        grid.add(new JLabel("Estrategia:"));
        grid.add(new JLabel(result.searchStrategy().label()));

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(headline, BorderLayout.NORTH);
        center.add(grid, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);
        setContentPane(root);
        pack();
        setLocationRelativeTo((Window) getOwner());
    }

    private String defaultText(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
