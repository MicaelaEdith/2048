package ar.com.game.services;

import ar.com.game.domain.Duel;
import ar.com.game.repository.DuelRepository;

public class DuelService {

    private final DuelRepository duelRepository = new DuelRepository();

    public boolean createDuel(int player1Id, int player2Id) {
        Duel duel = new Duel();
        duel.setPlayer1Id(player1Id);
        duel.setPlayer2Id(player2Id);
        try {
            return duelRepository.createDuel(duel);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean submitDuelScore(int userId, int score) {
        try {
            Duel duel = duelRepository.findPendingDuelForUser(userId);
            if (duel == null) {
                return false;
            }

            boolean updated = duelRepository.updateDuelScore(duel.getId(), userId, score);

            if (updated && duelRepository.isDuelFinished(duel.getId())) {
                duelRepository.resolveWinner(duel.getId());

                UserService userService = new UserService();
                ServiceResponse resp = userService.updateStatsForBothPlayersFromDuel(duel.getId());
                if (!resp.isSuccess()) {
                    System.err.println("Error actualizando estadísticas de usuarios: " + resp.getMessage());
                }
            }

            return updated;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    public Duel getPendingDuelForUser(int userId) {
        try {
            return duelRepository.findPendingDuelForUser(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Duel getLastDuelForUser(int userId) {
        try {
            return duelRepository.findLastDuelForUser(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
