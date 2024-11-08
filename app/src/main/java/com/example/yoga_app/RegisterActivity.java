package com.example.yoga_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yoga_app.model.Instructor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    DatabaseHelper db;
    EditText etEmail, etPassword, etConfirmPassword, etName;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        // Check wifi connection here
        if (!WifiChecker.isWifiConnected(this)) {
            WifiChecker.showWifiDialog(this);
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim().toLowerCase();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(name, email, password);
                }
            }
        });
    }

    private void registerUser(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng ký thành công, lưu thông tin giảng viên vào Realtime Database
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String hashedPassword = hashPassword(password);

                            // Lưu thông tin vào SQLite
                            boolean insert = db.insertUser(name, email, hashedPassword, 1); // Giả sử role_id = 1

                            if (insert) {
                                // Lưu thông tin giảng viên vào Realtime Database
                                saveUserToDatabase(user.getUid(), name, email);

                                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveUserToDatabase(String userId, String name, String email) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("instructors").child(userId);
        Instructor instructor = new Instructor(name, email, null, 1);

        userRef.setValue(instructor).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Thông tin giảng viên đã được lưu thành công
                Toast.makeText(RegisterActivity.this, "Instructor data saved to database", Toast.LENGTH_SHORT).show();
            } else {
                // Nếu lưu không thành công
                Toast.makeText(RegisterActivity.this, "Failed to save instructor data to database", Toast.LENGTH_SHORT).show();
            }
        });
    }
}