/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package domain;

/**
 *
 * @author jun
 */
public class Question {
    private int id;
    private String quest;
    private String q1, q2, q3, q4;
    private int correct_answer;
    private String explain;

    public Question(int id, String quest, String q1, String q2, String q3, String q4, int correct_answer, String explain) {
        this.id = id;
        this.quest = quest;
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
        this.q4 = q4;
        this.correct_answer = correct_answer;
        this.explain = explain;
    }
    
    public String getExplain() {
        return explain;
    }

    public int getId() {
        return id;
    }

    public String getQuest() {
        return quest;
    }

    public String getQ1() {
        return q1;
    }

    public String getQ2() {
        return q2;
    }

    public String getQ3() {
        return q3;
    }

    public String getQ4() {
        return q4;
    }
    
    public int getCorrect_answer() {
        return correct_answer;
    }
}
