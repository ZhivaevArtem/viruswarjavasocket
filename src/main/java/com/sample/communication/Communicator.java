package com.sample.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class Communicator {
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private Map<String, Consumer<Message>> actions;
    private Listener listener;

    public Communicator(Socket socket) {
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
                        if (actions.containsKey(message.messageType)) {
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

    public void on(String messageType, Consumer<Message> action) {
        this.actions.put(messageType, action);
    }

    public void emit(Message message) {
        try {
            os.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
