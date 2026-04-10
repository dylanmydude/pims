package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import dao.MedicineDAO;
import dao.SaleDAO;
import model.Medicine;
import model.Sale;
import model.SaleItem;
import model.User;

public class POSPanel extends JPanel {

    private final User currentUser;
    private final MedicineDAO medicineDAO;
    private final SaleDAO saleDAO;
    private final DefaultTableModel medicinesTableModel;
    private final DefaultTableModel cartTableModel;
    private final JTable medicinesTable;
    private final JTable cartTable;
    private final JTextField searchField;
    private final JLabel totalLabel;
    private final JButton checkoutButton;
    private final TableRowSorter<DefaultTableModel> medicinesSorter;
    private final Map<Integer, Medicine> medicinesById;
    private final Map<Integer, CartItem> cartItems;
    private final DecimalFormat amountFormat;

    public POSPanel(User user) {
        this.currentUser = user;
        this.medicineDAO = new MedicineDAO();
        this.saleDAO = new SaleDAO();
        this.medicinesTableModel = createMedicinesTableModel();
        this.cartTableModel = createCartTableModel();
        this.medicinesTable = new JTable(medicinesTableModel);
        this.cartTable = new JTable(cartTableModel);
        this.searchField = new JTextField(24);
        this.totalLabel = new JLabel("Total: 0.00");
        this.checkoutButton = new JButton("Checkout");
        this.medicinesSorter = new TableRowSorter<>(medicinesTableModel);
        this.medicinesById = new LinkedHashMap<>();
        this.cartItems = new LinkedHashMap<>();
        this.amountFormat = new DecimalFormat("0.00");

        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        medicinesTable.setRowHeight(24);
        medicinesTable.setFillsViewportHeight(true);
        medicinesTable.setRowSorter(medicinesSorter);
        medicinesTable.setDefaultRenderer(Object.class, new LowStockCellRenderer());

        cartTable.setRowHeight(24);
        cartTable.setFillsViewportHeight(true);

        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        attachSearchBehaviour();
        attachMedicineSelectionBehaviour();
        loadMedicines();
        refreshCartTable();
    }

