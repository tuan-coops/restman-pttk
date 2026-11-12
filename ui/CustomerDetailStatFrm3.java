package com.restman.ui;

import com.restman.dao.BillDAO3;
import com.restman.dao.StatCustomerDAO3;
import com.restman.model.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class CustomerDetailStatFrm3 extends JFrame {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat VI_CURRENCY = NumberFormat.getInstance(new Locale("vi", "VN"));

    private final BillDAO3 dao;
    private final List<Order3> orders;
    private final JFrame previous;
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Mã lần gọi", "Thời gian gọi", "Nhân viên phục vụ", "Số món/Combo", "Tổng tiền gọi món", "Trạng thái"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public CustomerDetailStatFrm3(int customerId, LocalDate start, LocalDate end, StatCustomerDAO3 statDAO, BillDAO3 dao, JFrame previous) {
        super("RestMan - Chi tiết khách hàng");
        this.dao = dao;
        this.previous = previous;
        Customer3 customer = statDAO != null ? statDAO.getCustomerDetail(customerId) : null;
        this.orders = dao.getOrdersByCustomer(customerId, start, end);

        long totalSpent = 0;
        for (Order3 o : orders) {
            long orderAmt = 0;
            if (o.dishOrders() != null) {
                for (DishOrder3 d : o.dishOrders()) orderAmt += d.amount();
            }
            if (o.comboOrders() != null) {
                for (ComboOrder3 c : o.comboOrders()) orderAmt += c.amount();
            }
            if (orderAmt == 0) {
                Bill3 b = dao.getBillByOrder(o.id());
                if (b != null) orderAmt = b.actualAmount();
            }
            totalSpent += orderAmt;
        }
        StatCustomer3 stat = new StatCustomer3(customer, orders.size(), totalSpent);
        buildUi(stat, start, end);
    }

    public CustomerDetailStatFrm3(StatCustomer3 stat, LocalDate start, LocalDate end, BillDAO3 dao, JFrame previous) {
        super("RestMan - Chi tiết khách hàng");
        this.dao = dao;
        this.previous = previous;
        this.orders = dao.getOrdersByCustomer(stat.customer().id(), start, end);
        buildUi(stat, start, end);
    }

    private void buildUi(StatCustomer3 stat, LocalDate start, LocalDate end) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1120, 660);
        setLocationRelativeTo(previous);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                if (previous != null) previous.setVisible(true);
            }
        });

        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        JPanel content = new JPanel(new BorderLayout(0, 22));

        // --- 1. Khối thông tin khách hàng phía trên theo đúng Ảnh 82 ---
        JPanel info = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 12, 6, 36);
        c.anchor = GridBagConstraints.WEST;

        addInfo(info, c, 0, 0, "Mã KH:", stat.customer() != null ? stat.customer().code() : "---", new Color(12, 55, 109));
        addInfo(info, c, 0, 2, "Họ và tên:", stat.customer() != null ? stat.customer().fullName() : "---", new Color(12, 55, 109));
        addInfo(info, c, 1, 0, "Khoảng ngày:", start.format(DATE_FMT) + " - " + end.format(DATE_FMT), Color.DARK_GRAY);
        addInfo(info, c, 1, 2, "Tổng tích lũy:", String.format("%,d VNĐ", stat.totalSpent()), new Color(204, 0, 0)); // Màu đỏ theo ảnh 82
        addInfo(info, c, 2, 0, "Số điện thoại:", stat.customer() != null ? stat.customer().phone() : "---", new Color(12, 55, 109));
        addInfo(info, c, 3, 0, "Tổng lần gọi:", stat.totalOrders() + " lần gọi", new Color(12, 55, 109));
        content.add(info, BorderLayout.NORTH);

        // --- 2. Bảng danh sách các lần gọi món chuẩn 6 cột theo đúng Ảnh 82 ---
        table.setRowHeight(32);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        table.getColumnModel().getColumn(0).setPreferredWidth(140);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        for (Order3 o : orders) {
            String orderCode = o.code();
            String orderTimeStr = o.orderTime().format(DT_FMT);
            String sellerName = o.seller() != null ? o.seller().fullName() : "---";

            int itemCount = 0;
            if (o.dishOrders() != null) itemCount += o.dishOrders().size();
            if (o.comboOrders() != null) itemCount += o.comboOrders().size();
            if (itemCount == 0) itemCount = 1;

            long orderTotal = 0;
            if (o.dishOrders() != null) {
                for (DishOrder3 d : o.dishOrders()) orderTotal += d.amount();
            }
            if (o.comboOrders() != null) {
                for (ComboOrder3 co : o.comboOrders()) orderTotal += co.amount();
            }
            if (orderTotal == 0) {
                Bill3 b = dao.getBillByOrder(o.id());
                if (b != null) orderTotal = b.actualAmount();
            }

            String orderTotalStr = VI_CURRENCY.format(orderTotal) + "đ";
            String status = (o.status() != null && !o.status().isBlank()) ? o.status() : "Đã thanh toán";

            model.addRow(new Object[]{orderCode, orderTimeStr, sellerName, itemCount, orderTotalStr, status});
        }

        if (!orders.isEmpty()) table.setRowSelectionInterval(0, 0);
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- 3. Hai nút bấm phía dưới căn giữa theo đúng Ảnh 82 ---
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        JButton bill = new JButton("Xem chi tiết hóa đơn");
        JButton back = new JButton("Quay lại bảng thống kê");

        bill.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bill.setForeground(new Color(12, 55, 109));
        bill.setPreferredSize(new Dimension(220, 42));

        back.setFont(new Font("Segoe UI", Font.BOLD, 13));
        back.setPreferredSize(new Dimension(220, 42));

        actions.add(bill);
        actions.add(back);

        bill.addActionListener(e -> openBill());
        back.addActionListener(e -> {
            dispose();
            if (previous != null) previous.setVisible(true);
        });
        content.add(actions, BorderLayout.SOUTH);

        panel.add(content, BorderLayout.CENTER);
        setContentPane(panel);
    }

    private void addInfo(JPanel p, GridBagConstraints c, int row, int col, String labelText, String valueText, Color valueColor) {
        c.gridy = row;
        c.gridx = col;
        JLabel l = new JLabel(labelText);
        l.setForeground(new Color(90, 90, 90));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(l, c);

        c.gridx = col + 1;
        JLabel v = new JLabel(valueText);
        v.setForeground(valueColor);
        v.setFont(new Font("Segoe UI", Font.BOLD, 14));
        p.add(v, c);
    }

    private void openBill() {
        int view = table.getSelectedRow();
        if (view < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lần gọi món.");
            return;
        }
        Order3 o = orders.get(table.convertRowIndexToModel(view));
        setVisible(false);
        new BillDetailFrm3(o.id(), dao, this).setVisible(true);
    }
}
