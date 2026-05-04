package src;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GridEntitySprite {
    public final BufferedImage sprite;
    public final BufferedImage hit_sprite;
    public final BufferedImage dead_sprite;

    public GridEntitySprite(BufferedImage sprite){
        this.sprite = sprite;
        this.hit_sprite = tint(sprite, Color.RED);
        this.dead_sprite = tint(sprite, Color.BLACK);
    }

    private BufferedImage tint(BufferedImage img, Color color){
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = bufferedImage.createGraphics();
        g2D.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
        g2D.setXORMode(color);
        g2D.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
        g2D.setPaintMode();
        g2D.dispose();
        return bufferedImage;
    }
}
