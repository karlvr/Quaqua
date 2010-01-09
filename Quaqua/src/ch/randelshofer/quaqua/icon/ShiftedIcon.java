/*
 * @(#)ShiftedIcon.java  1.0  May 12, 2006
 *
 * Copyright (c) 2006-2010 Werner Randelshofer
 * Hausmatt 10, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua.icon;

import java.awt.*;
import javax.swing.*;
/**
 * ShiftedIcon renders a target icon at a different location and can return
 * different width and height values than the target.
 *
 * @author Werner Randelshofer.
 * @version 1.0 May 12, 2006 Created.
 */
public class ShiftedIcon implements Icon {
    private Icon target;
    private Rectangle shift;
    
    /** Creates a new instance. */
    public ShiftedIcon(Icon target, Point shift) {
        this.target = target;
        this.shift = new Rectangle(
                shift.x, shift.y, 
                target.getIconWidth(), 
                target.getIconHeight()
                );
    }
    public ShiftedIcon(Icon target, Rectangle shiftAndSize) {
        this.target = target;
        this.shift = shiftAndSize;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        target.paintIcon(c, g, x + shift.x, y + shift.y);
    }

    public int getIconWidth() {
        return shift.width;
    }

    public int getIconHeight() {
        return shift.height;
    }
    
}
