package ar.com.game.repository;

import ar.com.game.domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Main main = new Main();
        List<User> users = main.getAllUsers();

        users.forEach(user -> System.out.println(user.toString()));
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        String query = "SELECT * FROM users";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
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

                users.add(user);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener los usuarios: " + e.getMessage());
        }

        return users;
    }
}
