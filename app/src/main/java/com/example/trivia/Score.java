package com.example.trivia;

import android.content.SharedPreferences;

public class Score {
    private int score;

    public void addScore(int c) {
        score += c;
    }

    public int getScore() {
        return score;
    }
}
