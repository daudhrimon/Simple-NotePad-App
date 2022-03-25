package com.daud.simplenotepad;

public class IdeasModel {
    String Title;
    String Idea;
    String Key;

    public IdeasModel() {
    }

    public IdeasModel(String title, String idea, String key) {
        Title = title;
        Idea = idea;
        Key = key;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getIdea() {
        return Idea;
    }

    public void setIdea(String idea) {
        Idea = idea;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }
}
