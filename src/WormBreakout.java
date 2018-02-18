import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
/**
 * Basic JavaFX Breakout Program based on the Worms Variant
 * 
 * @author Ryan Fu [rwf11]
 */
public class WormBreakout extends Application {
	public static final String TITLE = "Block Destroyer";
	public static final int SIZE = 400;
	public static final int FRAMES_PER_SECOND = 60;
	public static final int MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
	public static final double SECOND_DELAY = 1.0 / FRAMES_PER_SECOND;
	public static final Paint BACKGROUND = Color.MIDNIGHTBLUE;
	public static final Paint HIGHLIGHT = Color.OLIVEDRAB;    
	public static final String BOUNCER_IMAGE = "ball.gif";
	public static final String TITLE_IMAGE = "blockdestroyertitle.gif";
	public static final String GAMEOVER_IMAGE = "gameOverImage.gif";
	public static final String VICTORY_IMAGE = "victoryImage.gif";
	public static double X_BOUNCER_SPEED = 150;
	public static double Y_BOUNCER_SPEED = 150;
	public static final Paint MOVER_COLOR = Color.WHITE;
	public static int MOVER_SIZE = 50;
	public static int MOVER_SPEED = 20;
	public static final int X_START = 200;
	public static final int Y_START = 385;
	public static final int MOVER_X_START = 175;
	public static final int MOVER_Y_START = 390;
	public static int RADIUS = 5;
	public static final int BLOCK_WIDTH = 40;
	public static final int BLOCK_HEIGHT = 20;
	private Scene myScene;
	private ImageView blockDestroyer;
	private ImageView gameOverImage;
	private Circle myBall = new Circle(X_START, Y_START, RADIUS, Color.WHITE);
	private Rectangle myMover = new Rectangle(MOVER_X_START, MOVER_Y_START, MOVER_SIZE, MOVER_SIZE/10);
	private Boolean startBool = false;
	private Group firstroot = new Group(); 
	private Group welcomeRoot = new Group();
	private Group losingRoot = new Group();
	private Group secondRoot = new Group(); 
	private Group thirdRoot = new Group();
	private Group winningRoot = new Group();
	private int blockCount = 0;
	private int life = 3;
	private String power = "";
	private Boolean moveExploded = false;
	private int blockOneCount;
	private int blockTwoCount;
	private int blockThreeCount;
	private int score = 0;
	private Text textLives = new Text(5, 20, "Lives: "+ life);
	private Text textScore = new Text(320, 20, "Score: "+ score);
	private Text textPower = new Text(140, 20, "Power: "+ score);
	private Text finalScore = new Text(160, 180, "Final Score: "+ score);
	private File levelOne = new  File("levelone.txt");
	private File levelTwo = new  File("leveltwo.txt");
	private File levelThree = new  File("levelthree.txt");
	private Circle powerup = new Circle();
	private Boolean powerupON = false;
	private Boolean invincible = false;
	private Boolean powerupACTIVE = false;
	private int timer = 0;
	private Rectangle[] blocks; //creating an array of rectangles
	private Rectangle[] blocksOne;
	private Rectangle[] blocksTwo;
	private Rectangle[] blocksThree;
	private int levelCount = 0;
	private Boolean isPaused = false;
	private Circle[] explosion = new Circle[5]; //creates an array of five balls for the explosion	  
	private Boolean exploded = false;
	/**
	 * Initialize what will be displayed and how it will be updated.
	 */
	private Boolean lifecount = true;
	private Scene losingScene;
	private Scene welcomeScene; 
	private Scene secondLevel;
	private Scene thirdLevel;
	private Scene winScene;
	Button gameOverButton = new Button();
	Button invisible1 = new Button();
	Button invisible2 = new Button();
	Button invisible3 = new Button();
	@Override
	public void start (Stage stage) {
		//make size of window not resizable    	
		BorderPane bp = new BorderPane();
		stage.setMinWidth(200);
		stage.setMaxWidth(400);
		Scene scene = new Scene(bp, 400, 400);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
		//creating the Welcome Introduction Screen
		welcomeScene = setupWelcome(stage, 400, 400, Color.BLACK, welcomeRoot);  	

		//implementation of media and song to enhance the experience of the player
		String uriString = new File("8bitsong.mp3").toURI().toString();
		MediaPlayer player = new MediaPlayer( new Media(uriString));
		player.play();
		
		//losing screen	    	   
		losingScene = setupEndScreen("loss",stage, 400, 400, Color.BLACK, losingRoot);
		secondLevel = setupGame(secondRoot, stage, SIZE, SIZE, BACKGROUND, levelTwo, invisible2);
		thirdLevel = setupGame(thirdRoot, stage, SIZE, SIZE, BACKGROUND, levelThree, invisible3);
		winScene = setupEndScreen("win",stage, 400, 400, Color.BLACK, winningRoot);

		// attach scene to the stage and display it
		myScene = setupGame(firstroot, stage, SIZE, SIZE, BACKGROUND, levelOne, invisible1);

		stage.setScene(welcomeScene); //changed to myScene
		stage.setTitle(TITLE);
		stage.show();
		// attach "game loop" to timeline to play it
		KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY),
				e -> step(SECOND_DELAY, firstroot));
		Timeline animation = new Timeline();
		animation.setCycleCount(Timeline.INDEFINITE);
		animation.getKeyFrames().add(frame);
		animation.play();
	}

	//creating the game's welcome screne to help navigate between scenes.
	private Scene setupWelcome (Stage stage, int width, int height, Paint background, Group root) {
		//setting up various button and label objects for the introduction screen
		Label label1= new Label("How to Play:"); 
		label1.setLayoutX(180);
		label1.setLayoutY(150);
		label1.setTextFill(Color.WHITE);
		Label label2= new Label("SPACEBAR - Shoots the Ball\n"+ "RIGHT - Moves paddle to the right\n"+ "LEFT - Moves paddle to the left\n");
		label2.setWrapText(true);
		label2.setLayoutX(110);
		label2.setLayoutY(175);
		label2.setTextFill(Color.WHITE);
		Label label3= new Label("Blocks:");
		label3.setLayoutX(180);
		label3.setLayoutY(230);
		label3.setTextFill(Color.WHITE);
		Label label4= new Label("RED - Explodes adjacent blocks\n"+ "METAL - Cannot be destroyed\n"+ "BLUE - One Hit\n" + "GREEN - Two Hits\n");
		label4.setLayoutX(110);
		label4.setLayoutY(250);
		label4.setTextFill(Color.WHITE);
		Button button1= new Button("Level 1");
		button1.setLayoutX(80);
		button1.setLayoutY(340);
		button1.setOnAction(e -> stage.setScene(myScene));        
		Image image = new Image(getClass().getClassLoader().getResourceAsStream(TITLE_IMAGE));
		//level 2 button automatically skips and goes to level 2
		Button level2button = new Button("Level 2"); 
		level2button.setLayoutX(180);
		level2button.setLayoutY(340);
		level2button.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				stage.setScene(myScene);
				blockCount=0;
				invisible1.fire(); //automatically fires button
			}
			});
		//level 3 button that skips through level 1 and level 2
		Button level3button = new Button("Level 3"); 
		level3button.setLayoutX(280);
		level3button.setLayoutY(340);
		level3button.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				level2button.fire(); //automatically fires button
				levelCount=2;
				blockCount=0;
				invisible2.fire();
			}
			});       
		blockDestroyer = new ImageView(image);
		blockDestroyer.setFitHeight(90);
		blockDestroyer.setY(25);
		blockDestroyer.setPreserveRatio(true);
		root.getChildren().addAll(label1, button1, level2button, level3button, label2, label3, label4, blockDestroyer);
		Scene scene1= new Scene(root, width, height, background);
		return scene1;	
	}
	//method to create the End Screen Scene based on whether the scene is a "VICTORY" or "GAMEOVER"
	private Scene setupEndScreen(String state, Stage stage, int width, int height, Paint background, Group root) {  
		if (state.equals("win")) {
			Image image = new Image(getClass().getClassLoader().getResourceAsStream(VICTORY_IMAGE));
			gameOverImage = new ImageView(image);
			gameOverImage.setY(100);
			gameOverImage.setX(60);
		}else {
			Image image = new Image(getClass().getClassLoader().getResourceAsStream(GAMEOVER_IMAGE));
			gameOverImage = new ImageView(image);
			gameOverImage.setY(100);
			gameOverImage.setX(25);
		}	    	
		gameOverImage.setFitHeight(45);  	 	
		gameOverImage.setPreserveRatio(true);

		finalScore.setFill(Color.WHITE);
		root.getChildren().addAll(gameOverImage);
		Scene tempScene= new Scene(root, 400, 400, background);
		return tempScene;
	}  
	// Create the game's "scene": what shapes will be in the game and their starting properties
	private Scene setupGame (Group root, Stage stage, int width, int height, Paint background, File text, Button next) {
		//create player values;
		textLives.setFill(Color.WHITE);
		textScore.setFill(Color.WHITE);
		textPower.setFill(Color.WHITE);
		//if the game is over
		gameOverButton.setOnAction(e -> stage.setScene(losingScene));  
		next.setVisible(false);
		next.setText("NEXT LEVEL");
		if (next.equals(invisible1)) {
			next.setOnAction(e->stage.setScene(secondLevel)); //button to go to next level
		}
		else if (next.equals(invisible2)) {
			next.setOnAction(e->stage.setScene(thirdLevel));	 //button goes to third level
		}
		else {
			next.setOnAction(e->stage.setScene(winScene));	 //button goes to victory screen
		}
		next.setLayoutX(160);
		next.setLayoutY(200);
		// create one top level collection to organize the things in the scene
		Rectangle border = new Rectangle(0, 25, 400, 1);
		border.setFill(Color.AZURE);
		Scene scene = new Scene(root, width, height, background);
		myMover.setFill(MOVER_COLOR); //set mover colour to white

		// order added to the group is the order in which they are drawn	        
		root.getChildren().add(border);
		if (root.equals(firstroot)) { //first time level one is created
			firstroot.getChildren().add(myMover); //only add to first root
			firstroot.getChildren().add(myBall); //only add to first root
			firstroot.getChildren().add(textLives);	
			firstroot.getChildren().add(textScore);
			firstroot.getChildren().add(textPower);     
			firstroot.getChildren().add(gameOverButton);
			//gameOverButton.setOnAction(e -> stage.setScene(losingScene));  
			gameOverButton.setVisible(false);
		}	
		root.getChildren().add(next);  
		//create blocks for each level 
		//each subsequent level is connected to the previous level
		createBlocks(root, text);
		// respond to input	        
		scene.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
		return scene;
	}
	//createBlocks methods scans a particular file and creates an Array holding the scanned block information for each level
	private void createBlocks(Group root, File text) {
		ArrayList<Rectangle> scannedBlocks = new ArrayList<Rectangle>();
		//read in file and create blocks using scanner
		Scanner sc2 = null;
		try {
			sc2 = new Scanner(text);
		} catch (FileNotFoundException e) {
			e.printStackTrace();  
		}
		//create an array
		int heightCount = 0;
		int widthCount = 0;
		blockCount = 0;
		//Rectangle rect = null;
		while (sc2.hasNextLine()) {
			Scanner s2 = new Scanner(sc2.nextLine());
			while (s2.hasNext()) {
				String s = s2.next();
				System.out.println(s);
				scannedBlocks.add(new Rectangle(widthCount*50+5, heightCount*50+30, BLOCK_WIDTH, BLOCK_HEIGHT)); //adding new block to arraylist
				scannedBlocks.get(blockCount).setStroke(Color.WHITE);  //give white outline to all blocks
				//switch statement that reads each color from the file and creates matching colored block-type
				switch(s)			
				{
				case "blue": //blue block (1 hit)
					scannedBlocks.get(blockCount).setFill(Color.DEEPSKYBLUE);	                 	
					root.getChildren().add(scannedBlocks.get(blockCount)); //adding block to root from arraylist
					break;
				case "green": //green block (2 hits)
					scannedBlocks.get(blockCount).setFill(Color.MEDIUMSEAGREEN);	                		
					root.getChildren().add(scannedBlocks.get(blockCount)); //adding block to root from arraylist
					break;
				case "red": //red block (detonates adjacent blocks)
					scannedBlocks.get(blockCount).setFill(Color.RED);	                		
					root.getChildren().add(scannedBlocks.get(blockCount)); //adding block to root from arraylist
					break;
				case "metal": //metal block (cannot be destroyed)
					scannedBlocks.get(blockCount).setFill(Color.LIGHTGRAY);
					root.getChildren().add(scannedBlocks.get(blockCount)); //adding block to root from arraylist
					break;
				case "space": //space between blocks
					scannedBlocks.remove(blockCount);
					blockCount--;
					break;
				}
				widthCount++;   
				blockCount++;

			}
			widthCount = 0; //reset the width index
			heightCount++; //increment height counter
			s2.close();
		}  	     
		for (int i = 0; i<scannedBlocks.size();i++) {
			System.out.println(scannedBlocks.get(i));
		}
		//convert arraylist to array of BLOCKS
		if (text.equals(levelOne)) { //assigns correctly scanned array of blocks for level 1
			blockOneCount = blockCount;
			blocksOne = new Rectangle[scannedBlocks.size()];
			blocksOne = scannedBlocks.toArray(blocksOne);
			blocks = blocksOne;
		}
		if (text.equals(levelTwo)) { //assigns correctly scanned array of blocks for level 2
			blockTwoCount = blockCount;
			blocksTwo = new Rectangle[scannedBlocks.size()];
			blocksTwo = scannedBlocks.toArray(blocksTwo);
		} 
		if (text.equals(levelThree)) { //assigns correctly scanned array of blocks for level 3
			blockThreeCount = blockCount;
			blocksThree = new Rectangle[scannedBlocks.size()];
			blocksThree = scannedBlocks.toArray(blocksThree);
		}
		scannedBlocks.clear(); 
	}

	// Change properties of shapes to animate them 
	// Note, there are more sophisticated ways to animate shapes, but these simple ways work fine to start.
	private void step (double elapsedTime, Group root) {
		//check if game is over
		if (levelCount ==0) {
			blockCount= blockOneCount;
			levelCount++;
		}
		if (blockCount==0) {
			if (levelCount==1) { //add objects for level 2 root
				firstroot.getChildren().removeAll(); //delete all the children from first root because the game is over
				secondRoot.getChildren().addAll(myBall, myMover, textLives, textScore, textPower, gameOverButton);
				gameOverButton.setVisible(false);
				invisible1.setVisible(true);
				blockCount = blockTwoCount;
				blocks = blocksTwo; //switch blocks from level 1 to level 2
				reset(secondRoot);
			}
			if (levelCount==2) { //add objects for level 3 root
				secondRoot.getChildren().removeAll(); //delete all the children from first root because the game is over    				
				thirdRoot.getChildren().addAll(myBall, myMover, textLives, textScore, textPower, gameOverButton);
				gameOverButton.setVisible(false);
				invisible2.setVisible(true);
				blockCount = blockThreeCount;
				blocks = blocksThree; //switch blocks from level 2 to level 3
				reset(thirdRoot);
			}
			if (levelCount==3) {
				winningRoot.getChildren().add(finalScore);
				invisible3.fire();
			}
			reset(root); 	//might need to delete
			levelCount++;
		}
		if (life==0) {	    //checks number of lives left, if==0, set to gameOver screen
			losingRoot.getChildren().add(finalScore);
			winningRoot.getChildren().add(finalScore);
			gameOverButton.fire();
		}
		if (exploded==true) { //if exploded power-up is activated, initiates explosion based on scene/root
			if (levelCount==1) { 
				initiateExplosion(myBall, firstroot);
				exploded=false;
				moveExploded=true;
			}
			else if (levelCount==2) {
				initiateExplosion(myBall, secondRoot);
				exploded=false;
				moveExploded = true;
			}
			else{
				initiateExplosion(myBall, thirdRoot);
				exploded=false;
				moveExploded = true;
			}    			
		}
		if (moveExploded) {  //creates a set motion for each explosion object to move in a particular direction
			for (int i = 0; i<explosion.length; i++) {		
				if (i==0) {
					explosion[i].setCenterY(explosion[i].getCenterY() - 150 * elapsedTime);
					explosion[i].setCenterX(explosion[i].getCenterX() - 150* elapsedTime);
				}
				if (i==1) {
					explosion[i].setCenterY(explosion[i].getCenterY() + 150 * elapsedTime);
					explosion[i].setCenterX(explosion[i].getCenterX() + 150 * elapsedTime);
				}
				if (i==2) {
					explosion[i].setCenterY(explosion[i].getCenterY() - 30 * elapsedTime);
					explosion[i].setCenterX(explosion[i].getCenterX() - 150 * elapsedTime);
				}
				if (i==3) {
					explosion[i].setCenterY(explosion[i].getCenterY() - 30 * elapsedTime);
					explosion[i].setCenterX(explosion[i].getCenterX() + 150 * elapsedTime);
				}
				if (i==4) {
					explosion[i].setCenterY(explosion[i].getCenterY() - 80 * elapsedTime);
					explosion[i].setCenterX(explosion[i].getCenterX() - 100 * elapsedTime);
				}
			}
		}
		// update attributes
		textLives.setText("Lives: "+ life);
		textScore.setText("Score: "+ score);
		textPower.setText("Power:" + power);
		finalScore.setText("Final Score: "+ score);
		//updating color attributes of splash screen text
		textLives.setFill(Color.WHITE);
		textScore.setFill(Color.WHITE);
		textPower.setFill(Color.WHITE);
		if (startBool == true) { //activated by "SPACEBAR" key, sets ball in motion
			myBall.setCenterY(myBall.getCenterY() - Y_BOUNCER_SPEED * elapsedTime);
			myBall.setCenterX(myBall.getCenterX() - X_BOUNCER_SPEED * elapsedTime);
		}
		if (powerupON==true) { //sets power-up object in motion
			powerup.setCenterY(powerup.getCenterY() + 75 * elapsedTime);
		}

		// check for collisions
		//for loop to check collisions for each set of blocks of each level
		for (int i = 0; i<blocks.length;i++) {
			if (moveExploded) { //boolean if an explosion is currently activated
				for(int m = 0; m<explosion.length;m++) {
					if (explosion[m].getBoundsInParent().intersects(blocks[i].getBoundsInParent())){
						score = score+100;
						explosion[m].setCenterX(+10000);
						explosion[m].setCenterY(+10000);
						blocks[i].setX(-200);// move location of block to outside of the window frame
						blocks[i].setY(-200);
						blockCount--; //decrements number of blocks
					}
				}
			}
			//if the invincible feature is NOT activated
			if (invincible!=true) {
				//if the ball intersects the top side of a block
				if ((myBall.getBoundsInParent().intersects(blocks[i].getBoundsInParent()) && (myBall.getCenterY()+RADIUS>(blocks[i].getY())))) {
						if (levelCount==1) {
							initiatePowerUp(blocks[i], firstroot);
						}
						if (levelCount==2) {
							initiatePowerUp(blocks[i], secondRoot);
						}
						if (levelCount ==3) {
							initiatePowerUp(blocks[i], thirdRoot);
						}						        		
					if (blocks[i].getFill().equals(Color.MEDIUMSEAGREEN)) {
						blocks[i].setFill(Color.LIGHTGREEN);
						Y_BOUNCER_SPEED = Y_BOUNCER_SPEED*-1;
						score = score+100;   	      			
						break; 	        			
					}
					if (blocks[i].getFill().equals(Color.LIGHTGRAY)) {
						Y_BOUNCER_SPEED = Y_BOUNCER_SPEED*-1;
						break;
					}
					if (blocks[i].getFill().equals(Color.RED)) {	   //red block destroys adjacent blocks  	   
						if (!(blocks[i-1].getX()==-200)) {
							blocks[i-1].setX(-200);
							blocks[i-1].setY(-200);
							blockCount--;
						}
						if (!(blocks[i+1].getX()==-200)) {
							blocks[i+1].setX(-200);
							blocks[i+1].setY(-200);
							blockCount--;
						}
						blocks[i].setX(-200);// move location of block to outside of the window frame
						blocks[i].setY(-200);
						Y_BOUNCER_SPEED = Y_BOUNCER_SPEED*-1;
						blockCount--;
						break;
					}
					root.getChildren().remove(blocks[i]);
					blocks[i].setX(-200);// move location of block to outside of the window frame
					blocks[i].setY(-200);
					Y_BOUNCER_SPEED = Y_BOUNCER_SPEED*-1;
					score = score+100;
					blockCount--; //decrement number of blocks
					break;	    	        		
				}    
				//bottom side of block
				else if ((myBall.getBoundsInParent().intersects(blocks[i].getBoundsInParent()) && (myBall.getCenterY()+RADIUS<(blocks[i].getY()+BLOCK_HEIGHT)))) {
						if (levelCount==1) {
							initiatePowerUp(blocks[i], firstroot);
						}
						if (levelCount==2) {
							initiatePowerUp(blocks[i], secondRoot);
						}
						if (levelCount ==3) {
							initiatePowerUp(blocks[i], thirdRoot);
						}
					
					if (blocks[i].getFill().equals(Color.MEDIUMSEAGREEN)) {//Green blocks require two hits, changes block to Light Green
						blocks[i].setFill(Color.LIGHTGREEN);
						Y_BOUNCER_SPEED = Y_BOUNCER_SPEED*-1;
						score = score+100;

						break; 	        			
					}
					if (blocks[i].getFill().equals(Color.LIGHTGRAY)) {
						Y_BOUNCER_SPEED = Y_BOUNCER_SPEED*-1;
						break;
					}
					if (blocks[i].getFill().equals(Color.RED)) {	  //red block destroys adjacent blocks 	   
						if (!(blocks[i-1].getX()==-200)) {
							blocks[i-1].setX(-200);
							blocks[i-1].setY(-200);
							blockCount--;
						}
						if (!(blocks[i+1].getX()==-200)) {
							blocks[i+1].setX(-200);
							blocks[i+1].setY(-200);
							blockCount--;
						}
						blocks[i].setX(-200);// move location of block to outside of the window frame
						blocks[i].setY(-200);
						Y_BOUNCER_SPEED = Y_BOUNCER_SPEED*-1;
						blockCount--;
						break;
					}
					root.getChildren().remove(blocks[i]);
					blocks[i].setX(-200);// move location of block to outside of the window frame
					blocks[i].setY(-200);
					Y_BOUNCER_SPEED = Y_BOUNCER_SPEED*-1;
					score = score+100;
					blockCount--;
					break;	    	      
				}        			
				//left side of block
				else if ((myBall.getBoundsInParent().intersects(blocks[i].getBoundsInParent()) && (myBall.getCenterX()+RADIUS<(blocks[i].getX())))) {
				
						if (levelCount==1) {
							initiatePowerUp(blocks[i], firstroot);
						}
						if (levelCount==2) {
							initiatePowerUp(blocks[i], secondRoot);
						}
						if (levelCount ==3) {
							initiatePowerUp(blocks[i], thirdRoot);
						}
					
					if (blocks[i].getFill().equals(Color.MEDIUMSEAGREEN)) {//Green blocks require two hits, changes block to Light Green
						blocks[i].setFill(Color.LIGHTGREEN);
						Y_BOUNCER_SPEED = Y_BOUNCER_SPEED*-1;
						score = score+100;      			
						break; 	        			
					}
					if (blocks[i].getFill().equals(Color.LIGHTGRAY)) {
						Y_BOUNCER_SPEED = Y_BOUNCER_SPEED*-1;
						break;
					}
					if (blocks[i].getFill().equals(Color.RED)) {	   //red block destroys adjacent blocks 	   
						if (!(blocks[i-1].getX()==-200)) {
							blocks[i-1].setX(-200);
							blocks[i-1].setY(-200);
							blockCount--;
						}
						if (!(blocks[i+1].getX()==-200)) {
							blocks[i+1].setX(-200);
							blocks[i+1].setY(-200);
							blockCount--;
						}
						blocks[i].setX(-200);// move location of block to outside of the window frame
						blocks[i].setY(-200);
						Y_BOUNCER_SPEED = Y_BOUNCER_SPEED*-1;
						blockCount--;
						break;
					}
					root.getChildren().remove(blocks[i]);		        		
					blocks[i].setX(-200);// move location of block to outside of the window frame
					blocks[i].setY(-200);
					X_BOUNCER_SPEED = X_BOUNCER_SPEED*-1;
					score = score+100;
					blockCount--;
					break;    	        		
				}   
				//ball intersects the right side of the block
				else if ((myBall.getBoundsInParent().intersects(blocks[i].getBoundsInParent()) && (myBall.getCenterX()+RADIUS>(blocks[i].getX()+BLOCK_WIDTH)))) {		
						//if blocks are hit, they initiate power-up drops and drop based on their respective scene/roots
						if (levelCount==1) { 
							initiatePowerUp(blocks[i], firstroot);
						}
						if (levelCount==2) {
							initiatePowerUp(blocks[i], secondRoot);
						}
						if (levelCount ==3) {
							initiatePowerUp(blocks[i], thirdRoot);
						}			
					if (blocks[i].getFill().equals(Color.MEDIUMSEAGREEN)) { //Green blocks require two hits, changes block to Light Green
						blocks[i].setFill(Color.LIGHTGREEN);
						Y_BOUNCER_SPEED = Y_BOUNCER_SPEED*-1;
						score = score+100;      			
						break; 	        			
					}
					if (blocks[i].getFill().equals(Color.LIGHTGRAY)) { //block intersects a metal block, cannot be destroyed
						Y_BOUNCER_SPEED = Y_BOUNCER_SPEED*-1;
						break;
					}
					if (blocks[i].getFill().equals(Color.RED)) {	  //red block destroys adjacent blocks    
						if (!(blocks[i-1].getX()==-200)) { //check previous block
							blocks[i-1].setX(-200);
							blocks[i-1].setY(-200);
							blockCount--;
						}
						if (!(blocks[i+1].getX()==-200)) { //check next block
							blocks[i+1].setX(-200);
							blocks[i+1].setY(-200);
							blockCount--;
						}
						blocks[i].setX(-200);// move location of block to outside of the window frame
						blocks[i].setY(-200);
						Y_BOUNCER_SPEED = Y_BOUNCER_SPEED*-1;
						blockCount--;
						break;
					}
					root.getChildren().remove(blocks[i]);
					blocks[i].setX(-200);// move location of block to outside of the window frame
					blocks[i].setY(-200);
					X_BOUNCER_SPEED = X_BOUNCER_SPEED*-1;
					score = score+100;
					blockCount--;
					break;	    	        		
				}    	    	        
			} //if the ball has the "invincible" powerup
			else { //ball will destroy every block it intersects
				if (myBall.getBoundsInParent().intersects(blocks[i].getBoundsInParent())){
					root.getChildren().remove(blocks[i]);
					blocks[i].setX(-200);// move location of block to outside of the window frame
					blocks[i].setY(-200);
					score = score+200;
					blockCount--; //decrement number of blocks
					break;
				}     				
			}
		}	    		
		if (myBall.getCenterX()<5) { //left border boundary limits
			X_BOUNCER_SPEED = X_BOUNCER_SPEED * -1;
		}
		if (myBall.getCenterX()>395) { //right border boundary limits
			X_BOUNCER_SPEED = X_BOUNCER_SPEED * -1;
		}
		if (myBall.getCenterY()<32) { //top border boundary limits
			Y_BOUNCER_SPEED = Y_BOUNCER_SPEED * -1;
		}
		//paddle response to the ball
		if (myBall.getCenterY()<myMover.getY() && myBall.getBoundsInParent().intersects(myMover.getBoundsInParent())) {
			Random rand = new Random(); //random number generator
			int n = rand.nextInt(150+1+150)-150;
			X_BOUNCER_SPEED = n;		//ball reflects off the paddle randomly
			Y_BOUNCER_SPEED = Math.abs(Y_BOUNCER_SPEED);
		}
		//case if ball is dropped
		if (myBall.getCenterY()>420) { //reset after failure
			reset(firstroot); //resets paddle and ball position
			reset(secondRoot);
			reset(thirdRoot);
			removeLives(); //subtracts 1 life from total lives
		}
		//Power-up is not caught by paddle
		if (powerup.getCenterY()>420) {
			root.getChildren().remove(powerup);
			powerupON=false;
		}
		if (powerupACTIVE==true) {
			timer++; //timer that keeps track of how long the powerup lasts for
		}
		if (powerupACTIVE==true && timer>FRAMES_PER_SECOND*5) { //keep track of timer for 5 seconds
			timer=0;
			clearPowerUp(); //clears powerup, once the five seconds are over.
		}
		//if paddle catches power-up    
		if (powerup.getCenterY()>390 && powerup.getBoundsInParent().intersects(myMover.getBoundsInParent())) {
			root.getChildren().remove(powerup);       		
			powerupON=false;
			powerup.setCenterY(-200); //removes the power-up from the screen
			if (lifecount) {
				Random rand = new Random();
				int b = rand.nextInt(6+1+0)-0; //random integer is used to select powerup
				activatePowerUp(b); //calls activatePowerUp to apply power-up effect
				lifecount=false;
			}
		}
	}

	//creates and explosion of 5 balls that detonates all blocks that they hit
	private void initiateExplosion(Circle ball, Group root) { //method to call when explosion appears
		for (int i = 0; i<explosion.length;i++) {
			explosion[i]= new Circle();
			explosion[i].setCenterX(ball.getCenterX());
			explosion[i].setCenterY(ball.getCenterY());
			explosion[i].setRadius(5);
			explosion[i].setFill(Color.ORANGE);
			explosion[i].setStroke(Color.AZURE);
			root.getChildren().add(explosion[i]); 
		}
	}

	//creates the power-up and initiates the powerup to fall from a recently destroyed block
	private void initiatePowerUp(Rectangle destroyedBlock, Group root) { //released a powerup
		if (powerupON==false) {
			if (root.getChildren().contains(powerup)) {
				root.getChildren().remove(powerup);
			}
			powerup.setRadius(5);
			powerup.setCenterX(destroyedBlock.getX()+20);
			powerup.setCenterY(destroyedBlock.getY()+10);
			powerup.setStroke(Color.AZURE);
			powerup.setFill(Color.RED);
			root.getChildren().add(powerup);
			powerupON = true;
		}
	}
	//resets paddle and ball to center position
	private void reset(Group root) { 
		myBall.setCenterX(X_START);
		myBall.setCenterY(Y_START);
		Y_BOUNCER_SPEED = 150;
		X_BOUNCER_SPEED = Math.abs(X_BOUNCER_SPEED);
		myMover.setX(MOVER_X_START);
		myMover.setY(MOVER_Y_START);
		root.getChildren().remove(powerup);
		powerupON=false;
		startBool = false;
		for (int i =0; i<explosion.length;i++) {
			root.getChildren().remove(explosion[i]);
		}
		clearPowerUp();
	}
	
	//clears all power-ups at the end of a life, or at the end of a level
	private void clearPowerUp() {  //gets rid of power-ups after time has expired
		if(Y_BOUNCER_SPEED<0) {
			Y_BOUNCER_SPEED = -150;
		}else {
			Y_BOUNCER_SPEED = 150;
		}
		myMover.setWidth(MOVER_SIZE);
		MOVER_SPEED= 20;
		invincible=false;
		powerupACTIVE = false;
		power = "";		//clear power-up string
		moveExploded=false;
		lifecount=true; //lifecount ensures that activate powerup only runs once			
	}	    
	//method to activate powerups using a switch argument
	private void activatePowerUp(int n) {
		//lighting
		switch(n) {
		case 0:  //lightning power up
			if(Y_BOUNCER_SPEED<0) {
				Y_BOUNCER_SPEED = -250;
			}else {
				Y_BOUNCER_SPEED = 250;
			}
			power = "LIGHTNING";
			break;
		case 1:	//invincible ball power up
			invincible=true;
			power = "INVINCIBLE";
			break;
		case 2: //large paddle power up
			myMover.setWidth(MOVER_SIZE*2);
			power = "LARGE";
			break;
		case 3: //extra life power up
			life++;
			power = "EXTRA LIFE";
			break;
		case 4: //extra speed on mover
			MOVER_SPEED = 35;
			power = "OIL";
			break;
		case 5: //extra speed on mover
			myMover.setWidth(MOVER_SIZE/2);
			power = "MINI";
			break;
		case 6: //explosion of 5 balls that removes each intersected block
			exploded=true;
			power = "EXPLOSION";
			break;
		}
		powerupACTIVE = true;
	}

	//removes a life if the ball fallse below the paddle
	private void removeLives() {
		life--;
	}

	// What to do each time a key is pressed
	//combination of paddle functions, cheat codes, and achievements
	private void handleKeyInput (KeyCode code) {
		if ((code == KeyCode.RIGHT & startBool==true) || (code == KeyCode.RIGHT & isPaused) ) {  //Move Paddle Right
			if (myMover.getX()>400) {
				myMover.setX(-50);
			}else {
				myMover.setX(myMover.getX() + MOVER_SPEED);
			}
		}
		else if ((code == KeyCode.LEFT & startBool==true) || (code == KeyCode.LEFT & isPaused)) { //Move Paddle Left
			if (myMover.getX()<-50) {
				myMover.setX(401);
			}else {
				myMover.setX(myMover.getX() - MOVER_SPEED);
			}
		}
		else if (code == KeyCode.SPACE) { //begins game by shooting ball
			startBool = true;
		}
		else if (code == KeyCode.R) {  //resets the game without subtracting a life
			reset(firstroot);
			reset(secondRoot);
			reset(thirdRoot);
		}
		else if (code == KeyCode.MINUS) { //makes the radius of the ball smaller
			if (RADIUS>1) {
				RADIUS--;
				myBall.setRadius(RADIUS);
			}
		}
		else if (code ==KeyCode.T) {  //Achievements - Press T to change color of Ball
			if (score>=500 & score<1000) {
				myBall.setFill(Color.YELLOW);
			}
			if (score>=1000 & score<2000) {
				myBall.setFill(Color.CRIMSON);
			}
			if (score>=2000 & score<3000) {
				myBall.setFill(Color.AQUA);
			}
			if (score>=3000 & score<4000) {
				myBall.setFill(Color.GOLD);
			}
			if (score>=4000) {
				myBall.setFill(Color.PINK);
			}
		}
		else if (code==KeyCode.N) { //Press N to reset the Ball color to white
			myBall.setFill(Color.WHITE);
		}
		else if (code == KeyCode.P) { //Pause the Game 
			if (isPaused) {
				isPaused=false;
				startBool=true;
			}else {
				isPaused=true;
				startBool = false;
			}
		}
	}
	/**
	 * Start the program.
	 */
	public static void main (String[] args) {
		launch(args);	//launches program 

	}
}