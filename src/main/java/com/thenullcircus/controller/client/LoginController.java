package com.thenullcircus.controller.client;

import com.google.gson.JsonObject;
import com.thenullcircus.model.Gender;
import com.thenullcircus.model.User;
import com.thenullcircus.network.Client;
import com.thenullcircus.util.Session;

import java.io.IOException;

public class LoginController {

    public LoginController() {}

    public boolean login(String username, String password) {
        Client client = new Client();
        try {
            client.connect();

            JsonObject request = new JsonObject();
            request.addProperty("action",   "LOGIN");
            request.addProperty("username", username);
            request.addProperty("password", password);

            client.sendRequest(request);
            JsonObject response = client.readResponse();
            client.disconnect();

            if (response.get("status").getAsString().equals("SUCCESS")) {
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
                return true;
            }

            return false;

        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
            return false;
        }
    }
}