/*
 * @(#)ImageBevelBorder13.java  1.0.1 2005-10-15
 *
 * Copyright (c) 2001-2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua.border;

import ch.randelshofer.quaqua.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.image.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.*;

/**
 * A fast, but somewhat inacurate version of class ImageBevelBorder14 optimized
 * for Apples MRJ for Java 1.3.
 *
 * Draws a filled bevel border using an image and insets.
 * The image must consist of a bevel and a fill area.
 * <p>
 * The insets and the size of the image are
 * used do determine which parts of the image shall be
 * used to draw the corners and edges of the bevel as
 * well the fill area.
 *
 * <p>For example, if you provide an image of size 10,10
 * and a insets of size 2, 2, 4, 4, then the corners of
 * the border are made up of top left: 2,2, top right: 2,4,
 * bottom left: 2,4, bottom right: 4,4 rectangle of the image.
 * The inner area of the image is used to fill the inner area.
 *
 * @author  Werner Randelshofer
 * @version 1.0.1 2005-10-15 Convert image to buffered image when painting
 * (instead of in the constructor).
 * <br>1.0 2005-04-25 Refactored from class ImageBevelBorder14.
 */
public class ImageBevelBorder13 implements Border, UIResource {
    private final static boolean VERBOSE = false;
    /**
     * The image to be used for drawing.
     */
    private Image image;
    
    /**
     * The border insets
     */
    private Insets borderInsets;
    /**
     * The insets of the image.
     */
    private Insets imageInsets;
    
    /**
     * This attribute is set to true, when the image
     * is used to fill the content area too.
     */
    private boolean fillContentArea;
    
    /**
     * Creates a new instance with the given image and insets.
     * The image has the same insets as the border.
     */
    public ImageBevelBorder13(Image img, Insets borderInsets) {
        this(img, borderInsets, borderInsets, true);
    }
    
    /**
     * Creates a new instance with the given image and insets.
     * The image has different insets than the border.
     */
    public ImageBevelBorder13(Image img, Insets imageInsets, Insets borderInsets) {
        this(img, imageInsets, borderInsets, true);
    }
    /**
     * Creates a new instance with the given image and insets.
     * The image has different insets than the border.
     */
    public ImageBevelBorder13(Image img, Insets imageInsets, Insets borderInsets, boolean fillContentArea) {
        this.image = img;
        this.imageInsets = imageInsets;
        this.borderInsets = borderInsets;
        this.fillContentArea = fillContentArea;
    }
    
    /**
     * Returns true if the border is opaque.
     * This implementation always returns false.
     */
    public boolean isBorderOpaque() {
        return false;
    }
    
    /**
     * Returns the insets of the border.
     * @param c the component for which this border insets value applies
     */
    public Insets getBorderInsets(Component c) {
        return (Insets) borderInsets.clone();
    }
    
    
    /**
     * Paints the bevel image for the specified component with the
     * specified position and size.
     * @param c the component for which this border is being painted
     * @param gr the paint graphics
     * @param x the x position of the painted border
     * @param y the y position of the painted border
     * @param width the width of the painted border
     * @param height the height of the painted border
     */
    public void paintBorder(Component c, Graphics gr, int x, int y, int width, int height) {
        if (this.image == null) return;
        
        BufferedImage image = Images.toBufferedImage(this.image);
        
        if (! gr.getClipBounds().intersects(x, y, width, height)) {
            return;
        }
        
        // Cast Graphics to Graphics2D
        Graphics2D g = (Graphics2D) gr;
        
        // Set some variables for easy access of insets and image size
        int top = imageInsets.top;
        int left = imageInsets.left;
        int bottom = imageInsets.bottom;
        int right = imageInsets.right;
        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();
        
        
        // Optimisation: Draw image directly if it fits into the component
        if (fillContentArea) {
            if (width == imgWidth && height == imgHeight) {
                g.drawImage(image, x, y, c);
                return;
            }
        }
        
        // Optimisation: Remove insets, if image width or image height fits
        if (width == imgWidth) {
            left = imgWidth;
            right = 0;
        }
        if (height == imgHeight) {
            top = imgHeight;
            bottom = 0;
        }
        
        // Adjust insets if component is too small
        if (width < left + right) {
            left = Math.min(left, width / 2); //Math.max(0, left + (width - left - right) / 2);
            right = width - left;
        }
        if (height < top + bottom) {
            top = Math.min(top, height / 2); //Math.max(0, top + (height - top - bottom) / 2);
            bottom = height - top;
        }
        
        // Draw the Corners
        if (top > 0 && left > 0) {
            g.drawImage(
            image,
            x, y, x + left, y + top,
            0, 0, left, top,
            c
            );
        }
        if (top > 0 && right > 0) {
            //g.fillRect(x+width-right, y, x+width, y+top);
            g.drawImage(
            image,
            x + width - right, y, x + width, y + top,
            imgWidth - right, 0, imgWidth, top,
            c
            );
        }
        if (bottom > 0 && left > 0) {
            g.drawImage(
            image,
            x, y + height - bottom, x + left, y + height,
            0, imgHeight - bottom, left, imgHeight,
            c
            );
        }
        if (bottom > 0 && right > 0) {
            g.drawImage(
            image,
            x + width - right, y + height - bottom, x + width, y + height,
            imgWidth - right, imgHeight - bottom, imgWidth, imgHeight,
            c
            );
        }
        
        // Draw the edges
        // Note: We stretch the images to fill the edges. Strangely with Apple's
        // Java 1.3 VM this is by factor 10 faster then creating a Paint object 
        // and filling it using a replication fill. Also note, that the 
        // Graphics2D objects interpolates the pixels using a bilinear algorithm.
        // Setting the rendering hints has no effect, so we just live with it.
        BufferedImage subImg = null;
        
        // North
        if (top > 0 && left + right < width) {
            subImg = image.getSubimage(left, 0, imgWidth - right - left, top);
            g.drawImage(subImg, x+left, y, width - left - right, top, c);
        }
        // South
        if (bottom > 0 && left + right < width) {
            subImg = image.getSubimage(left, imgHeight - bottom, imgWidth - right - left, bottom);
            g.drawImage(subImg, x+left, y + height - bottom, width - left - right, bottom, c);
        }
        // West
        if (left > 0 && top + bottom < height) {
            subImg = image.getSubimage(0, top, left, imgHeight - top - bottom);
            g.drawImage(subImg, x, y+top, left, height - top - bottom, c);
        }
        // East
        if (right > 0 && top + bottom < height) {
            subImg = image.getSubimage(imgWidth - right, top, right, imgHeight - top - bottom);
            g.drawImage(subImg, x+width-right, y + top, right, height - top - bottom, c);
        }
        
        // Fill the center
        // Note: We extract a pixel at the top left corner of the content area 
        // and use it for painting. This isn't very accurate, but speeds up
        // drawing a lot for Java 1.3 quite considerably.
        if (fillContentArea) {
            if (left + right < width && top + bottom < height) {
                g.setColor(new Color(image.getRGB(left, top), true));
                g.fillRect(x+left, y + top, width - right - left, height - top - bottom);
            }
        }
    }
}
