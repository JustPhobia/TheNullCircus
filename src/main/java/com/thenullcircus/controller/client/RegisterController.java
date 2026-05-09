package com.thenullcircus.controller.client;

import com.google.gson.JsonObject;
import com.thenullcircus.network.Client;

import java.io.IOException;

public class RegisterController {

    public RegisterController() {}

    public boolean register(String name, String username, String surname,
                            String email, String gender, String password) {
        Client client = new Client();
        try {
            client.connect();

            JsonObject request = new JsonObject();
            request.addProperty("action",   "REGISTER");
            request.addProperty("name",     name);
            request.addProperty("surname",  surname);
            request.addProperty("email",    email);
            request.addProperty("gender",   gender);
            request.addProperty("username", username);
            request.addProperty("password", password);

            client.sendRequest(request);
            JsonObject response = client.readResponse();
            client.disconnect();

            return response.get("status").getAsString().equals("SUCCESS");

        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
            return false;
        }
    }
}