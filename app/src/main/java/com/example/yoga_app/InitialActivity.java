package com.example.yoga_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yoga_app.model.Instructor;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class InitialActivity extends AppCompatActivity {

    private Button loginButton;
    private TextView signupText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_activity);

        startInstructorListener();

        loginButton = findViewById(R.id.login_button);
        signupText = findViewById(R.id.signup_text);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InitialActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InitialActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startInstructorListener() {
        DatabaseReference instructorsRef = FirebaseDatabase.getInstance().getReference().child("instructors");

        instructorsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                try {
                    // Get data from snapshot in Map style to check data
                    Map<String, Object> instructorData = (Map<String, Object>) dataSnapshot.getValue();

                    if (instructorData != null) {

                        if (instructorData.get("id") instanceof Long) {
                            instructorData.put("id", ((Long) instructorData.get("id")).intValue());
                        } else if (instructorData.get("id") instanceof String) {
                            instructorData.put("id", Integer.parseInt((String) instructorData.get("id")));
                        }

                        if (instructorData.get("roleId") instanceof Long) {
                            instructorData.put("roleId", ((Long) instructorData.get("roleId")).intValue());
                        } else if (instructorData.get("roleId") instanceof String) {
                            instructorData.put("roleId", Integer.parseInt((String) instructorData.get("roleId")));
                        }

                        // Create Instructor from Map after convert
                        Instructor instructor = new Instructor(
                                (int) instructorData.get("id"),
                                (String) instructorData.get("name"),
                                (String) instructorData.get("email"),
                                (String) instructorData.get("password"),
                                (int) instructorData.get("roleId")
                        );

                        // Check in database SQLite
                        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                        if (!dbHelper.isInstructorExists(instructor.getEmail())) {
                            dbHelper.addInstructor(instructor);
                            Log.d("FirebaseSync", "New instructor added to SQLite: " + instructor.getName());
                        } else {
                            Log.d("FirebaseSync", "Instructor already exists in SQLite: " + instructor.getName());
                        }
                    }
                } catch (Exception e) {
                    Log.e("FirebaseSync", "Failed to process instructor data: " + e.getMessage());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseSync", "Failed to sync instructors: " + databaseError.getMessage());
            }
        });
    }
}
