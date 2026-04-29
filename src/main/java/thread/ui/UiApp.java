package thread.ui;

import thread.ui.controller.MockSearchScreenController;
import thread.ui.view.SearchScreenFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;

public final class UiApp {
    private UiApp() {
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // Keep default look and feel if the cross-platform one is unavailable.
        }

        configureDefaultFont();

        SwingUtilities.invokeLater(() -> {
            SearchScreenFrame frame = new SearchScreenFrame(new MockSearchScreenController());
            frame.setVisible(true);
        });
    }

    private static void configureDefaultFont() {
        String fontFamily = pickFontFamily();
        FontUIResource uiFont = new FontUIResource(new Font(fontFamily, Font.PLAIN, 14));

        UIManager.put("Button.font", uiFont);
        UIManager.put("CheckBox.font", uiFont);
        UIManager.put("Label.font", uiFont);
        UIManager.put("TextField.font", uiFont);
        UIManager.put("TextArea.font", uiFont);
        UIManager.put("RadioButton.font", uiFont);
        UIManager.put("Table.font", uiFont);
        UIManager.put("TableHeader.font", uiFont.deriveFont(Font.BOLD, 14f));
        UIManager.put("List.font", uiFont);
        UIManager.put("Panel.font", uiFont);
        UIManager.put("TitledBorder.font", uiFont.deriveFont(Font.BOLD, 14f));
    }

    private static String pickFontFamily() {
        String[] availableFonts = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();

        return Arrays.stream(new String[]{
                        "DejaVu Sans",
                        "Liberation Sans",
                        "Segoe UI",
                        "Cantarell",
                        "SansSerif"
                })
                .filter(preferred -> Arrays.asList(availableFonts).contains(preferred))
                .findFirst()
                .orElse("SansSerif");
    }
}
