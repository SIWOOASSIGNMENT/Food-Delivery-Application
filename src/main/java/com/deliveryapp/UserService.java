package com.deliveryapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {
    private final Connection connection;

    public UserService(Connection connection) {
        this.connection = connection;
    }

    // 회원가입
    public boolean registerUser(String userId, String password, String userType) {
        // 유효한 userType 값 확인
        if (!userType.equals("C") && !userType.equals("D") && !userType.equals("R")) {
            System.out.println("잘못된 계정 유형입니다. 'C', 'D', 'R' 중에서 선택하세요.");
            return false;
        }

        // 비밀번호를 SHA-256으로 해싱
        String hashedPassword = HashUtil.hashWithSHA256(password);

        String query = "INSERT INTO User (user_id, user_type_id, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.setString(2, userType);
            stmt.setString(3, hashedPassword);
            stmt.executeUpdate();
            System.out.println("회원가입이 완료되었습니다.");
            return true;
        } catch (SQLException e) {
            System.err.println("회원가입 실패: " + e.getMessage());
            return false;
        }
    }

    // 로그인
    public String loginUser(String userId, String password) {
        // 입력된 비밀번호를 해싱
        String hashedPassword = HashUtil.hashWithSHA256(password);

        String query = "SELECT user_type_id FROM User WHERE user_id = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.setString(2, hashedPassword);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("user_type_id");
                } else {
                    System.out.println("로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.");
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("로그인 실패: " + e.getMessage());
            return null;
        }
    }
}
