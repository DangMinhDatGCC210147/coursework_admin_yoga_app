package com.example.yoga_app.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yoga_app.DatabaseHelper;
import com.example.yoga_app.R;
import com.example.yoga_app.UploadTaskThread;
import com.example.yoga_app.WifiChecker;
import com.example.yoga_app.adapter.InstructorAdapter;
import com.example.yoga_app.model.Instructor;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InstructorManagementFragment extends Fragment {

    private RecyclerView recyclerViewInstructors;
    private InstructorAdapter instructorAdapter;
    private List<Instructor> instructorList;
    private DatabaseHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instructor_management, container, false);

        db = new DatabaseHelper(getContext());
        instructorList = new ArrayList<>();

        // Initialize RecyclerView
        recyclerViewInstructors = view.findViewById(R.id.recycler_view_instructors);
        recyclerViewInstructors.setLayoutManager(new LinearLayoutManager(getContext()));
        instructorAdapter = new InstructorAdapter(instructorList, getContext(), db);
        recyclerViewInstructors.setAdapter(instructorAdapter);

        // Load instructors from database
        loadInstructors();

        if (!WifiChecker.isWifiConnected(requireContext())) {
            WifiChecker.showWifiDialog(requireContext());
        }else{
            Button btnAddInstructor = view.findViewById(R.id.btn_add_instructor);
            btnAddInstructor.setOnClickListener(v -> showAddInstructorDialog());
//            btnAddInstructor.setOnClickListener(v -> importSampleInstructors());
        }

        return view;
    }

//    private void importSampleInstructors() {
//        List<Instructor> sampleInstructors = new ArrayList<>();
//
//        for (int i = 1; i <= 100; i++) {
//            String name = "Instructor " + i;
//            String email = "instructor" + i + "@example.com";
//            String password = generateRandomPassword(10);
//
//            // Create Instructor object with generated data
//            Instructor instructor = new Instructor(0, name, email, password, 3);
//            sampleInstructors.add(instructor);
//        }
//
//        // Pass to UploadTaskThread to handle both SQLite insertion and Firebase upload
//        UploadTaskThread uploadTask = new UploadTaskThread(null, null, null, sampleInstructors);
//        new Thread(uploadTask).start();
//
//        // Notify the user of process initiation
//        getActivity().runOnUiThread(() -> {
//            Toast.makeText(getContext(), "100 sample instructors created. Upload to Firebase and SQLite initiated.", Toast.LENGTH_SHORT).show();
//        });
//    }

    private void loadInstructors() {
        instructorList.clear();
        List<Instructor> allInstructors = db.getAllInstructors();

        for (Instructor instructor : allInstructors) {
            if (instructor.getRoleId() == 3) {
                instructorList.add(instructor);
            }
        }
        instructorAdapter.notifyDataSetChanged();
    }

    private void showAddInstructorDialog() {

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_instructor);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText etName = dialog.findViewById(R.id.instructor_name_input);
        EditText etEmail = dialog.findViewById(R.id.instructor_email_input);
        Button btnSave = dialog.findViewById(R.id.btn_save_instructor);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(email)) {
                Toast.makeText(getContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            String password = generateRandomPassword(10);

            boolean insert = db.insertInstructorInDatabase(name, email, password, 3);
            if (insert) {
                int instructorId = db.getLastInsertedInstructorId();
                addInstructorToFirebase(instructorId, name, email, password);

                Toast.makeText(getContext(), "Instructor added successfully", Toast.LENGTH_SHORT).show();
                loadInstructors();
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Failed to add instructor", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXY_Zabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private void addInstructorToFirebase(int instructorId, String name, String email, String password) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("instructors").child(String.valueOf(instructorId));

        HashMap<String, Object> instructorData = new HashMap<>();
        instructorData.put("id", instructorId);
        instructorData.put("name", name);
        instructorData.put("email", email);
        instructorData.put("roleId", 3);
        instructorData.put("password", password);

        databaseReference.setValue(instructorData)
                .addOnSuccessListener(aVoid -> Log.d("InstructorManagement", "Instructor added to Firebase successfully"))
                .addOnFailureListener(e -> Log.e("InstructorManagement", "Failed to add instructor to Firebase: " + e.getMessage()));
    }

}