/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jenes.tutorials.problem11;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

/**
 * An utility class that shows 1 or 2 images in a frame using {@link ImagePanel}
 * 
 */
public class ImageFrame extends JFrame {

    public ImageFrame(BufferedImage image) {
        this.add(new ImagePanel(image));
        
        this.setSize(300, 300);
        this.pack();
    }
    
    public ImageFrame(BufferedImage left, BufferedImage right) {
        this.setLayout(new GridLayout(1, 2));
        this.add(new ImagePanel(left));
        this.add(new ImagePanel(right));
        
        this.setSize(600, 300);
        this.pack();
    }
    
}
