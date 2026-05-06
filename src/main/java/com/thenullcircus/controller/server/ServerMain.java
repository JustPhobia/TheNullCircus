package com.thenullcircus.controller.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) {
        try{
            ServerSocket serverSocket = new ServerSocket(1234);

            while(true){
                Socket socket = serverSocket.accept();

                ClientHandler handler = new ClientHandler(socket);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
