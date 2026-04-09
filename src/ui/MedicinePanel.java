package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Date;
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

import dao.MedicineDAO;
import model.Medicine;

public class MedicinePanel extends JPanel {

    private final MedicineDAO medicineDAO;
    private final DefaultTableModel tableModel;
    private final JTable medicineTable;

    public MedicinePanel() {
        this.medicineDAO = new MedicineDAO();
        this.tableModel = createTableModel();
        this.medicineTable = new JTable(tableModel);

        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        medicineTable.setRowHeight(24);
        medicineTable.setFillsViewportHeight(true);

        add(new JScrollPane(medicineTable), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        refreshMedicines();
    }

    public void refreshMedicines() {
        loadMedicines();
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        addButton.addActionListener(event -> showMedicineDialog(null));
        editButton.addActionListener(event -> editSelectedMedicine());
        deleteButton.addActionListener(event -> deleteSelectedMedicine());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        return buttonPanel;
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(
                new String[] {
                        "ID",
                        "Name",
                        "Company",
                        "Type",
                        "Price",
                        "Quantity",
                        "Reorder Level",
                        "Expiry Date",
                        "Supplier ID"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void loadMedicines() {
        tableModel.setRowCount(0);

        try {
            List<Medicine> medicines = medicineDAO.getAllMedicines();

            for (Medicine medicine : medicines) {
                tableModel.addRow(new Object[] {
                        medicine.getMedicine_id(),
                        medicine.getName(),
                        medicine.getCompany(),
                        medicine.getMedicine_type(),
                        medicine.getPrice(),
                        medicine.getQuantity_in_stock(),
                        medicine.getReorder_level(),
                        medicine.getExpiry_date(),
                        medicine.getSupplier_id()
                });
            }
        } catch (SQLException exception) {
            showError("Unable to load medicines: " + exception.getMessage());
        }
    }

    private void editSelectedMedicine() {
        int selectedRow = medicineTable.getSelectedRow();

        if (selectedRow < 0) {
            showError("Please select a medicine to edit.");
            return;
        }

        Medicine medicine = getMedicineFromRow(selectedRow);
        showMedicineDialog(medicine);
    }

    private void deleteSelectedMedicine() {
        int selectedRow = medicineTable.getSelectedRow();

        if (selectedRow < 0) {
            showError("Please select a medicine to delete.");
            return;
        }

        int medicineId = (int) tableModel.getValueAt(selectedRow, 0);
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete selected medicine?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            medicineDAO.deleteMedicine(medicineId);
            loadMedicines();
        } catch (SQLException exception) {
            showError("Unable to delete medicine: " + exception.getMessage());
        }
    }

    private Medicine getMedicineFromRow(int row) {
        Medicine medicine = new Medicine();
        medicine.setMedicine_id((int) tableModel.getValueAt(row, 0));
        medicine.setName(String.valueOf(tableModel.getValueAt(row, 1)));
        medicine.setCompany(String.valueOf(tableModel.getValueAt(row, 2)));
        medicine.setMedicine_type(String.valueOf(tableModel.getValueAt(row, 3)));
        medicine.setPrice(((Number) tableModel.getValueAt(row, 4)).doubleValue());
        medicine.setQuantity_in_stock(((Number) tableModel.getValueAt(row, 5)).intValue());
        medicine.setReorder_level(((Number) tableModel.getValueAt(row, 6)).intValue());
        medicine.setExpiry_date((Date) tableModel.getValueAt(row, 7));
        medicine.setSupplier_id(((Number) tableModel.getValueAt(row, 8)).intValue());
        return medicine;
    }

    private void showMedicineDialog(Medicine medicine) {
        boolean editing = medicine != null;
        JDialog dialog = new JDialog(getOwnerFrame(), editing ? "Edit Medicine" : "Add Medicine", true);
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField companyField = new JTextField(20);
        JTextField typeField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField quantityField = new JTextField(20);
        JTextField reorderLevelField = new JTextField(20);
        JTextField expiryDateField = new JTextField(20);
        JTextField supplierIdField = new JTextField(20);

        if (editing) {
            nameField.setText(medicine.getName());
            companyField.setText(medicine.getCompany());
            typeField.setText(medicine.getMedicine_type());
            priceField.setText(String.valueOf(medicine.getPrice()));
            quantityField.setText(String.valueOf(medicine.getQuantity_in_stock()));
            reorderLevelField.setText(String.valueOf(medicine.getReorder_level()));
            expiryDateField.setText(String.valueOf(medicine.getExpiry_date()));
            supplierIdField.setText(String.valueOf(medicine.getSupplier_id()));
        }

        addField(formPanel, constraints, 0, "Name:", nameField);
        addField(formPanel, constraints, 1, "Company:", companyField);
        addField(formPanel, constraints, 2, "Medicine Type:", typeField);
        addField(formPanel, constraints, 3, "Price:", priceField);
        addField(formPanel, constraints, 4, "Quantity In Stock:", quantityField);
        addField(formPanel, constraints, 5, "Reorder Level:", reorderLevelField);
        addField(formPanel, constraints, 6, "Expiry Date (yyyy-mm-dd):", expiryDateField);
        addField(formPanel, constraints, 7, "Supplier ID:", supplierIdField);

        JButton saveButton = new JButton(editing ? "Update" : "Add");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(event -> saveMedicine(
                dialog,
                medicine,
                nameField,
                companyField,
                typeField,
                priceField,
                quantityField,
                reorderLevelField,
                expiryDateField,
                supplierIdField
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

    private void saveMedicine(
            JDialog dialog,
            Medicine existingMedicine,
            JTextField nameField,
            JTextField companyField,
            JTextField typeField,
            JTextField priceField,
            JTextField quantityField,
            JTextField reorderLevelField,
            JTextField expiryDateField,
            JTextField supplierIdField
    ) {
        try {
            Medicine medicine = buildMedicineFromForm(
                    existingMedicine,
                    nameField,
                    companyField,
                    typeField,
                    priceField,
                    quantityField,
                    reorderLevelField,
                    expiryDateField,
                    supplierIdField
            );

            if (!medicineDAO.supplierExists(medicine.getSupplier_id())) {
                showError("Supplier ID does not exist. Use an existing supplier ID, such as 1 or 2.");
                return;
            }

            if (existingMedicine == null) {
                medicineDAO.addMedicine(medicine);
            } else {
                medicineDAO.updateMedicine(medicine);
            }

            loadMedicines();
            dialog.dispose();
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (SQLException exception) {
            showError("Unable to save medicine: " + exception.getMessage());
        }
    }

    private Medicine buildMedicineFromForm(
            Medicine existingMedicine,
            JTextField nameField,
            JTextField companyField,
            JTextField typeField,
            JTextField priceField,
            JTextField quantityField,
            JTextField reorderLevelField,
            JTextField expiryDateField,
            JTextField supplierIdField
    ) {
        String name = requireValue(nameField.getText(), "Name is required.");
        String company = requireValue(companyField.getText(), "Company is required.");
        String medicineType = requireValue(typeField.getText(), "Medicine type is required.");
        String priceText = requireValue(priceField.getText(), "Price is required.");
        String quantityText = requireValue(quantityField.getText(), "Quantity is required.");
        String reorderLevelText = requireValue(reorderLevelField.getText(), "Reorder level is required.");
        String expiryDateText = requireValue(expiryDateField.getText(), "Expiry date is required.");
        String supplierIdText = requireValue(supplierIdField.getText(), "Supplier ID is required.");

        double price;
        int quantityInStock;
        int reorderLevel;
        int supplierId;
        Date expiryDate;

        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Price must be a valid number.");
        }

        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }

        try {
            quantityInStock = Integer.parseInt(quantityText);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Quantity must be a valid integer.");
        }

        if (quantityInStock < 0) {
            throw new IllegalArgumentException("Quantity must be 0 or greater.");
        }

        try {
            reorderLevel = Integer.parseInt(reorderLevelText);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Reorder level must be a valid integer.");
        }

        if (reorderLevel < 0) {
            throw new IllegalArgumentException("Reorder level must be 0 or greater.");
        }

        try {
            supplierId = Integer.parseInt(supplierIdText);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Supplier ID must be a valid integer.");
        }

        if (supplierId <= 0) {
            throw new IllegalArgumentException("Supplier ID must be greater than 0.");
        }

        try {
            expiryDate = Date.valueOf(expiryDateText);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Expiry date must be in yyyy-mm-dd format.");
        }

        Medicine medicine = new Medicine();

        if (existingMedicine != null) {
            medicine.setMedicine_id(existingMedicine.getMedicine_id());
        }

        medicine.setName(name);
        medicine.setCompany(company);
        medicine.setMedicine_type(medicineType);
        medicine.setPrice(price);
        medicine.setQuantity_in_stock(quantityInStock);
        medicine.setReorder_level(reorderLevel);
        medicine.setExpiry_date(expiryDate);
        medicine.setSupplier_id(supplierId);
        return medicine;
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
