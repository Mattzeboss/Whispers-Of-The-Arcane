package src.behaviors;

import src.*;

import java.util.ArrayList;

public class BossBehavior extends EnemyBehavior {
    protected final static int bossDamage = 20;
    protected final static int boss_time_to_move = 40;

    @Override
    public void update(GridEntity entity, Game game) {
        if (game.getTick_counter() - time_of_last_move > time_to_move(entity, game) && !entity.is_dead()) {
            move(entity, game);
            time_of_last_move = game.getTick_counter();
        }
    }

    public BossBehavior() {
        MOVES_PER_SHOT = 2;
        projectile_speed = 2.5;
        projectile_damage = 10;
        size = 1.5;
    }

    @Override
    public void move(GridEntity entity, Game game) {
        Field field = game.getField();

        Field.FieldPosition[] sorted_movements = get_sorted_movements(entity, game);

        //we go through each movement
        for (Field.FieldPosition move : sorted_movements) {
            //move in that direction
            field.move_entity(entity, move);
            ArrayList<GridEntity> overlap = field.get_overlapping_entities(entity);

            for (GridEntity enemy : overlap) {
                if (enemy == game.get_player()) {
                    continue;
                }
                enemy.take_damage(bossDamage, game); //we don't run the enemies on_death because the player shouldn't gain xp for the boss killing enemies
            }

            //if we would overlap the player, deal them damage
            if (overlap.contains(game.get_player())) {
                if (game.get_player().take_damage(bossDamage, game)) {
                    game.get_player().getBehavior().on_death(game.get_player(), game);
                }
            }

            break;
        }


        try_fire(entity, game);
    }

    @Override
    protected int time_to_move(GridEntity entity, Game game) {
        if (game.getCards().contains(TarotDeck.Card.THE_MOON)) {
            if (((PlayerBehavior) (game.get_player().getBehavior())).enemy_in_range(entity, game, false)) {
                return (int) (boss_time_to_move * 1.5);
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
