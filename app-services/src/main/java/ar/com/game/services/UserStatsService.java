package ar.com.game.services;

import ar.com.game.domain.UserStatsDTO;
import ar.com.game.repository.UserStatsRepository;

import java.sql.Connection;
import java.sql.SQLException;

public class UserStatsService {

    private final UserStatsRepository repository;

    public UserStatsService(Connection connection) {
        this.repository = new UserStatsRepository(connection);
    }

    public UserStatsDTO getUserStats(long userId) throws SQLException {
        return repository.getStatsByUserId(userId);
    }
}
