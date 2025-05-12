package ar.com.game.repository;

import ar.com.game.domain.Duel;

public class DuelTest {

    public static void main(String[] args) {
        DuelRepository repo = new DuelRepository();

        int retadorId = 1;
        int desafiadoId = 2;

        Duel duelo = new Duel(retadorId, desafiadoId);

        try {
            boolean creado = repo.createDuel(duelo);
            if (creado) {
                System.out.println("Duelo creado con éxito. ID generado: " + duelo.getId());
            } else {
                System.out.println("No se pudo crear el duelo.");
            }
        } catch (Exception e) {
            System.err.println("Error al crear duelo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
