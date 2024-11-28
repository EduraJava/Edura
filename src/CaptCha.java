
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author jun
 */
public final class CaptCha {
    private String currentCaptchaText; // 생성된 캡차 텍스트를 저장하는 변수
    
    public void generateCaptcha(JLabel lblImg) {
        
        // 캡차 텍스트 생성
        currentCaptchaText = generateRandomText(6);

        // 캡차 이미지를 생성
        BufferedImage captchaImage = generateCaptchaImage(currentCaptchaText);

        // JLabel에 캡차 이미지 설정
        lblImg.setIcon(new ImageIcon(captchaImage));
       
       
    }

    /**
     * 랜덤한 텍스트를 생성
     */
    private String generateRandomText(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder text = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            text.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return text.toString();
    }

    /**
     * 텍스트를 이미지로 변환
     */
    private BufferedImage generateCaptchaImage(String text) {
        int width = 300;  // 캡차 이미지 가로 크기
        int height = 80;  // 캡차 이미지 세로 크기

        // BufferedImage 생성
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 배경색 설정
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // 폰트 설정
        g2d.setFont(new Font("Arial", Font.BOLD, 30));

        // 텍스트 색상 및 위치 설정
        Random random = new Random();
        for (int i = 0; i < text.length(); i++) {
            g2d.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            int x = 20 + i * 30;
            int y = 30 + random.nextInt(10);
            g2d.drawString(String.valueOf(text.charAt(i)), x, y);
        }

        // 노이즈 추가
        for (int i = 0; i < 15; i++) {
            g2d.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g2d.drawLine(x1, y1, x2, y2);
        }

        g2d.dispose();
        return image;
    }
    public String getCurrentCaptchaText() {
        return currentCaptchaText;
    }
}
