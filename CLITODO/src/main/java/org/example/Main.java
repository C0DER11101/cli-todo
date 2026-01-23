package org.example;

public class Main {
    public static void main(String[] args) throws Exception {
        // greet the user
        System.out.println("Welcome to the CLI-based todo list generator!!");
        System.out.println("Type 'list' to get the list of supported commands");
        UserInterface ui = new UserInterface();

        ui.prompt();
    }
}