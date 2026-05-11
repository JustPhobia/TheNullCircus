package com.thenullcircus.controller.server;

import com.google.gson.JsonParser;
import com.thenullcircus.controller.services.AuthService;
import com.thenullcircus.dao.*;
import com.thenullcircus.model.*;
import com.google.gson.JsonObject;
import com.thenullcircus.controller.services.JokeOfDayService;
import com.google.gson.JsonArray;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

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
                case "GET_POSTS": {
                    PostDAOImpl postDAO = new PostDAOImpl();
                    ArrayList<Post> posts = postDAO.findAllApproved();

                    JsonObject response = new JsonObject();
                    JsonArray postArray = new JsonArray();

                    for (Post post : posts) {
                        postArray.add(postToJson(post));
                    }

                    response.addProperty("status", "SUCCESS");
                    response.add("posts", postArray);
                    return response.toString();
                }
                case "UPVOTE": {
                    String postId = request.get("postId").getAsString();
                    String userId = request.get("userId").getAsString();

                    VotesDAOImpl votesDAO = new VotesDAOImpl();
                    boolean success = votesDAO.upvotePost(
                            UUID.fromString(postId),
                            UUID.fromString(userId)
                    );

                    JsonObject response = new JsonObject();
                    response.addProperty("status", success ? "SUCCESS" : "FAILED");
                    return response.toString();
                }

                case "DOWNVOTE": {
                    String postId = request.get("postId").getAsString();
                    String userId = request.get("userId").getAsString();

                    VotesDAOImpl votesDAO = new VotesDAOImpl();
                    boolean success = votesDAO.downvotePost(
                            UUID.fromString(postId),
                            UUID.fromString(userId)
                    );

                    JsonObject response = new JsonObject();
                    response.addProperty("status", success ? "SUCCESS" : "FAILED");
                    return response.toString();
                }
                case "CREATE_POST": {
                    String userId = request.get("userId").getAsString();
                    String body = request.get("body").getAsString();

                    Post post = new Post(UUID.fromString(userId), body, null, Status.PENDING, null, LocalDateTime.now());

                    PostDAOImpl postDAO = new PostDAOImpl();
                    boolean success = postDAO.createPost(post);

                    JsonObject response = new JsonObject();
                    response.addProperty("status", success ? "SUCCESS" : "FAILED");
                    return response.toString();
                }
                case "GET_PENDING_POSTS": {
                    PostDAOImpl postDAO = new PostDAOImpl();
                    ArrayList<Post> posts = postDAO.findAllPending();

                    JsonObject response = new JsonObject();
                    JsonArray postArray = new JsonArray();

                    for (Post post : posts) {
                        postArray.add(postToJson(post));
                    }

                    response.addProperty("status", "SUCCESS");
                    response.add("posts", postArray);
                    return response.toString();
                }
                case "APPROVE_POST": {
                    String postId = request.get("postId").getAsString();
                    String moderatorId = request.get("moderatorId").getAsString();

                    PostDAOImpl postDAO = new PostDAOImpl();
                    boolean success = postDAO.approvePost(
                            UUID.fromString(postId),
                            UUID.fromString(moderatorId)
                    );

                    JsonObject response = new JsonObject();
                    response.addProperty("status", success ? "SUCCESS" : "FAILED");
                    return response.toString();
                }
                case "REJECT_POST": {
                    String postId = request.get("postId").getAsString();
                    String moderatorId = request.get("moderatorId").getAsString();

                    PostDAOImpl postDAO = new PostDAOImpl();
                    boolean success = postDAO.rejectPost(
                            UUID.fromString(postId),
                            UUID.fromString(moderatorId)
                    );

                    JsonObject response = new JsonObject();
                    response.addProperty("status", success ? "SUCCESS" : "FAILED");
                    return response.toString();
                }
                case "SUBMIT_ROLE_REQUEST": {
                    String userId = request.get("userId").getAsString();
                    String requestedRole = request.get("requestedRole").getAsString();
                    String reason = request.get("reason").getAsString();

                    RoleRequest roleRequest = new RoleRequest(
                            UUID.fromString(userId),
                            RequestedRole.valueOf(requestedRole),
                            reason
                    );

                    RoleRequestDAOImpl roleRequestDAO = new RoleRequestDAOImpl();
                    boolean success = roleRequestDAO.submitRequest(roleRequest);

                    JsonObject response = new JsonObject();
                    response.addProperty("status", success ? "SUCCESS" : "FAILED");
                    return response.toString();
                }
                case "GET_PENDING_ROLE_REQUESTS": {
                    RoleRequestDAOImpl roleRequestDAO = new RoleRequestDAOImpl();
                    ArrayList<RoleRequest> requests = roleRequestDAO.findAllPending();

                    JsonObject response = new JsonObject();
                    JsonArray requestArray = new JsonArray();

                    for (RoleRequest req : requests) {
                        JsonObject json = new JsonObject();
                        json.addProperty("requestId", req.getRequestId().toString());
                        json.addProperty("userId", req.getUserId().toString());
                        json.addProperty("requestedRole", req.getRequestedRole().toString());
                        json.addProperty("reason", req.getReason());
                        json.addProperty("status", req.getStatus().toString());
                        requestArray.add(json);
                    }

                    response.addProperty("status", "SUCCESS");
                    response.add("requests", requestArray);
                    return response.toString();
                }
                case "APPROVE_ROLE_REQUEST": {
                    String requestId = request.get("requestId").getAsString();
                    String ringleaderId = request.get("ringleaderId").getAsString();

                    RoleRequestDAOImpl roleRequestDAO = new RoleRequestDAOImpl();
                    boolean success = roleRequestDAO.approveRequest(
                            UUID.fromString(requestId),
                            UUID.fromString(ringleaderId)
                    );

                    JsonObject response = new JsonObject();
                    response.addProperty("status", success ? "SUCCESS" : "FAILED");
                    return response.toString();
                }
                case "REJECT_ROLE_REQUEST": {
                    String requestId = request.get("requestId").getAsString();
                    String ringleaderId = request.get("ringleaderId").getAsString();

                    RoleRequestDAOImpl roleRequestDAO = new RoleRequestDAOImpl();
                    boolean success = roleRequestDAO.rejectRequest(
                            UUID.fromString(requestId),
                            UUID.fromString(ringleaderId)
                    );

                    JsonObject response = new JsonObject();
                    response.addProperty("status", success ? "SUCCESS" : "FAILED");
                    return response.toString();
                }
                case "GET_MY_POSTS": {
                    String userId = request.get("userId").getAsString();
                    PostDAOImpl postDAO = new PostDAOImpl();
                    ArrayList<Post> posts = postDAO.findByUserId(UUID.fromString(userId));

                    JsonObject response = new JsonObject();
                    JsonArray postArray = new JsonArray();

                    for (Post post : posts) {
                        postArray.add(postToJson(post));
                    }

                    response.addProperty("status", "SUCCESS");
                    response.add("posts", postArray);
                    return response.toString();
                }

                case "GET_UPVOTED_POSTS": {
                    String userId = request.get("userId").getAsString();

                    VotesDAOImpl votesDAO = new VotesDAOImpl();
                    ArrayList<Post> posts = votesDAO.getUpvotedPostsByUser(UUID.fromString(userId));

                    JsonObject response = new JsonObject();
                    JsonArray postArray = new JsonArray();

                    for (Post post : posts) {
                        postArray.add(postToJson(post));
                    }

                    response.addProperty("status", "SUCCESS");
                    response.add("posts", postArray);
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

    private JsonObject postToJson(Post post) {
        JsonObject json = new JsonObject();
        json.addProperty("postId",      post.getPostId().toString());
        json.addProperty("userId",      post.getUserId().toString());

        User author = userDao.findById(post.getUserId());
        json.addProperty("username", author != null ? author.getUsername() : "Unknown");

        json.addProperty("body",        post.getBody());
        json.addProperty("comments",    post.getComments());
        json.addProperty("status",      post.getStatus().toString());
        json.addProperty("moderatorId", post.getModeratorId());
        json.addProperty("timestamp",   post.getTimestamp().toString());
        json.addProperty("upvotes",     post.getUpvotes());
        json.addProperty("downvotes",   post.getDownvotes());
        return json;
    }
}
