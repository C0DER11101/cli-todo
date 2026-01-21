package org.example;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

public class Task implements Serializable {
    private int id;
    private String name;
    private String dueDate;
    private TaskState state;
    private TaskType type;
    private boolean subtasksExist;
    private Map<Integer, Task> subtasks;


    public Task() {
        id = -1;
        name = null;
        dueDate = null;
        type = null;
        subtasks = null;
        subtasksExist = false;
        state = TaskState.NIL;
    }

    public Task(int id, String name, String dueDate, TaskType type) {
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        this.type = type;
        subtasks = null;
        subtasksExist = false;
        state = TaskState.INCOMPLETE;
    }

    public void addSubtask(int id, String name, String dueDate, TaskType type) {
        if(!subtasksExist) {
            subtasks = new HashMap<>();
            subtasksExist = true;
        }

        if(subtasks.containsKey(id)) {
            System.out.println("A task with ID " + id + " already exists. Please use a different task ID.");
            return;
        }

        subtasks.put(id, new Task(id, name, dueDate, type));
    }

    public void addSubtask(Task subtask) {
        if(!subtasksExist) {
            subtasks = new HashMap<>();
            subtasksExist = true;
        }

        if(subtasks.containsKey(id)) {
            System.out.println("A task with ID " + id + " already exists. Please use a different task ID:");
            return;
        }

        subtasks.put(subtask.id, subtask);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Task getSubtask(int id) {
        if(subtasks.containsKey(id))
            return subtasks.get(id);
        return null;
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
