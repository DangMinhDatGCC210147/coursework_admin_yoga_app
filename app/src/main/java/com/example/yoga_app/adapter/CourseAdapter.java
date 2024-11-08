package com.example.yoga_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yoga_app.R;
import com.example.yoga_app.model.Course;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private OnCourseClickListener courseClickListener;

    public CourseAdapter(List<Course> courseList, OnCourseClickListener listener) {
        this.courseList = courseList;
        this.courseClickListener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {

        Course course = courseList.get(position);
        holder.courseDay.setText(course.getCourseDay());
        holder.courseName.setText(course.getName());
        holder.coursePrice.setText(course.getPrice());
        holder.courseCapacity.setText(String.valueOf(course.getCapacity()));
        holder.courseDescription.setText(course.getDescription());

        holder.itemView.setOnClickListener(v -> courseClickListener.onCourseClick(course));
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, coursePrice, courseCapacity, courseDay, courseDescription;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseDescription = itemView.findViewById(R.id.course_description);
            courseDay = itemView.findViewById(R.id.course_day);
            courseName = itemView.findViewById(R.id.course_name);
            coursePrice = itemView.findViewById(R.id.course_price);
            courseCapacity = itemView.findViewById(R.id.course_capacity);
        }
    }
}
