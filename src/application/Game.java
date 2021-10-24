package application;

import java.util.*;

import javafx.geometry.BoundingBox;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;

public class Game {
	
	
	// constant sizes used
	private final double CARD_WIDTH = 100;
	private final double CARD_HEIGHT = 145.2;
	private final double ROUNDING_FACTOR = 10;
	private final double PADDING = 25;
	
	// vars for storing the cards and bounds
	private final Stack<Card> hand = new Stack<>();
	private final Stack<Card> waste = new Stack<>();
	private final List<Stack<Card>> board = new ArrayList<>();
	private final List<Stack<Card>> foundations = new ArrayList<>();
	private final BoundingBox handBounds = new BoundingBox(PADDING, PADDING, CARD_WIDTH, CARD_HEIGHT);
	private final BoundingBox wasteBounds = new BoundingBox(PADDING * 2 + CARD_WIDTH, PADDING, CARD_WIDTH, CARD_HEIGHT);
	private final List<BoundingBox> foundationsBounds = new ArrayList<>();
	private final List<List<BoundingBox>> boardBounds = new ArrayList<>();

	private final Map<String, Image> imageCache = new HashMap<>();
	private final Random random = new Random();
	private final GraphicsContext gc;
	
	// alert text and selected card vars

	private String winText = "";
	private Stack<Card> selected = new Stack<>();
	public int score;
	
	/*
	 * main constructor for the Game
	 */
	Game(GraphicsContext gc){
		this.gc = gc;
		initVars();
		loadImages();
		fillPack();
		shufflePack();
		layBoard();
		generateBoardBounds();
		revealCards();
		score = 0;
		drawGame();
	}
	
	public int getScore() {
		return score;
	}
	
	// initializes 2d array lists
	private void initVars() {
		for (int i = 0; i < 4; i++) {
			foundations.add(new Stack<>());
			foundationsBounds.add(new BoundingBox(PADDING + (CARD_WIDTH + PADDING) * (3 + i), PADDING, CARD_WIDTH, CARD_HEIGHT));
		}
	}
	
	// loads images of cards 
	private void loadImages() {
		for (Suit suit : Suit.values()) {
			for (Value value : Value.values()) {
				String filename = value.toString().toLowerCase() + "of" + suit.toString().toLowerCase();
				imageCache.put(filename, new Image(this.getClass().getResourceAsStream("/images/" +  filename + ".png")));
			}
		}
		imageCache.put("cardback", new Image(this.getClass().getResourceAsStream("/images/cardback.png")));
	}
	
	// fill Stack with a card 
	private void fillPack() {
		hand.clear();
		for (Suit suit : Suit.values()) {
			for (Value value : Value.values()) {
				hand.push(new Card(suit, value));
			}
		}
	}
	
	// shuffles the pack
	private void shufflePack() {
		for (int i = 0; i < 52; i++) {
			swapCard(random.nextInt(52 - i), 51 - i);
		}
	}
	
	// swaps card at index 1 in the pack with the card at index 2; used for shufflePack
	private void swapCard(int i1, int i2) {
		Card temp = hand.get(i1);
		hand.set(i1, hand.get(i2));
		hand.set(i2, temp);
	}
	
	// fills the tableau from pack of cards
	private void layBoard() {
		board.clear();
		for (int i = 0; i < 7; i++) {
			Stack<Card> stack = new Stack<>();
			for (int j = 0; j < i + 1; j++) {
				stack.push(hand.pop());
			}
			board.add(stack);
		}
	}
	
	// generates a bounding box for each card on the board
	private void generateBoardBounds() {
		boardBounds.clear();
		for (int i = 0; i < 7; i++) {
			boardBounds.add(new ArrayList<>());
			Stack<Card> stack = board.get(i);
			boardBounds.get(i).add(new BoundingBox(PADDING + (CARD_WIDTH + PADDING) * i, PADDING * 2 + CARD_HEIGHT, CARD_WIDTH, CARD_HEIGHT));
			for (int j = 1; j < stack.size(); j++) {
				boardBounds.get(i).add(new BoundingBox(PADDING + (CARD_WIDTH + PADDING) * i, PADDING * (2 + j) + CARD_HEIGHT, CARD_WIDTH, CARD_HEIGHT));
			}
		}
	}
	
	// turns over cards at bottom of tableau
	private void revealCards() {
		for (Stack<Card> stack : board) {
			if (!stack.isEmpty()) {
				Card card = stack.peek();
				if (!card.isRevealed()) {
					card.reveal();
					score = score + 5;
				}
			}
		}
	}
	
