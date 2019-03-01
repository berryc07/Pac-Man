
/**
 * GuiPacman.java
 *
 * @author      Bairui Chen <chenbr07@outlook.com>
 * @version     2.0
 * @since       2/27/2019
 *
 */

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.*;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import java.io.*;


public class GuiPacman extends Application
{
  private String outputBoard; // The filename for where to save the Board
  private Board board; // The Game Board

  // Fill colors to choose
  private static final Color COLOR_GAME_OVER = Color.rgb(100, 100, 100, 0.5);
  private static final Color COLOR_VALUE_LIGHT = Color.rgb(249, 246, 242);
  private static final Color COLOR_VALUE_DARK = Color.rgb(119, 110, 101);

  // Instance Variables
  private int gridSize;
  private GridPane pane;
  private StackPane stack;
  private double pacmanAngle = 0;
  private GridPane endPane;

  /*
   * Name:      start
   * Purpose:   Start and keep the game running.
   * Parameter: top level JavaFX container
   * Return:    void
   */
  @Override
   public void start(Stage primaryStage)
  {
    // Process Arguments and Initialize the Game Board
    processArgs(getParameters().getRaw().toArray(new String[0]));

    // Construct the GUI
    pane = new GridPane();
    stack = new StackPane(pane);
    setPane();

    Scene scene = new Scene(stack);
    scene.setOnKeyPressed(new myKeyHandler());

    primaryStage.setTitle("GuiPacman");
    primaryStage.setScene(scene);
    primaryStage.show();
  }



  /*
   * Name:    setPane
   *
   * Purpose: Update the GridPane based on board
   */
  private void setPane(){
    Text gameName = new Text();
    gameName.setText("Pac-Man");
    gameName.setFont(Font.font("Bauhaus 93", FontWeight.BOLD, 30));
    Text score = new Text();
    score.setText("Score: " + board.getScore());
    score.setFont(Font.font("Bauhaus 93", FontWeight.BOLD, 20));

    pane.setAlignment(Pos.CENTER);
    pane.setPadding(new Insets(5, 5, 5, 5));
    pane.setHgap(4);
    pane.setVgap(4);
    pane.setStyle("-fx-background-color: rgb(95, 158, 160)");
    gridSize = board.getGrid().length;
    pane.add(gameName, 0, 0, 10, 1);
    pane.add(score, gridSize/2, 0, 10, 1);
    for(int i = 0; i <gridSize; i++){
      for(int j = 0; j < gridSize; j++){
        Tile toAdd = new Tile(board.getGrid()[i][j]);
        pane.add(toAdd.getNode(), j, i+1);
      }
    }
  }
  /*
   * Name:       myKeyHandler
   *
   * Purpose:    Handle keyboard events
   *
   *
   */
  private class myKeyHandler implements EventHandler<KeyEvent> {

   /*
    * Name:      handle
    * Purpose:   handle the KeyEvent of user's input.
    * Parameter: keyboard event
    * Return:    void
    */
    @Override
    public void handle (KeyEvent e) {

      if(e.getCode().equals(KeyCode.UP)){
        if(board.canMove(Direction.UP) && !board.isGameOver()){
          board.move(Direction.UP);
          System.out.println("Moving Up");
          pacmanAngle = 270;
          refreshPane();
        }
      }else if(e.getCode().equals(KeyCode.RIGHT)){
        if(board.canMove(Direction.RIGHT) && !board.isGameOver()){
          board.move(Direction.RIGHT);
          System.out.println("Moving Right");
          pacmanAngle = 0;
          refreshPane();
        }
      }else if(e.getCode().equals(KeyCode.DOWN)){
        if(board.canMove(Direction.DOWN) && !board.isGameOver()){
          board.move(Direction.DOWN);
          System.out.println("Moving Down");
          pacmanAngle = 90;
          refreshPane();
        }
      }else if(e.getCode().equals(KeyCode.LEFT)){
        if(board.canMove(Direction.LEFT) && !board.isGameOver()){
          board.move(Direction.LEFT);
          System.out.println("Moving Left");
          pacmanAngle = 180;
          refreshPane();
        }
      }else if(e.getCode().equals(KeyCode.SPACE)){
        // Hit space bar to start a new game if the last game is over
        restartGame();
      }else if(e.getCode().equals(KeyCode.S)){
        System.out.println("Saving Board to " + outputBoard);
        try{
          board.saveBoard(outputBoard);
        }catch(IOException ex){
          System.out.println("IOException");
        }
      }
    }


    /*
     * Name:      gameIsOver
     * Purpose:   Check if the game is over and show the gameover board.
     *
     * Return:    void
     */
    private void gameIsOver() {
      Text gameOver = new Text();
      gameOver.setText("Game Over");
      gameOver.setFont(Font.font("Bauhaus 93", FontWeight.BOLD, 30));
      Text restartInstruction = new Text();
      restartInstruction.setText("Press Space Bar to Start a New Game");
      restartInstruction.setFont(Font.font("Bauhaus 93", FontWeight.BOLD, 20));



      endPane = new GridPane();
      endPane.setAlignment(Pos.CENTER);
      endPane.setPadding(new Insets(0, 0, 0, 0));
      endPane.setHgap(0);
      endPane.setVgap(0);
      endPane.setStyle("-fx-background-color: rgb(100, 100, 100, 0.5)");
      endPane.add(gameOver,0,0);
      endPane.add(restartInstruction, 0, 1);
      endPane.setHalignment(gameOver, HPos.CENTER);
      stack.getChildren().add(endPane);
    }

