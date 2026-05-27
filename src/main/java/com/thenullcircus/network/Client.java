package com.thenullcircus.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private final Logger logger = Logger.getLogger(Client.class.getName());
    private final String host;
    private final int port;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Client() {
        Dotenv dotenv = Dotenv.load();
        this.host = dotenv.get("SERVER_HOST");
        this.port = Integer.parseInt(dotenv.get("SERVER_PORT"));
        logger.fine("[NETWORK_INIT] Client config loaded: Host=" + host + ", Port=" + port);
    }

    public void connect() throws IOException {
        logger.info("[NETWORK] Attempting to establish socket connection to " + host + ":" + port);
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            logger.info("[NETWORK] Connection established successfully.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "[NETWORK_FAIL] Unable to connect to " + host + ":" + port + ". Error: " + e.getMessage(), e);
            throw e;
        }
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                logger.info("[NETWORK] Socket connection closed cleanly.");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "[NETWORK_ERROR] Issue while closing network socket: " + e.getMessage(), e);
        }
    }

    public void sendRequest(JsonObject request) throws IOException {
        if (out == null) {
            logger.severe("[NETWORK_ERR] sendRequest called before connect() or output stream is null.");
            throw new IOException("No output stream. Call connect() first.");
        }
        String jsonPayload = request.toString();
        out.println(jsonPayload);
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("[JSON_OUT] SENT: " + jsonPayload.replaceAll("\"password\"\\s*:\\s*\"[^\"]+\"", "\"password\":\"***\""));
        }
    }

    public JsonObject readResponse() throws IOException {
        if (in == null) {
            logger.severe("[NETWORK_ERR] readResponse called before connect() or input stream is null.");
            throw new IOException("No input stream. Call connect() first.");
        }
        logger.fine("[NETWORK] Waiting for server response...");
        String line = in.readLine();
        if (line == null) {
            logger.warning("[NETWORK_EOF] Server terminated stream unexpectedly (received null line).");
            throw new IOException("Server closed the connection.");
        }
        logger.fine("[JSON_IN] RECEIVED: " + line);
        return JsonParser.parseString(line).getAsJsonObject();
    }
}