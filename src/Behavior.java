package src;

import java.awt.*;

public interface Behavior {
    /*
    will be called when we are updating each entity when we aren't paused
    this will handle movement of entities, and damage dealing
     */
    void update(GridEntity entity, Game game);


    /*
    will be executed upon their death, immediately
     */
    void on_death(GridEntity entity, Game game);

    /*
    used to draw extra effects if needed
     */
    default void paint(GridEntity entity, Game game, Graphics2D g2D){}
}
