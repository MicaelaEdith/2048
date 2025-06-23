package ar.com.game.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public interface TournamentParticipantsRepository {
    List<Integer> findParticipantsByTournamentId(int tournamentId);
    int countParticipants(int tournamentId);
    boolean addParticipant(int tournamentId, int userId);
    boolean isParticipant(int tournamentId, int userId);
}
