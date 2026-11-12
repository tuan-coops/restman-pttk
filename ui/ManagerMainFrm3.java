package com.restman.ui;

import com.restman.dao.BillDAO3;
import com.restman.dao.StatCustomerDAO3;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

public final class ManagerMainFrm3 extends JFrame {
    private final JFrame previous;

    public ManagerMainFrm3(StatCustomerDAO3 statDAO, BillDAO3 billDAO, JFrame previous) {
        super("RestMan - Quản lý");
        this.previous = previous;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(previous);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (previous != null) previous.setVisible(true);
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        JLabel title = new JLabel("HỆ THỐNG QUẢN LÝ NHÀ HÀNG", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(12, 55, 109));
        panel.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JButton customer = menuButton("Thống kê khách hàng");
        JButton dish = menuButton("Thống kê món ăn");
        JButton supplier = menuButton("Thống kê nhà cung cấp");

        customer.addActionListener(e -> {
            setVisible(false);
            new StatCustomerFrm3(statDAO, billDAO, this).setVisible(true);
        });
        dish.addActionListener(e -> unavailable());
        supplier.addActionListener(e -> unavailable());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(12, 24, 12, 24);
        c.gridx = 0; c.gridy = 0; center.add(customer, c);
        c.gridx = 1; center.add(dish, c);
        c.gridx = 0; c.gridy = 1; center.add(supplier, c);
        panel.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        JButton logout = new JButton("Đăng xuất");
        logout.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logout.addActionListener(e -> {
            dispose();
            if (previous != null) previous.setVisible(true);
        });
        footer.add(logout);
        panel.add(footer, BorderLayout.SOUTH);

        setContentPane(panel);
    }

    private JButton menuButton(String text) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(285, 78));
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        return b;
    }

    private void unavailable() {
        JOptionPane.showMessageDialog(this, "Chức năng này chưa được triển khai.");
    }
}
