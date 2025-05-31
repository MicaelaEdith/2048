package ar.com.game.repository;

import java.util.Optional;

import ar.com.game.domain.Tournament;

public interface TournamentRepository {
    Tournament save(Tournament tournament); // Inserta y devuelve con ID generado
    void updateWinner(int tournamentId, int winnerId);
    Optional<Tournament> findById(int id);

}

