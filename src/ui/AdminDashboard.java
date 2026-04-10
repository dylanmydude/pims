package ui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import model.User;

public class AdminDashboard extends JFrame {

    private final User user;
    private final MedicinePanel medicinePanel;
    private final SupplierPanel supplierPanel;
    private final UserPanel userPanel;
    private final ReportPanel reportPanel;

    public AdminDashboard(User user) {
        this.user = user;
        this.medicinePanel = new MedicinePanel();
        this.supplierPanel = new SupplierPanel();
        this.userPanel = new UserPanel();
        this.reportPanel = new ReportPanel();

        setTitle("PIMS - Admin Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));

        JPanel headerPanel = createHeaderPanel();
        JTabbedPane tabbedPane = createTabbedPane();

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel welcomeLabel = new JLabel("Welcome, " + user.getFullName());
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(welcomeLabel, BorderLayout.SOUTH);
        return headerPanel;
    }

    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));

        tabbedPane.addTab("Medicines", medicinePanel);
        tabbedPane.addTab("Suppliers", supplierPanel);
        tabbedPane.addTab("Users", userPanel);
        tabbedPane.addTab("Reports", reportPanel);

        tabbedPane.addChangeListener(event -> {
            if (tabbedPane.getSelectedComponent() == medicinePanel) {
                medicinePanel.refreshMedicines();
            } else if (tabbedPane.getSelectedComponent() == supplierPanel) {
                supplierPanel.refreshSuppliers();
            } else if (tabbedPane.getSelectedComponent() == userPanel) {
                userPanel.refreshUsers();
            } else if (tabbedPane.getSelectedComponent() == reportPanel) {
                reportPanel.refreshCurrentReport();
            }
        });

        return tabbedPane;
    }
}
