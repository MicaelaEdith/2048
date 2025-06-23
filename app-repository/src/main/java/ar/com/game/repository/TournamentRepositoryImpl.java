package ar.com.game.repository;

import ar.com.game.domain.Tournament;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TournamentRepositoryImpl implements TournamentRepository {
    private final Connection connection;

    public TournamentRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Tournament save(Tournament tournament) {
        String sql = "INSERT INTO tournaments (name, creator_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tournament.getName());
            stmt.setInt(2, tournament.getCreatorId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    tournament.setId(rs.getInt(1));
                }
            }
            return tournament;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar torneo", e);
        }
    }

    @Override
    public void updateWinner(int tournamentId, int winnerId) {
        String sql = "UPDATE tournaments SET winner_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, winnerId);
            stmt.setInt(2, tournamentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar ganador del torneo", e);
        }
    }

    @Override
    public Optional<Tournament> findById(int id) {
        String sql = "SELECT * FROM tournaments WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Tournament t = new Tournament();
                    t.setId(rs.getInt("id"));
                    t.setName(rs.getString("name"));
                    t.setCreatorId(rs.getInt("creator_id"));
                    t.setWinnerId((Integer) rs.getObject("winner_id"));
                    t.setCreatedAt(rs.getTimestamp("created_at"));
                    return Optional.of(t);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar torneo", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Tournament> findAll() {
        String sql = "SELECT * FROM tournaments";
        List<Tournament> tournaments = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Tournament t = new Tournament();
                t.setId(rs.getInt("id"));
                t.setName(rs.getString("name"));
                t.setCreatorId(rs.getInt("creator_id"));
                t.setWinnerId((Integer) rs.getObject("winner_id"));
                t.setCreatedAt(rs.getTimestamp("created_at"));
                tournaments.add(t);
            }
            return tournaments;

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener lista de torneos", e);
        }
    }
}
