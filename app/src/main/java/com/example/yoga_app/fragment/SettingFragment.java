package com.example.yoga_app.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.content.SharedPreferences;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.yoga_app.DatabaseHelper;
import com.example.yoga_app.InitialActivity;
import com.example.yoga_app.R;
import com.example.yoga_app.UploadTaskThread;
import com.example.yoga_app.WifiChecker;
import com.example.yoga_app.model.Classes;
import com.example.yoga_app.model.Course;
import com.example.yoga_app.model.Instructor;
import com.example.yoga_app.model.Role;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SettingFragment extends Fragment {

    Button btnDeleteAccount, btnLogout, btnUpload, btnResetData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        if (!WifiChecker.isWifiConnected(requireContext())) {
            WifiChecker.showWifiDialog(requireContext());
        }

        btnDeleteAccount = view.findViewById(R.id.btn_delete_account);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnUpload =view.findViewById(R.id.btn_upload_toInternet);
        btnResetData = view.findViewById(R.id.btn_resetData);

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDeleteAccount();
            }
        });

        btnResetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmResetData();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!WifiChecker.isWifiConnected(requireContext())) {
                    WifiChecker.showWifiDialog(requireContext());
                }else{
                    uploadToInternet();
                }

            }
        });
        return view;
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Delete account in Firebase Authentication
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DatabaseHelper db = new DatabaseHelper(getContext());
                            boolean isDeleted = db.deleteUserByEmail(user.getEmail());

                            if (isDeleted) {
                                Toast.makeText(getActivity(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                logout();
                            } else {
                                Toast.makeText(getActivity(), "Failed to delete account from local database", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Failed to delete account from Firebase: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "No logged-in user found", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmResetData() {
        new AlertDialog.Builder(getContext())
                .setTitle("Reset Data")
                .setMessage("Are you sure you want to reset all data? This action will delete all courses and classes from both SQLite and Firebase.")
                .setPositiveButton("Yes", (dialog, which) -> resetData())
                .setNegativeButton("No", null)
                .show();
    }

    private void resetData() {
        // Delete data in SQLite
        DatabaseHelper db = new DatabaseHelper(getContext());
        db.clearTables();

        DatabaseReference firebaseCoursesRef = FirebaseDatabase.getInstance().getReference("courses");
        DatabaseReference firebaseClassesRef = FirebaseDatabase.getInstance().getReference("classes");
        DatabaseReference firebaseBookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
        DatabaseReference firebaseCartRef = FirebaseDatabase.getInstance().getReference("cart");

        firebaseCoursesRef.removeValue().addOnSuccessListener(aVoid ->
                Toast.makeText(getContext(), "Courses data deleted from Firebase", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Failed to delete courses from Firebase", Toast.LENGTH_SHORT).show()
        );

        firebaseClassesRef.removeValue().addOnSuccessListener(aVoid ->
                Toast.makeText(getContext(), "Classes data deleted from Firebase", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Failed to delete classes from Firebase", Toast.LENGTH_SHORT).show()
        );

        firebaseBookingsRef.removeValue().addOnSuccessListener(aVoid ->
                Toast.makeText(getContext(), "Bookings data deleted from Firebase", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Failed to delete bookings from Firebase", Toast.LENGTH_SHORT).show()
        );

        firebaseCartRef.removeValue().addOnSuccessListener(aVoid ->
                Toast.makeText(getContext(), "Cart data deleted from Firebase", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Failed to delete cart from Firebase", Toast.LENGTH_SHORT).show()
        );


        Toast.makeText(getContext(), "All data has been reset", Toast.LENGTH_SHORT).show();
    }


    private void logout() {
        FirebaseAuth.getInstance().signOut();

        Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(getActivity(), InitialActivity.class));
        getActivity().finish();
    }

    private void uploadToInternet() {
        Log.d("UploadToInternet", "Starting uploadToInternet()");

        DatabaseHelper db = new DatabaseHelper(getContext());
        List<Course> courseList = db.getAllCourses();
        List<Classes> classesList = db.getAllClasses();
        List<Role> roleList = db.getAllRoles();
        List<Instructor> instructorList = db.getAllInstructors();

        Log.d("UploadToInternet", "Fetched data - Courses: " + courseList.size() + ", Classes: " + classesList.size() +
                ", Roles: " + roleList.size() + ", Instructors: " + instructorList.size());

        if (courseList.isEmpty() && classesList.isEmpty() && roleList.isEmpty() && instructorList.isEmpty()) {
            Toast.makeText(getContext(), "No data to upload.", Toast.LENGTH_SHORT).show();
            Log.d("UploadToInternet", "No data to upload, exiting function.");
            return;
        }

        // Delete node “courses” and after that continuously delete node “classes” if success
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("courses").removeValue().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Log.d("UploadToInternet", "Node 'courses' deleted successfully.");
                database.child("classes").removeValue().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        Log.d("UploadToInternet", "Node 'classes' deleted successfully.");
                        // Just begin upload when both node above deleted successfully
                        Thread uploadThread = new Thread(() -> {
                            UploadTaskThread uploadTask = new UploadTaskThread(courseList, classesList, roleList, instructorList);
                            uploadTask.run();
                            //Wait
                            while (!uploadTask.isCompleted()) {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            // Update UI when successful
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Upload completed successfully!", Toast.LENGTH_SHORT).show();
                                Log.d("UploadToInternet", "Upload completed successfully.");
                            });
                        });
                        uploadThread.start();
                        Log.d("UploadToInternet", "Upload thread started.");
                    } else {
                        Log.e("UploadToInternet", "Failed to delete 'classes': " + task2.getException().getMessage());
                    }
                });
            } else {
                Log.e("UploadToInternet", "Failed to delete 'courses': " + task1.getException().getMessage());
            }
        });
    }
}