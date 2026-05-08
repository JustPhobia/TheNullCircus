package com.thenullcircus.controller.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerConnection {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public void connect(){
        try{
            socket = new Socket("192.168.8.164", 1234);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String sendMessage(String message){
        try{
            out.println(message);
            return in.readLine();
        } catch (IOException e) {
            return("Error");
        }
    }
}
