package spacecadets7;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GUI implements Runnable{

    private JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));
    private boolean imageInUse = false;
    private BufferedImage inputImage = null, outputImage = null;
    private int accuracy, minRadius, maxRadius, accuracy2;
    private SpaceCadets7 detector;
    private JLabel image;
    private Thread thread = null;
    private JProgressBar bar = null;

    public GUI() {
        accuracy = minRadius = maxRadius = -10000;
        JFrame frame = new JFrame();
        JMenuBar menuBar = new JMenuBar();
        JButton openButton = new JButton("Open");
        JButton runButton = new JButton("Run");
        JButton fillBlack = new JButton("Faster");
        JButton getCircles = new JButton("Get");
        JButton saveOutputButton = new JButton("SaveImage");
        
        JTextField textBox1 = new JTextField("Accuracy: ");

        JTextField textBox2 = new JTextField("Radius");
        JTextArea input1 = new JTextArea(1, 4);
        JTextArea input2 = new JTextArea(1, 20);
        input1.setLineWrap(false);
        input2.setLineWrap(false);
        menuBar.add(runButton);
        menuBar.add(openButton);
        menuBar.add(saveOutputButton);
        menuBar.add(fillBlack);
        menuBar.add(getCircles);
        menuBar.add(textBox1);
        menuBar.add(input1);
        menuBar.add(textBox2);
        menuBar.add(input2);
        //menuBar.add(bar);
        frame.setJMenuBar(menuBar);

        //JPanel imagePannel = new JPanel(new BorderLayout());
        //frame.add(imagePannel);
        
        openButton.addActionListener((ActionEvent arg0) -> {
            if (dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                String fileName = dialog.getSelectedFile().getAbsolutePath();
                try {

                    inputImage = ImageIO.read(new File(fileName));
                    outputImage = ImageIO.read(new File(fileName));
                    image = new JLabel(new ImageIcon(fileName));
                    frame.add(image);
                    frame.pack();
                    imageInUse = true;
                    String in = input1.getText();
                    if (!"".equals(in)) {
                        accuracy = Integer.parseInt(in);
                    }
                    in = input2.getText();
                    if (!"".equals(in)) {
                        minRadius = Integer.parseInt(in.substring(0, in.indexOf(' ')));
                        maxRadius = Integer.parseInt(in.substring(in.indexOf(' ')));
                    }

                    detector = new SpaceCadets7(inputImage, outputImage, accuracy, minRadius, maxRadius);

                } catch (IOException e) {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "Editor can't find the file called " + fileName);
                }
            }
        });

        runButton.addActionListener((ActionEvent arg0) -> {
            if (imageInUse == true) {
                bar = new JProgressBar(0,inputImage.getWidth());
                frame.add(bar, BorderLayout.SOUTH);
                thread = new Thread(this);
                
                thread.start();
                
                
                image.removeAll();

                frame.remove(image);
                image = new JLabel(new ImageIcon(detector.getImage()));
                frame.add(image);
                frame.pack();

            }
        });

        fillBlack.addActionListener((ActionEvent arg0) -> {
            if (imageInUse == true && detector != null) {
                detector.findNonCirlePixels();
            }
        });

        getCircles.addActionListener((ActionEvent arg0) -> {
            if (imageInUse == true && detector != null) {

                String in = input1.getText();
                if (!"".equals(in)) {
                    accuracy2 = Integer.parseInt(in);
                    detector.findBiggestCircle(accuracy2);
                } else {
                    detector.findBiggestCircle(300);
                }

            }
        });

        saveOutputButton.addActionListener((ActionEvent arg0) -> {
            if (imageInUse == true) {
                if (dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    String fileName = dialog.getSelectedFile().getAbsolutePath();
                    try {
                        File output = new File(fileName);
                        ImageIO.write(inputImage, fileName.substring(fileName.lastIndexOf('.')), output);
                    } catch (IOException e) {
                        System.out.println("Error in writting image");
                    }

                }
            }
        });

        frame.setTitle("CircleFinder");
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        GUI gui = new GUI();

    }
    
    @Override
    public void run(){
        int width = inputImage.getWidth();
                for (int i = 0; i < width; i++) {
                    detector.findCirclesByRow(i);
                    bar.setValue(i );
                    bar.setStringPainted(true);
                }
                
    }
}
