
import domain.Question;
import domain.UserSession;
import java.io.IOException;
import javax.swing.ButtonGroup;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author jun
 */
public class QuizFrame extends javax.swing.JFrame {
    private DB_MAN db = new DB_MAN(); // DB 연결 객체
    private String dayCategory; // 현재 요일에 따른 카테고리
    private int dayId; // 현재 카테고리 ID
    private int currentQuestionIndex = 0; // 현재 문제 인덱스
    List<Question> questions = new ArrayList<>();// 질문을 담을 리스트
    

    


    /**
     * Creates new form QuizFrame
     */
    public QuizFrame() throws IOException {
        
        initComponents();
        setCategoryByDay(); // 요일 보고 카테고리 적어두기. 
        loadQuestion(); // 질문 데이터 불러오기
        displayQuestion(); // 첫 번째 문제 표시
    }
    
    
    
    private void setCategoryByDay() {
        String[] days = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
        String[] categories = {"기타", "성폭력 예방 교육", "장애인 인식 개선 교육", 
                               "성폭력 예방 교육", "산업 재해 예방 교육", 
                               "개인 정보 보호 교육", "기타"};

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int dayIndex = calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1;
        dayCategory = categories[dayIndex];

        if (days[dayIndex].equals("월요일") || days[dayIndex].equals("수요일")) { 
            dayId = 1;
        } else if (days[dayIndex].equals("화요일")) { 
            dayId = 2;
        } else if (days[dayIndex].equals("목요일")) { 
            dayId = 3;
        } else { 
            dayId = 4;
        }
        // jLabel1에 카테고리 표시
        catelbl.setText("카테고리 : " + dayCategory);
    }
    
