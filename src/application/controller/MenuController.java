package application.controller;

import application.GUISolitaire;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MenuController {

	private GUISolitaire main;
	@FXML
	private Button exitButton;
	
	public void setMain(GUISolitaire main){
		this.main = main;
	}
	
	@FXML
	private void newGame() {
		main.newGame();
	}
	
	@FXML
	private void showHighscore() {
		if(main.game == null) {
			main.showHighscore(0);
		}
		else {
			
			main.showHighscore(main.game.score);
		}
	}
	
	@FXML
	private void exit(){
		Stage stage = (Stage) exitButton.getScene().getWindow();
		stage.close();
	}
}
