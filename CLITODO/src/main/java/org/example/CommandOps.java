package org.example;

import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
/*
TODO: isValidCommand() {write code for 'delete', 'tick' and 'edit'}
TODO: isValidCommand() shoud return some status codes instead of returning boolean
 */

/**
 * This class contains methods to validate and execute the commands entered by the user
 */
public class CommandOps {

    // some regexes
    private final String SPACE_SEP = "[ \t\n]+";
    private final String WORDS = "[a-zA-Z]+";
    private final String NUMBERS = "[0-9]+";
    private final String ALPHANUMERIC = "[a-zA-Z0-9]+";
    private final String PROJECT_REGEX = "#" + ALPHANUMERIC + "[-_]" + ALPHANUMERIC + ":" + NUMBERS;
    private final String SUBTASK_REGEX = "#" + NUMBERS + ":" + "#" + NUMBERS;
    private final String TASK_REGEX = "#" + NUMBERS;
    private final String DATE_REGEX = "([0-2][0-9]|[3][01])[-/]([01][0-2])[-/]([1-9][0-9]{3})";
    private final DateTimeFormatter formatter; // for formatting the dates in a particular pattern
    private Map<Integer, Task> tasks; // list of tasks

    // for the dates
    private String dueDate; // end date

    private FileOps taskReaderWriter; // for reading/writing tasks from/to the tasks file

    private enum DateState {
        VALID,
        INVALID,
        NOTADDED;
    }

    // predefined commands and specifiers
    private final String[] COMMANDS = {
            "create",
            "show-tasks",
            "delete",
            "tick", // tick mark a task as done
            "edit" // edit a task/subtask/project -- maybe change the due date
    };

    private final String[] SPECIFIERS = {
            "project",
            "subtask",
            "task"
    };

    public CommandOps() {
        tasks = new HashMap<>();
        taskReaderWriter = new FileOps();
        formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
    }

