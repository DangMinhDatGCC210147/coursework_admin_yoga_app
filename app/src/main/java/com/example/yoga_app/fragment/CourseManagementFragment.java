package com.example.yoga_app.fragment;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.yoga_app.R;
import com.example.yoga_app.WifiChecker;
import com.example.yoga_app.adapter.CourseAdapter;
import com.example.yoga_app.model.Course;
import com.example.yoga_app.DatabaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CourseManagementFragment extends Fragment {

    private RecyclerView recyclerViewCourses;
    private CourseAdapter courseAdapter;
    private List<Course> courseList;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_management, container, false);

        databaseHelper = new DatabaseHelper(getContext());
        recyclerViewCourses = view.findViewById(R.id.recycler_view_courses);
        recyclerViewCourses.setLayoutManager(new LinearLayoutManager(getContext()));

        courseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(courseList, this::navigateToCourseDetail);
        recyclerViewCourses.setAdapter(courseAdapter);

        loadCoursesFromDatabase();
        Button btnAddCourse = view.findViewById(R.id.btn_add_course);
        btnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (WifiChecker.isWifiConnected(requireContext())) {
                    showAddCourseDialog();
                } else {
                    WifiChecker.showWifiDialog(requireContext());
                }
            }
        });


        return view;
    }

    private void navigateToCourseDetail(Course course) {
        CourseDetailFragment courseDetailFragment = CourseDetailFragment.newInstance(course);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, courseDetailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadCoursesFromDatabase() {
        courseList.clear();
        List<Course> coursesFromDb = databaseHelper.getAllCourses();

        if (coursesFromDb != null) {
            courseList.addAll(coursesFromDb);
            courseAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getContext(), "Failed to load courses", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddCourseDialog() {
        if (!WifiChecker.isWifiConnected(requireContext())) {
            WifiChecker.showWifiDialog(requireContext());
        }

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_course);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText editCourseName = dialog.findViewById(R.id.course_name_input);
        Spinner editCourseType = dialog.findViewById(R.id.course_type_spinner);
        EditText editCoursePrice = dialog.findViewById(R.id.course_price_input);
        EditText editCourseDuration = dialog.findViewById(R.id.course_duration_input);
        EditText editCourseCapacity = dialog.findViewById(R.id.course_capacity_input);
        EditText editCourseDescription = dialog.findViewById(R.id.course_description_input);
        Spinner editCourseDay = dialog.findViewById(R.id.course_day_spinner);
        EditText editCourseTime = dialog.findViewById(R.id.course_time_input);
        Button buttonSave = dialog.findViewById(R.id.btn_save_course);

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
            boolean isInputsValid = validateInputs(editCourseName, editCoursePrice, editCourseDuration, editCourseCapacity, editCourseTime);
            if (isInputsValid) {
                Course tempCourse = new Course(
                        editCourseName.getText().toString(),
                        editCourseType.getSelectedItem().toString(),
                        "$" + editCoursePrice.getText().toString(),
                        editCourseDuration.getText().toString(),
                        editCourseCapacity.getText().toString(),
                        editCourseDescription.getText().toString(),
                        editCourseDay.getSelectedItem().toString(),
                        editCourseTime.getText().toString()
                );

                showCourseDetailsConfirmationDialog(tempCourse, dialog);
            }
        });

        dialog.show();
    }

    private void showCourseDetailsConfirmationDialog(Course course, Dialog parentDialog) {
        String message = "Name: " + course.getName() +
                "\nType: " + course.getType() +
                "\nPrice: " + course.getPrice() +
                "\nDuration: " + course.getDuration() +
                "\nCapacity: " + course.getCapacity() +
                "\nDay: " + course.getCourseDay() +
                "\nTime: " + course.getCourseTime();

        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Course Details")
                .setMessage(message)
                .setPositiveButton("OK", (confirmDialog, which) -> {
                    if (addCourseToDatabase(
                            course.getName(),
                            course.getType(),
                            course.getPrice().replace("$", ""),
                            course.getDuration(),
                            course.getCapacity(),
                            course.getDescription(),
                            course.getCourseDay(),
                            course.getCourseTime()
                    )) {
                        parentDialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", (confirmDialog, which) -> confirmDialog.dismiss())
                .show();
    }

    private boolean addCourseToDatabase(String courseName, String courseType, String coursePrice,
                                        String courseDuration, String courseCapacity, String courseDescription,
                                        String courseDay, String courseTime) {

        Course newCourse = new Course(courseName, courseType, "$" + coursePrice, courseDuration, courseCapacity, courseDescription, courseDay, courseTime);

        boolean isCreated = databaseHelper.insertCourse(courseDay, courseTime, courseName, courseType, "$" + coursePrice,
                Integer.parseInt(courseDuration), Integer.parseInt(courseCapacity), courseDescription);

        if (isCreated) {
            int courseId = databaseHelper.getLastInsertedCourseId();
            newCourse.setCourseId(courseId);
            courseList.add(newCourse);
            courseAdapter.notifyItemInserted(courseList.size() - 1);
            Toast.makeText(getContext(), "Course created successfully", Toast.LENGTH_SHORT).show();

            // Check WiFi connection before uploading to Firebase
            if (WifiChecker.isWifiConnected(requireContext())) {
                uploadCourseToFirebase(newCourse);
            } else {
                WifiChecker.showWifiDialog(requireContext());
            }

            return true;
        } else {
            Toast.makeText(getContext(), "Creation failed", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void uploadCourseToFirebase(Course course) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("courses");

        databaseReference.child(String.valueOf(course.getCourseId())).setValue(course)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ClassDetailFragment", "Course uploaded successfully to Firebase");
                    //Toast.makeText(getContext(), "Course uploaded to Firebase", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("ClassDetailFragment", "Failed to upload course to Firebase: " + e.getMessage());
                    //Toast.makeText(getContext(), "Failed to upload course to Firebase", Toast.LENGTH_SHORT).show();
                });
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
}