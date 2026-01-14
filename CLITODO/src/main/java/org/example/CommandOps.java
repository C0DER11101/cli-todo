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

    private FileOps taskReaderWriter; // for reading and writing tasks from and to the tasks file
    private Map<String, List<String>> tasks;
    private Map<Integer, String> projectIds; // stores the project ids and the project names

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

    public boolean isValidCommand(String command) {
        // first validate the prefix
        String prefix = command.trim().split(SPACE_SEP)[0];
        if(isValidPrefix((prefix))) {
            // next validate the specifier
            String specifier = command.trim().split(SPACE_SEP)[1];
            if(isValidSpecifier(specifier)) {
                // now validate the regex for the specifier
                if(specifier.equals(SPECIFIERS[2])) // if it's a normal task
                    return true;
                String specifierRegex = command.trim().split(SPACE_SEP)[2];
                if(isValidSpecifierRegex(specifierRegex, prefix, specifier)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isValidPrefix(String prefix) {
        for(String predefinedPrefix : COMMANDS)
            if(prefix.equals(predefinedPrefix))
                return true;
        return false;
    }

    private boolean isValidSpecifier(String specifier) {
        for(String predefinedSpecifier : SPECIFIERS)
            if(specifier.equals(predefinedSpecifier))
                return true;
        return false;
    }

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
} /* END CommandOps */
