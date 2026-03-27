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
    private final double damage;
    private final double size; // tile

    public Projectile(boolean is_player, BufferedImage sprite, double x, double y, double direction, double speed, double damage, double size) {
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
        for (int i = 0; i <= size; i++) {
            for (int j = 0; j <= size; j++) {
                if (Math.hypot(i-this.size/2, j-this.size/2) <= size/2) {
                    int t_x = (int) Math.floor(x - this.size / 2) + i;
                    int t_y = (int) Math.floor(y - this.size / 2) + j;
                    out.add(new Field.FieldPosition(t_x, t_y));
                }
            }
        }
        return out;
    }

    public double getDamage() {
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
