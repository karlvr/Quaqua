/*
 * @(#)Quaqua16LeopardLookAndFeel.java  1.0  2008-09-01
 *
 * Copyright (c) 2008-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.leopard;

import ch.randelshofer.quaqua.QuaquaLayoutStyle;
import ch.randelshofer.quaqua.color.GradientColor;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.LayoutStyle;
import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;

/**
 * The Quaqua16LeopardLookAndFeel provides bug fixes and enhancements for Apple's
 * Aqua Look and Feel for Java 1.6 on Mac OS X 10.5 (Leopard).
 * <p>
 * The Quaqua Look and Feel can not be used on other platforms than Mac OS X.
 * <p>
 * <h3>Usage</h3>
 * Please use the <code>QuaquaManager</code> to activate this look and feel in
 * your application. Or use the generic <code>QuaquaLookAndFeel</code>. Both
 * are designed to autodetect the appropriate Quaqua Look and Feel
 * implementation.
 * <p>
 * 
 * @author Werner Randelshofer
 * @version 1.0 2008-09-01 Created. 
 */
public class Quaqua16LeopardLookAndFeel extends Quaqua15LeopardLookAndFeel {

    private LayoutStyle layoutStyle;

    @Override
    public LayoutStyle getLayoutStyle() {
        if (layoutStyle == null) {
            layoutStyle = new QuaquaLayoutStyle();
        }
        return layoutStyle;
    }

    @Override
    protected void initDesignDefaults(UIDefaults table) {
        super.initDesignDefaults(table);

        ColorUIResource menuSelectionForeground = new ColorUIResource(0xffffff);
        Object[] uiDefaults;

        // FIXME Implement a screen menu bar by myself. We lose too many features here.
        if (isUseScreenMenuBar()) {
            uiDefaults = new Object[]{
                        "CheckBoxMenuItem.checkIcon", makeButtonStateIcon(commonDir + "CheckBoxMenuItem.icons.png", 6, new Rectangle(1, 0, 12, 12)),
                        "CheckBoxMenuItem.border", new BorderUIResource.EmptyBorderUIResource(0, 5, 2, 0),
                        "CheckBoxMenuItem.margin", new InsetsUIResource(0, 8, 0, 8),
                        "Menu.checkIcon", makeIcon(getClass(), commonDir + "MenuItem.checkIcon.png", new Point(1, 0)),
                        "Menu.arrowIcon", makeButtonStateIcon(commonDir + "MenuItem.arrowIcons.png", 2, new Rectangle(-6, 1, 6, 12)),
                        "Menu.border", new BorderUIResource.EmptyBorderUIResource(0, 5, 2, 4),
                        "Menu.margin", new InsetsUIResource(0, 8, 0, 8),
                        "Menu.menuPopupOffsetX", 0,
                        "Menu.menuPopupOffsetY", 1,
                        "Menu.submenuPopupOffsetX", 0,
                        "Menu.submenuPopupOffsetY", -4,
                        "MenuItem.checkIcon", makeIcon(getClass(), commonDir + "MenuItem.checkIcon.png", new Point(1, 0)),
                        "MenuItem.border", new BorderUIResource.EmptyBorderUIResource(0, 5, 2, 0),
                        "RadioButtonMenuItem.checkIcon", makeButtonStateIcon(commonDir + "RadioButtonMenuItem.icons.png", 6, new Rectangle(0, 0, 12, 12)),
                        "RadioButtonMenuItem.border", new BorderUIResource.EmptyBorderUIResource(0, 5, 2, 0),
                        "RadioButtonMenuItem.margin", new InsetsUIResource(0, 8, 0, 8), //
                    };
        } else {
            Border menuBorder = new BorderUIResource.EmptyBorderUIResource(1, 1, 1, 1);
            GradientColor.UIResource menuSelectionBackground = new GradientColor.UIResource(0x4b69ea, 0x5170f6, 0x1a43f3);
            uiDefaults = new Object[]{
                        "CheckBoxMenuItem.checkIcon", makeButtonStateIcon(commonDir + "CheckBoxMenuItem.icons.png", 6, new Point(0, 1)),
                        "CheckBoxMenuItem.border", menuBorder,
                        "CheckBoxMenuItem.selectionBackground", menuSelectionBackground,
                        "Menu.checkIcon", makeIcon(getClass(), commonDir + "MenuItem.checkIcon.png"),
                        "Menu.arrowIcon", makeButtonStateIcon(commonDir + "MenuItem.arrowIcons.png", 2, new Point(0, 1)),
                        "Menu.margin", new InsetsUIResource(0, 5, 0, 8),
                        "Menu.menuPopupOffsetX", 0,
                        "Menu.menuPopupOffsetY", 0,
                        "Menu.submenuPopupOffsetX", 0,
                        "Menu.submenuPopupOffsetY", -4,
                        "Menu.useMenuBarBackgroundForTopLevel", Boolean.TRUE,
                        "Menu.border", menuBorder,
                        "Menu.selectionBackground", menuSelectionBackground,
                        //"MenuBar.background", new TextureColorUIResource(0xf4f4f4, getClass().getResource(pantherDir+"MenuBar.texture.png")),
                        //"MenuBar.border", new BorderUIResource.MatteBorderUIResource(0,0,1,0,new Color(128,128,128)),
                        "MenuBar.border", makeImageBevelBackgroundBorder(tigerDir + "MenuBar.border.png", new Insets(10, 0, 11, 0), new Insets(0, 0, 0, 0), true),
                        "MenuBar.selectedBorder", makeImageBevelBackgroundBorder(tigerDir + "MenuBar.selectedBorder.png", new Insets(1, 0, 20, 0), new Insets(0, 0, 0, 0), true),
                        "MenuBar.margin", new InsetsUIResource(1, 8, 2, 8),
                        "MenuBar.shadow", null,
                        "MenuItem.acceleratorSelectionForeground", menuSelectionForeground,
                        "MenuItem.checkIcon", makeIcon(getClass(), commonDir + "MenuItem.checkIcon.png"),
                        "MenuItem.border", menuBorder,
                        "MenuItem.selectionBackground", menuSelectionBackground,
                        "RadioButtonMenuItem.checkIcon", makeButtonStateIcon(commonDir + "RadioButtonMenuItem.icons.png", 6),
                        "RadioButtonMenuItem.border", menuBorder,
                        "RadioButtonMenuItem.selectionBackground", menuSelectionBackground, //
                    };
        }
        putDefaults(table, uiDefaults);

    }
}
