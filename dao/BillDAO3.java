package com.restman.dao;

import com.restman.model.*;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class BillDAO3 extends DAO3 {
    public BillDAO3() {
        super();
    }

    public List<Order3> getOrdersByCustomer(int customerId, LocalDate startDate, LocalDate endDate) {
        List<Order3> orders = new ArrayList<>();
        if (con == null || startDate == null || endDate == null) return orders;

        String sql = "SELECT o.id, o.code, o.orderTime, o.status, o.sellerId, u.fullName AS sellerName " +
                     "FROM tblOrder3 o " +
                     "JOIN tblBill3 b ON o.billId = b.id " +
                     "JOIN tblUser3 u ON o.sellerId = u.id " +
                     "WHERE b.customerId = ? AND b.status = 'Đã thanh toán' " +
                     "  AND DATE(b.paymentTime) >= ? AND DATE(b.paymentTime) <= ? " +
                     "ORDER BY o.orderTime DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int orderId = rs.getInt("id");
                    String code = rs.getString("code");
                    LocalDateTime orderTime = rs.getTimestamp("orderTime").toLocalDateTime();
                    String status = rs.getString("status");
                    Seller3 seller = new Seller3(rs.getInt("sellerId"), "NV" + rs.getInt("sellerId"), rs.getString("sellerName"));

                    List<DishOrder3> dishOrders = loadDishOrders(orderId);
                    List<ComboOrder3> comboOrders = loadComboOrders(orderId);

                    orders.add(new Order3(orderId, code, orderTime, status, seller, dishOrders, comboOrders));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    public Bill3 getBillByOrder(int orderId) {
        if (con == null) return null;

        String sqlBill = "SELECT b.id, b.code, b.totalAmount, b.discountAmount, b.actualAmount, " +
                         "       b.paymentTime, b.paymentMethod, b.status, b.note, " +
                         "       c.id AS custId, c.code AS custCode, c.fullName AS custName, c.phone AS custPhone, c.email AS custEmail, " +
                         "       t.id AS tableId, t.code AS tableCode, t.name AS tableName, " +
                         "       s.id AS sellerId, s.fullName AS sellerName " +
                         "FROM tblBill3 b " +
                         "JOIN tblCustomer3 c ON b.customerId = c.id " +
                         "JOIN tblTable3 t ON b.tableId = t.id " +
                         "JOIN tblUser3 s ON b.sellerId = s.id " +
                         "WHERE b.id = (SELECT billId FROM tblOrder3 WHERE id = ?)";

        try (PreparedStatement ps = con.prepareStatement(sqlBill)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int billId = rs.getInt("id");
                    String code = rs.getString("code");
                    long totalAmount = rs.getLong("totalAmount");
                    long discountAmount = rs.getLong("discountAmount");
                    long actualAmount = rs.getLong("actualAmount");
                    LocalDateTime paymentTime = rs.getTimestamp("paymentTime").toLocalDateTime();
                    String paymentMethod = rs.getString("paymentMethod");
                    String status = rs.getString("status");
                    String note = rs.getString("note");

                    Customer3 customer = new Customer3(
                            rs.getInt("custId"),
                            rs.getString("custCode"),
                            rs.getString("custName"),
                            rs.getString("custPhone"),
                            rs.getString("custEmail")
                    );

                    Table3 table = new Table3(
                            rs.getInt("tableId"),
                            rs.getString("tableCode"),
                            rs.getString("tableName")
                    );

                    Seller3 seller = new Seller3(
                            rs.getInt("sellerId"),
                            "NV" + rs.getInt("sellerId"),
                            rs.getString("sellerName")
                    );

                    List<Order3> billOrders = loadOrdersForBill(billId);

                    return new Bill3(
                            billId, code, customer, table, seller,
                            billOrders, totalAmount, discountAmount, actualAmount,
                            paymentTime, paymentMethod, status, note == null ? "" : note
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Order3> loadOrdersForBill(int billId) {
        List<Order3> orders = new ArrayList<>();
        String sql = "SELECT o.id, o.code, o.orderTime, o.status, o.sellerId, u.fullName AS sellerName " +
                     "FROM tblOrder3 o " +
                     "JOIN tblUser3 u ON o.sellerId = u.id " +
                     "WHERE o.billId = ? " +
                     "ORDER BY o.orderTime ASC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int orderId = rs.getInt("id");
                    String code = rs.getString("code");
                    LocalDateTime orderTime = rs.getTimestamp("orderTime").toLocalDateTime();
                    String status = rs.getString("status");
                    Seller3 seller = new Seller3(rs.getInt("sellerId"), "NV" + rs.getInt("sellerId"), rs.getString("sellerName"));

                    List<DishOrder3> dishOrders = loadDishOrders(orderId);
                    List<ComboOrder3> comboOrders = loadComboOrders(orderId);

                    orders.add(new Order3(orderId, code, orderTime, status, seller, dishOrders, comboOrders));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    private List<DishOrder3> loadDishOrders(int orderId) {
        List<DishOrder3> list = new ArrayList<>();
        String sql = "SELECT do.id, do.quantity, do.price, " +
                     "       d.id AS dishId, d.code, d.name, d.category, d.unit, d.price AS defaultPrice, d.status, d.description " +
                     "FROM tblDishOrder3 do " +
                     "JOIN tblDish3 d ON do.dishId = d.id " +
                     "WHERE do.orderId = ? ORDER BY do.id";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Dish3 dish = new Dish3(
                            rs.getInt("dishId"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getString("unit"),
                            rs.getLong("defaultPrice"),
                            rs.getString("status"),
                            rs.getString("description")
                    );
                    list.add(new DishOrder3(rs.getInt("id"), dish, rs.getInt("quantity"), rs.getLong("price")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<ComboOrder3> loadComboOrders(int orderId) {
        List<ComboOrder3> list = new ArrayList<>();
        String sql = "SELECT co.id, co.quantity, co.price, " +
                     "       cb.id AS comboId, cb.code, cb.name, cb.price AS defaultPrice " +
                     "FROM tblComboOrder3 co " +
                     "JOIN tblCombo3 cb ON co.comboId = cb.id " +
                     "WHERE co.orderId = ? ORDER BY co.id";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Combo3 combo = new Combo3(
                            rs.getInt("comboId"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getLong("defaultPrice")
                    );
                    list.add(new ComboOrder3(rs.getInt("id"), combo, rs.getInt("quantity"), rs.getLong("price")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
