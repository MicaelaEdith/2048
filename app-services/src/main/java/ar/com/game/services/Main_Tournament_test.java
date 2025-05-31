package ar.com.game.services;

import ar.com.game.domain.Tournament;
import ar.com.game.repository.DatabaseConnector;
import ar.com.game.repository.TournamentRepository;
import ar.com.game.repository.TournamentRepositoryImpl;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class Main_Tournament_test {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            TournamentRepository repo = new TournamentRepositoryImpl(conn);
            TournamentService service = new TournamentService(repo);

            // Crear torneo
            Tournament torneo = service.createTournament("Test desde main", 1);
            System.out.println("Torneo creado: " + torneo.getName() + " (ID: " + torneo.getId() + ")");

            // Simular usuarios uniéndose
            service.joinTournament(torneo.getId(), 100);
            service.joinTournament(torneo.getId(), 101);
            service.joinTournament(torneo.getId(), 102);

            // Mostrar participantes
            List<Integer> participants = service.getParticipants(torneo.getId());
            System.out.println("Participantes del torneo " + torneo.getId() + ": " + participants);

            // Empezar torneo
            service.startTournament(torneo.getId());
            System.out.println("Torneo iniciado: " + torneo.isStarted());

            // Terminar torneo y asignar ganador
            //service.endTournament(torneo.getId(), 101);
            //System.out.println("Torneo terminado. Ganador: " + torneo.getWinnerId());

            // Intentar acceder nuevamente (ya no está en memoria)
            List<Integer> again = service.getParticipants(torneo.getId());
            System.out.println("Participantes tras finalizar (debería estar vacío): " + again);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
