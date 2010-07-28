/*
 * @(#)TextIcon.java  1.0  June 6, 2005
 *
 * Copyright (c) 2005 Werner Randelshofer
 * Hausmatt 10, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package test;

import java.awt.*;
import javax.swing.*;
/**
 * TextIcon.
 *
 * @author  Werner Randelshofer
 * @version 1.0 June 6, 2005 Created.
 */
public class TextIcon implements Icon {
    private JLabel label;
    /**
     * Creates a new instance.
     */
    public TextIcon(String text) {
        this.label = new JLabel(text);
        label.setSize(label.getPreferredSize());
    }
    
    public int getIconHeight() {
        return label.getHeight();
    }
    
    public int getIconWidth() {
        return label.getWidth();
    }
    
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.translate(x, y);
        label.paint(g);
        g.translate(-x, -y);
    }
}
