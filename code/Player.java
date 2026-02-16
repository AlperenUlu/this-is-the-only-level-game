
/**
 * Represents a player character in the game, with position, velocity, and directional properties.
 * This class handles the player's movement, collision detection, and drawing of the elephant character.
 *
 * The player’s position is controlled via x and y coordinates, and the elephant’s movement direction
 * is updated based on input and the current stage of the game.
 */
 public class Player {

    // Data fields
    private double x;
    private double y;
    private double width;
    private double height;
    private double velocityY;
    private String elephantDirection;

    /**
     * Constructs a new Player with the given initial position.
     *
     * @param x The initial x component of the player's center.
     * @param y The initial y component of the player's center.
     */
    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        this.width = 20;
        this.height = 20;
        this.elephantDirection = "misc/ElephantRight.png";
    }

    /**
     * Sets the x component of the player's center.
     *
     * @param x The new x component of the player's center.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Sets the y component of the player's center.
     *
     * @param y The new x component of the player's center.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Gets the x component of the player's center.
     *
     * @return The x component of the player's center.
     */
    public double getX() {
        return this.x;
    }

    /**
     * Gets the y component of the player's center.
     *
     * @return The y component of the player's center.
     */
    public double getY() {
        return this.y;
    }

    /**
     * Sets the vertical velocity of the player.
     *
     * @param velocityY The vertical velocity to set.
     */
    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    /**
     * Gets the vertical velocity of the player.
     *
     * @return The vertical velocity of the player.
     */
    public double getVelocityY() {
        return this.velocityY;
    }

    /**
     * Respawns the player to the initial position with default velocity and direction.
     */
    public void respawn() {
        this.x = 130;
        this.y = 465;
        this.velocityY = 0;
        this.elephantDirection = "misc/ElephantRight.png";
    }

    /**
     * Sets the direction of the elephant based on the input direction and the stage.
     *
     * @param direction The direction to set ('R' for right, 'L' for left).
     * @param stage The current stage of the game to determine behavior.
     */
    public void setElephant(char direction, Stage stage) {
        // Since in second stage elephant's moving controls are inverse its images are too.
        // Elephant's image to be updated with respect to direction ('R' for right, 'L' for left).
        if (stage.getStageNumber() == 1) {
            if (direction == 'R') {
                elephantDirection = "misc/ElephantLeft.png";
            } else if (direction == 'L') {
                elephantDirection = "misc/ElephantRight.png";
            }
        } else {
            if (direction == 'R') {
                elephantDirection = "misc/ElephantRight.png";
            }
            if (direction == 'L') {
                elephantDirection = "misc/ElephantLeft.png";
            }
        }
    }
    /**
     * Gets half of the player's width.
     *
     * @return Half of the player's width.
     */
    public double getHalfWidth() {
        return this.width / 2;
    }

    /**
     * Gets half of the player's height.
     *
     * @return Half of the player's height.
     */
    public double getHalfHeight() {
        return this.height / 2;
    }


    /**
     * Draws the elephant using the current position and direction.
     */
    public void draw() {
        StdDraw.picture(x, y, elephantDirection, width, height);
    }
}
