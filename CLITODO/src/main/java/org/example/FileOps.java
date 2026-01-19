package org.example;

import java.io.IOException;
import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;

public class FileOps {
    private final String FILENAME = "tasks.dat";
    private File file;

    public FileOps() {
        file = new File(FILENAME);
    }

    public boolean doesFileExist() {
        return file.exists();
    }

    /**
     * write the tasks one by one into the text file
     * @throws Exception
     */
    public void write() throws Exception {
    }

    /**
     * read the tasks one by one from the text file
     * @return a map of project name and a list of subtasks
     * @throws Exception
     */
    public Map<String, List<String>> read() throws Exception {
    }
}
