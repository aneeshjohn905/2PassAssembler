import javax.swing.*;

public class Main {
    // Main method to start the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TwoPassAssemblerGUI gui = new TwoPassAssemblerGUI();
            gui.setVisible(true);
        });
    }
}
