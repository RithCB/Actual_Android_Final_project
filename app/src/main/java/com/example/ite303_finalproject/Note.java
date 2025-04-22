package com.example.ite303_finalproject;

public class Note {
    private String note_title;
    private String note_description;
    private String priority;

    public Note(String note_title, String note_description, String priority) {
        this.note_title = note_title;
        this.note_description = note_description;
        this.priority = priority;
    }

    public String getNote_title() {
        return note_title;
    }

    public String getNote_description() {
        return note_description;
    }

    public String getPriority() {
        return priority;
    }
}
