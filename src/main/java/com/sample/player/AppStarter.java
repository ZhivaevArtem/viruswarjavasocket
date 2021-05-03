package com.sample.player;

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
import java.util.List;

public class AppStarter extends Application {

    private void runAsHost(Controller controller, int port) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            ServerSocket hostSocket = new ServerSocket(port);
            Socket enemySocket = hostSocket.accept();
            GameHandler.getInstance().init(true, enemySocket, controller);
            GameHandler.getInstance().startGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runAsClient(Controller controller, String host, int port) {
        try {
            Socket enemySocket = new Socket(host, port);
            GameHandler.getInstance().init(false, enemySocket, controller);
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

        Controller controller = (Controller) loader.getController();

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
                primaryStage.setTitle("X");
                runAsHost(controller, Integer.parseInt(args.get(1)));
                break;
            case "connect":
                primaryStage.setTitle("O");
                String[] split = args.get(1).split(":");
                int port = Integer.parseInt(split[1]);
                String host = split[0];
                runAsClient(controller, host, port);
                break;
        }
    }
}
