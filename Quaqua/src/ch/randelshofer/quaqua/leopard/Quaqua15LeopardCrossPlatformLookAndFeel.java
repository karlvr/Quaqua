/*
 * @(#)Quaqua15LeopardCrossPlatformLookAndFeel.java  
 *
 * Copyright (c) 2007-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.leopard;

import ch.randelshofer.quaqua.*;
import ch.randelshofer.quaqua.color.GradientColor;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import java.awt.*;

/**
 * The Quaqua15LeopardCrossPlatformLookAndFeel provides bug fixes and enhancements for Apple's
 * Aqua Look and Feel for Java 1.5 on Mac OS X 10.5 (Leopard).
 * <p>
 * The Quaqua Look and Feel can not be used on other platforms than Mac OS X.
 * <p>
 * <h3>Usage</h3>
 * Please use the <code>QuaquaManager</code> to activate this look and feel in
 * your application. Or use the generic <code>QuaquaLookAndFeel</code>. Both
 * are designed to autodetect the appropriate Quaqua Look and Feel
 * implementation for current Java VM.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Quaqua15LeopardCrossPlatformLookAndFeel extends Quaqua15LeopardLookAndFeel {

    /**
     * Creates a new instance.
     */
    public Quaqua15LeopardCrossPlatformLookAndFeel() {
        super(UIManager.getCrossPlatformLookAndFeelClassName());
    }

    /**
     * Return a one line description of this look and feel implementation,
     * e.g. "The CDE/Motif Look and Feel".   This string is intended for
     * the user, e.g. in the title of a window or in a ToolTip message.
     */
    @Override
    public String getDescription() {
        return "The Quaqua Leopard Cross Platform Look and Feel "
                + QuaquaManager.getVersion()
                + " for J2SE 5";
    }

    /*
    protected void initSystemColorDefaults(UIDefaults table) {
    super.initSystemColorDefaults(table);
    
    if (QuaquaManager.getDesign() != QuaquaManager.LEOPARD ||
    ! QuaquaManager.isOSX()) {
    boolean isBrushedMetal = isBrushedMetal();
    Object controlBackground = (isBrushedMetal) ? table.get("control") : new ColorUIResource(0xe8e8e8);
    Object toolBarBackground = (isBrushedMetal) ? table.get("ToolBar.background") : new ColorUIResource(0xe8e8e8);
    Object menuBackground = new ColorUIResource(0xffffff);
    Object menuHighlight = makeTextureColor(0x3471cf, pantherDir + "MenuBar.texture.S.png");
    
    Object[] uiDefaults = {
    "window", controlBackground, // Default color for the interior of windows
    "control", controlBackground, // Default color for controls (buttons, sliders, etc)
    "menu", menuBackground, // Default color for the interior of menus
    "menuHighlight", menuHighlight, // Default color for the interior of menus
    // Quaqua specific 'system' colors
    "listHighlight", table.get("textHighlight"), // List background color when selected
    "listHighlightText", table.get("textHighlightText"), // List color when selected
    "listHighlightBorder", new ColorUIResource(0x808080), // List color when selected
    
    };
    putDefaults(table, uiDefaults);
    }
    }*/
    @Override
    public boolean getSupportsWindowDecorations() {
        return true;
    }

    @Override
    protected boolean isUseScreenMenuBar() {
        return false;
    }

    @Override
    protected void initClassDefaults(UIDefaults table) {
        String originalPopupUI = table.getString("PopupMenuUI");

        super.initClassDefaults(table);
        String basicPrefix = "javax.swing.plaf.basic.Basic";
        String quaquaPrefix = "ch.randelshofer.quaqua.Quaqua";
        String quaquaJaguarPrefix = "ch.randelshofer.quaqua.jaguar.QuaquaJaguar";
        String quaquaPantherPrefix = "ch.randelshofer.quaqua.panther.QuaquaPanther";
        String quaquaLeopardPrefix = "ch.randelshofer.quaqua.leopard.QuaquaLeopard";

        // NOTE: Change code below, to override different
        // UI classes of the target look and feel.
        Object[] uiDefaults = {
            "SliderUI", quaquaPrefix + "SliderUI",
            "PopupMenuUI", quaquaPrefix + "PopupMenuUI",};
        putDefaults(table, uiDefaults);
    }

    @Override
    protected void initDesignDefaults(UIDefaults table) {
        super.initDesignDefaults(table);

        Color menuBackground = new ColorUIResource(0xffffff);
        Border menuBorder = new BorderUIResource.EmptyBorderUIResource(1, 1, 1, 1);
        GradientColor.UIResource menuSelectionBackground = new GradientColor.UIResource(0x3875d7, 0x5170f6, 0x1a43f3);

        Border rootPaneBorder = /*QuaquaManager.isOSX() || QuaquaManager.getOS() == QuaquaManager.DARWIN ?
                new BorderUIResource.EmptyBorderUIResource(0,0,0,0) :*/
                new BorderUIResource.LineBorderUIResource(new Color(0xa5a5a5));

        Border popupMenuBorder = new BorderUIResource.CompoundBorderUIResource(
                rootPaneBorder, BorderFactory.createMatteBorder(4, 0, 4, 0, menuBackground));

        Object rootPaneBackground = new UIDefaults.ProxyLazyValue(//
                "ch.randelshofer.quaqua.QuaquaRootPaneBackground",//
                new Object[]{0xa7a7a7, //
                    new int[]{0xdcdcdc, 0xc5c5c5, 0xafafaf, 0xafafaf, 0x969696}, //
                    new int[]{0xf1f1f1, 0xe9e9e9, 0xdfdfdf, 0xdfdfdf, 0xcfcfcf}//
                });

        Object[] uiDefaults = {
            "CheckBoxMenuItem.selectionBackground", menuSelectionBackground,
            "CheckBoxMenuItem.selectionForeground", new ColorUIResource(0xffffff),
            "CheckBoxMenuItem.background", menuBackground,
            "CheckBoxMenuItem.border", menuBorder,
            "ColorChooser.colorPickerMagnifier", makeBufferedImage(commonDir + "ColorChooser.colorPickerMagnifierPC.png"),
            "ColorChooser.colorPickerHotSpot", new UIDefaults.ProxyLazyValue("java.awt.Point", new Object[]{22, 22}),
            "ColorChooser.colorPickerGlassRect", new UIDefaults.ProxyLazyValue("java.awt.Rectangle", new Object[]{1, 1, 21, 21}),
            // Pick point relative to hot spot
            "ColorChooser.colorPickerPickOffset", new UIDefaults.ProxyLazyValue("java.awt.Point", new Object[]{-10, -10}),
            "ColorChooser.colorPickerCaptureRect", new UIDefaults.ProxyLazyValue("java.awt.Rectangle", new Object[]{-13, -13, 8, 8}),
            "ColorChooser.colorPickerZoomRect", new UIDefaults.ProxyLazyValue("java.awt.Rectangle", new Object[]{2, 2, 24, 24}),
            "ComboBox.popupBorder", popupMenuBorder,
            //
            // Set this to true, to sort files by type instead of by name
            "FileChooser.orderByType", Boolean.FALSE,
            "FileChooser.previewLabelForeground", new ColorUIResource(0x808080),
            "FileChooser.previewValueForeground", new ColorUIResource(0x000000),
            "FileChooser.previewLabelInsets", new InsetsUIResource(1, 0, 0, 4),
            "FileChooser.previewLabelDelimiter", "",
            "FileChooser.browserUseUnselectedExpandIconForLabeledFile", Boolean.TRUE,
            //
            //
            "Menu.submenuPopupOffsetY", -5,
            "Menu.selectionBackground", menuSelectionBackground,
            "Menu.selectionForeground", new ColorUIResource(0xffffff),
            "Menu.background", menuBackground,
            "Menu.border", menuBorder,
            //
            "MenuItem.selectionBackground", menuSelectionBackground,
            "MenuItem.selectionForeground", new ColorUIResource(0xffffff),
            "MenuItem.border", menuBorder,
            "MenuItem.background", menuBackground,
            //
            "RadioButtonMenuItem.selectionBackground", menuSelectionBackground,
            "RadioButtonMenuItem.selectionForeground", new ColorUIResource(0xffffff),
            "RadioButtonMenuItem.background", menuBackground,
            "RadioButtonMenuItem.border", menuBorder,
            //
            "PopupMenu.border", popupMenuBorder,
            "PopupMenu.background", menuBackground,
            "PopupMenu.enableHeavyWeightPopup", true,
            //
            "RootPane.frameBorder", rootPaneBorder,
            "RootPane.plainDialogBorder", rootPaneBorder,
            "RootPane.informationDialogBorder", rootPaneBorder,
            "RootPane.errorDialogBorder", rootPaneBorder,
            "RootPane.colorChooserDialogBorder", rootPaneBorder,
            "RootPane.fileChooserDialogBorder", rootPaneBorder,
            "RootPane.questionDialogBorder", rootPaneBorder,
            "RootPane.warningDialogBorder", rootPaneBorder,
            //
            "Sheet.border", rootPaneBorder,
            //
            "ToolBar.title.background", rootPaneBackground,};
        putDefaults(table, uiDefaults);
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
        return "Quaqua Leopard Cross Platform";
    }

    /** Installs the QuaquaPopupFactory if the PopupMenuUI is included. */
    @Override
    protected void installPopupFactory() {
        // Regression for issue 132: Installing QuaquaPopupFactory,
        // causes popups to appear behind dialog windows if one of the window
        // ancestors has "alwaysOnTop" set to true.


        try {
            PopupFactory.setSharedInstance(new QuaquaPopupFactory());
        } catch (SecurityException ex) {
            System.err.print("Warning: " + this + " couldn't install QuaquaPopupFactory.");
            //ex.printStackTrace();
        }
    }
}
