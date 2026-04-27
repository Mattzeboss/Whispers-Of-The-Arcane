package src;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public abstract class GameFont {
    private static final int[] char_width = new int[100];

    static {
        for (int i = 0; i < char_width.length; i++) {
            BufferedImage character = Sprites.Font.getSubimage(8 * (i % 10), 16 * (i / 10), 8, 16);
            while (char_width[i] < 7 && column_is_clear(character, 7-char_width[i])) {
                char_width[i]++;
            }
            char_width[i] = 9 - char_width[i];
        }

        char_width[0] = 5; //space should be wide, even though it takes up no space
    }

    private static boolean column_is_clear(BufferedImage img, int col) {
        for (int i = 0; i < img.getHeight(); i++) {
            if ((img.getRGB(col, i) >>> 24) != 0) {
                return false;
            }
        }
        return true;
    }


    public static void draw(Graphics2D g2D, String str, double x, double y, Color color) {
        draw(g2D, str, x, y, Double.MAX_VALUE, color);
    }

    public static void draw(Graphics2D g2D, String str, double x, double y, double max_width, Color color) {
        RescaleOp recolor = new RescaleOp(color.getComponents(null), new float[4], null);
        AffineTransformOp aff = new AffineTransformOp(AffineTransform.getScaleInstance(Main.PIXEL_SCALE, Main.PIXEL_SCALE), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        int offset = 0;
        int line = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '\n' && offset < max_width*Main.TILE_SIZE) {
                int ascii = (int) str.charAt(i) - 32;
                if (offset == 0 && line>=1){
                    if (str.charAt(i) == ' ') {
                        continue;
                    }
                }
                if (char_width[ascii] + offset >= max_width*Main.TILE_SIZE && str.charAt(i) != ' '){
                    if (str.charAt(i-1) != ' ') {
                        ascii = (int) '-' - 32;
                        i--;
                    }else{
                        offset += char_width[ascii];
                        i--;
                        continue;
                    }
                }
                g2D.drawImage(recolor.filter(Sprites.Font.getSubimage(8 * (ascii % 10), 16 * (ascii / 10), 8, 16), null), aff, (int) (Main.TILE_SIZE_PX * (x + (double) offset / Main.TILE_SIZE)), (int) (Main.TILE_SIZE_PX * (y + line * 1.1)));
                offset += char_width[ascii];
            } else {
                if (offset >= max_width*Main.TILE_SIZE){
                    i--;
                }
                offset = 0;
                line += 1;
            }
        }
    }

    public static double get_width(String str) {
        int offset = 0;
        int max_offset = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '\n') {
                int ascii = (int) str.charAt(i) - 32;
                offset += char_width[ascii];
            } else {
                offset = 0;
            }
            max_offset = Math.max(max_offset, offset);
        }
        return (double) max_offset / Main.TILE_SIZE;
    }
}
