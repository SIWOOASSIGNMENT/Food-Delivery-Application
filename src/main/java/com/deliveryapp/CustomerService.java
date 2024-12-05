package com.deliveryapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class CustomerService {
    private final Connection connection;

    public CustomerService(Connection connection) {
        this.connection = connection;
    }

    public void listStores() {
        String query = "SELECT store_id, store_name, addr_info FROM Store";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("상점 목록:");
            while (rs.next()) {
                System.out.println("상점 ID: " + rs.getString("store_id") +
                        ", 이름: " + rs.getString("store_name") +
                        ", 주소: " + rs.getString("addr_info"));
            }
        } catch (SQLException e) {
            System.err.println("상점 조회 실패: " + e.getMessage());
        }
    }

    public void orderFood(String customerId) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("상점 ID를 입력하세요: ");
        String storeId = scanner.nextLine();
        System.out.print("상품 ID를 입력하세요: ");
        String productId = scanner.nextLine();
        System.out.print("수량을 입력하세요: ");
        int quantity = scanner.nextInt();

        String orderQuery = "INSERT INTO `Order` (customer_id, store_id, status, orderdate) VALUES (?, ?, 'Pending', NOW())";
        String orderListQuery = "INSERT INTO Order_List (order_id, product_id, quantity) VALUES (LAST_INSERT_ID(), ?, ?)";

        try (PreparedStatement orderStmt = connection.prepareStatement(orderQuery);
             PreparedStatement orderListStmt = connection.prepareStatement(orderListQuery)) {
            orderStmt.setString(1, customerId);
            orderStmt.setString(2, storeId);
            orderStmt.executeUpdate();

            orderListStmt.setString(1, productId);
            orderListStmt.setInt(2, quantity);
            orderListStmt.executeUpdate();

            System.out.println("주문이 성공적으로 생성되었습니다.");
        } catch (SQLException e) {
            System.err.println("주문 실패: " + e.getMessage());
        }
    }
}