    /**
     * Checks whether the given command is valid or not
     * @param command
     * @return boolean
     */
    public boolean isValidCommand(String command) {
        // first validate the prefix
        String prefix = command.trim().split(SPACE_SEP)[0];
        if(isValidPrefix((prefix))) {
            // next validate the specifier
            if (prefix.equals(COMMANDS[0])) { // 'create'
                // retrieve the specifier
                String specifier = command.trim().split(SPACE_SEP)[1];
                if (isValidSpecifier(specifier)) {
                    // now validate the regex for the specifier
                    String specifierRegex = command.trim().split(SPACE_SEP)[2];
                    if (isValidSpecifierRegex(specifierRegex, prefix, specifier)) {
                        // now extract the id(s) from the specifier-regex
                        String ids = extractTaskID(specifierRegex, prefix, specifier);

                        // next, get the task body/name
                        String taskBody = getTaskBody(command.split(SPACE_SEP));

                        // now validate the dates
                        DateState dateState = isValidDate(command);
                        if (dateState == DateState.INVALID || dateState == DateState.NOTADDED) {
                            // consider the start and end date to be the day when the task was created
                            LocalDate currentDate = LocalDate.now();
                            dueDate = formatter.format(currentDate);
                        }

                        // if the ids contain a hyphen then
                        if(ids.contains("-")) { // a subtask was created
                            // retrieve project id and the subtask id
                            int projectId = Integer.parseInt(ids.trim().split("-")[0]);
                            int subtaskId = Integer.parseInt(ids.trim().split("-")[1]);
                            if(tasks.get(projectId) == null) {
                                System.out.println("A project must exist in order to create a subtask");
                                return false;
                            } else if(tasks.get(projectId).getSubtask(subtaskId) != null) { // if this project already has a subtask with the same id
                                System.out.println("A subtask with this id already exists...");
                                return false;
                            }

                            // add the subtask
                            tasks.get(projectId).addSubtask(subtaskId, taskBody, dueDate, TaskType.SUBTASK);
                        } else { // if a project/task was created
                            // code here
                            int id = Integer.parseInt(ids);
                            if(tasks.get(id) == null) {
                                // add the task
                                tasks.put(id, new Task(id, taskBody, dueDate, specifier.equals(SPECIFIERS[0]) ? TaskType.PROJECT : TaskType.NORMAL));
                            }
                        }
                        return true;
                    }
                }
            } else if(prefix.equals(COMMANDS[1])) { // for the 'show-tasks' command
                // TODO: display the tasks in directory-tree format
            } else if(prefix.equals(COMMANDS[2])) { // for the 'delete' command
                // validate the specifiers
                String specifier = command.trim().split(SPACE_SEP)[1];
                if(isValidSpecifier(specifier)) {
                    // now validate the specifier regex
                    String specifierRegex = command.trim().split(SPACE_SEP)[2];
                    if(isValidSpecifierRegex(specifierRegex, prefix, specifier)) {
                        // extract the task id
                        String ids = extractTaskID(specifierRegex, prefix, specifier);

                        if(ids.contains("-")) { // subtask needs to be deleted
                            // search for the subtask and delete it
                            int projectId = Integer.parseInt(ids.trim().split("-")[0]);
                            int subtaskId = Integer.parseInt(ids.trim().split("-")[1]);

                            if(tasks.get(projectId) == null) {
                                System.out.println("Cannot delete subtask of a non-existent project");
                                return false;
                            }

                            if(tasks.get(projectId).removeSubtask(subtaskId))
                                System.out.println("Subtask removed successfully");
                            else
                                System.out.println("Subtask not found");
                            return true;
                        }
                        // search for the project/task and delete it
                        int id = Integer.parseInt(ids);
                        Task target = tasks.get(id);
                        if(target.getTaskType() == TaskType.PROJECT) {
                            // check if it has any incomplete subtasks
                            if(!target.hasUndoneSubtasks())
                                tasks.remove(id);
                            else
                                System.out.println("This project has incomplete subtasks, not deleting it");
                        } else {
                            tasks.remove(id);
                        }
                        return true;
                    }
                }
            } else if(prefix.equals(COMMANDS[3])) { // the 'tick' command
                // validate the specifier
                String specifier = command.trim().split(SPACE_SEP)[1];
                if(isValidSpecifier(specifier)) {
                    // validate the specifier-regex
                    String specifierRegex = command.trim().split(SPACE_SEP)[2];
                    if(isValidSpecifierRegex(specifierRegex, prefix, specifier)) {
                        // extract the id
                        String ids = extractTaskID(specifierRegex, prefix, specifier);
                        if(ids.contains("-")) {
                            // TODO: search for the subtask and mark it as done
                            int projectId = Integer.parseInt(ids.trim().split("-")[0]);
                            int subtaskId = Integer.parseInt(ids.trim().split("-")[1]);

                            if(tasks.get(projectId) == null) {
                                System.out.println("Cannot tick subtask of a non-existent project");
                                return false;
                            }

                            if(tasks.get(projectId).markSubtaskDone(subtaskId))
                                System.out.println("Subtask marked as 'done'");
                            else
                                System.out.println("Subtask not found");
                            return true;
                        }

                        // TODO: search for the project/task and mark it as done
                        int id = Integer.parseInt(ids);
                        Task target = tasks.get(id);
                        if(target.getTaskType() == TaskType.PROJECT) {
                            // check if it has any incomplete subtasks
                            if(!target.hasUndoneSubtasks())
                                // TODO: mark this project as done
                            else
                                System.out.println("This project has incomplete subtasks, not marking it 'done'");
                        } else {
                            // TODO: mark this task as done
                        }
                    }
                }
            } else if(prefix.equals(COMMANDS[4])) { // the 'edit' command
                // TODO: search for the project/subtask/task and then prompt the user to enter a new name for the task
            }
        }
        return false;
    } /* ENDOF isValidCommand */

    /**
     * Checks whether the prefix of the command is valid or not
     * @param prefix
     * @return boolean
     */
    private boolean isValidPrefix(String prefix) {
        for(String predefinedPrefix : COMMANDS)
            if(prefix.equals(predefinedPrefix))
                return true;
        return false;
    } /* ENDOF isValidPrefix */

    /**
     * Checks whether the specifier is valid or not
     * @param specifier
     * @return boolean
     */
    private boolean isValidSpecifier(String specifier) {
        for(String predefinedSpecifier : SPECIFIERS)
            if(specifier.equals(predefinedSpecifier))
                return true;
        return false;
    } /* ENDOF isValidSpecifier */

