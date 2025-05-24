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
    
    public Duel findPendingDuelForUser(int userId) throws SQLException {
        String sql = """
            SELECT * FROM duels 
            WHERE ((player1_id = ? AND player1_points IS NULL) 
                OR (player2_id = ? AND player2_points IS NULL))
            AND winner_id IS NULL
            ORDER BY created_at DESC
            LIMIT 1
        """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Duel duel = new Duel();
                    duel.setId(rs.getInt("id"));
                    duel.setPlayer1Id(rs.getInt("player1_id"));
                    duel.setPlayer2Id(rs.getInt("player2_id"));
                    duel.setPlayer1Points(rs.getObject("player1_points", Integer.class));
                    duel.setPlayer2Points(rs.getObject("player2_points", Integer.class));
                    duel.setWinnerId(rs.getObject("winner_id", Integer.class));
                    return duel;
                }
            }
        }
        return null;
    }

}
