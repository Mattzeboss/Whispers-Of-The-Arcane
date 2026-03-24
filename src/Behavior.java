package src;

public interface Behavior {
    /*
    will be called when we are updating each entity when we aren't paused
    this will handle movement of entities, and damage dealing
     */
    public void update(GridEntity entity, Game game);


    /*
    will be executed upon their death, immediately
     */
    public void on_death(GridEntity entity, Game game);
}
