package src.behaviors;

import src.Behavior;
import src.Field;
import src.Game;
import src.GridEntity;

import java.awt.event.KeyEvent;

public class PlayerBehavior implements Behavior {
    private int projectile_fired_tick = Integer.MIN_VALUE;
    private final static int RELOAD_TIME = 10;
    private int last_action_tick = 0;
    private Action current_action = Action.None;

    private static final int[] keys = new int[]{KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D};
    private static final Action[] actions = new Action[]{Action.Up, Action.Right, Action.Down, Action.Left};

    static {
        assert keys.length == actions.length : "keys and action have to be the same length";
    }
    private enum Action{
        None,
        Up,
        Down,
        Left,
        Right,
    }
    
    @Override
    public void update(GridEntity entity, Game game) {
        //TODO: Movement and projectile spawning
        //updates current action
        for (int i = 0; i < keys.length; i++) {
            int key = keys[i];
            Action action = actions[i];
            if (game.getKeyManager().isPressed(key)){
                current_action = action;
            }
            if (game.getKeyManager().isReleased(key)){
                if (action == current_action){
                    current_action = Action.None;
                }
            }
        }

        //resolves current action
        if (game.getTick_counter() - last_action_tick > 20 && current_action != Action.None){
            switch (current_action){
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
    }

    @Override
    public void on_death(GridEntity entity, Game game) {
        game.setPaused(Game.PauseStates.LoseScreen);
    }
}
