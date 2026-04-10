package ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.User;

public class CashierDashboard extends JFrame {

    public CashierDashboard(User user) {
        setTitle("PIMS - Cashier Dashboard");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(new JLabel("Welcome, " + user.getFullName() + " (Cashier)"), BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);
        add(new POSPanel(user), BorderLayout.CENTER);
    }
}
