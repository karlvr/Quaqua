/*
 * @(#)QuaquaButtonBorder.java  
 *
 * Copyright (c) 2005-2009 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.border.OverlayBorder;
import ch.randelshofer.quaqua.border.FocusBorder;
import ch.randelshofer.quaqua.border.ButtonStateBorder;
import ch.randelshofer.quaqua.border.AnimatedBorder;
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
public class QuaquaButtonBorder implements Border, UIResource {
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

    public Border getBorder(JComponent c) {
        Border b = null;
        String style = getStyle(c);

        // Explicitly chosen styles
        if (style.equals("text") || style.equals("push")) {
            if (c.getFont().getSize() <= 11) {
                b = getSmallPushButtonBorder();
            } else {
                b = getRegularPushButtonBorder();
            }
        } else if (style.equals("toolBar")) {
            if (toolBarBorder == null) {
                toolBarBorder = new CompoundBorder(
                        new EmptyBorder(-1, -1, -1, -2),
                        new QuaquaToolBarButtonStateBorder(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.borders.png")),
                        10, true,
                        new Insets(8, 10, 15, 10), new Insets(4, 6, 4, 6), true, false));
            }
            b = toolBarBorder;
        } else if (style.equals("toolBarRollover")) {
            if (toolBarRolloverBorder == null) {
                toolBarRolloverBorder = new CompoundBorder(
                        new EmptyBorder(-1, -1, -1, -2),
                        new QuaquaToolBarButtonStateBorder(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.borders.png")),
                        10, true,
                        new Insets(8, 10, 15, 10), new Insets(4, 6, 4, 6), true, true));
            }
            b = toolBarRolloverBorder;
        } else if (style.equals("toolBarTab")) {
            if (toolBarTabBorder == null) {
                toolBarTabBorder = new QuaquaToolBarTabButtonBorder();
            }
            b = toolBarTabBorder;
        } else if (style.equals("square") || style.equals("toolbar")) {
            b = getSquareBorder();
        } else if (style.equals("placard") || style.equals("gradient")) {
            b = getPlacardBorder();
        } else if (style.equals("tableHeader")) {
            b = getTableHeaderBorder();
        } else if (style.equals("colorWell")) {
            if (colorWellBorder == null) {
                colorWellBorder = new CompoundBorder(
                        new VisualMargin(0, 0, 0, 0),
                        new OverlayBorder(
                        //new CompoundBorder(
                        //new EmptyBorder(3,3,3,3),
                        new QuaquaColorWellBorder() /*)*/,
                        new CompoundBorder(
                        new EmptyBorder(-2, -2, -2, -2),
                        new FocusBorder(
                        QuaquaBorderFactory.create(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Square.focusRing.png")),
                        new Insets(10, 9, 10, 8), new Insets(6, 9, 6, 9), true)))));
            }
            b = colorWellBorder;
        } else if (style.equals("icon") || style.equals("bevel")) {
            if (bevelBorder == null) {
                Insets borderInsets = new Insets(4, 3, 3, 3);
                Border focusBorder = new FocusBorder(
                        QuaquaBorderFactory.create(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/RoundedBevel.focusRing.png")),
                        new Insets(10, 9, 10, 8), borderInsets, true));

                bevelBorder = new CompoundBorder(
                        new VisualMargin(0, 0, 0, 0),
                        new CompoundBorder(
                        new EmptyBorder(-3, -2, -2, -2),
                        new OverlayBorder(
                        new ButtonStateBorder(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/RoundedBevel.borders.png")),
                        10, true,
                        new Insets(10, 9, 10, 8), borderInsets, true),
                        new CompoundBorder(
                        new EmptyBorder(0, -1, 0, -1),
                        focusBorder))));
            }
            b = bevelBorder;
        } else if (style.equals("toggle") || style.equals("segmented")) {
            if (toggleBorder == null) {
                Insets borderInsets = new Insets(3, 5, 3, 5);
                toggleBorder = new CompoundBorder(
                        new VisualMargin(2, 2, 2, 2),
                        new OverlayBorder(
                        new ButtonStateBorder(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.borders.png")),
                        10, true,
                        new Insets(8, 10, 15, 10), borderInsets, true),
                        new FocusBorder(
                        QuaquaBorderFactory.create(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.focusRing.png")),
                        new Insets(8, 10, 15, 10), borderInsets, false))));
            }
            b = toggleBorder;
        } else if (style.equals("toggleEast")) {
            if (toggleEastBorder == null) {
                VisualMargin cm;
                Insets borderInsets = new Insets(3, 1, 3, 5);
                toggleEastBorder = new CompoundBorder(
                        cm = new VisualMargin(2, 0, 2, 2),
                        new OverlayBorder(
                        new ButtonStateBorder(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.east.borders.png")),
                        10, true,
                        new Insets(8, 1, 15, 10), borderInsets, true),
                        new FocusBorder(
                        QuaquaBorderFactory.create(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.east.focusRing.png")),
                        new Insets(8, 4, 15, 10), borderInsets, false))));
                cm.setFixed(false, true, false, false);
            }
            b = toggleEastBorder;
        } else if (style.equals("toggleCenter")) {
            if (toggleCenterBorder == null) {
                VisualMargin cm;
                Insets borderInsets = new Insets(3, 1, 3, 1);
                toggleCenterBorder = new CompoundBorder(
                        cm = new VisualMargin(2, 0, 2, 0),
                        new OverlayBorder(
                        new ButtonStateBorder(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.center.borders.png")),
                        10, true,
                        new Insets(8, 0, 15, 1), borderInsets, true),
                        new FocusBorder(
                        QuaquaBorderFactory.create(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.center.focusRing.png")),
                        new Insets(8, 4, 15, 4), borderInsets, false))));
                cm.setFixed(false, true, false, true);
            }
            b = toggleCenterBorder;
        } else if (style.equals("toggleWest")) {
            if (toggleWestBorder == null) {
                VisualMargin cm;
                Insets borderInsets = new Insets(3, 5, 3, 1);
                toggleWestBorder = new CompoundBorder(
                        cm = new VisualMargin(2, 2, 2, 0),
                        new OverlayBorder(
                        new ButtonStateBorder(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.west.borders.png")),
                        10, true,
                        new Insets(8, 10, 15, 1), borderInsets, true),
                        new FocusBorder(
                        QuaquaBorderFactory.create(
                        Images.createImage(QuaquaButtonBorder.class.getResource("images/Toggle.west.focusRing.png")),
                        new Insets(8, 10, 15, 4), borderInsets, false))));
                cm.setFixed(false, false, false, true);
            }
            b = toggleWestBorder;
        } else if (style.equals("help")) {
            if (helpBorder == null) {
                helpBorder = new VisualMargin(2, 3, 2, 3);
            }
            b = helpBorder;
        // Implicit styles
        } else if (c.getParent() instanceof JToolBar) {
            b = getSquareBorder();
        } else {
            if (c.getFont().getSize() <= 11) {
                b = getSmallPushButtonBorder();
            } else {
                b = getRegularPushButtonBorder();
            }
        }
        if (b == null) {
            throw new InternalError(style);
        }
        return b;
    }

    private Border getRegularPushButtonBorder() {
        if (regularPushButtonBorder == null) {
            Insets borderInsets = new Insets(3, 8, 3, 8);
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

            regularPushButtonBorder = new CompoundBorder(
                    //new VisualMargin(2, 3, 2, 3),
                    new VisualMargin(0, 0, 0, 0),
                    new CompoundBorder(
                    new EmptyBorder(-2, -4, -2, -4),
                    new OverlayBorder(
                    buttonStateBorder,
                    new FocusBorder(
                    QuaquaBorderFactory.create(
                    Images.createImage(QuaquaButtonBorder.class.getResource("images/Button.focusRing.png")),
                    new Insets(12, 13, 12, 13),
                    borderInsets,
                    false)))));
        }
        return regularPushButtonBorder;
    }

    private Border getSquareBorder() {
        if (squareBorder == null) {
            squareBorder = new CompoundBorder(
                    new VisualMargin(0, 0, 0, 0),
                    new OverlayBorder(
                    QuaquaBorderFactory.createSquareButtonBorder(),
                    new CompoundBorder(
                    new EmptyBorder(-2, -2, -2, -2),
                    new FocusBorder(
                    QuaquaBorderFactory.create(
                    Images.createImage(QuaquaButtonBorder.class.getResource("images/Square.focusRing.png")),
                    new Insets(10, 9, 10, 8), new Insets(6, 9, 6, 9), true)))));
        }
        return squareBorder;
    }

    private Border getPlacardBorder() {
        if (placardBorder == null) {
            placardBorder = new CompoundBorder(
                    new VisualMargin(0, 0, 0, 0),
                    new OverlayBorder(
                    new CompoundBorder(
                    new EmptyBorder(-1, 0, -1, 0),
                    QuaquaBorderFactory.createPlacardButtonBorder()),
                    new CompoundBorder(
                    new EmptyBorder(-1, -1, -1, -1),
                    new FocusBorder(
                    QuaquaBorderFactory.create(
                    Images.createImage(QuaquaButtonBorder.class.getResource("images/Square.focusRing.png")),
                    new Insets(10, 9, 10, 8), new Insets(6, 9, 6, 9), true)))));
        }
        return placardBorder;
    }

    private Border getTableHeaderBorder() {
        if (tableHeaderBorder == null) {
            tableHeaderBorder = new ButtonStateBorder(
                    Images.createImage(QuaquaButtonBorder.class.getResource("images/TableHeader.borders.png")),
                    4, true, new Insets(7, 1, 8, 1), new Insets(1, 2, 1, 2), true);
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

            smallPushButtonBorder = new CompoundBorder(
                    //new VisualMargin(2, 2, 2, 2),
                    new VisualMargin(0, 0, 0, 0),
                    new CompoundBorder(
                    new EmptyBorder(-2, -3, -2, -3),
                    new OverlayBorder(
                    buttonStateBorder,
                    new FocusBorder(
                    QuaquaBorderFactory.create(
                    Images.createImage(QuaquaButtonBorder.class.getResource("images/Button.small.focusRing.png")),
                    new Insets(9, 14, 12, 14),
                    borderInsets,
                    false)))));
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
        boolean isSmall = QuaquaUtilities.isSmallSizeVariant(c);

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
        } else if (style.equals("placard") || style.equals("gradient")) {
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

    protected String getStyle(JComponent c) {
        String style = (String) c.getClientProperty("Quaqua.Button.style");
        if (style == null) {
            style = (String) c.getClientProperty("JButton.buttonType");
        }
        if (style == null || style.equals("segmented") || style.equals("toggle")) {
            String segmentPosition = (String) c.getClientProperty("JButton.segmentPosition");
            if (segmentPosition != null) {
                if (segmentPosition.equals("first")) {
                    style = "toggleWest";
                } else if (segmentPosition.equals("middle")) {
                    style = "toggleCenter";
                } else if (segmentPosition.equals("last")) {
                    style = "toggleEast";
                }
            }
        }
        if (style == null) {
            style = defaultStyle;
        }
        return style;
    }

    /**
     * Returns true, if this border has a visual cue for the pressed
     * state of the button.
     * If the border has no visual cue, then the ButtonUI has to provide
     * it by some other means.
     */
    public boolean hasPressedCue(JComponent c) {
        Border b = getBorder(c);
        return b != toolBarBorder;
    }

    public Insets getVisualMargin(Component c) {
        Border b = getBorder((JComponent) c);
        Insets visualMargin = new Insets(0, 0, 0, 0);

        if (b instanceof VisualMargin) {
            visualMargin = ((VisualMargin) b).getVisualMargin(c);
        } else if (b instanceof CompoundBorder) {
            b = ((CompoundBorder) b).getOutsideBorder();
            if (b instanceof VisualMargin) {
                visualMargin = ((VisualMargin) b).getVisualMargin(c);
            }
        }
        return visualMargin;
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
            insets = getBorder((JComponent) c).getBorderInsets(c);
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
        getBorder((JComponent) c).paintBorder(c, g, x, y, width, height);
    }
}
