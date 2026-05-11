package com.thenullcircus.controller.client;

import com.google.gson.JsonObject;
import com.thenullcircus.network.Client;
import java.io.IOException;
import java.util.logging.Logger;

public class RegisterController {
    private static final Logger logger = Logger.getLogger(RegisterController.class.getName());

    public RegisterController() {}

    public boolean register(String name, String username, String surname,
                            String email, String gender, String password) {
        logger.info("Initiating registration request for: " + username);
        Client client = new Client();
        try {
            client.connect();

            JsonObject request = new JsonObject();
            request.addProperty("action", "REGISTER");
            request.addProperty("name", name);
            request.addProperty("surname", surname);
            request.addProperty("email", email);
            request.addProperty("gender", gender);
            request.addProperty("username", username);
            request.addProperty("password", password);

            client.sendRequest(request);
            JsonObject response = client.readResponse();
            client.disconnect();

            boolean success = response.get("status").getAsString().equals("SUCCESS");
            logger.info("Registration result for " + username + ": " + (success ? "SUCCESS" : "FAILED"));
            return success;
        } catch (IOException e) {
            logger.severe("Network failure during registration for " + username + ": " + e.getMessage());
            return false;
        }
    }
}