package com.daud.simplenotepad;

public class IdeasModel {
    private String Title;
    private String Idea;
    private String IdeaKey;
    private int Status;

    public IdeasModel() {
    }

    public IdeasModel(String title, String idea, String ideaKey, int status) {
        Title = title;
        Idea = idea;
        IdeaKey = ideaKey;
        Status = status;
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
}
