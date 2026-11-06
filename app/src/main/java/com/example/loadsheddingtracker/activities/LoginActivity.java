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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loadsheddingtracker.R;
import com.example.loadsheddingtracker.utils.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;

// LECTURE TOPIC: Firebase Authentication (Email/Password Login & Reset)
// Demonstrates:
// 1. ConstraintLayout form layout
// 2. Button click handling via:
//    (a) android:onClick in XML (onRegisterClick)
//    (b) Java lambda setOnClickListener (btnLogin.setOnClickListener)
//    (c) Java Anonymous Inner Class (btnForgotPassword.setOnClickListener)
// 3. FirebaseAuth.signInWithEmailAndPassword and sendPasswordResetEmail
public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnForgotPassword;
    private ProgressBar pbLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Plain findViewById initialization
        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        btnForgotPassword = findViewById(R.id.btn_forgot_password);
        pbLogin = findViewById(R.id.pb_login);

        // Pre-fill demo credentials for easy viva demonstration
        etEmail.setText("student@example.com");
        etPassword.setText("123456");

        // LECTURE TOPIC: Button Click Handling (b) - Java Lambda setOnClickListener
        btnLogin.setOnClickListener(v -> performLogin());

        // LECTURE TOPIC: Button Click Handling (c) - Java Anonymous Class View.OnClickListener
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPasswordDialog();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Auto-login: if FirebaseAuth has an active signed-in user, go straight to DashboardActivity
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // LECTURE TOPIC: Button Click Handling (a) - XML android:onClick method
    // Declared in activity_login.xml on btn_go_register: android:onClick="onRegisterClick"
    public void onRegisterClick(View view) {
        // Explicit Intent to navigate to RegisterActivity
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Input validation
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

        pbLogin.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Check if Firebase has been configured with google-services.json
        if (!FirebaseHelper.isFirebaseConfigured(this)) {
            pbLogin.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            Toast.makeText(this, "Firebase not configured yet. Proceeding in Offline Demo Mode.", Toast.LENGTH_SHORT).show();
            // Allow student to demonstrate the rest of the application without crashing
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.putExtra("EXTRA_USER_EMAIL", email);
            startActivity(intent);
            finish();
            return;
        }

        // Firebase Auth signInWithEmailAndPassword
        FirebaseAuth auth = FirebaseHelper.getAuth(this);
        if (auth != null) {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        pbLogin.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Sign In Successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void showForgotPasswordDialog() {
        final EditText input = new EditText(this);
        input.setHint("Enter registered email");
        input.setText(etEmail.getText().toString().trim());

        new AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage("Enter your email to receive a password reset link:")
                .setView(input)
                .setPositiveButton("Send Reset Email", (dialog, which) -> {
                    String email = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(email)) {
                        FirebaseAuth auth = FirebaseHelper.getAuth(LoginActivity.this);
                        if (auth != null) {
                            auth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Error sending reset email.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(LoginActivity.this, "Firebase not configured. Check FIREBASE_SETUP.md", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

