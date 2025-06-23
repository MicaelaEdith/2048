package ar.com.game.domain;

public class UserStatsDTO {
    private int played;
    private int won;
    private int lost;

    public UserStatsDTO(int played, int won, int lost) {
        this.played = played;
        this.won = won;
        this.lost = lost;
    }

    public int getPlayed() {
        return played;
    }

    public int getWon() {
        return won;
    }

    public int getLost() {
        return lost;
    }
}
