
import java.awt.*;
import java.util.Random;

/**
 * Represents a game stage with specific properties such as gravity, velocity, control keys,and visual elements.
 * Each stage can have unique behavior, colors, and helpful hints for the player.
 *
 * This class stores information for one stage of the game, including
 * physics values (gravity, velocity), player controls (key codes), and hints/clues to assist the player.

 */
public class Stage {

    // Data fields
    private double gravity;
    private double velocityX;
    private double velocityY;
    private int stageNumber;
    private int rightCode;
    private int leftCode;
    private int upCode;
    private String clue;
    private String help;
    private Color color;
    private static Color[] colorArray;


    /**
     * Constructs a new Stage with the given parameters and sets up a default color array.
     *
     * @param gravity     The gravity applied in the stage, affecting vertical movement.
     * @param velocityX   The horizontal velocity of the player in this stage.
     * @param velocityY   The vertical velocity (initial or constant) in this stage.
     * @param stageNumber The identifier for the current stage.
     * @param rightCode   The key code used for moving the player to the right.
     * @param leftCode    The key code used for moving the player to the left.
     * @param upCode      The key code used for making the player jump (use -1 if jumping is disabled(i.e. stage3)).
     * @param clue        A short clue shown to the player about the stage.
     * @param help        A hint explaining the challenge in this stage.
     */
    public Stage(double gravity, double velocityX, double velocityY,
          int stageNumber, int rightCode, int leftCode, int upCode,
          String clue, String help) {
        this.gravity = gravity;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.stageNumber = stageNumber;
        this.rightCode = rightCode;
        this.leftCode = leftCode;
        this.upCode = upCode;
        this.clue = clue;
        this.help = help;

        // Creating a color array with 5 unique elements, specific to each stage.
        this.colorArray = new Color[5];
    }

    /**
     * Gets the stage number.
     *
     * @return The identifier for the current stage.
     */
    public int getStageNumber() {
        return stageNumber;
    }

    /**
     * Gets the gravity value for this stage.
     *
     * @return The gravity applied in this stage.
     */
    public double getGravity() {
        return gravity;
    }

    /**
     * Gets the horizontal velocity of the player.
     *
     * @return The horizontal velocity in this stage.
     */
    public double getVelocityX() {
        return velocityX;
    }

    /**
     * Gets the vertical velocity of the player.
     *
     * @return The vertical velocity in this stage.
     */
    public double getVelocityY() {
        return velocityY;
    }

    public int[] getKeyCodes() {
        return new int[] {rightCode,leftCode,upCode};
    }

    /**
     * Gets the clue for the stage.
     *
     * @return A brief clue about the stage.
     */
    public String getClue() {
        return clue;
    }

    /**
     * Gets a hint for the stage.
     *
     * @return A hint for the stage.
     */
    public String getHelp() {
        return help;
    }

    /**
     * Generates random colors and assigns them to the colorArray.
     * Each color is created using random values for red (R), green (G),
     * and blue (B) components, ranging from 0 to 255.
     */
    public static void setColorArray(){
        Random random = new Random();

        for (int i = 0; i < 5; i++) {
            int red = random.nextInt(256);
            int green = random.nextInt(256);
            int blue = random.nextInt(256);
            colorArray[i] = new Color(red, green, blue);
        }
    }
    /**
     * Gets the color associated with this stage.
     *
     * @return The stage's color if within range; otherwise, returns Color.BLACK as a fallback(in ideal case it will never happen).
     */
    public Color getColor() {
        if (stageNumber >= 0 && stageNumber < colorArray.length) {
            color = colorArray[stageNumber];
            return color;
        } else {
            return Color.BLACK;
        }
    }
}
