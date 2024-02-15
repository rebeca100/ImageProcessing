import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtil {

    public static BufferedImage loadImage(String filename){
        BufferedImage img = null;

        try {
            img = ImageIO.read(new File(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return img;
    }

    public static void saveImage(BufferedImage img, String fileName, String formatName){
        try {
            ImageIO.write(img, formatName, new File(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void displayImage(BufferedImage img, String title) {
        if (img == null)
            return;

        BufferedImage resizedImage = resizeImage(img, 7, 7);

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImagePanel imagePanel = new ImagePanel();
        imagePanel.setFitToScreen(false);
        imagePanel.setImage(resizedImage);

        frame.setContentPane(imagePanel);
        frame.pack();
        frame.setVisible(true);
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int newWidthInCm, int newHeightInCm) {
        double scaleFactor = calculateScaleFactor(originalImage.getWidth(), originalImage.getHeight(), newWidthInCm, newHeightInCm);

        int scaledWidth = (int) (originalImage.getWidth() * scaleFactor);
        int scaledHeight = (int) (originalImage.getHeight() * scaleFactor);

        BufferedImage resizedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        return resizedImage;
    }

    public static double calculateScaleFactor(int originalWidth, int originalHeight, int newWidthInCm, int newHeightInCm) {
        double cmToPixelConversion = 37.79;
        double newWidthInPixels = newWidthInCm * cmToPixelConversion;
        double newHeightInPixels = newHeightInCm * cmToPixelConversion;

        double scaleFactorWidth = newWidthInPixels / originalWidth;
        double scaleFactorHeight = newHeightInPixels / originalHeight;

        return Math.min(scaleFactorWidth, scaleFactorHeight);
    }

    public static BufferedImage extractBand8(BufferedImage inImg, char band){
        BufferedImage outImg = new BufferedImage(inImg.getWidth(),inImg.getHeight(),BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < inImg.getHeight(); y++)
            for (int x = 0; x < inImg.getWidth(); x++) {
                int pixel = inImg.getRGB(x,y);

                int alpha = (pixel & 0xff000000) >> 24;
                int red =   (pixel & 0x00ff0000) >> 16;
                int green = (pixel & 0x0000ff00) >> 8;
                int blue =  (pixel & 0x000000ff);

                switch (band){
                    case 'A' -> outImg.getRaster().setSample(x,y,0,alpha);
                    case 'R' -> outImg.getRaster().setSample(x,y,0,red);
                    case 'G' -> outImg.getRaster().setSample(x,y,0,green);
                    case 'B' -> outImg.getRaster().setSample(x,y,0,blue);
                }
            }
        return outImg;
    }

    public static BufferedImage extractBand24(BufferedImage inImg, char band) {
        BufferedImage outImg = new BufferedImage(inImg.getWidth(), inImg.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < inImg.getHeight(); y++) {
            for (int x = 0; x < inImg.getWidth(); x++) {
                int pixel = inImg.getRGB(x, y);

                int red =   (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue =  pixel & 0xFF;

                int newPixel = 0;

                switch (band) {
                    case 'R' -> newPixel = (red << 16) | (0 << 8) | 0;
                    case 'G' -> newPixel = (0 << 16) | (green << 8) | 0;
                    case 'B' -> newPixel = (0 << 16) | (0 << 8) | blue;
                    default -> throw new IllegalArgumentException("Invalid band: " + band);
                }

                outImg.setRGB(x, y, newPixel);
            }
        }

        return outImg;
    }

    public static boolean areImagesRGB(BufferedImage redBand, BufferedImage greenBand, BufferedImage blueBand) {
        return (redBand.getType() == BufferedImage.TYPE_INT_RGB)
                && (greenBand.getType() == BufferedImage.TYPE_INT_RGB)
                && (blueBand.getType() == BufferedImage.TYPE_INT_RGB);
    }

    public static BufferedImage combineBands8(BufferedImage redBand, BufferedImage greenBand, BufferedImage blueBand) {
        int width = redBand.getWidth();
        int height = redBand.getHeight();

        BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int red = redBand.getRaster().getSample(x, y, 0);
                int green = greenBand.getRaster().getSample(x, y, 0);
                int blue = blueBand.getRaster().getSample(x, y, 0);

                int rgb = (red << 16) | (green << 8) | blue;
                combinedImage.setRGB(x, y, rgb);
            }
        }

        return combinedImage;
    }

    public static BufferedImage combineBands24(BufferedImage redBand, BufferedImage greenBand, BufferedImage blueBand) {
        int width = redBand.getWidth();
        int height = redBand.getHeight();

        BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int red = (redBand.getRGB(x, y) >> 16) & 0xFF;
                int green = (greenBand.getRGB(x, y) >> 8) & 0xFF;
                int blue = blueBand.getRGB(x, y) & 0xFF;

                int rgb = (red << 16) | (green << 8) | blue;
                combinedImage.setRGB(x, y, rgb);
            }
        }

        return combinedImage;
    }

    public static BufferedImage processImage(BufferedImage image) {
        if (image == null) {
            return image;
        }

        if (image.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_RGB) {
            return image;
        } else {
            return convertToRGB(image);
        }
    }

    public static BufferedImage convertToRGB(BufferedImage image) {
        BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = rgbImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return rgbImage;
    }

}
