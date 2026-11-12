package com.restman.dao;

import java.sql.Connection;
import java.sql.DriverManager;


public class DAO3 {
    public static Connection con;

    public DAO3() {
        if (con == null) {
            String dbUrl = "jdbc:mysql://localhost:3306/restman_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8";
            String dbClass = "com.mysql.cj.jdbc.Driver";
            try {
                Class.forName(dbClass);
                try {
                    con = DriverManager.getConnection(dbUrl, "root", "123456");
                } catch (Exception e1) {
                    try {
                        con = DriverManager.getConnection(dbUrl, "root", "root");
                    } catch (Exception e2) {
                        con = DriverManager.getConnection(dbUrl, "root", "");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
