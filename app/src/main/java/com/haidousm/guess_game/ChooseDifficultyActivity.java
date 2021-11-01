package com.haidousm.guess_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChooseDifficultyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_difficulty_activity);
    }

    public void easyLevel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("LEVEL", MainActivity.LEVEL.EASY);
        startActivity(intent);


    }

    public void mediumLevel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("LEVEL", MainActivity.LEVEL.MEDIUM);
        startActivity(intent);

    }

    public void hardLevel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("LEVEL", MainActivity.LEVEL.HARD);
        startActivity(intent);

    }
}