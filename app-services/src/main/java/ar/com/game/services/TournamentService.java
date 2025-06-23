package ar.com.game.services;

import ar.com.game.domain.Tournament;
import ar.com.game.repository.TournamentParticipantsRepository;
import ar.com.game.repository.TournamentRepository;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TournamentService {
    private final TournamentRepository tournamentRepo;
    private final TournamentParticipantsRepository participantsRepo;
    private final Map<Integer, Tournament> activeTournaments = new HashMap<>();
    private final int MAX_PARTICIPANTS = 3;

    public TournamentService(TournamentRepository tournamentRepo, TournamentParticipantsRepository participantsRepo) {
        this.tournamentRepo = tournamentRepo;
        this.participantsRepo = participantsRepo;
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

    public boolean isTournamentFull(int tournamentId) throws SQLException {
        return participantsRepo.countParticipants(tournamentId) >= MAX_PARTICIPANTS;
    }

    public boolean joinTournament(int tournamentId, int userId) throws SQLException {
        if (isTournamentFull(tournamentId)) {
            return false;
        }
        boolean added = participantsRepo.addParticipant(tournamentId, userId);
		if (added) {
		    return true;
		}
        return false;
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

    public List<Integer> getParticipants(int tournamentId) throws SQLException {
        return participantsRepo.findParticipantsByTournamentId(tournamentId);
    }

    public boolean isCreator(int tournamentId, int userId) {
        Tournament t = activeTournaments.get(tournamentId);
        return t != null && t.getCreatorId() == userId;
    }

    public List<Tournament> getAllTournaments() {
        return tournamentRepo.findAll();
    }
}
