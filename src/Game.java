package src;

import src.behaviors.PlayerBehavior;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Iterator;

public class Game {
    /*
    Tick related stuff
     */
    public static final int TICKS_PER_SECOND = 60;
    public static final double MILLISECONDS_PER_TICK = 1000.0 / TICKS_PER_SECOND;
    private int tick_counter = 0;

    public int getTick_counter() {
        return tick_counter;
    }

    private long last_tick_time;
    private double fps = TICKS_PER_SECOND;

    /*
    Camera stuff
     */
    private double cameraX = 0.0;
    private double cameraY = 0.0;
    private static final double camera_follow_speed = 1.0-Math.pow(.25, 1.0/TICKS_PER_SECOND); // 0 means it will not move at all, 1 means it will follow the player perfectly

    public double getCameraX(){
        return cameraX;
    }

    public double getCameraY(){
        return cameraY;
    }

    /*
    Input related stuff
     */
    private final KeyManager keyManager;
    private final MouseManager mouseManager;


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
    private final GridEntity player = GridEntity.player();
    private final HashSet<GridEntity> entities = new HashSet<>();
    private final Field field = new Field();

    private final SwapAndPopList<Projectile> projectiles = new SwapAndPopList<>();

    public void add_entity(GridEntity e, Field.FieldPosition pos) {
        entities.add(e);
        field.add_entity(e, pos);
    }

    public void remove_entity(GridEntity entity) {
        entities.remove(entity);
        field.remove_entity(entity);
    }

    public GridEntity get_player() {
        return player; //the player will always be at index 0 because we never remove him
    }

    public Field getField() {
        return field;
    }

    public SwapAndPopList<Projectile> getProjectiles() {
        return projectiles;
    }

    /*
            Constructor
             */
    public Game(KeyManager keyManager, MouseManager mouseManager) {
        this.keyManager = keyManager;
        this.mouseManager = mouseManager;

        add_entity(get_player(), new Field.FieldPosition((int)cameraX, (int)cameraY)); //adding the player
        //TODO: remove this code eventually, it only for testing
        add_entity(GridEntity.large_enemy(), new Field.FieldPosition(5, 1));
//        projectiles.add(new Projectile(true, Sprites.Ball, -5, 0.5, 0, 2.0/TICKS_PER_SECOND, 100, 1.0));
    }

    /*
        src.Main loop
         */
    public void start(Main main) {
        last_tick_time = System.nanoTime();
        //main loop start
        while (true) { //this loop will exit when the user closes the app manually
            //wait for the tick to start
            long tick_start = System.nanoTime();
            double time_since_last_tick = (tick_start - last_tick_time) / 1.0e6;
            if (time_since_last_tick < MILLISECONDS_PER_TICK) {
                if (MILLISECONDS_PER_TICK - time_since_last_tick > 1) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                         //if we cannot sleep, we will busy wait
                    }
                }
                continue;
            }
            //prep for next tick
            fps = 1e3/time_since_last_tick;
            last_tick_time = tick_start;
            tick_counter += 1;

            //update state
            if (paused == PauseStates.NotPaused) {
                handle_update_world();
            } else { //if we are paused
                handle_ui_update();
            }
            keyManager.update();

            //update camera
            Field.FieldPosition player_pos = field.get_pos(get_player());
            cameraX = cameraX + camera_follow_speed*(player_pos.x - cameraX);
            cameraY = cameraY + camera_follow_speed*(player_pos.y - cameraY);

