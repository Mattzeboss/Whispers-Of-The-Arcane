package src.behaviors;

import src.*;

import javax.swing.text.html.parser.Entity;
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
    private enum Action{
        Up,
        Down,
        Left,
        Right,
    }
    
    @Override
    public void update(GridEntity entity, Game game) {
        //TODO: Movement and projectile spawning
        handle_static_cards(game.getCards());

        //updates current action
        for (int i = 0; i < keys.length; i++) {
            int key = keys[i];
            Action action = actions[i];
            if (game.getKeyManager().isPressed(key) && !current_actions.contains(action)){
                current_actions.add(action);
            }
            if (!game.getKeyManager().isDown(key)){
                current_actions.remove(action);
            }
        }


        //resolves current action
        if (game.getTick_counter() - last_action_tick > get_ticks_per_action() && !current_actions.isEmpty()){
            switch (current_actions.get(current_actions.size() - 1)){
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


        //projectiles
        has_shot_yet = false;
        if (game.getTick_counter() - last_shoot_tick > TICKS_PER_SHOT && game.getKeyManager().isDown(KeyEvent.VK_E)){
            handle_card_behavior(game, entity);
            if (!has_shot_yet){
                launch_projectile_at_mouse(game, entity, (int) (4 * DAMAGE_MULTIPLIER), 0.5, Sprites.Ball);
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

    }

    public int get_ticks_per_action() {
        return Game.TICKS_PER_SECOND / ACTIONS_PER_SECOND;
    }

    public void handle_card_behavior(Game game, GridEntity player) {
        ArrayList<TarotDeck.Card> cards = game.getCards();

        handle_static_cards(cards);

        for(TarotDeck.Card card : cards) {
            if(card == TarotDeck.Card.THE_MAGICIAN) {
                launch_projectile_at_mouse(game, player, (int) (4 * DAMAGE_MULTIPLIER), .75, Sprites.Ball);
                has_shot_yet = true;
            }
        }
    }

    public void handle_static_cards(ArrayList<TarotDeck.Card> cards) {
        DAMAGE_MULTIPLIER = 1.0d;
        ACTIONS_PER_SECOND = 3;

        for(TarotDeck.Card card : cards) {
            if (card == TarotDeck.Card.STRENGTH) {
                DAMAGE_MULTIPLIER *= 1.5;
            } else if(card == TarotDeck.Card.THE_CHARIOT) {
                ACTIONS_PER_SECOND *= 2;
            }
        }
    }

    private void launch_projectile_at_mouse(Game game, GridEntity entity, int damage, double size, BufferedImage sprite){
        Field.FieldPosition pos = game.getField().get_pos(entity);
        //spawn projectile
        game.getProjectiles().add(new Projectile(
                true,
                sprite,
                pos.x + 0.5,
                pos.y - 0.5,
                angle_to_mouse(game, entity),
                PROJECTILE_SPEED/Game.TICKS_PER_SECOND,
                damage,
                size
        ));
    }

    private double angle_to_mouse(Game game, GridEntity entity){
        //calculate mousex and y relative to player
        Field.FieldPosition pos = game.getField().get_pos(entity);
        double mouse_x = game.getMouseManager().getMouse_x();
        double mouse_y = game.getMouseManager().getMouse_y();
        mouse_x -= Main.SCREEN_WIDTH/2.0;
        mouse_y -= Main.SCREEN_HEIGHT/2.0;
        mouse_y = -mouse_y;
        mouse_x -= (pos.x - game.getCameraX()) * Main.TILE_SIZE_PX;
        mouse_y -= (pos.y - game.getCameraY()) * Main.TILE_SIZE_PX;

        return Math.atan2(mouse_y, mouse_x);
    }
}
