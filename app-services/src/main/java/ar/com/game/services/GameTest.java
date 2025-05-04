package ar.com.game.services;

import java.util.Scanner;

public class GameTest {
    public static void main(String[] args) {
        Game2048 game = new Game2048();
        game.printBoard();

        Scanner scanner = new Scanner(System.in);
        while (!game.isGameOver()) {
            System.out.print("Move (up/down/left/right/restart): ");
            String move = scanner.nextLine();
            if (move.equals("restart"))
                game.restart();
            else 
            game.move(move);
            
            
            game.printBoard();
        }
        System.out.println("Game Over!");
    }
}
