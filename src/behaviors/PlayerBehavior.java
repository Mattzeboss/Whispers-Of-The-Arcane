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

    private static final int[] keys = new int[]{KeyEvent.VK_E};
    private static final Action[] acitons = new Action[]{Action.Left};

    static {
        assert keys.length == acitons.length : "keys and action have to be the same length";
    }
    private static enum Action{
        None,
        Left,
    }
    
    @Override
    public void update(GridEntity entity, Game game) {
        //TODO: Movement and projectile spawning
        //updates current action
        for (int i = 0; i < keys.length; i++) {
            int key = keys[i];
            Action action = acitons[i];
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
                case Left:
                    game.getField().move_entity(entity, new Field.FieldPosition(1, 0));
            }
            last_action_tick = game.getTick_counter();
        }
    }

    @Override
    public void on_death(GridEntity entity, Game game) {
        game.setPaused(Game.PauseStates.LoseScreen);
    }
}