    /*
     * Name:      refleshPane
     * Purpose:   Update the pane after each keyboard event
     *
     * Return:    void
     */
    private void refreshPane(){
      pane.getChildren().clear(); // Clear the outdated nodes
      setPane();
      // Place a semi-transparent overlay if the game is over
      if(board.isGameOver()){
        gameIsOver();
      }
    }

    /*
     * Name:      restartGame
     * Purpose:   Start a new game
     *
     * Return:    void
     */
    private void restartGame(){
      if(board.isGameOver()){
        System.out.println("Start a New Game");
        stack.getChildren().remove(endPane);
        board = new Board(gridSize);
        pacmanAngle = 0;
        refreshPane();
      }
    }
  } // End of Inner Class myKeyHandler



  /*
   * Name:        Tile
   *
   * Purpose:     This class tile helps to make the tiles in the board
   *              presented using JavaFX. Whenever a tile is needed,
   *              the constructor taking one char parameter is called
   *              and create certain ImageView fit to the char representation
   *              of the tile.
   *
   *
   */
  private class Tile {

    private ImageView repr;   // This field is for the Rectangle of tile.

    /*
     * Constructor
     *
     * Purpose:    Set the ImageView with proper image based on the character
                   from the grid of the board
     * Parameter:  A character from the grid of the board
     *
     */
    public Tile(char tileAppearance) {
      Image image;
      if(tileAppearance == ' '){
        image = new Image("image/dot_eaten.png");
        repr = new ImageView(image);
      }else if(tileAppearance == '*'){
        image = new Image("image/dot_uneaten.png");
        repr = new ImageView(image);
      }else if(tileAppearance == 'P'){
        image = new Image("image/pacman_right.png");
        repr = new ImageView(image);
        // Rotate the pacman image to the direction of its movement
        repr.setRotate(pacmanAngle);
      }else if(tileAppearance == 'D'){
        image = new Image("image/inky_down.png");
        repr = new ImageView(image);
      }else if(tileAppearance == 'L'){
        image = new Image("image/pinky_left.png");
        repr = new ImageView(image);
      }else if(tileAppearance == 'R'){
        image = new Image("image/blinky_right.png");
        repr = new ImageView(image);
      }else if(tileAppearance == 'U'){
        image = new Image("image/clyde_up.png");
        repr = new ImageView(image);
      }else if(tileAppearance == 'C'){
        image = new Image("image/cherry.png");
        repr = new ImageView(image);
      }else if(tileAppearance == 'X'){
        image = new Image("image/pacman_dead.png");
        repr = new ImageView(image);
      }
      if(repr instanceof ImageView){
        repr.setFitWidth(30);
        repr.setFitHeight(30);
      }
    }

    /*
     * Name: getNode
     *
     * Retrieve the ImageView node
     *
     * Return: The ImageView node
     */
    public ImageView getNode() {
      return repr;
    }

  }  // End of Inner class Tile





  // The method used to process the command line arguments
  private void processArgs(String[] args)
  {
    String inputBoard = null;   // The filename for where to load the Board
    int boardSize = 0;          // The Size of the Board

    // Arguments must come in pairs
    if((args.length % 2) != 0)
    {
      printUsage();
      System.exit(-1);
    }

    // Process all the arguments
    for(int i = 0; i < args.length; i += 2)
    {
      if(args[i].equals("-i"))
      {   // We are processing the argument that specifies
        // the input file to be used to set the board
        inputBoard = args[i + 1];
      }
      else if(args[i].equals("-o"))
      {   // We are processing the argument that specifies
        // the output file to be used to save the board
        outputBoard = args[i + 1];
      }
      else if(args[i].equals("-s"))
      {   // We are processing the argument that specifies
        // the size of the Board
        boardSize = Integer.parseInt(args[i + 1]);
      }
      else
      {   // Incorrect Argument
        printUsage();
        System.exit(-1);
      }
    }

    // Set the default output file if none specified
    if(outputBoard == null)
      outputBoard = "Pac-Man.board";
    // Set the default Board size if none specified or less than 2
    if(boardSize < 3)
      boardSize = 10;

    // Initialize the Game Board
    try{
      if(inputBoard != null)
        board = new Board(inputBoard);
      else
        board = new Board(boardSize);
    }
    catch (Exception e)
    {
      System.out.println(e.getClass().getName() + " was thrown while creating a " +
          "Board from file " + inputBoard);
      System.out.println("Either your Board(String, Random) " +
          "Constructor is broken or the file isn't " +
          "formated correctly");
      System.exit(-1);
    }
  }

  // Print the Usage Message
  private static void printUsage()
  {
    System.out.println("GuiPacman");
    System.out.println("Usage:  GuiPacman [-i|o file ...]");
    System.out.println();
    System.out.println("  Command line arguments come in pairs of the form: <command> <argument>");
    System.out.println();
    System.out.println("  -i [file]  -> Specifies a Pacman board that should be loaded");
    System.out.println();
    System.out.println("  -o [file]  -> Specifies a file that should be used to save the Pac-Man board");
    System.out.println("                If none specified then the default \"Pac-Man.board\" file will be used");
    System.out.println("  -s [size]  -> Specifies the size of the Pac-Man board if an input file hasn't been");
    System.out.println("                specified.  If both -s and -i are used, then the size of the board");
    System.out.println("                will be determined by the input file. The default size is 10.");
  }
}
