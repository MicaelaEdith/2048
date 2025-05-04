package ar.com.game.services;

import java.util.*;

public class Game2048 {

    private int[][] board;
    private int score;
    private final int SIZE = 4;
    private final Random random = new Random();

    public Game2048() {
        board = new int[SIZE][SIZE];
        score = 0;
        spawnNewTile();
        spawnNewTile();
    }

    public int[][] getBoard() {
        return board;
    }

    public int getScore() {
        return score;
    }

    public boolean isGameOver() {
        return !canMove();
    }

    public void move(String direction) {
        boolean moved = false;
        switch (direction.toLowerCase()) {
            case "up": moved = moveUp(); break;
            case "down": moved = moveDown(); break;
            case "left": moved = moveLeft(); break;
            case "right": moved = moveRight(); break;
            default: return;
        }
        if (moved) {
            spawnNewTile();
        }
    }

    private boolean moveLeft() {
        boolean moved = false;
        for (int i = 0; i < SIZE; i++) {
            int[] compressed = compress(board[i]);
            int[] merged = merge(compressed);
            if (!Arrays.equals(board[i], merged)) {
                moved = true;
            }
            board[i] = merged;
        }
        return moved;
    }

    private boolean moveRight() {
        rotateBoard(180);
        boolean moved = moveLeft();
        rotateBoard(180);
        return moved;
    }

    private boolean moveUp() {
        rotateBoard(270);
        boolean moved = moveLeft();
        rotateBoard(90);
        return moved;
    }

    private boolean moveDown() {
        rotateBoard(90);
        boolean moved = moveLeft();
        rotateBoard(270);
        return moved;
    }

    private int[] compress(int[] row) {
        return Arrays.stream(row)
                     .filter(val -> val != 0)
                     .toArray();
    }

    private int[] merge(int[] row) {
        List<Integer> merged = new ArrayList<>();
        int i = 0;
        while (i < row.length) {
            if (i + 1 < row.length && row[i] == row[i + 1]) {
                merged.add(row[i] * 2);
                score += row[i] * 2;
                i += 2;
            } else {
                merged.add(row[i]);
                i++;
            }
        }
        while (merged.size() < SIZE) {
            merged.add(0);
        }
        return merged.stream().mapToInt(Integer::intValue).toArray();
    }

    private void rotateBoard(int degrees) {
        int[][] newBoard = new int[SIZE][SIZE];
        for (int i = 0; i < degrees / 90; i++) {
            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    newBoard[c][SIZE - 1 - r] = board[r][c];
                }
            }
            board = newBoard;
            newBoard = new int[SIZE][SIZE];
        }
    }

    private void spawnNewTile() {
        List<int[]> empty = new ArrayList<>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] == 0) empty.add(new int[]{r, c});
            }
        }
        if (!empty.isEmpty()) {
            int[] pos = empty.get(random.nextInt(empty.size()));
            board[pos[0]][pos[1]] = random.nextDouble() < 0.9 ? 2 : 4;
        }
    }

    private boolean canMove() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] == 0) return true;
                if (r < SIZE - 1 && board[r][c] == board[r + 1][c]) return true;
                if (c < SIZE - 1 && board[r][c] == board[r][c + 1]) return true;
            }
        }
        return false;
    }

    public void printBoard() {
        for (int[] row : board) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println("Score: " + score);
        System.out.println("Game Over: " + isGameOver());
    }
    
    public void restart() {
        board = new int[SIZE][SIZE];
        score = 0;
        spawnNewTile();
        spawnNewTile();
    }

}
