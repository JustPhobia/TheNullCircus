package com.thenullcircus.controller.server;

import com.google.gson.JsonParser;
import com.thenullcircus.controller.services.AuthService;
import com.thenullcircus.dao.UserDao;
import com.thenullcircus.dao.UserDaoImpl;
import com.thenullcircus.model.Gender;
import com.thenullcircus.model.User;
import com.google.gson.JsonObject;
import com.thenullcircus.controller.services.JokeOfDayService;
import com.thenullcircus.model.Post;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    public UserDao userDao = new UserDaoImpl();
    public AuthService authService;
    private final JokeOfDayService jokeOfDayService;

    public ClientHandler(Socket socket, JokeOfDayService jokeOfDayService) {

        this.socket = socket;

        this.userDao = new UserDaoImpl();
        this.authService = new AuthService(this.userDao);
        this.jokeOfDayService = jokeOfDayService;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);){


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

    private String handleRequest(String message){

        System.out.println("Raw request: " + message);

        try {
            JsonObject request = JsonParser.parseString(message).getAsJsonObject();
            String command = request.get("action").getAsString().toUpperCase();


            switch (command) {
                case "LOGIN": {
                    return handleLogin(request);

                }
                case "REGISTER": {
                    return handleRegister(request);
                }
                case "UPDATE_PROFILE": {
                    return handleUpdateProfile(request);

                }
                case "GET_JOKE_OF_DAY": {
                    Post joke = jokeOfDayService.getCachedJoke();
                    JsonObject response = new JsonObject();

                    if (joke != null) {
                        response.addProperty("status", "SUCCESS");
                        response.add("post", postToJson(joke));
                    } else {
                        response.addProperty("status", "NOT_FOUND");
                        response.addProperty("message", "No joke of the day available.");
                    }
                    return response.toString();
                }

                default: {
                    JsonObject response = new JsonObject();
                    response.addProperty("status", "ERROR");
                    response.addProperty("message", "unknown command");
                    return response.toString();
                }
            }
        } catch (Exception e){
            JsonObject response = new JsonObject();
            response.addProperty("status", "ERROR");
            response.addProperty("message", "Invalid request format");
            return response.toString();
        }

    }

    private String handleLogin(JsonObject request) {
        String username = request.get("username").getAsString();
        String password = request.get("password").getAsString();

        User user = authService.login(username, password);

        JsonObject response = new JsonObject();
        if (user != null) {
            response.addProperty("status", "SUCCESS");
            response.addProperty("userId",     user.getUserId().toString());
            response.addProperty("username",   user.getUsername());
            response.addProperty("name",       user.getName());
            response.addProperty("surname",    user.getSurname());
            response.addProperty("email",      user.getEmail());
            response.addProperty("gender",     user.getGender().toString());
            response.addProperty("clown",      user.getClown());
            response.addProperty("ringleader", user.getRingleader());
        } else {
            response.addProperty("status", "FAILED");
        }
        return response.toString();
    }

    private String handleRegister(JsonObject request) {
        try {
            User user = new User();
            user.setName(request.get("name").getAsString());
            user.setSurname(request.get("surname").getAsString());
            user.setEmail(request.get("email").getAsString());
            user.setGender(Gender.valueOf(request.get("gender").getAsString().toUpperCase()));
            user.setUsername(request.get("username").getAsString());
            user.setPassword(request.get("password").getAsString());
            user.setClown(false);
            user.setRingleader(false);

            boolean success = authService.register(user);

            JsonObject response = new JsonObject();
            response.addProperty("status", success ? "SUCCESS" : "FAILED");
            return response.toString();

        } catch (IllegalArgumentException e) {
            JsonObject response = new JsonObject();
            response.addProperty("status", "ERROR");
            response.addProperty("message", "Invalid gender value");
            return response.toString();
        }
    }

    private String handleUpdateProfile(JsonObject request) {
        String userId    = request.get("userId").getAsString();
        String fieldName = request.get("field").getAsString();
        String newValue  = request.get("value").getAsString();

        boolean success = userDao.updateProfile(
                java.util.UUID.fromString(userId), fieldName, newValue
        );

        JsonObject response = new JsonObject();
        response.addProperty("status", success ? "SUCCESS" : "FAILED");
        return response.toString();
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
