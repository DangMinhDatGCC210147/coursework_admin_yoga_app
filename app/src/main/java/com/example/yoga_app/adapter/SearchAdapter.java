package com.example.yoga_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yoga_app.DatabaseHelper;
import com.example.yoga_app.R;
import com.example.yoga_app.model.Classes;
import com.example.yoga_app.model.Course;
import com.example.yoga_app.model.Instructor;
import com.example.yoga_app.model.Role;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> items;
    private OnItemClickListener listener;
    private DatabaseHelper databaseHelper;

    public enum Type {
        CLASS, COURSE, INSTRUCTOR
    }

    public SearchAdapter(List<Object> items, OnItemClickListener listener, DatabaseHelper db) {
        this.items = items;
        this.listener = listener;
        this.databaseHelper = db;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Classes) {
            return Type.CLASS.ordinal(); // CLASS
        } else if (items.get(position) instanceof Course) {
            return Type.COURSE.ordinal(); // COURSE
        } else if (items.get(position) instanceof Instructor) {
            return Type.INSTRUCTOR.ordinal(); // INSTRUCTOR
        }
        return -1; // Invalid
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0: // CLASS
                View classView = inflater.inflate(R.layout.class_item, parent, false);
                return new ClassViewHolder(classView);
            case 1: // COURSE
                View courseView = inflater.inflate(R.layout.course_item, parent, false);
                return new CourseViewHolder(courseView);
            case 2: // INSTRUCTOR
                View instructorView = inflater.inflate(R.layout.instructor_item, parent, false);
                return new InstructorViewHolder(instructorView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);

        holder.itemView.setOnClickListener(v -> {
            int viewType = getItemViewType(position);
            Type type = Type.values()[viewType];
            listener.onItemClick(item, type);
        });

        if (holder instanceof ClassViewHolder) {
            Classes classes = (Classes) item;
            ClassViewHolder classHolder = (ClassViewHolder) holder;
            classHolder.bind(classes, databaseHelper);
        } else if (holder instanceof CourseViewHolder) {
            Course course = (Course) item;
            CourseViewHolder courseHolder = (CourseViewHolder) holder;
            courseHolder.bind(course);
        } else if (holder instanceof InstructorViewHolder) {
            Instructor instructor = (Instructor) item;
            InstructorViewHolder instructorHolder = (InstructorViewHolder) holder;
            instructorHolder.bind(instructor, databaseHelper);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateData(List<Object> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Object item, Type type);
    }

    static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView className, classDate, classInstructor;

        ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.class_name);
            classDate = itemView.findViewById(R.id.class_date);
            classInstructor = itemView.findViewById(R.id.class_instructor);
        }

        void bind(Classes classes, DatabaseHelper db) {
            className.setText(classes.getName());
            classDate.setText(classes.getDate());

            // Lấy tên giảng viên từ ID
            int instructorId = Integer.parseInt(classes.getInstructor());
            Instructor instructor = db.getUserById(instructorId);

            String instructorName = (instructor != null) ? instructor.getName() : "N/A";
            classInstructor.setText(instructorName);
        }
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, courseDay, coursePrice, courseCapacity, courseDescription;

        CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);
            courseDay = itemView.findViewById(R.id.course_day);
            coursePrice = itemView.findViewById(R.id.course_price);
            courseCapacity = itemView.findViewById(R.id.course_capacity);
            courseDescription = itemView.findViewById(R.id.course_description);
        }

        void bind(Course course) {
            courseName.setText(course.getName());
            courseDay.setText(course.getCourseDay());
            coursePrice.setText(course.getPrice());
            courseCapacity.setText(String.valueOf(course.getCapacity()));
            courseDescription.setText(course.getDescription());
        }
    }

    static class InstructorViewHolder extends RecyclerView.ViewHolder {
        TextView instructorName, instructorEmail, instructorRole;
        Button btnUpdate, btnDelete;
        InstructorViewHolder(@NonNull View itemView) {
            super(itemView);
            instructorName = itemView.findViewById(R.id.instructor_name);
            instructorEmail = itemView.findViewById(R.id.instructor_email);
            instructorRole = itemView.findViewById(R.id.instructor_role);
            btnUpdate = itemView.findViewById(R.id.btn_update_instructor);
            btnDelete = itemView.findViewById(R.id.btn_delete_instructor);

            btnUpdate.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        }

        void bind(Instructor instructor, DatabaseHelper db) {
            instructorName.setText(instructor.getName());
            instructorEmail.setText(instructor.getEmail());

            Role role = db.getRoleById(instructor.getRoleId());
            String roleName = role != null ? role.getRole_name() : "N/A";
            instructorRole.setText(roleName);
        }
    }
}
