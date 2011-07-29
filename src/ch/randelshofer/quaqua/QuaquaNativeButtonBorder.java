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

import ch.randelshofer.quaqua.border.CompositeVisualMarginBorder;
import ch.randelshofer.quaqua.border.VisualMarginBorder;
import ch.randelshofer.quaqua.util.Images;
import ch.randelshofer.quaqua.border.ButtonStateBorder;
import ch.randelshofer.quaqua.border.OverlayBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import ch.randelshofer.quaqua.util.InsetsUtil;
import ch.randelshofer.quaqua.border.BackgroundBorder;
import ch.randelshofer.quaqua.border.FocusedBorder;
import javax.swing.JComponent;
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
public class QuaquaNativeButtonBorder extends VisualMarginBorder implements Border, BackgroundBorder {

    private OSXAquaPainter painter;
    private Insets imageInsets;
    private Insets borderInsets;
    private Border backgroundBorder;

    /** The background border.
     * This border delegates the actual painting to different borders, because
     * {@link OSXAquaPainter} can not render all borders that we need.
     */
    private class BGBorder implements Border, VisualMargin {

        private Border nativeBorder;
        private Border bevelBorder;
        private Border placardBorder;
        private Border colorWellBorder;

        private Border getActualBorder(Component c) {


            Widget w = getWidget(c);
            //return w==null?bevelBorder:nativeBorder;

            Border b;
            if (w == null) {
                b = getBevelBorder();
            } else {
                switch (w) {
                    case framePlacard:
                        b = getPlacardBorder();
                        break;
                    case frameWell:
                        b = getColorWellBorder();
                        break;
                    default:
                        b = getNativeBorder();
                        break;
                }
            }

            return b;
        }

        private Border getBevelBorder() {
            if (bevelBorder == null) {
                bevelBorder = new FocusedBorder(
                        new CompositeVisualMarginBorder(
                        new ButtonStateBorder(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/RoundedBevel.borders.png")), 10, true,
                        new Insets(10, 9, 10, 8), new Insets(0, 0, 0, 0), true),
                        3, 2, 2, 2));
            }
            return bevelBorder;
        }

        private Border getNativeBorder() {
            if (nativeBorder == null) {
                this.nativeBorder =
                        new FocusedBorder(new NativeBGBorder());
            }
            return nativeBorder;
        }

        private Border getPlacardBorder() {
            if (placardBorder == null) {
                // The placarBorder does not have a dynamic visual margin.
                placardBorder = new FocusedBorder(
                        new CompositeVisualMarginBorder(
                        new QuaquaPlacardButtonBorder(),
                        1, 0, 1, 0, true, true, true, true));

            }
            return placardBorder;
        }

        private Border getColorWellBorder() {
            if (colorWellBorder == null) {
                colorWellBorder = new FocusedBorder(
                        new CompositeVisualMarginBorder(
                        new QuaquaColorWellBorder(),
                        0, 0, 0, 0));

            }
            return colorWellBorder;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            getActualBorder(c).paintBorder(c, g, x, y, width, height);
        }

        public Insets getBorderInsets(Component c) {
            return getActualBorder(c).getBorderInsets(c);
        }

        public boolean isBorderOpaque() {
            return false;
        }

        public Insets getVisualMargin(Component c) {
            return ((VisualMargin) getActualBorder(c)).getVisualMargin(c);
        }
    }

    /** This is the actual native button border. */
    private class NativeBGBorder extends CachedPainter implements Border, VisualMargin {
private final static int ARG_ACTIVE=0;
private final static int ARG_PRESSED=1;
private final static int ARG_DISABLED=2;
private final static int ARG_ROLLOVER=3;
private final static int ARG_SELECTED=4;
private final static int ARG_FOCUSED=5;
private final static int ARG_SIZE_VARIANT=6;//2 bits
private final static int ARG_SEGPOS=8;
private final static int ARG_WIDGET=11;// 7 bits
private final static int ARG_TRAILING_SEPARATOR=18;

