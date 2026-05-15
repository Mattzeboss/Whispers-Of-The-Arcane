package src.behaviors;

import src.*;

public class RangedEnemyBehavior extends EnemyBehavior{

    @Override
    protected void move(GridEntity entity, Game game){
        super.move(entity, game);
        try_fire(entity, game);
    }

    @Override
    protected double distance_to_player_with_move(GridEntity entity, Game game, Field.FieldPosition move) {
        return Math.abs(super.distance_to_player_with_move(entity, game, move) - 5); //will make the enemy stay a distance of 5 away from the player
    }
}
