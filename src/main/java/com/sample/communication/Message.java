package com.sample.communication;

import java.io.Serializable;

public class Message implements Serializable {
    public int i = -1;
    public int j = -1;
    public String messageType = null;
    public Integer[][] field = null;
    public String winner = null;
    public String player = null;
    public String currentPlayer = null;

    public Message(String messageType) {
        this.messageType = messageType;
    }

    public Message(String messageType, int i, int j) {
        this.messageType = messageType;
        this.i = i;
        this.j = j;
    }

    public Message(String messageType, Integer[][] field, String winner) {
        this.messageType = messageType;
        this.field = field;
        this.winner = winner;
    }
}
