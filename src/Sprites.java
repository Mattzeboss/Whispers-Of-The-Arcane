package src;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public abstract class Sprites {
    public static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image: " + path, e);
        }
    }

    //card sprites are handled inside the Card enum
    public static final BufferedImage TitleScreen = loadImage("./src/graphics/titlescreen.png");
    public static final BufferedImage Player = loadImage("./src/graphics/player.png");
    public static final BufferedImage Jack = loadImage("./src/graphics/enemyjack.png");
    public static final BufferedImage Joker = loadImage("./src/graphics/enemyjoker.png");
    public static final BufferedImage King = loadImage("./src/graphics/enemyking.png");
    public static final BufferedImage Queen = loadImage("./src/graphics/enemyqueen.png");
    public static final BufferedImage Background = loadImage("./src/graphics/tile.png");
    public static final BufferedImage PlayerProjectile = loadImage("./src/graphics/ball.png");
    public static final BufferedImage EnemyProjectile = loadImage("./src/graphics/ball.png");
    public static final BufferedImage Fireball = loadImage("./src/graphics/ball.png");
    public static final BufferedImage Font = loadImage("./src/graphics/bitmap_font_8x16.png");
    public static final BufferedImage CardSelect = loadImage("./src/graphics/cards/card_select.png");
    public static final BufferedImage BackgroundCached;
    static {
        BackgroundCached = new BufferedImage((Main.SCREEN_TILE_WIDTH + 1) * Main.TILE_SIZE, (Main.SCREEN_TILE_HEIGHT + 1) * Main.TILE_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D bg = BackgroundCached.createGraphics();
        for (int i = 0; i <= Main.SCREEN_TILE_WIDTH; i++) {
            for (int j = 0; j <= Main.SCREEN_TILE_HEIGHT; j++) {
                bg.drawImage(Background, Main.TILE_SIZE * i, Main.TILE_SIZE * j, Main.TILE_SIZE, Main.TILE_SIZE, null);
            }
        }
        bg.dispose();
    }
}
