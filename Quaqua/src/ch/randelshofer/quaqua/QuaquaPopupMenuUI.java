/*
 * @(#)QuaquaPopupMenuUI.java
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
package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.color.PaintableColor;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

/**
 * QuaquaPopupMenuUI.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class QuaquaPopupMenuUI extends BasicPopupMenuUI implements QuaquaMenuPainterClient {
    public static ComponentUI createUI(JComponent x) {
	return new QuaquaPopupMenuUI();
    }

    public QuaquaPopupMenuUI() {
    }

    public void paintBackground(Graphics g, JComponent component, int menuWidth, int menuHeight) {
        Color bgColor = UIManager.getColor("PopupMenu.selectionBackground");
        AbstractButton menuItem = (AbstractButton) component;
        ButtonModel model = menuItem.getModel();
        Color oldColor = g.getColor();

        if (menuItem.isOpaque()) {
            if (model.isArmed() || model.isSelected()) {
                ((Graphics2D) g).setPaint(PaintableColor.getPaint(bgColor, component));
                g.fillRect(0, 0, menuWidth, menuHeight);
            } else {
                ((Graphics2D) g).setPaint(PaintableColor.getPaint(menuItem.getBackground(), component));
                g.fillRect(0, 0, menuWidth, menuHeight);
            }
            g.setColor(oldColor);
        }
    }
}
