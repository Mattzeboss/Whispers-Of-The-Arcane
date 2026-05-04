package src.behaviors;

import src.*;

public class RangedEnemyBehavior extends EnemyBehavior{
    private final static int MOVES_PER_SHOT = 5;
    private int moves = 0;

    @Override
    protected void move(GridEntity entity, Game game){
        super.move(entity, game);
        Field field = game.getField();

        if (moves % MOVES_PER_SHOT == 0) {
            //fire projectile at player
            Field.FieldPosition player_pos = field.get_pos(game.get_player());
            Field.FieldPosition our_pos = field.get_pos(entity);

            double angle = Math.atan2(player_pos.y - our_pos.y, player_pos.x - our_pos.x);
            game.getProjectiles().add(new Projectile(
                    false,
                    Sprites.EnemyProjectile,
                    our_pos.x,
                    our_pos.y,
                    angle,
                    3.0 / Game.TICKS_PER_SECOND,
                    5,
                    0.5
            ));
        }

        moves += 1;
    }

    @Override
    protected double distance_to_player_with_move(GridEntity entity, Game game, Field.FieldPosition move) {
        return Math.abs(super.distance_to_player_with_move(entity, game, move) - 5); //will make the enemy stay a distance of 5 away from the player
    }
}
