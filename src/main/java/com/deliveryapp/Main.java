package com.deliveryapp;

import java.sql.*;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) {
                System.out.println("데이터베이스 연결 실패");
                return;
            }

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n=== 배달 앱 ===");
                System.out.println("1. 회원가입");
                System.out.println("2. 로그인");
                System.out.println("3. 종료");
                System.out.print("선택: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {
                    registerUser(connection, scanner);
                } else if (choice == 2) {
                    loginUser(connection, scanner);
                } else if (choice == 3) {
                    System.out.println("프로그램 종료");
                    break;
                } else {
                    System.out.println("잘못된 선택입니다. 다시 시도하세요.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void registerUser(Connection connection, Scanner scanner) {
        try {
            System.out.println("\n=== 회원가입 ===");
            System.out.print("아이디: ");
            String userId = scanner.nextLine();

            System.out.print("비밀번호: ");
            String password = scanner.nextLine();

            System.out.print("이메일: ");
            String email = scanner.nextLine();

            System.out.print("전화번호: ");
            String phone = scanner.nextLine();

            System.out.println("계정 유형을 선택하세요:");
            System.out.println("1. 고객(Customer)");
            System.out.println("2. 배달원(Delivery Person)");
            System.out.println("3. 판매자(Seller)");
            System.out.print("선택: ");
            int accountType = scanner.nextInt();
            scanner.nextLine();

            String userTypeId;
            switch (accountType) {
                case 1 -> userTypeId = "01"; // Customer
                case 2 -> userTypeId = "02"; // Delivery Person
                case 3 -> userTypeId = "03"; // Seller
                default -> {
                    System.out.println("잘못된 계정 유형입니다.");
                    return;
                }
            }

            String insertUserQuery = "INSERT INTO User (user_id, user_type_id, password, user_email, user_tel) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement userStmt = connection.prepareStatement(insertUserQuery);
            userStmt.setString(1, userId);
            userStmt.setString(2, userTypeId);
            userStmt.setString(3, password);
            userStmt.setString(4, email);
            userStmt.setString(5, phone);
            userStmt.executeUpdate();

            switch (userTypeId) {
                case "01" -> registerCustomer(connection, scanner, userId);
                case "02" -> registerDeliveryPerson(connection, scanner, userId);
                case "03" -> registerSeller(connection, scanner, userId);
            }

            System.out.println("회원가입이 성공적으로 완료되었습니다!");
        } catch (SQLException e) {
            System.err.println("회원가입 실패: " + e.getMessage());
        }
    }

    private static void registerCustomer(Connection connection, Scanner scanner, String userId) throws SQLException {
        System.out.print("고객 이름: ");
        String customerName = scanner.nextLine();

        String insertCustomerQuery = "INSERT INTO Customer (customer_id, user_id, user_name) VALUES (?, ?, ?)";
        PreparedStatement customerStmt = connection.prepareStatement(insertCustomerQuery);
        customerStmt.setString(1, UUID.randomUUID().toString().substring(0, 10));
        customerStmt.setString(2, userId);
        customerStmt.setString(3, customerName);
        customerStmt.executeUpdate();
    }

    private static void registerDeliveryPerson(Connection connection, Scanner scanner, String userId) throws SQLException {
        System.out.print("운송수단 종류(예: 오토바이, 자전거): ");
        String vehicleType = scanner.nextLine();

        String vehicleQuery = "SELECT vehicle_type_id FROM Vehicle_Type WHERE vehicle_name = ?";
        PreparedStatement vehicleStmt = connection.prepareStatement(vehicleQuery);
        vehicleStmt.setString(1, vehicleType);
        ResultSet vehicleRs = vehicleStmt.executeQuery();

        String vehicleTypeId;
        if (vehicleRs.next()) {
            vehicleTypeId = vehicleRs.getString("vehicle_type_id");
        } else {
            System.out.println("운송수단이 존재하지 않아 기본값을 사용합니다.");
            vehicleTypeId = "DEFAULT";
        }

        String insertDeliveryPersonQuery = "INSERT INTO Delivery_Man (delivery_man_id, user_id, vehicle_type_id) VALUES (?, ?, ?)";
        PreparedStatement deliveryPersonStmt = connection.prepareStatement(insertDeliveryPersonQuery);
        deliveryPersonStmt.setString(1, UUID.randomUUID().toString().substring(0, 10));
        deliveryPersonStmt.setString(2, userId);
        deliveryPersonStmt.setString(3, vehicleTypeId);
        deliveryPersonStmt.executeUpdate();
    }

    private static void registerSeller(Connection connection, Scanner scanner, String userId) throws SQLException {
        String insertSellerQuery = "INSERT INTO Seller (seller_id, user_id, income) VALUES (?, ?, 0)";
        PreparedStatement sellerStmt = connection.prepareStatement(insertSellerQuery);
        sellerStmt.setString(1, UUID.randomUUID().toString().substring(0, 10));
        sellerStmt.setString(2, userId);
        sellerStmt.executeUpdate();
    }

    private static void loginUser(Connection connection, Scanner scanner) {
        System.out.print("아이디: ");
        String userId = scanner.nextLine();
        System.out.print("비밀번호: ");
        String password = scanner.nextLine();

        try {
            String query = "SELECT user_type_id FROM User WHERE user_id = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, userId);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String userTypeId = rs.getString("user_type_id");
                switch (userTypeId) {
                    case "01" -> handleCustomerMenu(connection, scanner, userId);
                    case "02" -> handleDeliveryPersonMenu(connection, scanner, userId);
                    case "03" -> handleSellerMenu(connection, scanner, userId);
                    default -> System.out.println("알 수 없는 계정 유형입니다.");
                }
            } else {
                System.out.println("로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.");
            }
        } catch (SQLException e) {
            System.err.println("로그인 실패: " + e.getMessage());
        }
    }


    private static void handleCustomerMenu(Connection connection, Scanner scanner, String userId) {
        while (true) {
            System.out.println("\n=== 고객 메뉴 ===");
            System.out.println("1. 음식 주문");
            System.out.println("2. 주문 내역 확인");
            System.out.println("3. 리뷰 작성");
            System.out.println("4. 문의 작성");
            System.out.println("5. 로그아웃");
            System.out.print("선택: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> placeOrder(connection, scanner, userId);
                case 2 -> viewOrders(connection, userId);
                case 3 -> writeReview(connection, scanner, userId);
                case 4 -> submitInquiry(connection, scanner, userId);
                case 5 -> {
                    System.out.println("로그아웃합니다.");
                    return;
                }
                default -> System.out.println("잘못된 선택입니다.");
            }
        }
    }

    private static void handleSellerMenu(Connection connection, Scanner scanner, String userId) {
        while (true) {
            System.out.println("\n=== 판매자 메뉴 ===");
            System.out.println("1. 레스토랑 추가");
            System.out.println("2. 메뉴 추가");
            System.out.println("3. 프로모션 관리");
            System.out.println("4. 리뷰 답변 작성");
            System.out.println("5. 로그아웃");
            System.out.print("선택: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addRestaurant(connection, scanner, userId);
                case 2 -> addMenuItem(connection, scanner, userId);
                case 3 -> managePromotion(connection, scanner, userId);
                case 4 -> respondToReview(connection, scanner, userId);
                case 5 -> {
                    System.out.println("로그아웃합니다.");
                    return;
                }
                default -> System.out.println("잘못된 선택입니다.");
            }
        }
    }

    private static void handleDeliveryPersonMenu(Connection connection, Scanner scanner, String userId) {
        while (true) {
            System.out.println("\n=== 배달원 메뉴 ===");
            System.out.println("1. 배달 가능한 주문 보기");
            System.out.println("2. 배달 상태 변경");
            System.out.println("3. 로그아웃");
            System.out.print("선택: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewAvailableDeliveries(connection);
                case 2 -> updateDeliveryStatus(connection, scanner, userId);
                case 3 -> {
                    System.out.println("로그아웃합니다.");
                    return;
                }
                default -> System.out.println("잘못된 선택입니다.");
            }
        }
    }

    private static void placeOrder(Connection connection, Scanner scanner, String userId) {
        try {
            System.out.println("=== 음식 주문 ===");

            // 고객 주소 선택 또는 입력
            System.out.print("주소 ID를 입력하세요 (새로운 주소를 추가하려면 'new' 입력): ");
            String addrId = scanner.nextLine();

            if (addrId.equalsIgnoreCase("new")) {
                System.out.print("우편번호를 입력하세요: ");
                String postNo = scanner.nextLine();
                System.out.print("주소 정보를 입력하세요: ");
                String addrInfo = scanner.nextLine();

                String insertAddrQuery = "INSERT INTO Customer_Addr (addr_id, customer_id, postno, addr_info) VALUES (?, ?, ?, ?)";
                PreparedStatement addrStmt = connection.prepareStatement(insertAddrQuery);
                addrId = UUID.randomUUID().toString().substring(0, 10);
                addrStmt.setString(1, addrId);
                addrStmt.setString(2, userId);
                addrStmt.setString(3, postNo);
                addrStmt.setString(4, addrInfo);
                addrStmt.executeUpdate();
                System.out.println("주소가 성공적으로 추가되었습니다.");
            }

            // 레스토랑 선택
            System.out.print("레스토랑 ID를 입력하세요: ");
            String restaurantId = scanner.nextLine();

            // 주문 항목 추가
            String orderId = UUID.randomUUID().toString().substring(0, 10);
            boolean addMoreItems = true;

            while (addMoreItems) {
                System.out.print("메뉴 ID를 입력하세요: ");
                String productId = scanner.nextLine();
                System.out.print("수량을 입력하세요: ");
                int quantity = scanner.nextInt();
                scanner.nextLine();

                String insertOrderListQuery = "INSERT INTO Order_List (order_id, product_id, quantity) VALUES (?, ?, ?)";
                PreparedStatement orderListStmt = connection.prepareStatement(insertOrderListQuery);
                orderListStmt.setString(1, orderId);
                orderListStmt.setString(2, productId);
                orderListStmt.setInt(3, quantity);
                orderListStmt.executeUpdate();

                System.out.print("추가로 주문하시겠습니까? (yes/no): ");
                String response = scanner.nextLine();
                if (!response.equalsIgnoreCase("yes")) {
                    addMoreItems = false;
                }
            }

            // 주문 생성
            String insertOrderQuery = "INSERT INTO `Order` (order_id, customer_id, restaurant_id, addr_id, ordertime) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
            PreparedStatement orderStmt = connection.prepareStatement(insertOrderQuery);
            orderStmt.setString(1, orderId);
            orderStmt.setString(2, userId);
            orderStmt.setString(3, restaurantId);
            orderStmt.setString(4, addrId);
            orderStmt.executeUpdate();

            System.out.println("주문이 성공적으로 완료되었습니다! 주문 ID: " + orderId);
        } catch (SQLException e) {
            System.err.println("주문 실패: " + e.getMessage());
        }
    }

    private static void viewOrders(Connection connection, String userId) {
        try {
            String query = "CALL get_customer_orders(?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println("주문 번호: " + rs.getString("order_id"));
                System.out.println("음식 이름: " + rs.getString("product_name"));
                System.out.println("수량: " + rs.getInt("quantity"));
                System.out.println("레스토랑 이름: " + rs.getString("restaurant_name"));
                System.out.println("주문 시간: " + rs.getTimestamp("ordertime"));
                System.out.println("-------------------------");
            }
        } catch (SQLException e) {
            System.err.println("주문 내역 확인 실패: " + e.getMessage());
        }
    }

    private static void writeReview(Connection connection, Scanner scanner, String userId) {
        try {
            System.out.println("=== 리뷰 작성 ===");

            // 식당 이름 입력
            System.out.print("리뷰를 작성할 식당 이름을 입력하세요: ");
            String restaurantName = scanner.nextLine();

            // 식당 이름으로 레스토랑 ID 조회
            String restaurantQuery = "SELECT restaurant_id FROM Restaurant WHERE restaurant_name = ?";
            PreparedStatement restaurantStmt = connection.prepareStatement(restaurantQuery);
            restaurantStmt.setString(1, restaurantName);
            ResultSet restaurantRs = restaurantStmt.executeQuery();

            if (restaurantRs.next()) {
                String restaurantId = restaurantRs.getString("restaurant_id");

                // 리뷰 내용 입력
                System.out.print("리뷰 내용을 입력하세요: ");
                String content = scanner.nextLine();
                System.out.print("평점을 입력하세요(1-5): ");
                int rating = scanner.nextInt();
                scanner.nextLine();

                // 리뷰 추가
                String insertReviewQuery = "INSERT INTO Review (review_id, customer_id, restaurant_id, content, rating) VALUES (?, ?, ?, ?, ?)";
                String reviewId = UUID.randomUUID().toString().substring(0, 10);
                PreparedStatement reviewStmt = connection.prepareStatement(insertReviewQuery);
                reviewStmt.setString(1, reviewId);
                reviewStmt.setString(2, userId);
                reviewStmt.setString(3, restaurantId);
                reviewStmt.setString(4, content);
                reviewStmt.setInt(5, rating);
                reviewStmt.executeUpdate();

                // 리뷰 메타데이터 추가
                String insertMetadataQuery = "INSERT INTO Review_Metadata (metadata_id, review_id, create_date, update_date) VALUES (?, ?, CURRENT_DATE, CURRENT_DATE)";
                PreparedStatement metadataStmt = connection.prepareStatement(insertMetadataQuery);
                metadataStmt.setString(1, UUID.randomUUID().toString().substring(0, 10));
                metadataStmt.setString(2, reviewId);
                metadataStmt.executeUpdate();

                System.out.println("리뷰가 성공적으로 작성되었습니다!");
            } else {
                System.out.println("해당 이름의 식당을 찾을 수 없습니다. 다시 확인해주세요.");
            }
        } catch (SQLException e) {
            System.err.println("리뷰 작성 실패: " + e.getMessage());
        }
    }


    private static void submitInquiry(Connection connection, Scanner scanner, String userId) {
        try {
            System.out.println("=== 문의 작성 ===");
            System.out.print("문의 내용을 입력하세요: ");
            String content = scanner.nextLine();

            String insertInquiryQuery = "INSERT INTO Inquiry (Inquiry_id, customer_id, date, content) VALUES (?, ?, CURRENT_DATE, ?)";
            PreparedStatement inquiryStmt = connection.prepareStatement(insertInquiryQuery);
            inquiryStmt.setString(1, UUID.randomUUID().toString().substring(0, 10));
            inquiryStmt.setString(2, userId);
            inquiryStmt.setString(3, content);
            inquiryStmt.executeUpdate();

            System.out.println("문의가 성공적으로 제출되었습니다!");
        } catch (SQLException e) {
            System.err.println("문의 제출 실패: " + e.getMessage());
        }
    }


    private static void addRestaurant(Connection connection, Scanner scanner, String userId) {
        try {
            System.out.println("=== 레스토랑 추가 ===");
            System.out.print("레스토랑 이름: ");
            String restaurantName = scanner.nextLine();
            System.out.print("카테고리 ID: ");
            String categoryId = scanner.nextLine();
            System.out.print("주소: ");
            String address = scanner.nextLine();

            String insertRestaurantQuery = "INSERT INTO Restaurant (restaurant_id, seller_id, addr_info, restaurant_name, category_id, open_date) VALUES (?, ?, ?, ?, ?, CURRENT_DATE)";
            PreparedStatement restaurantStmt = connection.prepareStatement(insertRestaurantQuery);
            restaurantStmt.setString(1, UUID.randomUUID().toString().substring(0, 10));
            restaurantStmt.setString(2, userId);
            restaurantStmt.setString(3, address);
            restaurantStmt.setString(4, restaurantName);
            restaurantStmt.setString(5, categoryId);
            restaurantStmt.executeUpdate();

            System.out.println("레스토랑이 성공적으로 추가되었습니다!");
        } catch (SQLException e) {
            System.err.println("레스토랑 추가 실패: " + e.getMessage());
        }
    }


    private static void addMenuItem(Connection connection, Scanner scanner, String userId) {
        try {
            System.out.println("=== 메뉴 추가 ===");
            System.out.print("레스토랑 ID: ");
            String restaurantId = scanner.nextLine();
            System.out.print("메뉴 이름: ");
            String productName = scanner.nextLine();
            System.out.print("메뉴 설명: ");
            String productDetail = scanner.nextLine();
            System.out.print("가격: ");
            int price = scanner.nextInt();
            scanner.nextLine();

            String insertProductQuery = "INSERT INTO Product (product_id, product_name, product_detail, restaurant_id, price) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement productStmt = connection.prepareStatement(insertProductQuery);
            productStmt.setString(1, UUID.randomUUID().toString().substring(0, 10));
            productStmt.setString(2, productName);
            productStmt.setString(3, productDetail);
            productStmt.setString(4, restaurantId);
            productStmt.setInt(5, price);
            productStmt.executeUpdate();

            System.out.println("메뉴가 성공적으로 추가되었습니다!");
        } catch (SQLException e) {
            System.err.println("메뉴 추가 실패: " + e.getMessage());
        }
    }

    private static void managePromotion(Connection connection, Scanner scanner, String userId) {
        try {
            System.out.println("=== 프로모션 관리 ===");
            System.out.print("레스토랑 ID: ");
            String restaurantId = scanner.nextLine();
            System.out.print("프로모션 방식: ");
            String method = scanner.nextLine();
            System.out.print("시작 날짜(YYYY-MM-DD): ");
            String startDate = scanner.nextLine();
            System.out.print("종료 날짜(YYYY-MM-DD): ");
            String endDate = scanner.nextLine();

            String insertPromotionQuery = "INSERT INTO Promotion (promotion_id, restaurant_id, promotion_method, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement promotionStmt = connection.prepareStatement(insertPromotionQuery);
            promotionStmt.setString(1, UUID.randomUUID().toString().substring(0, 10));
            promotionStmt.setString(2, restaurantId);
            promotionStmt.setString(3, method);
            promotionStmt.setString(4, startDate);
            promotionStmt.setString(5, endDate);
            promotionStmt.executeUpdate();

            System.out.println("프로모션이 성공적으로 추가되었습니다!");
        } catch (SQLException e) {
            System.err.println("프로모션 관리 실패: " + e.getMessage());
        }
    }

    private static void respondToReview(Connection connection, Scanner scanner, String userId) {
        try {
            System.out.println("=== 리뷰 답변 작성 ===");
            System.out.print("답변할 리뷰 ID: ");
            String reviewId = scanner.nextLine();
            System.out.print("답변 내용: ");
            String content = scanner.nextLine();

            String insertReplyQuery = "INSERT INTO Review_Reply (review_id, seller_id, content) VALUES (?, ?, ?)";
            PreparedStatement replyStmt = connection.prepareStatement(insertReplyQuery);
            replyStmt.setString(1, reviewId);
            replyStmt.setString(2, userId);
            replyStmt.setString(3, content);
            replyStmt.executeUpdate();

            System.out.println("리뷰 답변이 성공적으로 추가되었습니다!");
        } catch (SQLException e) {
            System.err.println("리뷰 답변 작성 실패: " + e.getMessage());
        }
    }

    private static void viewAvailableDeliveries(Connection connection) {
        try {
            System.out.println("=== 배달 가능한 주문 ===");
            String query = "SELECT o.order_id, o.customer_id, o.restaurant_id FROM `Order` o WHERE o.status_id = 'Pending'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                System.out.println("주문 ID: " + rs.getString("order_id"));
                System.out.println("고객 ID: " + rs.getString("customer_id"));
                System.out.println("레스토랑 ID: " + rs.getString("restaurant_id"));
                System.out.println("-------------------------");
            }
        } catch (SQLException e) {
            System.err.println("배달 가능한 주문 조회 실패: " + e.getMessage());
        }
    }

    private static void updateDeliveryStatus(Connection connection, Scanner scanner, String userId) {
        try {
            System.out.println("=== 배달 상태 변경 ===");
            System.out.print("변경할 배달 ID: ");
            String deliveryId = scanner.nextLine();
            System.out.print("새로운 배달 상태(예: In Progress, Completed): ");
            String status = scanner.nextLine();

            String updateDeliveryQuery = "UPDATE Delivery SET delivery_status = ? WHERE delivery_id = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateDeliveryQuery);
            updateStmt.setString(1, status);
            updateStmt.setString(2, deliveryId);
            updateStmt.executeUpdate();

            System.out.println("배달 상태가 성공적으로 변경되었습니다!");
        } catch (SQLException e) {
            System.err.println("배달 상태 변경 실패: " + e.getMessage());
        }
    }
}