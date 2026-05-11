package com.thenullcircus.controller.client;

import com.google.gson.JsonObject;
import com.thenullcircus.model.Gender;
import com.thenullcircus.model.User;
import com.thenullcircus.network.Client;
import com.thenullcircus.util.Session;

import java.io.IOException;
import java.util.logging.Logger;

public class LoginController {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    public LoginController() {}

    public boolean login(String username, String password) {
        logger.info("[LOGIN] Attempting login for user: " + username);
        Client client = new Client();
        try {
            client.connect();

            JsonObject request = new JsonObject();
            request.addProperty("action",   "LOGIN");
            request.addProperty("username", username);
            request.addProperty("password", password);

            logger.fine("[LOGIN] Sending login request to server...");
            client.sendRequest(request);
            JsonObject response = client.readResponse();
            client.disconnect();

            String status = response.get("status").getAsString();
            if (status.equals("SUCCESS")) {
                logger.info("[LOGIN] Authentication successful for: " + username);
                // Build the User from the response and store in Session
                User user = new User();
                user.setUserId(java.util.UUID.fromString(
                        response.get("userId").getAsString()));
                user.setName(response.get("name").getAsString());
                user.setSurname(response.get("surname").getAsString());
                user.setEmail(response.get("email").getAsString());
                user.setGender(Gender.valueOf(
                        response.get("gender").getAsString()));
                user.setUsername(response.get("username").getAsString());
                user.setClown(response.get("clown").getAsBoolean());
                user.setRingleader(response.get("ringleader").getAsBoolean());

                Session.login(user);
                logger.info("[LOGIN] Session initialized for User ID: " + user.getUserId());
                return true;
            }

            logger.warning("[LOGIN] Authentication failed for: " + username + ". Server returned: " + status);
            return false;

        } catch (IOException e) {
            logger.severe("[LOGIN] Network failure during login for " + username + ": " + e.getMessage());
            System.err.println("Could not connect to server: " + e.getMessage());
            return false;
        }
    }
}