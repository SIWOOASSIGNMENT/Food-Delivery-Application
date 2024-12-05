package com.deliveryapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class RestaurantService {
    private final Connection connection;

    public RestaurantService(Connection connection) {
        this.connection = connection;
    }

    public void addStore(String sellerId) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("상점 이름: ");
        String storeName = scanner.nextLine();
        System.out.print("상점 주소: ");
        String addrInfo = scanner.nextLine();

        String query = "INSERT INTO Store (seller_id, store_name, addr_info, start_date, end_date) VALUES (?, ?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR))";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, sellerId);
            stmt.setString(2, storeName);
            stmt.setString(3, addrInfo);
            stmt.executeUpdate();
            System.out.println("상점이 성공적으로 추가되었습니다.");
        } catch (SQLException e) {
            System.err.println("상점 추가 실패: " + e.getMessage());
        }
    }

    public void addMenu(String storeId) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("상품 이름: ");
        String productName = scanner.nextLine();
        System.out.print("상품 설명: ");
        String productDetail = scanner.nextLine();
        System.out.print("상품 가격: ");
        int price = scanner.nextInt();

        String query = "INSERT INTO Product (store_id, product_name, product_detail, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, storeId);
            stmt.setString(2, productName);
            stmt.setString(3, productDetail);
            stmt.setInt(4, price);
            stmt.executeUpdate();
            System.out.println("메뉴가 성공적으로 추가되었습니다.");
        } catch (SQLException e) {
            System.err.println("메뉴 추가 실패: " + e.getMessage());
        }
    }

    public void viewOrders(String storeId) {
        String query = "SELECT order_id, customer_id, status FROM `Order` WHERE store_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, storeId);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("주문 목록:");
                while (rs.next()) {
                    System.out.println("주문 ID: " + rs.getString("order_id") +
                            ", 고객 ID: " + rs.getString("customer_id") +
                            ", 상태: " + rs.getString("status"));
                }
            }
        } catch (SQLException e) {
            System.err.println("주문 조회 실패: " + e.getMessage());
        }
    }

    public void acceptOrder(String orderId) {
        String query = "UPDATE `Order` SET status = 'Accepted' WHERE order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, orderId);
            stmt.executeUpdate();
            System.out.println("주문이 수락되었습니다.");
        } catch (SQLException e) {
            System.err.println("주문 수락 실패: " + e.getMessage());
        }
    }
}
