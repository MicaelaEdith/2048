package ar.com.game.repository;

import ar.com.game.domain.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public boolean createUser(User user) {
        String query = "INSERT INTO users (name, email, password, total_points, level, competitive_matches, wins, losses, contacts, current_skin, available_skins, created_at) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setInt(4, user.getTotalPoints() != null ? user.getTotalPoints() : 0);
            stmt.setInt(5, user.getLevel() != null ? user.getLevel() : 1);
            stmt.setInt(6, user.getCompetitiveMatches() != null ? user.getCompetitiveMatches() : 0);
            stmt.setInt(7, user.getWins() != null ? user.getWins() : 0);
            stmt.setInt(8, user.getLosses() != null ? user.getLosses() : 0);
            stmt.setString(9, user.getContacts());
            stmt.setString(10, user.getCurrentSkin());
            stmt.setString(11, user.getAvailableSkins());
            stmt.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
        }
        return false;
    }

    public boolean updateUser(User user) {
        String query = "UPDATE users SET name = ?, password = ? WHERE email = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
        return false;
    }

    public boolean updateContacts(int userId, String contacts) {
        String query = "UPDATE users SET contacts = ? WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, contacts);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating contacts: " + e.getMessage());
        }
        return false;
    }

    public boolean updateStats(User user) {
        String query = "UPDATE users SET " +
                "total_points = IFNULL(?, total_points), " +
                "level = IFNULL(?, level), " +
                "competitive_matches = IFNULL(?, competitive_matches), " +
                "wins = IFNULL(?, wins), " +
                "losses = IFNULL(?, losses) " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setObject(1, user.getTotalPoints(), Types.INTEGER);
            stmt.setObject(2, user.getLevel(), Types.INTEGER);
            stmt.setObject(3, user.getCompetitiveMatches(), Types.INTEGER);
            stmt.setObject(4, user.getWins(), Types.INTEGER);
            stmt.setObject(5, user.getLosses(), Types.INTEGER);
            stmt.setInt(6, user.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating stats: " + e.getMessage());
        }
        return false;
    }

    public User findByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding user: " + e.getMessage());
        }
        return null;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setTotalPoints(rs.getInt("total_points"));
        user.setLevel(rs.getInt("level"));
        user.setCompetitiveMatches(rs.getInt("competitive_matches"));
        user.setWins(rs.getInt("wins"));
        user.setLosses(rs.getInt("losses"));
        user.setContacts(rs.getString("contacts"));
        user.setCurrentSkin(rs.getString("current_skin"));
        user.setAvailableSkins(rs.getString("available_skins"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}
