
import java.awt.*;

/**
 * The Map class handles the game environment for a given stage.
 * It controls player movement, collisions, obstacles, buttons, spikes, pipes, and door mechanics.
 */
public class Map {

    private Stage stage;
    private Player player;
    private int[][] obstacles;
    private int[] button;
    private int[] buttonFloor;
    private int[][] startPipe;
    private int[][] exitPipe;
    private int[][] spikes;
    private int[] door;
    private int buttonPressNum;
    private boolean isDoorOpen;
    private boolean isInAir;
    private boolean isButtonVisible;
    private boolean isStageChanged;
    private int deaths;


    /**
     * Constructor for the Map class.
     * Sets up the map with the current stage and player.
     *
     * @param stage The current game stage (gravity, speed, etc.).
     * @param player The player with position and movement info.
     */
    public Map(Stage stage, Player player) {
        this.stage = stage;
        this.player = player;
        obstacles = new int[][]{
                new int[]{0, 120, 120, 270}, new int[]{0, 270, 168, 330},
                new int[]{0, 330, 30, 480}, new int[]{0, 480, 180, 600},
                new int[]{180, 570, 680, 600}, new int[]{270, 540, 300, 570},
                new int[]{590, 540, 620, 570}, new int[]{680, 510, 800, 600},
                new int[]{710, 450, 800, 510}, new int[]{740, 420, 800, 450},
                new int[]{770, 300, 800, 420}, new int[]{680, 240, 800, 300},
                new int[]{680, 300, 710, 330}, new int[]{770, 180, 800, 240},
                new int[]{0, 120, 800, 150}, new int[]{560, 150, 800, 180},
                new int[]{530, 180, 590, 210}, new int[]{530, 210, 560, 240},
                new int[]{320, 150, 440, 210}, new int[]{350, 210, 440, 270},
                new int[]{220, 270, 310, 300}, new int[]{360, 360, 480, 390},
                new int[]{530, 310, 590, 340}, new int[]{560, 400, 620, 430}};

        button = new int[]{400, 390, 470, 410};
        buttonFloor = new int[]{400, 390, 470, 400};
        startPipe = new int[][]{
                new int[]{115, 450, 145, 480},
                new int[]{110, 430, 150, 450}};
        exitPipe = new int[][]{
                new int[]{720, 175, 740, 215},
                new int[]{740, 180, 770, 210}};

        //Changed the value of 4th spike {750, 301, 769, 419}
        spikes = new int[][]{
                new int[]{30, 333, 50, 423}, new int[]{121, 150, 207, 170},
                new int[]{441, 150, 557, 170}, new int[]{591, 180, 621, 200},
                new int[]{750, 300, 770, 420}, new int[]{680, 490, 710, 510},
                new int[]{401, 550, 521, 570}};
        door = new int[]{685, 180, 700, 240};
        this.buttonPressNum = 0;
        this.isDoorOpen = false;
        this.isInAir = true;
        this.isButtonVisible = true;
        this.isStageChanged = false;
        this.deaths = 0;
    }
    /**
     * Moves the player in the given direction.
     * Also checks for all interactions in the game.
     *
     * @param direction 'R' for right, 'L' for left, 'U' for jump, 'N' for nowhere.
     */
    public void movePlayer(char direction) {
        // Initializing player and setting variables for velocity, gravity and position in next frame.
        double nextX = nextPosition()[0];
        double nextY = nextPosition()[1];
        player.setElephant(direction,stage);

        // If button is pressed a given number times (1 except fourth stage (5)), door opens.
        if (buttonPressNum >= 1) {
            if (stage.getStageNumber() != 3 || buttonPressNum >= 5) {
                isDoorOpen = true;
                openDoor();
            }
        }
        // If player collides with door,as it can not pass through, its position is corrected.
        if (checkCollision(nextX, nextY, door) && !isDoorOpen) {
            correctLocation(nextX,nextY,door);
        }
        // If player collides with button, button is pressed and becomes invisible.
        if (checkCollision(nextX, nextY, button)) {
            if (isButtonVisible){
                pressButton();
            }
            isButtonVisible = false;

        } else{
            isButtonVisible = true;
        }
        // If player collides with any obstacle ,as it can not pass through, its position is corrected.
        for (int[] obstacle: obstacles){
            // If an obstacle is colliding with two or more obstacle at the same time
            // side collisions are prioritized because of in this case player does not interact with corners visually
            if (isComingFromCorner(obstacle) && findCollisionNumber(nextX,nextY) > 1){
                continue;
            }
            if ((checkCollision(nextX, nextY, obstacle))) {
                correctLocation(nextX,nextY,obstacle);
            }
        }
        // If player collides with any spike,as it can not pass through, its position is corrected.
        // The player object, button press, death and door are set to their default values.
        for (int[] spike: spikes){
            if (checkCollision(nextX, nextY, spike)) {
                player.respawn();
                buttonPressNum = 0;
                isDoorOpen = false;
                door = new int[]{685, 180, 700, 240};
                deaths += 1;
                player.setElephant('R',stage);
            }
        }
        // If stage is changed player object and door values are set to their default values.
        if (changeStage()) {
            player.respawn();
            isStageChanged = true;
            door = new int[]{685, 180, 700, 240};
        }
        // Calculating whether the player is in the air
        isInAir = isInAir();

        if (direction == 'R') {
            player.setX(player.getX() + stage.getVelocityX());
        }
        if (direction == 'L') {
            player.setX(player.getX() - stage.getVelocityX());
        }
        if ((direction == 'U'|| getStage().getStageNumber() == 2) && !isInAir) {
            player.setVelocityY(stage.getVelocityY() + stage.getGravity());
            player.setY(player.getY() + stage.getVelocityY());
        }
        if (isInAir && (direction != 'R' && direction != 'L')){ // N stands for nowhere.
            player.setVelocityY(player.getVelocityY() + stage.getGravity());
            player.setY(player.getY() + player.getVelocityY());
        }

    }
    /**
     * Checks if the player hits an object.
     *
     * @param nextX The player's center's next x position.
     * @param nextY The player's center's next y position.
     * @param obstacle The object to check, given as [x1, y1, x2, y2].
     * @return true if there is a collision, false if not.
     */
    private boolean checkCollision(double nextX, double nextY, int[] obstacle) {// abs value ile yeniden yaz
        if ((nextX + player.getHalfWidth() > obstacle[0] && nextX - player.getHalfWidth() < obstacle [2]) &&
                (nextY + player.getHalfHeight() > obstacle[1] && nextY - player.getHalfHeight() < obstacle [3])){
            return true;
        }
        return false;
    }
    /**
     * Checks whether the player is overlapping with the exit pipe area to trigger a stage change.
     *
     * @return true if the player hits with the exit pipe, false otherwise.
     */
    public boolean changeStage() { // abs value ile yeniden yaz
        if ((player.getX() + player.getHalfWidth() > exitPipe[1][0] && player.getX() - player.getHalfWidth() <  exitPipe[1][2]) &&
                (player.getY() + player.getHalfHeight()> exitPipe[1][1] && player.getY() - player.getHalfHeight() < exitPipe[1][3])){
            return true;
        }
        return false;
    }

