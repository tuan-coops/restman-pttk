package com.restman.dao;

import com.restman.model.Customer3;
import com.restman.model.StatCustomer3;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StatCustomerDAO3 extends DAO3 {
    public StatCustomerDAO3() {
        super();
    }

    public List<StatCustomer3> getCustomerStat(LocalDate startDate, LocalDate endDate) {
        List<StatCustomer3> list = new ArrayList<>();
        if (con == null || startDate == null || endDate == null) return list;

        String sql = "SELECT b.customerId, c.code, c.fullName, c.phone, c.email, " +
                     "       SUM(b.actualAmount) AS totalSpent, " +
                     "       (SELECT COUNT(*) FROM tblOrder3 o JOIN tblBill3 b2 ON o.billId = b2.id " +
                     "        WHERE b2.customerId = b.customerId AND b2.status = 'Đã thanh toán' " +
                     "          AND DATE(b2.paymentTime) >= ? AND DATE(b2.paymentTime) <= ?) AS totalOrders " +
                     "FROM tblBill3 b " +
                     "JOIN tblCustomer3 c ON b.customerId = c.id " +
                     "WHERE b.status = 'Đã thanh toán' " +
                     "  AND DATE(b.paymentTime) >= ? AND DATE(b.paymentTime) <= ? " +
                     "GROUP BY b.customerId, c.code, c.fullName, c.phone, c.email " +
                     "ORDER BY totalSpent DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            Date dStart = Date.valueOf(startDate);
            Date dEnd = Date.valueOf(endDate);

            ps.setDate(1, dStart);
            ps.setDate(2, dEnd);
            ps.setDate(3, dStart);
            ps.setDate(4, dEnd);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Customer3 customer = new Customer3(
                            rs.getInt("customerId"),
                            rs.getString("code"),
                            rs.getString("fullName"),
                            rs.getString("phone"),
                            rs.getString("email")
                    );
                    int totalOrders = rs.getInt("totalOrders");
                    long totalSpent = rs.getLong("totalSpent");
                    list.add(new StatCustomer3(customer, totalOrders, totalSpent));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Customer3 getCustomerDetail(int customerId) {
        if (con == null) return null;
        String sql = "SELECT id, code, fullName, phone, email FROM tblCustomer3 WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer3(
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("fullName"),
                            rs.getString("phone"),
                            rs.getString("email")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
