package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

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
    input managers

    will help us detect inputs and store them to check later
     */
    private static final KeyManager keyManager = new KeyManager();
    private static final MouseManager mouseManager = new MouseManager();

    /*
    stuff to calculate offsets for rendering
     */
    public static int TITLE_BAR_HEIGHT = 0; //we set this later

    public int get_TITLE_BAR_HEIGHT(){
        return TITLE_BAR_HEIGHT;
    }

    /*
    The game will store all of our game logic
    I didn't put it in this class because I wanted to separate the window logic from the actual game

    it is going to be initially null until the user starts the game from the title screen
     */
    private static Game game;

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
            TITLE_BAR_HEIGHT = insets.top - insets.left; //we need to account for the shadow around the entire window
            frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT + TITLE_BAR_HEIGHT);
        });


        //lets us listen to keypresses
        addKeyListener(keyManager);
        addMouseListener(mouseManager);

        //focuses us
        requestFocusInWindow();

        //TODO: Add a title screen before this, wait for the user to press a key, then start the game
        //starts the game
        game = new Game(keyManager, mouseManager);
        game.start(this);
    }

    public void render() {
        //renders # of frames in the background then shows them in order
        //the parameter is the number of frames that are cycled through
        createBufferStrategy(2);
        BufferStrategy strategy = getBufferStrategy();
        Graphics g = null;
        do {
            try {
                g = strategy.getDrawGraphics();
            } finally {
                if (game != null) { //render can be called before game is initialized
                    game.paint((Graphics2D) g);
                }
            }
            strategy.show();
            g.dispose();
        } while (strategy.contentsLost());
        Toolkit.getDefaultToolkit().sync();
    }
}
