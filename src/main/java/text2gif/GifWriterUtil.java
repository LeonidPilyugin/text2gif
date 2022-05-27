package text2gif;

import com.sun.istack.internal.NotNull;
import sun.awt.image.BufferedImageDevice;

import javax.imageio.IIOException;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.io.*;
import java.nio.file.Files;

/**
 * Provides methods for converting text to GIF image
 * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
 */
public class GifWriterUtil {
    private static final int BUFFER_SIZE = 1024;

    /**
     * Converts text from text file to GIF image
     * @param inputFileName path to input text file (encoding is utf-8)
     * @param outputFileName path to result GIF (if not exists, it will be created)
     * @throws IOException if an I/O error occurs
     * @throws InvalidPathException if arguments are not path strings
     * @throws SecurityException if a security manager exists and does not allow access to files
     * @throws IIOException if no gif ImageWriters are found
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    public static void writeFile(@NotNull String inputFileName, @NotNull String outputFileName) throws IOException,
            InvalidPathException, SecurityException, IIOException {
        // Create path of string
        Path file = FileSystems.getDefault().getPath(inputFileName);
        // Set charset
        Charset charset = StandardCharsets.UTF_8;
        // Create reader and writer
        try (BufferedReader reader = Files.newBufferedReader(file, charset);
            ImageOutputStream output = new FileImageOutputStream(new File(outputFileName));
            GifSequenceWriter writer =
                     new GifSequenceWriter(output, TextUtil.IMAGE_TYPE, 1000, false)) {
            // Convert
            processAll(reader, writer);
        }
    }

    /**
     * Converts String to GIF image
     * @param string String to convert
     * @param outputFileName path to result GIF (if not exists, it will be created)
     * @throws IOException if an I/O error occurs
     * @throws SecurityException if a security manager exists and does not allow access to files
     * @throws IIOException if no gif ImageWriters are found
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    public static void writeString(@NotNull String string, @NotNull String outputFileName) throws IOException,
            SecurityException, IIOException {
        // Create reader and writer
        try (StringReader input = new StringReader(string);
             BufferedReader reader = new BufferedReader(input);
             ImageOutputStream output = new FileImageOutputStream(new File(outputFileName));
             GifSequenceWriter writer =
                     new GifSequenceWriter(output, TextUtil.IMAGE_TYPE, 1000, false)) {
            // Convert
            processAll(reader, writer);
        }
    }

    /**
     * Converts text from reader to GIF image via writer
     * @param reader text reader object for input
     * @param writer GifSequenceWriter for output
     * @throws IOException if an I/O error occurs
     * @author Leonid Pilyugin (l.pilyugin04@gmail.com)
     */
    private static void processAll(BufferedReader reader, GifSequenceWriter writer) throws IOException {
        // Reading buffer
        char[] buffer = new char[BUFFER_SIZE];
        // Images to write
        BufferedImage[] images;
        // Read first chunk
        int read = reader.read(buffer, 0, BUFFER_SIZE);
        do {
            // Convert line to images array
            images = TextUtil.getImagesFromString(new String(buffer, 0, read));
            // Add images to output
            for (BufferedImage image : images) {
                writer.writeToSequence(image);
            }
        // Read new chunk
        } while((read = reader.read(buffer, 0, BUFFER_SIZE)) == BUFFER_SIZE);
    }
}
