package src.behaviors;

import src.Behavior;
import src.Field;
import src.Game;
import src.GridEntity;


//TODO: make it move towards the player
public class EnemyBehavior implements Behavior {
    private int time_of_last_move = 0;
    private final static int time_to_move = 40;
    private final static int damage = 20;

    @Override
    public void update(GridEntity entity, Game game) {
        if(game.getTick_counter() - time_of_last_move > time_to_move) {
            Field field = game.getField();
            int xDist = field.get_pos(entity).x - field.get_pos(game.get_player()).x;
            int yDist = field.get_pos(entity).y - field.get_pos(game.get_player()).y;

            if(xDist < 0 && entity.getWidth() < xDist) {
                xDist = 0;
            }

            if(yDist > 0 && entity.getHeight() > yDist) {
                yDist = 0;
            }

            if (Math.abs(yDist) >= Math.abs(xDist) && yDist != 0) {
                field.move_entity(entity, yDist > 0 ?
                        new Field.FieldPosition(0, -1) :
                        new Field.FieldPosition(0, 1));
                if(yDist > 0) {
                    yDist--;
                } else {
                    yDist++;
                }
            } else if (xDist != 0) {
                field.move_entity(entity, xDist > 0 ?
                        new Field.FieldPosition(-1, 0) :
                        new Field.FieldPosition(1, 0));
                if(xDist > 0) {
                    xDist--;
                } else {
                    xDist++;
                }
            }

            if(xDist == 0 && yDist == 0) {
                if(game.get_player().take_damage(damage)) {
                    game.get_player().getBehavior().on_death(game.get_player(), game);
                }
            }

            time_of_last_move = game.getTick_counter();
        }
    }

    @Override
    public void on_death(GridEntity entity, Game game) {
        game.remove_entity(entity);
        game.gainXp(15);
    }
}
