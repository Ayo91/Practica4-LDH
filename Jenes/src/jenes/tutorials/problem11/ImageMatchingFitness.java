/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jenes.tutorials.problem11;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import jenes.chromosome.BitwiseChromosome;
import jenes.population.Fitness;
import jenes.population.Individual;

/**
 * This class represent the fitness function used to match orientation of a sample image. 
 * The algorithm used is inspired to Java Image Processing Cookbook by Rafael Santos 
 * (see <a href="http://www.lac.inpe.br/JIPCookbook/">Web site</a>)
 * 
 */
public class ImageMatchingFitness extends Fitness<BitwiseChromosome> {

    /** image to load */
    public static final String SOURCE_IMAGE = "files.Tutorial11/dog.jpg";
    
    /** target rotation for the sample image */
    private int target_degrees = 0;
    /** the target to match */
    private BufferedImage target;
    /** the source image (without trasformations) */
    private BufferedImage source;
    /**
     * The reference image "signature" (25 representative pixels, each in R,G,B).
     * We use instances of Color to make things simpler.
     */
    private Color[][] signature;
    /** The base size of the images. */
    private int baseSize;
    /** The size of sample area */
    private int sampleAreaSize = 10;
    /** The number of samples per rows */
    private int nSamples = 5;
    /** Default rotation anchor point */
    private int rotateAnchorX;
    private int rotateAnchorY;

    /**
     * Default constructor
     */
    public ImageMatchingFitness(int targetDegree) {
        /* 1 objective -> minimize distance between image sample from the target */
        super(false);

        this.target_degrees = targetDegree;
        try {
            //load the target image
            InputStream inputStream = new FileInputStream(SOURCE_IMAGE);
            this.source = ImageIO.read(inputStream);
            inputStream.close();

            //choose base size for algorithm as the minimum dimension of the image... 
            this.baseSize = Math.min(this.source.getWidth(), this.source.getHeight());
            this.rotateAnchorX = this.source.getWidth() / 2;
            this.rotateAnchorY = this.source.getHeight() / 2;

            //create the sample image with a rotation... the algorithm must compensate this rotation
            AffineTransform affine = new AffineTransform();
            affine.setToRotation(toRadiansForRotation(target_degrees), 64, 64);

            BufferedImage tmp = new BufferedImage(this.source.getWidth(), this.source.getHeight(), this.source.getType());
            Graphics2D graphics = (Graphics2D) tmp.getGraphics();
            graphics.setTransform(affine);
            graphics.drawImage(this.source, affine, null);

            this.target = tmp;
            this.signature = this.calcSignature(this.target, false);
            
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected Fitness<BitwiseChromosome> duplicate() throws CloneNotSupportedException {
        return new ImageMatchingFitness(this.target_degrees);
    }

    /**
     * Utility to trasform degrees to radians in problem
     * @param degrees
     * @return 
     */
    private static double toRadiansForRotation(double degrees) {
        return Math.PI * degrees / 180 / 2;
    }

    /**
     * Print a graphical representation of the results
     * @param rotation
     * @param showSignature 
     * @throws Exception 
     */
    public void printGraphics(double rotation, boolean showSignature) {
        
        BufferedImage tmp = new BufferedImage(this.source.getHeight(), this.source.getHeight(), this.source.getType());
        BufferedImage tmp2 = new BufferedImage(this.source.getHeight(), this.source.getHeight(), this.source.getType());

        Graphics2D graphics = (Graphics2D) tmp.getGraphics();
        Graphics2D graphics2 = (Graphics2D) tmp2.getGraphics();
        
        AffineTransform affine = new AffineTransform();
        affine.setToRotation(toRadiansForRotation(this.target_degrees), this.rotateAnchorX, this.rotateAnchorY);
        graphics.drawImage(this.source, affine, null);
        
        AffineTransform affine2 = new AffineTransform();
        affine2.setToRotation(toRadiansForRotation(rotation), this.rotateAnchorX, this.rotateAnchorY);
        graphics2.setTransform(affine2);
        graphics2.drawImage(this.source, affine2, null);

        graphics.dispose();
        graphics2.dispose();
        
        if( showSignature ) {
            this.calcSignature(tmp, true);
            this.calcSignature(tmp2, true);
        }
        
        ImageFrame frame = new ImageFrame(tmp, tmp2);
        frame.setVisible(true);
    }

    @Override
    public void evaluate(Individual<BitwiseChromosome> individual) {
        BitwiseChromosome chromosome = individual.getChromosome();

        int tau = (Integer) chromosome.getValueAt(0);

        double x = rotateAnchorX;
        double y = rotateAnchorY;
        double t = tau % 360;

        //applicare la trasformazione
        AffineTransform affine = new AffineTransform();
        affine.setToRotation(toRadiansForRotation(t), x, y);

        BufferedImage image = new BufferedImage(this.target.getWidth(),
                this.target.getHeight(), this.target.getType());

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setTransform(affine);
        graphics.drawImage(this.source, affine, null);

        //evaluate distance from image and target
        double distance = this.calcDistance(image);
        individual.setScore(distance);

        graphics.dispose();
    }

    /*
     * This method calculates and returns signature vectors for the input image.
     */
    private Color[][] calcSignature(BufferedImage i, boolean showSignature) {
        int n = this.nSamples;
        // Get memory for the signature.
        Color[][] sig = new Color[n][n];
        // For each of the 25 signature values average the pixels around it.
        // Note that the coordinate of the central pixel is in proportions.
        float[] prop = new float[]{1f / 10f, 3f / 10f, 5f / 10f, 7f / 10f, 9f / 10f};
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                sig[x][y] = this.averageAround(i, prop[x], prop[y], showSignature);
            }
        }
        return sig;
    }

