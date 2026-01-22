package org.example;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.io.Serializable;

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
        state = TaskState.INCOMPLETE; // default state of a task
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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

    public boolean removeSubtask(int id) {
        if(subtasks.isEmpty())
            return false;

        if(subtasks.get(id) == null)
            return false;
        subtasks.remove(id);
        return true;
    }

    public Task getSubtask(int id) {
        if(subtasks == null)
            return null;

        if(subtasks.containsKey(id))
            return subtasks.get(id);
        return null;
    }

    public Set<Integer> getSubtasks() {
        return subtasks.keySet();
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

    public TaskState getTaskState() {
        return state;
    }

    public void markTaskDone() {
        this.state = TaskState.COMPLETED;
    }

    public boolean markSubtaskDone(int id) {
        if(subtasks.get(id) == null)
            return false;
        subtasks.get(id).state = TaskState.COMPLETED;
        return true;
    }

    public boolean hasSubtasks() {
        if(subtasks == null)
            return false;
        if(subtasks.isEmpty())
            return false;

        return true;
    }

    public boolean hasUndoneSubtasks() {
        if(subtasks == null) // if no subtasks are available
            return false;
        if(subtasks.isEmpty())
            return false;

        Set<Integer> ids = subtasks.keySet();

        for(int id : ids)
            if(subtasks.get(id).state == TaskState.INCOMPLETE)
                return true;
        return false;
    }

    @Override
    public String toString() {
        return id + " " + name + " " + dueDate;
    }
}
