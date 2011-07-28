/*
 * @(#)FocusedBorder.java  1.0  2011-07-26
 * 
 * Copyright (c) 2011 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package ch.randelshofer.quaqua.border;

import ch.randelshofer.quaqua.QuaquaManager;
import javax.swing.UIManager;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.awt.image.ConvolveOp;
import ch.randelshofer.quaqua.QuaquaUtilities;
import java.awt.Component;
import java.awt.Graphics;
import static java.lang.Math.*;

/**
 * {@code FocusedBorder}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-07-26 Created.
 */
public abstract class AbstractFocusedPainter {

    private final static ConvolveOp edgeLeftOp = new ConvolveOp(new Kernel(3, 1, new float[]{1, 0, -1}));
    private final static ConvolveOp edgeRightOp = new ConvolveOp(new Kernel(3, 1, new float[]{-1, 0, 1}));
    private final static ConvolveOp edgeTopOp = new ConvolveOp(new Kernel(1, 3, new float[]{1, 0, -1}));
    private final static ConvolveOp edgeBottomOp = new ConvolveOp(new Kernel(1, 3, new float[]{-1, 0, 1}));
    private static float[] gaussian;
//    private final static float[] gaussian = gaussian(2f, 2.5f , 0.9f);
    private static ConvolveOp gaussianVOp;
    private static ConvolveOp gaussianHOp;

    public AbstractFocusedPainter() {
        initFilters();
    }

    private static void initFilters() {
        float sum;
        if ("true".equals(QuaquaManager.getProperty("apple.awt.graphics.UseQuartz", "false"))) {
            sum = 0.9f;
        } else {
            sum = 0.6f;
        }
        gaussian = gaussian(2f, 3.5f, sum);
        gaussianVOp = new ConvolveOp(new Kernel(1, gaussian.length, gaussian));
        gaussianHOp = new ConvolveOp(new Kernel(gaussian.length, 1, gaussian));

    }

    protected void paint(Component c, Graphics cgx, int x, int y, int width, int height) {
        if (QuaquaUtilities.isFocused(c)) {

            Graphics2D cg = (Graphics2D) cgx;
            int slack = 2;
            BufferedImage borderImg = new BufferedImage(width + 2 * slack, height + 2 * slack, BufferedImage.TYPE_INT_ARGB_PRE);
            BufferedImage focusImg = new BufferedImage(width + 2 * slack, height + 2 * slack, BufferedImage.TYPE_INT_ARGB_PRE);
            Graphics2D bg = borderImg.createGraphics();
            Graphics2D fg = focusImg.createGraphics();

            // draw the border, once into the borderImg and once onto the component
            doPaint(c, bg, slack, slack, width, height);
            cg.drawImage(borderImg, x - slack, y - slack, c);

            // generate the focusImg from the borderImg
            fg.setComposite(AlphaComposite.SrcOver);
            fg.drawImage(borderImg, edgeLeftOp, 0, 0);
            fg.drawImage(borderImg, edgeRightOp, 0, 0);
            fg.drawImage(borderImg, edgeTopOp, 0, 0);
            fg.drawImage(borderImg, edgeBottomOp, 0, 0);
            fg.setComposite(AlphaComposite.SrcIn);
            fg.setColor(UIManager.getColor("Focus.color"));
            fg.fillRect(0, 0, width + 2 * slack, height + 2 * slack);

            // draw the focusImg blurred onto the component
            cg.drawImage(focusImg, gaussianHOp, x - slack, y - slack);
            cg.drawImage(focusImg, gaussianVOp, x - slack, y - slack);
            //   cg.drawImage(focusImg, x-slack, y-slack,c);

            bg.dispose();
            fg.dispose();
            // cg.drawImage(focusImg, x, y, c);
            /*
            cg.setComposite(AlphaComposite.SrcOver);
            cg.setColor(Color.MAGENTA);
            cg.drawRect(x, y, width-1, height-1);*/
        } else {
            doPaint(c, cgx, x, y, width, height);
        }
    }

    protected abstract void doPaint(Component c, Graphics cgx, int x, int y, int width, int height);

    /** Creates a gaussian kernel with the specified radius, sigma and sum. */
    private static float[] gaussian(float radius, float sigma, float sum) {
        int r = (int) Math.ceil(radius);
        float[] gaussian = new float[r * 2 + 1];

        // compute the gaussian
        float h = 1f; // height of the peak 
        float c = r; // position of the centre of the peak 
        float invs2sq = 1f / (2f * sigma * sigma);
        for (int i = 0; i < gaussian.length; i++) {
            float x = i;
            gaussian[i] = (float) (h * exp(-pow(x - c, 2) * invs2sq));
        }

        normalizeKernel(gaussian, sum);
        return gaussian;
    }

    /** Creates a pyramid kernel with the specified radius and sum. */
    private static float[] pyramid(float radius, float sum) {
        int r = (int) Math.ceil(radius);
        float[] pyramid = new float[r * 2 + 1];

        // compute the pyramid
        float c = r; // position of the centre of the peak 

        for (int i = 0; i < pyramid.length; i++) {
            float x = i;
            pyramid[i] = (float) c - abs(x - c);
        }

        normalizeKernel(pyramid, sum);
        return pyramid;
    }

    /** Normalizes the kernel so that all its elements add up to the given
     * sum. 
     * 
     * @param kernel
     * @param sum 
     */
    private static void normalizeKernel(float[] kernel, float sum) {
        float total = 0;
        for (int i = 0; i < kernel.length; i++) {
            total += kernel[i];
        }
        if (abs(total) > 1e-20) {
            total = sum / total;
            for (int i = 0; i < kernel.length; i++) {
                kernel[i] *= total;
            }
        }

    }
}
