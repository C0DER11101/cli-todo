package org.example;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.io.Serializable;
import java.time.LocalDate;

public class Task implements Serializable {
    private int id;
    private String name;
    private String dueDate;
    private TaskState state;
    private TaskType type;
    private boolean subtasksExist;
    private Map<Integer, Task> subtasks;

    private final String[] COLORS = {
            "\033[91m", // RED: today's the deadline or it's just 2 days away or the task has crossed the deadline
            "\033[96m", // CYAN: deadline's more than a week away
            "\033[92m", // GREEN: task has been completed
            "\033[39m" // DEFAULT
    };

    public Task(int id, String name, String dueDate, TaskType type) {
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        this.type = type;
        subtasks = null;
        subtasksExist = false;
        state = TaskState.INCOMPLETE; // default state of a task
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

    /*
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
    */

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

    // this method is for color-based display
    public TaskState getTaskState() {
        return state;
    }

    public void markTaskDone() {
        this.state = TaskState.COMPLETED;
    }

    public void unMarkTask() {
        this.state = TaskState.INCOMPLETE;
    }

    public boolean markSubtaskDone(int id) {
        if(subtasks.get(id) == null)
            return false;
        subtasks.get(id).state = TaskState.COMPLETED;
        return true;
    }

    public boolean unMarkSubtask(int id) {
        if(subtasks.get(id) == null)
            return false;
        subtasks.get(id).state = TaskState.INCOMPLETE;
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

    private String colorizeTask() {
        String[] dateComponents = dueDate.trim().split("[-/]");
        int year = Integer.parseInt(dateComponents[2]);
        int month = Integer.parseInt(dateComponents[1]);
        int day = Integer.parseInt(dateComponents[0]);

        LocalDate currentDate = LocalDate.now();

        if(state == TaskState.INCOMPLETE) {
            if (year == currentDate.getYear()) {
                if (month == currentDate.getMonthValue()) {
                    if (day - currentDate.getDayOfMonth() > 2)
                        return COLORS[1];
                    else
                        return COLORS[0];
                } else if (month > currentDate.getMonthValue()) {
                    return COLORS[1];
                } else {
                    return COLORS[0];
                }
            } else if (year > currentDate.getYear()) {
                return COLORS[1];
            } else {
                return COLORS[0];
            }
        }

        return COLORS[2];
    }

    @Override
    public String toString() {
        return colorizeTask() + id + ": " + name + " " + dueDate + COLORS[3];
    }
}
