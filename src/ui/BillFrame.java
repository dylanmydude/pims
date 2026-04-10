package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.Sale;

public class BillFrame extends JFrame {

    private static final String PHARMACY_NAME = "HealthFirst Pharmacy";

    private final Sale sale;
    private final List<BillItem> items;
    private final JTable itemsTable;
    private final JLabel totalAmountLabel;
    private final DecimalFormat amountFormat;
    private final SimpleDateFormat dateFormat;

    public BillFrame(Sale sale, List<BillItem> items) {
        this.sale = sale;
        this.items = items;
        this.itemsTable = new JTable(createTableModel());
        this.totalAmountLabel = new JLabel();
        this.amountFormat = new DecimalFormat("0.00");
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        setTitle("Bill");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        itemsTable.setRowHeight(24);
        itemsTable.setFillsViewportHeight(true);

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(new JScrollPane(itemsTable), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        populateItems();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));

        JLabel pharmacyNameLabel = new JLabel(PHARMACY_NAME);
        pharmacyNameLabel.setFont(new Font("Serif", Font.BOLD, 22));

        JLabel saleIdLabel = new JLabel("Sale ID: " + sale.getSale_id());
        JLabel dateLabel = new JLabel("Date: " + formatDate());

        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        detailsPanel.add(saleIdLabel);
        detailsPanel.add(dateLabel);

        headerPanel.add(pharmacyNameLabel, BorderLayout.NORTH);
        headerPanel.add(detailsPanel, BorderLayout.SOUTH);
        return headerPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));

        JButton printButton = new JButton("Print");
        JButton saveButton = new JButton("Save");

        printButton.addActionListener(event -> printBill());
        saveButton.addActionListener(event -> saveBillAsText());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.add(saveButton);
        buttonPanel.add(printButton);

        totalAmountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        bottomPanel.add(totalAmountLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        return bottomPanel;
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(new String[] {"Name", "Quantity", "Price", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void populateItems() {
        DefaultTableModel tableModel = (DefaultTableModel) itemsTable.getModel();
        tableModel.setRowCount(0);

        for (BillItem item : items) {
            tableModel.addRow(new Object[] {
                    item.getName(),
                    item.getQuantity(),
                    amountFormat.format(item.getPrice()),
                    amountFormat.format(item.getSubtotal())
            });
        }

        totalAmountLabel.setText("Total Amount: " + amountFormat.format(sale.getTotal_amount()));
    }

    private void printBill() {
        try {
            boolean completed = itemsTable.print();
            if (!completed) {
                JOptionPane.showMessageDialog(this, "Print cancelled.", "Print", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to print bill: " + exception.getMessage(),
                    "Print Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void saveBillAsText() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("bill_" + sale.getSale_id() + ".txt"));

        int choice = fileChooser.showSaveDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(buildBillText());
            JOptionPane.showMessageDialog(this, "Bill saved successfully.", "Save", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to save bill: " + exception.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String buildBillText() {
        StringBuilder builder = new StringBuilder();
        builder.append(PHARMACY_NAME).append(System.lineSeparator());
        builder.append("Sale ID: ").append(sale.getSale_id()).append(System.lineSeparator());
        builder.append("Date: ").append(formatDate()).append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append(String.format("%-30s %-10s %-10s %-10s%n", "Name", "Quantity", "Price", "Subtotal"));

        for (BillItem item : items) {
            builder.append(
                    String.format(
                            "%-30s %-10d %-10s %-10s%n",
                            item.getName(),
                            item.getQuantity(),
                            amountFormat.format(item.getPrice()),
                            amountFormat.format(item.getSubtotal())
                    )
            );
        }

        builder.append(System.lineSeparator());
        builder.append("Total Amount: ").append(amountFormat.format(sale.getTotal_amount())).append(System.lineSeparator());
        return builder.toString();
    }

    private String formatDate() {
        if (sale.getSale_date() == null) {
            return "";
        }

        return dateFormat.format(sale.getSale_date());
    }

    public static class BillItem {
        private final String name;
        private final int quantity;
        private final double price;

        public BillItem(String name, int quantity, double price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }

        public double getSubtotal() {
            return quantity * price;
        }
    }
}
