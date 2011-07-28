/*
 * @(#)QuaquaLionButtonStateBorder.java 
 * 
 * Copyright (c) 2011 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.osx.OSXAquaPainter;
import ch.randelshofer.quaqua.util.CachedPainter;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import static ch.randelshofer.quaqua.osx.OSXAquaPainter.*;

/**
 * Native Aqua icon for an {@code AbstractButton).
 * This icon draws everything except the focus ring. To draw the focus
 * wring, wrap this border into a {@link ch.randelshofer.quaqua.border.FocusedIcon}.

 * @author Werner Randelshofer
 * @version $Id$
 */
public class QuaquaNativeButtonStateIcon extends CachedPainter implements Icon {

    private OSXAquaPainter painter;
    private int width;
    private int height;
    private int xoffset;
    private int yoffset;

    public QuaquaNativeButtonStateIcon(Widget widget, int width, int height) {
        this(widget,0,0,width,height);
        
    }
    public QuaquaNativeButtonStateIcon(Widget widget, int xoffset, int yoffset, int width, int height) {
        super(12);
        painter = new OSXAquaPainter();
        painter.setWidget(widget);
        this.xoffset=xoffset;
        this.yoffset=yoffset;
        this.width = width;
        this.height = height;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        AbstractButton b = null;
        ButtonModel bm = null;
        if (c instanceof AbstractButton) {
            b = (AbstractButton) c;
            bm = b.getModel();
        }

        int args = 0;
        State state;
        if (QuaquaUtilities.isOnActiveWindow(c)) {
            state = State.active;
            args |= 1;
        } else {
            state = State.inactive;
        }
        if (bm != null) {
            if (bm.isArmed() && bm.isPressed()) {
                state = State.pressed;
                args |= 2;
            }
            if (!bm.isEnabled()) {
                state = State.disabled;
                args |= 4;
            }
            if (bm.isRollover()) {
                state = State.rollover;
                args |= 8;
            }
        }
        painter.setState(state);
//        painter.setValueByKey(Key.arrowsOnly,1);

        boolean isFocused = QuaquaUtilities.isFocused(c);
        args |= (isFocused) ? 16 : 0;
        painter.setValueByKey(Key.focused, isFocused ? 1 : 0);

        Size size;
        if (QuaquaUtilities.isSmallSizeVariant(c)) {
            size = Size.small;
            args |= 32;
        } else if (QuaquaUtilities.isLargeSizeVariant(c)) {
            size = Size.large;
            args |= 64;
        } else {
            size = Size.regular;
        }
        painter.setSize(size);

        paint(c, g, x, y, width, height, args);
    }

    @Override
    protected Image createImage(Component c, int w, int h,
            GraphicsConfiguration config) {

        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE);

    }

    @Override
    protected void paintToImage(Component c, Image img, int w, int h, Object args) {
        Graphics2D ig = (Graphics2D) img.getGraphics();
        ig.setColor(new Color(0x0, true));
        ig.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        ig.fillRect(0, 0, img.getWidth(null), img.getHeight(null));
        ig.dispose();
        painter.paint((BufferedImage) img,//
                xoffset,yoffset,//
                width, //
                height);
    }

    @Override
    protected void paintToImage(Component c, Graphics g, int w, int h, Object args) {
        // empty
    }

    public int getIconWidth() {
        return width;
    }

    public int getIconHeight() {
        return height;
    }

    public boolean isBorderOpaque() {
        return false;
    }

    public static class UIResource extends QuaquaNativeButtonStateIcon implements javax.swing.plaf.UIResource {

        public UIResource(Widget widget, int offsetx, int offsety, int width, int height) {
            super(widget, offsetx, offsety, width, height);
        }

        public UIResource(Widget widget, int width, int height) {
            super(widget, width, height);
        }

       
        
    }
}
