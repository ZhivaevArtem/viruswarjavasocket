package com.sample.player;

import com.sample.Game;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class Controller {

    public GridPane gameFieldGrid;
    public Label statusText;
    public Button passButton;

    private Canvas[][] canvasField;
    private int rows;
    private int cols;

    public void initialize() {
        this.rows = 10;
        this.cols = 10;
        this.canvasField = new Canvas[rows][cols];
        int cellh = (int) this.gameFieldGrid.getPrefHeight() / rows;
        int cellw = (int) this.gameFieldGrid.getPrefWidth() / cols;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Canvas canvas = new Canvas(cellw, cellh);
                this.gameFieldGrid.add(canvas, j, i);
                this.canvasField[i][j] = canvas;
            }
        }
    }

    public Canvas getCanvasCell(int i, int j) {
        return canvasField[i][j];
    }

    public void draw(String[][] gameField) {
        for (int i = 0; i < gameField.length; i++) {
            String[] row = gameField[i];
            for (int j = 0; j < row.length; j++) {
                GraphicsContext ctx = canvasField[i][j].getGraphicsContext2D();
                ctx.setLineWidth(2);
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
                    case Game.X_KILLED:
                        ctx.setFill(Color.RED);
                        ctx.fillRect(0, 0, w, h);
                        ctx.beginPath();
                        ctx.moveTo(0, 0);
                        ctx.lineTo(w, h);
                        ctx.moveTo(w, 0);
                        ctx.lineTo(0, h);
                        ctx.stroke();
                        ctx.closePath();
                        break;
                    case Game.O_KILLED:
                        ctx.setFill(Color.RED);
                        ctx.fillRect(0, 0, w, h);
                        ctx.strokeOval(0, 0, w, h);
                    default:
                        break;
                }
            }
        }
    }

    public void setStatus(String s) {
        this.statusText.setText(s);
    }

    public Button getPassButton() {
        return this.passButton;
    }
}
