package org.example;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Task implements Serializable {
    private int id;
    private String name;
    private String dueDate;
    private TaskType type;
    private boolean subtasksExist;
    private List<Task> subtasks;


    public Task() {
        id = -1;
        name = null;
        dueDate = null;
        type = null;
        subtasks = null;
        subtasksExist = false;
    }

    public Task(int id, String name, String dueDate, TaskType type) {
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        this.type = type;
        subtasks = null;
        subtasksExist = false;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addSubtask(Task subtask) {
        if(!subtasksExist) {
            subtasks = new ArrayList<>();
            subtasksExist = true;
        }

        subtasks.add(subtask);
    }

    public int getId() {
        return id;
    }

    public TaskType getTaskType() {
        return type;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return id + " " + name + " " + dueDate;
    }
}
