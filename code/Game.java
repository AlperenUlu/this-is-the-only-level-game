
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * The Game class manages the overall gameplay loop including
 * input handling, stage transitions, timing, death tracking, and showing the game state.
 */
public class Game {

    private int stageIndex;
    private ArrayList<Stage> stages;
    private int deathNumber;
    private int gameTime;
    private int resetTime;
    private boolean resetGame;
    private boolean isGameOver;
    private boolean isHelpActivated;
    private boolean isMousePressedOnce;
    private int startTime;
    private int currentTime;
    private int pausedTime;
    private int finishTime;
    private int previousDeaths;
    private int frameTime;
    private int pauseDelay;

    /**
     * Constructs a Game with a list of stages.
     *
     * @param stages An ArrayList containing the game's stages.
     */
    public Game(ArrayList<Stage> stages) {
        this.stages = stages;
        this.stageIndex = 0;
        this.deathNumber = 0;
        this.resetTime = 0;
        this.gameTime = 0;
        this.frameTime = 25;
        this.pauseDelay = 2000;
        this.resetGame = false;
        this.isGameOver = false;
        this.isHelpActivated = false;
    }
    /**
     * Includes while loop that handles player movement, stage changes, input, drawing, and game logic.
     */
    public void play() {
        // Setting canvas size
        StdDraw.setCanvasSize(800, 600);
        StdDraw.setXscale(0, 800);
        StdDraw.setYscale(0, 600);

        // Enabling double buffering to hinder flickering
        StdDraw.enableDoubleBuffering();

        //Initializing player and map.
        Player player = new Player(130, 465); // Correct it by using start pipe location.
        Map map = new Map(stages.get(0), player);

        // Recording the time which the game started.
        startTime = (int) System.currentTimeMillis();

        // Setting colors for all stages before the game starts.
        Stage.setColorArray();

        while(true){
            // In each iteration of while loop, current time is recorded.
            currentTime = (int) System.currentTimeMillis();

            // To prevent death count from resetting when the stage ends or restarts,
            // The number of deaths in the current stage is added to the previous total.
            deathNumber = map.getDeathNumber() + previousDeaths;

            // If the game is over, a special screen of game over is shown.
            if(isGameOver){
                // Setting frame time and drawing the map.
                StdDraw.pause(25);
                draw(map);
                // When pressed "A" the game restarts.
                // Time, stage, death indexes are set to be zero
                // Player and map are initialized with their default value.
                if (StdDraw.isKeyPressed(KeyEvent.VK_A)){
                    //Stage
                    isGameOver = false;
                    stageIndex = 0;
                    //Objects
                    player = new Player(130, 465); // Correct it by using start pipe location.
                    map = new Map(stages.get(0), player);
                    // Time
                    startTime = (int) System.currentTimeMillis();
                    currentTime = (int) System.currentTimeMillis();
                    pausedTime = 0;
                    resetTime = 0;
                    finishTime = 0;
                    // Deaths
                    deathNumber = map.getDeathNumber();
                    previousDeaths = 0;
                }
                // When pressed Q, the game is finished and the player quits the game.
                if (StdDraw.isKeyPressed(KeyEvent.VK_Q)) {
                    System.exit(0);
                }
            }
            // In case game is on, playing screen is shown.
            else {
                // Setting frame time, handling mouse and keyboard inputs, and re-drawing map after clearing.
                StdDraw.pause(frameTime);
                StdDraw.clear();
                handleInput(map);
                map.draw();
                draw(map);
                // When the stage changes, the time spent on the current stage is recorded.
                // This ensures that if the player restarts the stage, the total time continues from the previous stages instead of resetting to 0.
                if (map.getStageChange()){
                    stageIndex ++;
                    resetTime += currentTime-startTime-pausedTime;
                    // When all stages are completed, game is over
                    if(stageIndex == stages.size()){
                        isGameOver = true;
                        finishTime = currentTime-startTime-pausedTime;
                        continue;
                    }
                    // Drawing both map and game settings.
                    map.draw();
                    // Decrementing stageIndex by one not to write next stage's stageIndex when the banner is shown.
                    // Re-incrementing after the draw() to get former settings.
                    stageIndex--;
                    draw(map);
                    stageIndex++;
                    StdDraw.show();

                    // In every stage change player waits 2 seconds.
                    StdDraw.pause(pauseDelay);
                    pausedTime += pauseDelay;

                    // Setting help and death variables.
                    isHelpActivated = false;
                    previousDeaths += map.getDeathNumber();
                    player = new Player(130, 465);
                    map = new Map(stages.get(stageIndex), player);
                }
                // When the game is reset, stage, death, help and time indexes are set to be default values.
                // Map and player are initialized with their default values.
                if (resetGame){
                    // Stage
                    stageIndex = 0;
                    // Objects
                    player = map.getPlayer();
                    player.respawn();
                    map = new Map(stages.get(stageIndex), player);
                    //Time
                    startTime = (int) System.currentTimeMillis();
                    currentTime = (int) System.currentTimeMillis();
                    pausedTime = 0;
                    resetTime = 0;
                    // Showing the last screen before pausing.
                    StdDraw.show();

                    // In every reset, player waits 2 seconds.
                    StdDraw.pause(pauseDelay);
                    pausedTime += pauseDelay;
                    //Deaths
                    deathNumber = map.getDeathNumber();
                    previousDeaths = 0;
                    // Reset
                    resetGame = false;
                    //Help
                    isHelpActivated = false;

                }

                StdDraw.show();

            }
        }

    }
    /**
     * Handles user input such as keyboard and mouse presses.
     *
     * @param map The current map to apply the input.
     */
    private void handleInput(Map map) {
        // Detecting right and left click.
        double mouseX = StdDraw.mouseX();
        double mouseY = StdDraw.mouseY();
        Stage stage = getCurrentStage();
        // The player moves in the pressed direction. If no direction is given, the player moves in direction 'N' (nowhere).
        if (StdDraw.isKeyPressed(stage.getKeyCodes()[0])){
            map.movePlayer('R');
        }
        if (StdDraw.isKeyPressed(stage.getKeyCodes()[1])){
            map.movePlayer('L');
        }
        if (StdDraw.isKeyPressed(stage.getKeyCodes()[2])) {
            map.movePlayer('U');
        }
        else {
            map.movePlayer('N');
        }
        // When mouse click is in specific interval, the help is seen, the game is restarted or reset.
        if(Math.abs(mouseX - 250) <= 40 && Math.abs(mouseY - 85) <= 15 && isMouseClicked()){
            isHelpActivated = true;
        }
        if(Math.abs(mouseX - 550) <= 40 && Math.abs(mouseY - 85) <= 15 && isMouseClicked()){
            map.restartStage();
            startTime = (int) System.currentTimeMillis() - resetTime;
            currentTime = (int) System.currentTimeMillis();
            pausedTime = 0;
            isHelpActivated = false;
        }
        if(Math.abs(mouseX - 400) < 80 && Math.abs(mouseY - 20) < 15 && isMouseClicked()){
            resetGame = true;
        }
        // Updating the stage index.
        stageIndex = getStageIndex(map);
    }

