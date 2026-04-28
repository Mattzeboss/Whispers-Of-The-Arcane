package src.behaviors;

import src.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;


//TODO: make it move towards the player
public class EnemyBehavior implements Behavior {
    private int time_of_last_move = 0;
    private final static int base_time_to_move = 40;
    private final static int damage = 20;

    private int damage_taken_time = -hit_indicator_time;
    private final static int hit_indicator_time = Game.TICKS_PER_SECOND / 4;

    private int current_tick = 0; //we can't access the game in the on_damage_taken method

    @Override
    public void update(GridEntity entity, Game game) {
        current_tick = game.getTick_counter();
        if (game.getTick_counter() - time_of_last_move > time_to_move(entity, game) && !entity.is_dead()) {
            Field field = game.getField();
            Field.FieldPosition our_pos = field.get_pos(entity);
            GridEntity player = game.get_player();
            Field.FieldPosition player_pos = field.get_pos(game.get_player());
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
                    .sorted(Comparator.comparing((move) ->
                                    //this formula is a bit weird, but it gets the distance between two axis-aligned-bounding boxes(non-rotated rectangles)
                                    //The formula is made weirder by the fact that entity positions are stored in the top left and not the bottom left
                                    //I checked in Desmos and the math seems to work (desmos link https://www.desmos.com/calculator/pe7og0kzzk)
                                    Math.hypot(
                                            Math.max(Math.abs((our_pos.x + move.x) * 2 + entity.getWidth() - player_pos.x * 2 - player.getWidth()) - entity.getWidth() - player.getWidth() + 2, 0),
                                            Math.max(Math.abs((our_pos.y + move.y) * 2 - entity.getHeight() - player_pos.y * 2 + player.getHeight()) - entity.getHeight() - player.getHeight() + 2, 0)
                                    )
                            //technically we should divide by two, but it doesn't change the ordering so it doesn't matter
                            //I just wanted to avoid using floating point as much as possible
                    ))
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
                    if (game.get_player().take_damage(damage)) {
                        game.get_player().getBehavior().on_death(game.get_player(), game);
                    }
                }

                //we moved and did not overlap with another enemy so we don't need to move anymore
                break;
            }

            time_of_last_move = game.getTick_counter();
        }

        if (game.getTick_counter() - damage_taken_time >= hit_indicator_time && entity.is_dead()) {
            game.remove_entity(entity);
        }
    }

    @Override
    public void on_death(GridEntity entity, Game game) {
        //we schedule the enemy for removal after the death indicator clears
        game.gainXp(15);
    }

    @Override
    public void paint(GridEntity entity, Game game, double screen_x, double screen_y, Graphics2D g2D) {
        if (game.getTick_counter() - damage_taken_time < hit_indicator_time) {
            g2D.setXORMode(entity.is_dead() ? Color.BLACK : Color.RED);
            Game.draw_sprite_on_grid(g2D, entity.getSprite(), screen_x, screen_y, entity.getWidth(), entity.getHeight());
            g2D.setPaintMode();
        }
    }

    @Override
    public void on_take_damage(GridEntity entity, int amount) {
        damage_taken_time = current_tick;
    }

    private int time_to_move(GridEntity entity, Game game) {
        if (game.getCards().contains(TarotDeck.Card.THE_MOON)) {
            if (((PlayerBehavior) (game.get_player().getBehavior())).enemy_in_range(entity, game, false)) {
                return base_time_to_move * 2;
            } else {
                return base_time_to_move;
            }
        } else {
            return base_time_to_move;
        }
    }
}
