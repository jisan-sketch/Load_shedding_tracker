package com.example.loadsheddingtracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loadsheddingtracker.R;
import com.example.loadsheddingtracker.utils.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;

// LECTURE TOPIC: Firebase Authentication - User Registration
// Demonstrates createUserWithEmailAndPassword, input validation, and ProgressBar state management.
public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirm;
    private Button btnRegister;
    private Button btnGoLogin;
    private ProgressBar pbRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.et_register_email);
        etPassword = findViewById(R.id.et_register_password);
        etConfirm = findViewById(R.id.et_register_confirm);
        btnRegister = findViewById(R.id.btn_register);
        btnGoLogin = findViewById(R.id.btn_go_login);
        pbRegister = findViewById(R.id.pb_register);

        btnRegister.setOnClickListener(v -> performRegistration());
        btnGoLogin.setOnClickListener(v -> finish());
    }

    private void performRegistration() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm = etConfirm.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirm)) {
            etConfirm.setError("Passwords do not match");
            etConfirm.requestFocus();
            return;
        }

        pbRegister.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        if (!FirebaseHelper.isFirebaseConfigured(this)) {
            pbRegister.setVisibility(View.GONE);
            btnRegister.setEnabled(true);
            Toast.makeText(this, "Firebase not configured. Account created in offline test mode.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
            intent.putExtra("EXTRA_USER_EMAIL", email);
            startActivity(intent);
            finish();
            return;
        }

        FirebaseAuth auth = FirebaseHelper.getAuth(this);
        if (auth != null) {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        pbRegister.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Account registered successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                            Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}

