package com.example.yoga_app.model;

import java.io.Serializable;

public class Classes implements Serializable {
    private int id;
    private String courseId;
    private String name;
    private String date;
    private String instructor;
    private String comments;

    public Classes(){

    }

    public Classes(String courseId, String name, String date, String instructor, String comments) {
        this.courseId = courseId;
        this.name = name;
        this.date = date;
        this.instructor = instructor;
        this.comments = comments;
    }

    public Classes(int id, String courseId, String name, String date, String instructor, String comments) {
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.date = date;
        this.instructor = instructor;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
