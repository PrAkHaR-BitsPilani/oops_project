package com.example.oops_project;

public class ToDoModel {
    // id is the database id, status is bool checked or unchecked
    private int id, status;
    // task is the text written in checkbox
    private String task;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}

