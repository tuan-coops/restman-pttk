package com.restman.ui;

import com.restman.dao.ComboDAO3;
import com.restman.dao.DishDAO3;
import com.restman.model.Combo3;
import com.restman.model.ComboDish3;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public final class ComboDetailFrm3 extends JFrame {
    private final Combo3 combo;
    private final DishDAO3 dishDAO;
    private final JFrame previous;
    private final DefaultTableModel model;
    private final JTable table;

    public ComboDetailFrm3(int comboId, ComboDAO3 comboDAO, DishDAO3 dishDAO, JFrame previous) {
        super("RestMan - Chi tiết combo");
        this.dishDAO = dishDAO;
        this.previous = previous;
        this.combo = comboDAO.getComboDetail(comboId);
        this.model = new DefaultTableModel(
                new Object[]{"STT", "Mã món", "Tên món ăn", "Phân loại", "Đơn vị tính", "Số lượng trong combo"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        this.table = new JTable(model);
        buildUi();
    }

    public ComboDetailFrm3(Combo3 combo, DishDAO3 dishDAO, JFrame previous) {
        super("RestMan - Chi tiết combo");
        this.combo = combo;
        this.dishDAO = dishDAO;
        this.previous = previous;
        this.model = new DefaultTableModel(
                new Object[]{"STT", "Mã món", "Tên món ăn", "Phân loại", "Đơn vị tính", "Số lượng trong combo"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        this.table = new JTable(model);
        buildUi();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(980, 580);
        setLocationRelativeTo(previous);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                if (previous != null) previous.setVisible(true);
            }
        });

        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        if (combo == null) {
            panel.add(new JLabel("Không tìm thấy thông tin combo.", SwingConstants.CENTER), BorderLayout.CENTER);
            setContentPane(panel);
            return;
        }

        JLabel title = new JLabel("Chi tiết combo " + combo.code());
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(12, 55, 109));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 16));

        JPanel info = new JPanel(new GridBagLayout());
        info.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 220, 230)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 8, 5, 24);
        c.anchor = GridBagConstraints.WEST;

        addInfo(info, c, 0, 0, "Mã combo", combo.code(), false);
        addInfo(info, c, 0, 2, "Tên combo", combo.name(), true);
        addInfo(info, c, 1, 0, "Đơn giá", String.format("%,d đ", combo.price()), true);
        addInfo(info, c, 1, 2, "Trạng thái", "Đang phục vụ", false);
        content.add(info, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        JLabel secTitle = new JLabel("Danh sách món ăn thành phần trong combo (ComboDish3)");
        secTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tablePanel.add(secTitle, BorderLayout.NORTH);

        int stt = 1;
        if (combo.dishes() != null) {
            for (ComboDish3 cd : combo.dishes()) {
                var dish = cd.dish();
                model.addRow(new Object[]{
                        stt++,
                        dish.code(),
                        dish.name(),
                        dish.category(),
                        dish.unit(),
                        cd.quantity() + " " + dish.unit().toLowerCase()
                });
            }
        }

        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(320);

        if (combo.dishes() != null && !combo.dishes().isEmpty()) {
            table.setRowSelectionInterval(0, 0);
        }

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    openDishDetail();
                }
            }
        });

        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(tablePanel, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        JButton viewDish = new JButton("Xem chi tiết món ăn");
        JButton back = new JButton("Quay lại danh sách");
        viewDish.setFont(new Font("Segoe UI", Font.BOLD, 13));
        back.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        viewDish.addActionListener(e -> openDishDetail());
        back.addActionListener(e -> {
            dispose();
            if (previous != null) previous.setVisible(true);
        });
        actions.add(viewDish);
        actions.add(back);
        content.add(actions, BorderLayout.SOUTH);

        panel.add(content, BorderLayout.CENTER);
        setContentPane(panel);
    }

    private void openDishDetail() {
        int selected = table.getSelectedRow();
        if (selected < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một món ăn trong danh sách thành phần.");
            return;
        }
        int modelIndex = table.convertRowIndexToModel(selected);
        ComboDish3 cd = combo.dishes().get(modelIndex);
        setVisible(false);
        if (dishDAO != null) {
            new DishDetailFrm3(cd.dish().id(), dishDAO, this).setVisible(true);
        } else {
            new DishDetailFrm3(cd.dish(), this).setVisible(true);
        }
    }

    private void addInfo(JPanel p, GridBagConstraints c, int row, int col, String name, String value, boolean bold) {
        c.gridy = row;
        c.gridx = col;
        JLabel l = new JLabel(name + ":");
        l.setForeground(Color.GRAY);
        p.add(l, c);
        c.gridx = col + 1;
        JLabel v = new JLabel(value);
        v.setForeground(bold ? new Color(12, 55, 109) : Color.DARK_GRAY);
        v.setFont(v.getFont().deriveFont(bold ? Font.BOLD : Font.PLAIN));
        p.add(v, c);
    }
}
