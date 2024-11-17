package com.example.yoga_app.fragment;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.yoga_app.DatabaseHelper;
import com.example.yoga_app.R;
import com.example.yoga_app.UploadTaskThread;
import com.example.yoga_app.WifiChecker;
import com.example.yoga_app.adapter.ClassesAdapter;
import com.example.yoga_app.adapter.CustomCourseAdapter;
import com.example.yoga_app.adapter.CustomInstructorAdapter;
import com.example.yoga_app.model.Classes;
import com.example.yoga_app.model.Course;
import com.example.yoga_app.model.Instructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassManagementFragment extends Fragment {

    private RecyclerView recyclerViewClasses;
    private ClassesAdapter classesAdapter;
    private List<Classes> classesList;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_class_management, container, false);

        databaseHelper = new DatabaseHelper(getContext());
        recyclerViewClasses = view.findViewById(R.id.recycler_view_classes);
        recyclerViewClasses.setLayoutManager(new LinearLayoutManager(getContext()));

        classesList = new ArrayList<>();
        classesAdapter = new ClassesAdapter(classesList, this::navigateToClassDetail, getContext());
        recyclerViewClasses.setAdapter(classesAdapter);

        EditText searchInstructor = view.findViewById(R.id.search_instructor_name);
        searchInstructor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterClassesByInstructor(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        if (WifiChecker.isWifiConnected(requireContext())) {
            Button btnAddClass = view.findViewById(R.id.btn_add_class);
            btnAddClass.setOnClickListener(v -> showAddClassDialog());
        } else {
            WifiChecker.showWifiDialog(requireContext());
        }

        ImageView btnFilter = view.findViewById(R.id.btn_filter);
        btnFilter.setOnClickListener(v -> showFilterDialog());

        loadClassesFromDatabase();

        return view;
    }

    private void showFilterDialog() {
        Dialog filterDialog = new Dialog(getContext());
        filterDialog.setContentView(R.layout.dialog_filter);

        Spinner dayOfWeekSpinner = filterDialog.findViewById(R.id.day_of_week_spinner);
        EditText dateInput = filterDialog.findViewById(R.id.date_input);
        Button applyFilterButton = filterDialog.findViewById(R.id.apply_filter_button);

        // Khi nhấn vào EditText để chọn ngày, hiển thị DatePickerClassesFragment
        dateInput.setOnClickListener(v -> {
            DatePickerClassesFragment datePickerFragment = new DatePickerClassesFragment(dateInput, "dd/MM/yyyy");
            datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        });

        applyFilterButton.setOnClickListener(v -> {
            String selectedDayOfWeek = dayOfWeekSpinner.getSelectedItem().toString();
            String date = dateInput.getText().toString();

            filterClassesByDateOrDay(selectedDayOfWeek, date);
            filterDialog.dismiss();
        });

        filterDialog.show();
    }

    private void filterClassesByDateOrDay(String dayOfWeek, String date) {
        List<Classes> filteredList = new ArrayList<>();

        boolean isDayOfWeekEmpty = dayOfWeek.equals("Select day");
        boolean isDateEmpty = date.isEmpty();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

        for (Classes classes : classesList) {
            boolean matchesDayOfWeek = isDayOfWeekEmpty;
            boolean matchesDate = isDateEmpty;

            if (!isDayOfWeekEmpty) {
                try {
                    Date classDate = sdf.parse(classes.getDate());
                    String classDayOfWeek = dayFormat.format(classDate);
                    matchesDayOfWeek = classDayOfWeek.equalsIgnoreCase(dayOfWeek);
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            if (!isDateEmpty) {
                matchesDate = classes.getDate().equals(date);
            }
            if (matchesDayOfWeek && matchesDate) {
                filteredList.add(classes);
            }
        }
        classesAdapter.updateData(filteredList);
    }


    private void loadClassesFromDatabase() {
        classesList.clear();
        classesList.addAll(databaseHelper.getAllClasses());
        classesAdapter.notifyDataSetChanged();
    }

    private void filterClassesByInstructor(String query) {
        if (query.isEmpty()){
            loadClassesFromDatabase();
        } else {
            List<Classes> filteredList = new ArrayList<>();
            for (Classes classes : classesList) {
                int instructorId = Integer.parseInt(classes.getInstructor());
                Instructor instructor = databaseHelper.getUserById(instructorId);
                if (instructor != null && instructor.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(classes);
                }
            }
            classesAdapter.updateData(filteredList);
        }
    }

    private void showAddClassDialog() {

        if (!WifiChecker.isWifiConnected(requireContext())) {
            WifiChecker.showWifiDialog(requireContext());
        }

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_class);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText editName = dialog.findViewById(R.id.class_name_input);
        Spinner editCourseId = dialog.findViewById(R.id.class_course_id_input);
        EditText editDate = dialog.findViewById(R.id.class_date_input);
        Spinner editInstructorId = dialog.findViewById(R.id.class_instructor_id_input);
        EditText editComments = dialog.findViewById(R.id.class_comments_input);
        Button buttonSave = dialog.findViewById(R.id.btn_save_class);

        // Load list courses into spinner
        List<Course> courses = databaseHelper.getAllCourses();
        CustomCourseAdapter courseAdapter = new CustomCourseAdapter(getContext(), courses);
        editCourseId.setAdapter(courseAdapter);

        //Load list instructors into spinner
        List<Instructor> instructors = databaseHelper.getAllInstructors();
        List<Instructor> filteredInstructors = new ArrayList<>();
        for (Instructor instructor : instructors) {
            if (instructor.getRoleId() == 3) {
                filteredInstructors.add(instructor);
            }
        }
        CustomInstructorAdapter instructorAdapter = new CustomInstructorAdapter(getContext(), filteredInstructors);
        editInstructorId.setAdapter(instructorAdapter);


        editCourseId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Course selectedCourse = (Course) parent.getItemAtPosition(position);
                String courseDay = selectedCourse.getCourseDay();
                // Set the date picker to only allow valid days
                setDatePickerConstraints(editDate, courseDay);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        buttonSave.setOnClickListener(v -> {
            String name = editName.getText().toString();
            String date = editDate.getText().toString();
            String comments = editComments.getText().toString();

            if (date.isEmpty() || name.isEmpty()) {
                Toast.makeText(getContext(), "Date and Name fields are required!", Toast.LENGTH_SHORT).show();
            } else {
                if (databaseHelper.isClassNameDuplicate(name)) {
                    Toast.makeText(getContext(), "Class name already exists. Please choose a different name.", Toast.LENGTH_SHORT).show();
                } else {
                    String courseId = String.valueOf(((Course) editCourseId.getSelectedItem()).getCourseId());
                    String instructorId = String.valueOf(((Instructor) editInstructorId.getSelectedItem()).getId());
                    addClassInstanceToDatabase(courseId, name, date, instructorId, comments);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void setDatePickerConstraints(EditText editDate, String courseDay) {
        editDate.setOnClickListener(v -> {
            DatePickerFragment datePickerFragment = new DatePickerFragment(editDate, "dd/MM/yyyy", courseDay);
            datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        });
    }

    private void addClassInstanceToDatabase(String courseId, String name, String date, String instructor, String comments) {
        if (date.isEmpty() || name.isEmpty()) {
            Toast.makeText(getContext(), "Date and Name fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        Classes newClass = new Classes(courseId, name, date, instructor, comments);

        boolean isInserted = databaseHelper.insertClassInstance(courseId, name, date, instructor, comments);

        if (isInserted) {
            int classId = databaseHelper.getLastInsertedClassId();
            newClass.setId(classId);
            classesList.add(newClass);
            classesAdapter.notifyItemInserted(classesList.size() - 1);
            Toast.makeText(getContext(), "Class added successfully", Toast.LENGTH_SHORT).show();

            if (WifiChecker.isWifiConnected(getContext())) {
                new Thread(new UploadTaskThread(null, classesList, null, null)).start();
            } else {
                WifiChecker.showWifiDialog(getContext());
            }
        } else {
            Toast.makeText(getContext(), "Failed to add class", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToClassDetail(Classes classes) {
        ClassDetailFragment classDetailFragment = ClassDetailFragment.newInstance(classes);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, classDetailFragment)
                .addToBackStack(null)
                .commit();
    }

}