    private void displayQuestion() throws IOException {
        // 현재 문제 인덱스가 유효한지 확인
        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            qlbl.setText("문제 : " + currentQuestion.getQuest());
            q1.setText(currentQuestion.getQ1());
            q2.setText(currentQuestion.getQ2());
            q3.setText(currentQuestion.getQ3());
            q4.setText(currentQuestion.getQ4());
            homeButton.setVisible(false);
        } else {
            // 더 이상 문제가 없으면 종료 메시지 표시
            qlbl.setText("문제를 모두 풀었습니다!");
            q1.setVisible(false);
            q2.setVisible(false);
            q3.setVisible(false);
            q4.setVisible(false);
            homeButton.setVisible(true);
            submitbutton.setVisible(false);
            
            addCategoryScore(UserSession.getUser_id(), dayId); // userId와 dayId 전달
        }
    }

    
    private void loadQuestion() throws IOException {
        try {
            db.dbOpen(); // 데이터베이스 연결
            String query = "SELECT q.id AS question_id, q.quest, q.q1, q.q2, q.q3, q.q4, q.correct_answer, q.explains "
                             + "FROM Question q "
                             + "WHERE q.category_id = ? "
                             + "ORDER BY RAND() LIMIT 5"; // 랜덤으로 5개 선택
            PreparedStatement pstmt = db.DB_con.prepareStatement(query);
            pstmt.setInt(1, dayId); // 카테고리를 기준으로 데이터 필터링
            ResultSet rs = pstmt.executeQuery();



            // ResultSet에서 데이터를 리스트에 저장
            while (rs.next()) {
                Question question = new Question(
                    rs.getInt("question_id"),
                    rs.getString("quest"),
                    rs.getString("q1"),
                    rs.getString("q2"),
                    rs.getString("q3"),
                    rs.getString("q4"),
                    rs.getInt("correct_answer"),
                    rs.getString("explains")

                );
                questions.add(question);
            }


            rs.close();
            pstmt.close();
            db.dbClose(); // 데이터베이스 연결 닫기
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("질문 로드 중 오류 발생: " + e.getMessage());
        }

    }   
    
    private void addCategoryScore(int userId, int categoryId) throws IOException {
        try {
            db.dbOpen(); // 데이터베이스 연결

            // 사용자 존재 여부 확인
            String userCheckQuery = "SELECT COUNT(*) FROM User WHERE id = ?";
            PreparedStatement userCheckStmt = db.DB_con.prepareStatement(userCheckQuery);
            userCheckStmt.setInt(1, userId);
            ResultSet userCheckRs = userCheckStmt.executeQuery();

            if (userCheckRs.next() && userCheckRs.getInt(1) == 0) {
                throw new SQLException("User ID가 User 테이블에 존재하지 않습니다.");
            }
            userCheckRs.close();
            userCheckStmt.close();

            // 카테고리 점수 확인
            String checkQuery = "SELECT COUNT(*) FROM User_Category WHERE user_id = ? AND category_id = ?";
            PreparedStatement checkStmt = db.DB_con.prepareStatement(checkQuery);
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, categoryId);
            ResultSet rs = checkStmt.executeQuery();

            boolean categoryExists = false;
            if (rs.next()) {
                categoryExists = rs.getInt(1) > 0;
            }
            rs.close();
            checkStmt.close();

            if (categoryExists) {
                // 카테고리가 존재하면 점수 업데이트
                String updateQuery = "UPDATE User_Category SET ptime = ptime + 50 WHERE user_id = ? AND category_id = ?";
                PreparedStatement updateStmt = db.DB_con.prepareStatement(updateQuery);
                updateStmt.setInt(1, userId);
                updateStmt.setInt(2, categoryId);

                int rowsUpdated = updateStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("점수가 성공적으로 업데이트되었습니다.");
                } else {
                    System.out.println("점수 업데이트 실패: 사용자나 카테고리를 찾을 수 없습니다.");
                }
                updateStmt.close();
            } else {
                // 카테고리가 없으면 새 레코드 삽입
                String insertQuery = "INSERT INTO User_Category (user_id, category_id, ptime) VALUES (?, ?, 50)";
                PreparedStatement insertStmt = db.DB_con.prepareStatement(insertQuery);
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, categoryId);

                int rowsInserted = insertStmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("새 카테고리가 생성되고 점수가 추가되었습니다.");
                } else {
                    System.out.println("카테고리 생성 실패.");
                }
                insertStmt.close();
            }

            db.dbClose(); // 데이터베이스 연결 닫기
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("점수 업데이트 중 오류 발생: " + e.getMessage());
        }
    }
    


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        catelbl = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        qlbl = new javax.swing.JLabel();
        q1 = new javax.swing.JRadioButton();
        q2 = new javax.swing.JRadioButton();
        q3 = new javax.swing.JRadioButton();
        q4 = new javax.swing.JRadioButton();
        submitbutton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        homeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        catelbl.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        catelbl.setForeground(new java.awt.Color(0, 0, 255));
        catelbl.setText("카테고리 :");

        qlbl.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        qlbl.setText("문제 :");

        buttonGroup1.add(q1);
        q1.setText("jRadioButton1");
        q1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                q1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(q2);
        q2.setText("jRadioButton2");

        buttonGroup1.add(q3);
        q3.setText("jRadioButton3");

        buttonGroup1.add(q4);
        q4.setText("jRadioButton4");

        submitbutton.setText("제출");
        submitbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitbuttonActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 51, 0));
        jLabel3.setText("* 패널티 규칙에 따라 정답/오답 표시");

        homeButton.setText("홈으로 가기");
        homeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(submitbutton, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(homeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(q3)
                    .addComponent(q2)
                    .addComponent(q1)
                    .addComponent(qlbl)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                    .addComponent(catelbl)
                    .addComponent(q4)
                    .addComponent(jSeparator2))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(catelbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(qlbl)
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(q1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(q2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(q3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(q4)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(submitbutton)
                    .addComponent(homeButton))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void q1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_q1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_q1ActionPerformed

    private void submitbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitbuttonActionPerformed
        // TODO add your handling code here:
        if (currentQuestionIndex < questions.size()) {
        Question currentQuestion = questions.get(currentQuestionIndex);

        // 선택된 라디오 버튼 확인
        int selectedAnswer = -1;
        if (q1.isSelected()) {
            selectedAnswer = 1;
        } else if (q2.isSelected()) {
            selectedAnswer = 2;
        } else if (q3.isSelected()) {
            selectedAnswer = 3;
        } else if (q4.isSelected()) {
            selectedAnswer = 4;
        }

        // 정답 여부 확인
        boolean isCorrect = (selectedAnswer == currentQuestion.getCorrect_answer());
        String explanation = currentQuestion.getExplain();
        
        // AnswerFrame 열기
        AnswerFrame answerFrame = new AnswerFrame(this, isCorrect, explanation);
        answerFrame.setVisible(true);

        // 현재 QuizFrame 비활성화
        this.setEnabled(false);

    }
    }//GEN-LAST:event_submitbuttonActionPerformed

    private void homeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeButtonActionPerformed
        // TODO add your handling code here:
        HomeJFrame homeFrame = null;
        try {
            homeFrame = new HomeJFrame();
        } catch (IOException ex) {
            Logger.getLogger(QuizFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        homeFrame.setVisible(true);

        // 현재 프레임 닫기
        this.dispose();
    }//GEN-LAST:event_homeButtonActionPerformed
    // 다음 문제로 이동
    public void nextQuestion() throws IOException {
        currentQuestionIndex++;
        displayQuestion();
        this.setEnabled(true); // 프레임 활성화
        
    }
    // 현재 문제 다시 풀기
    public void retryQuestion() throws IOException {
        displayQuestion(); 
        this.setEnabled(true); // 프레임 활성화
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(QuizFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QuizFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QuizFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QuizFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new QuizFrame().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(QuizFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel catelbl;
    private javax.swing.JButton homeButton;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JRadioButton q1;
    private javax.swing.JRadioButton q2;
    private javax.swing.JRadioButton q3;
    private javax.swing.JRadioButton q4;
    private javax.swing.JLabel qlbl;
    private javax.swing.JButton submitbutton;
    // End of variables declaration//GEN-END:variables
}
