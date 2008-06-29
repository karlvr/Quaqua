/*
 * @(#)MatteBevelBorder.java  1.0.1  2005-06-25
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
/**
 * MatteBevelBorder.
 *
 * @author  Werner Randelshofer
 * @version 1.0.1 2005-06-25 Return a new insets instance in method getBorderInsets.
 * <br>1.0  29 December 2004  Created.
 */
public class MatteBevelBorder implements Border {
    private Insets borderInsets;
    private Border bevelBorder;
    
    /** Creates a new instance. */
    public MatteBevelBorder(Insets borderInsets, Border bevelBorder) {
        this.borderInsets = borderInsets;
        this.bevelBorder = bevelBorder;
    }
    

    public Insets getBorderInsets(Component c) {
        return (Insets) borderInsets.clone();
    }
    
    public boolean isBorderOpaque() {
        return false;
    }
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        bevelBorder.paintBorder(c, g, x, y, width, height);
    }
    
}
