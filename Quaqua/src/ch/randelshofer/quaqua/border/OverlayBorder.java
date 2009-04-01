/*
 * @(#)OverlayBorder.java  1.0.1  2005-06-25
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

package ch.randelshofer.quaqua.border;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
/**
 * OverlayBorder.
 *
 * @author  Werner Randelshofer
 * @version 1.0.1 2005-06-25 Return a new insets instance in method getBorderInsets.
 * <br>1.0  21 March 2005  Created.
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
