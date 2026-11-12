package com.restman.ui;

import com.restman.dao.BillDAO3;
import com.restman.dao.ComboDAO3;
import com.restman.dao.DishDAO3;
import com.restman.dao.StatCustomerDAO3;
import java.awt.*;
import javax.swing.*;


public class MainFrm extends JFrame {
    private final DishDAO3 dishDAO;
    private final ComboDAO3 comboDAO;
    private final StatCustomerDAO3 statDAO;
    private final BillDAO3 billDAO;

    public MainFrm() {
        super("RestMan - Hệ thống quản lý nhà hàng");
        this.dishDAO = new DishDAO3();
        this.comboDAO = new ComboDAO3();
        this.statDAO = new StatCustomerDAO3();
        this.billDAO = new BillDAO3();

        initUi();
    }

    private void initUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 400);
        setMinimumSize(new Dimension(680, 360));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 36));
        root.setBorder(BorderFactory.createEmptyBorder(50, 70, 60, 70));

        JLabel title = new JLabel("HỆ THỐNG QUẢN LÝ NHÀ HÀNG", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(12, 55, 109));
        root.add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 28, 0));
        JButton customerBtn = new JButton("Giao diện khách hàng");
        JButton managerBtn = new JButton("Giao diện quản lý");

        customerBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        managerBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));

        customerBtn.addActionListener(e -> {
            setVisible(false);
            new CustomerMainFrm3(dishDAO, comboDAO, this).setVisible(true);
        });

        managerBtn.addActionListener(e -> {
            setVisible(false);
            new ManagerMainFrm3(statDAO, billDAO, this).setVisible(true);
        });

        buttons.add(customerBtn);
        buttons.add(managerBtn);
        root.add(buttons, BorderLayout.CENTER);

        setContentPane(root);
    }
}
