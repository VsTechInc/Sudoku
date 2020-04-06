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
    public int[][] grid = new int[9][9];
    public int[][] array = new int[9][9];
    int easyMax = 35;
    int easyMin = 30;
    int mediumMax = 30;
    int mediumMin = 25;
    int hardMax = 25;
    int hardMin = 20;
    private Button[][] bord = new Button[9][9];
    private Button[] num = new Button[9];
    private String[] generatedXY = new String[81];
    private int n = 0;

    public static boolean noConflict(int[][] array, int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (array[row][i] == num) {
                return false;
            }
            if (array[i][col] == num) {
                return false;
            }
        }
        int gridRow = row - (row % 3);
        int gridColumn = col - (col % 3);
        for (int p = gridRow; p < gridRow + 3; p++) {
            for (int q = gridColumn; q < gridColumn + 3; q++) {
                if (array[p][q] == num) {
                    return false;
                }
            }
        }
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
                int v;
                v = nonnull(i, j);
                if (noConflict(array, i, j, v)) {
                    Toast.makeText(MainActivity.this, "Successful", LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Try Again!", LENGTH_SHORT).show();
                }
            }
        }
    }

    private int nonnull(int i, int j) {
        int v;
        if (bord[i][j].getText().toString().equals("")) {
            v = 0;
        } else {
            v = Integer.parseInt(bord[i][j].getText().toString());
        }
        return v;
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
        generateBoard();
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

        int randomNumber;
        int noOfCells;
        Intent intent = getIntent();
        String option = intent.getStringExtra("level");

        assert option != null;
        switch (option) {
            case "easy":
                noOfCells = random.nextInt((easyMax - easyMin) + 1) + easyMax;
                break;
            case "medium":
                noOfCells = random.nextInt((mediumMax - mediumMin) + 1) + mediumMax;
                break;
            case "hard":
                noOfCells = random.nextInt((hardMax - hardMin) + 1) + hardMin;
                break;
            default:
                noOfCells = 10;
                break;
        }

        for (int i = 1; i <= noOfCells; i++) {
            row = random.nextInt(9);
            col = random.nextInt(9);
            randomNumber = random.nextInt(9) + 1;

            if (grid[row][col] == 0 && noConflict(grid, row, col, randomNumber)) {
                grid[row][col] = randomNumber;
            } else {
                i--;
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