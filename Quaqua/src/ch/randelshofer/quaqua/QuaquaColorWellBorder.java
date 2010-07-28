/*
 * @(#)QuaquaColorWellBorder.java  
 *
 * Copyright (c) 2004-2010 Werner Randelshofer
 * Hausmatt 10, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import java.awt.*;
import javax.swing.border.*;
/**
 * QuaquaColorWellBorder.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class QuaquaColorWellBorder implements Border {
    private Border squareButtonBorder;
    
    /** Creates a new instance. */
    public QuaquaColorWellBorder() {
        this(QuaquaBorderFactory.createSquareButtonBorder());
    }
    public QuaquaColorWellBorder(Border squareButtonBorder) {
        this.squareButtonBorder = squareButtonBorder;
    }
    
    public Insets getBorderInsets(Component c) {
        return new Insets(5, 5, 5, 5);
    }
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        squareButtonBorder.paintBorder(c, g, x, y, width, height);
        g.setColor(c.getBackground());
        g.fillRect(x+6,y+6,width-12,height-12);
        g.setColor(c.getBackground().darker());
        g.drawRect(x+5,y+5,width-11,height-11);
    }
    
    public boolean isBorderOpaque() {
        return squareButtonBorder.isBorderOpaque();
    }    
}
