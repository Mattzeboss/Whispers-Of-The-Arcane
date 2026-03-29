package src.behaviors;

import src.Behavior;
import src.Field;
import src.Game;
import src.GridEntity;

import java.awt.event.KeyEvent;


//TODO: make it move towards the player
public class EnemyBehavior implements Behavior {
    @Override
    public void update(GridEntity entity, Game game) {

    }

    @Override
    public void on_death(GridEntity entity, Game game) {
        game.remove_entity(entity);
    }
}
