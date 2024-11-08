package com.example.yoga_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.yoga_app.model.Course;

import java.util.List;

public class CustomCourseAdapter extends ArrayAdapter<Course> {
    public CustomCourseAdapter(Context context, List<Course> courses) {
        super(context, android.R.layout.simple_spinner_item, courses);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        Course course = getItem(position);
        if (course != null) {
            textView.setText(course.getName() + " - " + course.getCourseDay());
        }
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        Course course = getItem(position);
        if (course != null) {
            textView.setText(course.getName() + " - " + course.getCourseDay());
        }
        return view;
    }
}
