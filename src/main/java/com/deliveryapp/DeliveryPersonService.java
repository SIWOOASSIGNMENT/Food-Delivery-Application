package com.deliveryapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DeliveryPersonService {
    private final Connection connection;

    public DeliveryPersonService(Connection connection) {
        this.connection = connection;
    }

    public void viewDeliveries(String deliveryPersonId) {
        String query = "SELECT delivery_id, order_id, delivery_status FROM Delivery WHERE delivery_person_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, deliveryPersonId);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("배달 목록:");
                while (rs.next()) {
                    System.out.println("배달 ID: " + rs.getString("delivery_id") +
                            ", 주문 ID: " + rs.getString("order_id") +
                            ", 상태: " + rs.getString("delivery_status"));
                }
            }
        } catch (SQLException e) {
            System.err.println("배달 목록 조회 실패: " + e.getMessage());
        }
    }

    public void updateDeliveryStatus(String deliveryId, String status) {
        String query = "UPDATE Delivery SET delivery_status = ? WHERE delivery_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setString(2, deliveryId);
            stmt.executeUpdate();
            System.out.println("배달 상태가 업데이트되었습니다.");
        } catch (SQLException e) {
            System.err.println("배달 상태 업데이트 실패: " + e.getMessage());
        }
    }
}
