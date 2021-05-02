package com.sample;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class Game {
    public static final String X = "x";
    public static final String O = "o";
    public static final String X_KILLED = "O";
    public static final String O_KILLED = "X";
    public static final String EMPTY = "";

    private boolean gameEnded;
    private LocalDateTime startTime = LocalDateTime.now();
    private LocalDateTime endTime = null;
    private boolean lastTurnPass = false;
    private String currentPlayer = X;
    private int turnCount = 0;
    private int subTurnCount = 0;
    private int xCount = 0;
    private int oCount = 0;
    private String[][] field = new String[10][10];
    private Boolean[][] xGraph = new Boolean[100][100];
    private Boolean[][] oGraph = new Boolean[100][100];
    private Consumer<GameInfo> gameEndedCallback;

    public class GameInfo {
        public String winner;
        public LocalDateTime startTime;
        public LocalDateTime endTime;
        public int turnCount;

        public GameInfo(String winner, LocalDateTime startTime, LocalDateTime endTime, int turnCount) {
            this.winner = winner;
            this.startTime = startTime;
            this.endTime = endTime;
            this.turnCount = turnCount;
        }
    }

    public Game() {
        this(gameInfo -> {});
    }

    public Game(Consumer<GameInfo> gameEndedCallback) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                field[i][j] = EMPTY;
            }
        }
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                xGraph[i][j] = false;
                oGraph[i][j] = false;
            }
        }
        this.gameEndedCallback = gameEndedCallback;
    }

    // region: public functions
    public boolean turn(int row, int col) {
        if (gameEnded) {
            return false;
        }
        String enemy = X.equals(currentPlayer) ? O : X;
        String killed = X.equals(enemy) ? X_KILLED : O_KILLED;
        if (isAvailable(currentPlayer, row, col)) {
            if (enemy.equals(getField(row, col))) {
                setField(row, col, killed);
            } else {
                setField(row, col, currentPlayer);
            }
            endSubTurn();
            return true;
        }
        return false;
    }

    public boolean pass() {
        if (gameEnded) {
            return false;
        }
        if (subTurnCount != 0) {
            return false;
        }
        endTurn(true);

        return true;
    }

    public String[][] getField() {
        return field;
    }

    public String getField(int row, int col) {
        return this.field[row][col];
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public boolean isEnded() {
        return gameEnded;
    }
    // endregion

    // region: private functions
    private void setField(int row, int col, String e) {
        switch (getField(row, col)) {
            case X:
                xCount -= 1;
                if (xCount == 0) {
                    field[row][col] = e;
                    endGame(O);
                }
                break;
            case O:
                oCount -= 1;
                if (oCount == 0) {
                    field[row][col] = e;
                    endGame(X);
                }
                break;
        }
        switch (e) {
            case X:
                xCount += 1;
                break;
            case O:
                oCount += 1;
                break;
        }
        field[row][col] = e;
        if (field[0][9] == X) {
            endGame(X);
        } else if (field[9][0] == O) {
            endGame(O);
        }
        // Update graphs
        xGraph = buildGraph(X, xGraph);
        oGraph = buildGraph(O, oGraph);
    }

    private boolean isAvailable(String player, int row, int col) {
        if (xCount == 0 && X.equals(player)) {
            return row == 9 && col == 0 && EMPTY.equals(getField(row, col));
        }
        if (oCount == 0 && O.equals(player)) {
            return row == 0 && col == 9 && EMPTY.equals(getField(row, col));
        }
        String enemy = X.equals(player) ? O : X;
        String killed = X.equals(player) ? O_KILLED : X_KILLED;
        if (!enemy.equals(getField(row, col)) && !EMPTY.equals(getField(row, col))) {
            return false;
        }
        Boolean[][] graph;
        if (X.equals(player)) {
            graph = xGraph;
        } else {
            graph = oGraph;
        }
        Integer[][] around = aroundIndexes(row, col);
        for (Integer[] e : around) {
            if (player.equals(getField(e[0], e[1]))) {
                return true;
            }
        }
        for (Integer[] e : around) {
            if (killed.equals(getField(e[0], e[1]))) {
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (player.equals(getField(i, j))) {
                            if (findPath(graph, e[0] * 10 + e[1], i * 10 + j)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isAnyAvailableTurn(String player) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (isAvailable(player, i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void endSubTurn() {
        subTurnCount += 1;
        if (subTurnCount == 3 || !isAnyAvailableTurn(currentPlayer)) {
            endTurn(false);
        }
    }

    private void endTurn(boolean isPass) {
        turnCount += 1;
        currentPlayer = X.equals(currentPlayer) ? O : X;
        subTurnCount = 0;
        //if (!isAnyAvailableTurn(currentPlayer)) {
        //endGame(currentPlayer === X ? O : X);
        //}
        if (lastTurnPass && isPass) {
            endGame(EMPTY); // Draw
        }
        lastTurnPass = isPass;
    }

    private void endGame(String winner) {
        this.endTime = LocalDateTime.now();
        this.gameEnded = true;
        this.gameEndedCallback.accept(new GameInfo(winner,
                this.startTime, this.endTime, this.turnCount));
    }

    private Integer[][] aroundIndexes(int row, int col) {
        Integer[][] inx = new Integer[8][2];
        inx[0][0] = row - 1; inx[0][1] = col - 1;
        inx[1][0] = row - 1; inx[1][1] = col;
        inx[2][0] = row - 1; inx[2][1] = col + 1;
        inx[3][0] = row;     inx[3][1] = col - 1;
        inx[4][0] = row;     inx[4][1] = col + 1;
        inx[5][0] = row + 1; inx[5][1] = col - 1;
        inx[6][0] = row + 1; inx[6][1] = col;
        inx[7][0] = row + 1; inx[7][1] = col + 1;
        List<Integer[]> res = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if (!(inx[i][0] < 0 || inx[i][0] > 9 || inx[i][1] < 0 || inx[i][1] > 9)) {
                res.add(inx[i]);
            }
        }
        return (Integer[][]) res.toArray();
    }

    private Boolean[][] buildGraph(String player, Boolean[][] graph) {
        String killed = X.equals(player) ? O_KILLED : X_KILLED;

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                graph[i][j] = false;
            }
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (player.equals(field[i][j]) || killed.equals(field[i][j])) {
                    for (Integer[] e : aroundIndexes(i, j)) {
                        if (player.equals(field[e[0]][e[1]]) || killed.equals(field[e[0]][e[1]])) {
                            int ind1 = i * 10 + j;
                            int ind2 = e[0] * 10 + e[1];
                            graph[ind1][ind2] = true;
                            graph[ind2][ind1] = true;
                        }
                    }
                }
            }
        }
        return graph;
    }

    private int h(int start, int end) {
        int startI = start / 10;
        int startJ = start % 10;
        int endI = end / 10;
        int endJ = end % 10;
        return Math.abs(startI - endI) + Math.abs(startJ - endJ);
    }

    boolean findPath(Boolean[][] graph, int start, int end) {
        List<Integer> closed = new LinkedList<>();
        List<Integer> open = new LinkedList<>(); open.add(start);
        Map<Integer, Integer> from = new HashMap<>();
        Map<Integer, Integer> g = new HashMap<>();
        Map<Integer, Integer> f = new HashMap<>();
        g.put(start, 0);
        f.put(start, g.get(start) + h(start, end));
        while (open.size() > 0) {
            Integer curr = open.get(0);
            if (curr == end)
                return true;
            open.remove(curr);
            closed.add(curr);
            List<Integer> neighbours = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                if (graph[curr][i] && closed.indexOf(i) == -1) {
                    neighbours.add(i);
                }
            }
            for (Integer elem : neighbours) {
                Integer tmpG = g.get(curr) + 1;
                if (open.indexOf(elem) == -1 || tmpG < g.get(elem)) {
                    from.put(elem, curr);
                    g.put(curr, tmpG);
                    f.put(elem, g.get(elem) + h(elem, end));
                }
                if (open.indexOf(elem) == -1) {
                    open.add(elem);
                }
            }
        }
        return false;
    }
    // endregion
}
