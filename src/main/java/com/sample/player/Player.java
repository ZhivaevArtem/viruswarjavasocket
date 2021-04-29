package com.sample.player;

import com.sample.communication.Communicator;
import com.sample.communication.Message;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.Socket;

public abstract class Player extends Application {
    private String whoAmI;
    private String[][] field = new String[10][10];
    private Socket enemySocket;
    private Communicator communicator;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1280, 720);

        primaryStage.setResizable(false);
        primaryStage.setTitle("SAMPLE TEXT");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    protected Player(String player) {
        this.whoAmI = player;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                field[i][j] = "";
            }
        }
    }

    protected void startListen(Socket enemySocket) {
        this.enemySocket = enemySocket;
        this.communicator = new Communicator(enemySocket);
        this.communicator.startListen();
    }

    public void turn(int i, int j) {
        Message message = new Message("turn");
        message.i = i;
        message.j = j;
        this.communicator.emit(message);
    }

    public void pass() {
        Message message = new Message("pass");
        this.communicator.emit(message);
    }

    private void onRedraw(Message message) {
        // TODO
    }

    private void onGameStart(Message message) {
        // TODO
    }

    private void onGameOver(Message message) {
        // TODO
    }

    private void onGameHtml(Message message) {
        // TODO
    }

    private void onRedirectHome(Message message) {
        // TODO
    }
}
