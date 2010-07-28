/*
 * @(#)QuaquaRootPaneBackground.java
 * 
 * Copyright (c) 2009-2010 Werner Randelshofer
 * Hausmatt 10, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 * 
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms.
 */
package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.color.PaintableColor;
import ch.randelshofer.quaqua.ext.batik.ext.awt.LinearGradientPaint;
import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;
import java.awt.geom.Point2D;
import javax.swing.UIManager;

/**
 * QuaquaRootPaneBackground.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class QuaquaRootPaneBackground extends PaintableColor {

    private Color[] activeColors;
    private Color[] inactiveColors;

    public QuaquaRootPaneBackground(int plainColor, int[] activeTextureColors, int[] inactiveTextureColors) {
        super(plainColor);
        this.activeColors = new Color[activeTextureColors.length];
        for (int i = 0; i < activeTextureColors.length; i++) {
            activeColors[i] = new Color(activeTextureColors[i]);
        }
        this.inactiveColors = new Color[inactiveTextureColors.length];
        for (int i = 0; i < inactiveTextureColors.length; i++) {
            inactiveColors[i] = new Color(inactiveTextureColors[i]);
        }
    }

    public QuaquaRootPaneBackground(Color plainColor, Color[] activeTextureColors, Color[] inactiveTextureColors) {
        //super(0xa7a7a7);
        super(plainColor.getRGB());
        this.activeColors = activeTextureColors;
        this.inactiveColors = inactiveTextureColors;
    }

    public Paint getPaint(Component c, int x, int y, int width, int height) {
        boolean isTextured = QuaquaUtilities.isOnTexturedWindow(c);

        if (isTextured) {
            int rootOffset = 0;
            int rootHeight = 0;
            for (Component p = c; p != null && p.getParent() != null; p = p.getParent()) {
                rootOffset -= p.getY();
                rootHeight = p.getHeight();
            }

            rootHeight = Math.max(rootHeight, 1 + 80 + 40 + 1);

            return new LinearGradientPaint(new Point2D.Float(x, rootOffset), new Point2D.Float(x, rootOffset + rootHeight),
                    new float[]{
                        0f,
                        1f / rootHeight,
                        81f / rootHeight,
                        (rootHeight - 41f) / rootHeight,
                        (rootHeight - 1f) / rootHeight
                    },
                    QuaquaUtilities.isOnActiveWindow(c) ? activeColors : inactiveColors);
        } else {
            return UIManager.getColor("Panel.background");
        }
    }
}
