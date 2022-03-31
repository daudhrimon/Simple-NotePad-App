package com.daud.simplenotepad;

public class IdeasModel {
    String Title;
    String Idea;
    String IdeaKey;

    public IdeasModel() {
    }

    public IdeasModel(String title, String idea, String ideaKey) {
        Title = title;
        Idea = idea;
        IdeaKey = ideaKey;
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
}
