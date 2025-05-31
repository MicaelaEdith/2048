package ar.com.game.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class Tournament {
    private int id;
    private String name;
    private int creatorId;
    private Integer winnerId;
    private Timestamp createdAt;

    // No persistente
    private List<Integer> participants = new ArrayList<>();
    private boolean started = false;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}
	public Integer getWinnerId() {
		return winnerId;
	}
	public void setWinnerId(Integer winnerId) {
		this.winnerId = winnerId;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Timestamp createdAt) {
	    this.createdAt = createdAt;
	}

	public List<Integer> getParticipants() {
		return participants;
	}
	public void setParticipants(List<Integer> participants) {
		this.participants = participants;
	}
	public boolean isStarted() {
		return started;
	}
	public void setStarted(boolean started) {
		this.started = started;
	}

    
}
