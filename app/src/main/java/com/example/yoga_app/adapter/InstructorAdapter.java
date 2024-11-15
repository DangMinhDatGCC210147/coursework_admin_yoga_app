package com.example.yoga_app.adapter;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yoga_app.DatabaseHelper;
import com.example.yoga_app.R;
import com.example.yoga_app.WifiChecker;
import com.example.yoga_app.model.Instructor;
import com.example.yoga_app.model.Role;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class InstructorAdapter extends RecyclerView.Adapter<InstructorAdapter.InstructorViewHolder> {

    private List<Instructor> instructorList;
    private Context context;
    private DatabaseHelper db;

    public InstructorAdapter(List<Instructor> instructorList, Context context, DatabaseHelper db) {
        this.instructorList = instructorList;
        this.context = context;
        this.db = db;
    }

    @NonNull
    @Override
    public InstructorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.instructor_item, parent, false);
        return new InstructorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructorViewHolder holder, int position) {
        Instructor instructor = instructorList.get(position);
        holder.instructorName.setText(instructor.getName());
        holder.instructorEmail.setText(instructor.getEmail());

        Role role = db.getRoleById(instructor.getRoleId());
        String roleName = role != null ? role.getRole_name() : "N/A";
        holder.instructorRole.setText(roleName);

        holder.btnUpdate.setOnClickListener(v -> {

            Dialog editDialog = new Dialog(context);
            editDialog.setContentView(R.layout.dialog_add_instructor);
            editDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            EditText editName = editDialog.findViewById(R.id.instructor_name_input);
            EditText editEmail = editDialog.findViewById(R.id.instructor_email_input);
            Button btnSave = editDialog.findViewById(R.id.btn_save_instructor);

            editName.setText(instructor.getName());
            editEmail.setText(instructor.getEmail());

            btnSave.setOnClickListener(v1 -> {
                String newName = editName.getText().toString().trim();
                String newEmail = editEmail.getText().toString().trim();

                if (newName.isEmpty() || newEmail.isEmpty()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (db.updateInstructor(instructor.getId(), newName, newEmail)) {
                    updateInstructorInFirebase(instructor.getId(), newName, newEmail);
                    Toast.makeText(context, "Instructor updated successfully", Toast.LENGTH_SHORT).show();
                    loadInstructors();
                    editDialog.dismiss();
                } else {
                    Toast.makeText(context, "Failed to update instructor", Toast.LENGTH_SHORT).show();
                }
            });

            editDialog.show();
        });


        holder.btnDelete.setOnClickListener(v -> {
            showDeleteConfirmationDialog(instructor);
        });
    }

    private void loadInstructors() {
        instructorList.clear();
        List<Instructor> allInstructors = db.getAllInstructors();

        if (allInstructors != null) {
            for (Instructor instructor : allInstructors) {
                if (instructor.getRoleId() == 3) {
                    instructorList.add(instructor);
                }
            }
            notifyDataSetChanged();
        } else {
            Toast.makeText(context, "Failed to load instructors", Toast.LENGTH_SHORT).show();
        }
    }
    private void showDeleteConfirmationDialog(Instructor instructor) {
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete " + instructor.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> deleteInstructorInDatabase(instructor))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteInstructorInDatabase(Instructor instructor) {
        if (db.deleteInstructor(instructor.getId())) {
            deleteInstructorFromFirebase(instructor.getId());
            Toast.makeText(context, "Instructor deleted successfully", Toast.LENGTH_SHORT).show();
            loadInstructors(); // Refresh the list after deletion
        } else {
            Toast.makeText(context, "Failed to delete instructor", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateInstructorInFirebase(int instructorId, String name, String email) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("instructors").child(String.valueOf(instructorId));

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("email", email);

        databaseReference.updateChildren(updates)
                .addOnSuccessListener(aVoid -> Log.d("InstructorAdapter", "Instructor updated successfully in Firebase"))
                .addOnFailureListener(e -> Log.e("InstructorAdapter", "Failed to update instructor in Firebase: " + e.getMessage()));
    }

    private void deleteInstructorFromFirebase(int instructorId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("instructors").child(String.valueOf(instructorId));
        databaseReference.removeValue()
                .addOnSuccessListener(aVoid -> Log.d("InstructorAdapter", "Instructor deleted successfully from Firebase"))
                .addOnFailureListener(e -> Log.e("InstructorAdapter", "Failed to delete instructor from Firebase: " + e.getMessage()));
    }

    @Override
    public int getItemCount() {
        return instructorList.size();
    }

    public static class InstructorViewHolder extends RecyclerView.ViewHolder {
        TextView instructorName, instructorEmail, instructorRole;
        Button btnUpdate, btnDelete;

        public InstructorViewHolder(@NonNull View itemView) {
            super(itemView);
            instructorName = itemView.findViewById(R.id.instructor_name);
            instructorEmail = itemView.findViewById(R.id.instructor_email);
            instructorRole = itemView.findViewById(R.id.instructor_role);
            btnUpdate = itemView.findViewById(R.id.btn_update_instructor);
            btnDelete = itemView.findViewById(R.id.btn_delete_instructor);
        }
    }
}