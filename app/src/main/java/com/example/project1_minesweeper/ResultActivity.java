package com.example.project1_minesweeper;

import androidx.appcompat.app.AppCompatActivity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Get data from the intent
        Intent intent = getIntent();
        boolean isWin = intent.getBooleanExtra("isWin", false);
        int timeTaken = intent.getIntExtra("timeTaken", 0);

        // Display the result message and time taken
        TextView resultMessage = findViewById(R.id.resultText);
        TextView timeTakenText = findViewById(R.id.timePlayed);

        if (isWin) {
            resultMessage.setText("Congratulations!\nYou Won!");
        } else {
            resultMessage.setText("You Lost!\nTry Again.");
        }

        timeTakenText.setText("Time: " + timeTaken + " seconds");

        // Set up the "New Game" button
        Button restartButton = findViewById(R.id.playAgainButton);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRestartGame(); // Call the method to restart the game
            }
        });
    }

    public void onRestartGame() {
        // Create an intent to send back to MainActivity
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        // Optionally add flags to clear previous activities if needed
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close the ResultActivity
    }

}
