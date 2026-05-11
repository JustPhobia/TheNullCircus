package com.thenullcircus.controller.server;

import com.thenullcircus.controller.services.JokeOfDayService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain {
    private static final Logger logger = Logger.getLogger(ServerMain.class.getName());
    public static void main(String[] args) {
        logger.info("[SERVER_START] Initializing The Null Circus server components...");
        JokeOfDayService jokeOfDayService = new JokeOfDayService();
        jokeOfDayService.start();

        try(ServerSocket serverSocket = new ServerSocket(1234);){

            logger.info("[SERVER_START] Listening for connections on port 1234...");
            System.out.println("Server started on port 1234");

            while (true) {
                Socket socket = serverSocket.accept();
                logger.info("[NETWORK] New connection accepted from " + socket.getInetAddress().getHostAddress());
                System.out.println("Accepted connection from " + socket.getInetAddress().getHostName());

                ClientHandler handler = new ClientHandler(socket, jokeOfDayService);
                new Thread(handler).start();
                logger.fine("[THREADING] ClientHandler thread spawned for " + socket.getInetAddress());
            }
        } catch (IOException e) {
            jokeOfDayService.stop();
            logger.log(Level.SEVERE, "[FATAL] Server encountered a critical network error on port 1234: " + e.getMessage(), e);
            System.out.println("Server Error: " + e.getMessage());
        }
    }
}