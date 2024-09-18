package com.example.project1_minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.widget.GridLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;


public class GameActivity extends AppCompatActivity{
    private boolean running = false; // If the timer is running, should only be switched to true when lose/win
    private boolean isPickaxe = true; // true = pickaxe, false = flag
    private int flagCount = 4;
    private TextView flagCounterText;
    private TextView timerText;
    private TextView toggleButton;
    private int clock = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            clock = savedInstanceState.getInt("clock");
            running = savedInstanceState.getBoolean("running");
        }

        flagCounterText = findViewById(R.id.flagCount);
        timerText = findViewById(R.id.timer);
        toggleButton = findViewById(R.id.toggleIcons);

        flagCounterText.setText(String.valueOf(flagCount));

        runTimer();
        toggleButton.setOnClickListener(v -> togglePickaxeFlag());

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("clock", clock);
        savedInstanceState.putBoolean("running", running);
    }

    private void runTimer() {
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours =clock/3600;
                int minutes = (clock%3600) / 60;
                int seconds = clock%60;
                String time = String.format("%d:%02d:%02d", hours, minutes, seconds);
                timerText.setText(time);

                if (running) {
                    clock++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    public void updateFlagCount(int count) {
        flagCount += count;
        flagCounterText.setText(String.valueOf(flagCount));
    }

    private void togglePickaxeFlag() {
        isPickaxe = !isPickaxe; // Toggle between pickaxe and flag

        if (isPickaxe) {
            toggleButton.setText(R.string.pick); // Pickaxe icon
        }
        else {
            toggleButton.setText(R.string.flag); // Flag icon
        }
    }

    public boolean currMode(){ // Sends back what mode the toggle icon is on right now
        return isPickaxe;
    }


}
