/*
 * @(#)QuaquaButtonBorder.java  
 *
 * Copyright (c) 2005-2010 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.border.VisualMarginBorder;
import ch.randelshofer.quaqua.border.OverlayBorder;
import ch.randelshofer.quaqua.border.FocusBorder;
import ch.randelshofer.quaqua.border.ButtonStateBorder;
import ch.randelshofer.quaqua.border.AnimatedBorder;
import ch.randelshofer.quaqua.border.CompositeVisualMarginBorder;
import ch.randelshofer.quaqua.border.PressedCueBorder;
import ch.randelshofer.quaqua.osx.OSXAquaPainter.SegmentPosition;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;

import ch.randelshofer.quaqua.util.Images;
import ch.randelshofer.quaqua.util.InsetsUtil;
import javax.swing.plaf.InsetsUIResource;

/**
 * QuaquaButtonBorder.
 * This border uses client properties and font sizes of a JComponent to
 * determine which style the border shall have.
 * For some styles, the JComponent should honour size constrictions.
 * <p>
 * The following values of the client property <code>Quaqua.Button.style</code>
 * are supported:
 * <ul>
 * <li><code>push</code> Rounded push button. Maximum height of the JComponent
 * shall be constrained to its preferred height.</li>
 * <li><code>square</code> Square button. No size constraints.</li>
 * <li><code>placard</code> or <code>gradient</code> Placard button. No size constraints.</li>
 * <li><code>colorWell</code> Square button with color area in the center.
 * No size constraints.</li>
 * <li><code>bevel</code> Rounded Bevel button. No size constraints.</li>
 * <li><code>toggle</code> or <code>segmented</code> Toggle button. Maximum height of the JComponent
 * shall be constrained to its preferred height.</li>
 * <li><code>toggleWest</code> West Toggle button. Maximum height of the JComponent
 * shall be constrained to its preferred height.</li>
 * <li><code>toggleEast</code> East Toggle button. Maximum height of the JComponent
 * shall be constrained to its preferred height.</li>
 * <li><code>toggleCenter</code> Center Toggle button. Maximum height of the JComponent
 * shall be constrained to its preferred height.</li>
 * <li><code>toolBar</code> ToolBar button. No size constraints.</li>
 * <li><code>toolBarTab</code> ToolBar Tab button. No size constraints.</li>
 * <li><code>toolBarRollover</code> ToolBar button with rollover effect. No size constraints.</li>
 * </ul>
 * If the <code>Quaqua.Button.style</code> property is missing, then the
 * following values of the client property <code>JButton.buttonType</code>
 * are supported:
 * <ul>
 * <li><code>text</code> Rounded push button. Maximum height of the JComponent
 * shall be constrained to its preferred height.</li>
 * <li><code>toolBar</code> Square button. No size constraints.</li>
 * <li><code>icon</code> Rounded Bevel button. No size constraints.</li>
 * </ul>
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class QuaquaButtonBorder implements Border, PressedCueBorder, UIResource {
    // Shared borders

    private static Border regularPushButtonBorder;
    private static Border smallPushButtonBorder;
    private static Border squareBorder;
    private static Border placardBorder;
    private static Border colorWellBorder;
    private static Border bevelBorder;
    private static Border toolBarBorder;
    private static Border toolBarRolloverBorder;
    private static Border toolBarTabBorder;
    private static Border toggleWestBorder;
    private static Border toggleEastBorder;
    private static Border toggleCenterBorder;
    private static Border toggleBorder;
    private static Border helpBorder;
    private static Border tableHeaderBorder;
    /**
     * The default client property value to be used, when no client property
     * has been specified for the JComponent.
     */
    private String defaultStyle;

    /** Creates a new instance. */
    public QuaquaButtonBorder(String defaultStyle) {
        this.defaultStyle = defaultStyle;
    }

    /** Returns a Border that implements the VisualMargin interface. */
    public Border getActualBorder(Component c) {
        Border b = null;
        String style = getStyle(c);

        JComponent jc = c instanceof JComponent ? (JComponent) c : null;
        String segpos = (jc == null) ? "only" : (String) jc.getClientProperty("JButton.segmentPosition");
        if (style.equals("toggleEast")) {
            segpos = "first";
        } else if (style.equals("toggleCenter")) {
            segpos = "middle";
        } else if (style.equals("toggleWest")) {
            segpos = "last";
        }
        if (segpos == null//
                || !segpos.equals("first") && !segpos.equals("middle") && ! !segpos.equals("last")) {
            segpos = "only";
        }

        // Explicitly chosen styles
        if (style.equals("text") || style.equals("push")) {
            switch (QuaquaUtilities.getSizeVariant(c)) {
                case SMALL:
                case MINI:
                    b = getSmallPushButtonBorder();
                    break;
                default:
                    b = getRegularPushButtonBorder();
                    break;
            }
        } else if (style.equals("toolBar")) {
            if (toolBarBorder == null) {
                toolBarBorder = new CompositeVisualMarginBorder(new CompoundBorder(
                        new EmptyBorder(-1, -1, -1, -2),
                        new QuaquaToolBarButtonStateBorder(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.borders.png")),
                        10, true,
                        new Insets(8, 10, 15, 10), new Insets(4, 6, 4, 6), true, false)),//
                        0, 0, 0, 0);
            }
            b = toolBarBorder;
        } else if (style.equals("toolBarRollover")) {
            if (toolBarRolloverBorder == null) {
                toolBarRolloverBorder = new CompositeVisualMarginBorder(new CompoundBorder(
                        new EmptyBorder(-1, -1, -1, -2),
                        new QuaquaToolBarButtonStateBorder(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.borders.png")),
                        10, true,
                        new Insets(8, 10, 15, 10), new Insets(4, 6, 4, 6), true, true)),//
                        0, 0, 0, 0);
            }
            b = toolBarRolloverBorder;
        } else if (style.equals("toolBarTab")) {
            if (toolBarTabBorder == null) {
                toolBarTabBorder = new QuaquaToolBarTabButtonBorder();
            }
            b = toolBarTabBorder;
        } else if (style.equals("square") || style.equals("toolbar")) {
            b = getSquareBorder();
        } else if (style.equals("gradient")) {
            b = getPlacardBorder();
        } else if (style.equals("tableHeader")) {
            b = getTableHeaderBorder();
        } else if (style.equals("colorWell")) {
            if (colorWellBorder == null) {
                colorWellBorder = new CompositeVisualMarginBorder(
                        new OverlayBorder(
                        new QuaquaColorWellBorder() /*)*/,
                        new CompoundBorder(
                        new EmptyBorder(-2, -2, -2, -2),
                        new FocusBorder(
                        QuaquaBorderFactory.create(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Square.focusRing.png")),
                        new Insets(10, 9, 10, 8), new Insets(6, 9, 6, 9), true)))),//
                        0, 0, 0, 0);
            }
            b = colorWellBorder;
        } else if (style.equals("icon") || style.equals("bevel")) {
            if (bevelBorder == null) {
                Insets borderInsets = new Insets(4, 3, 3, 3);
                Border focusBorder = new FocusBorder(
                        QuaquaBorderFactory.create(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/RoundedBevel.focusRing.png")),
                        new Insets(10, 9, 10, 8), borderInsets, true));

                bevelBorder = new CompositeVisualMarginBorder(
                        new CompoundBorder(
                        new EmptyBorder(-3, -2, -2, -2),
                        new OverlayBorder(
                        new ButtonStateBorder(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/RoundedBevel.borders.png")),
                        10, true,
                        new Insets(10, 9, 10, 8), borderInsets, true),
                        new CompoundBorder(
                        new EmptyBorder(0, -1, 0, -1),
                        focusBorder))),//
                        0, 0, 0, 0);
            }
            b = bevelBorder;
        } else if (segpos.equals("only") && (style.equals("toggle") || style.equals("segmented")
                || style.equals("segmentedRoundRect") || style.equals("segmentedCapsule")
                || style.contains("segmentedTextured"))) {
            if (toggleBorder == null) {
                Insets borderInsets = new Insets(3, 5, 3, 5);
                toggleBorder = new CompositeVisualMarginBorder(
                        new OverlayBorder(
                        new ButtonStateBorder(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.borders.png")),
                        10, true,
                        new Insets(8, 10, 15, 10), borderInsets, true),
                        new FocusBorder(
                        QuaquaBorderFactory.create(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.focusRing.png")),
                        new Insets(8, 10, 15, 10), borderInsets, false))),
                        2, 2, 2, 2);
            }
            b = toggleBorder;
        } else if (segpos.equals("first")
                || style.equals("toggleEast")) {
            if (toggleEastBorder == null) {
                Insets borderInsets = new Insets(3, 1, 3, 5);
                toggleEastBorder = new CompositeVisualMarginBorder(
                        new OverlayBorder(
                        new ButtonStateBorder(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.east.borders.png")),
                        10, true,
                        new Insets(8, 1, 15, 10), borderInsets, true),
                        new FocusBorder(
                        QuaquaBorderFactory.create(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.east.focusRing.png")),
                        new Insets(8, 4, 15, 10), borderInsets, false))),
                        2, 0, 2, 2, false, true, false, false);
            }
            b = toggleEastBorder;
        } else if (segpos.equals("middle") || style.equals("toggleCenter")) {
            if (toggleCenterBorder == null) {
                Insets borderInsets = new Insets(3, 1, 3, 1);
                toggleCenterBorder = new CompositeVisualMarginBorder(
                        new OverlayBorder(
                        new ButtonStateBorder(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.center.borders.png")),
                        10, true,
                        new Insets(8, 0, 15, 1), borderInsets, true),
                        new FocusBorder(
                        QuaquaBorderFactory.create(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.center.focusRing.png")),
                        new Insets(8, 4, 15, 4), borderInsets, false))),
                        2, 0, 2, 0, false, true, false, true);
            }
            b = toggleCenterBorder;
        } else if (segpos.equals("last") || style.equals("toggleWest")) {
            if (toggleWestBorder == null) {
                Insets borderInsets = new Insets(3, 5, 3, 1);
                toggleWestBorder = new CompositeVisualMarginBorder(
                        new OverlayBorder(
                        new ButtonStateBorder(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.west.borders.png")),
                        10, true,
                        new Insets(8, 10, 15, 1), borderInsets, true),
                        new FocusBorder(
                        QuaquaBorderFactory.create(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.west.focusRing.png")),
                        new Insets(8, 10, 15, 4), borderInsets, false))),
                        2, 2, 2, 0, false, false, false, true);
            }
            b = toggleWestBorder;
        } else if (style.equals("help")) {
            if (helpBorder == null) {
                helpBorder = new VisualMarginBorder(2, 3, 2, 3);
            }
            b = helpBorder;
            // Implicit styles
        } else if (c.getParent() instanceof JToolBar) {
            b = getSquareBorder();
        } else {
            switch (QuaquaUtilities.getSizeVariant(c)) {
                case SMALL:
                case MINI:
                    b = getSmallPushButtonBorder();
                    break;
                default:
                    b = getRegularPushButtonBorder();
                    break;
            }
        }
        if (b == null) {
            throw new InternalError(style);
        }
        return b;
    }

    private Border getRegularPushButtonBorder() {
        if (regularPushButtonBorder == null) {
            Insets borderInsets = new Insets(1, 5, 1, 5);
            BufferedImage[] imageFrames = Images.split(
                    Images.createImage(QuaquaButtonBorder.class.getResource("images/Button.default.png")),
                    12, true);
            Border[] borderFrames = new Border[12];
            for (int i = 0; i < 12; i++) {
                borderFrames[i] = QuaquaBorderFactory.create(
                        imageFrames[i],
                        new Insets(11, 13, 13, 13),
                        borderInsets,
                        true);
            }
            ButtonStateBorder buttonStateBorder = new ButtonStateBorder(
                    Images.split(
                    Images.createImage(QuaquaButtonBorder.class.getResource("images/Button.borders.png")),
                    10, true),
                    new Insets(11, 13, 13, 13),
                    borderInsets,
                    true);

            buttonStateBorder.setBorder(
                    ButtonStateBorder.DEFAULT,
                    new AnimatedBorder(borderFrames, 100));

            regularPushButtonBorder = new CompositeVisualMarginBorder(
                    new OverlayBorder(
                    buttonStateBorder,
                    new FocusBorder(
                    QuaquaBorderFactory.create(
                    Images.createImage(QuaquaButtonBorder.class.getResource("images/Button.focusRing.png")),
                    new Insets(12, 13, 12, 13),
                    borderInsets,
                    false))),
                    2, 4, 2, 4);
        }
        return regularPushButtonBorder;
    }

    private Border getSquareBorder() {
        if (squareBorder == null) {
            squareBorder = new CompositeVisualMarginBorder(
                    new OverlayBorder(
                    QuaquaBorderFactory.createSquareButtonBorder(),
                    new CompoundBorder(
                    new EmptyBorder(-2, -2, -2, -2),
                    new FocusBorder(
                    QuaquaBorderFactory.create(
                    Images.createImage(QuaquaButtonBorder.class.getResource("images/Square.focusRing.png")),
                    new Insets(10, 9, 10, 8), new Insets(6, 9, 6, 9), true)))),
                    0, 0, 0, 0) {

                @Override
                protected Insets getVisualMargin(Component c, Insets insets) {
                    String s = getStyle(c);

                    insets = super.getVisualMargin(c, new InsetsUIResource(0, 0, 0, 0));

                    if (insets instanceof javax.swing.plaf.UIResource) {
                        switch (getSegmentPosition(c)) {
                            case first:
                                insets.right = -1;
                                break;
                            case middle:
                                insets.left = 0;
                                insets.right = -1;
                                break;
                            case last:
                                insets.left = 0;
                                break;
                        }
                    }
                    return insets;
                }
            };
        }
        return squareBorder;
    }

    private Border getPlacardBorder() {
        if (placardBorder == null) {
            placardBorder = new CompositeVisualMarginBorder(
                    new OverlayBorder(
                    new CompoundBorder(
                    new EmptyBorder(-1, 0, -1, 0),
                    QuaquaBorderFactory.createPlacardButtonBorder()),
                    new CompoundBorder(
                    new EmptyBorder(-1, -1, -1, -1),
                    new FocusBorder(
                    QuaquaBorderFactory.create(
                    Images.createImage(QuaquaButtonBorder.class.getResource("images/Square.focusRing.png")),
                    new Insets(10, 9, 10, 8), new Insets(6, 9, 6, 9), true)))),
                    0, 0, 0, 0){

                @Override
                protected Insets getVisualMargin(Component c, Insets insets) {
                    String s = getStyle(c);

                    insets = super.getVisualMargin(c, new InsetsUIResource(0, 0, 0, 0));
        if (insets instanceof javax.swing.plaf.UIResource) {
            if (s.equals("gradient") && (c.getParent() instanceof JToolBar)) {
                String ts = (String) ((JToolBar) c.getParent()).getClientProperty("Quaqua.ToolBar.style");
                if (ts != null && (ts.equals("placard") || ts.equals("gradient"))) {
                    InsetsUtil.clear(insets);
                }
            }
        }

                    if (insets instanceof javax.swing.plaf.UIResource) {
                        switch (getSegmentPosition(c)) {
                            case first:
                                insets.right = -1;
                                break;
                            case middle:
                                insets.left = 0;
                                insets.right = -1;
                                break;
                            case last:
                                insets.left = 0;
                                break;
                        }
                    }
                    return insets;
                }
            };
        }
        return placardBorder;
    }

    private Border getTableHeaderBorder() {
        if (tableHeaderBorder == null) {
            tableHeaderBorder = new CompositeVisualMarginBorder(
                    new ButtonStateBorder(
                    Images.createImage(QuaquaButtonBorder.class.getResource("images/TableHeader.borders.png")),
                    4, true, new Insets(7, 1, 8, 1), new Insets(1, 2, 1, 2), true),
                    0, 0, 0, 0);
        }
        return tableHeaderBorder;
    }

    private Border getSmallPushButtonBorder() {
        if (smallPushButtonBorder == null) {
            Insets borderInsets = new Insets(3, 8, 3, 8);
            BufferedImage[] imageFrames = Images.split(
                    Images.createImage(QuaquaButtonBorder.class.getResource("images/Button.small.default.png")),
                    12, true);
            Border[] borderFrames = new Border[12];
            for (int i = 0; i < 12; i++) {
                borderFrames[i] = QuaquaBorderFactory.create(
                        imageFrames[i],
                        new Insets(9, 13, 12, 13),
                        borderInsets,
                        true);
            }
            ButtonStateBorder buttonStateBorder = new ButtonStateBorder(
                    Images.split(
                    Images.createImage(QuaquaButtonBorder.class.getResource("images/Button.small.borders.png")),
                    10, true),
                    new Insets(9, 13, 12, 13),
                    borderInsets,
                    true);
            buttonStateBorder.setBorder(
                    ButtonStateBorder.DEFAULT,
                    new AnimatedBorder(borderFrames, 100));

            smallPushButtonBorder = new CompositeVisualMarginBorder(
                    new CompoundBorder(
                    new EmptyBorder(-2, -3, -2, -3),
                    new OverlayBorder(
                    buttonStateBorder,
                    new FocusBorder(
                    QuaquaBorderFactory.create(
                    Images.createImage(QuaquaButtonBorder.class.getResource("images/Button.small.focusRing.png")),
                    new Insets(9, 14, 12, 14),
                    borderInsets,
                    false)))),
                    0, 0, 0, 0);
        }
        return smallPushButtonBorder;
    }

    /**
     * Returns the default button margin for the specified component.
     *
     * FIXME: We should not create a new Insets instance on each method call.
     */
    public Insets getDefaultMargin(JComponent c) {
        Insets margin = null;
        String style = getStyle(c);
        QuaquaUtilities.SizeVariant sizeVariant = QuaquaUtilities.getSizeVariant(c);
        boolean isSmall = sizeVariant == QuaquaUtilities.SizeVariant.SMALL //
                || sizeVariant == QuaquaUtilities.SizeVariant.MINI;


        // Explicitly chosen styles
        if (style.equals("text") || style.equals("push")) {
            if (isSmall) {
                margin = new Insets(1, 3, 1, 3);
            } else {
                margin = new Insets(1, 6, 2, 6);
            }
        } else if (style.equals("toolBar")) {
            margin = new Insets(0, 0, 0, 0);
        } else if (style.equals("toolBarRollover")) {
            margin = new Insets(0, 0, 0, 0);
        } else if (style.equals("toolBarTab")) {
            margin = new Insets(0, 0, 0, 0);
        } else if (style.equals("square")) {
            if (isSmall) {
                margin = new Insets(1, 6, 1, 6);
            } else {
                margin = new Insets(1, 6, 2, 6);
            }
        } else if (style.equals("gradient")) {
            if (isSmall) {
                margin = new Insets(1, 6, 1, 6);
            } else {
                margin = new Insets(1, 6, 2, 6);
            }
        } else if (style.equals("colorWell")) {
            if (isSmall) {
                margin = new Insets(1, 6, 1, 6);
            } else {
                margin = new Insets(1, 6, 2, 6);
            }
        } else if (style.equals("icon") || style.equals("bevel")) {
            if (isSmall) {
                margin = new Insets(1, 6, 1, 6);
            } else {
                margin = new Insets(1, 6, 2, 6);
            }
        } else if (style.equals("toggle")) {
            if (isSmall) {
                margin = new Insets(1, 5, 1, 5);
            } else {
                margin = new Insets(1, 5, 2, 5);
            }
        } else if (style.equals("toggleEast")) {
            if (isSmall) {
                margin = new Insets(1, 5, 1, 5);
            } else {
                margin = new Insets(1, 5, 2, 5);
            }
        } else if (style.equals("toggleCenter")) {
            if (isSmall) {
                margin = new Insets(1, 5, 1, 5);
            } else {
                margin = new Insets(1, 5, 2, 5);
            }
        } else if (style.equals("toggleWest")) {
            if (isSmall) {
                margin = new Insets(1, 5, 1, 5);
            } else {
                margin = new Insets(1, 5, 2, 5);
            }
        } else if (style.equals("help")) {
            margin = new Insets(0, 0, 0, 0);

            // Implicit styles
        } else if (c.getParent() instanceof JToolBar) {
            margin = new Insets(0, 0, 0, 0);
        } else {
            if (isSmall) {
                margin = new Insets(1, 4, 1, 4);
            } else {
                margin = new Insets(1, 8, 2, 8);
            }
        }
        return margin;
    }

    public boolean isFixedHeight(JComponent c) {
        String style = getStyle(c).toLowerCase();
        return style.equals("text") || style.equals("push") || style.startsWith("toggle");
    }

    protected String getStyle(Component c) {

        String s = null;
        JComponent jc = (c instanceof JComponent) ? (JComponent) c : null;
        if (jc != null) {
            s = (String) jc.getClientProperty("Quaqua.Button.style");
            if (s == null) {
                s = (String) jc.getClientProperty("JButton.buttonType");
            }
        }
        if (s == null) {
            if (c.getParent() instanceof JToolBar) {
                String tbs = (String) ((JToolBar) c.getParent()).getClientProperty("Quaqua.ToolBar.style");
                if (tbs != null && (tbs.equals("gradient") || tbs.equals("placard"))) {
                    s = "gradient";
                } else {
                    s = "toolBar";
                }
            }
        }
        if (s == null || s.equals("segmented") || s.equals("toggle")
                || s.equals("segmentedRoundRect") || s.equals("segmentedCapsule")
                || s.contains("segmentedTextured")) {
            String segmentPosition = jc == null ? null : (String) jc.getClientProperty("JButton.segmentPosition");
            if (segmentPosition != null) {
                if (segmentPosition.equals("first")) {
                    s = "toggleWest";
                } else if (segmentPosition.equals("middle")) {
                    s = "toggleCenter";
                } else if (segmentPosition.equals("last")) {
                    s = "toggleEast";
                }
            }
        }
        if (s == null) {
            s = defaultStyle;
        }
        
        // coerce synonyms
        if (s.equals("placard")||s.equals("segmentedGradient")) {
            s="gradient";
        }
        
        return s;
    }

    /**
     * Returns true, if this border has a visual cue for the pressed
     * state of the button.
     * If the border has no visual cue, then the ButtonUI has to provide
     * it by some other means.
     */
    public boolean hasPressedCue(JComponent c) {
        Border b = getActualBorder(c);
        boolean haspc;
        if (b instanceof PressedCueBorder) {
            haspc = ((PressedCueBorder) b).hasPressedCue(c);
        }
        haspc = b != toolBarBorder;
        return haspc;
    }

    public Insets getVisualMargin(Component c) {
        return ((VisualMargin) getActualBorder(c)).getVisualMargin(c);
    }

    /**
     * Returns true, if this border has a visual cue for the disabled
     * state of the button.
     * If the border has no visual cue, then the ButtonUI has to provide
     * it by some other means.
     * /
     * public boolean hasDisabledCue(JComponent c) {
     * return false;
     * }*/
    public Insets getBorderInsets(Component c) {
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            Insets insets = (Insets) jc.getClientProperty("Quaqua.Border.insets");
            if (insets != null) {
                return (Insets) insets.clone();
            }
        }

        boolean isBorderPainted = true;
        if (c instanceof AbstractButton) {
            isBorderPainted = ((AbstractButton) c).isBorderPainted();
        }
        Insets insets;
        if (!isBorderPainted) {
            insets = (Insets) UIManager.getInsets("Component.visualMargin").clone();
        } else {
            insets = getActualBorder((JComponent) c).getBorderInsets(c);
            if (c instanceof AbstractButton) {
                AbstractButton b = (AbstractButton) c;
                Insets margin = b.getMargin();
                if (margin == null || (margin instanceof UIResource)) {
                    margin = getDefaultMargin((JComponent) c);
                }
                if (margin != null) {
                    insets.top += margin.top;
                    insets.left += margin.left;
                    insets.bottom += margin.bottom;
                    insets.right += margin.right;
                }
            }
        }
        return insets;
    }

    public boolean isBorderOpaque() {
        return false;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        getActualBorder((JComponent) c).paintBorder(c, g, x, y, width, height);
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
}
