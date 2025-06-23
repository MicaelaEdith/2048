package ar.com.game.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TournamentParticipantsRepositoryImpl implements TournamentParticipantsRepository {

    private final Connection connection;

    public TournamentParticipantsRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Integer> findParticipantsByTournamentId(int tournamentId) {
        List<Integer> participants = new ArrayList<>();
        String sql = "SELECT user_id FROM tournament_participants WHERE tournament_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, tournamentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    participants.add(rs.getInt("user_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener participantes del torneo", e);
        }
        return participants;
    }

    @Override
    public int countParticipants(int tournamentId) {
        String sql = "SELECT COUNT(*) FROM tournament_participants WHERE tournament_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, tournamentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar participantes del torneo", e);
        }
        return 0;
    }

    @Override
    public boolean addParticipant(int tournamentId, int userId) {
        if (isParticipant(tournamentId, userId)) {
            return false;
        }
        String sql = "INSERT INTO tournament_participants (tournament_id, user_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, tournamentId);
            stmt.setInt(2, userId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar participante al torneo", e);
        }
    }

    @Override
    public boolean isParticipant(int tournamentId, int userId) {
        String sql = "SELECT 1 FROM tournament_participants WHERE tournament_id = ? AND user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, tournamentId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar participante en torneo", e);
        }
    }
}
