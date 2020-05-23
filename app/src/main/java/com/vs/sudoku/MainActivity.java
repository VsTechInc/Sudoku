package com.vs.sudoku;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Random;

import hotchemi.android.rate.AppRate;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    public int I = -1, J = -1;
    public int UndoI = -1, UndoJ = -1;
    public Drawable background;
    //public int[][] grid = new int[9][9];
    public int[][] array = new int[9][9];

    int[][] grid = new int[][]{
            {1, 2, 3, 4, 5, 6, 7, 8, 9},
            {4, 5, 6, 7, 8, 9, 1, 2, 3},
            {7, 8, 9, 1, 2, 3, 4, 5, 6},

            {2, 3, 1, 5, 6, 4, 8, 9, 7},
            {5, 6, 4, 8, 9, 7, 2, 3, 1},
            {8, 9, 7, 2, 3, 1, 5, 6, 4},

            {3, 1, 2, 6, 4, 5, 9, 7, 8},
            {6, 4, 5, 9, 7, 8, 3, 1, 2},
            {9, 7, 8, 3, 1, 2, 6, 4, 5}
    };
    private Button[][] bord = new Button[9][9];
    private Button[] num = new Button[9];
    private String[] generatedXY = new String[81];
    private int n = 0;

    public static boolean noConflict(int[][] array) {
        for(int row = 0; row < 9; row++)
            for(int col = 0; col < 8; col++)
                for(int col2 = col + 1; col2 < 9; col2++)
                    if(array[row][col]==array[row][col2])
                        return false;

        for(int col = 0; col < 9; col++)
            for(int row = 0; row < 8; row++)
                for(int row2 = row + 1; row2 < 9; row2++)
                    if(array[row][col]==array[row2][col])
                        return false;

        for(int row = 0; row < 9; row += 3)
            for(int col = 0; col < 9; col += 3)
                for(int pos = 0; pos < 8; pos++)
                    for(int pos2 = pos + 1; pos2 < 9; pos2++)
                        if(array[row + pos%3][col + pos/3]==array[row + pos2%3][col + pos2/3])
                            return false;
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppRate.with(this)
                .setInstallDays(3)
                .setLaunchTimes(3)
                .setRemindInterval(5)
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);

        Button btnCheck = findViewById(R.id.btnCheck);
        Button btnClear = findViewById(R.id.btnClear);
        Button btnUndo = findViewById(R.id.btnUndo);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        for (int x = 0; x < 9; x++) {
            int j = x + 1;
            String buttonID = "btn" + j;
            int findID = getResources().getIdentifier(buttonID, "id", getPackageName());
            num[x] = findViewById(findID);
            final int finalI1 = x;
            num[x].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (I >= 0 || J >= 0) {
                        bord[I][J].setText(num[finalI1].getText().toString());
                        bord[I][J].setBackground(background);
                        saveForUndo(I, J);
                        I = -1;
                        J = -1;
                    }
                }
            });
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String buttonID = "btn" + i + j;
                int findID = getResources().getIdentifier(buttonID, "id", getPackageName());
                bord[i][j] = findViewById(findID);
            }
        }

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (I >= 0 && J >= 0 && !bord[I][J].getText().equals("")) {
                    int count = 0;
                    save();
                    int[] a = push(array);
                    if (I >= 0 || J >= 0) {
                        bord[I][J].setBackground(background);
                        Toast.makeText(MainActivity.this, "No Conflict", LENGTH_SHORT).show();
                    }
                    for (int i : a) {
                        if (i != 0 && !bord[I][J].getText().toString().equals("") && i == Integer.parseInt(bord[I][J].getText().toString())) {
                            Log.e("v", i + "");
                            count++;
                            if (count > 2) {
                                Toast.makeText(MainActivity.this, "Conflict", LENGTH_SHORT).show();
                            }
                        }
                    }
                } else if (I == -1 && J == -1) {
                    Toast.makeText(MainActivity.this, "Select a box to check", LENGTH_SHORT).show();
                }
            }
        });
        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UndoI != -1 && UndoJ != -1) {
                    bord[UndoI][UndoJ].setText("");
                }
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (I != -1 && J != -1) {
                    bord[I][J].setText("");
                    bord[I][J].setBackground(background);
                } else if (I == -1 && J == -1) {
                    Toast.makeText(MainActivity.this, "Select a box to clear", LENGTH_SHORT).show();
                }
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer();
            }
        });
        createBoard();
    }

    private int[] push(int[][] s) {
        int[] a = new int[18];
        if (I >= 0 && J >= 0) {
            for (int x = 0; x < 9; x++) {
                for (int y = 0; y < 9; y++) {
                    if (s[x][J] != 0 || s[I][y] != 0) {
                        a[x] = s[x][J];
                        a[9 + y] = s[I][y];
                    }
                }
            }
        }
        return a;
    }

    private void saveForUndo(int i, int j) {
        UndoI = i;
        UndoJ = j;
    }

    private void answer() {
        save();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (noConflict(grid)) {
                    Toast.makeText(MainActivity.this, "Successful", LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Try Again!", LENGTH_SHORT).show();
                }
            }
        }
    }

    private void save() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!bord[i][j].getText().toString().trim().equals("")) {
                    array[i][j] = Integer.parseInt(bord[i][j].getText().toString());
                }
            }
        }
    }

    private void saveBoard() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] != 0) {
                    generatedXY = append(i, j);
                }
            }
        }
    }

    private String[] append(int i, int j) {
        generatedXY[n] = "(" + i + "," + j + ")";
        n++;
        return generatedXY;
    }

    private void createBoard() {
        shuffleNumbers();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] != 0) {
                    if (bord[i][j].getBackground().getConstantState() == getResources().getDrawable(R.drawable.background1).getConstantState()) {
                        bord[i][j].setText(String.valueOf(grid[i][j]));
                        bord[i][j].setBackgroundResource(R.drawable.background3);
                    } else if (bord[i][j].getBackground().getConstantState() == getResources().getDrawable(R.drawable.background2).getConstantState()) {
                        bord[i][j].setText(String.valueOf(grid[i][j]));
                        bord[i][j].setBackgroundResource(R.drawable.background4);
                    }
                } else {
                    bord[i][j].setText("");
                    final int finalI = i;
                    final int finalJ = j;
                    bord[i][j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (I >= 0 || J >= 0) {
                                bord[I][J].setBackground(background);
                            }
                            clicked(finalI, finalJ);
                            background = bord[finalI][finalJ].getBackground();
                            bord[finalI][finalJ].setBackgroundResource(R.drawable.clicked);
                        }
                    });
                }
            }
        }
        saveBoard();
    }

    private void generateBoard() {
        Random random = new Random();
        int row;
        int col;
        int noOfCells;
        Intent intent = getIntent();
        String option = intent.getStringExtra("level");

        assert option != null;
        switch (option) {
            case "easy":
                noOfCells = 40;
                break;
            case "medium":
                noOfCells = 50;
                break;
            case "hard":
                noOfCells = 60;
                break;
            default:
                noOfCells = 10;
                break;
        }

        for (int i = 1; i <= noOfCells; i++) {
            row = random.nextInt(9);
            col = random.nextInt(9);

            if (grid[row][col] != 0) {
                grid[row][col] = 0;
            } else {
                i--;
            }
        }
    }

    void shuffleNumbers() {
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            int ranNum = random.nextInt(9);
            swapNumbers(i, ranNum);
        }
        generateBoard();
    }

    private void swapNumbers(int n1, int n2) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (grid[x][y] == n1)
                    grid[x][y] = 0;
                if (grid[x][y] == n2)
                    grid[x][y] = n1;
            }
        }

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (grid[x][y] == 0)
                    grid[x][y] = n2;
            }
        }
    }

    private void clicked(final int finalI, final int finalJ) {
        I = finalI;
        J = finalJ;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }
}