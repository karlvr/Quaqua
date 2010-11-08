/*
 * @(#)EmptyIcon.java  
 * 
 * Copyright © 2010 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 * 
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms.
 */

package ch.randelshofer.quaqua.icon;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;

/**
 * {@code EmptyIcon}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EmptyIcon implements Icon, Serializable {
    private int width;
    private int height;
    
    public EmptyIcon(int width, int height) {
        this.width=width;
        this.height=height;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        // empty
    }

    public int getIconWidth() {
        return width;
    }

    public int getIconHeight() {
        return height;
    }

}
