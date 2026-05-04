package src.behaviors;

import src.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static src.behaviors.PlayerBehavior.MOON_FREEZE_TICKS;


//TODO: make it move towards the player
public class EnemyBehavior implements Behavior {
    protected int time_of_last_move = 0;
    protected final static int base_time_to_move = 40;
    protected final static int damage = 20;


    @Override
    public void update(GridEntity entity, Game game) {
        if (game.getCards().contains(TarotDeck.Card.THE_MOON)) {
            if (((PlayerBehavior) (game.get_player().getBehavior())).enemy_in_range(entity, game, false)) {
                time_of_last_move = Math.min(time_of_last_move + MOON_FREEZE_TICKS, game.getTick_counter() + MOON_FREEZE_TICKS);
            }
        }
        if (game.getTick_counter() - time_of_last_move > time_to_move(entity, game) && !entity.is_dead()) {
            move(entity, game);
            time_of_last_move = game.getTick_counter();
        }
    }


    protected double distance_to_player_with_move(GridEntity entity, Game game, Field.FieldPosition move){
        Field field = game.getField();
        Field.FieldPosition our_pos = field.get_pos(entity);
        GridEntity player = game.get_player();
        Field.FieldPosition player_pos = field.get_pos(game.get_player());
        //this formula is a bit weird, but it gets the distance between two axis-aligned-bounding boxes(non-rotated rectangles)
        //The formula is made weirder by the fact that entity positions are stored in the top left and not the bottom left
        //I checked in Desmos and the math seems to work (desmos link https://www.desmos.com/calculator/pe7og0kzzk)
        return Math.hypot(
                Math.max(Math.abs((our_pos.x + move.x) * 2 + entity.getWidth() - player_pos.x * 2 - player.getWidth()) - entity.getWidth() - player.getWidth() + 2, 0),
                Math.max(Math.abs((our_pos.y + move.y) * 2 - entity.getHeight() - player_pos.y * 2 + player.getHeight()) - entity.getHeight() - player.getHeight() + 2, 0)
        )/2;
    }

    protected void move(GridEntity entity, Game game){
        Field field = game.getField();
        //every possible move we can make
        Field.FieldPosition[] movements = new Field.FieldPosition[]{
                new Field.FieldPosition(0, 0),
                new Field.FieldPosition(-1, 0),
                new Field.FieldPosition(0, 1),
                new Field.FieldPosition(1, 0),
                new Field.FieldPosition(0, -1),
        };
        //sort our movements by how close they will make us towards the player
        Field.FieldPosition[] sorted_movements = Arrays.stream(movements)
                .sorted(Comparator.comparing((move) -> distance_to_player_with_move(entity, game, move)))
                .toArray(Field.FieldPosition[]::new);

        //we go through each movement
        for (Field.FieldPosition move : sorted_movements) {
            //move in that direction
            field.move_entity(entity, move);
            ArrayList<GridEntity> overlap = field.get_overlapping_entities(entity);

            //if we would overlap another enemy, move back and continue to the next movement
            if (overlap.stream().anyMatch((ent) -> ent != game.get_player())) {
                field.move_entity(entity, move.mult(-1));
                continue;
            }

            //if we would overlap the player, deal them damage
            if (overlap.contains(game.get_player())) {
                if (game.get_player().take_damage(damage, game)) {
                    game.get_player().getBehavior().on_death(game.get_player(), game);
                }
            }

            //we moved and did not overlap with another enemy so we don't need to move anymore
            break;
        }
    }

    @Override
    public void on_death(GridEntity entity, Game game) {
        //we schedule the enemy for removal after the death indicator clears
        game.gainXp(15);
    }

    protected int time_to_move(GridEntity entity, Game game) {
        if (game.getCards().contains(TarotDeck.Card.THE_MOON)) {
            if (((PlayerBehavior) (game.get_player().getBehavior())).enemy_in_range(entity, game, false)) {
                return base_time_to_move;
            } else {
                return base_time_to_move;
            }
        } else {
            return base_time_to_move;
        }
    }
}

