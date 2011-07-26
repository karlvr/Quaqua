/*
 * @(#)FocusedIcon.java  1.0  2011-07-26
 * 
 * Copyright (c) 2011 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
/*//
//  AquaFocus.java
//  Copyright (c) 2009 Apple Inc. All rights reserved.
//*/
package ch.randelshofer.quaqua.icon;

import ch.randelshofer.quaqua.border.Painter;
import ch.randelshofer.quaqua.border.ShadowBorder;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
import javax.swing.UIManager;

/**
 * {@code FocusedIcon}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-07-26 Created.
 */
public class FocusedIcon extends ShadowBorder implements Icon {

    final Icon icon;
    final int slack;

    public FocusedIcon(final Icon icon, final int slack) {
        super(
                new Painter() {

                    public void paint(Graphics g, int x, int y, int w, int h) {
                        Graphics2D imgG = (Graphics2D) g;
                        imgG.setComposite(AlphaComposite.Src);
                        imgG.setColor(UIManager.getColor("Focus.color"));
                        imgG.fillRect(x, y, w - (slack * 2), h - (slack * 2));
                        imgG.setComposite(AlphaComposite.DstAtop);
                        icon.paintIcon(null, imgG, x, y);
                    }
                },
                new Painter() {

                    public void paint(Graphics g, int x, int y, int w, int h) {
                        ((Graphics2D) g).setComposite(AlphaComposite.SrcAtop);
                        icon.paintIcon(null, g, x, y);
                    }
                },
                slack, slack, 0.0f, 1.8f, 7);
        this.icon = icon;
        this.slack = slack;
    }

    @Override
    public int getIconHeight() {
        return icon.getIconHeight() + slack + slack;
    }

    @Override
    public int getIconWidth() {
        return icon.getIconWidth() + slack + slack;
    }

    @Override
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        paintBorder(c, g, x, y, getIconWidth(), getIconHeight());
        icon.paintIcon(c, g, x + slack, y + slack);
    }
}
