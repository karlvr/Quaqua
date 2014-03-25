/*
 * @(#)Quaqua15LeopardLookAndFeel.java 
 *
 * Copyright (c) 2007-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.leopard;

import ch.randelshofer.quaqua.border.VisualMarginBorder;
import ch.randelshofer.quaqua.color.InactivatableColorUIResource;
import ch.randelshofer.quaqua.color.GradientColor;
import ch.randelshofer.quaqua.color.AlphaColorUIResource;
import ch.randelshofer.quaqua.*;
import ch.randelshofer.quaqua.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import java.awt.*;

/**
 * The Quaqua15LeopardLookAndFeel provides bug fixes and enhancements for Apple's
 * Aqua Look and Feel for Java 1.5 on Mac OS X 10.5 (Leopard).
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
 * @version $Id$
 */
public class Quaqua15LeopardLookAndFeel extends BasicQuaquaLookAndFeel {

    /**
     * Creates a new instance.
     */
    public Quaqua15LeopardLookAndFeel() {
        // Our target look and feel is Apple's AquaLookAndFeel.
        super("apple.laf.AquaLookAndFeel");
    }

    /**
     * Creates a new instance.
     */
    protected Quaqua15LeopardLookAndFeel(String className) {
        super(className);
    }

    /**
     * Return a one line description of this look and feel implementation,
     * e.g. "The CDE/Motif Look and Feel".   This string is intended for
     * the user, e.g. in the title of a window or in a ToolTip message.
     */
    @Override
    public String getDescription() {
        return "The Quaqua Leopard Look and Feel "
                + QuaquaManager.getVersion()
                + " for J2SE 5";
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
        return "Quaqua Leopard";
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
        String quaquaPantherPrefix = "ch.randelshofer.quaqua.panther.QuaquaPanther";
        String quaquaLeopardPrefix = "ch.randelshofer.quaqua.leopard.QuaquaLeopard";

        // NOTE: Change code below, to override different
        // UI classes of the target look and feel.
        Object[] uiDefaults = {
            "BrowserUI", quaquaPrefix + "BrowserUI",
            "ButtonUI", quaquaPrefix + "ButtonUI",
            "CheckBoxUI", quaquaPrefix + "CheckBoxUI",
            "ColorChooserUI", quaquaPrefix + "ColorChooserUI",
            "FileChooserUI", quaquaLeopardPrefix + "FileChooserUI",
            "FormattedTextFieldUI", quaquaPrefix + "FormattedTextFieldUI",
            "RadioButtonUI", quaquaPrefix + "RadioButtonUI",
            "ToggleButtonUI", quaquaPrefix + "ToggleButtonUI",
            "SeparatorUI", quaquaPantherPrefix + "SeparatorUI",
            "MenuSeparatorUI", quaquaPantherPrefix + "SeparatorUI",
            //  "ProgressBarUI", basicPrefix + "ProgressBarUI",
            "ScrollBarUI", quaquaPrefix + "ScrollBarUI",
            "ScrollPaneUI", quaquaPrefix + "ScrollPaneUI",
            "SplitPaneUI", quaquaPrefix + "SplitPaneUI",
            "SliderUI", quaquaPrefix + "SliderUI",
            "SpinnerUI", quaquaPrefix + "SpinnerUI",
            "ToolBarSeparatorUI", quaquaPrefix + "ToolBarSeparatorUI",
            "PopupMenuSeparatorUI", quaquaPantherPrefix + "SeparatorUI",
            "TextAreaUI", quaquaPrefix + "TextAreaUI",
            "TextFieldUI", quaquaPrefix + "TextFieldUI",
            "PasswordFieldUI", quaquaPrefix + "PasswordFieldUI",
            "TextPaneUI", quaquaPrefix + "TextPaneUI",
            "EditorPaneUI", quaquaPrefix + "EditorPaneUI",
            "TreeUI", quaquaPrefix + "TreeUI",
            "LabelUI", quaquaPrefix + "LabelUI",
            "ListUI", quaquaPrefix + "ListUI",
            "TabbedPaneUI", quaquaPantherPrefix + "TabbedPaneUI",
            "ToolBarUI", quaquaPrefix + "ToolBarUI",
            // "ToolTipUI", basicPrefix + "ToolTipUI",
            "ComboBoxUI", quaquaPrefix + "ComboBoxUI",
            "TableUI", quaquaPrefix + "TableUI",
            "TableHeaderUI", quaquaPrefix + "TableHeaderUI",
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

    protected boolean isBrushedMetal() {
        String property;
        property = QuaquaManager.getProperty("apple.awt.brushMetalLook", "false");
        return property.equals("true");
    }

    protected boolean isUseScreenMenuBar() {
        String property;
        property = QuaquaManager.getProperty("apple.laf.useScreenMenuBar", "false");
        return property.equals("true");
    }

    @Override
    protected void initFontDefaults(UIDefaults table) {
        super.initFontDefaults(table);

        Object emphasizedSmallSystemFont = new UIDefaults.ProxyLazyValue(
                "javax.swing.plaf.FontUIResource",
                null,
                new Object[]{"Lucida Grande", Font.BOLD, 11});

        Object[] uiDefaults = {
            "FileChooser.previewLabelFont", emphasizedSmallSystemFont,};
        putDefaults(table, uiDefaults);
    }

    @Override
    protected void initSystemColorDefaults(UIDefaults table) {
        super.initSystemColorDefaults(table);
        /*
        if (QuaquaManager.getDesign() != QuaquaManager.TIGER) {
        boolean isBrushedMetal = isBrushedMetal();
        Object controlBackground = (isBrushedMetal)
        ? table.get("control")
        : makeTextureColor(0xf4f4f4, pantherDir+"Panel.texture.png");
        Object toolBarBackground = (isBrushedMetal)
        ? table.get("ToolBar.background")
        : makeTextureColor(0xf4f4f4, pantherDir+"ToolBar.texture.png");
        Object menuBackground = (isBrushedMetal)
        ? table.get("menu")
        : makeTextureColor(0xf4f4f4, pantherDir+"MenuBar.texture.png");
        Object menuHighlight = makeTextureColor(0x3471cf, pantherDir+"MenuBar.texture.S.png");

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
        }*/

    }

    @Override
    protected void initDesignDefaults(UIDefaults table) {
        boolean isBrushedMetal = isBrushedMetal();
        ColorUIResource disabledForeground = new ColorUIResource(128, 128, 128);
        Object menuBackground = table.get("menuHighlight");
        ColorUIResource menuSelectionForeground = new ColorUIResource(0xffffff);
        Object panelBackground = new ColorUIResource(0xe8e8e8);
        ColorUIResource inactiveSelectionBackground = new ColorUIResource(208, 208, 208);
        Color grayedFocusCellBorderColor = (Color) table.get("listHighlight");
        Object toolBarBackground = table.get("control");

        String sideBarIconsStart = "/System/Library/CoreServices/CoreTypes.bundle/Contents/Resources/Toolbar";
        String sideBarIconsEnd = "FolderIcon.icns";

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
            "ComboBox.selectionBackground", new GradientColor(new ColorUIResource(0x3875d7), new ColorUIResource(0x5170f6), new ColorUIResource(0x1a43f3)),
            "ComboBox.selectionForeground", menuSelectionForeground,
            "ComboBox.popupBorder",//
            new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.leopard.QuaquaLeopardComboBoxPopupBorder"),
            //
            "FileChooser.previewLabelForeground", new ColorUIResource(0x808080),
            "FileChooser.previewValueForeground", new ColorUIResource(0x000000),
            "FileChooser.previewLabelInsets", new InsetsUIResource(1, 0, 0, 4),
            "FileChooser.previewLabelDelimiter", "",
            "FileChooser.cellTipOrigin", new Point(18, 1),
            "FileChooser.splitPaneDividerSize", 1,
            "FileChooser.browserCellFocusBorder",
            new UIDefaults.ProxyLazyValue("javax.swing.plaf.BorderUIResource$EmptyBorderUIResource",
            new Object[]{new Insets(1, 1, 0, 1)}),
            "FileChooser.browserCellFocusBorderGrayed",
            new UIDefaults.ProxyLazyValue("javax.swing.plaf.BorderUIResource$MatteBorderUIResource",
            new Object[]{1, 1, 0, 1, grayedFocusCellBorderColor}),
            "FileChooser.browserCellBorder",
            new UIDefaults.ProxyLazyValue("javax.swing.plaf.BorderUIResource$EmptyBorderUIResource",
            new Object[]{new Insets(1, 1, 0, 1)}),
            "FileChooser.browserCellColorLabelInsets", new InsetsUIResource(0, 1, 0, 0),
            "FileChooser.browserCellSelectedColorLabelInsets", new InsetsUIResource(0, 0, 0, 0),
            "FileChooser.browserCellTextIconGap", 5,
            "FileChooser.browserCellTextArrowIconGap", 5,
            "FileChooser.browserUseUnselectedExpandIconForLabeledFile", Boolean.TRUE,
            "FileChooser.sideBarIcon.Applications", makeNativeIcon(sideBarIconsStart + "Apps" + sideBarIconsEnd, 16),
            "FileChooser.sideBarIcon.Desktop", makeNativeIcon(sideBarIconsStart + "Desktop" + sideBarIconsEnd, 16),
            "FileChooser.sideBarIcon.Documents", makeNativeIcon(sideBarIconsStart + "Documents" + sideBarIconsEnd, 16),
            "FileChooser.sideBarIcon.Downloads", makeNativeIcon(sideBarIconsStart + "Downloads" + sideBarIconsEnd, 16),
            "FileChooser.sideBarIcon.Library", makeNativeIcon(sideBarIconsStart + "Library" + sideBarIconsEnd, 16),
            "FileChooser.sideBarIcon.Movies", makeNativeIcon(sideBarIconsStart + "Movie" + sideBarIconsEnd, 16),// Note: no "s" in "Movie"
            "FileChooser.sideBarIcon.Music", makeNativeIcon(sideBarIconsStart + "Music" + sideBarIconsEnd, 16),
            "FileChooser.sideBarIcon.Pictures", makeNativeIcon(sideBarIconsStart + "Pictures" + sideBarIconsEnd, 16),
            "FileChooser.sideBarIcon.Public", makeNativeIcon(sideBarIconsStart + "Public" + sideBarIconsEnd, 16),
            "FileChooser.sideBarIcon.Sites", makeNativeIcon(sideBarIconsStart + "Sites" + sideBarIconsEnd, 16),
            "FileChooser.sideBarIcon.Utilities", makeNativeIcon(sideBarIconsStart + "Utilities" + sideBarIconsEnd, 16),
            //
            "FileView.computerIcon", makeIcon(getClass(), leopardDir + "FileView.computerIcon.png"),
            "FileView.fileIcon", makeIcon(getClass(), leopardDir + "FileView.fileIcon.png"),
            "FileView.directoryIcon", makeIcon(getClass(), leopardDir + "FileView.directoryIcon.png"),
            "FileView.hardDriveIcon", makeIcon(getClass(), leopardDir + "FileView.hardDriveIcon.png"),
            "FileView.floppyDriveIcon", makeIcon(getClass(), leopardDir + "FileView.floppyDriveIcon.png"),
            //
            "Frame.titlePaneBorders", makeImageBevelBorders(leopardDir + "Frame.titlePaneBorders.png", new Insets(0, 0, 22, 0), 2, true),
            "Frame.titlePaneBorders.small", makeImageBevelBorders(leopardDir + "Frame.titlePaneBorders.small.png", new Insets(0, 0, 16, 0), 2, true),
            "Frame.titlePaneBorders.mini", makeImageBevelBorders(leopardDir + "Frame.titlePaneBorders.mini.png", new Insets(0, 0, 12, 0), 2, true),
            "Frame.titlePaneBorders.vertical", makeImageBevelBorders(leopardDir + "Frame.titlePaneBorders.vertical.png", new Insets(0, 0, 0, 22), 2, false),
            "Frame.titlePaneBorders.vertical.small", makeImageBevelBorders(leopardDir + "Frame.titlePaneBorders.vertical.small.png", new Insets(0, 0, 0, 16), 2, false),
            "Frame.titlePaneBorders.vertical.mini", makeImageBevelBorders(leopardDir + "Frame.titlePaneBorders.vertical.mini.png", new Insets(0, 0, 0, 12), 2, false),
            "Frame.titlePaneEmbossForeground", new AlphaColorUIResource(0x7effffff),
            //
            "InternalFrame.titlePaneBorders", makeImageBevelBorders(leopardDir + "Frame.titlePaneBorders.png", new Insets(0, 0, 22, 0), 2, true),
            "InternalFrame.titlePaneBorders.small", makeImageBevelBorders(leopardDir + "Frame.titlePaneBorders.small.png", new Insets(0, 0, 16, 0), 2, true),
            "InternalFrame.titlePaneBorders.mini", makeImageBevelBorders(leopardDir + "Frame.titlePaneBorders.mini.png", new Insets(0, 0, 10, 0), 2, true),
            "InternalFrame.closeIcon", makeFrameButtonStateIcon(leopardDir + "Frame.closeIcons.png", 12),
            "InternalFrame.maximizeIcon", makeFrameButtonStateIcon(leopardDir + "Frame.maximizeIcons.png", 12),
            "InternalFrame.iconifyIcon", makeFrameButtonStateIcon(leopardDir + "Frame.iconifyIcons.png", 12),
            "InternalFrame.closeIcon.small", makeFrameButtonStateIcon(leopardDir + "Frame.closeIcons.small.png", 12),
            "InternalFrame.maximizeIcon.small", makeFrameButtonStateIcon(leopardDir + "Frame.maximizeIcons.small.png", 12),
            "InternalFrame.iconifyIcon.small", makeFrameButtonStateIcon(leopardDir + "Frame.iconifyIcons.small.png", 12),
            "InternalFrame.resizeIcon", makeIcon(getClass(), leopardDir + "Frame.resize.png"),
            //
            "Label.embossForeground", new AlphaColorUIResource(0x7effffff),
            "Label.shadowForeground", new AlphaColorUIResource(0x7e000000),
            //
            "OptionPane.errorIconResource", "/ch/randelshofer/quaqua/leopard/images/OptionPane.errorIcon.png",
            "OptionPane.warningIconResource", "/ch/randelshofer/quaqua/leopard/images/OptionPane.warningIcon.png",
            //
            "Panel.background", panelBackground,
            //
            "PopupMenu.border", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.leopard.QuaquaLeopardMenuBorder"),
            //
            "RootPane.background", panelBackground,
            //
            "Separator.foreground", new ColorUIResource(139, 139, 139),
            "Separator.highlight", new ColorUIResource(243, 243, 243),
            "Separator.shadow", new ColorUIResource(213, 213, 213),
            "Separator.border", new VisualMarginBorder(),
            //
            "TabbedPane.disabledForeground", disabledForeground,
            "TabbedPane.tabInsets", new InsetsUIResource(1, 10, 4, 9),
            "TabbedPane.selectedTabPadInsets", new InsetsUIResource(2, 2, 2, 1),
            "TabbedPane.tabAreaInsets", new InsetsUIResource(4, 16, 0, 16),
            "TabbedPane.contentBorderInsets", new InsetsUIResource(5, 6, 6, 6),
            //"TabbedPane.background", (isBrushedMetal) ? table.get("TabbedPane.background") : makeTextureColor(0xf4f4f4, pantherDir+"Panel.texture.png"),
            "TabbedPane.tabLayoutPolicy", (isJaguarTabbedPane()) ? JTabbedPane.WRAP_TAB_LAYOUT : JTabbedPane.SCROLL_TAB_LAYOUT,
            "TabbedPane.wrap.disabledForeground", disabledForeground,
            "TabbedPane.wrap.tabInsets", new InsetsUIResource(1, 10, 4, 9),
            "TabbedPane.wrap.selectedTabPadInsets", new InsetsUIResource(2, 2, 2, 1),
            "TabbedPane.wrap.tabAreaInsets", new InsetsUIResource(4, 16, 0, 16),
            "TabbedPane.wrap.contentBorderInsets", new InsetsUIResource(2, 3, 3, 3),
            //"TabbedPane.wrap.background", (isBrushedMetal) ? table.get("TabbedPane.background") : makeTextureColor(0xf4f4f4, pantherDir+"Panel.texture.png"),
            "TabbedPane.scroll.selectedTabPadInsets", new InsetsUIResource(0, 0, 0, 0),
            "TabbedPane.scroll.tabRunOverlay", 0,
            "TabbedPane.scroll.tabInsets", new InsetsUIResource(1, 7, 2, 7),
            "TabbedPane.scroll.smallTabInsets", new InsetsUIResource(1, 5, 2, 5),
            "TabbedPane.scroll.outerTabInsets", new InsetsUIResource(1, 11, 2, 11),
            "TabbedPane.scroll.smallOuterTabInsets", new InsetsUIResource(1, 9, 2, 9),
            "TabbedPane.scroll.contentBorderInsets", new InsetsUIResource(2, 2, 2, 2),
            "TabbedPane.scroll.tabAreaInsets", new InsetsUIResource(-2, 16, 1, 16),
            "TabbedPane.scroll.contentBorder", makeImageBevelBorder(
            commonDir + "GroupBox.png", new Insets(7, 7, 7, 7), true, new Color(0x08000000, true)),
            "TabbedPane.scroll.emptyContentBorder", makeImageBevelBorder(
            commonDir + "GroupBox.empty.png", new Insets(7, 7, 7, 7), true),
            "TabbedPane.scroll.tabBorders", makeImageBevelBorders(commonDir + "Toggle.borders.png",
            new Insets(8, 10, 15, 10), 10, true),
            "TabbedPane.scroll.tabFocusRing", makeImageBevelBorder(commonDir + "Toggle.focusRing.png",
            new Insets(8, 10, 15, 10), true),
            "TabbedPane.scroll.eastTabBorders", makeImageBevelBorders(commonDir + "Toggle.east.borders.png",
            new Insets(8, 1, 15, 10), 10, true),
            "TabbedPane.scroll.eastTabFocusRing", makeImageBevelBorder(commonDir + "Toggle.east.focusRing.png",
            new Insets(8, 4, 15, 10), true),
            "TabbedPane.scroll.centerTabBorders", makeImageBevelBorders(commonDir + "Toggle.center.borders.png",
            new Insets(8, 0, 15, 1), 10, true),
            "TabbedPane.scroll.centerTabFocusRing", makeImageBevelBorder(commonDir + "Toggle.center.focusRing.png",
            new Insets(8, 4, 15, 4), false),
            "TabbedPane.scroll.westTabBorders", makeImageBevelBorders(commonDir + "Toggle.west.borders.png",
            new Insets(8, 10, 15, 1), 10, true),
            "TabbedPane.scroll.westTabFocusRing", makeImageBevelBorder(commonDir + "Toggle.west.focusRing.png",
            new Insets(8, 10, 15, 4), true),
            //
            "TitledBorder.border", new GroupBox(),
            "TitledBorder.titleColor", new ColorUIResource(0x303030),
            //
            "ToolBar.background", panelBackground,
            "ToolBar.bottom.borderDivider", new ColorUIResource(0x404040),
            "ToolBar.bottom.borderDividerInactive", new ColorUIResource(0x878787),
            "ToolBar.bottom.gradient", new Color[]{new Color(0xcbcbcb), new Color(0xa7a7a7)},
            "ToolBar.bottom.gradientInactive", new Color[]{new Color(0xeaeaea), new Color(0xd8d8d8)},
            "ToolBar.gradient.borderDivider", new ColorUIResource(0xd4d4d4),
            "ToolBar.gradient.borderDividerInactive", new ColorUIResource(0xd4d4d4),
            "ToolBar.textured.dragMovesWindow", Boolean.TRUE,
            "ToolBar.title.background", toolBarBackground,
            "ToolBar.title.borderDivider", new ColorUIResource(0x404040),
            "ToolBar.title.borderDividerInactive", new ColorUIResource(0x878787),
            //
            "ToolBarSeparator.foreground", new ColorUIResource(0x808080),
            //
            "Tree.collapsedIcon", makeIcon(getClass(), leopardDir + "Tree.collapsedIcon.png"),
            "Tree.expandedIcon", makeIcon(getClass(), leopardDir + "Tree.expandedIcon.png"),
            "Tree.leafIcon", makeIcon(getClass(), leopardDir + "Tree.leafIcon.png"),
            "Tree.openIcon", makeIcon(getClass(), leopardDir + "Tree.openIcon.png"),
            "Tree.leftChildIndent", 8, // 7
            "Tree.rightChildIndent", 12, // 13
            "Tree.icons", makeIcons(leopardDir + "Tree.icons.png", 15, true),
            "Tree.closedIcon", makeIcon(getClass(), leopardDir + "Tree.closedIcon.png"),
            "Tree.sideBar.background", new InactivatableColorUIResource(0xd5dde5, 0xe8e8e8),
            "Tree.sideBar.selectionBorder", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.leopard.QuaquaLeopardSideBarSelectionBorder"),
            "Tree.sideBar.icons", makeIcons(leopardDir + "Tree.sideBar.icons.png", 15, true),
            "Tree.sideBarCategory.foreground", new InactivatableColorUIResource(0x728194, 0x5f5f5f),
            "Tree.sideBarCategory.selectionForeground", new InactivatableColorUIResource(0xffffff, 0xffffff),
            "Tree.sideBarCategory.font", new FontUIResource("Lucida Grande", Font.BOLD, 11),
            "Tree.sideBarCategory.selectionFont", new FontUIResource("Lucida Grande", Font.BOLD, 11),
            "Tree.sideBar.foreground",
            new InactivatableColorUIResource(0x000000, 0x000000),
            "Tree.sideBar.selectionForeground",
            new InactivatableColorUIResource(0xffffff, 0xffffff),
            "Tree.sideBar.font", new FontUIResource("Lucida Grande", Font.PLAIN, 11),
            "Tree.sideBar.selectionFont", new FontUIResource("Lucida Grande", Font.BOLD, 11), //
        };

        putDefaults(table, uiDefaults);

        // FIXME Implement a screen menu bar by myself. We lose too many features here.
        if (isUseScreenMenuBar()) {
            uiDefaults = new Object[]{
                        "CheckBoxMenuItem.checkIcon", makeButtonStateIcon(commonDir + "CheckBoxMenuItem.icons.png", 6, new Rectangle(5, 1, 17, 12)),
                        "CheckBoxMenuItem.border", new BorderUIResource.EmptyBorderUIResource(0, 0, 2, 0),
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
                        "RadioButtonMenuItem.checkIcon", makeButtonStateIcon(commonDir + "RadioButtonMenuItem.icons.png", 6, new Rectangle(5, 0, 17, 12)),
                        "RadioButtonMenuItem.border", new BorderUIResource.EmptyBorderUIResource(0, 0, 2, 0),};
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

    public boolean getSupportsWindowDecorations() {
        return true;
    }
}


