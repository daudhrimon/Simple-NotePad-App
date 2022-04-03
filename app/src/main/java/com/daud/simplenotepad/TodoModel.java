package com.daud.simplenotepad;

public class TodoModel {
    private int Status;
    private String Todo;
    private String TodoKey;

    public TodoModel() {
    }

    public TodoModel(String todo, String todoKey, int status) {
        Todo = todo;
        TodoKey = todoKey;
        Status = status;
    }

    public String getTodo() {
        return Todo;
    }

    public void setTodo(String todo) {
        Todo = todo;
    }

    public String getTodoKey() {
        return TodoKey;
    }

    public void setTodoKey(String todoKey) {
        TodoKey = todoKey;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
