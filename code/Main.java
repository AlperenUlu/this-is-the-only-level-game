
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Entry point for the game setup and stage initialization.
 */
public class Main {
    public static void main(String[] args){
        // Indicating jumping has no relation with up key in third stage.
        int nullButton = -1;
        // Given Stages
        Stage s1 = new Stage(-0.45, 3.65,10,0,KeyEvent.VK_RIGHT,KeyEvent.VK_LEFT,KeyEvent.VK_UP,"Arrow keys are required","Arrow keys move player ,press button and enter the second pipe");
        Stage s2 = new Stage(-0.45, 3.65,10,1,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT,KeyEvent.VK_UP,"Not always straight forward","Right and left buttons reversed");
        Stage s3 = new Stage(-2, 3.65, 24,2,KeyEvent.VK_RIGHT,KeyEvent.VK_LEFT,nullButton ,"A bit bouncy here","You jump constantly");
        Stage s4 = new Stage(-0.45, 3.65,10,3,KeyEvent.VK_RIGHT,KeyEvent.VK_LEFT,KeyEvent.VK_UP,"Never gonna give you up","Press button 5 times ");
        Stage s5 = new Stage(-0.45, 3.65,10,4,KeyEvent.VK_RIGHT,KeyEvent.VK_LEFT,KeyEvent.VK_UP,"Do you remember?","All's in former place, but the coder forgot to paint it. He's fired");
        // Adding the stages to the arraylist.
        ArrayList<Stage> stages = new ArrayList<>();
        stages.add(s1);
        stages.add(s2);
        stages.add(s3);
        stages.add(s4);
        stages.add(s5);
        // Initializing game and activating play mechanism.
        Game game = new Game(stages);
        game.play();
    }
}