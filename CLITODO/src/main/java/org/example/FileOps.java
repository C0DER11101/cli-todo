package org.example;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.util.Scanner;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class FileOps {
    private final String FILENAME = "tasks.txt";
    private File file;

    public FileOps() {
        file = new File(FILENAME);
    }

    public boolean doesFileExist() {
        return file.exists();
    }

    /**
     * write the tasks one by one into the text file
     * @param text
     * @throws Exception
     */
    public void write(String text) throws Exception {
        try(
                // open in append mode
                PrintWriter writer = new PrintWriter(new FileOutputStream(file, true))
        ) {
            writer.println(text);
        }
    }

    /**
     * read the tasks one by one from the text file
     * @return a map of project name and a list of subtasks
     * @throws Exception
     */
    public Map<String, List<String>> read() throws Exception {
        Map<String, List<String>> tasks = new HashMap<>();
        try(
                Scanner read = new Scanner(file)
        ) {
            while(read.hasNext()) {
                String task = read.nextLine();
                // INCOMPLETE
            }
        }
        return null; // just a dummy value
    }
}
