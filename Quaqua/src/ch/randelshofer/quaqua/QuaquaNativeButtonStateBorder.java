/*
 * @(#)QuaquaNativeButtonStateBorder.java 
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
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.border.Border;
import static ch.randelshofer.quaqua.osx.OSXAquaPainter.*;

/**
 * Native Aqua border for an {@code AbstractButton).
 * This border draws everything except the focus ring. To draw the focus
 * wring, wrap this border into a {@link ch.randelshofer.quaqua.border.FocusedBorder}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class QuaquaNativeButtonStateBorder extends CachedPainter implements Border {

    private OSXAquaPainter painter;
    private Insets imageInsets;
    private Insets borderInsets;

    public QuaquaNativeButtonStateBorder(OSXAquaPainter.Widget widget) {
        this(widget, new Insets(0, 0, 0, 0), new Insets(0, 0, 0, 0), true);
    }

    public QuaquaNativeButtonStateBorder(OSXAquaPainter.Widget widget, Insets imageInsets, Insets borderInsets, boolean fill) {
        super(12);
        painter = new OSXAquaPainter();
        painter.setWidget(widget);
        this.imageInsets = imageInsets;
        this.borderInsets = borderInsets;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        AbstractButton b = null;
        ButtonModel bm = null;
        if (c instanceof AbstractButton) {
            b = (AbstractButton) c;
            bm = b.getModel();
        }

        int args = 0;
        OSXAquaPainter.State state;
        if (QuaquaUtilities.isOnActiveWindow(c)) {
            state = OSXAquaPainter.State.active;
            args |= 1;
        } else {
            state = OSXAquaPainter.State.inactive;
        }
        if (bm != null) {
            if (bm.isArmed() && bm.isPressed()) {
                state = OSXAquaPainter.State.pressed;
                args |= 2;
            }
            if (!bm.isEnabled()) {
                state = OSXAquaPainter.State.disabled;
                args |= 4;
            }
            if (bm.isRollover()) {
                state = OSXAquaPainter.State.rollover;
                args |= 8;
            }
        }
        painter.setState(state);

        boolean isFocused = QuaquaUtilities.isFocused(c);
        args |= (isFocused) ? 16 : 0;
        painter.setValueByKey(OSXAquaPainter.Key.focused, isFocused ? 1 : 0);

        OSXAquaPainter.Size size;
        switch (QuaquaUtilities.getSizeVariant(c)) {
            case REGULAR:default:
            size = OSXAquaPainter.Size.regular;
                break;
            case SMALL:
            size = OSXAquaPainter.Size.small;
            args |= 32;
                break;
            case MINI:
            size = OSXAquaPainter.Size.mini;
            args |= 64;
                break;
                
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
                imageInsets.left, imageInsets.top,//
                w - imageInsets.left - imageInsets.right, //
                h - imageInsets.top - imageInsets.bottom);
    }

    @Override
    protected void paintToImage(Component c, Graphics g, int w, int h, Object args) {
        // empty
    }

    public Insets getBorderInsets(Component c) {
        return (Insets) borderInsets.clone();
    }

    public boolean isBorderOpaque() {
        return false;
    }

    public static class UIResource extends QuaquaNativeButtonStateBorder implements javax.swing.plaf.UIResource {

        public UIResource(OSXAquaPainter.Widget widget) {
            super(widget);
        }

        /**
         * Creates a new instance.
         * All borders must have the same dimensions.
         */
        public UIResource(OSXAquaPainter.Widget widget, Insets imageInsets, Insets borderInsets, boolean fill) {
            super(widget, imageInsets, borderInsets, fill);
        }
    }
}
