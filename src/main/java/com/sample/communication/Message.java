package com.sample.communication;

import java.io.Serializable;

public class Message implements Serializable {
    public int i = -1;
    public int j = -1;
    public String messageType = null;
    public String[][] field = null;
    public String winner = null;
    public String playerSender = null;
    public String currentPlayer = null;

    public Message(String messageType) {
        this.messageType = messageType;
    }

    public Message(String messageType, int i, int j) {
        this.messageType = messageType;
        this.i = i;
        this.j = j;
    }
}
