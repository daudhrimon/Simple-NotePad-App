package com.daud.simplenotepad;

public class IdeasModel {
    private String Title;
    private String Idea;
    private String IdeaKey;
    private int Status;
    private int Color;

    public IdeasModel() {
    }

    public IdeasModel(String title, String idea, String ideaKey, int status, int color) {
        Title = title;
        Idea = idea;
        IdeaKey = ideaKey;
        Status = status;
        Color = color;
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

    public String getIdeaKey() {
        return IdeaKey;
    }

    public void setIdeaKey(String ideaKey) {
        IdeaKey = ideaKey;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }
}
