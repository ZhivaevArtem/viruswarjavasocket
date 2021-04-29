package com.sample.player;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends Player {
    private Socket socket;
    private String host;
    private int port;

    public Client() {
        super("O");
    }

    public Client(String host, int port) {
        super("O");
        this.host = host;
        this.port = port;
    }

    public boolean connectHost() {
        try {
            this.socket = new Socket(host, port);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
