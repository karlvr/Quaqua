/*
 * @(#)QuaquaSmallColorWellBorder.java  1.0  2005-04-18
 *
 * Copyright (c) 2005-2010 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */

package ch.randelshofer.quaqua.colorchooser;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
/**
 * SmallColorWellBorder.
 *
 * @author  werni
 */
public class SmallColorWellBorder implements Border {
    private static Color inner = Color.white;
    private static Color outer = new Color(0x949494);
    /** Creates a new instance of QuaquaSquareButtonBorder */
    public SmallColorWellBorder() {
    }
    
    public Insets getBorderInsets(Component c) {
        return new Insets(1, 1, 1, 1);
    }
    
    public boolean isBorderOpaque() {
        return true;
    }
    
    public void paintBorder(Component c, Graphics gr, int x, int y, int width, int height) {
        gr.setColor(c.getBackground());
        gr.fillRect(x + 2, y + 2, width - 4, height - 4);
        gr.setColor(inner);
        gr.drawRect(x + 1, y + 1, width - 3, height - 3);
        gr.setColor(outer);
        gr.drawRect(x, y, width - 1, height - 1);
    }    
}
