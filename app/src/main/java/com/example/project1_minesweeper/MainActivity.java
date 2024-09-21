package com.example.project1_minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.widget.GridLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private static final int COLUMN_COUNT = 10;
    private static final int ROW_COUNT = 12;

    private ArrayList<TextView> cell_tvs; // UI array
    private int[][] gridState; // Handles all the logical, backend 2D array
    private GameActivity gameActivity;

    private int flagsPlaced = 0;
    private boolean gameStarted = false;
    private boolean gameFinished = false;
    private boolean gameResult = true;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView flagCounterText = findViewById(R.id.flagCount);
        TextView timerText = findViewById(R.id.timer);
        TextView toggleButton = findViewById(R.id.toggleIcons);
        gameActivity = new GameActivity(flagCounterText, timerText, toggleButton);

        cell_tvs = new ArrayList<>();
        gridState = new int[ROW_COUNT][COLUMN_COUNT];

        GridLayout grid = findViewById(R.id.gridLayout01);
        grid.setColumnCount(COLUMN_COUNT);
        grid.setRowCount(ROW_COUNT);
        for (int i = 0; i<ROW_COUNT; i++) {
            for (int j= 0; j< COLUMN_COUNT; j++) {
                TextView tv = new TextView(this);
                tv.setHeight( dpToPixel(32) );
                tv.setWidth( dpToPixel(32) );
                tv.setTextSize( 16 );//dpToPixel(32) );
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.parseColor("lime"));
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(2), dpToPixel(2), dpToPixel(2), dpToPixel(2));
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
            }
        }

        placeMines();
    }

    // Keep this
    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    // Place 4 random mines when we create the grid and keep track in gridState
    private void placeMines(){
        Random random = new Random();
        for (int i = 0; i < 4; i++){
            int row = random.nextInt(ROW_COUNT);
            int col = random.nextInt(COLUMN_COUNT);
            if (gridState[row][col] != -1){
                gridState[row][col] = -1; // -1 indicates there's a bomb
            }
        }

        // Go through gridState
        for (int i = 0; i < ROW_COUNT; i++) {
            for (int j = 0; j < COLUMN_COUNT; j++) {
                // Skip mines
                if (gridState[i][j] == -1) {
                    continue;
                }

                // Update this cell w/ how many adjacent mines there are
                gridState[i][j] = countMines(i, j);
            }
        }
    }

    // Finds all mines around a cell
    private int countMines(int row, int col){
        int[][] directions = {
                {-1, 0},   // up
                {1, 0},    // down
                {0, -1},   // left
                {0, 1},    // right
                {-1, -1},  // up-left diagonal
                {-1, 1},   // up-right diagonal
                {1, -1},   // down-left diagonal
                {1, 1}     // down-right diagonal
        };
        int mines = 0;

        for (int[] dir : directions){
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (newRow >= 0 && newRow < ROW_COUNT && newCol >= 0 && newCol < COLUMN_COUNT){
                if (gridState[newRow][newCol] == -1){
                    mines++;
                }
            }
        }

        return mines;
    }

    // Function that displays the bomb, or # of adj bombs nearby. Uses a listener
    public void onClickTV(View view){
        if (gameFinished){
            // check if the grids are the same...
            showResult(gameResult);
            return;
        }
        if (!gameStarted){
            gameActivity.startTimer();
            gameStarted = true;
            return;
        }

        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n / COLUMN_COUNT;
        int j = n % COLUMN_COUNT;

        // Display bomb
        if (gameActivity.currMode()){ // If the current icon/mode is set to the pickaxe
            if (gridState[i][j] == -1){
                tv.setText(R.string.mine);
                gameActivity.stopTimer();
                gameFinished = true;
                gameResult = false;
                return;
            }
            else {
                displayAdjCells(i, j);
            }

            Boolean gameStatus = checkWinCondition();
            if (gameStatus){
                showResult(true);
            }
        }
        else { // The current icon is a flag and we are flagging where the bomb could be
            if (tv.getCurrentTextColor() == Color.LTGRAY) {
                return;
            }

            // If the cell already has a flag, allow removing the flag
            if (tv.getText().toString().equals(getString(R.string.flag))) {
                tv.setText("");
                gameActivity.updateFlagCount(1); // Increment the flag count
                flagsPlaced--; // Decrease the flagsPlaced count
            }
            // Ensure there are fewer than 4 flags placed, and the cell doesn't already have a flag
            else if (flagsPlaced < 4 && !tv.getText().toString().equals(getString(R.string.flag))) {
                flagsPlaced++;
                tv.setText(R.string.flag);
                gameActivity.updateFlagCount(-1);
            }
        }
    }

    // Displays all cells around a cell that has 0 mines nearby
    private void displayAdjCells(int row, int col){
        int n = row * COLUMN_COUNT + col;
        TextView tv = cell_tvs.get(n);
        if (tv.getBackground() instanceof ColorDrawable && ((ColorDrawable) tv.getBackground()).getColor() == Color.LTGRAY) {
            return;
        }

        int adjacentMines = countMines(row, col);
        tv.setText(String.valueOf(adjacentMines));
        tv.setBackgroundColor(Color.LTGRAY);

        // Only reveal adjacent cells if there are no adjacent mines
        if (adjacentMines == 0) {
            revealNeighbors(row, col);
        }
    }

    private void revealNeighbors(int row, int col) {
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        // Searches all 8 adjacent cells
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (newRow >= 0 && newRow < ROW_COUNT && newCol >= 0 && newCol < COLUMN_COUNT) {
                int n = newRow * COLUMN_COUNT + newCol;
                TextView neighbor = cell_tvs.get(n);
                if (gridState[newRow][newCol] != -1 && neighbor.getText().toString().isEmpty()) {
                    displayAdjCells(newRow, newCol);
                }
            }
        }
    }

    private void showResult(boolean isWin) {
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.putExtra("isWin", isWin);
        intent.putExtra("timeTaken", gameActivity.getTotalTime()); // Pass time used
        startActivity(intent);
        finish(); // Close MainActivity
    }

    private Boolean checkWinCondition() {
        int currRevealed = 0;
        for (TextView tv : cell_tvs){
            if (tv.getSolidColor() == Color.LTGRAY){
                currRevealed++;
            }
        }

        return currRevealed == 116;
    }

}