package com.example.yoga_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.yoga_app.model.Instructor;

import java.util.List;

public class CustomInstructorAdapter extends ArrayAdapter<Instructor> {

    public CustomInstructorAdapter(Context context, List<Instructor> instructors) {
        super(context, android.R.layout.simple_spinner_item, instructors);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = view.findViewById(android.R.id.text1);
        Instructor instructor = getItem(position);
        if (instructor != null) {
            textView.setText(instructor.getName());
        }
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = view.findViewById(android.R.id.text1);
        Instructor instructor = getItem(position);
        if (instructor != null) {
            textView.setText(instructor.getName());
        }
        return view;
    }
}