    /**
     * Gets the current stage index from the given map object.
     *
     * @param map The map used to determine the stage.
     * @return The current stage index.
     */
    public int getStageIndex(Map map) {
        stageIndex = map.getStage().getStageNumber();
        return this.stageIndex;
    }

    /**
     * Shows the currently active stage.
     *
     * @return The current Stage object.
     */
    public Stage getCurrentStage() {

        return this.stages.get(stageIndex);
    }
    /**
     * Checks whether the mouse was clicked (pressed once).
     *
     * @return true if the mouse is clicked (pressed and not already registered), false otherwise.
     */
    private boolean isMouseClicked() {
        boolean current = StdDraw.isMousePressed();
        boolean clicked = current && !isMousePressedOnce;
        isMousePressedOnce = current;
        return clicked;
    }
    /**
     * Converts a time value in milliseconds into minutes, seconds, and milliseconds/10.
     *
     * @param timeInMs The time in milliseconds.
     * @return An array of 3 integers: {minutes, seconds, milliseconds/10}.
     */
    private int[] getTime(int timeInMs){
        int minute;
        int second;
        int millisecond;
        minute = timeInMs / (60 * 1000);
        second = (timeInMs % (60 * 1000)) / 1000;
        millisecond = (timeInMs % 1000)/10;
        return new int[]{minute,second,millisecond};
    }
    /**
     * Shows the game screen and the game over screen depending on what's happening in the game.
     *
     * @param map The current map, used to show help or clue messages.
     */
    private void draw(Map map){
        if (resetGame) {
            // Drawing the banner and putting corresponding text message with correct font.
            StdDraw.setPenColor(StdDraw.GREEN);
            StdDraw.filledRectangle(400, 300, 400, 60);
            StdDraw.setPenColor(StdDraw.WHITE);
            Font font = new Font("SansSerif", Font.PLAIN, 50);
            StdDraw.setFont(font);
            StdDraw.text(400,300,"RESETTING THE GAME...");
            font = new Font("SansSerif", Font.PLAIN, 16);
            StdDraw.setFont(font);

        }
        if (isGameOver){
            // Drawing the end screen and putting banner and corresponding text message with correct font.
            // Shows finishing time and death number.
            StdDraw.clear();
            StdDraw.pause(frameTime);
            StdDraw.setPenColor(StdDraw.GREEN);
            StdDraw.filledRectangle(400, 300, 400, 60);
            StdDraw.setPenColor(StdDraw.WHITE);
            Font font = new Font("SansSerif", Font.PLAIN, 32);
            StdDraw.setFont(font);
            StdDraw.text(400,330,"CONGRATULATIONS YOU FINISHED THE LEVEL");
            StdDraw.text(400,290,"PRESS A TO PLAY AGAIN");
            font = new Font("SansSerif", Font.PLAIN, 20);
            StdDraw.setFont(font);
            StdDraw.text(400,250,String.format("You finished with %d deaths in %02d:%02d:%02d",deathNumber,getTime(finishTime)[0] , getTime(finishTime)[1], getTime(finishTime)[2]));
            StdDraw.show();
        }
        // Drawing the settings area and putting corresponding text message with correct font.
        // Help or clue is shown based on help activation.
        else{
            gameTime = currentTime - startTime - pausedTime;
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(250,85,"Help");
            StdDraw.rectangle(250,85,40,15); // Help button
            StdDraw.text(550,85,"Restart");
            StdDraw.rectangle(550,85,40,15); // Restart button
            StdDraw.text(400,20,"RESET THE GAME");
            StdDraw.rectangle(400,20,80,15); // Reset button
            StdDraw.text(700, 75, "Deaths: " + deathNumber);
            StdDraw.text(700, 50, "Stage: " + (stageIndex + 1));
            StdDraw.text(100, 50, String.format("%02d:%02d:%02d",getTime(gameTime)[0] , getTime(gameTime)[1], getTime(gameTime)[2]));
            StdDraw.text(100,75, "Level: 1");
            if(isHelpActivated){
                StdDraw.text(400, 85, "Help:");
                StdDraw.text(400, 55, map.getStage().getHelp());
            }
            else {
                StdDraw.text(400, 85, "Clue:");
                StdDraw.text(400, 55, map.getStage().getClue());
            }
        }

    }
}
