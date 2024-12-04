package com.deliveryapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerService {
    private final Connection connection;

    public CustomerService(Connection connection) {
        this.connection = connection;
    }

    public void listCustomers() {
        String query = "SELECT customer_id, user_name, user_rank FROM Customer";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("고객 목록:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getString("customer_id") +
                        ", 이름: " + rs.getString("user_name") +
                        ", 등급: " + rs.getString("user_rank"));
            }
        } catch (SQLException e) {
            System.err.println("고객 목록 조회 실패: " + e.getMessage());
        }
    }

    public void addCustomer(String customerId, String userId, String userName, String userRank) {
        String query = "INSERT INTO Customer (customer_id, user_id, user_name, user_rank) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, customerId);
            stmt.setString(2, userId);
            stmt.setString(3, userName);
            stmt.setString(4, userRank);
            stmt.executeUpdate();
            System.out.println("고객 추가 성공");
        } catch (SQLException e) {
            System.err.println("고객 추가 실패: " + e.getMessage());
        }
    }
}