    private JSplitPane createCenterPanel() {
        JPanel medicinesPanel = new JPanel(new BorderLayout(0, 10));
        medicinesPanel.setBorder(BorderFactory.createTitledBorder("Medicines"));
        medicinesPanel.add(searchField, BorderLayout.NORTH);
        medicinesPanel.add(new JScrollPane(medicinesTable), BorderLayout.CENTER);

        JButton addToCartButton = new JButton("Add To Cart");
        addToCartButton.addActionListener(event -> addSelectedMedicineToCart());

        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftButtonPanel.add(addToCartButton);
        medicinesPanel.add(leftButtonPanel, BorderLayout.SOUTH);

        JPanel cartPanel = new JPanel(new BorderLayout(0, 10));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Cart"));
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, medicinesPanel, cartPanel);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerLocation(520);
        return splitPane;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        totalLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));

        JButton clearCartButton = new JButton("Clear Cart");

        checkoutButton.addActionListener(event -> handleCheckout());
        clearCartButton.addActionListener(event -> clearCart());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.add(clearCartButton);
        actionPanel.add(checkoutButton);

        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(actionPanel, BorderLayout.EAST);
        return bottomPanel;
    }

    private DefaultTableModel createMedicinesTableModel() {
        return new DefaultTableModel(new String[] {"ID", "Name", "Company", "Type", "Price", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private DefaultTableModel createCartTableModel() {
        return new DefaultTableModel(new String[] {"Medicine", "Quantity", "Unit Price", "Line Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void attachSearchBehaviour() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                filterMedicines();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                filterMedicines();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                filterMedicines();
            }
        });
    }

    private void attachMedicineSelectionBehaviour() {
        medicinesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                if (event.getClickCount() == 2) {
                    addSelectedMedicineToCart();
                }
            }
        });
    }

    private void filterMedicines() {
        String text = searchField.getText() == null ? "" : searchField.getText().trim();

        if (text.isEmpty()) {
            medicinesSorter.setRowFilter(null);
            return;
        }

        medicinesSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
    }

    private void loadMedicines() {
        medicinesTableModel.setRowCount(0);
        medicinesById.clear();

        try {
            List<Medicine> medicines = medicineDAO.getAllMedicines();

            for (Medicine medicine : medicines) {
                medicinesById.put(medicine.getMedicine_id(), medicine);
                medicinesTableModel.addRow(new Object[] {
                        medicine.getMedicine_id(),
                        medicine.getName(),
                        medicine.getCompany(),
                        medicine.getMedicine_type(),
                        medicine.getPrice(),
                        medicine.getQuantity_in_stock()
                });
            }
        } catch (SQLException exception) {
            showError("Unable to load medicines: " + exception.getMessage());
        }
    }

    private void addSelectedMedicineToCart() {
        int selectedRow = medicinesTable.getSelectedRow();

        if (selectedRow < 0) {
            showError("Please select a medicine to add to the cart.");
            return;
        }

        int modelRow = medicinesTable.convertRowIndexToModel(selectedRow);
        int medicineId = (int) medicinesTableModel.getValueAt(modelRow, 0);
        Medicine medicine = medicinesById.get(medicineId);

        if (medicine == null) {
            showError("Selected medicine could not be found.");
            return;
        }

        if (medicine.getQuantity_in_stock() <= 0) {
            showError("Selected medicine is out of stock.");
            return;
        }

        String quantityText = JOptionPane.showInputDialog(
                this,
                "Enter quantity for " + medicine.getName() + ":",
                "Add To Cart",
                JOptionPane.QUESTION_MESSAGE
        );

        if (quantityText == null) {
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText.trim());
        } catch (NumberFormatException exception) {
            showError("Quantity must be a valid whole number.");
            return;
        }

        if (quantity <= 0) {
            showError("Quantity must be greater than 0.");
            return;
        }

        CartItem existingItem = cartItems.get(medicineId);
        int existingQuantity = existingItem == null ? 0 : existingItem.quantity;
        int requestedTotal = existingQuantity + quantity;

        if (requestedTotal > medicine.getQuantity_in_stock()) {
            showError("Requested quantity exceeds available stock.");
            return;
        }

        if (existingItem == null) {
            cartItems.put(medicineId, new CartItem(medicineId, medicine.getName(), quantity, medicine.getPrice()));
        } else {
            existingItem.quantity = requestedTotal;
        }

        refreshCartTable();
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        double total = 0.0;

        for (CartItem item : cartItems.values()) {
            double lineTotal = item.quantity * item.unitPrice;
            total += lineTotal;

            cartTableModel.addRow(new Object[] {
                    item.name,
                    item.quantity,
                    amountFormat.format(item.unitPrice),
                    amountFormat.format(lineTotal)
            });
        }

        totalLabel.setText("Total: " + amountFormat.format(total));
        checkoutButton.setEnabled(!cartItems.isEmpty());
    }

    private void clearCart() {
        if (cartItems.isEmpty()) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Clear all items from the cart?",
                "Clear Cart",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        cartItems.clear();
        refreshCartTable();
    }

    private void handleCheckout() {
        if (cartItems.isEmpty()) {
            showError("Cart is empty.");
            return;
        }

        if (currentUser == null || currentUser.getUser_id() <= 0) {
            showError("Current cashier information is invalid.");
            return;
        }

        try {
            validateCartForCheckout();

            Sale sale = new Sale();
            sale.setUser_id(currentUser.getUser_id());
            sale.setSale_date(new Timestamp(System.currentTimeMillis()));
            sale.setTotal_amount(calculateCartTotal());

            List<SaleItem> items = buildSaleItems();
            saleDAO.createSale(sale, items);

            JOptionPane.showMessageDialog(
                    this,
                    "Sale completed successfully.",
                    "Checkout Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

            cartItems.clear();
            refreshCartTable();
            loadMedicines();
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (SQLException exception) {
            showError("Checkout failed: " + exception.getMessage());
            loadMedicines();
        }
    }

    private void validateCartForCheckout() {
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty.");
        }

        for (CartItem item : cartItems.values()) {
            Medicine medicine = medicinesById.get(item.medicineId);

            if (medicine == null) {
                throw new IllegalArgumentException("A cart item is no longer available.");
            }

            if (item.quantity <= 0) {
                throw new IllegalArgumentException("All cart quantities must be greater than 0.");
            }

            if (item.quantity > medicine.getQuantity_in_stock()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for " + medicine.getName() + ". Available: "
                                + medicine.getQuantity_in_stock()
                );
            }
        }
    }

    private List<SaleItem> buildSaleItems() {
        List<SaleItem> items = new ArrayList<>();

        for (CartItem item : cartItems.values()) {
            SaleItem saleItem = new SaleItem();
            saleItem.setMedicine_id(item.medicineId);
            saleItem.setQuantity_sold(item.quantity);
            saleItem.setPrice_at_sale(item.unitPrice);
            items.add(saleItem);
        }

        return items;
    }

    private double calculateCartTotal() {
        double total = 0.0;

        for (CartItem item : cartItems.values()) {
            total += item.quantity * item.unitPrice;
        }

        return total;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static final class CartItem {
        private final int medicineId;
        private final String name;
        private int quantity;
        private final double unitPrice;

        private CartItem(int medicineId, String name, int quantity, double unitPrice) {
            this.medicineId = medicineId;
            this.name = name;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
    }

    private final class LowStockCellRenderer extends DefaultTableCellRenderer {
        private static final Color LOW_STOCK_COLOR = new Color(255, 235, 205);
        private static final Color LOW_STOCK_SELECTED_COLOR = new Color(255, 204, 153);

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int modelRow = table.convertRowIndexToModel(row);
            int medicineId = (int) medicinesTableModel.getValueAt(modelRow, 0);
            Medicine medicine = medicinesById.get(medicineId);
            boolean isLowStock = medicine != null && medicine.getQuantity_in_stock() <= medicine.getReorder_level();

            if (isLowStock) {
                component.setBackground(isSelected ? LOW_STOCK_SELECTED_COLOR : LOW_STOCK_COLOR);
            } else {
                component.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            }

            return component;
        }
    }
}
