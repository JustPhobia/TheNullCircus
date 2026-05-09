
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
        JokeOfDayService jokeOfDayService = new JokeOfDayService();
        jokeOfDayService.start();
        try(ServerSocket serverSocket = new ServerSocket(1234);){

            System.out.println("Server started on port 1234");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Accepted connection from " + socket.getInetAddress().getHostName());

                ClientHandler handler = new ClientHandler(socket, jokeOfDayService);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            jokeOfDayService.stop();
            logger.log(Level.SEVERE, "Server encountered a fatal network error: " + e.getMessage());
            System.out.println("Server Error: " + e.getMessage());
        }
    }
}
