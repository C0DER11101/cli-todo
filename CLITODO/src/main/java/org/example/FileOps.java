package org.example;

import java.io.File;
import java.util.Map;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
     * @throws IOException
     */
    public void write() throws IOException {
        try(
                ObjectInputStream obis = new ObjectInputStream(new FileInputStream(file))
        ) {
            // TODO: write the contents to the file
        }
    }

    /**
     * read the tasks one by one from the text file
     * @return a map of project name and a list of subtasks
     * @throws IOException
     */
    public Map<Integer, Task> read() throws IOException , ClassNotFoundException {
        try(
                ObjectOutputStream obos = new ObjectOutputStream(new FileOutputStream(file))
        ) {
            // TODO: read from the file
        }

        return null; // replace this statement, this is just a dummy statement
    }
}
