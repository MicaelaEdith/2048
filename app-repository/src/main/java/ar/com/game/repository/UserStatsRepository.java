package ar.com.game.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ar.com.game.domain.UserStatsDTO;

public class UserStatsRepository {

    private final Connection connection;

    public UserStatsRepository(Connection connection) {
        this.connection = connection;
    }

    public UserStatsDTO getStatsByUserId(long userId) throws SQLException {
        String sql = "SELECT " +
                "COUNT(*) AS played, " +
                "SUM(CASE WHEN winner_id = ? THEN 1 ELSE 0 END) AS won, " +
                "SUM(CASE WHEN winner_id != ? AND winner_id IS NOT NULL THEN 1 ELSE 0 END) AS lost " +
                "FROM duels " +
                "WHERE player1_id = ? OR player2_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, userId);
            ps.setLong(3, userId);
            ps.setLong(4, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int played = rs.getInt("played");
                    int won = rs.getInt("won");
                    int lost = rs.getInt("lost");
                    return new UserStatsDTO(played, won, lost);
                }
            }
        }
        return new UserStatsDTO(0, 0, 0);
    }
}
