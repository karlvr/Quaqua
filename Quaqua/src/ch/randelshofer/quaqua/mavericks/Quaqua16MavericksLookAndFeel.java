/*
 * Copyright (c) 2014 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement. For details see accompanying license terms.
 */
package ch.randelshofer.quaqua.mavericks;

import ch.randelshofer.quaqua.QuaquaManager;
import ch.randelshofer.quaqua.mountainlion.Quaqua16MountainLionLookAndFeel;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

/**
 * Quaqua look and feel for Mavericks.
 */
public class Quaqua16MavericksLookAndFeel extends Quaqua16MountainLionLookAndFeel {

	public Quaqua16MavericksLookAndFeel() {
		super();
	}

	protected Quaqua16MavericksLookAndFeel(String className) {
		super(className);
	}

    @Override
    public String getDescription() {
        return "The Quaqua Mavericks Look and Feel "
                + QuaquaManager.getVersion();
    }

    @Override
    public String getName() {
        return "Quaqua Mavericks";
    }
    
    @Override
    protected void initClassDefaults(UIDefaults table) {
        String basicPrefix = "javax.swing.plaf.basic.Basic";
        String quaquaPrefix = "ch.randelshofer.quaqua.Quaqua";
        String quaquaJaguarPrefix = "ch.randelshofer.quaqua.jaguar.QuaquaJaguar";
        String quaquaPantherPrefix = "ch.randelshofer.quaqua.panther.QuaquaPanther";
        String quaquaLeopardPrefix = "ch.randelshofer.quaqua.leopard.QuaquaLeopard";
        String quaquaLionPrefix = "ch.randelshofer.quaqua.lion.QuaquaLion";
        String quaquaMavericksPrefix = "ch.randelshofer.quaqua.mavericks.QuaquaMavericks";

        // NOTE: Change code below, to override different
        // UI classes of the target look and feel.
        Object[] uiDefaults = {
            "BrowserUI", quaquaPrefix + "BrowserUI",
            "ButtonUI", quaquaPrefix + "ButtonUI",
            "CheckBoxUI", quaquaPrefix + "CheckBoxUI",
            "ColorChooserUI", quaquaPrefix + "ColorChooserUI",
            "FileChooserUI", quaquaMavericksPrefix + "FileChooserUI",
            "FormattedTextFieldUI", quaquaPrefix + "FormattedTextFieldUI",
            "RadioButtonUI", quaquaPrefix + "RadioButtonUI",
            "ToggleButtonUI", quaquaPrefix + "ToggleButtonUI",
            "SeparatorUI", quaquaPantherPrefix + "SeparatorUI",
            //"MenuSeparatorUI", quaquaPantherPrefix + "SeparatorUI",
            //"ProgressBarUI", basicPrefix + "ProgressBarUI",
            "ScrollBarUI", quaquaPrefix + "ScrollBarUI",
            "ScrollPaneUI", quaquaPrefix + "ScrollPaneUI",
            "SplitPaneUI", quaquaPrefix + "SplitPaneUI",
            //"SliderUI", quaquaPrefix + "SliderUI",
            //"SpinnerUI", quaquaPrefix + "SpinnerUI",
            "ToolBarSeparatorUI", quaquaPrefix + "ToolBarSeparatorUI",
            //"PopupMenuSeparatorUI", quaquaPantherPrefix + "SeparatorUI",
            "TextAreaUI", quaquaPrefix + "TextAreaUI",
            "TextFieldUI", quaquaPrefix + "TextFieldUI",
            "PasswordFieldUI", quaquaPrefix + "PasswordFieldUI",
            "TextPaneUI", quaquaPrefix + "TextPaneUI",
            "EditorPaneUI", quaquaPrefix + "EditorPaneUI",
            "TreeUI", quaquaPrefix + "TreeUI",
            "LabelUI", quaquaPrefix + "LabelUI",
            "ListUI", quaquaPrefix + "ListUI",
            //"TabbedPaneUI", quaquaPantherPrefix + "TabbedPaneUI",
            "ToolBarUI", quaquaPrefix + "ToolBarUI",
            //"ToolTipUI", basicPrefix + "ToolTipUI",
            "ComboBoxUI", quaquaPrefix + "ComboBoxUI",
            "TableUI", quaquaPrefix + "TableUI",
            //"TableHeaderUI", quaquaPrefix + "TableHeaderUI",
            // "InternalFrameUI", basicPrefix + "InternalFrameUI",
            //"DesktopPaneUI", quaquaPrefix + "DesktopPaneUI",
            //"DesktopIconUI", basicPrefix + "DesktopIconUI",
            "OptionPaneUI", quaquaPrefix + "OptionPaneUI",
            "PanelUI", quaquaPrefix + "PanelUI",
            "ViewportUI", quaquaPrefix + "ViewportUI",
            // Do not create a RootPaneUI on our own, unless we also
            // create our own ButtonUI. Aqua's RootPaneUI is responsible
            // for updating the border of the ButtonUI, when it is the default,
            // and for propagating window activation/dectivation events to
            // all the child components of a window.
            "RootPaneUI", quaquaPrefix + "RootPaneUI",};
        putDefaults(table, uiDefaults);

        /*
        // Popup menu fix only works fully when we have all AWT event permission
        SecurityManager security = System.getSecurityManager();
        try {
        if (security != null) {
        security.checkPermission(sun.security.util.SecurityConstants.ALL_AWT_EVENTS_PERMISSION);
        }
        uiDefaults = new Object[] {
        "PopupMenuUI", quaquaPrefix + "PopupMenuUI",
        };
        } catch (SecurityException e) {
        // do nothing
        }*/
        uiDefaults = new Object[]{
                    "PopupMenuUI", quaquaPrefix + "PopupMenuUI",};
        putDefaults(table, uiDefaults);


        // FIXME Menu related workarounds work only if useScreenMenuBar is off.
        if (!isUseScreenMenuBar()) {
            uiDefaults = new Object[]{
                        "MenuBarUI", quaquaPrefix + "MenuBarUI",
                        "MenuUI", quaquaPrefix + "MenuUI",
                        "MenuItemUI", quaquaPrefix + "MenuItemUI",
                        "CheckBoxMenuItemUI", quaquaPrefix + "MenuItemUI",
                        "RadioButtonMenuItemUI", quaquaPrefix + "MenuItemUI"
                    };
            putDefaults(table, uiDefaults);
        }
    }
    @Override
    protected void initDesignDefaults(UIDefaults table) {

        super.initDesignDefaults(table);

        Object[] uiDefaults = {
            "FileChooser.listView.extraColumnTextColor", new ColorUIResource(80, 80, 80),
            "FileChooser.listView.headerColor", new ColorUIResource(100, 100, 120),
            "FileChooser.listView.headerBorderColor", new ColorUIResource(190, 190, 190),
            //
        };

        putDefaults(table, uiDefaults);
    }
}
