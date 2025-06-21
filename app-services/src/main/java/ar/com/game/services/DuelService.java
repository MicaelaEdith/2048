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
}
