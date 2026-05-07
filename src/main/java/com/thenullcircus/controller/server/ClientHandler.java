package com.thenullcircus.controller.server;

import com.google.gson.JsonObject;
import com.thenullcircus.model.Post;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
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

        String[] parts = request.split(" ");
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

    private void handleLogin(String[] parts){
        if(parts.length < 3){
            System.out.println("Error: Missing login fields");
        }

        String username = parts[1];
        String password = parts[2];

        boolean success = AuthService.login(username, password);
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
