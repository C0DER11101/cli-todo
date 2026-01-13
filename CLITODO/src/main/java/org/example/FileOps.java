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
    private String filename;
    private File file;

    public FileOps() {
        this.filename = "tasks.txt"; // default name of the text file
        file = new File(this.filename);
    }

    public FileOps(String filename) {
        this.filename = filename;
        file = new File(this.filename);
    }

    public boolean doesFileExist() {
        return file.exists();
    }

    public boolean doesFileExist(String filename) {
        return new File(filename).exists();
    }

    public void write(String text) throws Exception {
        try(
                // open in append mode
                PrintWriter writer = new PrintWriter(new FileOutputStream(file, true))
        ) {
            writer.println(text);
        }
    }

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
    }
}
