package org.example;

import java.util.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// FIXME: DATE_REGEX has invalid regex for month

/**
 * This class contains methods to validate and execute the commands entered by the user
 */
public class CommandOps {

    // some regexes
    private final String SPACE_SEP = "[ \t\n]+";
    //private final String WORDS = "[a-zA-Z]+";
    private final String NUMBERS = "[0-9]+";
    private final String ALPHANUMERIC = "[a-zA-Z0-9]+";
    private final String PROJECT_REGEX = "#" + ALPHANUMERIC + ":" + NUMBERS;
    private final String SUBTASK_REGEX = "#" + NUMBERS + ":" + "#" + NUMBERS;
    private final String TASK_REGEX = "#" + NUMBERS;
    private final String DATE_REGEX = "([0-2][0-9]|[3][01])[-/]([0][0-9]|[1][0-2])[-/]([1-9][0-9]{3})";
    private final DateTimeFormatter formatter; // for formatting the dates in a particular pattern
    private Map<Integer, Task> tasks; // list of tasks

    // UNICODE characters
    private final char BDLUR = '\u2514'; // └
    private final char BDLH = '\u2500'; // ─
    private final char BDLVR = '\u251c'; // ├
    //private final char BDLV = '\u2502'; // │

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
            "edit", // edit a task/subtask/project -- maybe change the due date
            "list", // list the available commands
            "exit"
    };

    private final String[] SPECIFIERS = {
            "project",
            "subtask",
            "task"
    };

    public CommandOps() throws IOException, ClassNotFoundException {
        taskReaderWriter = new FileOps();
        if(taskReaderWriter.doesFileExist())
            readTasks();
        else
            tasks = new HashMap<>();
        formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
    }

    public void listCommands() {
        for(int i = 0; i < COMMANDS.length; i++)
            System.out.println(COMMANDS[i]);
    }

    /**
     * Checks whether the given command is valid or not
     * @param command
     * @return boolean
     */
    public Status isValidCommand(String command) {
        String[] commandComponents = command.trim().split(SPACE_SEP);
        String prefix = commandComponents[0];
        String specifier = null;
        String specifierRegex = null;

        if(commandComponents.length >= 3) {
            specifier = commandComponents[1];
            specifierRegex = commandComponents[2];
        }

        // VALIDATE THE COMMANDS AND THE SPECIFIERS
        // validate the prefix
        if(isValidPrefix(prefix)) {
            if(prefix.equals("show-tasks")) {
                displayTasks();
                return Status.VALID;
            } else if(prefix.equals("list")) {
                listCommands();
                return Status.VALID;
            } else if(prefix.equals(COMMANDS[0]) || prefix.equals(COMMANDS[2]) || prefix.equals(COMMANDS[3]) || prefix.equals(COMMANDS[4])) {
                // prefix is either 'create', 'delete', 'tick' or 'edit'
                if(commandComponents.length < 3)
                    return Status.INVALID;
                // validate the specifier
                if(!isValidSpecifier(specifier))
                    return Status.INVALID;
            } else
                return Status.INVALID;
        } else {
            return Status.INVALID;
        }

        // first validate the prefix
        if (prefix.equals(COMMANDS[0])) { // 'create'
            // now validate the regex for the specifier
            if (isValidSpecifierRegex(specifierRegex, prefix, specifier)) {
                // now extract the id(s) from the specifier-regex
                String ids = extractTaskID(specifierRegex, prefix, specifier);

                // next, get the task body/name
                String taskBody = getTaskBody(commandComponents);

                // now validate the dates
                DateState dateState = isValidDate(command);
                // if the date was invalid or wasn't added
                if (dateState == DateState.INVALID || dateState == DateState.NOTADDED) {
                    System.out.println("The due date of this task is set to the creation date.");
                    System.out.println("You can use the 'edit' command to set the due date manually.");
                    LocalDate currentDate = LocalDate.now(); // then set the current date to the date of task's creation
                    dueDate = formatter.format(currentDate); // set the date format to dd/MM/YYYY
                }

                // if the ids contain a hyphen then
                if(ids.contains("-")) { // a subtask was created
                    // retrieve project id and the subtask id
                    int projectId = Integer.parseInt(ids.trim().split("-")[0]);
                    int subtaskId = Integer.parseInt(ids.trim().split("-")[1]);
                    if(tasks.get(projectId) == null) {
                        System.out.println("A project must exist in order to create a subtask");
                        return Status.ERROR;
                    } else if(tasks.get(projectId).getSubtask(subtaskId) != null) { // if this project already has a subtask with the same id
                        System.out.println("A subtask with this id already exists...");
                        return Status.ERROR;
                    }

                    // add the subtask
                    tasks.get(projectId).addSubtask(subtaskId, taskBody, dueDate, TaskType.SUBTASK);
                } else { // if a project/task was created
                    int id = Integer.parseInt(ids);
                    if(tasks.get(id) == null) {
                        // add the task
                        tasks.put(id, new Task(id, taskBody, dueDate, specifier.equals(SPECIFIERS[0]) ? TaskType.PROJECT : TaskType.TASK));
                    } else {
                        System.out.println("A project/task with id " + id + " already exists");
                        return Status.ERROR;
                    }
                }
                return Status.VALID;
            }
        } else if(prefix.equals(COMMANDS[2])) { // for the 'delete' command
            // now validate the specifier regex
            if(isValidSpecifierRegex(specifierRegex, prefix, specifier)) {
                // extract the task id
                String ids = extractTaskID(specifierRegex, prefix, specifier);

                if(ids.contains("-")) { // subtask needs to be deleted
                    // search for the subtask and delete it
                    int projectId = Integer.parseInt(ids.trim().split("-")[0]);
                    int subtaskId = Integer.parseInt(ids.trim().split("-")[1]);

                    if(tasks.get(projectId) == null) {
                        System.out.println("Cannot delete subtask of a non-existent project");
                        return Status.ERROR;
                    }

                    if(tasks.get(projectId).removeSubtask(subtaskId)) {
                        System.out.println("Subtask removed successfully");
                    } else {
                        System.out.println("Subtask not found");
                        return Status.ERROR;
                    }
                    return Status.VALID;
                }
                // search for the project/task and delete it
                int id = Integer.parseInt(ids);
                Task target = tasks.get(id);
                if(target == null) {
                    System.out.println("The project/task with id " + id + " doesn't exist");
                    return Status.ERROR;
                }

                if(target.getTaskType() == TaskType.PROJECT && specifier.equals(SPECIFIERS[0])) {
                    // check if it has any incomplete subtasks
                    if(!target.hasUndoneSubtasks()) {
                        tasks.remove(id);
                    } else {
                        System.out.println("This project has incomplete subtasks, not deleting it");
                        return Status.ERROR;
                    }
                } else if(target.getTaskType() == TaskType.PROJECT && specifier.equals(SPECIFIERS[2])) {
                    // if the specifier is 'task' while the task with the id is actually a project
                    System.out.println("The task is a project. Not deleting it");
                    return Status.ERROR;
                } else if(target.getTaskType() == TaskType.TASK && specifier.equals(SPECIFIERS[0])) {
                    // if the specifier is 'project' while the task with the id is actually a normal task
                    System.out.println("The task is not a project. Not deleting it");
                    return Status.ERROR;
                } else {
                    tasks.remove(id);
                }
                return Status.VALID;
            }
        } else if(prefix.equals(COMMANDS[3])) { // the 'tick' command
            // validate the specifier-regex
            if(isValidSpecifierRegex(specifierRegex, prefix, specifier)) {
                // extract the id
                String ids = extractTaskID(specifierRegex, prefix, specifier);
                if(ids.contains("-")) {
                    // search for the subtask and mark it as done
                    int projectId = Integer.parseInt(ids.trim().split("-")[0]);
                    int subtaskId = Integer.parseInt(ids.trim().split("-")[1]);

                    if(tasks.get(projectId) == null) {
                        System.out.println("Cannot tick subtask of a non-existent project");
                        return Status.ERROR;
                    }

                    if(tasks.get(projectId).markSubtaskDone(subtaskId))
                        System.out.println("Subtask marked as 'done'");
                    else
                        System.out.println("Subtask not found");
                    return Status.VALID;
                }

                // search for the project/task and mark it as done
                int id = Integer.parseInt(ids);
                Task target = tasks.get(id);
                if(target == null) {
                    System.out.println("The project/task with id " + id + " doesn't exist");
                    return Status.ERROR;
                }
                // mark the project/task as done
                if(target.hasUndoneSubtasks()) {
                    System.out.println("This project has undone tasks, not marking it");
                    return Status.ERROR;
                } else {
                    target.markTaskDone();
                    return Status.VALID;
                }
            }
        } else if(prefix.equals(COMMANDS[4])) { // the 'edit' command
            // validate the specifier regex
            if(isValidSpecifierRegex(specifierRegex, prefix, specifier)) {

                // extract the ids
                String ids = extractTaskID(specifierRegex, prefix, specifier);

                int projectId = -1, subtaskId = -1;

                if(ids.contains("-")) { // a subtask needs to be edited
                    projectId = Integer.parseInt(ids.trim().split("-")[0]);
                    subtaskId = Integer.parseInt(ids.trim().split("-")[1]);
                } else
                    projectId = Integer.parseInt(ids);

                // a project/subtask/task needs to edited
                Task target = tasks.get(projectId);
                Task subtask = null;
                if(target == null && subtaskId >= 0) {
                    System.out.println("Project of the subtask doesn't exist, not editing");
                    return Status.ERROR;
                } else if(target == null) {
                    System.out.println("Project doesn't exist, not editing");
                    return Status.ERROR;
                }

                if(subtaskId >= 0) {
                    subtask = target.getSubtask(subtaskId);
                    if(subtask == null) {
                        System.out.println("The subtask doesn't exist, not editing");
                        return Status.ERROR;
                    }
                }

                System.out.println("What do you want to edit ? ");
                System.out.println("[1] Task name");
                System.out.println("[2] Task due date");
                System.out.print("(opt) ");
                Scanner input = new Scanner(System.in);
                String opt = input.nextLine();

                try {
                    int selection = Integer.parseInt(opt);

                    if (selection == 1) {
                        String taskName;
                        System.out.print("Enter the new name for the task: ");
                        taskName = input.nextLine();
                        if (subtask == null) {
                            if (isValidProjectName(taskName)) // follow the convention of naming projects
                                target.setName(taskName);
                            else
                                System.out.println("Invalid name...");
                        } else
                            subtask.setName(taskName);
                    } else if (selection == 2) {
                        System.out.print("Enter the new due date: ");
                        dueDate = input.nextLine();

                        if (isValidDate(dueDate) == DateState.VALID) {
                            if (subtask == null)
                                target.setDueDate(dueDate);
                            else
                                subtask.setDueDate(dueDate);
                        } else {
                            System.out.println("Invalid date...Date not set...");
                        }
                    } else {
                        System.out.println("Invalid option");
                        System.out.println("Aborting process");
                    }
                } catch(NumberFormatException ex) {
                    System.out.println("Not an integer...");
                }

                return Status.VALID;
            }
        }
        return Status.INVALID;
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

    private boolean isValidProjectName(String name) {
        String[] components = name.trim().split("[-_]");
        for(int i = 0; i < components.length; i++) {
            if(!components[i].matches(ALPHANUMERIC))
                return false;
        }

        return true;
    }

    /**
     * Checks whether the regular expression entered for the specifier is valid or not
     * @param regex
     * @param prefix
     * @param specifier
     * @return boolean
     */
    private boolean isValidSpecifierRegex(String regex, String prefix, String specifier) {

        // use the prefix and the specifier to decide how to split 'regex'

        // for the 'create' command
        if (prefix.equals(COMMANDS[0])) {
            // splitting for the 'project' specifier
            if (specifier.equals(SPECIFIERS[0])) {
                String[] components = regex.trim().split("[-_]"); // split by hyphen or underscore

                if (components.length == 1 && components[0].matches(PROJECT_REGEX))
                    return true;
                if (components[0].matches("#" + ALPHANUMERIC) && components[components.length - 1].matches(ALPHANUMERIC + ":" + NUMBERS)) {
                    for (int i = 1; i < components.length - 1; i++)
                        if (!components[i].matches(ALPHANUMERIC))
                            return false;
                } else {
                    return false;
                }
            } else if (specifier.equals(SPECIFIERS[1])) { // for the 'subtask' specifier
                if (!regex.matches(SUBTASK_REGEX))
                    return false;
            } else if (specifier.equals(SPECIFIERS[2])) { // for the 'task' specifier
                if (!regex.matches(TASK_REGEX))
                    return false;
            }
        } else if (prefix.equals(COMMANDS[2]) || prefix.equals(COMMANDS[3]) || prefix.equals(COMMANDS[4])) { // for 'delete', 'tick' or 'edit'
            if(specifier.equals(SPECIFIERS[0]) || prefix.equals(SPECIFIERS[2])) { // 'project' or 'task'
                if(!regex.matches(TASK_REGEX))
                    return false;
            } else if(specifier.equals(SPECIFIERS[1])) { // 'subtask'
                if(!regex.matches(SUBTASK_REGEX))
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
        String[] dateComponents = components[components.length - 1].trim().split("[-/]");

        LocalDate currentDate = LocalDate.now(); // get the current date

        // date has been entered
        if(dateComponents.length == 3) {
            // check whether the entered date is valid or not
            // check if the date matches the DATE_REGEX
            if(components[components.length - 1].trim().matches(DATE_REGEX)) {
                // check the year
                int year = Integer.parseInt(dateComponents[2]);

                // if the year of the due-date is less than the current year
                if(!(year >= currentDate.getYear()))
                    return DateState.INVALID;

                // validate the number of days in the months
                int dayOfMonth = Integer.parseInt(dateComponents[0]);
                int month = Integer.parseInt(dateComponents[1]);
                if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                    if(dayOfMonth >= 1 && dayOfMonth <= 31) {
                        dueDate = components[components.length - 1];
                        return DateState.VALID;
                    }
                    return DateState.INVALID;
                } else if(month == 2) {
                    if(isLeap(year) ? dayOfMonth >= 1 && dayOfMonth <= 29 : dayOfMonth >= 1 && dayOfMonth <= 28) {
                        dueDate = components[components.length - 1];
                        return DateState.VALID;
                    }

                    return DateState.INVALID;
                } else {
                    if(dayOfMonth >= 1 && dayOfMonth <= 30) {
                        dueDate = components[components.length - 1];
                        return DateState.VALID;
                    }
                    return DateState.INVALID;
                }
            } else { // if the dates don't match the DATE_REGEX
                return DateState.INVALID;
            }
        }
        return DateState.NOTADDED; // if no dates are added
    } /* ENDOF isValidDate */

    /**
     * is the year leap ?
     * @param year
     * @return boolean
     */
    private boolean isLeap(int year) {
        return ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0));
    }

    /**
     * Returns the task body i.e. the name of the task.
     * @param commandComponents
     * @return
     */
    private String getTaskBody(String[] commandComponents) {
        StringBuilder body = new StringBuilder();

        if(commandComponents[1].equals(SPECIFIERS[0])) { // for the 'project' specifier
            return commandComponents[2].trim().split("#")[1].trim().split(":")[0].trim();
        }

        // for the 'subtask' or 'task' specifiers
        for(int i = 3; i < commandComponents.length && commandComponents[i].charAt(0) != ':'; i++)
            body.append(commandComponents[i] + " ");
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
                projectId = specifierRegex.trim().split(":")[1].trim(); // split by colon
            } else if (specifier.equals(SPECIFIERS[1])) { // 'subtask'
                projectId = specifierRegex.trim().split(":")[0].trim().split("#")[1].trim();
                subtaskId = specifierRegex.trim().split(":")[1].trim().split("#")[1].trim();
            } else if(specifier.equals(SPECIFIERS[2])) { // 'task'
                projectId = specifierRegex.trim().split("#")[1].trim();
            }
        } else { // for 'delete', 'tick' and 'edit' commands
            if(specifier.equals(SPECIFIERS[0]) || specifier.equals(SPECIFIERS[2])) { // 'project' or 'task'
                projectId = specifierRegex.trim().split("#")[1].trim();
            } else if(specifier.equals(SPECIFIERS[1])) { // 'subtask'
                projectId = specifierRegex.trim().split(":")[0].trim().split("#")[1].trim();
                subtaskId = specifierRegex.trim().split(":")[1].trim().split("#")[1].trim();
            }
        }

        if(subtaskId == null)
            return projectId;
        return projectId + "-" + subtaskId;
    } /* ENDOF extractTaskID */

    /**
     * Displays the tasks in directory-tree format
     */
    public void displayTasks() {
        Set<Integer> ids = tasks.keySet();

        for(int id : ids) {
            System.out.println(tasks.get(id)); // first print the project
            Task project = tasks.get(id);
            if(project.hasSubtasks()) {
                Set<Integer> subIds = project.getSubtasks(); // retrieve the subtask ids
                int subTaskCounter = 0;
                for(int subId : subIds) {
                    subTaskCounter++;
                    if (subTaskCounter == subIds.size())
                        System.out.print("\t" + BDLUR + BDLH + BDLH); // print └── if there is only subtask or if it's the last subtask
                    else
                        System.out.print("\t" + BDLVR + BDLH + BDLH); // print ├──
                    System.out.println(project.getSubtask(subId));
                }
            }
        }
    } /*ENDOF displayTasks */

    public void saveTasks() throws IOException {
        // save the tasks to file
        taskReaderWriter.write(tasks);
    } /* ENDOF saveTasks */

    public void readTasks() throws IOException, ClassNotFoundException {
        if(taskReaderWriter.doesFileExist())
            tasks = taskReaderWriter.read();
    } /* ENDOF readTasks */
} /* ENDOF CommandOps */