            //render
            SwingUtilities.invokeLater(main::render);
        }
    }

    private void handle_ui_update() {
        //TODO: finish implementing
    }

    private void handle_update_world() {
        //entity updating
        Iterator<GridEntity> entityIterator = entities.iterator();
        while (entityIterator.hasNext()) {
            GridEntity entity = entityIterator.next();
            entity.getBehavior().update(entity, this);
        }

        //projectile movement & hitting
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);

            //movement
            projectile.move();

            //hit detection
            damage: for (Field.FieldPosition tile: projectile.hitting()) {
                for (GridEntity ent: getField().get_entities(tile)){
                    if (projectile.isParent_is_player() != ent.getBehavior() instanceof PlayerBehavior){ //if our projectile and entity aren't on the same team
                        if (ent.take_damage(projectile.getDamage())){ // we killed it
                            ent.getBehavior().on_death(ent, this);
                        }
                        projectiles.remove(i);
                        break damage; // we only want to damage one entity per projectile
                    }
                }
            }

            //removal if out of bounds
            double projectile_distance_from_camera = Math.hypot(projectile.getX() - cameraX, projectile.getY() - cameraY) - projectile.getSize()/2;
            if (projectile_distance_from_camera > Math.hypot(Main.SCREEN_TILE_WIDTH, Main.SCREEN_TILE_HEIGHT)/2 * 1.5){
                projectiles.remove(i);
            }
        }
    }

    /*
    Rendering utilities
     */
    //everything that we will render
    public void paint(Graphics2D g2D) {
        g2D.setColor(Color.WHITE);

        //background
        for (int i = 0; i <= Main.SCREEN_TILE_WIDTH; i++) {
            for (int j = 0; j <= Main.SCREEN_TILE_HEIGHT; j++) {
                draw_sprite_on_grid(g2D, Sprites.Background, (i -  Main.SCREEN_TILE_WIDTH / 2) - Util.true_mod(cameraX, 1.0), (j - Main.SCREEN_TILE_HEIGHT / 2) - Util.true_mod(cameraY, 1.0), 1.0, 1.0);
            }
        }

        //entity rendering
        for (GridEntity entity : entities) {
            Field.FieldPosition pos = field.get_pos(entity);
            double relative_pos_x = pos.x - cameraX;
            double relative_pos_y = pos.y - cameraY;

            //bounds check, if we would be visible on screen
            if (is_rect_on_screen(pos.x, pos.y, entity.getWidth(), entity.getHeight())) {
                draw_sprite_on_grid(g2D, entity.getSprite(), relative_pos_x, relative_pos_y, entity.getWidth(), entity.getHeight());
            }

            entity.getBehavior().paint(entity, this, relative_pos_x, relative_pos_y, g2D);
        }

        //draw player
        Field.FieldPosition player_pos = field.get_pos(get_player());
        draw_sprite_on_grid(g2D, get_player().getSprite(), player_pos.x - cameraX, player_pos.y - cameraY, get_player().getWidth(), get_player().getHeight());

        //projectile rendering
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile projectile = projectiles.get(i);
            //we don't need to worry about rendering things that are too far out of the camera's view because we despawn projectiles that go too far away from the camera
            draw_sprite_on_grid(g2D, projectile.getSprite(), projectile.getX() - cameraX, projectile.getY() - cameraY, projectile.getSize(), projectile.getSize());
        }

        //draw side ui
        {
            //background
            g2D.setColor(Color.BLACK);
            g2D.fillRect(Main.SCREEN_WIDTH, 0, Main.UI_WIDTH, Main.SCREEN_HEIGHT);
            //separator line
            g2D.setColor(Color.WHITE);
            g2D.setStroke(new BasicStroke(6.0f));
            g2D.drawLine(Main.SCREEN_WIDTH - 3, 0, Main.SCREEN_WIDTH - 3, Main.SCREEN_HEIGHT);
            //health
            GameFont.draw(g2D, "Health: " + get_player().getHealth(), Main.SCREEN_TILE_WIDTH + 0.1, 0, Color.WHITE);
        }

        //FPS counter
        g2D.setColor(Color.RED);
        g2D.setFont(new Font("Ariel", Font.BOLD, 50));
        g2D.drawString("FPS: " + (int)Math.round(fps),0 , 50);
    }

    //drawing at tiles from the center
    public static void draw_sprite_on_grid(Graphics2D g2D, BufferedImage sprite, double x, double y, double width, double height) {
        g2D.drawImage(
                sprite,
                transform_x(
                        (int) (
                                (x - 0.5) *
                                        Main.TILE_SIZE_PX
                        )
                ),
                transform_y(
                        (int) (
                                (y + 0.5) *
                                        Main.TILE_SIZE_PX
                        )
                ),
                (int) (Main.TILE_SIZE_PX * width),
                (int) (Main.TILE_SIZE_PX * height),
                null
        );
    }

    //used for occulsion culling
    //x and y are given in field space not screen space
    public boolean is_rect_on_screen(double x, double y, double w, double h){
        double left_bound = cameraX - Main.SCREEN_TILE_WIDTH/2.0;
        double right_bound = cameraX + Main.SCREEN_TILE_WIDTH/2.0;
        double top_bound = cameraY + Main.SCREEN_TILE_HEIGHT/2.0;
        double bottom_bound = cameraY - Main.SCREEN_TILE_HEIGHT/2.0;

        return !( x - 0.5 > right_bound || x - 0.5 +w < left_bound || y + 0.5 - h > top_bound || y + 0.5 < bottom_bound);
    }

    //lets us go from the standard Cartesian coordinates( 0,0 at the center +x is right and +y is up) to screen space coordinates
    private static int transform_x(int preimage) {
        return preimage + Main.SCREEN_WIDTH / 2;
    }

    private static int transform_y(int preimage) {
        return -preimage + Main.SCREEN_HEIGHT / 2;
    }
}
