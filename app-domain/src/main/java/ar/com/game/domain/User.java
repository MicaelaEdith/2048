package ar.com.game.domain;

import java.time.LocalDateTime;
import java.util.List;

public class User {

    private Integer id;
    private String name;
    private String email;
    private String password;
    private Integer totalPoints;
    private Integer level;
    private Integer competitiveMatches;
    private Integer wins;
    private Integer losses;
    private String contacts;
    private String currentSkin;
    private String availableSkins;
    private LocalDateTime createdAt;

    
    public User() {}

    public User(Integer id, String name, String email, String password, Integer totalPoints, Integer level,
                Integer competitiveMatches, Integer wins, Integer losses, String contacts,
                String currentSkin, String availableSkins, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.totalPoints = totalPoints;
        this.level = level;
        this.competitiveMatches = competitiveMatches;
        this.wins = wins;
        this.losses = losses;
        this.contacts = contacts;
        this.currentSkin = currentSkin;
        this.availableSkins = availableSkins;
        this.createdAt = createdAt;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(Integer totalPoints) {
		this.totalPoints = totalPoints;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getCompetitiveMatches() {
		return competitiveMatches;
	}

	public void setCompetitiveMatches(Integer competitiveMatches) {
		this.competitiveMatches = competitiveMatches;
	}

	public Integer getWins() {
		return wins;
	}

	public void setWins(Integer wins) {
		this.wins = wins;
	}

	public Integer getLosses() {
		return losses;
	}

	public void setLosses(Integer losses) {
		this.losses = losses;
	}

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String string) {
		this.contacts = string;
	}

	public String getCurrentSkin() {
		return currentSkin;
	}

	public void setCurrentSkin(String currentSkin) {
		this.currentSkin = currentSkin;
	}

	public String getAvailableSkins() {
		return availableSkins;
	}
 
	public void setAvailableSkins(String string) {
		this.availableSkins = string;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	@Override
	public String toString() {
	    return "User{" +
	            "id=" + id +
	            ", name='" + name + '\'' +
	            ", email='" + email + '\'' +
	            ", totalPoints=" + totalPoints +
	            ", level=" + level +
	            ", competitiveMatches=" + competitiveMatches +
	            ", wins=" + wins +
	            ", losses=" + losses +
	            ", contacts='" + contacts + '\'' +
	            ", currentSkin='" + currentSkin + '\'' +
	            ", availableSkins='" + availableSkins + '\'' +
	            '}';
	}
}
