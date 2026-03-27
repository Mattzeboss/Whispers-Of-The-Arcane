package src;

import src.behaviors.EnemyBehavior;
import src.behaviors.PlayerBehavior;

import java.awt.image.BufferedImage;

public class GridEntity {
    /*
        important stats
         */
    private final int MAX_HEALTH;
    private int health;
    private int width;
    private int height;

    public int getHealth() {
        return health;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /*
    Sprite for drawing
     */
    private final BufferedImage sprite;

    public BufferedImage getSprite() {
        return sprite;
    }

    /*
    src.Behavior to determine how it acts
     */
    private final Behavior behavior;

    public Behavior getBehavior() {
        return behavior;
    }

    private GridEntity(BufferedImage sprite, int MAX_HEALTH, int width, int height, Behavior behavior) {
        this.sprite = sprite;
        this.MAX_HEALTH = MAX_HEALTH;
        health = MAX_HEALTH;
        this.width = width;
        this.height = height;
        this.behavior = behavior;
    }

    public boolean is_dead() {
        return health <= 0;
    }

    //returns true if this damage killed the entity
    public boolean take_damage(int damage) {
        if (health > 0) {
            health = Math.max(0, health - damage); //we want to clamp at 0 so that we don't go below
            return is_dead();//are we still alive
        }
        return false;
    }

    public void heal(int heal) {
        if (!is_dead()) { //can't heal if we are dead
            health = Math.min(MAX_HEALTH, health + heal);
        }
    }

    /*
    default entities
     */

    public static GridEntity player() {
        return new GridEntity(Sprites.Player, 100, 1, 1, new PlayerBehavior());
    }

    public static GridEntity enemy() {
        return new GridEntity(Sprites.Enemy, 100, 1, 1, new EnemyBehavior());
    }
}