        public NativeBGBorder() {
            super(12);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            AbstractButton b = null;
            JComponent jc = null;
            ButtonModel bm = null;
            if (c instanceof AbstractButton) {
                b = (AbstractButton) c;
                jc = b;
                bm = b.getModel();
            } else if (c instanceof JComponent) {
                jc = (JComponent) c;
            }

            int args = 0;
            OSXAquaPainter.State state;
            if (QuaquaUtilities.isOnActiveWindow(c)) {
                state = OSXAquaPainter.State.active;
                args |= 1 << ARG_ACTIVE;
            } else {
                state = OSXAquaPainter.State.inactive;
            }
            if (bm != null) {
                if (bm.isArmed() && bm.isPressed()) {
                    state = OSXAquaPainter.State.pressed;
                    args |= 1 << ARG_PRESSED;
                }
                if (!bm.isEnabled()) {
                    state = OSXAquaPainter.State.disabled;
                    args |= 1 << ARG_DISABLED;
                }
                if (bm.isRollover()) {
                    state = OSXAquaPainter.State.rollover;
                    args |= 1 << ARG_ROLLOVER;
                }
            }
            painter.setState(state);

            int value = b == null ? 1 : (b.isSelected() ? 1 : 0);
            painter.setValueByKey(Key.value, value);
            args |= value << ARG_SELECTED;
            boolean isFocused = QuaquaUtilities.isFocused(c);
            args |= (isFocused) ? 1 << ARG_FOCUSED : 0;
            painter.setValueByKey(OSXAquaPainter.Key.focused, isFocused ? 1 : 0);

            Size size;
            switch (QuaquaUtilities.getSizeVariant(c)) {
                case REGULAR:
                default:
                    size = Size.regular;
                    args |= 0 << ARG_SIZE_VARIANT;
                    break;
                case SMALL:
                    size = Size.small;
                    args |= 1 << ARG_SIZE_VARIANT;
                    break;
                case LARGE:
                    size = Size.large;
                    args |= 2 << ARG_SIZE_VARIANT;
                    break;
                case MINI:
                    size = Size.mini;
                    args |= 3 << ARG_SIZE_VARIANT;
                    break;

            }
            painter.setSize(size);

            SegmentPosition segpos = getSegmentPosition(c);
            painter.setSegmentPosition(segpos);
            args |= segpos.getId() << ARG_SEGPOS;
            switch (segpos) {
                case first:
                case middle:
                    painter.setValueByKey(Key.segmentTrailingSeparator, 1);
                    args|=1<<ARG_TRAILING_SEPARATOR;
                    break;
                default:
                    painter.setValueByKey(Key.segmentTrailingSeparator, 0);
            }



            Widget widget = getWidget(c);
            args |= widget.getId() << ARG_WIDGET;
            painter.setWidget(widget);
            
            imageInsets.left = imageInsets.top = imageInsets.right = imageInsets.bottom = 0;
            switch (widget) {
                case buttonPush:
                    switch (size) {
                        case regular:
                        default:
                            imageInsets.top = 2;
                            imageInsets.left = imageInsets.right = -3;
                            break;
                        case small:
                            imageInsets.top = 2;
                            imageInsets.left = imageInsets.right = -2;
                            break;
                        case mini:
                            imageInsets.top = 1;
                            imageInsets.left = imageInsets.right = 2;
                            break;
                    }
                    break;
                case buttonBevel: // bevel is actually square
                    imageInsets.top = imageInsets.left = imageInsets.right = imageInsets.bottom = 0;
                    break;
                case buttonBevelRound:
                    imageInsets.top = imageInsets.left = imageInsets.right = imageInsets.bottom = 1;
                    break;
                case buttonBevelInset:
                    imageInsets.top = imageInsets.bottom = -1;
                    break;
                case buttonListHeader:
                    imageInsets.left = -1;
                    break;
                case buttonPushTextured:
                    switch (size) {
                        case regular:
                        default:
                            imageInsets.top = 2;
                            imageInsets.left = imageInsets.right = 3;
                            imageInsets.bottom = 1;
                            break;
                        case small:
                            imageInsets.top = 2;
                            imageInsets.left = imageInsets.right = -2;
                            break;
                        case mini:
                            imageInsets.top = 1;
                            imageInsets.left = imageInsets.right = 2;
                            break;
                    }
                    break;
                case buttonPushInset: // round rect
                    imageInsets.left = 2;
                    imageInsets.right = 2;
                    break;
                case buttonPushScope: // recessed
                    imageInsets.left = 2;
                    imageInsets.right = 2;
                    break;
                case buttonRoundHelp:
                    break;
                case buttonSegmented:
                    switch (size) {
                        case regular:
                        default:
                            switch (segpos) {
                                case first:
                                    imageInsets.left = 1;
                                    break;
                                case middle:
                                    imageInsets.left = 0;
                                    break;
                                case last:
                                    imageInsets.left =0;
                                    imageInsets.right = 1;
                                    break;
                                case only:
                                    imageInsets.left = 1;
                                    imageInsets.right = 1;
                                    break;
                            }
                            break;
                        case small:
                            imageInsets.top = 1;
                            switch (segpos) {
                                case first:
                                    imageInsets.left = 1;
                                    break;
                                case last:
                                    imageInsets.right = 1;
                                    break;
                                case only:
                                    imageInsets.left = imageInsets.right = 1;
                                    break;
                            }
                            break;
                        case mini:
                            switch (segpos) {
                                case first:
                                    imageInsets.left = 2;
                                    break;
                                case last:
                                    imageInsets.right = 2;
                                    break;
                                case only:
                                    imageInsets.left = imageInsets.right = 2;
                                    break;
                            }
                            break;
                    }
                    break;
                case buttonSegmentedInset:
                    switch (size) {
                        case regular:
                        default:
                            switch (segpos) {
                                case first:
                                    imageInsets.left = 2;
                                    break;
                                case last:
                                    imageInsets.right = 2;
                                    break;
                                case only:
                                    imageInsets.left = imageInsets.right = 2;
                            }
                            break;
                        case small:
                            imageInsets.top = 0;
                            switch (segpos) {
                                case first:
                                    imageInsets.left = 2;
                                    break;
                                case last:
                                    imageInsets.right = 2;
                                    break;
                                case only:
                                    imageInsets.left = imageInsets.right = 2;
                                    break;
                            }
                            break;
                        case mini:
                            switch (segpos) {
                                case first:
                                    imageInsets.left = 2;
                                    break;
                                case last:
                                    imageInsets.right = 2;
                                    break;
                                case only:
                                    imageInsets.left = imageInsets.right = 2;
                                    break;
                            }
                            break;
                    }
                    break;
                case buttonSegmentedSCurve:
                    break;
                case buttonSegmentedTextured:
                    break;
                case buttonSegmentedToolbar:
                    break;
            }

            paint(c, g, x, y, width, height, args);
        }

