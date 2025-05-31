package ar.com.game.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.game.domain.Tournament;
import ar.com.game.repository.TournamentRepository;


public class TournamentService {
    private final TournamentRepository tournamentRepo;
    private final Map<Integer, Tournament> activeTournaments = new HashMap<>();

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
}
