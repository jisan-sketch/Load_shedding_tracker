package com.example.loadsheddingtracker.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loadsheddingtracker.R;

/**
 * LECTURE TOPIC: Simple ListView with ArrayAdapter & ActivityResult returning data
 *
 * EXPLANATION FOR VIVA:
 * 1. Shows a simple String[] dataset using built-in system layout android.R.layout.simple_list_item_1.
 * 2. Uses setOnItemClickListener to detect taps, retrieves the clicked area using position and adapter.getItem(position).
 * 3. Bundles the selected area in an Intent extra and sets it as the Activity result via setResult(RESULT_OK, data).
 * 4. Returns the result back to DashboardActivity which launched it via modern ActivityResultLauncher.
 */
public class SelectAreaActivity extends AppCompatActivity {

    // Bangladesh areas array for load-shedding tracking
    private final String[] BANGLADESH_AREAS = new String[]{
            "Khulna",
            "Dhaka",
            "Rajshahi",
            "Sylhet",
            "Barisal",
            "Chattogram",
            "Rangpur",
            "Mymensingh",
            "Comilla",
            "Gazipur",
            "Jessore",
            "Kushtia"
    };

    private ListView lvAreas;
    private EditText etSearchArea;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_area);

        // Find views by ID
        lvAreas = findViewById(R.id.lv_areas);
        etSearchArea = findViewById(R.id.et_search_area);
        btnBack = findViewById(R.id.btn_select_area_back);

        btnBack.setOnClickListener(v -> finish());

        // LECTURE TOPIC: Simple ArrayAdapter with android.R.layout.simple_list_item_1
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                BANGLADESH_AREAS
        );

        // Bind adapter to ListView
        lvAreas.setAdapter(adapter);

        // Instant search filtering
        etSearchArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // LECTURE TOPIC: ListView item click handling using position
        lvAreas.setOnItemClickListener((parent, view, position, id) -> {
            String selectedArea = adapter.getItem(position);
            Toast.makeText(SelectAreaActivity.this, "Selected: " + selectedArea, Toast.LENGTH_SHORT).show();

            // Prepare result Intent to send data back to caller
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SELECTED_AREA", selectedArea);
            setResult(Activity.RESULT_OK, resultIntent);

            // Close this activity and return to DashboardActivity
            finish();
        });
    }
}
