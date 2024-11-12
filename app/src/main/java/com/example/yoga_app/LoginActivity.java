package com.example.yoga_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText etLoginUsername, etLoginPassword;
    Button btnLogin;
    TextView btnGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check wifi connection here
        if (!WifiChecker.isWifiConnected(this)) {
            WifiChecker.showWifiDialog(this);
        }

        setContentView(R.layout.login_activity);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
//            finish();
//            return;
//        }

        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etLoginUsername.getText().toString();
                String password = etLoginPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(email, password);
                }
            }
        });

        btnGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Move to HomePageActivity directly after successful login
                        startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}