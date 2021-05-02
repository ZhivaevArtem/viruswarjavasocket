package com.sample.player;

import com.sample.Game;
import com.sample.communication.Communicator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class AppStarter extends Application {

    private int port;
    private String host;
    private ServerSocket hostSocket;
    private Socket enemySocket;
    private Game game;
    private Controller controller;

    private void runAsHost(int port) {
        this.port = port;
        try {
            this.host = InetAddress.getLocalHost().getHostAddress();
            this.hostSocket = new ServerSocket(port);
            this.enemySocket = this.hostSocket.accept();
            this.game = new Game(gameInfo -> {});
            this.controller.isHost = true;
            this.controller.setCommunicator(new Communicator(this.enemySocket));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runAsClient(String host, int port) {
        this.port = port;
        this.host = host;
        try {
            this.enemySocket = new Socket(host, port);
            this.controller.isHost = false;
            this.controller.setCommunicator(new Communicator(this.enemySocket));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println(getClass().getResource("."));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1280, 720);

        this.controller = (Controller) loader.getController();

        primaryStage.setResizable(false);
        primaryStage.setTitle("SAMPLE TEXT");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        List<String> args = getParameters().getRaw();

        switch (args.get(0)) {
            case "host":
                runAsHost(Integer.parseInt(args.get(1)));
                break;
            case "connect":
                String[] split = args.get(1).split(":");
                int port = Integer.parseInt(split[1]);
                String host = split[0];
                runAsClient(host, port);
                break;
        }
    }
}
