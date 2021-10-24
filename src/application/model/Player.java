package application.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Player {
	private final SimpleStringProperty player;
	private final SimpleIntegerProperty score;
	
	public Player(String player, int score) {
		this.player = new SimpleStringProperty(player);
		this.score = new SimpleIntegerProperty(score);
	}
	
	public String getPlayer() {
		return player.get();
	}
	
	public int getScore() {
		return score.get();
	}
	
	public void setPlayer(String player) {
		this.player.set(player);
	}
	
	public void setScore(int score) {
		this.score.set(score);
	}
	
	
}

