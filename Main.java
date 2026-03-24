import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends Canvas {
    //our entry point
    public static void main(String[] args) {
        new Main();
    }

    /*
    Tile and Screen Sizes
     */
    public static final int TILE_SIZE = 16; //16x16 px tiles
    public static final int PIXEL_SCALE = 4; //each texel is going to be double the size (4x the area)
    public static final int SCREEN_TILE_WIDTH = 17; //How many tiles across is the screen
    public static final int SCREEN_TILE_HEIGHT = 13; //how many tiles tall is the screen

    //our calculated screen width and height in px
    public static final int SCREEN_WIDTH = SCREEN_TILE_WIDTH * TILE_SIZE * PIXEL_SCALE;
    public static final int SCREEN_HEIGHT = SCREEN_TILE_HEIGHT * TILE_SIZE * PIXEL_SCALE;

    /*
    Key manager

    will help us detect keypresses and store them to check later
     */
    private static final KeyManager keyManager= new KeyManager();




    public Main() {

        //Code from Game that creates the window for the display
        JFrame frame = new JFrame("Whispers of the arcane");
        frame.add(this);
        //set our screen settings
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        //we need to set the height after it has opened so that we can get the correct insets
        //otherwise our height will be incorrect
        SwingUtilities.invokeLater(() -> {
            Insets insets = frame.getInsets();
            int title_bar_height = insets.top - insets.left; //we need to account for the shadow around the entire window
            frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT + title_bar_height);
        });



        //lets us listen to keypresses
        addKeyListener(keyManager);

        //focuses us
        requestFocusInWindow();
    }
}
