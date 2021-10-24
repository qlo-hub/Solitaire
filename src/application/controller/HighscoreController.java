package application.controller;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HighscoreController {
	@FXML
	VBox scores;
	
	Stage stage;
	public void getStage(Stage stage) {
		this.stage = stage;
	}
	
	public void addtoHS(ArrayList<Integer> array) {
		for(int i=0 ; i< array.size();i++ ) {
			scores.getChildren().add(new Label(array.get(i) + "\n\n"));
		}
	}

	public void inputHighscores(int score) {
		Label label = new Label(Integer.toString(score));
		scores.getChildren().add(label);
	}
	
}
