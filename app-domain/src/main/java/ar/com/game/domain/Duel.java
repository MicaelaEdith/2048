package ar.com.game.domain;

import java.time.LocalDateTime;

public class Duel {
    private Integer id;
    private Integer player1Id;
    private Integer player2Id;
    private Integer player1Points;
    private Integer player2Points;
    private Integer player1TimeSeconds;
    public void setId(Integer id) {
		this.id = id;
	}

	private Integer player2TimeSeconds;
    private Integer winnerId;
    private LocalDateTime createdAt;


    public Duel() {}

    public Duel(Integer player1Id, Integer player2Id) {
        this.player1Id = player1Id;
        this.player2Id = player2Id;
    }

	public Integer getPlayer1Id() {
		return player1Id;
	}

	public void setPlayer1Id(Integer player1Id) {
		this.player1Id = player1Id;
	}

	public Integer getPlayer2Id() {
		return player2Id;
	}

	public void setPlayer2Id(Integer player2Id) {
		this.player2Id = player2Id;
	}

	public Integer getPlayer1Points() {
		return player1Points;
	}

	public void setPlayer1Points(Integer player1Points) {
		this.player1Points = player1Points;
	}

	public Integer getPlayer2Points() {
		return player2Points;
	}

	public void setPlayer2Points(Integer player2Points) {
		this.player2Points = player2Points;
	}

	public Integer getPlayer1TimeSeconds() {
		return player1TimeSeconds;
	}

	public void setPlayer1TimeSeconds(Integer player1TimeSeconds) {
		this.player1TimeSeconds = player1TimeSeconds;
	}

	public Integer getPlayer2TimeSeconds() {
		return player2TimeSeconds;
	}

	public void setPlayer2TimeSeconds(Integer player2TimeSeconds) {
		this.player2TimeSeconds = player2TimeSeconds;
	}

	public Integer getWinnerId() {
		return winnerId;
	}

	public void setWinnerId(Integer winnerId) {
		this.winnerId = winnerId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Integer getId() {
		return id;
	}

}
