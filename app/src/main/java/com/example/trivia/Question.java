package com.example.trivia;

public class Question {

    private String question;
    private boolean answer;

    public Question(String q, boolean b) {
        question = q;
        answer = b;
    }

    public String getQuestion() {
        return question;
    }

    public boolean getAnswer() {
        return answer;
    }

    @Override
    public String toString() {
        return question + "\t" + answer;
    }
}
