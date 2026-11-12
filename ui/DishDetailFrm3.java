package com.restman.ui;

import com.restman.dao.DishDAO3;
import com.restman.model.Dish3;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

public final class DishDetailFrm3 extends JFrame {
    private final Dish3 dish;
    private final JFrame previous;

    public DishDetailFrm3(int dishId, DishDAO3 dishDAO, JFrame previous) {
        super("RestMan - Chi tiết món ăn");
        this.previous = previous;
        this.dish = dishDAO.getDishDetail(dishId);
        buildUi(this.dish);
    }

    public DishDetailFrm3(Dish3 dish, JFrame previous) {
        super("RestMan - Chi tiết món ăn");
        this.previous = previous;
        this.dish = dish;
        buildUi(dish);
    }

    private void buildUi(Dish3 dish) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 580);
        setLocationRelativeTo(previous);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                if (previous != null) previous.setVisible(true);
            }
        });

        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        if (dish == null) {
            panel.add(new JLabel("Không tìm thấy thông tin món ăn.", SwingConstants.CENTER), BorderLayout.CENTER);
            setContentPane(panel);
            return;
        }

        JLabel title = new JLabel("Chi tiết món ăn " + dish.code());
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(12, 55, 109));
        panel.add(title, BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 220, 230)),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 18);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        add(card, c, 0, "Mã món ăn", dish.code(), false);
        add(card, c, 1, "Tên món ăn", dish.name(), true);
        add(card, c, 2, "Phân loại", dish.category(), false);
        add(card, c, 3, "Đơn giá", String.format("%,d đ", dish.price()), true);
        add(card, c, 4, "Đơn vị tính", dish.unit(), false);
        add(card, c, 5, "Trạng thái", dish.status(), false);

        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 2;
        c.weightx = 1;
        JLabel descTitle = new JLabel("Mô tả chi tiết");
        descTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        card.add(descTitle, c);

        JTextArea description = new JTextArea(dish.description(), 5, 52);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setEditable(false);
        description.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        description.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        c.gridy = 7;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        card.add(new JScrollPane(description), c);

        panel.add(card, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton back = new JButton("Quay lại");
        back.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        back.addActionListener(e -> {
            dispose();
            if (previous != null) previous.setVisible(true);
        });
        actions.add(back);
        panel.add(actions, BorderLayout.SOUTH);

        setContentPane(panel);
    }

    private void add(JPanel p, GridBagConstraints c, int row, String name, String text, boolean bold) {
        c.gridy = row;
        c.gridx = 0;
        c.gridwidth = 1;
        c.weightx = 0;
        JLabel l = new JLabel(name + ":");
        l.setForeground(Color.GRAY);
        p.add(l, c);

        c.gridx = 1;
        c.weightx = 1;
        JLabel v = new JLabel(text);
        v.setForeground(bold ? new Color(12, 55, 109) : Color.DARK_GRAY);
        v.setFont(v.getFont().deriveFont(bold ? Font.BOLD : Font.PLAIN));
        p.add(v, c);
    }
}
