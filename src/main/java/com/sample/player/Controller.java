package com.sample.player;

import com.sample.communication.Communicator;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;

public class Controller {

    public GridPane gameFieldGrid;
    private Communicator communicator;
    private GraphicsContext[][] canvasField;

    public void setCommunicator(Communicator communicator) {
        this.communicator = communicator;
        this.communicator.startListen();
    }

    public void initialize() {
        int rows = 10;
        int cols = 10;
        this.canvasField = new GraphicsContext[rows][cols];
        int cellh = (int) this.gameFieldGrid.getPrefHeight() / rows;
        int cellw = (int) this.gameFieldGrid.getPrefWidth() / cols;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Canvas canvas = new Canvas(cellw, cellh);
                final int row = i;
                final int col = j;
                canvas.setOnMouseClicked(event -> onFieldClick(row, col));
                this.gameFieldGrid.add(canvas, i, j);
                this.canvasField[i][j] = canvas.getGraphicsContext2D();
//                this.canvasField[i][j].fillRect(0, 0, 50, 50);
            }
        }
    }

    private void onFieldClick(int i, int j) {
        GraphicsContext ctx = this.canvasField[i][j];
        int w = (int) ctx.getCanvas().getWidth();
        int h = (int) ctx.getCanvas().getHeight();
        ctx.beginPath();
        ctx.moveTo(0, 0);
        ctx.lineTo(w, h);
        ctx.stroke();
    }
}
