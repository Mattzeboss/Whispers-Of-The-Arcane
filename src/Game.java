package src;

import src.behaviors.PlayerBehavior;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.ArrayList;
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

    private long time_since_start = 0;

    public int time_since_start_seconds(){
        return (int) (time_since_start/1e9);
    }
    private double fps = TICKS_PER_SECOND;

    /*
    Card related stuff
     */
    private final TarotDeck deck = new TarotDeck();
    private final ArrayList<TarotDeck.Card> cards = new ArrayList<>();

    /*
        Camera stuff
         */
    private double cameraX = 0.0;
    private double cameraY = 0.0;
    private static final double camera_follow_speed = 1.0 - Math.pow(.25, 1.0 / TICKS_PER_SECOND); // 0 means it will not move at all, 1 means it will follow the player perfectly

    public double getCameraX() {
        return cameraX;
    }

    public double getCameraY() {
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
    Card select related stuff
     */

    private int current_selected_card = 1;
    private TarotDeck.Card[] drawn_cards = new TarotDeck.Card[0];

    private void draw_cards(){
        setPaused(PauseStates.CardSelect);
        drawn_cards = new TarotDeck.Card[Math.min(3, deck.size())];
        for (int i = 0; i < drawn_cards.length; i++) {
            drawn_cards[i] = deck.getTopCard();
        }
        current_selected_card = 1;
    }

    private void select_card(){
        if (drawn_cards.length != 0) {
            TarotDeck.Card card = drawn_cards[current_selected_card];
            cards.add(card);
        }

        //cards that weren't selected are sent to the bottom of the deck
        for (int i = 0; i < drawn_cards.length; i++) {
            if (i == current_selected_card){ continue;}
            deck.putOnBottom(drawn_cards[i]);
        }

        setPaused(PauseStates.NotPaused);
    }

    /*
    XP system
     */
    private int xp = 0;

    public int getXp(){
        return xp;
    }

    public void gainXp(int amount){
        xp += amount;
    }

    public int requiredXp(){
        return 15 * (int)Math.pow(2, cards.size());
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

    public ArrayList<TarotDeck.Card> getCards() {
        return cards;
    }

    /*
            Constructor
             */
    public Game(KeyManager keyManager, MouseManager mouseManager) {
        this.keyManager = keyManager;
        this.mouseManager = mouseManager;

        add_entity(get_player(), new Field.FieldPosition((int) cameraX, (int) cameraY)); //adding the player
        //TODO: remove this code eventually, it only for testing
        add_entity(GridEntity.large_enemy(), new Field.FieldPosition(5, 1));
        //projectiles.add(new Projectile(true, Sprites.Ball, -5, 0.5, 0, 2.0/TICKS_PER_SECOND, 100, 0.5));
        cards.add(TarotDeck.Card.STRENGTH);
        cards.add(TarotDeck.Card.STRENGTH);
        cards.add(TarotDeck.Card.STRENGTH);
        cards.add(TarotDeck.Card.STRENGTH);
        cards.add(TarotDeck.Card.THE_CHARIOT);
        cards.add(TarotDeck.Card.THE_MAGICIAN);
    }

    /*
        src.Main loop
         */
    public void start(Main main) {
        long last_tick_time = System.nanoTime();
        //main loop start
        while (true) { //this loop will exit when the user closes the app manually
            //wait for the tick to start
            long tick_start = System.nanoTime();
            long time_since_last_tick = (tick_start - last_tick_time);
            if (time_since_last_tick / 1.0e6 < MILLISECONDS_PER_TICK) {
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
            fps = 1e9 / time_since_last_tick;
            last_tick_time = tick_start;
            tick_counter += 1;


            //TODO: Remove this code, it is for testing
            if (keyManager.isPressed(KeyEvent.VK_C)){
                draw_cards();
            }

            //update state
            if (paused == PauseStates.NotPaused) {
                handle_update_world();
                time_since_start += time_since_last_tick;
            } else { //if we are paused
                handle_ui_update();
                keyManager.update();
            }


            //update camera
            Field.FieldPosition player_pos = field.get_pos(get_player());
            cameraX = cameraX + camera_follow_speed * (player_pos.x - cameraX);
            cameraY = cameraY + camera_follow_speed * (player_pos.y - cameraY);

            //render
            main.render();
        }
    }

    private void handle_ui_update() {
        //TODO: finish implementing
        switch (paused){
            case NotPaused:
                break;
            case CardSelect:
                //change selected card
                if (keyManager.isPressed(KeyEvent.VK_A)){
                    current_selected_card -= 1;
                }
                if (keyManager.isPressed(KeyEvent.VK_D)){
                    current_selected_card += 1;
                }
                if (drawn_cards.length != 0) {
                    current_selected_card = Math.floorMod(current_selected_card, drawn_cards.length);
                }

                //finalize selection
                if (keyManager.isReleased(KeyEvent.VK_ENTER)){
                    select_card();
                }
                break;
            case WinScreen:
                break;
            case LoseScreen:
                break;
        }
    }

    private void handle_update_world() {
        //entity updating
        for (GridEntity entity : entities) {
            entity.getBehavior().update(entity, this);
        }
        keyManager.update();

        //projectile movement & hitting
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);

            //movement
            projectile.move();

            //hit detection
            damage:
            for (Field.FieldPosition tile : projectile.hitting()) {
                for (GridEntity ent : getField().get_entities(tile)) {
                    if (projectile.isParent_is_player() != ent.getBehavior() instanceof PlayerBehavior) { //if our projectile and entity aren't on the same team
                        if (ent.take_damage(projectile.getDamage())) { // we killed it
                            ent.getBehavior().on_death(ent, this);
                        }
                        projectiles.remove(i);
                        break damage; // we only want to damage one entity per projectile
                    }
                }
            }

            //removal if out of bounds
            double projectile_distance_from_camera = Math.hypot(projectile.getX() - cameraX, projectile.getY() - cameraY) - projectile.getSize() / 2;
            if (projectile_distance_from_camera > Math.hypot(Main.SCREEN_TILE_WIDTH, Main.SCREEN_TILE_HEIGHT) / 2 * 1.5) {
                projectiles.remove(i);
            }
        }

        //enemy spawning
        if (tick_counter % (TICKS_PER_SECOND * 5) == 0){ //every 5 seconds

            int max_enemies = (int) (5 + Math.pow(Math.min(time_since_start_seconds(), 5 * 60) / 30.0, 2));
            int current_enemies = entities.size() - 1; //subtract 1 for the player
            if (current_enemies < max_enemies){ //if we can spawn more enemies
                int enemies_to_spawn = (int)Math.ceil((max_enemies - current_enemies)/4.0); //spawn 1/4 of however many we can spawn
                for (int i = 0; i < enemies_to_spawn; i++) {
                    final int spawn_radius = 10;

                    int left_bound = (int)Math.ceil(cameraX - Main.SCREEN_TILE_WIDTH / 2.0) - spawn_radius;
                    int right_bound = (int)Math.ceil(cameraX + Main.SCREEN_TILE_WIDTH / 2.0) + spawn_radius;
                    int top_bound = (int)Math.floor(cameraY + Main.SCREEN_TILE_HEIGHT / 2.0) + spawn_radius;
                    int bottom_bound =(int)Math.floor( cameraY - Main.SCREEN_TILE_HEIGHT / 2.0) - spawn_radius;

                    for (int j = 0; j < 100; j++) { // we get 100 attempts to spawn choose a spawn location
                        int x = (int) (Math.random() * (right_bound - left_bound) + left_bound);
                        int y = (int) (Math.random() * (top_bound - bottom_bound) + bottom_bound);

                        if (!is_rect_on_screen(x, y, 1.0, 1.0)) { //rejection sampling
                            add_entity(GridEntity.enemy(), new Field.FieldPosition(x, y));
                            break; //we only want to spawn one enemy
                        }
                    }


                }
            }
        }

        //handling xp
        if (xp >= requiredXp()){
            xp -= requiredXp();
            draw_cards();
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
                draw_sprite_on_grid(g2D, Sprites.Background, (i - Main.SCREEN_TILE_WIDTH / 2) - Util.true_mod(cameraX, 1.0), (j - Main.SCREEN_TILE_HEIGHT / 2) - Util.true_mod(cameraY, 1.0), 1.0, 1.0);
            }
        }

        //entity rendering
        for (GridEntity entity : entities) {
            Field.FieldPosition pos = field.get_pos(entity);
            double relative_pos_x = pos.x - cameraX;
            double relative_pos_y = pos.y - cameraY;

            //we want to draw the player later on top of the other entities
            if (entity != get_player()) {
                //bounds check, if we would be visible on screen
                if (is_rect_on_screen(pos.x, pos.y, entity.getWidth(), entity.getHeight())) {
                    draw_sprite_on_grid(g2D, entity.getSprite(), relative_pos_x, relative_pos_y, entity.getWidth(), entity.getHeight());
                }
            }

            entity.getBehavior().paint(entity, this, relative_pos_x, relative_pos_y, g2D);
        }

        //draw player
        Field.FieldPosition player_pos = field.get_pos(get_player());
        draw_sprite_on_grid(g2D, get_player().getSprite(), player_pos.x - cameraX, player_pos.y - cameraY, get_player().getWidth(), get_player().getHeight());

        //projectile rendering
        for (Projectile projectile : projectiles) {
            //we don't need to worry about rendering things that are too far out of the camera's view because we despawn projectiles that go too far away from the camera
            draw_sprite_on_grid(g2D, projectile.getSprite(), projectile.getX() - cameraX - projectile.getSize() / 2, projectile.getY() - cameraY + projectile.getSize() / 2, projectile.getSize(), projectile.getSize());
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
            //timer
            int s = time_since_start_seconds();
            GameFont.draw(g2D, "Time: " + String.format("%d:%02d", (s % 3600) / 60, (s % 60)), Main.SCREEN_TILE_WIDTH + 0.1, 0, Color.WHITE);
            //health
            GameFont.draw(g2D, "Health: " + get_player().getHealth(), Main.SCREEN_TILE_WIDTH + 0.1, 2, Color.WHITE);
            //xp
            GameFont.draw(g2D, "XP: " + xp + "/" + requiredXp(), Main.SCREEN_TILE_WIDTH + 0.1, 4, Color.WHITE);
            //cards
            GameFont.draw(g2D, "Cards:", Main.SCREEN_TILE_WIDTH + 0.1, 6, Color.WHITE);
            for (int i = 0; i < cards.size(); i++) {
                TarotDeck.Card card = cards.get(i);
                draw_sprite_on_screen(
                        g2D,
                        card.getSprite(),
                        Main.SCREEN_TILE_WIDTH + 0.1 + 1.1 * (i%3),
                        7 + 1.6*(i/3),
                        1.0,
                        1.5
                );
            }
        }

        //draw pause screens
        if (paused != PauseStates.NotPaused){
            g2D.setColor(new Color(105, 45, 230, 173)); // nice semi-transparent purple
            g2D.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
        }

        switch (paused){
            case NotPaused:
                break;
            case CardSelect:
                for (int i = 0; i < drawn_cards.length; i++) {
                    //distance between card centers in Tiles
                    final double card_distance = 5;
                    //Card width in Tiles
                    final double width = 3;

                    //draw the card
                    TarotDeck.Card card = drawn_cards[i];
                    double y = Main.SCREEN_TILE_HEIGHT/3.0;
                    double x = Main.SCREEN_TILE_WIDTH/2.0 + (i - (drawn_cards.length - 1)/2.0)*card_distance;
                    double height = 1.5 * width;
                    draw_sprite_on_screen(g2D, card.getSprite(), x-width/2, y - height/2, width, height);
                    //draw outline if the card is selected
                    if (current_selected_card == i){
                        draw_sprite_on_screen(g2D, Sprites.CardSelect, x-width/2, y - height/2, width, height);
                    }

                    //draw card description
                    String desc = card.getDescription();
                    double desc_width = GameFont.get_width(desc);
                    GameFont.draw(g2D, desc, x-(width/4 + card_distance/4), y+ height/2.0 + 1, width/2 + card_distance/2, Color.GREEN);
                }
                break;
            case WinScreen:
                GameFont.draw(g2D, "you win", 0, 0, Color.WHITE);
                break;
            case LoseScreen:
                GameFont.draw(g2D, "you ded", 0, 0, Color.WHITE);
                break;
        }

        //FPS counter
        g2D.setColor(Color.RED);
        g2D.setFont(new Font("Ariel", Font.BOLD, 50));
        g2D.drawString("FPS: " + (int) Math.round(fps), 0, 50);
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

    public static void draw_sprite_on_screen(Graphics2D g2D, BufferedImage sprite, double x, double y, double width, double height) {
        g2D.drawImage(
                sprite,
                (int) (
                        (x) *
                                Main.TILE_SIZE_PX
                ),
                (int) (
                        (y) *
                                Main.TILE_SIZE_PX
                ),
                (int) (Main.TILE_SIZE_PX * width),
                (int) (Main.TILE_SIZE_PX * height),
                null
        );
    }

    //used for occulsion culling
    //x and y are given in field space not screen space
    public boolean is_rect_on_screen(double x, double y, double w, double h) {
        double left_bound = cameraX - Main.SCREEN_TILE_WIDTH / 2.0;
        double right_bound = cameraX + Main.SCREEN_TILE_WIDTH / 2.0;
        double top_bound = cameraY + Main.SCREEN_TILE_HEIGHT / 2.0;
        double bottom_bound = cameraY - Main.SCREEN_TILE_HEIGHT / 2.0;

        return !(x - 0.5 > right_bound || x - 0.5 + w < left_bound || y + 0.5 - h > top_bound || y + 0.5 < bottom_bound);
    }

    //lets us go from the standard Cartesian coordinates( 0,0 at the center +x is right and +y is up) to screen space coordinates
    private static int transform_x(int preimage) {
        return preimage + Main.SCREEN_WIDTH / 2;
    }

    private static int transform_y(int preimage) {
        return -preimage + Main.SCREEN_HEIGHT / 2;
    }
}
