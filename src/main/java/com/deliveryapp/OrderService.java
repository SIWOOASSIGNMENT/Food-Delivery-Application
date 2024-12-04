package com.deliveryapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderService {
    private final Connection connection;

    public OrderService(Connection connection) {
        this.connection = connection;
    }

    public void listOrders() {
        String query = "SELECT order_id, customer_id, store_id, status FROM `Order`";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("주문 목록:");
            while (rs.next()) {
                System.out.println("주문 ID: " + rs.getString("order_id") +
                        ", 고객 ID: " + rs.getString("customer_id") +
                        ", 상점 ID: " + rs.getString("store_id") +
                        ", 상태: " + rs.getString("status"));
            }
        } catch (SQLException e) {
            System.err.println("주문 목록 조회 실패: " + e.getMessage());
        }
    }
}
