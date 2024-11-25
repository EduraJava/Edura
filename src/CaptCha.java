
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


    public CaptCha(JLabel lblImg) {
        // JFrame 생성
        JFrame frame = new JFrame("Captcha Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        // JLabel 초기화
        lblImg = new JLabel();
        lblImg.setHorizontalAlignment(SwingConstants.CENTER);
        lblImg.setVerticalAlignment(SwingConstants.CENTER);

        // JPanel에 추가
        JPanel panel = new JPanel();
        panel.add(lblImg);

        frame.add(panel);

        // 캡차 생성 및 표시
        resetCaptcha(lblImg);

        frame.setVisible(true);
    }

    public void resetCaptcha(JLabel lblImg) {
        // 랜덤 텍스트 생성
        String captchaText = generateRandomText(6);

        // 텍스트를 이미지로 변환
        BufferedImage captchaImage = generateCaptchaImage(captchaText);

        // JLabel에 이미지 설정
        lblImg.setIcon(new ImageIcon(captchaImage));
    }

    private String generateRandomText(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder text = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            text.append(chars.charAt(random.nextInt(chars.length())));
        }

        return text.toString();
    }

    private BufferedImage generateCaptchaImage(String text) {
        int width = 200;
        int height = 50;

        // BufferedImage 생성
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 배경 설정
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // 폰트 설정
        g2d.setFont(new Font("Arial", Font.BOLD, 30));

        // 랜덤 색상 및 텍스트 그리기
        Random random = new Random();
        for (int i = 0; i < text.length(); i++) {
            g2d.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            int x = 20 + i * 30;
            int y = 30 + random.nextInt(10);
            g2d.drawString(String.valueOf(text.charAt(i)), x, y);
        }

        // 노이즈 추가
        for (int i = 0; i < 20; i++) {
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

}
