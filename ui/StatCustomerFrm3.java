package com.restman.ui;

import com.restman.dao.BillDAO3;
import com.restman.dao.StatCustomerDAO3;
import com.restman.model.StatCustomer3;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class StatCustomerFrm3 extends JFrame {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final StatCustomerDAO3 statDAO;
    private final BillDAO3 billDAO;
    private final JFrame previous;
    private final JTextField start = new JTextField(12), end = new JTextField(12);
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Mã KH", "Họ và tên khách hàng", "Số điện thoại", "Email liên hệ", "Số lần gọi món", "Tổng chi tiêu"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private List<StatCustomer3> stats = List.of();
    private LocalDate startDate, endDate;

    public StatCustomerFrm3(StatCustomerDAO3 statDAO, BillDAO3 billDAO, JFrame previous) {
        super("RestMan - Thống kê khách hàng");
        this.statDAO = statDAO;
        this.billDAO = billDAO;
        this.previous = previous;
        buildUi();
    }

    private void buildUi() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1120, 650);
        setLocationRelativeTo(previous);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                if (previous != null) previous.setVisible(true);
            }
        });

        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Thống kê khách hàng theo doanh thu");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(12, 55, 109));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 16));

        JPanel filters = new JPanel(new GridBagLayout());
        filters.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 220, 230)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 8, 4, 8);
        c.gridy = 0;
        c.gridx = 0;
        filters.add(new JLabel("Từ ngày:"), c);
        c.gridx++;
        filters.add(start, c);
        c.gridx++;
        filters.add(new JLabel("Đến ngày:"), c);
        c.gridx++;
        filters.add(end, c);

        JButton stat = new JButton("Thống kê");
        stat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        c.gridx++;
        filters.add(stat, c);

        FocusAdapter autoFormat = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField tf = (JTextField) e.getSource();
                String val = tf.getText().trim();
                if (!val.isEmpty()) {
                    tf.setText(normalizeDateString(val));
                }
            }
        };
        start.addFocusListener(autoFormat);
        end.addFocusListener(autoFormat);

        stat.addActionListener(e -> refresh());
        start.addActionListener(e -> refresh());
        end.addActionListener(e -> refresh());
        content.add(filters, BorderLayout.NORTH);

        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        content.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        JButton detail = new JButton("Xem chi tiết khách hàng");
        JButton exit = new JButton("Thoát chức năng");
        detail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        exit.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        actions.add(detail);
        actions.add(exit);
        detail.addActionListener(e -> openDetail());
        exit.addActionListener(e -> {
            dispose();
            if (previous != null) previous.setVisible(true);
        });
        content.add(actions, BorderLayout.SOUTH);

        panel.add(content, BorderLayout.CENTER);
        setContentPane(panel);
    }

    private void refresh() {
        String startText = start.getText().trim();
        String endText = end.getText().trim();
        if (startText.isEmpty() || endText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày bắt đầu và ngày kết thúc (định dạng dd/MM/yyyy).");
            return;
        }

        String normStart = normalizeDateString(startText);
        String normEnd = normalizeDateString(endText);
        start.setText(normStart);
        end.setText(normEnd);

        try {
            startDate = parseDate(normStart);
            endDate = parseDate(normEnd);
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc.");
            }
            stats = statDAO.getCustomerStat(startDate, endDate);
            model.setRowCount(0);
            for (StatCustomer3 s : stats) {
                var x = s.customer();
                String moneyStr = String.format("%,d đ", s.totalSpent());
                model.addRow(new Object[]{x.code(), x.fullName(), x.phone(), x.email(), s.totalOrders(), moneyStr});
            }
            if (!stats.isEmpty()) table.setRowSelectionInterval(0, 0);
            else JOptionPane.showMessageDialog(this, "Không có giao dịch trong khoảng thời gian đã chọn.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void openDetail() {
        int view = table.getSelectedRow();
        if (view < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng.");
            return;
        }
        StatCustomer3 selected = stats.get(table.convertRowIndexToModel(view));
        setVisible(false);
        new CustomerDetailStatFrm3(selected, startDate, endDate, billDAO, this).setVisible(true);
    }

    private static LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException("Ngày không được để trống.");
        String s = value.trim();
        try {
            return LocalDate.parse(s, DATE_FMT);
        } catch (DateTimeParseException ignored) { }

        String normalized = s.replaceAll("[-._\\s]+", "/");
        String[] parts = normalized.split("/");
        if (parts.length == 3) {
            try {
                int p0 = Integer.parseInt(parts[0].trim());
                int p1 = Integer.parseInt(parts[1].trim());
                int p2 = Integer.parseInt(parts[2].trim());
                if (p0 > 1000) return LocalDate.of(p0, p1, p2);
                int year = p2 < 100 ? (p2 < 70 ? 2000 + p2 : 1900 + p2) : p2;
                return LocalDate.of(year, p1, p0);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Ngày không hợp lệ: " + value);
            }
        }
        throw new IllegalArgumentException("Ngày phải có định dạng dd/MM/yyyy (ví dụ: 01/01/2026).");
    }

    private static String normalizeDateString(String value) {
        if (value == null || value.trim().isEmpty()) return "";
        try {
            LocalDate ld = parseDate(value);
            return ld.format(DATE_FMT);
        } catch (Exception e) {
            return value.trim();
        }
    }
}
