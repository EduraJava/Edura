/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain;

/**
 *
 * @author jun
 */
public class UserSession {
    private static String name; // 사용자 이름
    private static String birth; // 생년월일
    private static int user_id;

    public static void setUser(int id, String userName, String userBirth) {
        user_id = id;
        name = userName;
        birth = userBirth;
    }
    

    public static String getName() {
        return name;
    }

    public static String getBirth() {
        return birth;
    }
    public static int getUser_id() {
        return user_id;
    }

    public static void clear() {
        user_id = -1;
        name = null;
        birth = null;
    }
}