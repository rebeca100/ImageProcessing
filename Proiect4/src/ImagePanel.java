import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel{

    private BufferedImage image = null;

    boolean aspectRatio = true;
    boolean fitToScreen = true;
    boolean centerImage = true;
    double scaleValue = 1.0;

    public ImagePanel() {
        super();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(image == null) return;

        if(fitToScreen) {
            if(aspectRatio) {

                double scaleW = (double)getWidth() / image.getWidth();
                double scaleH = (double)getHeight() / image.getHeight();
                scaleValue = Math.min(scaleW, scaleH);

                int w = (int)(scaleValue * image.getWidth());
                int h = (int)(scaleValue * image.getHeight());

                if(centerImage) {
                    g.drawImage(image, (getWidth()-w)/2, (getHeight()-h)/2, w, h , null);
                }
                else {
                    g.drawImage(image,0,0,(int)(scaleValue * image.getWidth()),
                            (int)(scaleValue * image.getHeight()),null);
                }

            }
            else {
                g.drawImage(image,0,0,getWidth(),getHeight(),null);
            }
        }
        else {
            g.drawImage(image,0,0,image.getWidth(),image.getHeight(),null);
        }
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    public void setFitToScreen(boolean fitToScreen) {
        this.fitToScreen = fitToScreen;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (image == null)
            return new Dimension(200, 200);
        if (fitToScreen)
            return new Dimension(0, 0);

        int width = (int) (scaleValue * image.getWidth());
        int height = (int) (scaleValue * image.getHeight());
        return new Dimension(width, height);
    }
}
