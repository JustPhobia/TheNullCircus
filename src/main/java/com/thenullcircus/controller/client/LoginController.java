package com.thenullcircus.controller.client;

import java.sql.Connection;

public class LoginController {

    private ServerConnection connection;

    public LoginController(){
        this.connection = new ServerConnection();
        try{
            this.connection.connect();
        } catch (RuntimeException e) {
            System.err.println("WARNING: Could not connect to server- " + e.getMessage());
        }

    }

    public boolean login(String username, String password){
        String message = "LOGIN|" +  username + "|" + password;
        String response = connection.sendMessage(message);
        return "LOGIN_SUCCESSFUL".equals(response);
    }
}
