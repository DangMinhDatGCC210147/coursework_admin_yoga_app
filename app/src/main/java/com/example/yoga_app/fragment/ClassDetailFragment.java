package com.example.yoga_app.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yoga_app.R;
import com.example.yoga_app.WifiChecker;
import com.example.yoga_app.adapter.CustomCourseAdapter;
import com.example.yoga_app.adapter.CustomInstructorAdapter;
import com.example.yoga_app.model.Classes;
import com.example.yoga_app.model.Course;
import com.example.yoga_app.DatabaseHelper;
import com.example.yoga_app.model.Instructor;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ClassDetailFragment extends Fragment {

    private static final String ARG_CLASS = "classes";
    private DatabaseHelper databaseHelper;
    private View view;
    private DatabaseReference databaseReference;

    public static ClassDetailFragment newInstance(Classes classes) {
        ClassDetailFragment fragment = new ClassDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CLASS, classes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_class_detail, container, false);

        databaseHelper = new DatabaseHelper(getContext());
        Classes classes = (Classes) getArguments().getSerializable(ARG_CLASS);

        if (WifiChecker.isWifiConnected(requireContext())) {
            // Initialize and display class information
            initializeClassViews(classes);

            Button editClassButton = view.findViewById(R.id.edit_class_button);
            editClassButton.setOnClickListener(view12 -> showUpdateClassDialog(classes, "Update Class"));

            Button deleteClassButton = view.findViewById(R.id.delete_class_button);
            deleteClassButton.setOnClickListener(view1 -> showDeleteConfirmationDialog(classes));
        }else{
            WifiChecker.showWifiDialog(requireContext());
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("classes");

        return view;
    }

    private void initializeClassViews(Classes classes) {
        TextView classNameDisplay = view.findViewById(R.id.class_name_display);
        TextView classDate = view.findViewById(R.id.class_date);
        TextView classInstructor = view.findViewById(R.id.class_instructor);
        TextView classComments = view.findViewById(R.id.class_comments);
        TextView courseNameDisplay = view.findViewById(R.id.course_name_display);

        // Get course information based on course_id
        int courseIdConvert = Integer.parseInt(classes.getCourseId());
        Course course = databaseHelper.getCourseById(courseIdConvert);
        String courseName = (course != null) ? course.getName() : "N/A";

        int instructorIdConvert = Integer.parseInt(classes.getInstructor()); // Assuming getInstructor() returns a String
        Instructor instructor = databaseHelper.getUserById(instructorIdConvert);
        String instructorName = (instructor != null) ? instructor.getName() : "N/A";

        // Set class information
        classNameDisplay.setText(classes.getName());
        classDate.setText(classes.getDate());
        classInstructor.setText(instructorName);
        classComments.setText(classes.getComments());
        courseNameDisplay.setText(courseName);
    }

    private void showUpdateClassDialog(Classes classes, String title) {
        if (!WifiChecker.isWifiConnected(requireContext())) {
            WifiChecker.showWifiDialog(requireContext());
        }

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_class);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView titleTextView = dialog.findViewById(R.id.dialog_title);
        titleTextView.setText(title);

        // Initialize dialog fields
        EditText editClassName = dialog.findViewById(R.id.class_name_input);
        Spinner editCourseId = dialog.findViewById(R.id.class_course_id_input);
        EditText editDate = dialog.findViewById(R.id.class_date_input);
        Spinner editInstructorId = dialog.findViewById(R.id.class_instructor_id_input);
        EditText editComments = dialog.findViewById(R.id.class_comments_input);
        Button buttonSave = dialog.findViewById(R.id.btn_save_class);

        // Set existing values
        editClassName.setText(classes.getName());
        editDate.setText(classes.getDate());
        editComments.setText(classes.getComments());

        // Load courses and instructors into spinners
        loadCourseSpinner(editCourseId, classes.getCourseId());
        loadInstructorSpinner(editInstructorId, classes.getInstructor());

        setupDialogInteractions(dialog, classes, editClassName, editCourseId, editDate, editInstructorId, editComments, buttonSave);

        dialog.show();
    }

    private void setupDialogInteractions(Dialog dialog, Classes classes, EditText editClassName,
                                         Spinner editCourseId, EditText editDate, Spinner editInstructorId,
                                         EditText editComments, Button buttonSave) {
        // Set initial course day
        List<Course> courses = databaseHelper.getAllCourses();
        int currentCourseIndex = getCourseSpinnerIndex(editCourseId, Integer.parseInt(classes.getCourseId()));
        final String[] currentCourseDay = {courses.get(currentCourseIndex).getCourseDay()};

        editCourseId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Course selectedCourse = (Course) parent.getItemAtPosition(position);
                String courseDay = selectedCourse.getCourseDay();
                if (!courseDay.equalsIgnoreCase(currentCourseDay[0])) {
                    editDate.setText("");
                }
                currentCourseDay[0] = courseDay;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        editDate.setOnClickListener(v -> {
            DatePickerFragment datePickerFragment = new DatePickerFragment(editDate, "dd/MM/yyyy", currentCourseDay[0]);
            datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        });

        buttonSave.setOnClickListener(v -> {
            String name = editClassName.getText().toString();
            String date = editDate.getText().toString();
            String comments = editComments.getText().toString();

            if (name.isEmpty() || date.isEmpty()) {
                Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date selectedDate = sdf.parse(date);
                String selectedDay = new SimpleDateFormat("EEEE", Locale.getDefault()).format(selectedDate);

                if (!selectedDay.equalsIgnoreCase(currentCourseDay[0])) {
                    Toast.makeText(getContext(), "Selected date does not match the course day!", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Invalid date format!", Toast.LENGTH_SHORT).show();
                return;
            }

            int courseId = ((Course) editCourseId.getSelectedItem()).getCourseId();
            int instructorId = ((Instructor) editInstructorId.getSelectedItem()).getId();
            updateClassInDatabase(classes.getId(), courseId, name, date, instructorId, comments);
            dialog.dismiss();
        });
    }

    private void loadCourseSpinner(Spinner editCourseId, String selectedCourseId) {
        List<Course> courses = databaseHelper.getAllCourses();
        CustomCourseAdapter courseAdapter = new CustomCourseAdapter(getContext(), courses);
        editCourseId.setAdapter(courseAdapter);

        int courseIdSelection = Integer.parseInt(selectedCourseId);
        int currentCourseIndex = getCourseSpinnerIndex(editCourseId, courseIdSelection);
        editCourseId.setSelection(currentCourseIndex);
    }

    private void loadInstructorSpinner(Spinner editInstructorId, String selectedInstructorId) {
        List<Instructor> instructors = databaseHelper.getAllInstructors();
        List<Instructor> filteredInstructors = new ArrayList<>();
        for (Instructor instructor : instructors) {
            if (instructor.getRoleId() == 3) {
                filteredInstructors.add(instructor);
            }
        }
        CustomInstructorAdapter instructorAdapter = new CustomInstructorAdapter(getContext(), filteredInstructors);
        editInstructorId.setAdapter(instructorAdapter);

        int instructorIdSelection = Integer.parseInt(selectedInstructorId);
        int instructorIndex = getInstructorSpinnerIndex(editInstructorId, instructorIdSelection);
        editInstructorId.setSelection(instructorIndex);
    }

    private void updateClassInDatabase(int classId, int courseId, String name, String date, int instructor, String comments) {
        // Update the class in the database
        if (databaseHelper.updateClassInstance(classId, courseId, name, date, instructor, comments)) {
            Toast.makeText(getContext(), "Class updated successfully!", Toast.LENGTH_SHORT).show();
            uploadUpdatedClassToFirebase(classId, courseId, name, date, instructor, comments);
            loadClassesFromDatabase(classId);
        } else {
            Toast.makeText(getContext(), "Failed to update class!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog(Classes classes) {

        String message = "Are you sure you want to delete the classes: " + classes.getName();
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Deletion")
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> deleteClass(classes))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteClass(Classes classes) {
        if (classes != null) {
            boolean isDeleted = databaseHelper.deleteClassInDatabase(classes.getId());
            if (isDeleted) {
                Toast.makeText(getContext(), "Class has been deleted", Toast.LENGTH_SHORT).show();
                deleteClassFromFirebase(classes.getId());
                ClassManagementFragment classManagementFragment = new ClassManagementFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, classManagementFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getContext(), "Deletion failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadUpdatedClassToFirebase(int classId, int courseId, String name, String date, int instructor, String comments) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("classes").child(String.valueOf(classId));

        // Prepare data to update
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("courseId", courseId);
        updates.put("date", date);
        updates.put("instructor", instructor);
        updates.put("comments", comments);

        // Update Firebase and handle success/failure
        databaseReference.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ClassDetailFragment", "Class updated successfully in Firebase");
                    Toast.makeText(getContext(), "Class updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("ClassDetailFragment", "Failed to update class in Firebase: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to update class in Firebase", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteClassFromFirebase(int classId) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("classes").child(String.valueOf(classId));

        databaseReference.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d("ClassDetailFragment", "Class deleted successfully from Firebase");
                    Toast.makeText(getContext(), "Class deleted successfully!", Toast.LENGTH_SHORT).show();
                    // Now remove from local database
                    boolean isDeleted = databaseHelper.deleteClassInDatabase(classId);
                    if (isDeleted) {
                        Toast.makeText(getContext(), "Class has been deleted locally", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_layout, new ClassManagementFragment())
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Toast.makeText(getContext(), "Failed to delete class locally", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ClassDetailFragment", "Failed to delete class from Firebase: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to delete class from Firebase", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadClassesFromDatabase(int id) {
        Classes updatedClass = databaseHelper.getClassById(id);
        if (updatedClass != null) {
            initializeClassViews(updatedClass); // Refresh the UI with the updated class details
        } else {
            Toast.makeText(getContext(), "Failed to load updated class data", Toast.LENGTH_SHORT).show();
        }
    }

    private int getCourseSpinnerIndex(Spinner spinner, int courseId) {
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            Course course = (Course) spinner.getAdapter().getItem(i);
            if (course.getCourseId() == courseId) {
                return i;
            }
        }
        return 0;
    }

    private int getInstructorSpinnerIndex(Spinner spinner, int instructorId) {
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            Instructor instructor = (Instructor) spinner.getAdapter().getItem(i);
            if (instructor.getId() == instructorId) {
                return i;
            }
        }
        return 0;
    }
}
