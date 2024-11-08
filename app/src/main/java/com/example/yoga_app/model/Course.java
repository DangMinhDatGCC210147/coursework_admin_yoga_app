package com.example.yoga_app.model;

import java.io.Serializable;

public class Course implements Serializable {
    private int courseId;
    private String name;
    private String type;
    private String price;
    private String duration;
    private String capacity;
    private String description;
    private String courseDay;
    private String courseTime;

    public Course() {

    }

    public Course(String name, String type, String price, String duration, String capacity, String description, String courseDay, String courseTime) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.duration = duration;
        this.capacity = capacity;
        this.description = description != null ? description : "No description available";
        this.courseDay = courseDay;
        this.courseTime = courseTime;
    }

    public Course(int courseId, String name, String type, String price, String duration, String capacity, String description, String courseDay, String courseTime) {
        this.courseId = courseId;
        this.name = name;
        this.type = type;
        this.price = price;
        this.duration = duration;
        this.capacity = capacity;
        this.description = description;
        this.courseDay = courseDay;
        this.courseTime = courseTime;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCourseDay() {
        return courseDay;
    }

    public void setCourseDay(String courseDay) {
        this.courseDay = courseDay;
    }

    public String getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(String courseTime) {
        this.courseTime = courseTime;
    }
}
