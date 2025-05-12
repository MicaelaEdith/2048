package ar.com.game.repository;

import ar.com.game.domain.Duel;

import java.sql.*;

public class DuelRepository {

    public boolean createDuel(Duel duel) throws SQLException {
        String sql = "INSERT INTO duels (player1_id, player2_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, duel.getPlayer1Id());
            stmt.setInt(2, duel.getPlayer2Id());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    duel.setId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
        }
    }
}
