package ar.com.game.services;

import ar.com.game.domain.Tournament;
import ar.com.game.repository.TournamentRepository;
import ar.com.game.repository.TournamentRepositoryImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class TournamentService {
    private final TournamentRepository tournamentRepo;
    private final Map<Integer, Tournament> activeTournaments = new HashMap<>();

    // Constructor vacío que crea la conexión y la implementación del repositorio
    public TournamentService() {
        try {
            // Cambia esta URL por la de tu DB, con usuario y contraseña si hace falta
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tu_basededatos", "usuario", "password");
            this.tournamentRepo = new TournamentRepositoryImpl(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar a la base de datos", e);
        }
    }

    // Constructor con repo externo (por si inyectás)
    public TournamentService(TournamentRepository repo) {
        this.tournamentRepo = repo;
    }

    public Tournament createTournament(String name, int creatorId) {
        Tournament t = new Tournament();
        t.setName(name == null || name.isBlank() ? "Torneo" : name);
        t.setCreatorId(creatorId);
        Tournament saved = tournamentRepo.save(t);
        if (saved.getName().equals("Torneo")) {
            saved.setName("Torneo" + saved.getId());
        }
        activeTournaments.put(saved.getId(), saved);
        return saved;
    }

    public void joinTournament(int tournamentId, int userId) {
        Tournament t = activeTournaments.get(tournamentId);
        if (t != null && !t.isStarted()) {
            t.getParticipants().add(userId);
        }
    }

    public void startTournament(int tournamentId) {
        Tournament t = activeTournaments.get(tournamentId);
        if (t != null) {
            t.setStarted(true);
        }
    }

    public void endTournament(int tournamentId, int winnerId) {
        Tournament t = activeTournaments.get(tournamentId);
        if (t != null) {
            t.setWinnerId(winnerId);
            tournamentRepo.updateWinner(t.getId(), winnerId);
            activeTournaments.remove(tournamentId);
        }
    }

    public List<Integer> getParticipants(int tournamentId) {
        Tournament t = activeTournaments.get(tournamentId);
        return t != null ? t.getParticipants() : Collections.emptyList();
    }

    public boolean isCreator(int tournamentId, int userId) {
        Tournament t = activeTournaments.get(tournamentId);
        return t != null && t.getCreatorId() == userId;
    }

    // Método para obtener todos los torneos de la BD
    public List<Tournament> getAllTournaments() {
        return tournamentRepo.findAll();
    }
}
