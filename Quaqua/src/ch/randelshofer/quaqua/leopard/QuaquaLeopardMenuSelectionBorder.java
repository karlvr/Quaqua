/**
 * @(#)QuaquaLeopardMenuSelectionBorder.java  1.0  May 4, 2008
 *
 * Copyright (c) 2008-2010 Werner Randelshofer
 * Hausmatt 10, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package ch.randelshofer.quaqua.leopard;

import java.awt.*;
import java.awt.geom.Point2D;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * QuaquaLeopardMenuSelectionBackgroundBorder.
 *
 * @author Werner Randelshofer
 * @version 1.0 May 4, 2008 Created.
 */
public class QuaquaLeopardMenuSelectionBorder implements Border {

    public void paintBorder(Component c, Graphics gr, int x, int y, int width, int height) {
        Graphics2D g = (Graphics2D) gr;
        if (c instanceof AbstractButton) {
        AbstractButton menuItem = (AbstractButton) c;
        ButtonModel model = menuItem.getModel();

            if (model.isArmed() || (menuItem instanceof JMenu && model.isSelected())) {
                g.setColor(new Color(0x4b69ea));
                g.fillRect(x, y, width, 1);
                g.setColor(new Color(0x0e37e7));
                g.fillRect(x, y + height - 1, width, 1);
                g.setPaint(new GradientPaint(new Point2D.Float(x, y + 1), new Color(0x5170f6), new Point2D.Float(x, y + height - 2), new Color(0x1a43f3)));
                g.fillRect(x, y + 1, width, height - 2);
            }
        }
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(1, 1, 1, 1);
    }

    public boolean isBorderOpaque() {
        return false;
    }
}