    /**
     * Checks whether the regular expression entered for the specifier (except 'task') is valid or not
     * @param regex
     * @param prefix
     * @param specifier
     * @return boolean
     */
    private boolean isValidSpecifierRegex(String regex, String prefix, String specifier) {

        // use the prefix and the specifier to decide how to split 'regex'

        // for the 'create' command
        if(prefix.equals(COMMANDS[1])) {
            // splitting for the 'project' specifier
            if(specifier.equals(SPECIFIERS[0])) {
                String[] components = regex.trim().split("[-_]"); // split by hyphen or underscore

                if (components.length == 1 && components[0].matches(PROJECT_REGEX))
                    return true;
                for (int i = 0; i < components.length; i++)
                    if (!(components[i].matches(WORDS) || components[i].matches(NUMBERS)))
                        return false;
            } else if(specifier.equals(SPECIFIERS[1])) { // for the 'subtask' specifier
                if(!regex.matches(SUBTASK_REGEX))
                    return false;
            } else if(specifier.equals(SPECIFIERS[2])) { // for the 'task' specifier
                if(!regex.matches(TASK_REGEX))
                    return false;
            }
        }
        return true;
    } /* ENDOF isValidSpecifierRegex */

    /**
     * Checks whether any dates have been entered or not and if so whether they are valid or not
     * @return one of the three predefined states of date
     */
    private DateState isValidDate(String command) {
        String[] components = command.trim().split(":"); // split by colon

        // split the date into its individual components (i.e., day, month and year)
        String[] dueDate = components[components.length - 1].trim().split("[-/]");

        LocalDate currentDate = LocalDate.now(); // get the current date

        // date has been entered
        if(dueDate.length == 3) {
            // check whether the entered date is valid or not
            // check if the date matches the DATE_REGEX
            if(components[components.length - 1].trim().matches(DATE_REGEX)) {
                // check the years
                int year = Integer.valueOf(dueDate[2]);

                // if the year of the due-date is less than the current year
                if(!(year >= currentDate.getYear()))
                    return DateState.INVALID;
                return DateState.VALID;
            } else { // if the dates don't match the DATE_REGEX
                return DateState.INVALID;
            }
        }
        return DateState.NOTADDED; // if no dates are added
    } /* ENDOF isValidDate */

    /**
     * Returns the task body i.e. the name of the task.
     * @param commandComponents
     * @return
     */
    private String getTaskBody(String[] commandComponents) {
        StringBuilder body = new StringBuilder();
        if(commandComponents[1].equals(SPECIFIERS[2])) { // for a normal task
            for(int i = 2; i < commandComponents.length && commandComponents[i].charAt(0) != ':'; i++)
                body.append(commandComponents[i]);
        } else { // for project or a subtask
            for(int i = 3; i < commandComponents.length && commandComponents[i].charAt(0) != ':'; i++)
                body.append(commandComponents[i]);
        }
        return body.toString();
    } /*ENDOF getTaskBody */

    /**
     * Extracts the project and/or subtask ID and returns it/them
     * @param specifierRegex
     * @param prefix
     * @param specifier
     * @return project and/or subtask ID
     */
    private String extractTaskID(String specifierRegex, String prefix, String specifier) {
        String projectId = null, subtaskId = null;
        if(prefix.equals(COMMANDS[0])) { // 'create' command
            if (specifier.equals(SPECIFIERS[0])) { // 'project'
                projectId = specifierRegex.trim().split(":")[1]; // split by colon
            } else if (specifier.equals(SPECIFIERS[1])) { // 'subtask'
                projectId = specifierRegex.trim().split(":")[0].trim().split("#")[1];
                subtaskId = specifierRegex.trim().split(":")[1].trim().split("#")[1];
            }
        } else { // for 'delete', 'tick' and 'edit' commands
            if(specifier.equals(SPECIFIERS[0]) || specifier.equals(SPECIFIERS[2])) { // 'project' or 'task'
                projectId = specifierRegex.trim().split("#")[1];
            } else if(specifier.equals(SPECIFIERS[1])) { // 'subtask'
                projectId = specifierRegex.trim().split(":")[0].trim().split("#")[1];
                subtaskId = specifierRegex.trim().split(":")[1].trim().split("#")[1];
            }
        }

        if(subtaskId == null)
            return projectId;
        return projectId + "-" + subtaskId;
    } /* ENDOF extractTaskID */
} /* ENDOF CommandOps */
