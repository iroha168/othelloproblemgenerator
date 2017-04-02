import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Game {
    static final int BLACK = 1;
    static final int WHITE = 2;
    static final int EMPTY = 0;
    static final int WALL = -1;
    static final int BOARD_SIZE = 10;
    static final String blackSymbol = " X ";
    static final String whiteSymbol = " O ";
    static final String emptySymbol = " . ";
    boolean isGameEnd;
    int player;
    int count;
    int limit;
    int[][] board = new int[10][10];
    ArrayList<Point> availables = new ArrayList<>();
    ArrayList<int[][]> boardHistory = new ArrayList<>();

    public void start(int limit) {
        this.limit = Math.max(0, limit - 1);
        init();
        play();
    }

    private void drawBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                color(i, j);
            }
            System.out.println();
        }
    }

    private void color(int row, int col) {
        if (isBlackCell(row, col)) {
            System.out.printf(blackSymbol);
        } else if (isWhiteCell(row, col)) {
            System.out.printf(whiteSymbol);
        } else if (isEmptyCell(row, col)) {
            System.out.printf(emptySymbol);
        }
    }

    private boolean isBlackCell(int row, int col) {
        return board[row][col] == BLACK;
    }

    private boolean isWhiteCell(int row, int col) {
        return board[row][col] == WHITE;
    }

    private boolean isEmptyCell(int row, int col) {
        return board[row][col] == EMPTY;
    }

    private void init() {
        player = BLACK;
        isGameEnd = false;
        count = 0;
        initBoard();
    }

    private void initBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (i == 0 || j == 0 || i == BOARD_SIZE - 1 || j == BOARD_SIZE - 1) {
                    board[i][j] = WALL;
                } else if ((i == 5 && j == 4) || (i == 4 && j == 5)) {
                    board[i][j] = BLACK;
                } else if ((i == 4 && j == 4) || (i == 5 && j == 5)) {
                    board[i][j] = WHITE;
                } else {
                    board[i][j] = EMPTY;
                }
            }
        }
    }

    private void play() {
        record();
        while (!isGameEnd) {
            for (int i = 0; i < 2; i++) {
                if (doTurn()) {
                    isGameEnd = false;
                    break;
                } else {
                    changeTurn();
                    isGameEnd = true;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
            drawBoard();

        }
    }


    private boolean doTurn() {
        if (!isMovable()) return false;
        Random random = new Random();
        int index = random.nextInt(availables.size());
        move(availables.get(index).y, availables.get(index).x);
        changeTurn();
        return true;
    }

    private void changeTurn() {
        player = 3 - player;
    }

    private void move(int y, int x) {
        board[y][x] = player;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                int newX = x + i;
                int newY = y + j;
                if (!isOpponent(newY, newX)) continue;
                while (isOpponent(newY, newX)) {
                    newX += i;
                    newY += j;
                }
                if (isPlayer(newY, newX)) {
                    newX -= i;
                    newY -= j;
                    while (isOpponent(newY, newX)) {
                        board[newY][newX] = player;
                        newX -= i;
                        newY -= j;
                    }
                }
            }
        }
        count++;
        record();
    }

    private void record() {
        int[][] boardForDuplication = new int[10][10];
        for (int i = 0; i < board.length; i++) {
            boardForDuplication[i] = new int[board.length];
            System.arraycopy(board[i], 0, boardForDuplication[i], 0, board[i].length);
        }
        boardHistory.add(boardForDuplication);
    }

    private boolean isMovable() {
        availables.clear();
        for (int i = 1; i < BOARD_SIZE - 1; i++) {
            for (int j = 1; j < BOARD_SIZE - 1; j++) {
                checkCell(i, j, false);
            }
        }
        return availables.size() > 0;
    }

    private boolean checkCell(int row, int col, boolean once) {
        if (board[row][col] != EMPTY) return false;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;

                int newX = col + i;
                int newY = row + j;
                if (!isOpponent(newY, newX)) continue;
                while (isOpponent(newY, newX)) {
                    newX += i;
                    newY += j;
                }
                if (isPlayer(newY, newX)) {
                    availables.add(new Point(col, row));
                    if (once) return true;
                }
            }
        }
        return availables.size() > 0;
    }

    private boolean isPlayer(int row, int col) {
        return board[row][col] == player;
    }

    private boolean isOpponent(int row, int col) {
        return board[row][col] == getOpponent();
    }

    private int getOpponent() {
        return 3 - player;
    }

}
