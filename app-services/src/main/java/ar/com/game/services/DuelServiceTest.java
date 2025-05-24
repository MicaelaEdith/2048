package ar.com.game.services;

import java.util.Scanner;

import ar.com.game.domain.Duel;


public class DuelServiceTest {

	public static void main(String[] args) {
		UserService userService = new UserService();
		ServiceResponse response = userService.loginUserWithDuelCheck("juan@test.com", "clave123");

		System.out.println(response.getMessage());
		if (response.isSuccess() && response.getExtra() != null) {
		    System.out.println("Duelo pendiente.Ingresar 'ok' para jugarlo.");
		    Scanner scanner = new Scanner(System.in);
		    String input = scanner.nextLine();
		    if ("ok".equalsIgnoreCase(input)) {
		        Duel duel = (Duel) response.getExtra();
		        System.out.println("El duelo ID " + duel.getId() + " está listo para ser jugado.");
		    }
		}


	}

}
