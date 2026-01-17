package org.example;

import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// TODO: NO NEED FOR START DATE OF A TASK, THE START DATE WILL BE ASSUMED AS THE DATE WHEN THE TASK WAS CREATED

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
    private final String DATE_REGEX = "([0-2][0-9]|[3][01])[-/]([01][0-2])[-/]([1-9][0-9]{3})";
    private final DateTimeFormatter formatter; // for formatting the dates in a particular pattern

    // for the dates
    // TODO: remove `start`
    private String start; // start date
    private String end; // end date

    private FileOps taskReaderWriter; // for reading and writing tasks from and to the tasks file
    private Map<Integer, Map<Integer, String>> tasks; // stores project IDs and the corresponding list of subtask IDs and subtasks
    private Map<Integer, String> projectIds; // stores the project ids, normal task ids and the project names
    private Map<Character, String> normalTasks; // stores normal task Ids (as Character) and normal tasks
    private char id;

    private enum DateState {
        VALID,
        INVALID,
        NOTADDED,
        START_DATE, // if only the start date is provided
        END_DATE; // if only the end date is provided
    }

    // predefined commands and specifiers
    private final String[] COMMANDS = {
            "create",
            "show-tasks",
            "delete",
            "tick" // tick mark a task as done
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
        id = 'a'; // default id for the first normal task
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
                String specifier = command.trim().split(SPACE_SEP)[1];
                if (isValidSpecifier(specifier)) {
                    // now validate the regex for the specifier
                    if (specifier.equals(SPECIFIERS[2])) { // if it's a normal task
                        // a normal task has no specifier-regex
                        // check the validity of the dates
                        DateState dateState = isValidDate(command);
                        if (dateState == DateState.INVALID || dateState == DateState.NOTADDED) {
                            // a date that is invalid or isn't added will be replaced by the date when the task was created, this applies for
                            // both start date and end date
                            LocalDate currentDate = LocalDate.now();
                            start = formatter.format(currentDate);
                            end = formatter.format(currentDate);
                        } else if (dateState == DateState.START_DATE) { // if only the start date is provided
                            // then set the end date to the start date as well
                            end = formatter.format(LocalDate.parse(start));
                        } else if (dateState == DateState.END_DATE) { // if only the end date is provided
                            // then set the start date to the end date as well
                            start = formatter.format(LocalDate.parse(end));
                        }
                        return true; // then there is no need to check for any regex here, simply return true
                    }
                    String specifierRegex = command.trim().split(SPACE_SEP)[2];
                    if (isValidSpecifierRegex(specifierRegex, prefix, specifier)) {
                        // now validate the dates
                        DateState dateState = isValidDate(command);
                        if (dateState == DateState.INVALID || dateState == DateState.NOTADDED) {
                            // consider the start and end date to be the day when the task was created
                            LocalDate currentDate = LocalDate.now();
                            start = formatter.format(currentDate);
                            end = formatter.format(currentDate);
                        } else if (dateState == DateState.START_DATE) {
                            // then set the end date to start date as well
                            end = formatter.format(LocalDate.parse(start));
                        } else if (dateState == DateState.END_DATE) {
                            // then set the start date to end date as well
                            start = formatter.format(LocalDate.parse(end));
                        }
                        return true;
                    }
                }
            } else if(prefix.equals(COMMANDS[1])) { // for the 'show-tasks' command
                // display the tasks in directory-tree format
            } else if(prefix.equals(COMMANDS[2])) { // for the 'delete' command
                // again, validate the specifiers
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
    }

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
    }

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

        // split the dates into their individual components (i.e., day, month and year)
        String[] startDate = components[components.length - 2].trim().split("[-/]");
        String[] endDate = components[components.length - 1].trim().split("[-/]");

        LocalDate currentDate = LocalDate.now(); // get the current date

        // dates have been entered
        if(startDate.length == 3 && endDate.length == 3) { // both the start and end dates have been entered
            // check whether the entered dates are valid or not
            // check if the dates match the date-regex
            if(components[components.length - 2].trim().matches(DATE_REGEX) && components[components.length - 1].trim().matches(DATE_REGEX)) {
                // check the years
                int startYear = Integer.valueOf(startDate[2]);
                int endYear = Integer.valueOf(endDate[2]);

                // the years must be equal to or greater than the current year
                if(!(startYear >= currentDate.getYear() && endYear >= currentDate.getYear()))
                    return DateState.INVALID;
                if(startYear == endYear) { // if the years are the same then
                    // check the months
                    int startMonth = Integer.valueOf(startDate[1]);
                    int endMonth = Integer.valueOf(endDate[1]);

                    // the start and end months must be greater than or equal to the current month
                    if(!(startMonth >= currentDate.getMonthValue() && endMonth >= currentDate.getMonthValue()))
                        return DateState.INVALID;

                    if(startMonth <= endMonth) { // if the months are the same then or start month is smaller than the end month
                        // check the days
                        int startDay = Integer.valueOf(startDate[0]);
                        int endDay = Integer.valueOf(endDate[0]);
                        if(startDay <= endDay || (startDay > endDay && startMonth != endMonth)) {
                            start = formatter.format(LocalDate.parse(components[components.length - 2]));
                            end = formatter.format(LocalDate.parse(components[components.length - 1]));
                            return DateState.VALID;
                        } else
                            return DateState.INVALID;
                    } else if(startMonth > endMonth){ // start month cannot be greater than the end month, if it is then it will be considered invalid
                        return DateState.INVALID;
                    }
                } else if(startYear < endYear) { // if the start year is less than the end year
                    start = formatter.format(LocalDate.parse(components[components.length - 2]));
                    end = formatter.format(LocalDate.parse(components[components.length - 1]));
                    return DateState.VALID; // the date automatically becomes valid, no need to check the months and the days
                }
            } else { // if the dates don't match the date-regex
                return DateState.INVALID;
            }

        } else if(startDate.length == 3) { // if only start date is provided
            // check whether the entered date is valid or not
            // check if the date matches the date-regex
            if(components[components.length - 1].trim().matches(DATE_REGEX)) {
                // now check the validity of the year
                int startYear = Integer.valueOf(startDate[2]);

                if(startYear >= currentDate.getYear()) {
                    // check the month
                    int startMonth = Integer.valueOf(startDate[1]);
                    if(startMonth >= currentDate.getMonthValue()) {
                        // check the day of the month
                        int startDay = Integer.valueOf(startDate[0]);
                        if(startDay >= currentDate.getDayOfMonth()) {
                            start = formatter.format(LocalDate.parse(components[components.length - 1]));
                            return DateState.START_DATE;
                        }
                        return DateState.INVALID;
                    }
                    return DateState.INVALID;
                }
                return DateState.INVALID;
            } else {
                return DateState.INVALID;
            }
        } else if(endDate.length == 3) { // if only end date is provided
            // check whether the entered date is valid or not
            if(components[components.length - 1].trim().matches(DATE_REGEX)) {
                int endYear = Integer.valueOf(endDate[2]);
                if(endYear >= currentDate.getYear()) {
                    // check the month
                    int endMonth = Integer.valueOf(endDate[1]);
                    if(endMonth >= currentDate.getMonthValue()) {
                        // check the day of the month
                        int endDay = Integer.valueOf(endDate[0]);
                        if(endDay >= currentDate.getDayOfMonth()) {
                            end = formatter.format(LocalDate.parse(components[components.length - 1]));
                            return DateState.END_DATE;
                        }
                        return DateState.INVALID;
                    }

                    return DateState.INVALID;
                }
            } else {
                return DateState.INVALID;
            }
        }

        return DateState.NOTADDED; // if no dates are added
    } /* ENDOF isValidDate */
} /* ENDOF CommandOps */