    /**
     * Increments the number of button presses.
     */
    public void pressButton() {
        buttonPressNum += 1;
    }

    /**
     * Restarts the current stage by setting default values for player, door, and death-related values.
     */
    public void restartStage() {
        player.respawn();
        isDoorOpen = false;
        buttonPressNum =0;
        deaths ++;
        door = new int[]{685, 180, 700, 240};
    }
    /**
     * Shows the current stage object.
     *
     * @return the current stage
     */
    public Stage getStage() {
        return stage;
    }
    /**
     * Shows the player object.
     *
     * @return the current player
     */
    public Player getPlayer() {
        return player;
    }
    /**
     * Checks if the stage has been changed.
     *
     * @return true if the stage has changed, false otherwise
     */
    public boolean getStageChange(){
        return this.isStageChanged;
    }
    /**
     * Gets the number of times the player has died.
     *
     * @return the total death count
     */
    public int getDeathNumber(){
        return this.deaths;
    }
    /**
     * Checks if the player is approaching an obstacle from one of its corners.
     * The method evaluates whether the player's current position is diagonally
     * offset from the obstacle's bounding box (i.e., from one of the corners).
     *
     * @param obstacle an array representing the obstacle's bounding box with coordinates:
     *                 [leftX, topY, rightX, bottomY]
     * @return true if the player is approaching from any of the four corners of the obstacle; false otherwise
     */
    private boolean isComingFromCorner(int [] obstacle){
        if (player.getY() < obstacle[1] && player.getX() < obstacle[0]){
            return true;
        }
        else if (player.getY() < obstacle[1] && player.getX() > obstacle[2]){
            return true;
        }
        else if (player.getY() > obstacle[3] && player.getX() < obstacle[0]){
            return true;
        }
        else if (player.getY() > obstacle[3] && player.getX() > obstacle[2]){
            return true;
        }
        return false;
    }
    /**
     * Counts how many obstacles the player would collide with if moved to the specified position.
     * This method simulates the player's next movement and checks how many obstacles
     * would be collided with at the given coordinates.
     *
     * @param nextX the player's prospective X-coordinate
     * @param nextY the player's prospective Y-coordinate
     * @return the number of obstacles the player would collide with
     */
    private int findCollisionNumber(double nextX, double nextY){
        int collisionNumber = 0;
        for (int[] obstacle: obstacles){
            if ((checkCollision(nextX, nextY, obstacle))) {
                collisionNumber++;
            }
        }
        return collisionNumber;
    }
    /**
     * Opens the door by lowering its height by 2 in each frame.
     */
    private void openDoor(){
        if (door[3] > door[1]) {
            door[3] -= 2;
        }
        else{
            door[3] = door[1];
        }
    }
    /**
     * Calculates the player's center's next x and y position.
     *
     * @return An array with the next x and y position of the player's center.
     */
    private double[] nextPosition(){
        // Setting variables for velocity, gravity and position in next frame.
        double nextX= player.getX();
        double nextY= player.getY();
        double velocityX = stage.getVelocityX();
        double velocityY = player.getVelocityY();
        double gravity = stage.getGravity();

        // Same mechanism in move player is applied, but 1 frame earlier to estimate next position.
        if (StdDraw.isKeyPressed(stage.getKeyCodes()[0])){
            nextX += velocityX;
        }
        if (StdDraw.isKeyPressed(stage.getKeyCodes()[1])){
            nextX -= velocityX;
        }
        if ((StdDraw.isKeyPressed(stage.getKeyCodes()[2])|| getStage().getStageNumber() == 2) && !isInAir){
            velocityY += (stage.getVelocityY() + gravity);
            nextY += velocityY;
        }
        if (isInAir){
            velocityY += gravity;
            nextY += velocityY;
        }

        return new double[] {nextX,nextY};

    }
    /**
     * Fixes the player's position if they hit an object.
     *
     * @param nextX The player's next x position.
     * @param nextY The player's next y position.
     * @param obstacle The object the player hits, given as [x1, y1, x2, y2].
     */
    private void correctLocation(double nextX,double nextY, int[] obstacle){
        if (player.getY() < obstacle[1] && nextY > player.getY()){
            player.setY(obstacle[1] - player.getHalfHeight());
            player.setVelocityY(0);
        }
        else if (player.getY() > obstacle[3] && nextY < player.getY()){
            player.setY(obstacle[3] + player.getHalfHeight());
            player.setVelocityY(0);
        }
        else if (player.getX()  < obstacle[0] && nextX > player.getX()){
            player.setX(obstacle[0] - player.getHalfWidth());
        }
        else if (player.getX()  > obstacle[2] &&  nextX < player.getX() ){
            player.setX(obstacle[2] + player.getHalfWidth());
        }
    }
    /**
     * Checks if the player is in the air or standing on obstacles.
     * @return true if the player is in the air, false if on the ground.
     */
    private boolean isInAir() {
        for (int[] obstacle : obstacles) {
            if (player.getY() - player.getHalfHeight() == obstacle[3]
                    && player.getX() + player.getHalfWidth() > obstacle[0]
                    && player.getX() - player.getHalfWidth() < obstacle[2]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Draws everything in the current game stage related to playing mechanism, including the player, buttons, obstacles, spikes, and pipes.
     * In last stage everything except pipes and player is colored StdDraw.WHITE.
     */
    public void draw() {
        StdDraw.setPenColor(new Color(56, 93, 172)); // Color of the area
        StdDraw.filledRectangle(400, 60, 400, 60); // Drawing bottom part
        Font font = new Font("SansSerif", Font.PLAIN, 16);
        StdDraw.setFont(font);
        player = getPlayer();

        // If button is visible, it is drawn.
        if (isButtonVisible) {
            if(stage.getStageNumber() == 4){
                StdDraw.setPenColor(StdDraw.WHITE);
            }
            else{
                StdDraw.setPenColor(StdDraw.RED);
            }

            StdDraw.filledRectangle((button[0] + button[2]) / 2, (button[1] + button[3]) / 2, (button[2] - button[0]) / 2, (button[3] - button[1]) / 2);
        }
        // Drawing the button floor except in last stage.
        if(stage.getStageNumber() == 4){
            StdDraw.setPenColor(StdDraw.WHITE);
        }
        else{
            StdDraw.setPenColor(StdDraw.BLACK);
        }
        StdDraw.filledRectangle((buttonFloor[0] + buttonFloor [2]) / 2,(buttonFloor[1] + buttonFloor [3]) / 2,(buttonFloor[2] - buttonFloor [0]) / 2,(buttonFloor[3] - buttonFloor [1]) / 2);

        // Drawing the player.
        player.draw();

        // Drawing the obstacle except in last stage.
        for (int[] obstacle: obstacles){
            int centerX = (obstacle[0] + obstacle [2]) / 2;
            int centerY = (obstacle[1] + obstacle [3]) / 2;
            int halfWidth = (obstacle[2] - obstacle [0]) / 2;
            int halfHeight = (obstacle[3] - obstacle [1]) / 2;

            if(stage.getStageNumber() == 4){
                StdDraw.setPenColor(StdDraw.WHITE);
            }
            else{
                StdDraw.setPenColor(stage.getColor());
            }
            StdDraw.filledRectangle(centerX,centerY,halfWidth,halfHeight);
        }
        // Drawing the spikes except in last stage.
        for (int i = 0; i < spikes.length; i++) {
            int[] spike = spikes[i];
            int centerX = (spike[0] + spike[2]) / 2;
            int centerY = (spike[1] + spike[3]) / 2;
            int width = spike[2] - spike[0];
            int height = spike[3] - spike[1];

            double angle;
            // The spikes are located by turning depending on which wall they are lying.
            // Angles{Ceiling: 0, Left Wall: 90, Floor: 180, Right Wall: 270}
            if (i == 0 && stage.getStageNumber() != 4) {
                angle = 90;
                StdDraw.picture(centerX, centerY, "misc/Spikes.png", height, width, angle);
            }
            else if ((i == 1 || i == 2 || i == 3) && stage.getStageNumber() != 4) {
                angle = 180;
                StdDraw.picture(centerX, centerY, "misc/Spikes.png", width, height, angle);
            }
            else if (i == 4 && stage.getStageNumber() != 4) {
                angle = 270;
                StdDraw.picture(centerX, centerY, "misc/Spikes.png", height, width, angle);
            }
            else {
                if(stage.getStageNumber() !=4) {
                    angle = 0;
                    StdDraw.picture(centerX, centerY, "misc/Spikes.png", width, height, angle);
                }
            }

        }
        // Drawing the start pipe
        for (int[] startPipe : startPipe) {
            int centerX = (startPipe[0] + startPipe[2]) / 2;
            int centerY = (startPipe[1] + startPipe[3]) / 2;
            int halfWidth = (startPipe[2] - startPipe[0]) / 2;
            int halfHeight = (startPipe[3] - startPipe[1]) / 2;

            StdDraw.setPenColor(StdDraw.ORANGE);
            StdDraw.filledRectangle(centerX, centerY, halfWidth, halfHeight);
        }
        // Drawing exit pipe
        for (int[] exitPipe : exitPipe) {
            int centerX = (exitPipe[0] + exitPipe[2]) / 2;
            int centerY = (exitPipe[1] + exitPipe[3]) / 2;
            int halfWidth = (exitPipe[2] - exitPipe[0]) / 2;
            int halfHeight = (exitPipe[3] - exitPipe[1]) / 2;

            StdDraw.setPenColor(StdDraw.ORANGE);
            StdDraw.filledRectangle(centerX, centerY, halfWidth, halfHeight);
        }
        // Drawing the door except in last stage.
        //
        if(stage.getStageNumber() == 4){
            StdDraw.setPenColor(StdDraw.WHITE);
        }
        else{
            StdDraw.setPenColor(StdDraw.GREEN);
        }
        // Making door invisible while waiting before stage changes.
        if(!isStageChanged) {
            StdDraw.filledRectangle((door[0] + door[2]) / 2, (door[1] + door[3]) / 2, (door[2] - door[0]) / 2, (door[3] - door[1]) / 2);
        }
        // When stage changes a banner with a text message appears with a correct font
        if (isStageChanged){
            StdDraw.setPenColor(StdDraw.GREEN);
            StdDraw.filledRectangle(400, 300, 400, 60);
            StdDraw.setPenColor(StdDraw.WHITE);
            font = new Font("SansSerif", Font.PLAIN, 32);
            StdDraw.setFont(font);
            StdDraw.text(400,330,"You passed the stage");
            StdDraw.text(400,270,"But is the level over?!");
            font = new Font("SansSerif", Font.PLAIN, 16);
            StdDraw.setFont(font);
        }
    }
}
