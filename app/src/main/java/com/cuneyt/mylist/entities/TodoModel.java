package com.cuneyt.mylist.entities;

public class TodoModel {
    private String id;
    private String todo;
    private String creationDate;
    private String notification;
    private String sort;
    private String showLetter;

    public TodoModel() {
    }

    public TodoModel(String id, String todo, String creationDate, String notification, String sort, String showLetter) {
        this.id = id;
        this.todo = todo;
        this.creationDate = creationDate;
        this.notification = notification;
        this.sort = sort;
        this.showLetter = showLetter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getShowLetter() {
        return showLetter;
    }

    public void setShowLetter(String showLetter) {
        this.showLetter = showLetter;
    }
}
