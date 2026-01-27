package org.example;

import java.util.Scanner;
import java.io.IOException;

public class UserInterface {
    private Scanner input;
    private CommandOps commandOperator;
    private String command;

    public UserInterface() throws IOException, ClassNotFoundException {
        commandOperator = new CommandOps();
    }

    public void prompt() throws IOException {
        input = new Scanner(System.in);
        while(true) {
            System.out.print("(command) ");
            command = input.nextLine();

            if(command.equals("exit")) {
                commandOperator.saveTasks();
                break;
            } else if(!command.isEmpty()) {
                Status commandStatus = commandOperator.isValidCommand(command);
                if(commandStatus == Status.ERROR)
                    System.out.println("Error...");
                else if(commandStatus == Status.INVALID)
                    System.out.println("Invalid command...");
            }
        }
        input.close();
    }
}