	// draws the overall game gui
	private void drawGame() {
	    //Clear the game canvas
		gc.clearRect(0, 0, 900, 600);
		
		//Draw the hand
		double x = PADDING, y = PADDING;
		if (hand.isEmpty()) {
			drawEmpty(x, y);
		} else {
			drawCardBack(x, y);
		}
		
		//Draw the waste
		x = PADDING * 2 + CARD_WIDTH;
		y = PADDING;
		if (waste.isEmpty()) {
			drawEmpty(x, y);
		} else {
			drawCard(waste.peek(), x, y);
		}
		
		//Draw the board
		for (int i = 0; i < 7; i++) {
			x = PADDING + (CARD_WIDTH + PADDING) * i;
			Stack<Card> stack = board.get(i);
			if (stack.isEmpty()) {
				drawEmpty(x, PADDING * 2 + CARD_HEIGHT);
			} else {
				for (int j = 0; j < stack.size(); j++) {
					y = PADDING * (2 + j) + CARD_HEIGHT;
					Card card = stack.get(j);
					if (card.isRevealed()) {
						drawCard(card, x, y);
					} else {
						drawCardBack(x, y);
					}
				}
			}
		}
		
		//Draw the foundations
		y = PADDING;
		for (int i = 0; i < 4; i++) {
			x = PADDING + (CARD_WIDTH + PADDING) * (3 + i);			
			Stack<Card> stack = foundations.get(i);
			if (stack.isEmpty()) {
				drawEmpty(x, y);
			} else {
				drawCard(stack.peek(), x, y);
			}
		}
		
		// Draws the score
		if(score < 0) {
			score = 0;
			String strScore = "SCORE: " + score;
			drawText(strScore, 10, 590, 18, Color.WHITE, TextAlignment.LEFT);
		}else {
			String strScore = "SCORE: " + score;
			drawText(strScore, 10, 590, 18, Color.WHITE, TextAlignment.LEFT);
		}
		

		//Draw the win text
		drawText(winText, 450, 350, 150, Color.BLACK, TextAlignment.CENTER);
	}
	
	// draw an empty card space to the canvas 
	private void drawEmpty(double x, double y) {
		gc.setStroke(Color.WHITE);
		gc.setLineWidth(1);
		gc.strokeRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, ROUNDING_FACTOR, ROUNDING_FACTOR);
	}
	
	// draws a card back to the game canvas
	private void drawCardBack(double x, double y) {
		gc.drawImage(imageCache.get("cardback"), x, y, CARD_WIDTH, CARD_HEIGHT);
	}
	
	// draws a card to the game canvas 
	private void drawCard(Card card, double x, double y) {
		gc.drawImage(imageCache.get(card.getName()), x, y, CARD_WIDTH, CARD_HEIGHT);
		if (card.isSelected()) {
			gc.setStroke(Color.LIGHTBLUE);
			gc.setLineWidth(3);
			gc.strokeRoundRect(x, y, CARD_WIDTH, CARD_HEIGHT, ROUNDING_FACTOR, ROUNDING_FACTOR);
		}
	}
	
	// draw the text(alertText, winText, etc) at coordinates
