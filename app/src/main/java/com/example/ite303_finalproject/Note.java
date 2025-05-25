package com.example.ite303_finalproject;

public class Note {


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private String note_title;
    private String note_description;
    private String priority;
    public void setNote_title(String note_title) {
        this.note_title = note_title;
    }

    public void setNote_description(String note_description) {
        this.note_description = note_description;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }



    public Note(int id,String note_title, String note_description, String priority) {
        this.note_title = note_title;
        this.note_description = note_description;
        this.priority = priority;
        this.id = id;

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
