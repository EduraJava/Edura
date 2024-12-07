
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


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
    
    public boolean saveUser(String name, String birth) {
        try {
            dbOpen(); 

            // SQL 쿼리 작성
            String insertQuery = "INSERT INTO user (name, birth) VALUES (?, ?)";
            PreparedStatement pstmt = DB_con.prepareStatement(insertQuery);
            pstmt.setString(1, name); 
            pstmt.setString(2, birth);
            int rowsAffected = pstmt.executeUpdate();

            pstmt.close();
            dbClose(); 

            return rowsAffected > 0; 
        } catch (SQLException e) {
            System.out.println("사용자 등록 중 SQLException 발생");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("사용자 등록 중 기타 예외 발생");
            e.printStackTrace();
        }

        return false; 
    }
    
    public boolean deleteUser(String name, String birth) throws IOException {
        try {
            dbOpen();
            String query = "DELETE FROM user WHERE name = ? AND birth = ?";
            PreparedStatement pstmt = DB_con.prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, birth);

            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("사용자 삭제 중 오류 발생: " + e.getMessage());
        } finally {
            dbClose();
        }
        return false;
    }

    
    // 유저 카테고리를 찾아 이수 시간을 보여주는 메서드 
    public List<String> getUserCategoryInfo(int userId) throws IOException {
        List<String> categoryInfo = new ArrayList<>();

        try {
            dbOpen(); // 데이터베이스 연결
            String query = "SELECT c.title, uc.ptime FROM User_Category uc " +
                           "JOIN Category c ON uc.category_id = c.id " +
                           "WHERE uc.user_id = ?";
            PreparedStatement pstmt = DB_con.prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String category = rs.getString("title");
                int ptime = rs.getInt("ptime");
                if (ptime >= 120) {
                    categoryInfo.add(category + ": " + ptime + "분 이수     이수 완료");
                } else {
                    categoryInfo.add(category + ": " + ptime + "분 이수     이수중");
                }
            }

            rs.close();
            pstmt.close();
            dbClose(); // 데이터베이스 연결 닫기
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("이수 정보 로드 중 오류 발생: " + e.getMessage());
        }

        return categoryInfo;
    }
    
    public boolean isButtonClickableToday(int userId) throws IOException {
        boolean clickable = true; // 기본적으로 클릭 가능

        try {
            dbOpen(); // DB 연결
            String query = "SELECT last_click_date FROM User_ButtonLog WHERE user_id = ?";
            PreparedStatement pstmt = DB_con.prepareStatement(query);
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                LocalDate lastClickDate = rs.getDate("last_click_date").toLocalDate();
                // 오늘 날짜 가져오기
                LocalDate today = LocalDate.now();


                // 마지막 클릭 날짜가 오늘이면 클릭 불가
                if (lastClickDate.equals(today)) {
                    return false; // 클릭 불가, 바로 반환
                }
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("날짜 확인 중 오류 발생: " + e.getMessage());
        } finally {
            dbClose(); // DB 연결 닫기
        }

        return clickable; // 결과 반환
    }

    public void updateButtonClickDate(int userId) throws IOException {
        try {
            dbOpen(); // DB 연결
            String query = "INSERT INTO User_ButtonLog (user_id, last_click_date) VALUES (?, ?) " +
                           "ON DUPLICATE KEY UPDATE last_click_date = ?";
            PreparedStatement pstmt = DB_con.prepareStatement(query);
            Date today = new Date(System.currentTimeMillis());

            pstmt.setInt(1, userId);
            pstmt.setDate(2, today);
            pstmt.setDate(3, today);

            pstmt.executeUpdate();
            pstmt.close();
            dbClose(); // DB 연결 닫기
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("날짜 업데이트 중 오류 발생: " + e.getMessage());
        }
    }
}
