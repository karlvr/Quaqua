/*
 * @(#)FocusedBorder.java  1.0  2011-07-26
 * 
 * Copyright (c) 2011 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package ch.randelshofer.quaqua.border;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * {@code FocusedBorder}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-07-26 Created.
 */
public class FocusedBorder extends ShadowBorder {

    final Border actualBorder;
    final int slack;
    Component component;

    public FocusedBorder(final Border icon, final int slack) {
        super(null, null,
                slack, slack, 0.0f, 1.8f, 7);

        prePainter = new Painter() {

            public void paint(Graphics g, int x, int y, int w, int h) {
                Graphics2D imgG = (Graphics2D) g;
                imgG.setComposite(AlphaComposite.Src);
                imgG.setColor(UIManager.getColor("Focus.color"));
                imgG.fillRect(x, y, w - (slack * 2), h - (slack * 2));
                imgG.setComposite(AlphaComposite.DstAtop);
                icon.paintBorder(component, imgG, x, y, w, h);
            }
        };
        postPainter = new Painter() {

            public void paint(Graphics g, int x, int y, int w, int h) {
                ((Graphics2D) g).setComposite(AlphaComposite.SrcAtop);
                icon.paintBorder(component, g, x, y, w, h);
            }
        };
        this.actualBorder = icon;
        this.slack = slack;
    }

    @Override
    public void paintBorder(final Component c, final Graphics g, final int x, final int y, int width, int height) {
        if (c.isFocusOwner()) {
            this.component = c;
            super.paintBorder(c, g, x, y, width, height);
        }
        actualBorder.paintBorder(c, g, x + slack, y + slack, width - (2 * slack), height - (2 * slack));
    }
}
