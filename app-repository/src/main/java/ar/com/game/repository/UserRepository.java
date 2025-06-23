
package ar.com.game.repository;

import ar.com.game.domain.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    /**
     * Crea un nuevo usuario en la BD.
     * @throws SQLException si ocurre un error de BD, con el mensaje original.
     */
    public boolean createUser(User user) throws SQLException {
        String query = "INSERT INTO users (name, email, password, total_points, level, competitive_matches, wins, losses, contacts, current_skin, available_skins, created_at) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setInt(4, user.getTotalPoints());
            stmt.setInt(5, user.getLevel());
            stmt.setInt(6, user.getCompetitiveMatches());
            stmt.setInt(7, user.getWins());
            stmt.setInt(8, user.getLosses());
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
            return false;
        }
    }

    public boolean updateUser(User user) throws SQLException {
        String query = "UPDATE users SET name = ?, password = ? WHERE email = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateContacts(int userId, String contacts) throws SQLException {
        String query = "UPDATE users SET contacts = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, contacts);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateStats(User user) throws SQLException {
        String query = "UPDATE users SET total_points = IFNULL(?, total_points), level = IFNULL(?, level), competitive_matches = IFNULL(?, competitive_matches), wins = IFNULL(?, wins), losses = IFNULL(?, losses) WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setObject(1, user.getTotalPoints(), Types.INTEGER);
            stmt.setObject(2, user.getLevel(), Types.INTEGER);
            stmt.setObject(3, user.getCompetitiveMatches(), Types.INTEGER);
            stmt.setObject(4, user.getWins(), Types.INTEGER);
            stmt.setObject(5, user.getLosses(), Types.INTEGER);
            stmt.setInt(6, user.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public User findByEmail(String email) throws SQLException {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
            return null;
        }
    }

    public User findById(Long id) throws SQLException {
        String query = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
            return null;
        }
    }

    public boolean deleteUserById(Long id) throws SQLException {
    	String query = "UPDATE users SET activo = 0 WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE activo = 1 ORDER BY id";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }
        return users;
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
    
    public List<UserRankingDTO> getTop10Players() throws SQLException {
        List<UserRankingDTO> ranking = new ArrayList<>();
        String sql = "SELECT name, total_points FROM users ORDER BY total_points DESC LIMIT 10;";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("name");
                int score = rs.getInt("total_points");
                ranking.add(new UserRankingDTO(username, score));
            }
        }

        return ranking;
    }
    
    
    public String getContactsByUserId(int userId) throws SQLException {
        String query = "SELECT contacts FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("contacts");
                }
            }
        }
        return null;
    }

    
    public boolean addContactToUser(int userId, String newContact) throws SQLException {
        String currentContacts = getContactsByUserId(userId);

        if (currentContacts != null && !currentContacts.isEmpty()) {
            String[] existing = currentContacts.split(",");
            for (String contact : existing) {
                if (contact.trim().equalsIgnoreCase(newContact.trim())) {
                    return false;
                }
            }
            currentContacts += "," + newContact;
        } else {
            currentContacts = newContact;
        }

        return updateContacts(userId, currentContacts);
    }


    public boolean updateTotalPointsIfHigher(int userId, int newScore) throws SQLException {
        String sql = """
            UPDATE users
            SET total_points = ?
            WHERE id = ? AND ? > total_points
        """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newScore);
            stmt.setInt(2, userId);
            stmt.setInt(3, newScore);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateUserStatsAfterDuel(Long userId, boolean won, boolean lost, int points) throws SQLException {
        String sql = "UPDATE users SET " +
                     "total_points = GREATEST(total_points, ?), " +
                     "competitive_matches = competitive_matches + 1, " +
                     "wins = wins + ?, " +
                     "losses = losses + ? " +
                     "WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
        	PreparedStatement ps = conn.prepareStatement(sql)) {
        	
	            ps.setInt(1, points);
	            ps.setInt(2, won ? 1 : 0);
	            ps.setInt(3, lost ? 1 : 0);
	            ps.setLong(4, userId);
	
	            return ps.executeUpdate() > 0;
        }
    }
    public boolean updateStatsForBothPlayersFromDuel(int duelId) throws SQLException {
        String selectSql = """
            SELECT 
              player1_id, 
              player2_id, 
              winner_id, 
              player1_points, 
              player2_points
            FROM duels
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement psSelect = conn.prepareStatement(selectSql)) {

            psSelect.setInt(1, duelId);
            try (ResultSet rs = psSelect.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }

                Integer player1Id  = rs.getObject("player1_id",  Integer.class);
                Integer player2Id  = rs.getObject("player2_id",  Integer.class);
                Integer winnerId   = rs.getObject("winner_id",    Integer.class);
                Integer p1Points   = rs.getObject("player1_points", Integer.class);
                Integer p2Points   = rs.getObject("player2_points", Integer.class);

                if (winnerId == null) {
                    return false;
                }

                if (winnerId.equals(player1Id)) {
                    String win1 = """
                        UPDATE users
                        SET 
                          total_points       = GREATEST(total_points, ?),
                          competitive_matches = competitive_matches + 1,
                          wins               = wins + 1
                        WHERE id = ?
                    """;
                    try (PreparedStatement ps1 = conn.prepareStatement(win1)) {
                        ps1.setInt(1, p1Points != null ? p1Points : 0);
                        ps1.setInt(2, player1Id);
                        ps1.executeUpdate();
                    }
                } else {
                    String lose1 = """
                        UPDATE users
                        SET 
                          total_points       = GREATEST(total_points, ?),
                          competitive_matches = competitive_matches + 1,
                          losses             = losses + 1
                        WHERE id = ?
                    """;
                    try (PreparedStatement ps1 = conn.prepareStatement(lose1)) {
                        ps1.setInt(1, p1Points != null ? p1Points : 0);
                        ps1.setInt(2, player1Id);
                        ps1.executeUpdate();
                    }
                }

                if (winnerId.equals(player2Id)) {
                    String win2 = """
                        UPDATE users
                        SET 
                          total_points       = GREATEST(total_points, ?),
                          competitive_matches = competitive_matches + 1,
                          wins               = wins + 1
                        WHERE id = ?
                    """;
                    try (PreparedStatement ps2 = conn.prepareStatement(win2)) {
                        ps2.setInt(1, p2Points != null ? p2Points : 0);
                        ps2.setInt(2, player2Id);
                        ps2.executeUpdate();
                    }
                } else {
                    String lose2 = """
                        UPDATE users
                        SET 
                          total_points       = GREATEST(total_points, ?),
                          competitive_matches = competitive_matches + 1,
                          losses             = losses + 1
                        WHERE id = ?
                    """;
                    try (PreparedStatement ps2 = conn.prepareStatement(lose2)) {
                        ps2.setInt(1, p2Points != null ? p2Points : 0);
                        ps2.setInt(2, player2Id);
                        ps2.executeUpdate();
                    }
                }

                return true;
            }
        }
    }

} 