package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.example.trivia.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Repository repository = new Repository();
    private EventHandler handler = new EventHandler();
    private Score score = new Score();

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private List<Question> questions;
    private List<Integer> answeredIndexes = new LinkedList<Integer>();
    private String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";
    private int index = 0;

    private TextView questionTxt;
    private TextView questionCount;
    private TextView scoreTxt;
    private TextView highscoreTxt;
    private TextView questionStatusTxt;
    private Button nextBtn;
    private Button trueBtn;
    private Button falseBtn;
    private Button prevBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        preferences = getSharedPreferences("Main", MODE_PRIVATE);
        editor = preferences.edit();
        initViews();

        repository.getQuestions(MainActivity.this, url, new Requests() {
            @Override
            public void requestDone(List<Question> list) {
                questions = list;
                highscoreTxt.setText("Highscore: " + preferences.getInt("highscore",0));
                setText();
                setButtons();
            }
        });
    }

    protected void onPause() {
        super.onPause();
        setHighscore();
    }

    private void setHighscore() {
        if (score.getScore()>getHighScore()) {
            editor.putInt("highscore", score.getScore());
            editor.apply();
        }
    }

    private void setText() {
        questionTxt.setText(questions.get(index).getQuestion());
        questionCount.setText(String.format("Question: %d/%d", index+1, questions.size()-1));
        scoreTxt.setText("Score: " + String.valueOf(score.getScore()));
    }

    private void prevBtn() {
        index += index!=0 ? -1 : questions.size()-2;
        setText();
        setButtons();
    }

    private void nextBtn() {
        index += index!=questions.size()-2 ? 1 : -index;
        setText();
        blinkAnimation();
        setButtons();
    }

    private void checkAnswer(boolean userAnswer, boolean correctAnswer) {
        String s;
        if (userAnswer==correctAnswer) {
            s = "Correct! :)";
            score.addScore(20);
        }else {
            s = "Incorrect! :(";
            if (score.getScore()>=20) {
                score.addScore(-20);
            }
        }
        answeredIndexes.add(index);
        setText();
        setButtons();
        Snackbar.make(trueBtn, s, Snackbar.LENGTH_SHORT).show();
    }

    private void setButtons() {
        boolean enable = true;
        for (int i = 0; i < answeredIndexes.size(); i++) {
            if (answeredIndexes.get(i)==index) {
                enable = false;
                break;
            }
        }
        trueBtn.setEnabled(enable);
        falseBtn.setEnabled(enable);
        questionStatusTxt.setText(enable ? "Unanswered!" : "Answered!");
    }

    private void blinkAnimation() {
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
        questionTxt.setAnimation(animation);
    }

    private void initViews() {
        questionTxt = binding.textView4;
        questionCount = binding.textView;
        trueBtn = binding.button;
        trueBtn.setOnClickListener(handler);
        falseBtn = binding.button2;
        falseBtn.setOnClickListener(handler);
        nextBtn = binding.button3;
        nextBtn.setOnClickListener(handler);
        prevBtn = binding.button4;
        prevBtn.setOnClickListener(handler);
        scoreTxt = binding.textView3;
        highscoreTxt = binding.textView5;
        questionStatusTxt = binding.textView6;
    }

    public int getHighScore() {
        return preferences.getInt("highscore", 0);
    }

    private class EventHandler implements View.OnClickListener {
        public void onClick(View v) {
            if (v==trueBtn) {
                checkAnswer(true, questions.get(index).getAnswer());
            }
            if (v==falseBtn) {
                checkAnswer(false, questions.get(index).getAnswer());
            }
            if (v==nextBtn) {
                nextBtn();
            }
            if (v==prevBtn)
                prevBtn();
        }
    }
}