        @Override
        protected Image createImage(Component c, int w, int h,
                GraphicsConfiguration config) {

            return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE);

        }

        @Override
        protected void paintToImage(Component c, Image img, int w, int h, Object argso) {
            Graphics2D ig = (Graphics2D) img.getGraphics();
            ig.setColor(new Color(0x0, true));
            ig.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
            ig.fillRect(0, 0, img.getWidth(null), img.getHeight(null));
            ig.dispose();
            painter.paint((BufferedImage) img,//
                    imageInsets.left, imageInsets.top,//
                    w - imageInsets.left - imageInsets.right, //
                    h - imageInsets.top - imageInsets.bottom);
            
            // Workaround for trailing separators: for some reason they are not
            // drawn, so we draw them by ourselves.
            // FIXME - The color and offsets of the saparator should not be
            // hardcoded here.
            int args=(Integer)argso;
            if ((args&(1<<ARG_TRAILING_SEPARATOR))!=0) {
                Graphics2D g=((BufferedImage)img).createGraphics();
                g.setColor(new Color(0xeabbbbbb,true));
                g.drawLine(w-1,6,w-1,h-8);
                g.dispose();
            }
        }

        @Override
        protected void paintToImage(Component c, Graphics g, int w, int h, Object args) {
            // empty
        }

        /** Returns fake insets since this is just a background border. 
         * The real insets are returned by {@link QuaquaNativeButtonBorder#getBorderInsets}.
         */
        public Insets getBorderInsets(Component c) {
            return new Insets(0, 0, 0, 0);
        }

        public boolean isBorderOpaque() {
            return false;
        }

