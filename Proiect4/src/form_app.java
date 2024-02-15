import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;


public class form_app extends JFrame {
    private JButton compunereButton;
    private JButton descompunereButton;
    private JPanel appPanel;
    private JTextField imgR;
    private JTextField imgG;
    private JTextField imgB;
    private JTextField img;


    public form_app() {
        setTitle("Select Biti");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(appPanel);
        setMinimumSize(new Dimension(400,400));


        descompunereButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String image = img.getText().trim();
                if (image.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Introduceți un nume de imagine!");
                    return;
                }

                BufferedImage img = ImageUtil.loadImage("./test_images/" + image);

                img = ImageUtil.processImage(img);
                int x = img.getColorModel().getPixelSize();
                if (x == 8) {
                    JOptionPane.showMessageDialog(null, "Imaginea are 8 biți per pixel.");
                    Descompunere8(img);
                }
                else if(x == 24){
                    JOptionPane.showMessageDialog(null, "Imaginea are 24 de biți per pixel.");
                    Descompunere24(img);
                }
                    else
                        JOptionPane.showMessageDialog(null, "Imaginile nu respecta formatul!");
                }
        });

        compunereButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String imgRed = imgR.getText().trim();
                String imgGreen = imgG.getText().trim();
                String imgBlue = imgB.getText().trim();
                if (imgRed.isEmpty() || imgGreen.isEmpty() || imgBlue.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Introduceți cele trei imagini!");
                    return;
                }

                BufferedImage bandaR = ImageUtil.loadImage("./test_images/" + imgRed);
                BufferedImage bandaG = ImageUtil.loadImage("./test_images/" + imgGreen);
                BufferedImage bandaB = ImageUtil.loadImage("./test_images/" + imgBlue);

                int x = bandaR.getColorModel().getPixelSize();
                int y = bandaG.getColorModel().getPixelSize();
                int z = bandaB.getColorModel().getPixelSize();

                if(x==8 && y==8 && z==8) {
                    JOptionPane.showMessageDialog(null, "Imaginile au 8 biți per pixel.");
                    Compunere8(bandaR, bandaG, bandaB);
                }
                else if(x==24 && y==24 && z==24) {
                    JOptionPane.showMessageDialog(null, "Imaginile au 24 de biți per pixel.");
                    Compunere24(bandaR, bandaG, bandaB);
                }
                    else
                        JOptionPane.showMessageDialog(null, "Imaginile nu respecta formatul!");
            }
        });

        pack();
        setLocationRelativeTo(null);
    }


    private void Compunere8(BufferedImage bandaR, BufferedImage bandaG, BufferedImage bandaB) {
        BufferedImage combine = ImageUtil.combineBands8(bandaR, bandaG, bandaB);
        ImageUtil.saveImage(combine, "./images/descompunere8/Combine8.png", "png");

        ImageUtil.displayImage(bandaR, "Banda RED 8 biti");
        ImageUtil.displayImage(bandaG, "Banda GREEN 8 biti");
        ImageUtil.displayImage(bandaB, "Banda BLUE 8 biti");
        ImageUtil.displayImage(combine, "Combined Image 8");
    }


    private void Compunere24(BufferedImage bandaR, BufferedImage bandaG, BufferedImage bandaB) {
        BufferedImage combine = ImageUtil.combineBands24(bandaR, bandaG, bandaB);
        ImageUtil.saveImage(combine, "./images/descompunere24/Combine24.png", "png");

        ImageUtil.displayImage(bandaR, "Banda RED 24 biti");
        ImageUtil.displayImage(bandaG, "Banda GREEN 24 biti");
        ImageUtil.displayImage(bandaB, "Banda BLUE 24 biti");
        ImageUtil.displayImage(combine, "Combined Image 24");
    }

    private void Descompunere8(BufferedImage img) {
        int x = img.getColorModel().getPixelSize();

        if(x==8)
        {
            BufferedImage bandaR = ImageUtil.extractBand8(img, 'R');
            BufferedImage bandaG = ImageUtil.extractBand8(img, 'G');
            BufferedImage bandaB = ImageUtil.extractBand8(img, 'B');


            ImageUtil.saveImage(bandaR, "./images/descompunere8/BandaR8.png", "png");
            ImageUtil.saveImage(bandaG, "./images/descompunere8/BandaG8.png", "png");
            ImageUtil.saveImage(bandaB, "./images/descompunere8/BandaB8.png", "png");

            ImageUtil.displayImage(img, "Original image");
            ImageUtil.displayImage(bandaR, "Banda RED 8 biti");
            ImageUtil.displayImage(bandaG, "Banda GREEN 8 biti");
            ImageUtil.displayImage(bandaB, "Banda BLUE 8 biti");

        }else{
            JOptionPane.showMessageDialog(null, "Imaginea nu este de 8 de biti!");
        }
    }


    private void Descompunere24(BufferedImage img) {
        int x = img.getColorModel().getPixelSize();

        if(x==24)
        {
            BufferedImage bandaR = ImageUtil.extractBand24(img, 'R');
            BufferedImage bandaG = ImageUtil.extractBand24(img, 'G');
            BufferedImage bandaB = ImageUtil.extractBand24(img, 'B');


            ImageUtil.saveImage(bandaR, "./test_images/bandaR24.png", "png");
            ImageUtil.saveImage(bandaG, "./test_images/bandaG24.png", "png");
            ImageUtil.saveImage(bandaB, "./test_images/bandaB24.png", "png");

            ImageUtil.displayImage(img, "Original image");
            ImageUtil.displayImage(bandaR, "Banda RED 24 biti");
            ImageUtil.displayImage(bandaG, "Banda GREEN 24 biti");
            ImageUtil.displayImage(bandaB, "Banda BLUE 24 biti");

        }else{
            JOptionPane.showMessageDialog(null, "Imaginea nu este de 24 de biti!");
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new form_app().setVisible(true);
            }
        });
    }
}