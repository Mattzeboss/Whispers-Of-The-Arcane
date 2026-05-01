package src;

import src.behaviors.*;

import java.awt.image.BufferedImage;
import java.util.Random;

public class GridEntity {
    /*
        important stats
         */
    private final int MAX_HEALTH;
    private int health;
    private int width;
    private int height;

    public enum EnemyType {
        NORMAL,
        RANGED,
        FAST,
        TANK,
        BOSS;

        // NORMAL, RANGED, FAST, TANK
        private static final int[] WEIGHTS = {50, 20, 25, 5};
        private static final int TOTAL_WEIGHT;

        static {
            int sum = 0;
            for (int w : WEIGHTS) sum += w;
            TOTAL_WEIGHT = sum;
        }

        public static EnemyType generate_random() {
            int roll = (int) (Math.random() * (TOTAL_WEIGHT + 1));
            int sum = 0;
            EnemyType[] values = values();

            for (int i = 0; i < values.length - 1; i++) {
                sum += WEIGHTS[i];
                if (roll < sum) return values[i];
            }

            return NORMAL;
        }
    }

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
        behavior.on_take_damage(this, damage);
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

    public static GridEntity enemy(EnemyType type) {
        switch (type) {
            case NORMAL:
                return new GridEntity(Sprites.Jack, 50, 1, 1, new EnemyBehavior());
            case TANK:
                return new GridEntity(Sprites.King, 75, 2, 2, new EnemyBehavior());
            case RANGED:
                return new GridEntity(Sprites.Queen, 25, 1, 1, new RangedEnemyBehavior());
            case FAST:
                return new GridEntity(Sprites.Joker, 25,1, 1, new FastEnemyBehavior());
            case BOSS:
                return new GridEntity(Sprites.Joker, 2000, 3, 3, new BossBehavior());
            default:
                return enemy(EnemyType.NORMAL);
        }
    }
}
