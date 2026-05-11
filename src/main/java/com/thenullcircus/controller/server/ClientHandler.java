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
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final Socket socket;
    public UserDao userDao;
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
        logger.info("Thread started for connection from: " + socket.getInetAddress());
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String message;
            while ((message = in.readLine()) != null) {
                logger.fine("Received raw message: " + message);
                String response = handleRequest(message);
                out.println(response);
            }
        } catch (IOException e) {
            logger.warning("Connection lost with client: " + socket.getInetAddress());
        }
    }

    private String handleRequest(String message) {
        try {
            JsonObject request = JsonParser.parseString(message).getAsJsonObject();
            String command = request.get("action").getAsString().toUpperCase();
            logger.info("Routing Action: " + command);

            return switch (command) {
                case "LOGIN" -> handleLogin(request);
                case "REGISTER" -> handleRegister(request);
                case "UPDATE_PROFILE" -> handleUpdateProfile(request);
                case "GET_JOKE_OF_DAY" -> handleJokeOfDayRequest();
                case "GET_POSTS" -> handleGetPosts();
                case "UPVOTE" -> handleVote(request, "UPVOTE");
                case "DOWNVOTE" -> handleVote(request, "DOWNVOTE");
                case "CREATE_POST" -> handleCreatePost(request);
                case "GET_PENDING_POSTS" -> handleGetPendingPosts();
                case "APPROVE_POST" -> handleModeration(request, "APPROVE");
                case "REJECT_POST" -> handleModeration(request, "REJECT");
                case "SUBMIT_ROLE_REQUEST" -> handleSubmitRoleRequest(request);
                case "GET_PENDING_ROLE_REQUESTS" -> handleGetRoleRequests();
                case "APPROVE_ROLE_REQUEST" -> handleRoleDecision(request, true);
                case "REJECT_ROLE_REQUEST" -> handleRoleDecision(request, false);
                case "GET_MY_POSTS" -> handleGetMyPosts(request);
                case "GET_UPVOTED_POSTS" -> handleGetUpvotedPosts(request);
                default -> {
                    logger.warning("Unrecognized command received: " + command);
                    yield buildErrorResponse("Unknown command");
                }
            };
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Critical error parsing request: " + message, e);
            return buildErrorResponse("Invalid request format");
        }
    }

    // --- AUTHENTICATION HANDLERS ---

    private String handleLogin(JsonObject request) {
        String u = request.get("username").getAsString();
        logger.info("Attempting login for: " + u);
        User user = authService.login(u, request.get("password").getAsString());

        JsonObject response = new JsonObject();
        if (user != null) {
            response.addProperty("status", "SUCCESS");
            response.addProperty("userId", user.getUserId().toString());
            response.addProperty("username", user.getUsername());
            response.addProperty("name", user.getName());
            response.addProperty("surname", user.getSurname());
            response.addProperty("email", user.getEmail());
            response.addProperty("gender", user.getGender().toString());
            response.addProperty("clown", user.getClown());
            response.addProperty("ringleader", user.getRingleader());
            logger.info("Login SUCCESS: " + u);
        } else {
            response.addProperty("status", "FAILED");
            logger.warning("Login FAILED: " + u);
        }
        return response.toString();
    }

    private String handleRegister(JsonObject request) {
        logger.info("Processing new registration for: " + request.get("username").getAsString());
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
            logger.info("Registration result for " + user.getUsername() + ": " + (success ? "SUCCESS" : "FAILED"));
            return response.toString();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Registration failed due to invalid data", e);
            return buildErrorResponse("Registration failed");
        }
    }

    // --- PROFILE & SESSION HANDLERS ---

    private String handleUpdateProfile(JsonObject request) {
        String userId = request.get("userId").getAsString();
        String field = request.get("field").getAsString();
        logger.info("Profile update request: User " + userId + " updating " + field);

        boolean success = userDao.updateProfile(
                UUID.fromString(userId), field, request.get("value").getAsString()
        );

        JsonObject response = new JsonObject();
        response.addProperty("status", success ? "SUCCESS" : "FAILED");
        return response.toString();
    }

    private String handleJokeOfDayRequest() {
        Post joke = jokeOfDayService.getCachedJoke();
        JsonObject response = new JsonObject();
        if (joke != null) {
            response.addProperty("status", "SUCCESS");
            response.add("post", postToJson(joke, null));
            logger.info("Served Joke of the Day: " + joke.getPostId());
        } else {
            response.addProperty("status", "NOT_FOUND");
            logger.info("Joke of the Day requested but none available.");
        }
        return response.toString();
    }

    // --- POST & VOTE HANDLERS ---

    private String handleGetPosts() {
        logger.info("Fetching all approved posts for client.");
        PostDAOImpl postDAO = new PostDAOImpl();
        ArrayList<Post> posts = postDAO.findAllApproved();
        return buildPostListResponse(posts);
    }

    private String handleVote(JsonObject request, String type) {
        UUID postId = UUID.fromString(request.get("postId").getAsString());
        UUID userId = UUID.fromString(request.get("userId").getAsString());
        logger.info("User " + userId + " performing " + type + " on Post " + postId);

        VotesDAOImpl votesDAO = new VotesDAOImpl();
        boolean success = type.equals("UPVOTE") ?
                votesDAO.upvotePost(postId, userId) : votesDAO.downvotePost(postId, userId);

        JsonObject response = new JsonObject();
        response.addProperty("status", success ? "SUCCESS" : "FAILED");
        return response.toString();
    }

    private String handleCreatePost(JsonObject request) {
        String userId = request.get("userId").getAsString();
        logger.info("Creating new post for User ID: " + userId);

        Post post = new Post(UUID.fromString(userId), request.get("body").getAsString(),
                null, Status.PENDING, null, LocalDateTime.now());

        boolean success = new PostDAOImpl().createPost(post);
        JsonObject response = new JsonObject();
        response.addProperty("status", success ? "SUCCESS" : "FAILED");
        return response.toString();
    }

    // --- MODERATION & ROLE HANDLERS ---

    private String handleGetPendingPosts() {
        logger.info("Ringleader requested pending moderation queue.");
        ArrayList<Post> posts = new PostDAOImpl().findAllPending();
        return buildPostListResponse(posts);
    }

    private String handleModeration(JsonObject request, String action) {
        UUID postId = UUID.fromString(request.get("postId").getAsString());
        UUID modId = UUID.fromString(request.get("moderatorId").getAsString());
        logger.info("Moderation: " + action + " for Post " + postId + " by Moderator " + modId);

        PostDAOImpl dao = new PostDAOImpl();
        boolean success = action.equals("APPROVE") ?
                dao.approvePost(postId, modId) : dao.rejectPost(postId, modId);

        JsonObject response = new JsonObject();
        response.addProperty("status", success ? "SUCCESS" : "FAILED");
        return response.toString();
    }

    private String handleSubmitRoleRequest(JsonObject request) {
        logger.info("Role request submitted by User: " + request.get("userId").getAsString());
        RoleRequest roleReq = new RoleRequest(
                UUID.fromString(request.get("userId").getAsString()),
                RequestedRole.valueOf(request.get("requestedRole").getAsString()),
                request.get("reason").getAsString()
        );

        boolean success = new RoleRequestDAOImpl().submitRequest(roleReq);
        JsonObject response = new JsonObject();
        response.addProperty("status", success ? "SUCCESS" : "FAILED");
        return response.toString();
    }

    private String handleGetRoleRequests() {
        logger.info("Fetching pending role requests.");
        ArrayList<RoleRequest> requests = new RoleRequestDAOImpl().findAllPending();
        JsonArray array = new JsonArray();

        for (RoleRequest req : requests) {
            JsonObject json = new JsonObject();
            json.addProperty("requestId", req.getRequestId().toString());
            json.addProperty("userId", req.getUserId().toString());
            json.addProperty("requestedRole", req.getRequestedRole().toString());
            json.addProperty("reason", req.getReason());
            json.addProperty("status", req.getStatus().toString());

            User user = userDao.findById(req.getUserId());
            json.addProperty("username", (user != null) ? user.getUsername() : "Unknown");
            array.add(json);
        }

        JsonObject response = new JsonObject();
        response.addProperty("status", "SUCCESS");
        response.add("requests", array);
        return response.toString();
    }

    private String handleRoleDecision(JsonObject request, boolean approve) {
        UUID reqId = UUID.fromString(request.get("requestId").getAsString());
        UUID ringId = UUID.fromString(request.get("ringleaderId").getAsString());
        logger.info("Role Decision: " + (approve ? "APPROVE" : "REJECT") + " for request " + reqId);

        RoleRequestDAOImpl dao = new RoleRequestDAOImpl();
        RoleRequest target = dao.findById(reqId);
        boolean success = approve ? dao.approveRequest(reqId, ringId) : dao.rejectRequest(reqId, ringId);

        if (success && approve && target != null) {
            userDao.updateRole(target.getUserId(), target.getRequestedRole().toString());
        }

        JsonObject response = new JsonObject();
        response.addProperty("status", success ? "SUCCESS" : "FAILED");
        return response.toString();
    }

    private String handleGetMyPosts(JsonObject request) {
        UUID userId = UUID.fromString(request.get("userId").getAsString());
        logger.info("User " + userId + " fetching their own posts.");
        return buildPostListResponse(new PostDAOImpl().findByUserId(userId));
    }

    private String handleGetUpvotedPosts(JsonObject request) {
        UUID userId = UUID.fromString(request.get("userId").getAsString());
        logger.info("User " + userId + " fetching their upvoted posts.");
        return buildPostListResponse(new VotesDAOImpl().getUpvotedPostsByUser(userId));
    }

    // --- SHARED UTILITIES ---

    private String buildPostListResponse(ArrayList<Post> posts) {
        JsonObject response = new JsonObject();
        JsonArray array = new JsonArray();
        HashMap<UUID, String> userCache = new HashMap<>();

        if (posts != null) {
            for (Post p : posts) array.add(postToJson(p, userCache));
        }

        response.addProperty("status", "SUCCESS");
        response.add("posts", array);
        return response.toString();
    }

    private JsonObject postToJson(Post post, java.util.Map<UUID, String> userCache) {
        JsonObject json = new JsonObject();
        json.addProperty("postId", post.getPostId().toString());
        json.addProperty("userId", post.getUserId().toString());

        String username = "Unknown";
        if (userCache != null && userCache.containsKey(post.getUserId())) {
            username = userCache.get(post.getUserId());
        } else {
            User author = userDao.findById(post.getUserId());
            if (author != null) username = author.getUsername();
            if (userCache != null) userCache.put(post.getUserId(), username);
        }

        json.addProperty("username", username);
        json.addProperty("body", post.getBody());
        json.addProperty("comments", post.getComments() == null ? "" : post.getComments());
        json.addProperty("status", post.getStatus().toString());
        json.addProperty("moderatorId", post.getModeratorId() == null ? "" : post.getModeratorId());
        json.addProperty("timestamp", post.getTimestamp().toString());
        json.addProperty("upvotes", post.getUpvotes());
        json.addProperty("downvotes", post.getDownvotes());
        return json;
    }

    private String buildErrorResponse(String msg) {
        JsonObject response = new JsonObject();
        response.addProperty("status", "ERROR");
        response.addProperty("message", msg);
        return response.toString();
    }
}