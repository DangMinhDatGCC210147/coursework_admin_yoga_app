package com.example.yoga_app.fragment;

import static com.example.yoga_app.DatabaseHelper.COURSE_ID;
import static com.example.yoga_app.DatabaseHelper.COURSE_TABLE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.yoga_app.R;
import com.example.yoga_app.WifiChecker;
import com.example.yoga_app.adapter.ClassesAdapter;
import com.example.yoga_app.model.Classes;
import com.example.yoga_app.model.Course;
import com.example.yoga_app.DatabaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CourseDetailFragment extends Fragment {

    private static final String ARG_COURSE = "course";
    private DatabaseHelper databaseHelper;
    private RecyclerView classesRecyclerView;
    private ClassesAdapter classesAdapter;
    private View view;

    public static CourseDetailFragment newInstance(Course course) {
        CourseDetailFragment fragment = new CourseDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_COURSE, course);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_course_detail, container, false);

        if (!WifiChecker.isWifiConnected(requireContext())) {
            WifiChecker.showWifiDialog(requireContext());
        }

        databaseHelper = new DatabaseHelper(getContext());
        Course course = (Course) getArguments().getSerializable(ARG_COURSE);

        // Initialize and display course information
        initializeCourseViews(course);

        classesRecyclerView = view.findViewById(R.id.classes_recycler_view);
        classesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String courseIdString = String.valueOf(course.getCourseId());
        int courseId = Integer.parseInt(courseIdString);

        List<Classes> classesList = databaseHelper.getClassesByCourseId(courseId);

        // Set adapter for RecyclerView
        classesAdapter = new ClassesAdapter(classesList, this::onClassClick, getContext());
        classesRecyclerView.setAdapter(classesAdapter);

        if (WifiChecker.isWifiConnected(requireContext())) {
            Button editCourseButton = view.findViewById(R.id.edit_course_button);
            editCourseButton.setOnClickListener(view12 -> showUpdateCourseDialog(course, "Update Course"));

            Button deleteCourseButton = view.findViewById(R.id.delete_course_button);
            deleteCourseButton.setOnClickListener(view1 -> showDeleteConfirmationDialog(course));
        } else {
            WifiChecker.showWifiDialog(requireContext());
        }

        return view;
    }

    private void onClassClick(Classes classes) {
        ClassDetailFragment classDetailFragment = ClassDetailFragment.newInstance(classes);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, classDetailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void initializeCourseViews(Course course) {
        TextView courseName = view.findViewById(R.id.course_name);
        TextView courseType = view.findViewById(R.id.course_type);
        TextView coursePrice = view.findViewById(R.id.course_price);
        TextView courseDuration = view.findViewById(R.id.course_duration);
        TextView courseCapacity = view.findViewById(R.id.course_capacity);
        TextView courseDescription = view.findViewById(R.id.course_description);
        TextView courseDay = view.findViewById(R.id.course_day);
        TextView courseTime = view.findViewById(R.id.course_time);

        if (course != null) {
            courseName.setText(course.getName());
            courseType.setText(course.getType());
            coursePrice.setText(course.getPrice());
            courseDuration.setText(course.getDuration());
            courseCapacity.setText(String.valueOf(course.getCapacity()));
            courseDescription.setText(course.getDescription());
            courseDay.setText(course.getCourseDay());
            courseTime.setText(course.getCourseTime());
        }
    }

    private void showUpdateCourseDialog(Course course, String title) {

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_course);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView titleTextView = dialog.findViewById(R.id.dialog_title);
        EditText editCourseName = dialog.findViewById(R.id.course_name_input);
        Spinner editCourseType = dialog.findViewById(R.id.course_type_spinner);
        EditText editCoursePrice = dialog.findViewById(R.id.course_price_input);
        EditText editCourseDuration = dialog.findViewById(R.id.course_duration_input);
        EditText editCourseCapacity = dialog.findViewById(R.id.course_capacity_input);
        EditText editCourseDescription = dialog.findViewById(R.id.course_description_input);
        Spinner editCourseDay = dialog.findViewById(R.id.course_day_spinner);
        EditText editCourseTime = dialog.findViewById(R.id.course_time_input);
        Button buttonSave = dialog.findViewById(R.id.btn_save_course);

        titleTextView.setText(title);
        editCourseName.setText(course.getName());
        editCourseType.setSelection(getSpinnerIndex(editCourseType, course.getType()));
        editCoursePrice.setText(course.getPrice());
        editCourseDuration.setText(course.getDuration());
        editCourseCapacity.setText(String.valueOf(course.getCapacity()));
        editCourseDescription.setText(course.getDescription());
        editCourseDay.setSelection(getSpinnerIndex(editCourseDay, course.getCourseDay()));
        editCourseTime.setText(course.getCourseTime());

        editCourseTime.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (view1, selectedHour, selectedMinute) -> {
                        String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                        editCourseTime.setText(time);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        buttonSave.setOnClickListener(v -> {
            boolean isInputsValid = validateInputs(
                    editCourseName,
                    editCoursePrice,
                    editCourseDuration,
                    editCourseCapacity,
                    editCourseTime
            );
            if (isInputsValid) {
                updateCourseInDatabase(course.getCourseId(), editCourseName, editCourseType, editCoursePrice,
                        editCourseDuration, editCourseCapacity, editCourseDescription, editCourseDay, editCourseTime);

                dialog.dismiss();

            }
        });

        dialog.show();
    }

    private int getSpinnerIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            if (spinner.getAdapter().getItem(i).toString().equals(value)) {
                return i;
            }
        }
        return 0; // Default index
    }

    private void showDeleteConfirmationDialog(Course course) {

        String message = "Are you sure you want to delete the course: " + course.getName();
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Deletion")
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> deleteCourse(course))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteCourse(Course course) {
        if (course != null) {
            boolean isDeleted = databaseHelper.deleteCourseInDatabase(course.getCourseId());
            if (isDeleted) {
                Toast.makeText(getContext(), "Course has been deleted", Toast.LENGTH_SHORT).show();
                deleteCourseFromFirebase(course.getCourseId());
                CourseManagementFragment courseManagementFragment = new CourseManagementFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, courseManagementFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getContext(), "Deletion failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateCourseInDatabase(int courseId, EditText editCourseName, Spinner editCourseType, EditText editCoursePrice,
                                        EditText editCourseDuration, EditText editCourseCapacity, EditText editCourseDescription,
                                        Spinner editCourseDay, EditText editCourseTime) {
        String courseName = editCourseName.getText().toString();
        String courseType = editCourseType.getSelectedItem().toString();
        String coursePrice = editCoursePrice.getText().toString();
        String courseDuration = editCourseDuration.getText().toString();
        int courseCapacity = Integer.parseInt(editCourseCapacity.getText().toString());
        String courseDescription = editCourseDescription.getText().toString();
        String courseDay = editCourseDay.getSelectedItem().toString();
        String courseTime = editCourseTime.getText().toString();

        boolean isUpdated = databaseHelper.updateCourse(courseId, courseName, courseType, coursePrice, courseDuration,
                                                        courseCapacity, courseDescription, courseDay, courseTime);
        if (isUpdated) {
            Toast.makeText(getContext(), "Course updated successfully", Toast.LENGTH_SHORT).show();
            uploadUpdatedCourseToFirebase(courseId, courseName, courseType, coursePrice,
                    courseDuration, courseCapacity, courseDescription, courseDay, courseTime);
            loadCourseFromDatabase(courseId);
        } else {
            Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCourseFromDatabase(int courseId) {
        Course updatedCourse = databaseHelper.getCourseById(courseId);
        if (updatedCourse != null) {
            initializeCourseViews(updatedCourse); // Refresh the UI with the updated course details
        } else {
            Toast.makeText(getContext(), "Failed to load updated course data", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(EditText editCourseName, EditText editCoursePrice, EditText editCourseDuration, EditText editCourseCapacity, EditText editCourseTime) {
        EditText[] inputs = {editCourseName, editCoursePrice, editCourseDuration, editCourseCapacity, editCourseTime};
        for (EditText input : inputs) {
            if (input.getText().toString().trim().isEmpty()) {
                input.setError("This field cannot be empty");
                return false;
            }
        }

        String durationText = editCourseDuration.getText().toString().trim();
        try {
            int duration = Integer.parseInt(durationText);
            if (duration <= 0) {
                editCourseDuration.setError("Duration must be a positive number");
                return false;
            }
        } catch (NumberFormatException e) {
            editCourseDuration.setError("Duration must be a number");
            return false;
        }

        String capacityText = editCourseCapacity.getText().toString().trim();
        try {
            int capacity = Integer.parseInt(capacityText);
            if (capacity <= 0) {
                editCourseCapacity.setError("Capacity must be a positive number");
                return false;
            }
        } catch (NumberFormatException e) {
            editCourseCapacity.setError("Capacity must be a number");
            return false;
        }

        return true;
    }

    private void uploadUpdatedCourseToFirebase(int courseId, String courseName, String courseType, String coursePrice,
                                               String courseDuration, int courseCapacity, String courseDescription,
                                               String courseDay, String courseTime) {
        // Get DatabaseReference to node available in Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("courses").child(String.valueOf(courseId));

        // Create HashMap to store the properties needed to update
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("name", courseName);
        updates.put("type", courseType);
        updates.put("price", coursePrice);
        updates.put("duration", courseDuration);
        updates.put("capacity", courseCapacity);
        updates.put("description", courseDescription);
        updates.put("courseDay", courseDay);
        updates.put("courseTime", courseTime);

        // Update
        databaseReference.updateChildren(updates)
                .addOnSuccessListener(aVoid -> Log.d("CourseDetailFragment", "Course updated successfully in Firebase"))
                .addOnFailureListener(e -> Log.e("CourseDetailFragment", "Failed to update course in Firebase: " + e.getMessage()));
    }

    private void deleteCourseFromFirebase(int courseId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("courses").child(String.valueOf(courseId));
        databaseReference.removeValue()
                .addOnSuccessListener(aVoid -> Log.d("CourseDetailFragment", "Course deleted successfully from Firebase"))
                .addOnFailureListener(e -> Log.e("CourseDetailFragment", "Failed to delete course from Firebase: " + e.getMessage()));
    }
}
