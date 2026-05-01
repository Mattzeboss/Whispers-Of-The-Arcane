package src.behaviors;

import src.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PlayerBehavior implements Behavior {
    /*
    projectile stuff
     */
    private static final int SHOTS_PER_SECOND = 3;
    private static final int TICKS_PER_SHOT = Game.TICKS_PER_SECOND / SHOTS_PER_SECOND;
    private int last_shoot_tick = -TICKS_PER_SHOT;
    private static final double PROJECTILE_SPEED = 2.0; //unit is tiles per second
    private static final double PROJECTILE_SIZE = 0.5; //unit is tiles per second

    private double DAMAGE_MULTIPLIER = 1.0d;

    private boolean has_shot_yet = false;

    /*
    sun and moon stuff
     */
    private double SUN_POSITION = 0;
    private static final double SUN_MOON_RADIUS = 6;
    private static final double DAMAGE_DEBUFF_RADIUS = 3;
    private static final double rotation_per_second = 0.25;
    public static final int MOON_FREEZE_TICKS = (int) (Game.TICKS_PER_SECOND * 0.25); //freeze the enemies for 1/4 of a second
    private static final double SUN_DAMAGE = 1;



    /*
    moment stuff
     */
    private int ACTIONS_PER_SECOND = 3;
    private int last_action_tick = -get_ticks_per_action();
    private final ArrayList<Action> current_actions = new ArrayList<>();

    private static final int[] keys = new int[]{KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D};
    private static final Action[] actions = new Action[]{Action.Up, Action.Right, Action.Down, Action.Left};

    static {
        assert keys.length == actions.length : "keys and action have to be the same length";
    }

    private enum Action {
        Up,
        Down,
        Left,
        Right,
    }

    /*
    Damage indicator
     */

    private int damage_taken_time = -hit_indicator_time;
    private final static int hit_indicator_time = Game.TICKS_PER_SECOND / 4;
    private int current_tick = 0; //we can't access the game in the on_damage_taken method

    @Override
    public void update(GridEntity entity, Game game) {
        //TODO: Movement and projectile spawning
        handle_static_cards(game.getCards());
        current_tick = game.getTick_counter();

        //updates current action
        for (int i = 0; i < keys.length; i++) {
            int key = keys[i];
            Action action = actions[i];
            if (game.getKeyManager().isPressed(key) && !current_actions.contains(action)) {
                current_actions.add(action);
            }
            if (!game.getKeyManager().isDown(key)) {
                current_actions.remove(action);
            }
        }


        //resolves current action
        if (game.getTick_counter() - last_action_tick > get_ticks_per_action() && !current_actions.isEmpty()) {
            switch (current_actions.get(current_actions.size() - 1)) {
                case Up:
                    game.getField().move_entity(entity, new Field.FieldPosition(0, 1));
                    break;
                case Down:
                    game.getField().move_entity(entity, new Field.FieldPosition(0, -1));
                    break;
                case Left:
                    game.getField().move_entity(entity, new Field.FieldPosition(1, 0));
                    break;
                case Right:
                    game.getField().move_entity(entity, new Field.FieldPosition(-1, 0));
                    break;
            }
            last_action_tick = game.getTick_counter();
        }

        // sun & moon
        if (game.getCards().contains(TarotDeck.Card.THE_SUN) || game.getCards().contains(TarotDeck.Card.THE_MOON)) {
            SUN_POSITION += 2 * Math.PI / Game.TICKS_PER_SECOND * rotation_per_second;
            if (game.getCards().contains(TarotDeck.Card.THE_SUN) && game.getTick_counter()%5==0) {
                for (GridEntity g : game.getEntities()) {
                    if (g != game.get_player() && enemy_in_range(g, game, true)) {
                        if (g.take_damage((int) (SUN_DAMAGE * DAMAGE_MULTIPLIER))) {
                            g.getBehavior().on_death(g, game);
                        }
                    }
                }
            }
        }


        //projectiles
        has_shot_yet = false;
        if (game.getTick_counter() - last_shoot_tick > TICKS_PER_SHOT && game.getKeyManager().isDown(KeyEvent.VK_SPACE)) {
            handle_card_behavior(game, entity);
            if (!has_shot_yet) {
                launch_projectile_at_mouse(game, entity, (int) (4 * DAMAGE_MULTIPLIER), PROJECTILE_SIZE, Sprites.PlayerProjectile);
            }
            last_shoot_tick = game.getTick_counter();

        }


    }

    @Override
    public void on_death(GridEntity entity, Game game) {
        game.setPaused(Game.PauseStates.LoseScreen);
    }

    //use this to paint card specific effects, we don't want to litter the Game.java file
    @Override
    public void paint(GridEntity entity, Game game, double screen_x, double screen_y, Graphics2D g2D) {
        if (game.getTick_counter() - damage_taken_time < hit_indicator_time) {
            g2D.setXORMode(Color.RED);
            Game.draw_sprite_on_grid(g2D, entity.getSprite(), screen_x, screen_y, entity.getWidth(), entity.getHeight());
            g2D.setPaintMode();
        }

        if (game.getCards().contains(TarotDeck.Card.THE_SUN)) {
            double sun_x = SUN_MOON_RADIUS * Math.cos(SUN_POSITION);
            double sun_y = SUN_MOON_RADIUS * Math.sin(SUN_POSITION);

            //aura of damage
            g2D.setColor(new Color(255, 0, 0, 128));
            g2D.fillOval(
                    (int) Game.transform_x((sun_x - DAMAGE_DEBUFF_RADIUS) * Main.TILE_SIZE_PX),
                    (int) Game.transform_y((sun_y + DAMAGE_DEBUFF_RADIUS) * Main.TILE_SIZE_PX),
                    (int) (2 * DAMAGE_DEBUFF_RADIUS * Main.TILE_SIZE_PX),
                    (int) (2 * DAMAGE_DEBUFF_RADIUS * Main.TILE_SIZE_PX)
            );

            //sun
            Game.draw_sprite_on_grid(g2D, Sprites.PlayerProjectile,
                    sun_x,
                    sun_y,
                    1,
                    1
            );
        }

        if (game.getCards().contains(TarotDeck.Card.THE_MOON)) {
            double moon_x = SUN_MOON_RADIUS * Math.cos(SUN_POSITION - Math.PI);
            double moon_y =  SUN_MOON_RADIUS * Math.sin(SUN_POSITION - Math.PI);

            //aura of damage
            g2D.setColor(new Color(50, 50, 200, 128));
            g2D.fillOval(
                    (int) Game.transform_x((moon_x - DAMAGE_DEBUFF_RADIUS) * Main.TILE_SIZE_PX),
                    (int) Game.transform_y((moon_y + DAMAGE_DEBUFF_RADIUS) * Main.TILE_SIZE_PX),
                    (int) (2 * DAMAGE_DEBUFF_RADIUS * Main.TILE_SIZE_PX),
                    (int) (2 * DAMAGE_DEBUFF_RADIUS * Main.TILE_SIZE_PX)
            );

            //sun
            Game.draw_sprite_on_grid(g2D, Sprites.PlayerProjectile,
                    moon_x,
                    moon_y,
                    1,
                    1
            );
        }
    }

    @Override
    public void on_take_damage(GridEntity entity, int amount) {
        damage_taken_time = current_tick;
    }

    public int get_ticks_per_action() {
        return Game.TICKS_PER_SECOND / ACTIONS_PER_SECOND;
    }

    public void handle_card_behavior(Game game, GridEntity player) {
        ArrayList<TarotDeck.Card> cards = game.getCards();

        handle_static_cards(cards);

        for (TarotDeck.Card card : cards) {
            if (card == TarotDeck.Card.THE_MAGICIAN) {
                launch_projectile_at_mouse(game, player, (int) (16 * DAMAGE_MULTIPLIER), PROJECTILE_SIZE * 2, Sprites.Fireball);
                has_shot_yet = true;
            }
        }
    }

    public void handle_static_cards(ArrayList<TarotDeck.Card> cards) {
        DAMAGE_MULTIPLIER = 1.0d;
        ACTIONS_PER_SECOND = 3;

        for (TarotDeck.Card card : cards) {
            if (card == TarotDeck.Card.STRENGTH) {
                DAMAGE_MULTIPLIER *= 1.5;
            } else if (card == TarotDeck.Card.THE_CHARIOT) {
                ACTIONS_PER_SECOND *= 2;
            }
        }
    }

    private void launch_projectile_at_mouse(Game game, GridEntity entity, int damage, double size, BufferedImage sprite) {
        Field.FieldPosition pos = game.getField().get_pos(entity);
        //spawn projectile
        game.getProjectiles().add(new Projectile(
                true,
                sprite,
                pos.x + 0.5,
                pos.y - 0.5,
                angle_to_mouse(game, entity),
                PROJECTILE_SPEED / Game.TICKS_PER_SECOND,
                damage,
                size
        ));
    }

    private double angle_to_mouse(Game game, GridEntity entity) {
        //calculate mousex and y relative to player
        Field.FieldPosition pos = game.getField().get_pos(entity);
        double mouse_x = game.getMouseManager().getMouse_x();
        double mouse_y = game.getMouseManager().getMouse_y();
        mouse_x -= Main.SCREEN_WIDTH / 2.0;
        mouse_y -= Main.SCREEN_HEIGHT / 2.0;
        mouse_y = -mouse_y;
        mouse_x -= (pos.x - game.getCameraX()) * Main.TILE_SIZE_PX;
        mouse_y -= (pos.y - game.getCameraY()) * Main.TILE_SIZE_PX;

        return Math.atan2(mouse_y, mouse_x);
    }

    public boolean enemy_in_range(GridEntity g, Game game, boolean fromSun) {
        double rotPosX = game.getCameraX() + 0.5 + SUN_MOON_RADIUS * Math.cos(fromSun ? SUN_POSITION : SUN_POSITION - Math.PI);
        double rotPosY = game.getCameraY() - 0.5 + SUN_MOON_RADIUS * Math.sin(fromSun ? SUN_POSITION : SUN_POSITION - Math.PI);

        double distX = Math.abs(rotPosX - game.getField().get_pos(g).x - g.getWidth() / 2.0) - g.getWidth() / 2.0;
        double distY = Math.abs(rotPosY - game.getField().get_pos(g).y + g.getHeight() / 2.0) - g.getHeight() / 2.0;

        double squaredDist = Math.pow(Math.max(distX, 0), 2) + Math.pow(Math.max(distY, 0), 2);

        return squaredDist <= DAMAGE_DEBUFF_RADIUS * DAMAGE_DEBUFF_RADIUS;
    }
}
