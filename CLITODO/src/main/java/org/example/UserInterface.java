package org.example;

import java.util.Scanner;

public class UserInterface {
    private Scanner input;
    private CommandOps commandOperator;

    public UserInterface() throws Exception {
        input = new Scanner(System.in);
        commandOperator = new CommandOps();
    }

    // prompt the user to enter commands
    public void prompt() throws Exception {
        while(true) {
            System.out.print("(command) ");
            String command = input.nextLine();

            if(command.equals("exit")) {
                // write to file and then exit
                commandOperator.saveTasks(); // save the tasks and exit
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
