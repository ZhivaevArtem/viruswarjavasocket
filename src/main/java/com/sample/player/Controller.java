package com.sample.player;

import com.sample.Game;
import com.sample.communication.Communicator;
import com.sample.communication.Message;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.net.Socket;

public class Controller {

    public GridPane gameFieldGrid;
    public Label statusText;
    private Canvas[][] canvasField;
    public boolean isHost;
    public Communicator communicator;
    public String[][] gameField;
    public String player;

    public void setCommunicator(Communicator communicator) {
        this.communicator = communicator;

        this.communicator.on("turn", this::onTurn);

        this.communicator.on("ready", this::onConnectionEstablished);
        this.communicator.emit("ready", new Message("ready"));
        this.communicator.startListen();
    }

    public void initialize() {
        int rows = 10;
        int cols = 10;
        this.canvasField = new Canvas[rows][cols];
        this.gameField = new String[rows][cols];
        int cellh = (int) this.gameFieldGrid.getPrefHeight() / rows;
        int cellw = (int) this.gameFieldGrid.getPrefWidth() / cols;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.gameField[i][j] = Game.EMPTY;
                Canvas canvas = new Canvas(cellw, cellh);
                this.gameFieldGrid.add(canvas, i, j);
                this.canvasField[i][j] = canvas;
            }
        }
    }

    private void onConnectionEstablished(Message data) {
        Platform.runLater(() -> {
            this.statusText.setText("Enemy ready");
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    int finalI = i;
                    int finalJ = j;
                    canvasField[i][j].setOnMouseClicked(event -> {
                        this.gameField[finalI][finalJ] = this.isHost ? Game.X : Game.O;
                        redraw();
                        this.emitTurn(finalI, finalJ);
                    });
                }
            }
        });
    }

    private void onTurn(Message data) {
        int i = data.i;
        int j = data.j;

        this.gameField[i][j] = this.isHost ? Game.O : Game.X;
        Platform.runLater(this::redraw);
    }

    private void emitTurn(int i, int j) {
        Message message = new Message("turn");
        message.i = i;
        message.j = j;
        this.communicator.emit("turn", message);
    }

    private void redraw() {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    GraphicsContext ctx = canvasField[i][j].getGraphicsContext2D();
                    double w = canvasField[i][j].getWidth();
                    double h = canvasField[i][j].getHeight();
                    ctx.clearRect(0, 0, w, h);
                    if (gameField[i][j] == null) {
                        gameField[i][j] = Game.EMPTY;
                    }
                    switch (gameField[i][j]) {
                        case Game.X:
                            ctx.beginPath();
                            ctx.moveTo(0, 0);
                            ctx.lineTo(w, h);
                            ctx.moveTo(w, 0);
                            ctx.lineTo(0, h);
                            ctx.stroke();
                            ctx.closePath();
                            break;
                        case Game.O:
                            ctx.strokeOval(0, 0, w, h);
                            break;
                        default:
                            break;
                    }
                }
            }
    }
}
