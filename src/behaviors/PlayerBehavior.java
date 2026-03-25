package src.behaviors;

import src.Behavior;
import src.Field;
import src.Game;
import src.GridEntity;

import java.awt.event.KeyEvent;

public class PlayerBehavior implements Behavior {
    @Override
    public void update(GridEntity entity, Game game) {
        //TODO: Movement and projectile spawning
        if (game.getKeyManager().isDown(KeyEvent.VK_E) && game.getTick_counter()%20 == 0){
            System.out.println(game.getField().get_pos(entity).x);
            game.getField().move_entity(entity, new Field.FieldPosition(1, 0));
        }
    }

    @Override
    public void on_death(GridEntity entity, Game game) {
        game.setPaused(Game.PauseStates.LoseScreen);
    }
}
