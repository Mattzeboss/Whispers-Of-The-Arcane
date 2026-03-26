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
    private final Field field;

    public void add_entity(GridEntity e, Field.FieldPosition pos) {
        entities.add(e);
        field.add_entity(e, pos);
    }

    public void remove_entity(int ind) {
        field.remove_entity(entities.remove(ind));
    }

    public GridEntity get_player() {
        return entities.get(0); //the player will always be at index 0 because we never remove him
    }

    public Field getField() {
        return field;
    }

    /*
        Constructor
         */
    public Game(KeyManager keyManager, MouseManager mouseManager) {
        this.keyManager = keyManager;
        this.mouseManager = mouseManager;
        this.entities = new SwapAndPopList<>();
        this.field = new Field();

        add_entity(GridEntity.player(), new Field.FieldPosition(0, 0)); //adding the player
        //TODO: remove this code, it only for testing
        add_entity(GridEntity.enemy(), new Field.FieldPosition(1, 2));
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
            last_tick_time = Instant.now();


            if (paused == PauseStates.NotPaused) {
                handle_update_world();
            } else { //if we are paused
                handle_ui_update();
            }

            main.render();

            keyManager.update();
            tick_counter += 1;
        }
    }

    private void handle_ui_update() {
        //TODO: finish implementing
    }

    private void handle_update_world() {
        //TODO: add projectiles
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).getBehavior().update(entities.get(i), this);
        }
    }

    /*
    Rendering utilities
     */
    //everything that we will render
    public void paint(Graphics2D g2D) {
        g2D.setColor(Color.WHITE);

        //background
        for (int i = 0; i < Main.SCREEN_TILE_WIDTH; i++) {
            for (int j = 0; j < Main.SCREEN_TILE_HEIGHT; j++) {
                draw_sprite_on_grid(g2D, Sprites.Background, new Field.FieldPosition(i - Main.SCREEN_TILE_WIDTH / 2, j - Main.SCREEN_TILE_HEIGHT / 2));
            }
        }

        Field.FieldPosition player_pos = field.get_pos(get_player());
        //entity rendering
        for (int i = entities.size() - 1; i >= 0; i--) { //we go in reverse because we always want to draw the player on top
            GridEntity entity = entities.get(i);
            Field.FieldPosition relative_pos = field.get_pos(entity).sub(player_pos);
            //bounds check, if we would be invisible on screen
            if (
                    (relative_pos.x + entity.getWidth()) < -Main.SCREEN_TILE_WIDTH / 2 ||
                            relative_pos.x > Main.SCREEN_TILE_WIDTH / 2 ||
                            relative_pos.y < -Main.SCREEN_TILE_HEIGHT / 2 ||
                            (relative_pos.y - entity.getHeight()) > Main.SCREEN_TILE_HEIGHT / 2
            ) {
                continue;
            }

            draw_sprite_on_grid(g2D, entity.getSprite(), relative_pos);
        }
        g2D.drawLine(Main.SCREEN_WIDTH / 2, 0, Main.SCREEN_WIDTH / 2, Main.SCREEN_HEIGHT);
        g2D.drawLine(0, Main.SCREEN_HEIGHT / 2, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT / 2);
    }

    //drawing at tiles from the center
    private void draw_sprite_on_grid(Graphics2D g2D, BufferedImage sprite, Field.FieldPosition offset) {
        g2D.drawImage(
                sprite,
                transform_x(
                        (int) (
                                ((double) offset.x - 0.5) *
                                        Main.TILE_SIZE_PX
                        )
                ),
                transform_y(
                        (int) (
                                ((double) offset.y + 0.5) *
                                        Main.TILE_SIZE_PX
                        )
                ),
                Main.TILE_SIZE_PX,
                Main.TILE_SIZE_PX,
                null
        );
    }

    //lets us go from the standard Cartesian coordinates( 0,0 at the center +x is right and +y is up) to screen space coordinates
    private int transform_x(int preimage) {
        return preimage + Main.SCREEN_WIDTH / 2;
    }

    private int transform_y(int preimage) {
        return -preimage + Main.SCREEN_HEIGHT / 2;
    }
}
