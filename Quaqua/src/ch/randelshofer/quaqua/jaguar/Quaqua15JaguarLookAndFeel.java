/*
 * @(#)Quaqua15JaguarLookAndFeel.java
 *
 * Copyright (c) 2003-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.jaguar;

import ch.randelshofer.quaqua.border.VisualMarginBorder;
import ch.randelshofer.quaqua.*;
import ch.randelshofer.quaqua.color.AlphaColorUIResource;

import javax.swing.*;
import javax.swing.plaf.*;
import java.awt.*;
import java.security.*;

/**
 * The Quaqua15JaguarLookAndFeel is an extension for Apple's Aqua Look and Feel
 * for J2SE 5 on Mac OS X 10.0 through 10.2 (Jaguar).
 * <p>
 * The Quaqua Look and Feel can not be used on other platforms than Mac OS X.
 * <p>
 * <h3>Usage</h3>
 * Please use the <code>QuaquaManager</code> to activate this look and feel in
 * your application. Or use the generic <code>QuaquaLookAndFeel</code>. Both
 * are designed to automatically detect the appropriate Quaqua Look and Feel
 * implementation.
 *
 * @see QuaquaManager
 * @see QuaquaLookAndFeel
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Quaqua15JaguarLookAndFeel extends BasicQuaquaLookAndFeel {
    /**
     * Creates a new instance.
     */
    public Quaqua15JaguarLookAndFeel() {
        // Our target look and feel is Apple's AquaLookAndFeel.
        super("apple.laf.AquaLookAndFeel");
    }
    
    /**
     * Return a one line description of this look and feel implementation,
     * e.g. "The CDE/Motif Look and Feel".   This string is intended for
     * the user, e.g. in the title of a window or in a ToolTip message.
     */
    @Override
    public String getDescription() {
        return "The Quaqua Jaguar Look and Feel "+
                QuaquaManager.getVersion()+
                " for J2SE 5";
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
        return "Quaqua Jaguar";
    }
    @Override
    public boolean getSupportsWindowDecorations() {
        return true;
    }
    
    /**
     * Initialize the uiClassID to BasicComponentUI mapping.
     * The JComponent classes define their own uiClassID constants
     * (see AbstractComponent.getUIClassID).  This table must
     * map those constants to a BasicComponentUI class of the
     * appropriate type.
     *
     * @see #getDefaults
     */
    @Override
    protected void initClassDefaults(UIDefaults table) {
        String basicPrefix = "javax.swing.plaf.basic.Basic";
        String quaquaPrefix = "ch.randelshofer.quaqua.Quaqua";
        String quaquaJaguarPrefix = "ch.randelshofer.quaqua.jaguar.QuaquaJaguar";
        
        // NOTE: Change code below, to override different
        // UI classes of the target look and feel.
        Object[] uiDefaults = {
            "BrowserUI", quaquaPrefix + "BrowserUI",
            "ButtonUI", quaquaPrefix + "ButtonUI",
            "CheckBoxUI", quaquaPrefix + "CheckBoxUI",
            "ColorChooserUI", quaquaPrefix + "ColorChooserUI",
            "FileChooserUI", quaquaJaguarPrefix + "FileChooserUI",
            
            "FormattedTextFieldUI", quaquaPrefix + "FormattedTextFieldUI",
            "RadioButtonUI", quaquaPrefix + "RadioButtonUI",
            "ToggleButtonUI", quaquaPrefix + "ToggleButtonUI",
            "SeparatorUI", quaquaJaguarPrefix + "SeparatorUI",
            /*  "ProgressBarUI", basicPrefix + "ProgressBarUI",*/
            "ScrollBarUI", quaquaPrefix + "ScrollBarUI",
            "ScrollPaneUI", quaquaPrefix + "ScrollPaneUI",
            "SplitPaneUI", quaquaPrefix + "SplitPaneUI",
            "SliderUI", quaquaPrefix + "SliderUI",
            "SpinnerUI", quaquaPrefix + "SpinnerUI",
            "ToolBarSeparatorUI", quaquaPrefix + "ToolBarSeparatorUI",
            "PopupMenuSeparatorUI", quaquaJaguarPrefix + "SeparatorUI",
            //"TabbedPaneUI", quaquaPrefix + "TabbedPaneUI",
            
            "TextAreaUI", quaquaPrefix + "TextAreaUI",
            "TextFieldUI", quaquaPrefix + "TextFieldUI",
            "PasswordFieldUI", quaquaPrefix + "PasswordFieldUI",
            "TextPaneUI", quaquaPrefix + "TextPaneUI",
            "EditorPaneUI", quaquaPrefix + "EditorPaneUI",
            "TreeUI", quaquaPrefix + "TreeUI",
            "LabelUI", quaquaPrefix + "LabelUI",
            "ListUI", quaquaPrefix + "ListUI",
            "ToolBarUI", quaquaPrefix + "ToolBarUI",
            /* "ToolTipUI", basicPrefix + "ToolTipUI",*/
            "ComboBoxUI", quaquaPrefix + "ComboBoxUI",
            "TableUI", quaquaPrefix + "TableUI",
            "TableHeaderUI", quaquaPrefix + "TableHeaderUI",
            /* "InternalFrameUI", basicPrefix + "InternalFrameUI",*/
            //"DesktopPaneUI", quaquaPrefix + "DesktopPaneUI",
            /*"DesktopIconUI", basicPrefix + "DesktopIconUI",*/
            "OptionPaneUI", quaquaPrefix + "OptionPaneUI",
            "PanelUI", quaquaPrefix + "PanelUI",
            "ViewportUI", quaquaPrefix + "ViewportUI",
            // Do not create a RootPaneUI on our own, until we have
            // also implemented a ButtonUI. Aqua's RootPaneUI is responsible
            // for updating the border of the ButtonUI, when it is the default,
            // and for propagating window activation/dectivation events to
            // all the child components of a window.
            "RootPaneUI", quaquaPrefix + "RootPaneUI",
        };
        putDefaults(table, uiDefaults);
        
        // Popup menu fix only works fully when we have all AWT event permission
        SecurityManager security = System.getSecurityManager();
        try {
            if (security != null) {
                security.checkPermission(sun.security.util.SecurityConstants.ALL_PERMISSION);
            }
            uiDefaults = new Object[] {
                "PopupMenuUI", quaquaPrefix +"PopupMenuUI",
            };
        } catch (SecurityException e) {
            // Silently do nothing
        }
        putDefaults(table, uiDefaults);
        
        
        // Jaguar design for tabbed panes can be chosen by setting
        // a system property.
        if (isJaguarTabbedPane()) {
            uiDefaults = new Object[] {
                "TabbedPaneUI", quaquaJaguarPrefix + "TabbedPaneUI",
            };
            
            putDefaults(table, uiDefaults);
        }
        
        // FIXME Menu related workarounds work only if useScreenMenuBar is off.
        if (! isUseScreenMenuBar()) {
            uiDefaults = new Object[] {
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
    protected boolean isJaguarTabbedPane() {
        String property;
        try {
            property = QuaquaManager.getProperty("Quaqua.tabLayoutPolicy");
            if (property == null) {
                property = QuaquaManager.getProperty("Quaqua.TabbedPane.design", "auto");
            }
        } catch (AccessControlException e) {
            property = "auto";
        }
        return property.equals("auto")
        || property.equals("jaguar")
        || property.equals("wrap");
    }
    private boolean isBrushedMetal() {
        String property;
        try {
            property = QuaquaManager.getProperty("apple.awt.brushMetalLook", "false");
        } catch (AccessControlException e) {
            property = "false";
        }
        return property.equals("true");
    }
    private boolean isUseScreenMenuBar() {
        String property;
        try {
            property = QuaquaManager.getProperty("apple.laf.useScreenMenuBar", "false");
        } catch (AccessControlException e) {
            property = "false";
        }
        return property.equals("true");
    }
    /**
     * Load the SystemColors into the defaults table.  The keys
     * for SystemColor defaults are the same as the names of
     * the public fields in SystemColor.  If the table is being
     * created on a native Windows platform we use the SystemColor
     * values, otherwise we create color uiDefaults whose values match
     * the defaults Windows95 colors.
     */
    @Override
    protected void initSystemColorDefaults(UIDefaults table) {
        super.initSystemColorDefaults(table);
        
        boolean isBrushedMetal = isBrushedMetal();
        Object controlBackground = (isBrushedMetal)
        ? table.get("control")
        : makeTextureColor(0xf4f4f4, jaguarDir+"Panel.texture.png");

        Object menuBackground = (isBrushedMetal)
        ? table.get("menu")
        : makeTextureColor(0xf4f4f4, jaguarDir+"MenuBar.texture.png");
        
        Object[] uiDefaults = {
            "window", controlBackground, /* Default color for the interior of windows */
            "control", controlBackground, /* Default color for controls (buttons, sliders, etc) */
            "menu", menuBackground, /* Default color for the interior of menus */
            
            // Quaqua specific 'system' colors
            "listHighlight", table.get("textHighlight"), /* List background color when selected */
            "listHighlightText", table.get("textHighlightText"), /* List color when selected */
            "listHighlightBorder", new ColorUIResource(0x808080), /* List color when selected */
        };
        putDefaults(table, uiDefaults);

    }
    
    @Override
    protected void initDesignDefaults(UIDefaults table) {
        boolean isBrushedMetal = isBrushedMetal();
        
        Object toolBarBackground = (isBrushedMetal)
        ? table.get("ToolBar.background")
        : makeTextureColor(0xf4f4f4, jaguarDir+"ToolBar.texture.png");
        
        Object[] uiDefaults = {
            "Browser.expandedIcon", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.QuaquaIconFactory", "createIcon",
            new Object[]{jaguarDir + "Browser.disclosureIcons.png", 6, Boolean.TRUE, 0}),
            "Browser.expandingIcon", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.QuaquaIconFactory", "createIcon",
            new Object[]{jaguarDir + "Browser.disclosureIcons.png", 6, Boolean.TRUE, 1}),
            "Browser.focusedSelectedExpandedIcon", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.QuaquaIconFactory", "createIcon",
            new Object[]{jaguarDir + "Browser.disclosureIcons.png", 6, Boolean.TRUE, 2}),
            "Browser.focusedSelectedExpandingIcon", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.QuaquaIconFactory", "createIcon",
            new Object[]{jaguarDir + "Browser.disclosureIcons.png", 6, Boolean.TRUE, 3}),
            "Browser.selectedExpandedIcon", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.QuaquaIconFactory", "createIcon",
            new Object[]{jaguarDir + "Browser.disclosureIcons.png", 6, Boolean.TRUE, 4}),
            "Browser.selectedExpandingIcon", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.QuaquaIconFactory", "createIcon",
            new Object[]{jaguarDir + "Browser.disclosureIcons.png", 6, Boolean.TRUE, 5}),
            //
            "FileChooser.browserUseUnselectedExpandIconForLabeledFile", Boolean.TRUE,
            //
            "Frame.titlePaneBorders", makeImageBevelBorders(jaguarDir+"Frame.titlePaneBorders.png", new Insets(0,0,22,0), 2, true),
            "Frame.titlePaneBorders.small", makeImageBevelBorders(jaguarDir+"Frame.titlePaneBorders.small.png", new Insets(0,0,16,0), 2, true),
            "Frame.titlePaneBorders.mini", makeImageBevelBorders(jaguarDir+"Frame.titlePaneBorders.mini.png", new Insets(0,0,12,0), 2, true),
            "Frame.titlePaneBorders.vertical", makeImageBevelBorders(jaguarDir+"Frame.titlePaneBorders.vertical.png", new Insets(0,0,0,22), 2, false),
            "Frame.titlePaneBorders.vertical.small", makeImageBevelBorders(jaguarDir+"Frame.titlePaneBorders.vertical.small.png", new Insets(0,0,0,16), 2, false),
            "Frame.titlePaneBorders.vertical.mini", makeImageBevelBorders(jaguarDir+"Frame.titlePaneBorders.vertical.mini.png", new Insets(0,0,0,12), 2, false),
            //
            "Label.embossForeground", new AlphaColorUIResource(0x7effffff),
            "Label.shadowForeground", new AlphaColorUIResource(0x7e000000),
            //
            "Separator.foreground", new ColorUIResource(139,139,139),
            "Separator.highlight", new ColorUIResource(243,243,243),
            "Separator.shadow", new ColorUIResource(213,213,213),
            "Separator.border", new VisualMarginBorder(),
            
            "ToolBar.background", toolBarBackground,
            "ToolBar.bottom.gradient", null,
            "ToolBar.bottom.inactiveGradient", null,
            "ToolBar.title.background", toolBarBackground,

            "Tree.sideBar.background", new ColorUIResource(0xffffff),
            "Tree.sideBar.selectionBorder", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.leopard.QuaquaLeopardSideBarSelectionBorder"),
            "Tree.sideBar.icons", makeIcons(leopardDir + "Tree.sideBar.icons.png", 15, true),
            "Tree.sideBarCategory.foreground",  new ColorUIResource(0x000000),
            "Tree.sideBarCategory.selectionForeground",  new ColorUIResource(0xffffff),
            "Tree.sideBarCategory.font", new FontUIResource("Lucida Grande", Font.BOLD, 11),
            "Tree.sideBarCategory.selectionFont", new FontUIResource("Lucida Grande", Font.BOLD, 11),
            "Tree.sideBar.foreground", new ColorUIResource(0x000000),
            "Tree.sideBar.selectionForeground", new ColorUIResource(0xffffff),
            "Tree.sideBar.font", new FontUIResource("Lucida Grande", Font.PLAIN, 11),
            "Tree.sideBar.selectionFont", new FontUIResource("Lucida Grande", Font.BOLD, 11), //
            "Tree.sideBarCategory.style",  "emboss",
            "Tree.sideBarCategory.selectionStyle",  "shadow",
            "Tree.sideBar.style",  "plain",
            "Tree.sideBar.selectionStyle",  "shadow",
        };
        putDefaults(table, uiDefaults);
        
        // FIXME Implement a screen menu bar by myself. We lose too many features here.
        if (isUseScreenMenuBar()) {
            uiDefaults = new Object[] {
                "CheckBoxMenuItem.checkIcon", makeButtonStateIcon(commonDir+"CheckBoxMenuItem.icons.png", 6),
                
                "Menu.checkIcon", makeIcon(getClass(), commonDir+"MenuItem.checkIcon.png"),
                "Menu.submenuPopupOffsetY", -4,
                
                "MenuItem.checkIcon", makeIcon(getClass(), commonDir+"MenuItem.checkIcon.png"),
                
                "PopupMenu.border", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.QuaquaMenuBorder"),
                
                "RadioButtonMenuItem.checkIcon", makeButtonStateIcon(commonDir+"RadioButtonMenuItem.icons.png", 6),
            };
        } else {
            uiDefaults = new Object[] {
                "CheckBoxMenuItem.checkIcon", makeButtonStateIcon(commonDir+"CheckBoxMenuItem.icons.png", 6),
                "CheckBoxMenuItem.border", new BorderUIResource.EmptyBorderUIResource(1,1,1,1),
                
                "Menu.checkIcon", makeIcon(getClass(), commonDir+"MenuItem.checkIcon.png"),
                "Menu.margin", new InsetsUIResource(0, 8, 0, 8),
                "Menu.menuPopupOffsetX", 0,
                "Menu.menuPopupOffsetY", 0,
                "Menu.submenuPopupOffsetX", 0,
                "Menu.submenuPopupOffsetY", -4,
                "Menu.useMenuBarBackgroundForTopLevel", Boolean.TRUE,
                "Menu.border", new BorderUIResource.EmptyBorderUIResource(1,1,1,1),
                
                "MenuBar.border", new BorderUIResource.MatteBorderUIResource(0,0,1,0,new Color(128,128,128)),
                "MenuBar.margin", new InsetsUIResource(1, 8, 1, 8),
                "MenuBar.shadow", null,
                
                "MenuItem.checkIcon", makeIcon(getClass(), commonDir+"MenuItem.checkIcon.png"),
                "MenuItem.border", new BorderUIResource.EmptyBorderUIResource(1,1,1,1),
                
                "PopupMenu.border", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.QuaquaMenuBorder"),
                
                "RadioButtonMenuItem.checkIcon", makeButtonStateIcon(commonDir+"RadioButtonMenuItem.icons.png", 6),
                "RadioButtonMenuItem.border", new BorderUIResource.EmptyBorderUIResource(1,1,1,1),
            };
        }
        putDefaults(table, uiDefaults);
        if (isJaguarTabbedPane()) {
            uiDefaults = new Object[] {
                "TabbedPane.tabInsets", new InsetsUIResource(1, 10, 4, 9),
                "TabbedPane.selectedTabPadInsets", new InsetsUIResource(2, 2, 2, 1),
                "TabbedPane.tabAreaInsets", new InsetsUIResource(4, 16, 0, 16),
                "TabbedPane.contentBorderInsets", new InsetsUIResource(5, 6, 6, 6),
                //"TabbedPane.border", new VisualMarginBorder(3,3,5,3),
            };
        } else {
            uiDefaults = new Object[] {
                "TabbedPane.background", new AlphaColorUIResource(0,0,0,0),
            };
        }
        putDefaults(table, uiDefaults);
    }
    
}
