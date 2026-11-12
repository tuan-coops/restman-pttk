package com.restman.ui;

import com.restman.dao.BillDAO3;
import com.restman.model.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class BillDetailFrm3 extends JFrame {
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JFrame previous;
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Mã Order", "Tên món ăn / Combo", "Phân loại", "Số lượng", "Đơn giá", "Thành tiền"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    public BillDetailFrm3(int orderId, BillDAO3 billDAO, JFrame previous) {
        super("RestMan - Chi tiết hóa đơn");
        this.previous = previous;
        Bill3 bill = billDAO != null ? billDAO.getBillByOrder(orderId) : null;
        buildUi(bill, orderId);
    }

    public BillDetailFrm3(Bill3 bill, int selectedId, JFrame previous) {
        super("RestMan - Chi tiết hóa đơn");
        this.previous = previous;
        buildUi(bill, selectedId);
    }

    private void buildUi(Bill3 bill, int selectedId) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1180, 720);
        setLocationRelativeTo(previous);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                if (previous != null) previous.setVisible(true);
            }
        });

        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        if (bill == null) {
            panel.add(new JLabel("Không tìm thấy thông tin hóa đơn tương ứng với lần gọi này.", SwingConstants.CENTER), BorderLayout.CENTER);
            setContentPane(panel);
            return;
        }

        JLabel title = new JLabel("Chi tiết hóa đơn " + bill.code());
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(12, 55, 109));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 14));

        JPanel info = new JPanel(new GridBagLayout());
        info.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 220, 230)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 8, 4, 26);
        c.anchor = GridBagConstraints.WEST;

        addInfo(info, c, 0, 0, "Khách hàng", bill.customer() != null ? bill.customer().fullName() : "---", true);
        addInfo(info, c, 0, 2, "Thu ngân lập HĐ", bill.seller() != null ? bill.seller().fullName() : "---", true);
        addInfo(info, c, 1, 0, "Bàn phục vụ", bill.table() != null ? bill.table().name() : "---", true);
        addInfo(info, c, 1, 2, "Mã HĐ", bill.code(), true);
        addInfo(info, c, 2, 0, "Hình thức", bill.paymentMethod(), false);
        addInfo(info, c, 2, 2, "Ngày thanh toán", bill.paymentTime() != null ? bill.paymentTime().format(DT_FMT) : "---", true);
        content.add(info, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        JLabel secTitle = new JLabel("Chi tiết món và combo thuộc tất cả Order trong hóa đơn");
        secTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        center.add(secTitle, BorderLayout.NORTH);

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(1).setPreferredWidth(330);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        for (Order3 o : bill.orders()) {
            String code = o.code() + (o.id() == selectedId ? "  • Đã chọn" : "");
            if (o.dishOrders() != null) {
                for (DishOrder3 d : o.dishOrders()) {
                    model.addRow(new Object[]{code, d.dish().name(), "Món lẻ", d.quantity(),
                            String.format("%,d đ", d.price()), String.format("%,d đ", d.amount())});
                }
            }
            if (o.comboOrders() != null) {
                for (ComboOrder3 d : o.comboOrders()) {
                    model.addRow(new Object[]{code, d.combo().name(), "Combo", d.quantity(),
                            String.format("%,d đ", d.price()), String.format("%,d đ", d.amount())});
                }
            }
        }
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(18, 0));
        JTextArea note = new JTextArea("Ghi chú hóa đơn:\n" + (bill.note().isBlank() ? "Không có" : bill.note()), 3, 42);
        note.setEditable(false);
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        note.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 220, 230)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        footer.add(note, BorderLayout.CENTER);

        JPanel totals = new JPanel(new GridLayout(0, 1, 0, 3));
        long totalAmt = bill.totalAmount();
        long discAmt = bill.discountAmount();
        long actAmt = bill.actualAmount();

        JLabel lblTotal = new JLabel("Tổng tiền món: " + String.format("%,d đ", totalAmt));
        lblTotal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        totals.add(lblTotal);

        JLabel lblDisc = new JLabel("Giảm giá / Chiết khấu: " + (discAmt > 0 ? ("-" + String.format("%,d đ", discAmt)) : "0 đ"));
        lblDisc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDisc.setForeground(discAmt > 0 ? new Color(180, 40, 40) : Color.GRAY);
        lblDisc.setHorizontalAlignment(SwingConstants.RIGHT);
        totals.add(lblDisc);

        JLabel lblActual = new JLabel("TỔNG THỰC THU: " + String.format("%,d đ", actAmt));
        lblActual.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblActual.setForeground(new Color(12, 55, 109));
        lblActual.setHorizontalAlignment(SwingConstants.RIGHT);
        totals.add(lblActual);

        JButton back = new JButton("Quay lại danh sách Order");
        back.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        back.addActionListener(e -> {
            dispose();
            if (previous != null) previous.setVisible(true);
        });

        JPanel rightBottom = new JPanel(new BorderLayout(0, 10));
        rightBottom.add(totals, BorderLayout.NORTH);
        rightBottom.add(back, BorderLayout.SOUTH);
        footer.add(rightBottom, BorderLayout.EAST);
        content.add(footer, BorderLayout.SOUTH);

        panel.add(content, BorderLayout.CENTER);
        setContentPane(panel);
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
