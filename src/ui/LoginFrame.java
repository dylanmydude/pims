package ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import dao.UserDAO;
import model.Role;
import model.User;

public class LoginFrame extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final UserDAO userDAO;

    public LoginFrame() {
        this.userDAO = new UserDAO();
        this.usernameField = new JTextField(18);
        this.passwordField = new JPasswordField(18);
        this.loginButton = new JButton("Login");

        setTitle("PIMS - Login");
        setSize(420, 240);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 8, 8, 8);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 0;
        formPanel.add(new JLabel("Username:"), constraints);

        constraints.gridx = 1;
        formPanel.add(usernameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        formPanel.add(new JLabel("Password:"), constraints);

        constraints.gridx = 1;
        formPanel.add(passwordField, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.EAST;
        formPanel.add(loginButton, constraints);

        loginButton.addActionListener(event -> attemptLogin());
        getRootPane().setDefaultButton(loginButton);

        add(formPanel, BorderLayout.CENTER);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both username and password.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            User user = userDAO.authenticate(username, password);

            if (user == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            redirectByRole(user);
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void redirectByRole(User user) {
        JFrame dashboard;

        if (user.getRole() == Role.ADMIN) {
            dashboard = new AdminDashboard(user);
        } else if (user.getRole() == Role.CASHIER) {
            dashboard = new CashierDashboard(user);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Unsupported user role.",
                    "Access Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        dashboard.setVisible(true);
        dispose();
    }
}
