package com.vvv.shootinggame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RankActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_rank);

        List<Integer> scores = getSavedScores();

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.list_item_score, R.id.scoreTextView, scores);

        ListView scoreListView = findViewById(R.id.scoreListView);
        scoreListView.setAdapter(adapter);

        ImageView back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(v -> {
            finish();
        });
    }

    private List<Integer> getSavedScores() {
        List<Integer> scores = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String scoresString = preferences.getString("SCORES", "");

        if (!scoresString.isEmpty()) {
            String[] scoreArray = scoresString.split(",");
            for (String scoreStr : scoreArray) {
                try {
                    int score = Integer.parseInt(scoreStr);
                    scores.add(score);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        return scores;
    }
}
