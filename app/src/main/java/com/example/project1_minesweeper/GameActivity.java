package com.example.project1_minesweeper;

import android.os.Handler;
import android.widget.TextView;

public class GameActivity{
    private boolean running = false; // If the timer is running, should only be switched to true when lose/win
    private final Handler handler = new Handler();
    private int clock = 0;
    private Runnable timerRunnable; // Runnable for timer logic

    private boolean isPickaxe = true;// true = pickaxe, false = flag
    private int flagCount = 4;

    private final TextView flagCounterText;
    private final TextView timerText;
    private final TextView toggleButton;

    public GameActivity(TextView flagCounterText, TextView timerText, TextView toggleButton) {
        this.flagCounterText = flagCounterText;
        this.timerText = timerText;
        this.toggleButton = toggleButton;

        // Initialize the flag counter
        flagCounterText.setText(String.valueOf(flagCount));



        // Set up toggle button listener
        toggleButton.setOnClickListener(v -> togglePickaxeFlag());
    }

    public void stopTimer() {
        running = false; // Stop the timer from incrementing
        handler.removeCallbacks(timerRunnable); // Remove the timer callbacks
    }

    public void startTimer(){
        running = true;
        runTimer();
    }

    private void runTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                // Display the time in seconds
                String time = String.format("%02d", clock);
                timerText.setText(time); // Update the TextView

                if (running) {
                    clock++; // Increment the seconds
                }

                // Run again after 1000ms (1 second)
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(timerRunnable); // Start the timer
    }

    public int getTotalTime(){
        return clock;
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
