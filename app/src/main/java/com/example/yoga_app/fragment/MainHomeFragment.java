package com.example.yoga_app.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.yoga_app.R;
import com.example.yoga_app.WifiChecker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainHomeFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);

        ImageView manageCourse = (ImageView) view.findViewById(R.id.imageView);
        ImageView manageClass = (ImageView) view.findViewById(R.id.imageView2);
        ImageView viewSchedule = (ImageView) view.findViewById(R.id.imageView4);
        ImageView manageInstructor = (ImageView) view.findViewById(R.id.imageView5);
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);

        if (!WifiChecker.isWifiConnected(requireContext())) {
            WifiChecker.showWifiDialog(requireContext());
        }else{

        }

        manageCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment courseManagementFragment = new CourseManagementFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, courseManagementFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        manageClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment classManagementFragment = new ClassManagementFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, classManagementFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        viewSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment scheduleFragment = new ScheduleFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, scheduleFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        manageInstructor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment instructorManagementFragment = new InstructorManagementFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, instructorManagementFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}