        public Insets getVisualMargin(Component c) {
            Insets i = QuaquaNativeButtonBorder.this.getVisualMargin(c);
            return i;
        }
    }

    public QuaquaNativeButtonBorder() {
        super(new Insets(0, 0, 0, 0));
        painter = new OSXAquaPainter();
        this.imageInsets = new Insets(0, 0, 0, 0);
        this.borderInsets = new Insets(0, 0, 0, 0);
    }

    public Border getBackgroundBorder() {
        if (backgroundBorder == null) {
            this.backgroundBorder = new BGBorder();
        }
        return backgroundBorder;
    }

    private Widget getWidget(Component c) {
        String s = null;
        Widget widget;
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            s = (String) jc.getClientProperty("Quaqua.Button.style");
            if (s == null) {
                s = (String) jc.getClientProperty("JButton.buttonType");
            }
        }

        if (s == null || s.equals("push")) {
            Insets vm = getVisualMargin(c);

            // push buttons can only have an inner size of 21 pixels or less
            if (c.getHeight() - vm.top - vm.bottom > 21
                    || QuaquaUtilities.getSizeVariant(c) == QuaquaUtilities.SizeVariant.LARGE) {
                widget = Widget.buttonBevelRound;
            } else {
                widget = Widget.buttonPush;
            }
        } else if (s.equals("square")) {
            widget = Widget.buttonBevel;
        } else if (s.equals("bevel")) {
            widget = Widget.buttonBevelRound;
        } else if (s.equals("help")) {
            widget = Widget.buttonRoundHelp;
        } else if (s.equals("placard") || s.equals("gradient")) {
            widget = Widget.buttonBevelInset;
        } else if (s.equals("colorWell")) {
            widget = Widget.frameWell; // wrong widget - we render the correct one by ourselves
        } else if (s.equals("tableHeader")) {
            widget = Widget.buttonListHeader;
        } else if (s.equals("textured")) {
            widget = Widget.buttonPushTextured;
        } else if (s.equals("roundRect")) {
            widget = Widget.buttonPushInset;
        } else if (s.equals("recessed")) {
            widget = Widget.buttonPushScope;
        } else if (s.equals("toggle") || s.equals("toggleEast")
                || s.equals("toggleCenter") || s.equals("toggleWest")) {
            widget = Widget.buttonSegmented;
        } else if (s.equals("segmented")) {
            widget = Widget.buttonSegmented;
        } else if (s.equals("segmentedRoundRect")) {
            widget = Widget.buttonSegmentedInset;
        } else if (s.equals("segmentedCapsule")) {
            widget = Widget.buttonSegmentedTextured; // capsule seems not to be supported
        } else if (s.equals("segmentedTextured")) {
            widget = Widget.buttonSegmentedTextured;
        } else {
            widget = Widget.buttonPush;
            imageInsets.top = 1;
        }
        return widget;
    }

    private SegmentPosition getSegmentPosition(Component c) {
        String s = null;
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            s = (String) jc.getClientProperty("Quaqua.Button.style");
            if (s != null) {
                if (s.equals("toggleWest")) {
                    return SegmentPosition.first;
                } else if (s.equals("toggleCenter")) {
                    return SegmentPosition.middle;
                } else if (s.equals("toggleEast")) {
                    return SegmentPosition.last;
                }
            }
            s = (String) jc.getClientProperty("JButton.segmentPosition");
            if (s != null) {
                if (s.equals("first")) {
                    return SegmentPosition.first;
                } else if (s.equals("middle")) {
                    return SegmentPosition.middle;
                } else if (s.equals("last")) {
                    return SegmentPosition.last;
                }
            }
        }
        return SegmentPosition.only;
    }

    @Override
    protected Insets getVisualMargin(Component c, Insets insets) {
        String s = null;
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            s = (String) jc.getClientProperty("Quaqua.Button.style");
            if (s == null) {
                s = (String) jc.getClientProperty("JButton.buttonType");
            }
        }
        super.getVisualMargin(c, insets);
        if (s == null || s.equals("push") || s.equals("textured")) {
        } else if (s.equals("square")) {
            InsetsUtil.clear(insets);

        } else if (s.equals("bevel")) {
        } else if (s.equals("help")) {
        } else if (s.equals("placard") || s.equals("gradient")) {
            InsetsUtil.clear(insets);

        } else if (s.equals("colorWell")) {
        } else if (s.equals("tableHeader")) {
            InsetsUtil.clear(insets);

        } else if (s.equals("roundRect")) {
        } else if (s.equals("recessed")) {
        } else {
        }

        switch (getSegmentPosition(c)) {
            case first:
                insets.right = 0;
                break;
            case middle:
                insets.left = insets.right = 0;
                break;
            case last:
                insets.left = 0;
                break;
        }

        return insets;
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        Insets i = super.getBorderInsets(c, insets);

        QuaquaUtilities.SizeVariant size = QuaquaUtilities.getSizeVariant(c);

        String s = null;
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            s = (String) jc.getClientProperty("Quaqua.Button.style");
            if (s == null) {
                s = (String) jc.getClientProperty("JButton.buttonType");
            }
        }

        InsetsUtil.clear(borderInsets);
        if (s == null || s.equals("push") || s.equals("textured")) {
            switch (size) {
                case REGULAR:
                default:
                    InsetsUtil.setTo(2, 13, 3, 13, borderInsets);
                    break;
                case SMALL:
                    InsetsUtil.setTo(2, 8, 2, 8, borderInsets);
                    break;
                case MINI:
                    InsetsUtil.setTo(2, 7, 2, 7, borderInsets);
                    break;
            }
        } else if (s.equals("bevel")) {
            switch (size) {
                case REGULAR:
                default:
                    InsetsUtil.setTo(4, 10, 5, 10, borderInsets);
                    break;
                case SMALL:
                    InsetsUtil.setTo(4, 8, 4, 8, borderInsets);
                    break;
                case MINI:
                    InsetsUtil.setTo(4, 7, 4, 7, borderInsets);
                    break;
            }
        } else if (s.equals("square") || s.equals("placard") || s.equals("gradient")) {
            switch (size) {
                case REGULAR:
                default:
                    InsetsUtil.setTo(2, 9, 3, 9, borderInsets);
                    break;
                case SMALL:
                    InsetsUtil.setTo(2, 6, 2, 6, borderInsets);
                    break;
                case MINI:
                    InsetsUtil.setTo(2, 5, 2, 5, borderInsets);
                    break;
            }
        } else if (s.equals("help")) {
        } else if (s.equals("colorWell")) {
            InsetsUtil.setTo(7, 12, 7, 12, borderInsets);
        } else if (s.equals("tableHeader")) {
            InsetsUtil.setTo(2, 4, 2, 4, borderInsets);
        } else if (s.equals("roundRect") || s.equals("recessed")) {
            switch (size) {
                case REGULAR:
                default:
                    InsetsUtil.setTo(1, 11, 1, 11, borderInsets);
                    break;
                case SMALL:
                    InsetsUtil.setTo(1, 8, 1, 8, borderInsets);
                    break;
                case MINI:
                    InsetsUtil.setTo(1, 7, 1, 7, borderInsets);
                    break;
            }
        } else if (s.equals("segmented") || s.equals("segmentedCapsule")
                || s.equals("segmentedRoundRect")
                || s.equals("segmentedTextured")
                || s.equals("toggle") || s.equals("toggleEast")
                || s.equals("toggleWest") || s.equals("toggleCenter")) {
            switch (size) {
                case REGULAR:
                default:
                    InsetsUtil.setTo(2, 13, 3, 13, borderInsets);
                    break;
                case SMALL:
                    InsetsUtil.setTo(2, 8, 2, 8, borderInsets);
                    break;
                case MINI:
                    InsetsUtil.setTo(2, 7, 2, 7, borderInsets);
                    break;
            }
        } else {
        }


        //
        InsetsUtil.addTo(borderInsets, i);
        if (c instanceof AbstractButton) {
            Insets m = ((AbstractButton) c).getMargin();
            InsetsUtil.addTo(m, i);
        }

        return i;
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    public static class UIResource extends QuaquaNativeButtonBorder implements javax.swing.plaf.UIResource {

        public UIResource() {
            super();
        }
    }
}
