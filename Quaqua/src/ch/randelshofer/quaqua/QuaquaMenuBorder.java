/*
 * @(#)QuaquaMenuBorder.java  1.1  2005-12-10
 *
 * Copyright (c) 2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * http://www.randelshofer.ch
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
 * A replacement for the AquaMenuBorder.
 * <p>
 * This class provides the following workaround for a bug in Apple's
 * implementation of the Aqua Look and Feel in Java 1.4.1:
 * <ul>
 * <li>Draws a border at the top and the bottom of JPopupMenu's.
 * </ul>
 *
 * @author Werner Randelshofer
 * @version 1.1 2005-12-10 getBorderInsets does now an instanceof test.
 * <br>1.0.1 2005-06-25 Return a new instance of Insets in method getBorderInsets.
 * <br>1.0 August 31, 2003  Created.
 */
public class QuaquaMenuBorder implements Border {    
    protected static Insets popupBorderInsets;
    protected static Insets itemBorderInsets;
    
    public void paintBorder(Component component, Graphics graphics, int x,
    int y, int width, int height) {
        /* empty */
    }
    
    public Insets getBorderInsets(Component component) {
        Insets insets;
        
        if (component instanceof JPopupMenu) {
            if (popupBorderInsets == null) {
                popupBorderInsets = new Insets(4, 0, 4, 0);
                }
            insets = (Insets) popupBorderInsets.clone();
        } else {
            if (itemBorderInsets == null) {
                itemBorderInsets = new Insets(0, 0, 0, 0);
                }
            insets = (Insets) itemBorderInsets.clone();
        }
        return insets;
    }
    
    public boolean isBorderOpaque() {
        return false;
    }
}