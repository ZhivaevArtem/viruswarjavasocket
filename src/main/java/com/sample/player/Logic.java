package com.sample.player;

import com.sample.Game;
import com.sample.communication.Communicator;
import com.sample.communication.Message;
import javafx.application.Platform;

import java.net.Socket;
import java.util.Locale;
import java.util.Objects;

public class Logic {

    private static Logic instance = new Logic();
    public static Logic getInstance() {
        return instance;
    }

    private boolean isHost;
    private boolean isGameEnded;
    private boolean isMyTurn;
    private Communicator communicator;
    private Controller controller;
    private int rows = 10;
    private int cols = 10;
    private String[][] gameField = new String[rows][cols];
    private Game game;

    private String whoAmI() {
        return this.isHost ? Game.X : Game.O;
    }
    /**
     * Call once
     * @param isHost
     * @param socket
     * @param controller
     */
    public void init(boolean isHost, Socket socket, Controller controller) {
        this.isHost = isHost;
        this.controller = controller;
        this.communicator = new Communicator(socket, this.whoAmI());
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                gameField[i][j] = Game.EMPTY;
            }
        }

        this.communicator.on("game_start", this::onGameStart);
        this.communicator.on("redraw", this::onRedraw);
        this.communicator.on("game_over", this::onGameOver);
        if (this.isHost) {
            this.communicator.on("turn", this::onTurn);
            this.communicator.on("pass", this::onPass);
        }
        this.communicator.startListen();
    }

    private void onGameOver(Message data) {
        this.isGameEnded = true;
        this.communicator.stopListen();
        String winner = data.winner.toLowerCase();

        this.controller.getPassButton().setOnMouseClicked(event -> {});
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                this.controller.getCanvasCell(i, j).setOnMouseClicked(event -> {});
            }
        }

        String status = winner.equals(this.whoAmI().toLowerCase()) ? "You win!" : "You lose :(";
        String[][] field = data.field;
        this.fieldRedraw(field, status);
    }

    /**
     * Call from host
     */
    public void startGame() {
        if (isHost) {
            this.communicator.emitAll("game_start", new Message("game_start"));
        }
    }

    /**
     * Start new game handler
     * @param data not used
     */
    private void onGameStart(Message data) {
        this.isGameEnded = false;
        if (isHost) {
            this.game = new Game(gameInfo -> {
                Message m = new Message("game_over");
                m.winner = gameInfo.winner;
                m.field = gameInfo.field;
                communicator.emitAll("game_over", m);
            });
            this.isMyTurn = true;
        } else {
            this.game = null;
            this.isMyTurn = false;
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.gameField[i][j] = Game.EMPTY;
            }
        }
        this.fieldRedraw(this.gameField, this.isMyTurn ? "Your turn" : "Enemy's turn");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Message m = new Message("turn");
                m.i = i;
                m.j = j;
                this.controller.getCanvasCell(i, j).setOnMouseClicked(event -> {
                    communicator.emitAll("turn", m);
                });
            }
        }
        Message m = new Message("pass");
        this.controller.getPassButton().setOnMouseClicked(event -> {
            communicator.emitAll("pass", m);
        });
    }

    private void fieldRedraw(String status) {
        this.fieldRedraw(null, status);
    }
    private void fieldRedraw(String[][] field) {
        this.fieldRedraw(field, null);
    }
    private void fieldRedraw(String[][] field, String status) {
        Platform.runLater(() -> {
            if (field != null)
                this.controller.draw(field);
            if (status != null)
                this.controller.setStatus(status);
        });
    }

    /**
     * Host-side handler
     * @param data
     */
    private void onTurn(Message data) {
        int i = data.i;
        int j = data.j;
        String sender = data.playerSender;

        if (!Objects.equals(sender.toLowerCase(), this.game.getCurrentPlayer().toLowerCase())) {
            return;
        }
        if (this.game.turn(i, j)) {
            Message message = new Message("redraw");
            message.currentPlayer = this.game.getCurrentPlayer();
            message.field = new String[10][10];
            for (int k = 0; k < 10; k++) {
                for (int l = 0; l < 10; l++) {
                    message.field[k][l] = this.game.getField(k, l);
                }
            }
            this.communicator.emitAll("redraw", message);
        }
    }

    /**
     * Host-side handler
     * @param data
     */
    private void onPass(Message data) {
        String sender = data.playerSender;
        if (!Objects.equals(sender.toLowerCase(), this.game.getCurrentPlayer().toLowerCase())) {
            return;
        }
        if (this.game.pass()) {
            Message message = new Message("redraw");
            message.currentPlayer = game.getCurrentPlayer();
            this.communicator.emitAll("redraw", message);
        }
    }

    private void onRedraw(Message data) {
        if (this.isGameEnded) return;
        String currentPlayer = data.currentPlayer;
        String[][] field = data.field;

        this.isMyTurn = Objects.equals(currentPlayer.toLowerCase(), this.whoAmI().toLowerCase());
        this.gameField = field;
        this.fieldRedraw(this.gameField, this.isMyTurn ? "Your turn" : "Enemy's turn");
    }
}
