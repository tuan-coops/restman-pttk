package com.restman.ui;

import com.restman.dao.ComboDAO3;
import com.restman.dao.DishDAO3;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

public final class CustomerMainFrm3 extends JFrame {
    private final JFrame previous;

    public CustomerMainFrm3(DishDAO3 dishDAO, ComboDAO3 comboDAO, JFrame previous) {
        super("RestMan - Khách hàng");
        this.previous = previous;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 520);
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

        JButton search = menuButton("Tìm kiếm thông tin món ăn");
        JButton booking = menuButton("Đặt bàn trực tuyến");

        search.addActionListener(e -> {
            setVisible(false);
            new SearchDishFrm3(dishDAO, comboDAO, this).setVisible(true);
        });
        booking.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Chức năng đặt bàn không thuộc hai module đang triển khai."));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(16, 22, 16, 22);
        c.gridx = 0; center.add(search, c);
        c.gridx = 1; center.add(booking, c);
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
        b.setPreferredSize(new Dimension(300, 82));
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        return b;
    }
}
