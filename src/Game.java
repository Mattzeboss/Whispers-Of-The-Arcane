package src;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.time.Instant;
import java.util.Date;

public class Game {
    /*
    Tick related stuff
     */
    public static final int TICKS_PER_SECOND = 60;
    public static final int MILLISECONDS_PER_TICK = 1000 / TICKS_PER_SECOND;
    private int tick_counter = 0;

    public int getTick_counter() {
        return tick_counter;
    }

    /*
    Input related stuff
     */
    private KeyManager keyManager;
    private MouseManager mouseManager;


    public MouseManager getMouseManager() {
        return mouseManager;
    }

    public KeyManager getKeyManager() {
        return keyManager;
    }

    /*
    pause related stuff
     */
    public enum PauseStates {
        NotPaused, CardSelect, WinScreen, LoseScreen,
    }

    private PauseStates paused = PauseStates.NotPaused;

    public PauseStates getPaused() {
        return paused;
    }

    public void setPaused(PauseStates paused) {
        this.paused = paused;
    }

    /*
    Entities and projectiles
     */
    private final SwapAndPopList<GridEntity> entities;

    /*
    Constructor
     */
    public Game(KeyManager keyManager, MouseManager mouseManager) {
        this.keyManager = keyManager;
        this.mouseManager = mouseManager;
        this.entities = new SwapAndPopList<>();
        this.entities.add(GridEntity.player());
    }

    /*
        src.Main loop
         */
    public void start(Main main) {
        Instant last_tick_time = Instant.now();
        //main loop start
        while (true) { //this loop will exit when the user closes the app manually
            //wait for the tick to start
            if (last_tick_time.toEpochMilli() < MILLISECONDS_PER_TICK) {
                try {
                    Thread.sleep(MILLISECONDS_PER_TICK - last_tick_time.toEpochMilli());
                } catch (InterruptedException e) {
                    continue; //if we cannot sleep, we will busy wait
                }
            }


            if (paused != PauseStates.NotPaused) { //if we are paused
                handle_ui_update();
            } else {
                handle_update_world();
            }

            main.render();

            keyManager.update();
        }
    }

    private void handle_ui_update() {
        //TODO: finish implementing
    }

    private void handle_update_world() {

        //TODO: add projectiles
    }

    /*
    Rendering utilities
     */
    //everything that we will render
    public void paint(Graphics2D g2D) {
        g2D.setColor(Color.WHITE);
        g2D.drawString("centered", transform_x(0), transform_y(0));
        g2D.drawImage(entities.get(0).getSprite(), transform_x(-Main.TILE_SIZE/2), transform_y(Main.TILE_SIZE/2), Main.TILE_SIZE, Main.TILE_SIZE, null);
    }

    //drawing at tiles from the center

    //lets us go from the standard Cartesian coordinates( 0,0 at the center +x is right and +y is up) to screen space coordinates
    private int transform_x(int preimage) {
        return preimage + Main.SCREEN_WIDTH / 2;
    }

    private int transform_y(int preimage) {
        return -preimage + Main.SCREEN_HEIGHT / 2 + Main.TITLE_BAR_HEIGHT;
    }
}
