/*
 * @(#)QuaquaPopupMenuUI.java
 *
 * Copyright (c) 2004-2010 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
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
    public final static String WINDOW_ALPHA_PROPERTY = "Quaqua.PopupMenu.windowAlpha";

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

    /**
     * Returns the <code>Popup</code> that will be responsible for
     * displaying the <code>JPopupMenu</code>.
     *
     * @param popup JPopupMenu requesting Popup
     * @param x     Screen x location Popup is to be shown at
     * @param y     Screen y location Popup is to be shown at.
     * @return Popup that will show the JPopupMenu
     * @since 1.4
     *//*
    @Override
    public Popup getPopup(JPopupMenu popup, int x, int y) {
            PopupFactory popupFactory = PopupFactory.getSharedInstance();
            return popupFactory.getPopup(popup.getInvoker(), popup, x, y);
    }*/
}
