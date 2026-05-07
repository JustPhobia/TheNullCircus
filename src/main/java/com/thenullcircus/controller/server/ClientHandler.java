package com.thenullcircus.controller.server;

import com.thenullcircus.controller.services.AuthService;
import com.thenullcircus.dao.UserDao;
import com.thenullcircus.dao.UserDaoImpl;
import com.thenullcircus.model.Gender;
import com.thenullcircus.model.User;
import com.google.gson.JsonObject;
import com.thenullcircus.controller.JokeOfDayService;
import com.thenullcircus.model.Post;

import com.thenullcircus.controller.services.AuthService;

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
    private JokeOfDayService jokeOfDayService;

    public ClientHandler(Socket socket, JokeOfDayService jokeOfDayService) {

        this.socket = socket;

        this.userDao = new UserDaoImpl();
        this.authService = new AuthService(this.userDao);
        this.jokeOfDayService = jokeOfDayService;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Client said: " + message);
                String response = handleRequest(message);
                out.println(response);
            }
        } catch (IOException e) {
            System.out.println("Error: Client disconnected");
        }
    }

    private String handleRequest(String request){
        System.out.println("Raw request: " + request);

        String[] parts = request.split("\\|");
        String command = parts[0].toUpperCase();

        switch (command){
            case "LOGIN":{
                return handleLogin(parts);

            }
            case "REGISTER": {
                return handleRegister(parts);
            }
            case "GET_JOKE_OF_DAY": {
                Post joke = jokeOfDayService.getCachedJoke();
                JsonObject response = new JsonObject();

                if (joke != null){
                    response.addProperty("status", "SUCCESS");
                    response.add("post", postToJson(joke));
                } else {
                    response.addProperty("status", "NOT_FOUND");
                    response.addProperty("message", "No joke of the day available.");
                }

                out.println(response.toString());
                break;
            }
            default:{
                return "Error: Unknown command";
            }
        }
        return null;
    }

    private String handleLogin(String[] parts){
        if(parts.length < 3){
            return "Error: Missing login fields";
        }

        String username = parts[1];
        String password = parts[2];

        boolean success = authService.login(username, password);

        if(success){
            return "LOGIN_SUCCESSFUL";
        }
        return "LOGIN_FAILED";
    }

    private String handleRegister(String[] parts){
        if(parts.length != 7){
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
            return "Error: Invalid gender value";
        }

    }

    private JsonObject postToJson(Post post){
        JsonObject json = new JsonObject();
        json.addProperty("postId", post.getPostId().toString());
        json.addProperty("userId", post.getUserId().toString());
        json.addProperty("body", post.getBody());
        json.addProperty("comments", post.getComments());
        json.addProperty("status", post.getStatus().toString());
        json.addProperty("moderatorId", post.getModeratorId());
        json.addProperty("timestamp", post.getTimestamp().toString());
        json.addProperty("upvotes", post.getUpvotes());
        json.addProperty("downvotes", post.getDownvotes());
        return json;
    }
}
