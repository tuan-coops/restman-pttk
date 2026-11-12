package com.restman.ui;

import com.restman.dao.ComboDAO3;
import com.restman.dao.DishDAO3;
import com.restman.model.Combo3;
import com.restman.model.Dish3;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class SearchDishFrm3 extends JFrame {
    private record SearchResult(String type, int id, String code, String name,
            String category, String unit, long price, String status) {}

    private final DishDAO3 dishDAO;
    private final ComboDAO3 comboDAO;
    private final JFrame previous;
    private final JTextField keyword = new JTextField(24);
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Mã", "Tên món ăn / Combo", "Loại kết quả", "Phân loại", "Đơn vị", "Đơn giá", "Trạng thái"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private List<SearchResult> results = List.of();

    public SearchDishFrm3(DishDAO3 dishDAO, ComboDAO3 comboDAO, JFrame previous) {
        super("RestMan - Tìm kiếm món ăn và combo");
        this.dishDAO = dishDAO;
        this.comboDAO = comboDAO;
        this.previous = previous;
        buildUi();
        reset();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1180, 680);
        setLocationRelativeTo(previous);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                if (previous != null) previous.setVisible(true);
            }
        });

        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel header = new JPanel(new BorderLayout(0, 4));
        JLabel title = new JLabel("Tìm kiếm thông tin món ăn và combo");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(12, 55, 109));
        header.add(title, BorderLayout.NORTH);
        JLabel note = new JLabel("Tra cứu món ăn và combo đang phục vụ trong nhà hàng");
        note.setForeground(Color.GRAY);
        header.add(note, BorderLayout.SOUTH);
        panel.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 14));

        JPanel filters = new JPanel(new GridBagLayout());
        filters.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 220, 230)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 8, 4, 8);
        c.gridy = 0;
        c.gridx = 0;
        filters.add(new JLabel("Từ khóa:"), c);
        c.gridx++;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        filters.add(keyword, c);
        c.gridx++;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;

        JButton find = new JButton("Tìm kiếm");
        JButton reset = new JButton("Làm mới");
        find.setFont(new Font("Segoe UI", Font.BOLD, 13));
        reset.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filters.add(find, c);
        c.gridx++;
        filters.add(reset, c);
        content.add(filters, BorderLayout.NORTH);

        find.addActionListener(e -> search());
        keyword.addActionListener(e -> search());
        reset.addActionListener(e -> reset());

        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(1).setPreferredWidth(310);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        content.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        JButton detail = new JButton("Xem chi tiết kết quả đang chọn");
        JButton back = new JButton("Quay lại trang chủ");
        detail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        back.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        actions.add(detail);
        actions.add(back);

        detail.addActionListener(e -> openDetail());
        back.addActionListener(e -> {
            dispose();
            if (previous != null) previous.setVisible(true);
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent event) {
                if (event.getClickCount() == 2) openDetail();
            }
        });

        content.add(actions, BorderLayout.SOUTH);
        panel.add(content, BorderLayout.CENTER);

        setContentPane(panel);
    }

    private void search() {
        String key = keyword.getText().trim();
        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm.");
            return;
        }
        loadResults(key);
        if (results.isEmpty()) JOptionPane.showMessageDialog(this, "Không tìm thấy món ăn hoặc combo phù hợp.");
    }

    private void reset() {
        keyword.setText("");
        loadResults("");
        keyword.requestFocusInWindow();
    }

    private void loadResults(String key) {
        List<SearchResult> loaded = new ArrayList<>();
        for (Dish3 dish : dishDAO.searchDish(key)) {
            loaded.add(new SearchResult("Món ăn", dish.id(), dish.code(), dish.name(),
                    dish.category(), dish.unit(), dish.price(), dish.status()));
        }
        for (Combo3 combo : comboDAO.searchCombo(key)) {
            loaded.add(new SearchResult("Combo", combo.id(), combo.code(), combo.name(),
                    "Combo", "Suất", combo.price(), "Đang phục vụ"));
        }
        results = List.copyOf(loaded);
        showResults();
    }

    private void showResults() {
        model.setRowCount(0);
        for (SearchResult result : results) {
            String moneyStr = String.format("%,d đ", result.price());
            model.addRow(new Object[]{result.code(), result.name(), result.type(), result.category(),
                    result.unit(), moneyStr, result.status()});
        }
        if (!results.isEmpty()) table.setRowSelectionInterval(0, 0);
    }

    private void openDetail() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một món ăn hoặc combo.");
            return;
        }
        SearchResult result = results.get(table.convertRowIndexToModel(viewRow));
        setVisible(false);
        if ("Combo".equals(result.type())) {
            new ComboDetailFrm3(result.id(), comboDAO, dishDAO, this).setVisible(true);
        } else {
            new DishDetailFrm3(result.id(), dishDAO, this).setVisible(true);
        }
    }
}
