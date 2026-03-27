package src.behaviors;

import src.Behavior;
import src.Field;
import src.Game;
import src.GridEntity;

import java.awt.event.KeyEvent;

public class PlayerBehavior implements Behavior {
    private int projectile_fired_tick = Integer.MIN_VALUE;
    private final static int RELOAD_TIME = 10;
    
    @Override
    public void update(GridEntity entity, Game game) {
        //TODO: Movement and projectile spawning
        if (game.getKeyManager().isDown(KeyEvent.VK_E) && game.getTick_counter()%20 == 0){
            game.getField().move_entity(entity, new Field.FieldPosition(1, 0));
        }

        if (game.getKeyManager().isPressed(KeyEvent.VK_S) && (game.getTick_counter() - projectile_fired_tick) >= RELOAD_TIME){
            projectile_fired_tick = game.getTick_counter();
            //TODO: Spawn projectile
        }
    }

    @Override
    public void on_death(GridEntity entity, Game game) {
        game.setPaused(Game.PauseStates.LoseScreen);
    }
}
