package com.example.test_sel.Classes;

import java.io.Serializable;
import java.util.HashMap;

public class Course implements Serializable, CardInfo {
    private String courseCode;
    private String courseName;
    private String usersCounter = "0";
    private HashMap<String, String> users = new HashMap<String, String>();
    private HashMap<String, CoursePerUser> usersList = new HashMap<String, CoursePerUser>();

    final public static String KIND = "course";

    public Course() {

    }

    public Course(String courseCode, String courseName) {
        this.courseCode = courseCode;
        this.courseName = courseName;
    }

    public HashMap<String, CoursePerUser> getUsersList() {
        return usersList;
    }

    public void setUsersList(HashMap<String, CoursePerUser> usersList) {
        this.usersList = usersList;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getUsersCounter() {
        return usersCounter;
    }

    public void setUsersCounter(String usersCounter) {
        this.usersCounter = usersCounter;
    }

    public HashMap<String, String> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, String> users) {
        this.users = users;
    }

    @Override
    public String Title() {
        return "";

    }

    @Override
    public String FirstRow() {
    return  getCourseName();
    }

    @Override
    public String SecondRow() {
        return  getCourseCode();
    }

    @Override
    public String ThirdRow() {
        return  getUsersCounter();
    }

    @Override
    public String LastRow() {
        return "";
    }

    @Override
    public String Image() {
        return "";
//        getImage();
    }

    @Override
    public String kind() {
        return KIND;
    }
}
