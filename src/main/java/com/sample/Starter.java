package com.sample;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

class Starter {

    public static void main(String[] args) {
        switch (args[0]) {
            case "host":
                runHost(Integer.parseInt(args[1]));
                break;
            case "connect":
                String[] split = args[1].split(":");
                int port = Integer.parseInt(split[1]);
                String host = split[0];
                runClient(host, port);
                break;
        }
    }

    private static void runHost(int port) {
        try {
            ServerSocket hostSocket = new ServerSocket(port, 0, InetAddress.getLocalHost());
            System.out.println("Waiting...");
            Socket clientSocket = hostSocket.accept();
            System.out.println("Connected...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runClient(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
