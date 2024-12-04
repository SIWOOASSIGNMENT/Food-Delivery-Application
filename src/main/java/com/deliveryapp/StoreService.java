package com.deliveryapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StoreService {
    private final Connection connection;

    public StoreService(Connection connection) {
        this.connection = connection;
    }

    public void listStores() {
        String query = "SELECT store_id, store_name, addr_info FROM Store";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("상점 목록:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getString("store_id") +
                        ", 이름: " + rs.getString("store_name") +
                        ", 주소: " + rs.getString("addr_info"));
            }
        } catch (SQLException e) {
            System.err.println("상점 목록 조회 실패: " + e.getMessage());
        }
    }
}
