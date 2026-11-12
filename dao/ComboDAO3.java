package com.restman.dao;

import com.restman.model.Combo3;
import com.restman.model.ComboDish3;
import com.restman.model.Dish3;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class ComboDAO3 extends DAO3 {
    public ComboDAO3() {
        super();
    }

    public List<Combo3> searchCombo(String keyword) {
        List<Combo3> list = new ArrayList<>();
        if (con == null) return list;

        String sql = "SELECT id, code, name, price FROM tblCombo3 WHERE name LIKE ? ORDER BY id";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String pattern = "%" + (keyword == null ? "" : keyword.trim()) + "%";
            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Combo3(
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getLong("price")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Combo3 getComboDetail(int id) {
        if (con == null) return null;

        Combo3 combo = null;
        String sqlCombo = "SELECT id, code, name, price FROM tblCombo3 WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlCombo)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    combo = new Combo3(
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getLong("price")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (combo == null) return null;

        // Truy xuất danh sách món thành phần ComboDish3 thuộc combo
        List<ComboDish3> comboDishes = new ArrayList<>();
        String sqlDishes = "SELECT cd.id AS cdId, cd.quantity, d.id AS dishId, d.code, d.name, d.category, d.unit, d.price, d.status, d.description " +
                           "FROM tblComboDish3 cd " +
                           "JOIN tblDish3 d ON cd.dishId = d.id " +
                           "WHERE cd.comboId = ? " +
                           "ORDER BY cd.id";

        try (PreparedStatement ps = con.prepareStatement(sqlDishes)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Dish3 dish = new Dish3(
                            rs.getInt("dishId"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getString("unit"),
                            rs.getLong("price"),
                            rs.getString("status"),
                            rs.getString("description")
                    );
                    comboDishes.add(new ComboDish3(rs.getInt("cdId"), dish, rs.getInt("quantity")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Combo3(combo.id(), combo.code(), combo.name(), combo.price(), comboDishes);
    }
}
