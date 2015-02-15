/*
 * @(#)ScaledImageView.java
 *
 * Copyright (c) 2015 Werner Randelshofer, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package ch.randelshofer.quaqua.mavericks.filechooser;

import ch.randelshofer.quaqua.QuaquaUtilities;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 * ScaledImageView.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ScaledImageView extends JComponent {
private BufferedImage im;

        public void setImage(BufferedImage im) {
            this.im = im;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics gr) {
            Graphics2D g=(Graphics2D)gr;
           Object oldHints= QuaquaUtilities.beginGraphics(g);
            if (isOpaque()) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            if (im != null) {
                Insets s = getInsets();
                int left = s.left;
                int top = s.top;
                int cwidth = getWidth() - s.left - s.right;
                int cheight = getHeight() - s.top - s.bottom;
                float imwidth = im.getWidth();
                float imheight = im.getHeight();
                if (imwidth > 0 && imheight > 0) {
                    int size = Math.min(cwidth, cheight);
                    float scale = Math.min(cwidth/imwidth, cheight/imheight);
                    int extraLeft = (int) Math.max(0, (cwidth - imwidth*scale) / 2);
                    int extraTop = (int) Math.max(0, (cheight - imheight*scale) / 2);
                    g.drawImage(im, left + extraLeft, top + extraTop, size, size, null);
                }
            }
            QuaquaUtilities.endGraphics(g,oldHints);
        }
}
