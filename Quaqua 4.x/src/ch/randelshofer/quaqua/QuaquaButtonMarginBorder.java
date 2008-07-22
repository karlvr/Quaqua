/*
 * @(#)QuaquaButtonMarginBorder.java  1.0  31 March 2005
 *
 * Copyright (c) 2004 Werner Randelshofer
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
 * QuaquaButtonMarginBorder is used to honour the margins between button text 
 * and button border.
 *
 * @author  Werner Randelshofer
 * @version 1.0  31 March 2005  Created.
 */
public class QuaquaButtonMarginBorder extends AbstractBorder {
    
    public Insets getBorderInsets(Component c) {
        return getBorderInsets(c, new Insets(0, 0, 0, 0));
    }
    
    /**
     * Reinitializes the insets parameter with this Border's current Insets.
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     * @return the <code>insets</code> object
     */
    public Insets getBorderInsets(Component c, Insets insets) {
        if (c instanceof AbstractButton) {
            AbstractButton b = (AbstractButton) c;
            Insets margin = b.getMargin();
            /*
            if (margin == null) {
                margin = getDefaultMargin((JComponent) c);
            }*/
            if (margin != null) {
                insets.top += margin.top;
                insets.left += margin.left;
                insets.bottom += margin.bottom;
                insets.right += margin.right;
            }
        }
        return insets;
    }
}
