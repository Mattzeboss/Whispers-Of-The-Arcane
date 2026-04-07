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
            BufferedImage character = Sprites.Font.getSubimage(8*(i%10),16*(i/10), 8, 16);
            while(!column_is_clear(character, char_width[i])){
                char_width[i]++;
            }
            char_width[i]++;
        }

        char_width[0] = 5; //space should be wide, even though it takes up no space
    }

    private static boolean column_is_clear(BufferedImage img, int col){
        for (int i = 0; i < img.getHeight(); i++) {
            if ((img.getRGB(col, i) >>> 24) != 0){ return false; }
        }
        return true;
    }


    public static void draw(Graphics2D g2D, String str, double x, double y, Color color){
        RescaleOp recolor = new RescaleOp(color.getComponents(null), new float[4], null);
        AffineTransformOp aff = new AffineTransformOp(AffineTransform.getScaleInstance(Main.PIXEL_SCALE, Main.PIXEL_SCALE), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        int offset = 0;
        for (int i = 0; i < str.length(); i++) {
            int ascii = (int)str.charAt(i) - 32;
            g2D.drawImage(recolor.filter(Sprites.Font.getSubimage(8*(ascii%10),16*(ascii/10), 8, 16), null), aff, (int) (Main.TILE_SIZE_PX*(x+1.0/16*offset)), (int) (Main.TILE_SIZE_PX*y));
            offset += char_width[ascii];
            //g2D.drawImage(Sprites.Font.getSubimage(8*(ascii%10),16*(ascii/10), 8, 16), (int) (Main.TILE_SIZE_PX*(x+0.5*i)), (int) (Main.TILE_SIZE_PX*y), (int) (Main.TILE_SIZE_PX/2.0), Main.TILE_SIZE_PX, null);
        }
    }
}
