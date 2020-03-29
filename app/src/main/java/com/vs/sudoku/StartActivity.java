package com.vs.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    public String level;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Spinner spinner = findViewById(R.id.spinner);
        String[] sem = new String[]{"Easy","Medium","Hard"};
        ArrayAdapter adapter = new ArrayAdapter<>(StartActivity.this,android.R.layout.simple_spinner_dropdown_item,sem);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Toast.makeText(StartActivity.this, "Select Hardness ", Toast.LENGTH_SHORT).show();
                        level = "easy";
                        break;
                    case 1:
                        level = "medium";
                        break;
                    case 2:
                        level = "hard";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(StartActivity.this,"Please Select Hardness!",Toast.LENGTH_SHORT).show();
            }
        });

        final Button Start = findViewById(R.id.Start);
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this,MainActivity.class);
                intent.putExtra("level",level);
                startActivity(intent);
                finish();
            }
        });
    }
}
