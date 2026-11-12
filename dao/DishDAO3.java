package com.restman.dao;

import com.restman.model.Dish3;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class DishDAO3 extends DAO3 {
    public DishDAO3() {
        super();
    }

    public List<Dish3> searchDish(String keyword) {
        List<Dish3> list = new ArrayList<>();
        if (con == null) return list;

        String sql = "SELECT id, code, name, category, unit, price, status, description " +
                     "FROM tblDish3 WHERE name LIKE ? ORDER BY id";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String pattern = "%" + (keyword == null ? "" : keyword.trim()) + "%";
            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Dish3(
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getString("unit"),
                            rs.getLong("price"),
                            rs.getString("status"),
                            rs.getString("description")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Dish3 getDishDetail(int id) {
        if (con == null) return null;
        String sql = "SELECT id, code, name, category, unit, price, status, description FROM tblDish3 WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Dish3(
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getString("unit"),
                            rs.getLong("price"),
                            rs.getString("status"),
                            rs.getString("description")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
