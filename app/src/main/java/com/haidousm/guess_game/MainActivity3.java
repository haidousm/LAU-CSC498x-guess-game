package com.haidousm.guess_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
    }

    public void easyMode(View view) {
        String ModeString = "EASY";
        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra("ModeString", ModeString);
        startActivity(intent);


    }
    public void mediumMode(View view) {
        String ModeString = "MEDIUM";
        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra("ModeString", ModeString);
        startActivity(intent);

    }
    public void hardMode(View view) {
        String ModeString = "HARD";
        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra("ModeString", ModeString);
        startActivity(intent);

    }
}