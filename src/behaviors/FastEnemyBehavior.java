package src.behaviors;

import src.Game;
import src.GridEntity;

public class FastEnemyBehavior extends EnemyBehavior{
    @Override
    protected int time_to_move(GridEntity entity, Game game){
        return (int) (super.time_to_move(entity, game) / 1.5);
    }
}
