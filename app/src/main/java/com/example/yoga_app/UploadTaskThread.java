package com.example.yoga_app;

import android.util.Log;
import com.example.yoga_app.model.Classes;
import com.example.yoga_app.model.Course;
import com.example.yoga_app.model.Instructor;
import com.example.yoga_app.model.Role;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class UploadTaskThread implements Runnable {
    private List<Course> courseList;
    private List<Classes> classesList;
    private List<Role> roleList;
    private List<Instructor> instructorList;
    private DatabaseReference databaseReference;
    private boolean hasNewData = false;
    private boolean isCompleted = false;

    // HashMap to store mapping between ID and Firebase key
    private HashMap<Integer, String> courseIdToKeyMap = new HashMap<>();
    private HashMap<Integer, String> classIdToKeyMap = new HashMap<>();
    private HashMap<Integer, String> roleIdToKeyMap = new HashMap<>();
    private HashMap<Integer, String> instructorIdToKeyMap = new HashMap<>();

    public UploadTaskThread(List<Course> courseList, List<Classes> classesList, List<Role> roleList, List<Instructor> instructorList) {
        this.courseList = courseList;
        this.classesList = classesList;
        this.roleList = roleList;
        this.instructorList = instructorList;

        this.databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://course-work-faee7-default-rtdb.europe-west1.firebasedatabase.app/");
    }

    @Override
    public void run() {
        try{
            if (courseList != null && !courseList.isEmpty()) {
                uploadCourses();
            }
            if (classesList != null && !classesList.isEmpty()) {
                uploadClasses();
            }
            if (roleList != null && !roleList.isEmpty()) {
                uploadRoles();
            }
            if (instructorList != null && !instructorList.isEmpty()) {
                uploadInstructors();
            }
        }finally {
            isCompleted = true;
        }
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    private void uploadCourses() {
        databaseReference.child("courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    try {
                        Object value = courseSnapshot.getValue();
                        Log.d("UploadTaskThread", "Course Key: " + courseSnapshot.getKey() + ", Type: " + (value != null ? value.getClass().getName() : "null") + ", Value: " + value);

                        Course course = courseSnapshot.getValue(Course.class);
                        if (course != null) {
                            courseIdToKeyMap.put(course.getCourseId(), courseSnapshot.getKey());
                        }
                    } catch (Exception e) {
                        Log.e("UploadTaskThread", "Error processing course: " + e.getMessage());
                    }
                }

                for (Course localCourse : courseList) {
                    String courseId = String.valueOf(localCourse.getCourseId());
                    Log.d("UploadTaskThread", "Attempting to upload course with ID: " + courseId);

                    if (!courseIdToKeyMap.containsKey(localCourse.getCourseId())) {
                        hasNewData = true;
                        databaseReference.child("courses").child(courseId).setValue(localCourse)
                                .addOnSuccessListener(aVoid -> Log.d("UploadTaskThread", "Course uploaded successfully: " + localCourse.getName()))
                                .addOnFailureListener(e -> Log.e("UploadTaskThread", "Failed to upload course: " + e.getMessage()));
                    } else {
                        Log.d("UploadTaskThread", "Course already exists with ID: " + courseId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("UploadTaskThread", "Failed to read courses: " + error.getMessage());
            }
        });
    }

    private void uploadClasses() {
        databaseReference.child("classes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot classSnapshot : snapshot.getChildren()) {
                    try {
                        Object value = classSnapshot.getValue();
                        Log.d("UploadTaskThread", "Class Key: " + classSnapshot.getKey() + ", Type: " + (value != null ? value.getClass().getName() : "null") + ", Value: " + value);

                        Classes classItem = classSnapshot.getValue(Classes.class);
                        if (classItem != null) {
                            classIdToKeyMap.put(classItem.getId(), classSnapshot.getKey());
                        }
                    } catch (Exception e) {
                        Log.e("UploadTaskThread", "Error processing class: " + e.getMessage());
                    }
                }

                for (Classes localClass : classesList) {
                    String classId = String.valueOf(localClass.getId());
                    Log.d("UploadTaskThread", "Attempting to upload class with ID: " + classId);

                    if (!classIdToKeyMap.containsKey(localClass.getId())) {
                        hasNewData = true;
                        databaseReference.child("classes").child(classId).setValue(localClass)
                                .addOnSuccessListener(aVoid -> Log.d("UploadTaskThread", "Class uploaded successfully: " + localClass.getName()))
                                .addOnFailureListener(e -> Log.e("UploadTaskThread", "Failed to upload class: " + e.getMessage()));
                    } else {
                        Log.d("UploadTaskThread", "Class already exists with ID: " + classId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("UploadTaskThread", "Failed to read classes: " + error.getMessage());
            }
        });
    }

    private void uploadRoles() {
        databaseReference.child("roles").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot roleSnapshot : snapshot.getChildren()) {
                    try {
                        Object value = roleSnapshot.getValue();
                        Log.d("UploadTaskThread", "Role Key: " + roleSnapshot.getKey() + ", Type: " + (value != null ? value.getClass().getName() : "null") + ", Value: " + value);

                        Role roleItem = roleSnapshot.getValue(Role.class);
                        if (roleItem != null) {
                            roleIdToKeyMap.put(roleItem.getId(), roleSnapshot.getKey());
                        }
                    } catch (Exception e) {
                        Log.e("UploadTaskThread", "Error processing role: " + e.getMessage());
                    }
                }

                for (Role localRole : roleList) {
                    String roleId = String.valueOf(localRole.getId());
                    Log.d("UploadTaskThread", "Attempting to upload role with ID: " + roleId);

                    if (!roleIdToKeyMap.containsKey(localRole.getId())) {
                        hasNewData = true;
                        databaseReference.child("roles").child(roleId).setValue(localRole)
                                .addOnSuccessListener(aVoid -> Log.d("UploadTaskThread", "Role uploaded successfully: " + localRole.getRole_name()))
                                .addOnFailureListener(e -> Log.e("UploadTaskThread", "Failed to upload role: " + e.getMessage()));
                    } else {
                        Log.d("UploadTaskThread", "Role already exists with ID: " + roleId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("UploadTaskThread", "Failed to read roles: " + error.getMessage());
            }
        });
    }

    private void uploadInstructors() {
        databaseReference.child("instructors").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot instructorSnapshot : snapshot.getChildren()) {
                    try {
                        // Get id value from instructorSnapshot
                        Object idValue = instructorSnapshot.child("id").getValue();

                        int id;
                        if (idValue instanceof Long) {
                            id = ((Long) idValue).intValue();
                        } else if (idValue instanceof String) {
                            try {
                                id = Integer.parseInt((String) idValue);
                            } catch (NumberFormatException e) {
                                Log.e("UploadTaskThread", "Invalid Instructor ID format: " + idValue);
                                continue;
                            }
                        } else {
                            Log.e("UploadTaskThread", "Unexpected ID type for Instructor: " + idValue);
                            continue;
                        }

                        // Create Instructor and add to HashMap if there is no error
                        Instructor instructor = instructorSnapshot.getValue(Instructor.class);
                        if (instructor != null) {
                            instructorIdToKeyMap.put(id, instructorSnapshot.getKey()); // Add id after confirmation
                        }
                    } catch (Exception e) {
                        Log.e("UploadTaskThread", "Error processing instructor: " + e.getMessage());
                    }
                }

                for (Instructor localInstructor : instructorList) {
                    String instructorId = String.valueOf(localInstructor.getId());
                    Log.d("UploadTaskThread", "Attempting to upload instructor with ID: " + instructorId);

                    if (!instructorIdToKeyMap.containsKey(localInstructor.getId())) {
                        hasNewData = true;
                        databaseReference.child("instructors").child(instructorId).setValue(localInstructor)
                                .addOnSuccessListener(aVoid -> Log.d("UploadTaskThread", "Instructor uploaded successfully: " + localInstructor.getName()))
                                .addOnFailureListener(e -> Log.e("UploadTaskThread", "Failed to upload instructor: " + e.getMessage()));
                    } else {
                        Log.d("UploadTaskThread", "Instructor already exists with ID: " + instructorId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("UploadTaskThread", "Failed to read instructors: " + error.getMessage());
            }
        });
    }
}