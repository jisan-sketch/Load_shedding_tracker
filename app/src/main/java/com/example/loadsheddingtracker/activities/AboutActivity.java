package com.example.loadsheddingtracker.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loadsheddingtracker.R;

// LECTURE TOPIC: TableLayout & Viva Topic Coverage Reference
// Displays a structured viva syllabus table matching CSE 2200 course objectives to application components.
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("CSE 2200 Viva Topic Map");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        android.widget.ImageButton btnBack = findViewById(R.id.btn_about_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

