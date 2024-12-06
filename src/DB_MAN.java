
import java.io.IOException;
import java.sql.*;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author jun
 */
public class DB_MAN {
    // MySQL Driver 설정
    String strDriver = "com.mysql.cj.jdbc.Driver";  // MySQL JDBC Driver
    String strURL = "jdbc:mysql://localhost:3306/Edura?serverTimezone=Asia/Seoul&characterEncoding=UTF-8";
//    String strURL = "jdbc:mysql://10.70.41.80:3306/mydb?characterEncoding=UTF-8&serverTimezone=UTC";
    String user = "root";  // MySQL 사용자 이름
    String password = "";  // MySQL 사용자 비밀번호
    
    Connection DB_con;  // DB Connection 객체
    Statement DB_stmt;  // Statement 객체
    ResultSet DB_rs;    // SQL 실행 결과를 저장할 ResultSet 객체
    
    public void dbOpen() throws IOException {
        try {
            Class.forName(strDriver);
            
            DB_con = DriverManager.getConnection(strURL, user, password);
            DB_stmt = DB_con.createStatement();
        } catch(Exception e) {
            System.out.println("SQLException : " + e.getMessage());
        }
    }
    
    public void dbClose() throws IOException {
        try {
            DB_stmt.close();
            DB_con.close();
        } catch(Exception e) {
            System.out.println("SQLException : " + e.getMessage());
        }
    }

    
    // id를 찾는 메서드
    public int findUserId(String name, String birth) {
    int userId = -1; // 기본값으로 -1을 설정 (찾지 못한 경우)
    try {
        dbOpen(); // 데이터베이스 연결 열기

        // SQL 쿼리 작성
        String query = "SELECT id FROM user WHERE name = ? AND birth = ?";
        PreparedStatement pstmt = DB_con.prepareStatement(query);
        pstmt.setString(1, name); // 첫 번째 파라미터에 이름 설정
        pstmt.setString(2, birth); // 두 번째 파라미터에 생년월일 설정

        // 쿼리 실행 및 결과 처리
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            userId = rs.getInt("id"); // id 값 가져오기
        }

        rs.close();
        pstmt.close();
    } catch (SQLException e) {
        System.out.println("사용자 ID 찾기 중 SQLException 발생");
        e.printStackTrace();
    } catch (Exception e) {
        System.out.println("사용자 ID 찾기 중 기타 예외 발생");
        e.printStackTrace();
    } finally {
        try {
            dbClose(); // 데이터베이스 연결 닫기
        } catch (Exception e) {
            System.out.println("DB 연결 종료 중 오류 발생");
            e.printStackTrace();
        }
    }
    return userId; // 사용자 ID 반환
}
}