//	private void drawText(String text, double x, double y) {
//		drawText(text, x, y, Font.getDefault().getSize(), Color.BLACK, TextAlignment.LEFT);
//	}
	
	private void drawText(String text, double x, double y, double size, Paint paint, TextAlignment textAlignment) {
		gc.setFont(new Font(size));
		gc.setFill(paint);
		gc.setTextAlign(textAlignment);
		gc.fillText(text, x, y);
	}
	
	// handler for the mouse clicks
	void handleMouseClicked(MouseEvent me) {
		double x = me.getX(), y = me.getY();

		//Handle hand interactivity
		if (handBounds.contains(x, y)) {
			handClicked();
			finish(me);
			return;
		}
		
		//Handle waste interactivity
		if (wasteBounds.contains(x, y)) {
			wasteClicked();
			finish(me);
			return;
		}
		
		//Handle board interactivity. Checks the bounds for each card on the board to see if any have been clicked. If
        //the mouse was clicked at an overlap of bounds it selects the topmost one.
		boolean boardClicked = false;
		int indexX = -1, indexY = -1;
		for (int i = 0; i < 7; i++) {
			List<BoundingBox> boundsList = boardBounds.get(i);
			for (int j = 0; j < boundsList.size(); j++) {
				if (boundsList.get(j).contains(x, y)) {
					if (board.get(i).isEmpty() || board.get(i).get(j).isRevealed()) {
						indexX = i;
						indexY = j;
						boardClicked = true;
					}
				}
			}
			if (boardClicked) {
				boardClicked(indexX, indexY);
				finish(me);
				return;
			}
		}
		
		//Handle foundations interactivity
		for (int i = 0; i < 4; i++) {
			if (foundationsBounds.get(i).contains(x, y)) {
				foundationsClicked(i);
				finish(me);
				return;
			}
		}

		//If nothing was clicked
		deselect();
		finish(me);
	}
	
	// selects a card
	private void select(Card card) {
		card.toggleSelected();
		selected.add(card);
	}
	
	// deselects a selected card 
	private void deselect() {
		if (!selected.isEmpty()) {
			for (Card card : selected) {
				card.toggleSelected();
			}
			selected.clear();
		}
	}
	
	
	// handles the hand being clicked; for handleMouseClicked
	private void handClicked() {
		if (hand.isEmpty()) {
			resetHand();
		} else {
			turnHand();
		}
		deselect();
	}
	
	// resets waste back to the pack; for handClicked
	private void resetHand() {
		int size = waste.size();
		for (int i = 0; i < size; i++) {
			hand.push(waste.pop());
		}
		score -= 100;
	}
	
	// takes top card from pack and place it face up on waste pile; for handClicked
	private void turnHand() {
		waste.push(hand.pop());
	}
	
	// handles the Waste being clicked; for handleMouseClicked
	private void wasteClicked() {
		if (!waste.isEmpty()) {
			Card card = waste.peek();
			if (!selected.isEmpty() && selected.contains(card)) {
				deselect();
			} else {
				deselect();
				select(card);
			}
		} else {
			deselect();
		}
	}
	
	// handles board being clicked; for handleMouseClicked
	private void boardClicked(int indexX, int indexY) {
		Stack<Card> stack = board.get(indexX);
		Card card = null;
		if (!stack.isEmpty()) {
			card = stack.get(indexY);
		}
		if (!selected.isEmpty()) {
			if (selected.contains(card)) {
				deselect();
			} else if (isValidBoardMove(card, selected.get(0)) && (indexY == stack.size() - 1 || indexY == 0)) {
				moveCards(stack);
				generateBoardBounds();
				deselect();
			} else {
				deselect();
			}
		} else {
			deselect();
			for (int i = indexY; i < stack.size(); i++) {
				select(stack.get(i));
			}
		}
	}
	
	// checks validity of the move player is trying to make; for boardClicked
	private boolean isValidBoardMove(Card parent, Card child) {
	    //Explicit if statements preserved for clarity
		if (parent == null) {
			return child.getValue() == Value.KING;
		}
		if (parent.getColor() == child.getColor()) {
			return false;
		}
		if (parent.getValue().ordinal() != child.getValue().ordinal() + 1) {
			return false;
		}
		if(foundations.contains(selected)) {
			score = score - 15;
		}
		return true;
	}
	
	// move selected card to specified stack; for boardClicked and foundationsClicked
	private void moveCards(Stack<Card> stack) {
		for (Card card : selected) {
			waste.remove(card);
			for (Stack<Card> boardStack : board) {
				boardStack.remove(card);
			}
			for (Stack<Card> foundStack : foundations) {
				foundStack.remove(card);
			}
			stack.push(card);
		}
		if (isGameWon()) {
			winText = "You win!";
		}
	}
	
	// for moveCards
	private boolean isGameWon() {
		for (Stack<Card> stack : foundations) {
			if (stack.size() != 13) {
				return false;
			}
		}
		return true;
	}
	
	// handles the foundation being clicked; for handleMouseClicked
	private void foundationsClicked(int index) {
		Stack<Card> stack = foundations.get(index);
		Card card = null;
		if (!stack.isEmpty()) {
			card = stack.peek();
		}
		if (!selected.isEmpty()) {
			if (selected.contains(card)) {
				deselect();
			} else if (isValidFoundationsMove(card, selected.get(0))) {
				moveCards(stack);
				generateBoardBounds();
				score = score + 10;
				deselect();
			} else {
				deselect();
			}
		} else if (card != null){
			deselect();
			select(card);
		} else {
			deselect();
		}
	}
	
	// checks validity of moving a card to the foundation; for foundationsClicked
	private boolean isValidFoundationsMove(Card parent, Card child) {
		//Explicit if statements preserved for clarity
		if (parent == null) {
			return child.getValue() == Value.ACE;
		}
		if (parent.getSuit() != child.getSuit()) {
			return false;
		}
		if (parent.getValue().ordinal() != child.getValue().ordinal() - 1) {
			return false;
		}
		return true;
	}
	
	// finish for handleMouseClicked
	private void finish(MouseEvent me) {
		revealCards();
		drawGame();
		me.consume();
	}
	
			
}
