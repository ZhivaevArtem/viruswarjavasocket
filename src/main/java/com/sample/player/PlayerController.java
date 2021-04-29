package com.sample.player;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;

public class PlayerController {

    public GridPane gameFieldGrid;

    private Canvas[][] canvasesField;

    public void initialize() {
        canvasesField = new Canvas[this.gameFieldGrid.getRowCount()][this.gameFieldGrid.getColumnCount()];
        for (int i = 0; i < this.gameFieldGrid.getRowCount(); i++) {
            for (int j = 0; j < this.gameFieldGrid.getColumnCount(); j++) {
                Canvas canvas = new Canvas();
                this.gameFieldGrid.add(canvas, i, j);
                GraphicsContext ctx = canvas.getGraphicsContext2D();
                ctx.moveTo(0, 0);
                ctx.lineTo(canvas.getHeight(), canvas.getWidth());
            }
        }
    }
}
