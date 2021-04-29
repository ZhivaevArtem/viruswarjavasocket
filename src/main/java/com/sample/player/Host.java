package com.sample.player;

import com.sample.Game;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Host extends Player {
    private int port;
    private ServerSocket hostSocket;
    private Socket clientSocket;
    private Game game;

    public Host(int port) {
        super("X");
        this.port = port;
        this.game = new Game(gameInfo -> {});
    }

    public boolean waitClient() {
        try {
            hostSocket = new ServerSocket(port, 0, InetAddress.getLocalHost());
            clientSocket = hostSocket.accept();
            this.startListen(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
