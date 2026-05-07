package com.thenullcircus.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.fasterxml.jackson.databind.util.JSONPObject;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    private Logger logger = Logger.getLogger(Client.class.getName());

    //Instance Variables
    private String host;
    private int port;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    //Constructor
    public Client(String host, int port) {
        Dotenv dotenv = Dotenv.load();
        this.host = dotenv.get("HOST");
        this.port = Integer.parseInt(dotenv.get("PORT"));
    }

    //Connect to socket
    public void connect() throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        logger.info("Connected to %s:%d" + host + ": " + port);
    }

    public void disconnect() {
        try{
            if (socket != null && !socket.isClosed()) {
                socket.close();
                logger.info("Disconnected from server");

            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while closing socket", e);
        }
    }

    public void sendRequest(JsonObject request) throws IOException {
        if (out == null) {
            throw new IOException("Not connected. Call connect() first.");
        }
        out.println(request.toString());
        logger.fine("Sent: " + request);

    }

    public JsonObject readResponse() throws IOException {
        if (in == null) {
            throw new IOException("Not connected. Call connect() first.");
        }
        String line = in.readLine();
        if (line == null) {
            throw new IOException("Server closed the connection.");
        }
        logger.fine("Received: " + line);
        return JsonParser.parseString(line).getAsJsonObject();
    }


}
