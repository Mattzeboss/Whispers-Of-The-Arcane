package src;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public abstract class Sprites {
    private static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image: " + path, e);
        }
    }

    public static final BufferedImage Player = loadImage("./src/graphics/smile.png");
    public static final BufferedImage Enemy = loadImage("./src/graphics/smile_pink.png");
    public static final BufferedImage Background = loadImage("./src/graphics/placeholder.png");
    public static final BufferedImage Ball = loadImage("./src/graphics/ball.png");
}
