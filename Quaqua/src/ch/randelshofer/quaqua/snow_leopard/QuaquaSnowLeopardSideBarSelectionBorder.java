/*
 * @(#)QuaquaSnowLeopardSideBarSelectionBorder.java
 *
 * Copyright (c) 2009-2010 Werner Randelshofer
 * Hausmatt 10, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package ch.randelshofer.quaqua.snow_leopard;

import ch.randelshofer.quaqua.*;
import java.awt.*;
import java.awt.Insets;
import javax.swing.border.*;
import javax.swing.plaf.UIResource;

/**
 * QuaquaSnowLeopardSideBarSelectionBorder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class QuaquaSnowLeopardSideBarSelectionBorder implements Border, UIResource {

    /** Creates a new instance. */
    public QuaquaSnowLeopardSideBarSelectionBorder() {
    }

    public void paintBorder(Component c, Graphics gr, int x, int y, int width, int height) {
        Graphics2D g = (Graphics2D) gr;
        if (QuaquaUtilities.isFocused(c)) {
            // top line: 0x5896d0
            g.setColor(new Color(0x5896d0));
            g.fillRect(x, y, width, 1);
            g.setPaint(new GradientPaint(
                    x, y + 1, new Color(0x6ea6d6),
                    x, y + height - 1, new Color(0x216cb7),
                    true));
        } else {
            if (QuaquaUtilities.isOnActiveWindow(c, true)) {
                // top line: 0xa2b1cb
                g.setColor(new Color(0xa2b1cb));
                g.fillRect(x, y, width, 1);
                g.setPaint(new GradientPaint(
                        x, y + 1, new Color(0xb1bfd8),
                        x, y + height - 1, new Color(0x8296b8),
                        true));

            } else {
                // top line: 0xa8a8a8
                g.setColor(new Color(0xa8a8a8));
                g.fillRect(x, y, width, 1);
                g.setPaint(new GradientPaint(
                        x, y + 1, new Color(0xc1c1c1),
                        x, y + height - 1, new Color(0x9c9c9c)));
            }
        }
        g.fillRect(x, y + 1, width, height - 1);
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 0, 0);
    }

    public boolean isBorderOpaque() {
        return true;
    }
}
