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

    private FileOps taskReaderWriter;
    private Map<String, List<String>> tasks;

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
            }
        }

        return false; // just a dummy value
    }

    private boolean isValidPrefix(String prefix) {
        return false; // just a dummy value
    }

    private boolean isValidSpecifier(String specifier) {
        return false; // just a dummy value
    }
}
