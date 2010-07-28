/*
 * @(#)QuaquaTigerSideBarSelectionBorder.java
 *
 * Copyright (c) 2007-2010 Werner Randelshofer
 * Hausmatt 10, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package ch.randelshofer.quaqua.tiger;

import ch.randelshofer.quaqua.*;
import java.awt.*;
import java.awt.Insets;
import javax.swing.border.*;
import javax.swing.plaf.UIResource;

/**
 * QuaquaTigerSideBarSelectionBorder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class QuaquaTigerSideBarSelectionBorder implements Border, UIResource {

    /** Creates a new instance. */
    public QuaquaTigerSideBarSelectionBorder() {
    }

    public void paintBorder(Component c, Graphics gr, int x, int y, int width, int height) {
        Graphics2D g = (Graphics2D) gr;
        if (QuaquaUtilities.isFocused(c)) {
            // top line
            g.setColor(new Color(0xadbbd0));
            g.fillRect(x, y, width, 1);
            g.setPaint(new GradientPaint(
                    x, y + 1, new Color(0xacbacf),
                    x, y + height - 2, new Color(0x98aac4),
                    true));
            g.fillRect(x, y + 1, width, height - 1);
            // bottom line
            g.setColor(new Color(0x8c98b0));
            g.fillRect(x, y + height - 1, width, 1);
        } else {
            if (QuaquaUtilities.isOnActiveWindow(c, true)) {
                // top line
                g.setColor(new Color(0xadbbd0));
                g.fillRect(x, y, width, 1);
                g.setPaint(new GradientPaint(
                        x, y + 1, new Color(0xacbacf),
                        x, y + height - 2, new Color(0x98aac4),
                        true));
                g.fillRect(x, y + 1, width, height - 1);
                // bottom line
                g.setColor(new Color(0x8c98b0));
                g.fillRect(x, y + height - 1, width, 1);
            } else {
                // top line
                g.setColor(new Color(0xadbbd0));
                g.fillRect(x, y, width, 1);
                g.setPaint(new GradientPaint(
                        x, y + 1, new Color(0xacbacf),
                        x, y + height - 2, new Color(0x98aac4),
                        true));
                g.fillRect(x, y + 1, width, height - 1);
                // bottom line
                g.setColor(new Color(0x8c98b0));
                g.fillRect(x, y + height - 1, width, 1);
            }
        }
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 0, 0);
    }

    public boolean isBorderOpaque() {
        return true;
    }
}
