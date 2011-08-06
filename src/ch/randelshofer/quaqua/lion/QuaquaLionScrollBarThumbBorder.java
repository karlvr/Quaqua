/*
 * @(#)QuaquaLionScrollBarThumbBorder.java  1.0  2011-08-05
 * 
 * Copyright (c) 2011 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package ch.randelshofer.quaqua.lion;

import ch.randelshofer.quaqua.QuaquaUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

/**
 * {@code QuaquaLionScrollBarThumbBorder}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-08-05 Created.
 */
public class QuaquaLionScrollBarThumbBorder implements Border, UIResource {

    private final static Color activeThumbColor = new Color(0x74747474, false);
    private final static Color thumbColor = new Color(0xc3c3c3c3, false);

    public void paintBorder(Component c, Graphics gr, int x, int y, int width, int height) {
        JScrollBar sb = (JScrollBar) c;
        Graphics2D g = (Graphics2D) gr;
        Object oldHints = QuaquaUtilities.beginGraphics(g);
        Dimension ps = sb.getUI().getPreferredSize(sb);

        boolean isActive=sb.getValueIsAdjusting();
        
        g.setColor(isActive?activeThumbColor:thumbColor);

        if (sb.getOrientation() == SwingConstants.HORIZONTAL) {
            height = Math.min(ps.height, height);
            g.fillRoundRect(x + 2, y + 2, width - 4, height - 4, height - 4, height - 4);
        } else {
            width = Math.min(ps.width, width);
            g.fillRoundRect(x + 2, y + 2, width - 4, height - 4, width - 4, width - 4);
        }
        QuaquaUtilities.endGraphics(g, oldHints);
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(2, 2, 2, 2);
    }

    public boolean isBorderOpaque() {
        return false;
    }
}
