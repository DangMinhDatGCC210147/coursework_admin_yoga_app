package com.example.yoga_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yoga_app.DatabaseHelper;
import com.example.yoga_app.R;
import com.example.yoga_app.model.Classes;
import com.example.yoga_app.model.Instructor;

import java.util.List;

public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ClassesViewHolder> {

    private List<Classes> classesList;
    private OnClassesClickListener classesClickListener;
    private DatabaseHelper databaseHelper;

    public ClassesAdapter(List<Classes> classesList, OnClassesClickListener listener, Context context) {
        this.classesList = classesList;
        this.classesClickListener = listener;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ClassesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item, parent, false);
        return new ClassesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassesViewHolder holder, int position) {
        Classes classes = classesList.get(position);
        holder.clasName.setText(classes.getName());
        holder.classDate.setText(classes.getDate());

        int instructorId = Integer.parseInt(classes.getInstructor());
        Instructor instructor = databaseHelper.getUserById(instructorId);
        String instructorName = (instructor != null) ? instructor.getName() : "N/A";
        holder.classInstructor.setText(instructorName);

        holder.itemView.setOnClickListener(v -> classesClickListener.onClassesClick(classes));
    }

    @Override
    public int getItemCount() {
        return classesList.size();
    }

    public void updateData(List<Classes> newClassesList) {
        classesList.clear();
        classesList.addAll(newClassesList);
        notifyDataSetChanged();
    }

    public interface OnClassesClickListener {
        void onClassesClick(Classes classes);
    }

    public static class ClassesViewHolder extends RecyclerView.ViewHolder {
        TextView clasName, classDate, classInstructor;

        public ClassesViewHolder(@NonNull View itemView) {
            super(itemView);
            clasName = itemView.findViewById(R.id.class_name);
            classDate = itemView.findViewById(R.id.class_date);
            classInstructor = itemView.findViewById(R.id.class_instructor);
        }
    }
}
