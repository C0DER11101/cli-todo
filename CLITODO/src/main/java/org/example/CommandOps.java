package org.example;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

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

    private FileOps taskReaderWriter; // for reading and writing tasks from and to the tasks file
    private Map<String, List<String>> tasks;
    private Map<Integer, String> projectIds; // stores the project ids and the project names

    private final String[] DATESTATES = {
            "valid",
            "invalid",
            "not added"
    };

    // predefined commands and specifiers
    private final String[] COMMANDS = {
            "list",
            "create",
            "show-tasks",
            "delete",
            "exit"
    };

    private final String[] SPECIFIERS = {
            "project",
            "subtask",
            "task"
    };

    public CommandOps() {
        tasks = new HashMap<>();
        taskReaderWriter = new FileOps();
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
            String specifier = command.trim().split(SPACE_SEP)[1];
            if(isValidSpecifier(specifier)) {
                // now validate the regex for the specifier
                if(specifier.equals(SPECIFIERS[2])) { // if it's a normal task
                    // check the validity of the dates
                    String dateState = isValidDate(command);
                    return true; // then there is no need to check for any regex here, simply return true
                }
                String specifierRegex = command.trim().split(SPACE_SEP)[2];
                if(isValidSpecifierRegex(specifierRegex, prefix, specifier)) {
                    // now validate the dates
                    String dateState = isValidDate(command);
                    if(dateState.equals(DATESTATES[0]) || dateState.equals(DATESTATES[2]))
                        return true;
                }
            }
        }

        return false;
    } /* END isValidCommand */

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
    } /* END isValidSpecifierRegex */

    /**
     * Checks whether any dates have been entered or not and if so whether they are valid or not
     * @return one of the three predefined states of date
     */
    private String isValidDate(String command) {
        String[] components = command.trim().split(":");
        String startDate = components[components.length - 2];
        String endDate = components[components.length - 1];

        // dates have been entered
        if(startDate.trim().split("[-/]").length == 3 && endDate.trim().split("[-/]").length == 3) {
            // check whether the entered dates are valid or not
        } else if(startDate.trim().split("[-/]").length == 3) {
            // check whether the entered date is valid or not

            // if valid, then assume the end date as well to be the same as start date
        } else if(endDate.trim().split("[-/]").length == 3) {
            // check whether the entered date is valid or not

            // if valid, then assume the start date to be the same as the end date
        } else {
            // no dates entered, assume the start date and the end date to be the day when the task was created
        }

        return DATESTATES[2];
    } /* END isValidDate */
} /* END CommandOps */
