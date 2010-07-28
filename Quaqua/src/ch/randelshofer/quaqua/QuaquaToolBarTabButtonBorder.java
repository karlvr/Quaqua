/*
 * @(#)ToolBarTabButtonBorder.java  1.0  30 March 2005
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
import javax.swing.*;
import javax.swing.border.*;

/**
 * ToolBarTabButtonBorder.
 *
 * @author  Werner Randelshofer
 * @version 1.0  30 March 2005  Created.
 */
public class QuaquaToolBarTabButtonBorder implements Border {
    private final static Color foreground = new Color(185,185,185);
    private final static Color background = new Color(0x1e000000,true);
    
    public Insets getBorderInsets(Component c) {
        return new Insets(3,5,3,5);
    }
    
    public boolean isBorderOpaque() {
        return false;
    }
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (c instanceof AbstractButton) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();
            if (b.isSelected() || model.isPressed() && model.isArmed()) {
                g.setColor(foreground);
                g.fillRect(x, y, 1, height);
                g.fillRect(x + width - 1, y, 1, height);
                g.setColor(background);
                g.fillRect(x + 1, y, width - 2, height);
            }
        }
    }
    
}
