package com.sample.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class Communicator {
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private Map<String, Consumer<Message>> actions = new HashMap<>();
    private Listener listener;
    private String whoAmI;

    public Communicator(Socket socket, String whoAmI) {
        this.whoAmI = whoAmI;
        try {
            this.os = new ObjectOutputStream(socket.getOutputStream());
            this.is = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Listener extends Thread {
        private boolean isRun = true;

        public void stopListener() {
            this.isRun = false;
        }

        public Listener() {
        }

        @Override
        public void run() {
            while (isRun) {
                try {
                    Object obj = is.readObject();
                    if (obj instanceof Message) {
                        Message message = (Message)obj;
                        boolean b = actions.containsKey(message.messageType);
                        for (String k : actions.keySet()) {
                            if (k.equals(message.messageType)) {
                                b = true;
                                break;
                            }
                        }
                        if (b) {
                            actions.get(message.messageType).accept(message);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startListen() {
        this.listener = new Listener();
        this.listener.start();
    }

    public void stopListen() {
        this.listener.stopListener();
    }

    public void on(String messageType, Consumer<Message> action) {
        this.actions.put(messageType, action);
    }

    public void emit(String messageType, Message message) {
        try {
            message.messageType = messageType;
            message.playerSender = this.whoAmI;
            os.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void emitAll(String messageType, Message message) {
        message.playerSender = this.whoAmI;
        message.messageType = messageType;
        if (actions.containsKey(messageType)) {
            actions.get(messageType).accept(message);
        }
        emit(messageType, message);
    }
}
