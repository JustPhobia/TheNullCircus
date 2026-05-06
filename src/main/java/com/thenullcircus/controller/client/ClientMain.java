package com.thenullcircus.controller.client;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        ServerConnection connection = new ServerConnection();
        connection.connect();

        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.print("Enter command: ");
            String command = input.nextLine();

            String response = connection.sendMessage(command);
            System.out.println("Server Response: " + response);
        }
    }
}
