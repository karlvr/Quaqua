/*
 * @(#)CompositeIcon.java  1.0  20 March 2005
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

package ch.randelshofer.quaqua.icon;

import javax.swing.*;
import java.awt.*;
/**
 * OverlayIcon.
 *
 * @author  Werner Randelshofer
 * @version 1.0  20 March 2005  Created.
 */
public class OverlayIcon implements Icon {
    private Icon[] icons;
    
    /**
     * Creates a new instance.
     * Constructor with objects only used by BasicQuaquaLookAndFeel classes.
     * This constructor helps to reduce startuc latency, because we don't
     * need to load the Icon interface during the first creation of 
     * BasicQuaquaLookAndFeel class.
     */
    public OverlayIcon(Object first, Object second) {
        this((Icon) first, (Icon) second);
    }
    /** Creates a new instance. */
    public OverlayIcon(Icon first, Icon second) {
        this.icons = new Icon[] { first, second };
    }
    
    public int getIconHeight() {
        return icons[0].getIconHeight();
    }
    
    public int getIconWidth() {
        return icons[0].getIconWidth();
    }
    
    public void paintIcon(Component c, Graphics g, int x, int y) {
        for (int i=0; i < icons.length; i++) {
            if (icons[i]!=null) {
            icons[i].paintIcon(c, g, x, y);
            }
        }
    }    
}
