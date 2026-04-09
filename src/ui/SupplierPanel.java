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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import dao.SupplierDAO;
import model.Supplier;

public class SupplierPanel extends JPanel {

    private final SupplierDAO supplierDAO;
    private final DefaultTableModel tableModel;
    private final JTable supplierTable;

    public SupplierPanel() {
        this.supplierDAO = new SupplierDAO();
        this.tableModel = createTableModel();
        this.supplierTable = new JTable(tableModel);

        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        supplierTable.setRowHeight(24);
        supplierTable.setFillsViewportHeight(true);

        add(new JScrollPane(supplierTable), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        refreshSuppliers();
    }

    public void refreshSuppliers() {
        loadSuppliers();
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        addButton.addActionListener(event -> showSupplierDialog(null));
        editButton.addActionListener(event -> editSelectedSupplier());
        deleteButton.addActionListener(event -> deleteSelectedSupplier());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        return buttonPanel;
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(
                new String[] {"ID", "Name", "Contact Person", "Phone", "Email", "Address"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void loadSuppliers() {
        tableModel.setRowCount(0);

        try {
            List<Supplier> suppliers = supplierDAO.getAllSuppliers();

            for (Supplier supplier : suppliers) {
                tableModel.addRow(new Object[] {
                        supplier.getSupplier_id(),
                        supplier.getName(),
                        supplier.getContact_person(),
                        supplier.getPhone(),
                        supplier.getEmail(),
                        supplier.getAddress()
                });
            }
        } catch (SQLException exception) {
            showError("Unable to load suppliers: " + exception.getMessage());
        }
    }

    private void editSelectedSupplier() {
        int selectedRow = supplierTable.getSelectedRow();

        if (selectedRow < 0) {
            showError("Please select a supplier to edit.");
            return;
        }

        Supplier supplier = getSupplierFromRow(selectedRow);
        showSupplierDialog(supplier);
    }

    private void deleteSelectedSupplier() {
        int selectedRow = supplierTable.getSelectedRow();

        if (selectedRow < 0) {
            showError("Please select a supplier to delete.");
            return;
        }

        int supplierId = (int) tableModel.getValueAt(selectedRow, 0);
        String supplierName = String.valueOf(tableModel.getValueAt(selectedRow, 1));

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete supplier \"" + supplierName + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            supplierDAO.deleteSupplier(supplierId);
            refreshSuppliers();
        } catch (SQLException exception) {
            showError("Unable to delete supplier: " + exception.getMessage());
        }
    }

    private Supplier getSupplierFromRow(int row) {
        Supplier supplier = new Supplier();
        supplier.setSupplier_id((int) tableModel.getValueAt(row, 0));
        supplier.setName(String.valueOf(tableModel.getValueAt(row, 1)));
        supplier.setContact_person(String.valueOf(tableModel.getValueAt(row, 2)));
        supplier.setPhone(String.valueOf(tableModel.getValueAt(row, 3)));
        supplier.setEmail(String.valueOf(tableModel.getValueAt(row, 4)));
        supplier.setAddress(String.valueOf(tableModel.getValueAt(row, 5)));
        return supplier;
    }

    private void showSupplierDialog(Supplier supplier) {
        boolean editing = supplier != null;
        JDialog dialog = new JDialog(getOwnerFrame(), editing ? "Edit Supplier" : "Add Supplier", true);
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField contactPersonField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField addressField = new JTextField(20);

        if (editing) {
            nameField.setText(supplier.getName());
            contactPersonField.setText(supplier.getContact_person());
            phoneField.setText(supplier.getPhone());
            emailField.setText(supplier.getEmail());
            addressField.setText(supplier.getAddress());
        }

        addField(formPanel, constraints, 0, "Name:", nameField);
        addField(formPanel, constraints, 1, "Contact Person:", contactPersonField);
        addField(formPanel, constraints, 2, "Phone:", phoneField);
        addField(formPanel, constraints, 3, "Email:", emailField);
        addField(formPanel, constraints, 4, "Address:", addressField);

        JButton saveButton = new JButton(editing ? "Update" : "Add");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(event -> saveSupplier(
                dialog,
                supplier,
                nameField,
                contactPersonField,
                phoneField,
                emailField,
                addressField
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

    private void saveSupplier(
            JDialog dialog,
            Supplier existingSupplier,
            JTextField nameField,
            JTextField contactPersonField,
            JTextField phoneField,
            JTextField emailField,
            JTextField addressField
    ) {
        try {
            Supplier supplier = buildSupplierFromForm(
                    existingSupplier,
                    nameField,
                    contactPersonField,
                    phoneField,
                    emailField,
                    addressField
            );

            if (existingSupplier == null) {
                supplierDAO.addSupplier(supplier);
            } else {
                supplierDAO.updateSupplier(supplier);
            }

            refreshSuppliers();
            dialog.dispose();
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (SQLException exception) {
            showError("Unable to save supplier: " + exception.getMessage());
        }
    }

    private Supplier buildSupplierFromForm(
            Supplier existingSupplier,
            JTextField nameField,
            JTextField contactPersonField,
            JTextField phoneField,
            JTextField emailField,
            JTextField addressField
    ) {
        String name = requireValue(nameField.getText(), "Name is required.");
        String contactPerson = contactPersonField.getText() == null ? "" : contactPersonField.getText().trim();
        String phone = requireValue(phoneField.getText(), "Phone is required.");
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String address = addressField.getText() == null ? "" : addressField.getText().trim();

        if (!email.isEmpty() && !isValidEmail(email)) {
            throw new IllegalArgumentException("Email format is invalid.");
        }

        Supplier supplier = new Supplier();

        if (existingSupplier != null) {
            supplier.setSupplier_id(existingSupplier.getSupplier_id());
        }

        supplier.setName(name);
        supplier.setContact_person(contactPerson);
        supplier.setPhone(phone);
        supplier.setEmail(email);
        supplier.setAddress(address);
        return supplier;
    }

    private String requireValue(String value, String errorMessage) {
        String trimmedValue = value == null ? "" : value.trim();

        if (trimmedValue.isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }

        return trimmedValue;
    }

    private boolean isValidEmail(String email) {
        int atIndex = email.indexOf('@');
        int dotIndex = email.lastIndexOf('.');
        return atIndex > 0 && dotIndex > atIndex + 1 && dotIndex < email.length() - 1;
    }

    private void addField(
            JPanel panel,
            GridBagConstraints constraints,
            int row,
            String labelText,
            JTextField textField
    ) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;
        panel.add(new JLabel(labelText), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        panel.add(textField, constraints);
    }

    private Frame getOwnerFrame() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
