package src;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Projectile {
    private final boolean parent_is_player;
    private final BufferedImage sprite;
    private double x;
    private double y;
    private final double direction; //in radians
    private final double speed; // tiles per tick
    private final int damage;
    private final double size; // tile

    public Projectile(boolean is_player, BufferedImage sprite, double x, double y, double direction, double speed, int damage, double size) {
        this.parent_is_player = is_player;
        this.sprite = sprite;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = speed;
        this.damage = damage;
        this.size = size;
    }

    public void move(){
        x += speed * Math.cos(direction);
        y += speed * Math.sin(direction);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public ArrayList<Field.FieldPosition> hitting(){
        ArrayList<Field.FieldPosition> out = new ArrayList<>();
        //generate a square around the projectile and only add tiles if they are within the circle
        //we check if the center of the tile is inside the projectile
        for (int i = (int)Math.floor(x-size/2); i <= (int)Math.ceil(x+size/2); i++) {
            for (int j = (int)Math.floor(y-size/2); j <= (int)Math.ceil(y+size/2); j++) {
                //the +0.5 means that we are checking if the projectile is touching a circle of diameter 0.95 positioned at the center of the tile
                if (Math.hypot(i - x, j - y) - 0.475 < size/2) {
                    out.add(new Field.FieldPosition(i, j));
                }
            }
        }
        return out;
    }

    public int getDamage() {
        return damage;
    }

    public double getSize() {
        return size;
    }

    public boolean isParent_is_player() {
        return parent_is_player;
    }

    public BufferedImage getSprite() {
        return sprite;
    }
}
