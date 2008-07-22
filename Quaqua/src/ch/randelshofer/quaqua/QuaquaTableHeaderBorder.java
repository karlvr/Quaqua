/*
 * @(#)QuaquaTableHeaderBorder.java  1.0  December 8, 2005
 *
 * Copyright (c) 2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.util.*;
import ch.randelshofer.quaqua.BackgroundBorder;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
//import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

/**
 * QuaquaTableHeaderBorder.
 *
 * @author  Werner Randelshofer
 * @version 1.0 December 8, 2005 Created.
 */
public class QuaquaTableHeaderBorder implements BackgroundBorder {
    /** Location of the border images. */
    private String imagesLocation;
    private Insets imageInsets;
    /** Array with image bevel borders.
     * This array is created lazily.
     **/
    private Border[] borders;
    
    /**
     * Column index.
     */
    private int columnIndex = 0;
    
    private Border tableHeaderBackground = new Border() {
        public Insets getBorderInsets(Component c) {
            return new Insets(0,0,0,0);
        }
        public boolean isBorderOpaque() {
            return false;
        }
        
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            getBorder(c).paintBorder(c, g, x, y, width, height);
        }
    };
    
    /** Creates a new instance. */
    public QuaquaTableHeaderBorder(String imagesLocation, Insets imageInsets) {
        this.imagesLocation = imagesLocation;
        this.imageInsets = imageInsets;
    }
    
    public Insets getBorderInsets(Component c) {
        return getBorderInsets(c, null);
    }
    
    public Insets getBorderInsets(Component c, Insets insets) {
        return new Insets(1,2,1,2);
    }
    
    public boolean isBorderOpaque() {
        return false;
    }
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    }
    
    public Border getBackgroundBorder() {
        return tableHeaderBackground;
    }
    
    /**
     * FIXME: We should find a better way to pass the column index to the border.
     */
    public void setColumnIndex(int index) {
        this.columnIndex = index;
    }
    
    private Border getBorder(Component c) {
        if (borders == null) {
            borders = (Border[]) QuaquaBorderFactory.create(imagesLocation, imageInsets, 4, true, true, true);
        }
        return borders[0];
    }
    
    public static class UIResource extends QuaquaTableHeaderBorder implements javax.swing.plaf.UIResource {
        public UIResource(String imagesLocation, Insets imageInsets) {
            super(imagesLocation, imageInsets);
        }
    }
}
