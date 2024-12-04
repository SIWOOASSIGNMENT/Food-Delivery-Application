package com.deliveryapp;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) {
                System.out.println("데이터베이스 연결 실패");
                return;
            }

            CustomerService customerService = new CustomerService(connection);
            StoreService storeService = new StoreService(connection);
            OrderService orderService = new OrderService(connection);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n메뉴:");
                System.out.println("1. 고객 목록 조회");
                System.out.println("2. 고객 추가");
                System.out.println("3. 상점 목록 조회");
                System.out.println("4. 주문 목록 조회");
                System.out.println("5. 종료");
                System.out.print("선택: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // 개행 문자 제거

                switch (choice) {
                    case 1 -> customerService.listCustomers();
                    case 2 -> {
                        System.out.print("고객 ID: ");
                        String customerId = scanner.nextLine();
                        System.out.print("사용자 ID: ");
                        String userId = scanner.nextLine();
                        System.out.print("이름: ");
                        String userName = scanner.nextLine();
                        System.out.print("등급: ");
                        String userRank = scanner.nextLine();
                        customerService.addCustomer(customerId, userId, userName, userRank);
                    }
                    case 3 -> storeService.listStores();
                    case 4 -> orderService.listOrders();
                    case 5 -> {
                        System.out.println("프로그램 종료");
                        return;
                    }
                    default -> System.out.println("올바른 번호를 선택하세요.");
                }
            }
        } catch (Exception e) {
            System.err.println("애플리케이션 오류: " + e.getMessage());
        }
    }
}
