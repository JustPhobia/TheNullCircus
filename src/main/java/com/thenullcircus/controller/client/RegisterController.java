package com.thenullcircus.controller.client;

import java.sql.Connection;

public class RegisterController {
    private final ServerConnection connection;

    public RegisterController(){
        this.connection = new ServerConnection();
        try{
           this.connection.connect();
        } catch (RuntimeException e) {
            System.err.println("WARNING: Could not connect to server - " + e.getMessage());
        }
    }

    public boolean register(String name, String surname, String email,
                            String gender, String username, String password){
        String message = "REGISTER|" + name + "|" + surname + "|" + email
                         + "|" + gender + "|" + username + "|" + password;
        String response = connection.sendMessage(message);

        return "REGISTRATION_SUCCESSFUL".equals(response);
    }
}
