package ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import model.User;

public class CashierDashboard extends JFrame {

    public CashierDashboard(User user) {
        setTitle("PIMS - Cashier Dashboard");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new JLabel("Welcome, " + user.getFullName() + " (Cashier)", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
