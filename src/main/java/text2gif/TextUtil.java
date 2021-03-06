package text2gif;

import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import text2gif.annotations.NotNegative;
import text2gif.annotations.Positive;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Provides methods for converting string to array of images.
 * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
 */
public class TextUtil {
    /**
     * Type of images in gif.
     */
    public static final int IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB;

    /**
     * Creates array of letter images from String object.
     * @param string string, which will be converted to images
     * @return array of images, created from String
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    public static BufferedImage[] getImagesFromString(@NotNull String string) {
        return getImagesFromArray(string.toCharArray());
    }

    /**
     * Creates array of letter images from char array.
     * @param array char array, which will be converted to images
     * @return array of images, created from String
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    public static BufferedImage[] getImagesFromArray(@NotNull char[] array) {
        // Result array
        BufferedImage[] result = new BufferedImage[array.length];
        // Create image of every char
        for (int i = 0; i < array.length; i++) {
            result[i] = paintString(array[i]);
        }
        // Return result
        return result;
    }

    /**
     * Creates array of letter images from char array.
     * @param array char array, which will be converted to images
     * @param offset initial offset
     * @param count length
     * @return array of images, created from String
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    public static BufferedImage[] getImagesFromArray(@NotNull char[] array, @NotNegative int offset,
                                                     @Positive int count) throws ValueException {
        // Check values
        if (offset + count > array.length) {
            throw new ValueException("offset + count must be less or equal to array.length");
        }
        // Result array
        BufferedImage[] result = new BufferedImage[count];
        // Create image of every char
        for (int i = 0; i < count; i++) {
            result[i] = paintString(array[i + offset]);
        }
        // Return result
        return result;
    }

    /**
     * Creates BufferedImage of char.
     * @param ch char to paint
     * @return image of char
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    private static BufferedImage paintString(char ch) {
        // Convert char to String
        String s = "" + ch;
        // Create graphics object to paint (in this part we create it to get size of font)
        BufferedImage img = new BufferedImage(1, 1, IMAGE_TYPE);
        Graphics2D g2d = img.createGraphics();
        // Create font
        Font font = new Font("Monospace", Font.PLAIN, 48);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        // Get size of font
        int hewidth = fm.getHeight();
        g2d.dispose();
        // Create new image and graphics
        img = new BufferedImage(hewidth, hewidth, IMAGE_TYPE);
        g2d = img.createGraphics();
        // Set graphics hints
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        // Set font
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        // Fill background rectangle
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
        // Paint letter at center
        g2d.setColor(Color.BLACK);
        g2d.drawString(s, (img.getWidth() - fm.stringWidth(s)) / 2, ((img.getHeight() - fm.getHeight()) / 2) + fm.getAscent());
        g2d.dispose();
        // Return result
        return img;
    }
}
