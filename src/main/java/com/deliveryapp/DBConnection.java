package com.deliveryapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/* URL, USER, PASSWORD 를 본인 설정에 맞게 수정하세요 !-! :) */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "본인 비밀번호";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("데이터베이스 연결 성공");
        } catch (ClassNotFoundException e) {
            System.err.println("드라이버를 로드할 수 없습니다: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("데이터베이스 연결 실패: " + e.getMessage());
        }
        return connection;
    }
}
