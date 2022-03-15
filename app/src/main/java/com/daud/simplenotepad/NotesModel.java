package com.daud.simplenotepad;

public class NotesModel {
    String Title;
    String Note;
    String Key;

    public NotesModel(String title, String note, String key) {
        Title = title;
        Note = note;
        Key = key;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }
}
