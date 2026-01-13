package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // greet the user
        System.out.println("Welcome to the CLI-based todo list generator!!");
        System.out.println("Type 'list' to get the list of supported commands");
        UserInterface ui = new UserInterface();

        ui.prompt();
    }
}