    /**
     * This method averages the pixel values around a central point and return the
     * average as an instance of Color. The point coordinates are proportional to
     * the image.
     */
    private Color averageAround(BufferedImage i, double px, double py, boolean showSignature) {

        // Get memory for a pixel and for the accumulator.
        double[] pixel = new double[3];
        double[] accum = new double[3];
        // The size of the sampling area.
        int sampleSize = this.sampleAreaSize;
        int numPixels = 0;
        // Sample the pixels.
        Raster raster = i.getData();
        for (double x = px * baseSize - sampleSize; x < px * baseSize + sampleSize; x++) {
            for (double y = py * baseSize - sampleSize; y < py * baseSize + sampleSize; y++) {
                raster.getPixel((int) x, (int) y, pixel);
                accum[0] += pixel[0];
                accum[1] += pixel[1];
                accum[2] += pixel[2];

                numPixels++;
                // only for debug mode... print on the surface of the image all the sample area painted with orange color
                if (showSignature) {
                    i.setRGB((int) x, (int) y, 0xFF6600);
                }
            }
        }

        // Average the accumulated values.
        accum[0] /= numPixels;
        accum[1] /= numPixels;
        accum[2] /= numPixels;

        // only for debug mode... print on the image the areas evaluated as sample for distance evaluation
        if (showSignature) {
            for (double x = px * baseSize - sampleSize; x < px * baseSize + sampleSize; x++) {
                for (double y = py * baseSize - sampleSize; y < py * baseSize + sampleSize; y++) {
                    i.setRGB((int) x, (int) y, new Color((int) accum[0], (int) accum[1], (int) accum[2]).getRGB());
                }
            }
        }

        return new Color((int) accum[0], (int) accum[1], (int) accum[2]);
    }

    /**
     * This method calculates the distance between the signatures of an image and
     * the reference one. The signatures for the image passed as the parameter are
     * calculated inside the method.
     */
    private double calcDistance(BufferedImage other) {
        // Calculate the signature for that image.
        Color[][] sigOther = calcSignature(other, false);
        // There are several ways to calculate distances between two vectors,
        // we will calculate the sum of the distances between the RGB values of
        // pixels in the same positions.
        double dist = 0;
        int n = this.nSamples;
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                int r1 = signature[x][y].getRed();
                int g1 = signature[x][y].getGreen();
                int b1 = signature[x][y].getBlue();

                int r2 = sigOther[x][y].getRed();
                int g2 = sigOther[x][y].getGreen();
                int b2 = sigOther[x][y].getBlue();

                double tempDist = Math.sqrt(
                        (r1 - r2) * (r1 - r2)
                        + (g1 - g2) * (g1 - g2)
                        + (b1 - b2) * (b1 - b2));
                dist += tempDist;
            }
        }
        return dist;
    }
}
