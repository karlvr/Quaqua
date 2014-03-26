/*
 * @(#)GroupBox.java
 *
 * Copyright (c) 2005-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.quaqua.util;

import ch.randelshofer.quaqua.*;
import ch.randelshofer.quaqua.border.BackgroundBorder;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
/**
 * GroupBox draws a Aqua-style group box similar to a native Cocoa NSBox.
 * XXX - This class should go away. We can easily get the same functionality
 * by instantiating one of the existing Quaqua border classes.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class GroupBox implements BackgroundBorder {
    private static Border etchedBorder = new EtchedBorder();

    private static final int IMAGE_STRIP = 7;
    private static final int EXTRA_TOP = 3; // the height of the image that is above the nominal inside area
    private static final int EXTRA_LEFT = 3;
    private static final int EXTRA_BOTTOM = 3;
    private static final int EXTRA_RIGHT = 3;

    private Insets minInsets = new Insets(EXTRA_TOP, EXTRA_LEFT, EXTRA_BOTTOM, EXTRA_RIGHT);
    private Insets s = minInsets;
    private Border boxBorder;

    public Border getBackgroundBorder() {
        if (boxBorder == null) {
            boxBorder = createBorder();
        }
        return boxBorder;
    }

    public Insets getBorderInsets(Component c) {
        return s;
    }

    public void setInsets(Insets s) {
        this.s = s != null ? ensureMinimum(s) : minInsets;
        boxBorder = null;
    }

    private Insets ensureMinimum(Insets s) {
        return new Insets(Math.max(EXTRA_TOP, s.top),
                Math.max(EXTRA_LEFT, s.left),
                Math.max(EXTRA_BOTTOM, s.bottom),
                Math.max(EXTRA_RIGHT, s.right));
    }

    public boolean isBorderOpaque() {
        return false;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        // Components that don't have a UI can't deal with BackgroundBorder
        // interface. As a work around, we paint a Etched border.
        if ((c instanceof Box) || ! (c instanceof JComponent)) {
            etchedBorder.paintBorder(c, g, x, y, width, height);
        }
    }

    private Border createBorder() {
        int design = QuaquaManager.getDesign();
        String rs = design >= QuaquaManager.MAVERICKS ? "/ch/randelshofer/quaqua/images/GroupBoxMavericks.png" : "/ch/randelshofer/quaqua/images/GroupBox.png";
        Color fill = design >= QuaquaManager.MAVERICKS ? new Color(0x0A000000,true) : new Color(0x08000000,true);
        return new CompoundBorder(
                new EmptyBorder(s.top-EXTRA_TOP, s.left-EXTRA_LEFT, s.bottom-EXTRA_BOTTOM, s.right-EXTRA_RIGHT),
                QuaquaBorderFactory.create(
                        Toolkit.getDefaultToolkit().createImage(GroupBox.class.getResource(rs)),
                        new Insets(IMAGE_STRIP,IMAGE_STRIP,IMAGE_STRIP,IMAGE_STRIP),
                        new Insets(IMAGE_STRIP,IMAGE_STRIP,IMAGE_STRIP,IMAGE_STRIP),
                        true, fill, false
                ));
    }
}
