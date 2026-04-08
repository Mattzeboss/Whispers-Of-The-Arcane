package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Canvas {
    //our entry point
    public static void main(String[] args) {
        new Main();
    }

    /*
    Tile and Screen Sizes
     */
    public static final int TILE_SIZE = 16; //16x16 texel tiles
    public static final int PIXEL_SCALE = 4; //each texel is going to be double the size (4x the area)

    public static final int TILE_SIZE_PX = TILE_SIZE * PIXEL_SCALE; //16x16 px tile
    public static final int SCREEN_TILE_WIDTH = 17; //How many tiles across is the screen
    public static final int SCREEN_TILE_HEIGHT = 13; //how many tiles tall is the screen
    public static final int UI_TILE_WIDTH = 4; //how many extra tiles does the UI take

    //our calculated screen width and height in px
    public static final int SCREEN_WIDTH = SCREEN_TILE_WIDTH * TILE_SIZE_PX;
    public static final int SCREEN_HEIGHT = SCREEN_TILE_HEIGHT * TILE_SIZE_PX;
    public static final int UI_WIDTH = UI_TILE_WIDTH * TILE_SIZE_PX;

    /*
    input managers

    will help us detect inputs and store them to check later
     */
    private static final KeyManager keyManager = new KeyManager();
    private static final MouseManager mouseManager = new MouseManager();

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
        frame.setSize(SCREEN_WIDTH + UI_WIDTH, SCREEN_HEIGHT);

        (new Timer()).schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        frame.setSize(2 * (SCREEN_WIDTH + UI_WIDTH) - Main.this.getSize().width, 2 * SCREEN_HEIGHT - Main.this.getSize().height);
                    }
                }, //setting the proper height and width is complicated
                1
        );


        //lets us listen to inputs
        addKeyListener(keyManager);
        addFocusListener(keyManager);
        addMouseListener(mouseManager);
        addMouseMotionListener(mouseManager);

        //focuses us
        requestFocusInWindow(false);

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
