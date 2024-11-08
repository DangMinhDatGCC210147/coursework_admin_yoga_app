package com.example.yoga_app.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;

import com.example.yoga_app.DatabaseHelper;
import com.example.yoga_app.R;
import com.example.yoga_app.WifiChecker;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AccountFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private TextView tvEmail, tvName;
    private EditText etOldPassword, etNewPassword;
    private Button btnUpdatePassword;
    private String name, email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        if (!WifiChecker.isWifiConnected(requireContext())) {
            WifiChecker.showWifiDialog(requireContext());
        }

        // Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Setup database helper
        databaseHelper = new DatabaseHelper(getActivity());
        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_email);
//        etOldPassword = view.findViewById(R.id.et_old_password);
//        etNewPassword = view.findViewById(R.id.et_new_password);
//        btnUpdatePassword = view.findViewById(R.id.btn_update_password);

        if (currentUser != null) {
            email = currentUser.getEmail();
            // Assuming you have a method to fetch user name based on email
            name = databaseHelper.getUserNameByEmail(email);

            tvName.setText("Name: " + name);
            tvEmail.setText("Email: " + email);
        } else {
            Toast.makeText(getActivity(), "No user is currently logged in.", Toast.LENGTH_SHORT).show();
        }

//        btnUpdatePassword.setOnClickListener(v -> {
//            String oldPassword = etOldPassword.getText().toString();
//            String newPassword = etNewPassword.getText().toString();
//
//            if (databaseHelper.checkUser(email, oldPassword)) {
//                updateUserPassword(email, newPassword);
//            } else {
//                Toast.makeText(getActivity(), "Please check your old password!", Toast.LENGTH_SHORT).show();
//            }
//        });

        return view;
    }

//    private void updateUserPassword(String email, String newPassword) {
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = mAuth.getCurrentUser();
//
//        if (user != null) {
//            String oldPassword = etOldPassword.getText().toString();
//
//            // Xác thực lại người dùng bằng mật khẩu cũ
//            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
//            user.reauthenticate(credential).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
//                        if (updateTask.isSuccessful()) {
//                            String hashedPassword = hashPassword(newPassword);
//                            boolean isUpdated = databaseHelper.updatePassword(email, hashedPassword);
//                            if (isUpdated) {
//                                Toast.makeText(getActivity(), "Password updated successfully!", Toast.LENGTH_SHORT).show();
//                                etOldPassword.setText("");
//                                etNewPassword.setText("");
//                                hideKeyboard();
//                            } else {
//                                Toast.makeText(getActivity(), "Failed to update password in SQLite!", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(getActivity(), "Failed to update password in Firebase: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//                    Toast.makeText(getActivity(), "Authentication failed. Please check your old password!", Toast.LENGTH_SHORT).show();
//                }
//            });
//        } else {
//            Toast.makeText(getActivity(), "No user is currently logged in.", Toast.LENGTH_SHORT).show();
//        }
//    }

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

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }
}