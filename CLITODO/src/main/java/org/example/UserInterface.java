package org.example;

import java.util.Scanner;

public class UserInterface {
    private Scanner input;
    private CommandOps commandOperator;

    public UserInterface() {
        input = new Scanner(System.in);
    }

    // prompt the user to enter commands
    public void prompt() {
        while(true) {
            System.out.print("(command) ");
            String command = input.nextLine();

            if(command.length() != 0) {
                if (commandOperator.isValidCommand(command)) {
                } else {
                    System.out.println("Invalid command...");
                }
            }
        }
    }
}
