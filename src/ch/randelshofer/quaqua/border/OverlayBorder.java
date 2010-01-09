/*
 * @(#)OverlayBorder.java  
 *
 * Copyright (c) 2005-2010 Werner Randelshofer
 * Hausmatt 10, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua.border;

import java.awt.*;
import javax.swing.border.*;
/**
 * OverlayBorder.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class OverlayBorder implements Border {
    private Border[] borders;
    
    /** Creates a new instance. */
    public OverlayBorder(Border first, Border second) {
        borders = new Border[] { first, second };
    }
    
    public Insets getBorderInsets(Component c) {
        return (Insets) borders[0].getBorderInsets(c).clone();
    }
    
    public boolean isBorderOpaque() {
        return false;
    }
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        for (int i=0; i < borders.length; i++) {
            borders[i].paintBorder(c, g, x, y, width, height);
        }
    }    
}
