package com.thenullcircus.controller.server;

import com.thenullcircus.controller.services.AuthService;
import com.thenullcircus.dao.UserDao;
import com.thenullcircus.dao.UserDaoImpl;
import com.thenullcircus.model.Gender;
import com.thenullcircus.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    public UserDao userDao = new UserDaoImpl();
    public AuthService authService;

    public ClientHandler(Socket socket) {
        this.socket = socket;

        UserDao userDao = new UserDaoImpl();
        this.authService = new AuthService(userDao);
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Client said: " + message);
                out.println("Server received: " + message);
            }
        } catch (IOException e) {
            System.out.println("Error: Client disconnected");
        }
    }

    private void handleRequest(String request){
        System.out.println("Raw request: " + request);

        String[] parts = request.split("\\|");
        String command = parts[0].toUpperCase();

        switch (command){
            case "LOGIN":{
                handleLogin(parts);
                break;
            }
            case "REGISTER": {
                handleRegister(parts);
                break;
            }
            default:{
                System.out.println("Error: Unknown command");
                break;
            }
        }
    }

    private String handleLogin(String[] parts){
        if(parts.length < 3){
            System.out.println("Error: Missing login fields");
        }

        String username = parts[1];
        String password = parts[2];

        boolean success = AuthService.login(username, password);

        if(success){
            return "LOGIN_SUCCESSFUL";
        }
        return "LOGIN_FAILED";
    }

    private String handleRegister(String[] parts){
        if(parts.length < 7){
            return "Error: Missing login fields";
        }
        try{
            User user = new User();

            user.setName(parts[1]);
            user.setSurname(parts[2]);
            user.setEmail(parts[3]);
            user.setGender(Gender.valueOf(parts[4].toUpperCase()));
            user.setUsername(parts[5]);
            user.setPassword(parts[6]);
            user.setClown(false);
            user.setRingleader(false);

            boolean success =  authService.register(user);

            if(success){
                return "REGISTRATION_SUCCESSFUL";
            }
            return "REGISTRATION_FAILED";
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
