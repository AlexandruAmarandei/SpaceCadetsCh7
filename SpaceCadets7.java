package spacecadets7;

import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

/**
 *
 * @author alexa
 */
public class SpaceCadets7 {

    private final int RADIUSMAXIMUM = 60;
    private BufferedImage inputImage = null, outputImage = null;
    private int height, witdh, maxRadius, minRadius;
    private short circles[][][];
    private double cosValues[], sinValues[];
    private int accuracy;
    private final int[][] sobelX = {
        {-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}};

    private final int[][] sobelY = {
        {-1, -2, -1},
        {0, 0, 0},
        {1, 2, 1}};
    public int pixels[][], pixelsContrast[][];

    public void loadImage() {
        height = inputImage.getHeight();
        witdh = inputImage.getWidth();
        pixels = new int[witdh][height];
        pixelsContrast = new int[witdh][height];
        circles = new short[witdh][height][maxRadius];

    }

    public void convertToGreyScale() {
        for (int i = 0; i < witdh; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = inputImage.getRGB(i, j);
                // then we get the rgb value
                //pixels[i][j] = rgb;
                int redValue = (rgb >> 16) & 0xff;
                int greenValue = (rgb >> 8) & 0xff;
                int blueValue = rgb & 0xff;
                int avarage = (redValue + greenValue + blueValue) / 3;
                int grey = (avarage << 16) + (avarage << 8) + avarage;
                pixels[i][j] = avarage;
                inputImage.setRGB(i, j, grey);

            }
        }

    }

    public void applySobel(int contrast) {
        int pixelX, pixelY;
        for (int i = 1; i < witdh - 2; i++) {
            for (int j = 1; j < height - 2; j++) {
                pixelX = 0;
                pixelY = 0;
                for (int si = 0; si < 3; si++) {
                    for (int sj = 0; sj < 3; sj++) {
                        pixelX += sobelX[si][sj] * pixels[i - 1 + sj][j - 1 + si];
                    }
                }
                for (int si = 0; si < 3; si++) {
                    for (int sj = 0; sj < 3; sj++) {
                        pixelY += sobelY[si][sj] * pixels[i - 1 + sj][j - 1 + si];
                    }
                }

                int valueToSet = (int) Math.sqrt((pixelX * pixelX) + (pixelY * pixelY));
                //System.out.println(pixelX + " "+ pixelY);
                if (valueToSet < contrast) {
                    valueToSet = 0;

                }

                if (valueToSet >= contrast) {
                    valueToSet = 255;

                }
                pixelsContrast[i][j] = valueToSet;
                valueToSet = (valueToSet << 16) + (valueToSet << 8) + valueToSet;

                inputImage.setRGB(i, j, valueToSet);

            }

        }
        for (int i = 0; i < witdh; i++) {
            System.arraycopy(pixelsContrast[i], 0, pixels[i], 0, height);
        }
        printImage(inputImage, "C:\\Users\\alexa\\Desktop\\ImageSobel.jpg");
    }

    public void findCircles() {
        int a, b;
        for (int i = 0; i < witdh; i++) {
            for (int j = 0; j < height; j++) {
                if (pixelsContrast[i][j] > 0) {
                    for (int radius = 3; radius < maxRadius; radius++) {
                        // System.out.println(radius);
                        for (int theta = 0; theta < 360; theta++) {

                            a = (int) (i - radius * cosValues[theta]);
                            b = (int) (j - radius * sinValues[theta]);
                            if (inBounds(a, b)) {
                                circles[a][b][radius]++;
                            }
                        }
                    }
                }

            }
            System.out.println(i);
        }
    }

    public void findBiggestCircle(int accuracy) {
        int xMax, yMax, rMax, valueMax;
        xMax = yMax = rMax = -1;
        for (int i = 0; i < witdh; i++) {
            for (int j = 0; j < height; j++) {
                for (int r = minRadius; r < maxRadius; r++) {
                    //System.out.println(i+ " " +j+  " " +r+ " "+ circles[i][j][r]);
                    if (circles[i][j][r] > accuracy) {

                        valueMax = circles[i][j][r];
                        xMax = i;
                        yMax = j;
                        rMax = r;
                        System.out.println(xMax + " " + yMax + " " + rMax + " " + valueMax);
                        drawCircle(i, j, r);
                    }
                }
            }
        }

    }

    public void findBiggestCircle() {
        int xMax, yMax, rMax, valueMax;
        xMax = yMax = rMax = -1;
        for (int i = 0; i < witdh; i++) {
            for (int j = 0; j < height; j++) {
                for (int r = minRadius; r < maxRadius; r++) {
                    //System.out.println(i+ " " +j+  " " +r+ " "+ circles[i][j][r]);
                    if (circles[i][j][r] > accuracy) {

                        valueMax = circles[i][j][r];
                        xMax = i;
                        yMax = j;
                        rMax = r;
                        System.out.println(xMax + " " + yMax + " " + rMax + " " + valueMax);
                        drawCircle(i, j, r);
                    }
                }
            }
        }

    }

    public void drawCircle(int x, int y, int radius) {
        int circleX, circleY;
        for (int t = 0; t < 360; t++) {
            circleX = (int) (radius * cosValues[t]);
            circleY = (int) (radius * sinValues[t]);
            if (x + circleX > 0 && x + circleX < witdh && y + circleY > 0 && y + circleY < height) {
                outputImage.setRGB(x + circleX, y + circleY, 255);
            }
        }

    }

    public void initialization() {
        cosValues = new double[360];
        sinValues = new double[360];
        for (int i = 0; i < 360; i++) {
            cosValues[i] = Math.cos(i * Math.PI / 180);
        }

        for (int i = 0; i < 360; i++) {
            sinValues[i] = Math.sin(i * Math.PI / 180);
        }
    }

    public void sortCircles() {

    }

    public boolean inBounds(int x, int y) {
        return x > 0 && x < witdh && y > 0 && y < height;
    }

    public void destroyNonCirclePixels(int x, int y) {
        pixels[x][y] = 255;
        int black = 0;
        if (inBounds(x - 1, y) && pixels[x - 1][y] == 0) {
            black++;
        }
        if (inBounds(x + 1, y) && pixels[x + 1][y] == 0) {
            black++;
        }
        if (inBounds(x, y - 1) && pixels[x][y - 1] == 0) {
            black++;
        }
        if (inBounds(x, y + 1) && pixels[x][y + 1] == 0) {
            black++;
        }

        if (black >= 3) {
            pixels[x][y] = 0;
            inputImage.setRGB(x, y, 0);

            if (inBounds(x - 1, y) && pixels[x - 1][y] != 0) {
                destroyNonCirclePixels(x - 1, y);
            }
            if (inBounds(x + 1, y) && pixels[x + 1][y] != 0) {
                destroyNonCirclePixels(x + 1, y);
            }
            if (inBounds(x, y - 1) && pixels[x][y - 1] != 0) {
                destroyNonCirclePixels(x, y - 1);
            }
            if (inBounds(x, y + 1) && pixels[x][y + 1] != 0) {
                destroyNonCirclePixels(x, y + 1);
            }
        }

    }

    public void fillWithBalck(int x, int y) {
        int black = 0;
        if (inBounds(x - 1, y) && pixels[x - 1][y] == 0) {
            black++;
        }
        if (inBounds(x + 1, y) && pixels[x + 1][y] == 0) {
            black++;
        }
        if (inBounds(x, y - 1) && pixels[x][y - 1] == 0) {
            black++;
        }
        if (inBounds(x, y + 1) && pixels[x][y + 1] == 0) {
            black++;
        }
        if (black == 0) {
            pixels[x][y] = 1;
            inputImage.setRGB(x, y, 0);
        }
    }

    public void fillWithWhite(int x, int y) {
        int black = 0;
        int white = (255 << 16) + (255 << 8) + 255;
        if (inBounds(x - 1, y)) {
            pixels[x - 1][y] = 250;
            inputImage.setRGB(x - 1, y, white);
        }
        if (inBounds(x + 1, y)) {
            pixels[x + 1][y] = 250;
            inputImage.setRGB(x + 1, y, white);
        }
        if (inBounds(x, y - 1)) {
            pixels[x][y - 1] = 250;
            inputImage.setRGB(x, y - 1, white);
        }
        if (inBounds(x, y + 1)) {
            pixels[x][y + 1] = 250;
            inputImage.setRGB(x, y + 1, white);
        }

    }

    public void findNonCirlePixels() {
        for (int i = 0; i < witdh; i++) {
            for (int j = 0; j < height; j++) {
                if (pixels[i][j] == 255) {
                    pixels[i][j] = 254;
                }
            }
        }

        for (int i = 0; i < witdh; i++) {
            for (int j = 0; j < height; j++) {
                if (pixels[i][j] == 254) {
                    destroyNonCirclePixels(i, j);
                }
            }
        }

        for (int i = 0; i < witdh; i++) {
            for (int j = 0; j < height; j++) {
                if (pixels[i][j] == 255) {
                    fillWithBalck(i, j);
                }
            }
        }

        for (int i = 0; i < witdh; i++) {
            for (int j = 0; j < height; j++) {
                if (pixels[i][j] == 255) {
                    //fillWithWhite(i, j);
                }
            }
        }

        for (int i = 0; i < witdh; i++) {
            for (int j = 0; j < height; j++) {
                if (pixels[i][j] == 1) {
                    pixels[i][j] = 0;
                }
                if (pixels[i][j] == 250) {
                    pixels[i][j] = 255;
                }
                pixelsContrast[i][j] = pixels[i][j];
            }
        }
    }

    public void main(String[] args) {
        initialization();
        loadImage();
        convertToGreyScale();

        applySobel(75);
        findNonCirlePixels();
        printImage(inputImage, "C:\\Users\\alexa\\Desktop\\ImageAfterPixles.jpg");
        findCircles();
        findBiggestCircle();
        printImage(outputImage, "C:\\Users\\alexa\\Desktop\\ImageFinal.jpg");
    }

    public void printImage(BufferedImage imageToPrint, String path) {
        try {
            File output = new File(path);
            ImageIO.write(imageToPrint, "jpg", output);
        } catch (IOException e) {
            System.out.println("Error in writting image");
        }
    }

    public SpaceCadets7(BufferedImage input, BufferedImage output, int acc, int min, int max) {
        inputImage = input;
        outputImage = output;
        if (accuracy != -10000) {
            accuracy = acc;
        } else {
            accuracy = 300;
        }
        int temp = Math.min(inputImage.getWidth() / 2 + 1, inputImage.getHeight() / 2 + 1);
        if (max != -10000) {
            maxRadius = max;
        } else {
            maxRadius = temp;
        }
        if (max > temp) {
            maxRadius = temp;
        }
        System.out.println(maxRadius);

        if (min != -10000) {
            minRadius = min;
        } else {
            minRadius = 3;
        }

        runSequence();
    }

    public final void runSequence() {
        initialization();
        loadImage();
        convertToGreyScale();
        applySobel(75);
        /*
        findCircles();
        findBiggestCircle();
         */

    }

    public BufferedImage getImage() {
        return outputImage;
    }

    public void findCirclesByRow(int i) {
        int a, b;
        for (int j = 0; j < height; j++) {
            if (pixelsContrast[i][j] > 0) {
                for (int radius = 3; radius < maxRadius; radius++) {
                    // System.out.println(radius);
                    for (int theta = 0; theta < 360; theta++) {

                        a = (int) (i - radius * cosValues[theta]);
                        b = (int) (j - radius * sinValues[theta]);
                        if (inBounds(a, b)) {
                            circles[a][b][radius]++;
                        }
                    }
                }
            }

        }
        System.out.println(i);
    }

}
