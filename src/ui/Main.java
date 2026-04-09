package ui;

import javax.swing.SwingUtilities;

import ui.LoginFrame;

public class Main {

    private Main() {
        // Prevent instantiation.
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
