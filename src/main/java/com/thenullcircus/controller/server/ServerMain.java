
package com.thenullcircus.controller.server;

import com.thenullcircus.controller.services.JokeOfDayService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) {
        try{
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Server started on port 1234");

            while (true) {
                Socket socket = serverSocket.accept();
                JokeOfDayService jokeOfDayService = new JokeOfDayService();
                System.out.println("Accepted connection from " + socket.getInetAddress().getHostName());

                ClientHandler handler = new ClientHandler(socket, jokeOfDayService);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}