package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import dao.ReportDAO;

public class ReportPanel extends JPanel {

    private static final String SALES_REPORT = "Sales";
    private static final String ITEM_WISE_REPORT = "Item-wise";
    private static final String LOW_STOCK_REPORT = "Low stock";
    private static final String EXPIRY_REPORT = "Expiry";

    private final ReportDAO reportDAO;
    private final JComboBox<String> reportSelector;
    private final DefaultTableModel tableModel;
    private final JTable reportTable;

    public ReportPanel() {
        this.reportDAO = new ReportDAO();
        this.reportSelector = new JComboBox<>(new String[] {
                SALES_REPORT,
                ITEM_WISE_REPORT,
                LOW_STOCK_REPORT,
                EXPIRY_REPORT
        });
        this.tableModel = new DefaultTableModel();
        this.reportTable = new JTable(tableModel);

        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        reportTable.setRowHeight(24);
        reportTable.setFillsViewportHeight(true);

        add(createTopPanel(), BorderLayout.NORTH);
        add(new JScrollPane(reportTable), BorderLayout.CENTER);

        reportSelector.addActionListener(event -> loadSelectedReport());
        loadSelectedReport();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.add(new JLabel("Select Report:"));
        topPanel.add(reportSelector);
        return topPanel;
    }

    public void refreshCurrentReport() {
        loadSelectedReport();
    }

    private void loadSelectedReport() {
        String selectedReport = (String) reportSelector.getSelectedItem();

        if (SALES_REPORT.equals(selectedReport)) {
            loadSalesReport();
        } else if (ITEM_WISE_REPORT.equals(selectedReport)) {
            loadItemWiseSalesReport();
        } else if (LOW_STOCK_REPORT.equals(selectedReport)) {
            loadLowStockReport();
        } else if (EXPIRY_REPORT.equals(selectedReport)) {
            loadExpiryReport();
        }
    }

    private void loadSalesReport() {
        try {
            populateTable(
                    new String[] {"Date", "Total Sales"},
                    reportDAO.getSalesReport()
            );
        } catch (SQLException exception) {
            showError("Unable to load sales report: " + exception.getMessage());
        }
    }

    private void loadItemWiseSalesReport() {
        try {
            populateTable(
                    new String[] {"Medicine ID", "Medicine Name", "Quantity Sold"},
                    reportDAO.getItemWiseSalesReport()
            );
        } catch (SQLException exception) {
            showError("Unable to load item-wise sales report: " + exception.getMessage());
        }
    }

    private void loadLowStockReport() {
        try {
            populateTable(
                    new String[] {"Medicine ID", "Medicine Name", "Quantity In Stock", "Reorder Level"},
                    reportDAO.getLowStockReport()
            );
        } catch (SQLException exception) {
            showError("Unable to load low stock report: " + exception.getMessage());
        }
    }

    private void loadExpiryReport() {
        try {
            populateTable(
                    new String[] {"Medicine ID", "Medicine Name", "Expiry Date", "Quantity In Stock"},
                    reportDAO.getExpiryReport()
            );
        } catch (SQLException exception) {
            showError("Unable to load expiry report: " + exception.getMessage());
        }
    }

    private void populateTable(String[] columns, List<Object[]> rows) {
        tableModel.setDataVector(new Object[0][0], columns);

        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
