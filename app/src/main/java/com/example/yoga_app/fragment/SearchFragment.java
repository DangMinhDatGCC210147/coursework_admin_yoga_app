package com.example.yoga_app.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.yoga_app.DatabaseHelper;
import com.example.yoga_app.R;
import com.example.yoga_app.WifiChecker;
import com.example.yoga_app.adapter.SearchAdapter;
import com.example.yoga_app.model.Classes;
import com.example.yoga_app.model.Course;
import com.example.yoga_app.model.Instructor;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private SearchAdapter searchAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        if (!WifiChecker.isWifiConnected(requireContext())) {
            WifiChecker.showWifiDialog(requireContext());
        }

        databaseHelper = new DatabaseHelper(getContext());

        Spinner searchConditionSpinner = view.findViewById(R.id.search_condition_spinner);
        EditText searchInput = view.findViewById(R.id.search_input);
        Button btnSearch = view.findViewById(R.id.btn_search);
        RecyclerView recyclerViewResults = view.findViewById(R.id.recycler_view_results);

        recyclerViewResults.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter = new SearchAdapter(new ArrayList<>(), this::navigateToDetail, databaseHelper);
        recyclerViewResults.setAdapter(searchAdapter);

        btnSearch.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString();
            int selectedCondition = searchConditionSpinner.getSelectedItemPosition();

            if (searchTerm.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a search term", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Object> searchResults = new ArrayList<>();

            switch (selectedCondition) {
                case 0: // By Class Name
                    List<Classes> classResults = databaseHelper.searchClassesByName(searchTerm);
                    searchResults.addAll(classResults);
                    break;
                case 1: // By Course Name
                    List<Course> courseResults = databaseHelper.searchCoursesByName(searchTerm);
                    searchResults.addAll(courseResults);
                    break;
                case 2: // By Instructor Name
                    List<Instructor> instructorResults = databaseHelper.searchInstructorsByName(searchTerm);
                    searchResults.addAll(instructorResults);
                    break;
                case 3: // By Instructor Class
                    List<Classes> classByInstructorResults = databaseHelper.searchClassesByInstructor(searchTerm);
                    searchResults.addAll(classByInstructorResults);
                    break;
                case 4: // By Date
                    List<Classes> classByDateResults = databaseHelper.getClassesByDate(searchTerm);
                    searchResults.addAll(classByDateResults);
                    break;
            }

            if (searchResults.isEmpty()) {
                Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
            } else {
                hideKeyboard();
                searchAdapter.updateData(searchResults);
            }
        });
        return view;
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }

    private void navigateToDetail(Object item, SearchAdapter.Type type) {
        switch (type) {
            case CLASS:
                Classes classItem = (Classes) item;
                ClassDetailFragment classDetailFragment = ClassDetailFragment.newInstance(classItem);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, classDetailFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case COURSE:
                Course courseItem = (Course) item;
                CourseDetailFragment courseDetailFragment = CourseDetailFragment.newInstance(courseItem);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, courseDetailFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case INSTRUCTOR:
                break;
        }
    }
}