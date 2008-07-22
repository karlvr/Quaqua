/*
 * @(#)BackgroundBorderUIResource.java  2.0  2005-09-25
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

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
/**
 * A BackgroundBorderUIResource is used by the Quaqua Look And Feel to tag a
 * BorderUIResource that has to be drawn on to the background of a JComponent.
 * <p>
 * It is used like a regular Border object, the BackgroundBorderUIResource works 
 * like an EmptyBorder. It just has insets, but draws nothing.
 * Using the getBackgroundBorder method, one can retrieve the background border
 * used to draw on the background of a JComponent.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2005-09-25 Interface BackgroundBorder added.
 * <br>1.0  29 March 2005  Created.
 */
public class BackgroundBorderUIResource implements Border, BackgroundBorder, UIResource {
    private Border backgroundBorder;
    /**
     * Creates an EmptyBorder which has the same insets as the specified
     * background border.
     */
    public BackgroundBorderUIResource(Border backgroundBorder) {
        this.backgroundBorder = backgroundBorder;
    }
    
    public Insets getBorderInsets(Component c) {
        return backgroundBorder.getBorderInsets(c);
    }
    
    public boolean isBorderOpaque() {
        return false;
    }
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        // do nothing
    }
    
    public Border getBackgroundBorder() {
        return backgroundBorder;
    }
}
