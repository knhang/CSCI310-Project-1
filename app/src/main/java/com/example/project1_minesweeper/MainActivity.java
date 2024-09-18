package com.example.project1_minesweeper;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.GridLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final int COLUMN_COUNT = 10;
    private static final int ROW_COUNT = 12;
    private GameActivity gameActivity;

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs; // UI array
    private int[][] gridState; // Handles all the logical, backend 2D array

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameActivity = new GameActivity();
        cell_tvs = new ArrayList<TextView>();
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
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n / COLUMN_COUNT;
        int j = n % COLUMN_COUNT;

        // Display bomb
        if (gameActivity.currMode()){ // If the current icon/mode is set to the pickaxe
            if (gridState[i][j] == -1){
                tv.setText(R.string.mine);
                // Handle game over and result activity page
            }
            else if (gridState[i][j] > 0){ // If its just a normal grid with bombs nearby
                tv.setText(String.valueOf(gridState[i][j]));
                tv.setBackgroundColor(Color.LTGRAY);
                tv.setTextColor(Color.BLACK);
            }
            else if (gridState[i][j] == 0){
                // Display adjacent cells!
                tv.setText("");
                tv.setBackgroundColor(Color.LTGRAY);
                displayAdjCells(i, j);
                // Hello.
            }
        }
        else{ // The current icon is a flag and we are flagging where the bomb could be
            tv.setText(R.string.flag);
            gameActivity.updateFlagCount(-1);
        }

    }

    // Displays all cells around a cell that has 0 mines nearby
    private void displayAdjCells(int row, int col){
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

        for (int[] dir : directions){
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (newRow >= 0 && newRow < ROW_COUNT && newCol >= 0 && newCol < COLUMN_COUNT) {
                // check if the grid im trying to reveal hasn't been revealed alr
                int n = newRow * COLUMN_COUNT + newCol;
                TextView tv = cell_tvs.get(n);
                if (tv.getCurrentTextColor() == Color.LTGRAY){ // It was alr revealed, don't overwrite
                    continue;
                }
                if (gridState[newRow][newCol] > 0){
                    tv.setText(String.valueOf(gridState[newRow][newCol]));
                    tv.setTextColor(Color.BLACK);
                    tv.setBackgroundColor(Color.LTGRAY);
                }
                else{
                    tv.setText("");
                    tv.setBackgroundColor(Color.LTGRAY);
                }

            }
        }
    }


}