package src.behaviors;

import src.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static src.behaviors.PlayerBehavior.MOON_FREEZE_TICKS;

public class BossBehavior extends EnemyBehavior {
    protected final static int bossDamage = 50;
    protected final static int boss_time_to_move = 50;

    @Override
    public void update(GridEntity entity, Game game) {
        current_tick = game.getTick_counter();
        if (game.getCards().contains(TarotDeck.Card.THE_MOON)) {
            if (((PlayerBehavior) (game.get_player().getBehavior())).enemy_in_range(entity, game, false)) {
                time_of_last_move = Math.min(time_of_last_move + MOON_FREEZE_TICKS, game.getTick_counter() + MOON_FREEZE_TICKS);
            }
        }
        if (game.getTick_counter() - time_of_last_move > time_to_move(entity, game) && !entity.is_dead()) {
            move(entity, game);
            time_of_last_move = game.getTick_counter();
        }

        if (game.getTick_counter() - damage_taken_time >= hit_indicator_time && entity.is_dead()) {
            game.remove_entity(entity);
        }
    }

    @Override
    public void move(GridEntity entity, Game game) {
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

            //if we would overlap the player, deal them damage
            if (overlap.contains(game.get_player())) {
                if (game.get_player().take_damage(bossDamage)) {
                    game.get_player().getBehavior().on_death(game.get_player(), game);
                }
            }

            break;
        }
    }

    @Override
    protected int time_to_move(GridEntity entity, Game game) {
        if (game.getCards().contains(TarotDeck.Card.THE_MOON)) {
            if (((PlayerBehavior) (game.get_player().getBehavior())).enemy_in_range(entity, game, false)) {
                return (int)(boss_time_to_move * 1.5);
            } else {
                return boss_time_to_move;
            }
        } else {
            return boss_time_to_move;
        }
    }

    @Override
    public void on_death(GridEntity entity, Game game) {
        //we won!!
        game.setPaused(Game.PauseStates.WinScreen);
    }
}
