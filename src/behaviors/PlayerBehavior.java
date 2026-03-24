package src.behaviors;

import src.Behavior;
import src.Game;
import src.GridEntity;

public class PlayerBehavior implements Behavior {
    @Override
    public void update(GridEntity entity, Game game) {
        //TODO: Movement and projectile spawning
    }

    @Override
    public void on_death(GridEntity entity, Game game) {
        game.setPaused(Game.PauseStates.LoseScreen);
    }
}
