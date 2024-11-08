package com.example.yoga_app.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yoga_app.R;
import com.example.yoga_app.WifiChecker;
import com.example.yoga_app.adapter.ClassesAdapter;
import com.example.yoga_app.model.Classes;
import com.example.yoga_app.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {
    private CalendarView calendarView;
    private RecyclerView classesRecyclerView;
    private ClassesAdapter classAdapter;
    private List<Classes> classList;
    private DatabaseHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        if (!WifiChecker.isWifiConnected(requireContext())) {
            WifiChecker.showWifiDialog(requireContext());
        }

        // Khởi tạo DatabaseHelper
        db = new DatabaseHelper(getContext());

        calendarView = view.findViewById(R.id.calendarView);
        classesRecyclerView = view.findViewById(R.id.classes_recycler_view);
        classList = new ArrayList<>();

        // Thiết lập RecyclerView
        classesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        classAdapter = new ClassesAdapter(classList, classes -> {
            ClassDetailFragment classDetailFragment = ClassDetailFragment.newInstance(classes);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, classDetailFragment)
                    .addToBackStack(null)
                    .commit();
        }, getContext());
        classesRecyclerView.setAdapter(classAdapter);

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
            loadClassesForDate(selectedDate);
        });

        return view;
    }

    private void loadClassesForDate(String date) {
//        Log.d("ScheduleFragment", "Selected date: " + date); // Log selected date
        List<Classes> classesForDate = db.getClassesByDate(date);
        classList.clear();
        classList.addAll(classesForDate);

        if (classList.isEmpty()) {
            Toast.makeText(getContext(), "No classes found for the selected date.", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("ScheduleFragment", "Classes found: " + classList.size()); // Log number of classes found
        }
        classAdapter.notifyDataSetChanged();
    }
}