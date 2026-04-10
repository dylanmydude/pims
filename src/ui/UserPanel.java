package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import dao.UserDAO;
import model.User;

public class UserPanel extends JPanel {

    private final UserDAO userDAO;
    private final DefaultTableModel tableModel;
    private final JTable userTable;

    public UserPanel() {
        this.userDAO = new UserDAO();
        this.tableModel = createTableModel();
        this.userTable = new JTable(tableModel);

        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        userTable.setRowHeight(24);
        userTable.setFillsViewportHeight(true);

        add(new JScrollPane(userTable), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        refreshUsers();
    }

    public void refreshUsers() {
        loadUsers();
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        addButton.addActionListener(event -> showAddUserDialog());
        editButton.addActionListener(event -> showEditUserDialog());
        deleteButton.addActionListener(event -> deleteSelectedUser());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        return buttonPanel;
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(new String[] {"ID", "Username", "Full Name", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void loadUsers() {
        tableModel.setRowCount(0);

        try {
            List<User> users = userDAO.getAllUsers();

            for (User user : users) {
                tableModel.addRow(new Object[] {
                        user.getUser_id(),
                        user.getUsername(),
                        user.getFull_name(),
                        user.getRole()
                });
            }
        } catch (SQLException exception) {
            showError("Unable to load users: " + exception.getMessage());
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();

        if (selectedRow < 0) {
            showError("Please select a user to delete.");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = String.valueOf(tableModel.getValueAt(selectedRow, 1));

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete user \"" + username + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            userDAO.deleteUser(userId);
            refreshUsers();
        } catch (SQLException exception) {
            showError("Unable to delete user: " + exception.getMessage());
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(getOwnerFrame(), "Add User", true);
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField fullNameField = new JTextField(20);
        JComboBox<String> roleComboBox = new JComboBox<>(new String[] {"Admin", "Cashier"});
        roleComboBox.setSelectedIndex(-1);

        addField(formPanel, constraints, 0, "Username:", usernameField);
        addField(formPanel, constraints, 1, "Password:", passwordField);
        addField(formPanel, constraints, 2, "Full Name:", fullNameField);
        addField(formPanel, constraints, 3, "Role:", roleComboBox);

        JButton saveButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(event -> saveUser(
                dialog,
                usernameField,
                passwordField,
                fullNameField,
                roleComboBox
        ));
        cancelButton.addActionListener(event -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(contentPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();

        if (selectedRow < 0) {
            showError("Please select a user to edit.");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = String.valueOf(tableModel.getValueAt(selectedRow, 1));
        String fullName = String.valueOf(tableModel.getValueAt(selectedRow, 2));
        String role = String.valueOf(tableModel.getValueAt(selectedRow, 3));

        JDialog dialog = new JDialog(getOwnerFrame(), "Edit User", true);
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = new JTextField(username, 20);
        usernameField.setEditable(false);
        usernameField.setEnabled(false);

        JTextField fullNameField = new JTextField(fullName, 20);
        JComboBox<String> roleComboBox = new JComboBox<>(new String[] {"Admin", "Cashier"});
        roleComboBox.setSelectedItem(role);

        addField(formPanel, constraints, 0, "Username:", usernameField);
        addField(formPanel, constraints, 1, "Full Name:", fullNameField);
        addField(formPanel, constraints, 2, "Role:", roleComboBox);

        JButton saveButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(event -> updateUser(dialog, userId, fullNameField, roleComboBox));
        cancelButton.addActionListener(event -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(contentPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void saveUser(
            JDialog dialog,
            JTextField usernameField,
            JPasswordField passwordField,
            JTextField fullNameField,
            JComboBox<String> roleComboBox
    ) {
        try {
            User user = buildUserFromForm(usernameField, passwordField, fullNameField, roleComboBox);
            userDAO.addUser(user);
            refreshUsers();
            dialog.dispose();
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (SQLException exception) {
            showError("Unable to save user: " + exception.getMessage());
        }
    }

    private void updateUser(
            JDialog dialog,
            int userId,
            JTextField fullNameField,
            JComboBox<String> roleComboBox
    ) {
        try {
            String fullName = fullNameField.getText() == null ? "" : fullNameField.getText().trim();
            Object selectedRole = roleComboBox.getSelectedItem();

            if (selectedRole == null || String.valueOf(selectedRole).trim().isEmpty()) {
                throw new IllegalArgumentException("Role must be selected.");
            }

            User user = new User();
            user.setUser_id(userId);
            user.setFull_name(fullName);
            user.setRole(String.valueOf(selectedRole).trim());

            userDAO.updateUser(user);
            refreshUsers();
            dialog.dispose();
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (SQLException exception) {
            showError("Unable to update user: " + exception.getMessage());
        }
    }

    private User buildUserFromForm(
            JTextField usernameField,
            JPasswordField passwordField,
            JTextField fullNameField,
            JComboBox<String> roleComboBox
    ) {
        String username = requireValue(usernameField.getText(), "Username is required.");
        String password = requireValue(new String(passwordField.getPassword()), "Password is required.");
        String fullName = fullNameField.getText() == null ? "" : fullNameField.getText().trim();
        Object selectedRole = roleComboBox.getSelectedItem();

        if (selectedRole == null) {
            throw new IllegalArgumentException("Role must be selected.");
        }

        String role = String.valueOf(selectedRole).trim();
        if (role.isEmpty()) {
            throw new IllegalArgumentException("Role must be selected.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFull_name(fullName);
        user.setRole(role);
        return user;
    }

    private String requireValue(String value, String errorMessage) {
        String trimmedValue = value == null ? "" : value.trim();

        if (trimmedValue.isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }

        return trimmedValue;
    }

    private void addField(
            JPanel panel,
            GridBagConstraints constraints,
            int row,
            String labelText,
            java.awt.Component component
    ) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;
        panel.add(new JLabel(labelText), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        panel.add(component, constraints);
    }

    private Frame getOwnerFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
