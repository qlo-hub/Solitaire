package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Random;

import application.controller.HighscoreController;
import application.controller.MenuController;
import application.controller.PlayernameController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

// driver for the game 

public class GUISolitaire extends Application {
	public Stage primaryStage;
	public Game game;
	private ArrayList<Integer> array= new ArrayList<Integer>();
	Random rd = new Random(); //
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		menu();
	}
	
	public void menu() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(GUISolitaire.class.getResource("view/MenuView.fxml"));
			AnchorPane Menu = (AnchorPane) loader.load();

			Scene scene = new Scene(Menu);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Solitaire");
			primaryStage.show();
			
			MenuController menuController = loader.getController();
			menuController.setMain(this);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void newGame() {
		Button bMenu = new Button("Back to Menu");
		bMenu.setLayoutX(850);
		bMenu.setLayoutY(570);
		EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				/*FXMLLoader loader = new FXMLLoader();
				HighscoreController highscoreController = loader.getController();
				int score = Game.getScore();
				highscoreController.add(10);*/
				
				menu();
			}
		};
		bMenu.setOnAction(event);
		
		Canvas canvas = new Canvas(900, 600);

		Pane root = new Pane(canvas);
		root.setStyle("-fx-background-color: darkgreen;");
		root.getChildren().add(bMenu);

		Scene scene = new Scene(root);

		Game game = new Game(canvas.getGraphicsContext2D());
		this.game = game;

		canvas.setOnMouseClicked(game::handleMouseClicked);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Solitaire");
		primaryStage.show();
	}
	
	public void showHighscore(int score) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(GUISolitaire.class.getResource("view/HighscoreView.fxml"));
			AnchorPane root = (AnchorPane) loader.load();
			
			Scene scene = new Scene(root);
			Stage highscoreStage = new Stage();
			
			highscoreStage.initOwner(primaryStage);
			highscoreStage.initModality(Modality.WINDOW_MODAL);
			highscoreStage.setTitle("Highscores");
			
			highscoreStage.setScene(scene);
			highscoreStage.show();
			
			HighscoreController controller = loader.getController();
			controller.getStage(highscoreStage);
			for(int i = 5; i <= 50; i=i+5) {
				array.add(i);
			}
			if(score != 0) {
				array.add(score);
			}
			LinkedHashSet<Integer> hashSet = new LinkedHashSet<>(array);
			ArrayList<Integer> hsNoDupli = new ArrayList<>(hashSet);
			Collections.sort(hsNoDupli,Collections.reverseOrder());
			controller.addtoHS(hsNoDupli);
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		launch(args);
	}

}
