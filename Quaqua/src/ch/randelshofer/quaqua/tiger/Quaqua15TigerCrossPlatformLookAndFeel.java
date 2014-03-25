/*
 * @(#)Quaqua15TigerCrossPlatformLookAndFeel.java 
 *
 * Copyright (c) 2007-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.tiger;

import ch.randelshofer.quaqua.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import java.awt.*;

/**
 * The Quaqua15TigerCrossPlatformLookAndFeel provides bug fixes and enhancements for Apple's
 * Aqua Look and Feel for Java 1.4 on Mac OS X 10.4 (Tiger).
 * <p>
 * The Quaqua Look and Feel can not be used on other platforms than Mac OS X.
 * <p>
 * <h3>Usage</h3>
 * Please use the <code>QuaquaManager</code> to activate this look and feel in
 * your application. Or use the generic <code>QuaquaLookAndFeel</code>. Both
 * are designed to autodetect the appropriate Quaqua Look and Feel
 * implementation for current Java VM.
 * <p>
 * @see QuaquaManager
 * @see QuaquaLookAndFeel
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Quaqua15TigerCrossPlatformLookAndFeel extends Quaqua15TigerLookAndFeel {

    /**
     * Creates a new instance.
     */
    public Quaqua15TigerCrossPlatformLookAndFeel() {
        super(UIManager.getCrossPlatformLookAndFeelClassName());
    }

    /**
     * Return a one line description of this look and feel implementation,
     * e.g. "The CDE/Motif Look and Feel".   This string is intended for
     * the user, e.g. in the title of a window or in a ToolTip message.
     */
    @Override
    public String getDescription() {
        return "The Quaqua Tiger Cross Platform Look and Feel "
                + QuaquaManager.getVersion()
                + " for J2SE 5";
    }

    @Override
    protected void initSystemColorDefaults(UIDefaults table) {
        super.initSystemColorDefaults(table);

        if (!QuaquaManager.isOSX()
                && QuaquaManager.getOS() != QuaquaManager.DARWIN) {
            boolean isBrushedMetal = isBrushedMetal();
            Object controlBackground = (isBrushedMetal)
                    ? table.get("control")
                    : makeTextureColor(0xf4f4f4, pantherDir + "Panel.texture.png");

            Object menuBackground = (isBrushedMetal)
                    ? table.get("menu")
                    : makeTextureColor(0xf4f4f4, pantherDir + "MenuBar.texture.png");
            Object menuHighlight = makeTextureColor(0x3471cf, pantherDir + "MenuBar.texture.S.png");

            Object[] uiDefaults = {
                "window", controlBackground, /* Default color for the interior of windows */
                "control", controlBackground, /* Default color for controls (buttons, sliders, etc) */
                "menu", menuBackground, /* Default color for the interior of menus */
                "menuHighlight", menuHighlight, /* Default color for the interior of menus */
                // Quaqua specific 'system' colors
                "listHighlight", table.get("textHighlight"), /* List background color when selected */
                "listHighlightText", table.get("textHighlightText"), /* List color when selected */
                "listHighlightBorder", new ColorUIResource(0x808080), /* List color when selected */};
            table.putDefaults(uiDefaults);
        }

    }

    @Override
    public boolean getSupportsWindowDecorations() {
        return true;
    }

    @Override
    protected boolean isUseScreenMenuBar() {
        return false;
    }

    @Override
    protected void initDesignDefaults(UIDefaults table) {
        super.initDesignDefaults(table);

        Object menuBackground = makeTextureColor(0xf4f4f4, pantherDir + "MenuBar.texture.png");
        Color menuBackgroundColor = new Color(0xf4f4f4);

        Border rootPaneBorder = new BorderUIResource.LineBorderUIResource(new Color(0xa5a5a5));
        Border popupMenuBorder = new BorderUIResource.CompoundBorderUIResource(
                rootPaneBorder, BorderFactory.createMatteBorder(4, 0, 4, 0, menuBackgroundColor));


        Object[] uiDefaults = {
            // Set this to true, to sort files by type instead of by name
            "FileChooser.orderByType", Boolean.FALSE,
            "FileChooser.previewLabelForeground", new ColorUIResource(0x808080),
            "FileChooser.previewValueForeground", new ColorUIResource(0x000000),
            "FileChooser.previewLabelInsets", new InsetsUIResource(1, 0, 0, 4),
            "FileChooser.previewLabelDelimiter", "",
            "FileChooser.browserUseUnselectedExpandIconForLabeledFile", Boolean.TRUE,
            "CheckBoxMenuItem.selectionBackground", new ColorUIResource(0x3875d7),
            "CheckBoxMenuItem.selectionForeground", new ColorUIResource(0xffffff),
            "CheckBoxMenuItem.background", menuBackground,
            "ColorChooser.colorPickerMagnifier", makeBufferedImage(commonDir + "ColorChooser.colorPickerMagnifierPC.png"),
            "ColorChooser.colorPickerHotSpot", new UIDefaults.ProxyLazyValue("java.awt.Point", new Object[]{22, 22}),
            "ColorChooser.colorPickerGlassRect", new UIDefaults.ProxyLazyValue("java.awt.Rectangle", new Object[]{1, 1, 21, 21}),
            // Pick point relative to hot spot
            "ColorChooser.colorPickerPickOffset", new UIDefaults.ProxyLazyValue("java.awt.Point", new Object[]{-10, -10}),
            "ColorChooser.colorPickerCaptureRect", new UIDefaults.ProxyLazyValue("java.awt.Rectangle", new Object[]{-13, -13, 8, 8}),
            "ColorChooser.colorPickerZoomRect", new UIDefaults.ProxyLazyValue("java.awt.Rectangle", new Object[]{2, 2, 24, 24}),
            "ComboBox.popupBorder", popupMenuBorder,
            "Menu.submenuPopupOffsetY", -5,
            "Menu.selectionBackground", new ColorUIResource(0x3875d7),
            "Menu.selectionForeground", new ColorUIResource(0xffffff),
            "Menu.background", menuBackground,
            "MenuItem.selectionBackground", new ColorUIResource(0x3875d7),
            "MenuItem.selectionForeground", new ColorUIResource(0xffffff),
            "MenuItem.background", menuBackground,
            "PopupMenu.border", popupMenuBorder,
            "PopupMenu.background", menuBackground,
            "RadioButtonMenuItem.selectionBackground", new ColorUIResource(0x3875d7),
            "RadioButtonMenuItem.selectionForeground", new ColorUIResource(0xffffff),
            "RadioButtonMenuItem.background", menuBackground,
            "RootPane.frameBorder", rootPaneBorder,
            "RootPane.plainDialogBorder", rootPaneBorder,
            "RootPane.informationDialogBorder", rootPaneBorder,
            "RootPane.errorDialogBorder", rootPaneBorder,
            "RootPane.colorChooserDialogBorder", rootPaneBorder,
            "RootPane.fileChooserDialogBorder", rootPaneBorder,
            "RootPane.questionDialogBorder", rootPaneBorder,
            "RootPane.warningDialogBorder", rootPaneBorder,
            "Sheet.border", rootPaneBorder,};
        table.putDefaults(uiDefaults);
    }

    /**
     * Return a short string that identifies this look and feel, e.g.
     * "CDE/Motif".  This string should be appropriate for a menu item.
     * Distinct look and feels should have different names, e.g.
     * a subclass of MotifLookAndFeel that changes the way a few components
     * are rendered should be called "CDE/Motif My Way"; something
     * that would be useful to a user trying to select a L&amp;F from a list
     * of names.
     */
    @Override
    public String getName() {
        return "Quaqua Tiger Cross Platform";
    }
}


