package ch.randelshofer.quaqua.quaqua18;

import ch.randelshofer.quaqua.*;
import ch.randelshofer.quaqua.color.AlphaColorUIResource;
import ch.randelshofer.quaqua.color.GradientColor;
import ch.randelshofer.quaqua.color.InactivatableColorUIResource;
import ch.randelshofer.quaqua.mavericks.Quaqua16MavericksLookAndFeel;
import ch.randelshofer.quaqua.osx.OSXConfiguration;
import ch.randelshofer.quaqua.yosemite.Quaqua16YosemiteLookAndFeel;

import javax.swing.*;
import javax.swing.plaf.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import static ch.randelshofer.quaqua.BasicQuaquaNativeLookAndFeel.makeNativeIcon;
import static ch.randelshofer.quaqua.BasicQuaquaNativeLookAndFeel.makeNativeSidebarIcon;

/**
 * A look and feel for Java 1.8 that mostly uses the Aqua look and feel. The goal of this look and feel is to support
 * Retina displays, which the Aqua look and feel in Java 1.8 already does. The approach taken here is to strip out all
 * of Quaqua except the file chooser and color chooser. That is not as bad as it sounds because the Java 1.8 Aqua look
 * and feel is much better than the old ones. In the future, changes from Quaqua that improve the Aqua look and feel
 * could be added to this look and feel.
 *
 * There is currently no file chooser only version of this look and feel, because it would be almost identical to this
 * look and feel.
 */
public class Quaqua18LookAndFeel extends LookAndFeelProxy15 {
    private LayoutStyle layoutStyle;

    /**
     * Creates a new instance.
     */
    public Quaqua18LookAndFeel() {
        // Our target look and feel is Apple's AquaLookAndFeel.
        this("apple.laf.AquaLookAndFeel");
    }

    /**
     * Creates a new instance.
     */
    protected Quaqua18LookAndFeel(String targetClassName) {
        try {
            setTarget((LookAndFeel) Class.forName(targetClassName).newInstance());
        } catch (Exception e) {
            throw new InternalError(
                    "Unable to instantiate target Look and Feel \"" + targetClassName + "\". " + e.getMessage());
        }
    }

    @Override
    public String getID() {
        return "Aqua";
    }

    @Override
    public String getDescription() {
        return "The Quaqua Look and Feel "
                + QuaquaManager.getVersion()
                + " for Java 1.8";
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
        return "Quaqua";
    }

    /**
     * This method is called once by UIManager.setLookAndFeel to create
     * the look and feel specific defaults table.  Other applications,
     * for example an application builder, may also call this method.
     *
     * @see #initialize
     * @see #uninitialize
     * @see UIManager#setLookAndFeel
     */
    @Override
    public UIDefaults getDefaults() {
        UIDefaults table = target.getDefaults();

        initClassDefaults(table);
        initSystemColorDefaults(table);
        initComponentDefaults(table);

        //installKeyboardFocusManager();
        //installPopupFactory();
        //installMouseGrabber();

        return table;
    }

    @Override
    protected final void initComponentDefaults(UIDefaults table) {
        initResourceBundle(table);
        //initColorDefaults(table);
        //initInputMapDefaults(table);
        initFontDefaults(table);
        initGeneralDefaults(table);
        initDesignDefaults(table);
    }

    protected void initResourceBundle(UIDefaults table) {
        ResourceBundle bundle = ResourceBundle.getBundle(
                "ch.randelshofer.quaqua.Labels",
                Locale.getDefault(),
                getClass().getClassLoader());
        for (Enumeration i = bundle.getKeys(); i.hasMoreElements();) {
            String key = (String) i.nextElement();
            table.put(key, bundle.getObject(key));
        }
    }

    /** Use this to test if an UI is included.
     * An UI may be implicitly or explicitly included, or may be explicitly
     * excluded.
     *
     * @param ui For example "LabelUI".
     * @return True if UI is included.
     */
    protected boolean isUIIncluded(String ui) {
        Set included = QuaquaManager.getIncludedUIs();
        Set excluded = QuaquaManager.getExcludedUIs();

        if (excluded == null) {
            // everyting is implicitly excluded
            return false;
        } else if (included == null && excluded.isEmpty()) {
            // everyting is implicitly included, nothing is explicitly excluded
            return true;
        } else if (included != null && excluded.isEmpty()) {
            // something is explicitly included, nothing is explicitly excluded
            return included.contains(ui);
        } else if (included == null) {
            return !excluded.contains(ui);
        } else {
            // something is explicitly included, something is explicitly excluded
            return included.contains(ui) && !excluded.contains(ui);
        }
    }

    /**
     * Puts defaults into the specified UIDefaults table.
     * Honors QuaquaManager.getIncludedUIs() and QuaquaManager.getExcludedUIs().
     *
     * @param table Table onto which defaults are appended.
     * @param keyValueList Key value list of the defaults.
     */
    protected void putDefaults(UIDefaults table, Object[] keyValueList) {
        Set included = QuaquaManager.getIncludedUIs();
        Set excluded = QuaquaManager.getExcludedUIs();

        if (excluded == null) {
            // everyting is implicitly excluded
            return;
        } else if (included == null && excluded.isEmpty()) {
            // everyting is implicitly included, nothing is explicitly excluded
            table.putDefaults(keyValueList);
        } else if (included != null && excluded.isEmpty()) {
            // something is explicitly included, nothing is explicitly excluded
            for (int i = 0; i < keyValueList.length; i += 2) {
                if (keyValueList[i] instanceof String) {
                    String name = (String) keyValueList[i];
                    int p = name.indexOf('.');
                    if (p == -1 && name.endsWith("UI")) {
                        name = name.substring(0, name.length() - 2);
                        p = 1;
                    } else if (p != -1) {
                        name = name.substring(0, p);
                    }
                    if (p == -1 || name.equals("Component") || included.contains(name)) {
                        table.put(keyValueList[i], keyValueList[i + 1]);
                    }
                } else {
                    table.put(keyValueList[i], keyValueList[i + 1]);
                }
            }
        } else if (included == null) {
            // everything is implicitly included, something is explicitly excluded
            for (int i = 0; i < keyValueList.length; i += 2) {
                if (keyValueList[i] instanceof String) {
                    String name = (String) keyValueList[i];
                    int p = name.indexOf('.');
                    if (p == -1 && name.endsWith("UI")) {
                        name = name.substring(0, name.length() - 2);
                        p = 1;
                    } else if (p != -1) {
                        name = name.substring(0, p);
                    }
                    if (p == -1 || !excluded.contains(name)) {
                        table.put(keyValueList[i], keyValueList[i + 1]);
                    }
                } else {
                    table.put(keyValueList[i], keyValueList[i + 1]);
                }
            }
        } else {
            // something is explicitly included, something is explicitly excluded
            for (int i = 0; i < keyValueList.length; i += 2) {
                if (keyValueList[i] instanceof String) {
                    String name = (String) keyValueList[i];
                    int p = name.indexOf('.');
                    if (p == -1 && name.endsWith("UI")) {
                        name = name.substring(0, name.length() - 2);
                        p = 1;
                    } else if (p != -1) {
                        name = name.substring(0, p);
                    }
                    if (p == -1 || //
                            (name.equals("Component") || included.contains(name))//
                            && !excluded.contains(name)) {
                        table.put(keyValueList[i], keyValueList[i + 1]);
                    }
                } else {
                    table.put(keyValueList[i], keyValueList[i + 1]);
                }
            }
        }
    }

    @Override
    public void uninitialize() {
        uninstallPopupFactory();
        uninstallKeyboardFocusManager();
        //uninstallMouseGrabber();
        super.uninitialize();
    }

    protected void uninstallPopupFactory() {
        try {
            if (PopupFactory.getSharedInstance() instanceof QuaquaPopupFactory) {
                PopupFactory.setSharedInstance(new PopupFactory());
            }
        } catch (SecurityException ex) {
            System.err.print("Warning: " + this + " couldn't uninstall QuaquaPopupFactory.");
            //ex.printStackTrace();
        }
    }

    protected void uninstallKeyboardFocusManager() {
        try {
            if (KeyboardFocusManager.getCurrentKeyboardFocusManager() instanceof QuaquaKeyboardFocusManager) {
                KeyboardFocusManager.setCurrentKeyboardFocusManager(new DefaultKeyboardFocusManager());
            }
            // currentManager.

        } catch (SecurityException ex) {
            System.err.print("Warning: " + this + " couldn't uninstall QuaquaKeyboardFocusManager.");
            //ex.printStackTrace();
        }
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
        String quaquaLionPrefix = "ch.randelshofer.quaqua.lion.QuaquaLion";

        // NOTE: Change code below, to override different
        // UI classes of the target look and feel.
        Object[] uiDefaults = {
            //"BrowserUI", quaquaPrefix + "BrowserUI",
            //"ButtonUI", quaquaPrefix + "ButtonUI",
            //"CheckBoxUI", quaquaPrefix + "CheckBoxUI",
            "ColorChooserUI", quaquaPrefix + "ColorChooserUI",
            "FileChooserUI", quaquaLionPrefix + "FileChooserUI",
            //"FormattedTextFieldUI", quaquaPrefix + "FormattedTextFieldUI",
            //"RadioButtonUI", quaquaPrefix + "RadioButtonUI",
            //"ToggleButtonUI", quaquaPrefix + "ToggleButtonUI",
            //"SeparatorUI", quaquaPantherPrefix + "SeparatorUI",
            //"MenuSeparatorUI", quaquaPantherPrefix + "SeparatorUI",
            //"ProgressBarUI", basicPrefix + "ProgressBarUI",
            //"ScrollBarUI", quaquaPrefix + "ScrollBarUI",
            //"ScrollPaneUI", quaquaPrefix + "ScrollPaneUI",
            //"SplitPaneUI", quaquaPrefix + "SplitPaneUI",
            //"SliderUI", quaquaPrefix + "SliderUI",
            //"SpinnerUI", quaquaPrefix + "SpinnerUI",
            //"ToolBarSeparatorUI", quaquaPrefix + "ToolBarSeparatorUI",
            //"PopupMenuSeparatorUI", quaquaPantherPrefix + "SeparatorUI",
            //"TextAreaUI", quaquaPrefix + "TextAreaUI",
            //"TextFieldUI", quaquaPrefix + "TextFieldUI",
            //"PasswordFieldUI", quaquaPrefix + "PasswordFieldUI",
            //"TextPaneUI", quaquaPrefix + "TextPaneUI",
            //"EditorPaneUI", quaquaPrefix + "EditorPaneUI",
            //"TreeUI", quaquaPrefix + "TreeUI",
            //"LabelUI", quaquaPrefix + "LabelUI",
            //"ListUI", quaquaPrefix + "ListUI",
            //"TabbedPaneUI", quaquaPantherPrefix + "TabbedPaneUI",
            //"ToolBarUI", quaquaPrefix + "ToolBarUI",
            //"ToolTipUI", basicPrefix + "ToolTipUI",
            //"ComboBoxUI", quaquaPrefix + "ComboBoxUI",
            //"TableUI", quaquaPrefix + "TableUI",
            //"TableHeaderUI", quaquaPrefix + "TableHeaderUI",
            // "InternalFrameUI", basicPrefix + "InternalFrameUI",
            //"DesktopPaneUI", quaquaPrefix + "DesktopPaneUI",
            //"DesktopIconUI", basicPrefix + "DesktopIconUI",
            //"OptionPaneUI", quaquaPrefix + "OptionPaneUI",
            //"PanelUI", quaquaPrefix + "PanelUI",
            //"ViewportUI", quaquaPrefix + "ViewportUI",
            // Do not create a RootPaneUI on our own, unless we also
            // create our own ButtonUI. Aqua's RootPaneUI is responsible
            // for updating the border of the ButtonUI, when it is the default,
            // and for propagating window activation/dectivation events to
            // all the child components of a window.
            //"RootPaneUI", quaquaPrefix + "RootPaneUI",
            };
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
                    //"PopupMenuUI", quaquaPrefix + "PopupMenuUI",
        };
        putDefaults(table, uiDefaults);


        // FIXME Menu related workarounds work only if useScreenMenuBar is off.
        if (!isUseScreenMenuBar()) {
            uiDefaults = new Object[]{
                        //"MenuBarUI", quaquaPrefix + "MenuBarUI",
                        //"MenuUI", quaquaPrefix + "MenuUI",
                        //"MenuItemUI", quaquaPrefix + "MenuItemUI",
                        //"CheckBoxMenuItemUI", quaquaPrefix + "MenuItemUI",
                        //"RadioButtonMenuItemUI", quaquaPrefix + "MenuItemUI"
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

    // The following override appears to be pointless

//    @Override
//    protected void initFontDefaults(UIDefaults table) {
//        super.initFontDefaults(table);
//
//        Object smallSystemFont = new UIDefaults.ProxyLazyValue(
//                "javax.swing.plaf.FontUIResource",
//                null,
//                new Object[]{"Lucida Grande", Font.PLAIN, 11});
//        Object emphasizedSmallSystemFont = new UIDefaults.ProxyLazyValue(
//                "javax.swing.plaf.FontUIResource",
//                null,
//                new Object[]{"Lucida Grande", Font.BOLD, 11});
//
//        Object[] uiDefaults = {
//            "FileChooser.previewLabelFont", smallSystemFont,
//            //
//        };
//        putDefaults(table, uiDefaults);
//    }

    protected void initFontDefaults(UIDefaults table) {

        // Imitating AquaLookAndFeel except for the possible substitution for the system font.
        // Unfortunately, the rendering in Java 1.7+ does not match the native rendering.

        int design = QuaquaManager.getDesign();
        String name = design >= QuaquaManager.YOSEMITE ? "Helvetica Neue" : "Lucida Grande";
        Object systemFont = new FontUIResource(name, Font.PLAIN, 13);
        Object smallSystemFont = new FontUIResource(name, Font.PLAIN, 11);
        Object viewFont = new FontUIResource(name, Font.PLAIN, 12);
        Object controlFont = new FontUIResource(name, Font.PLAIN, 13);
        Object controlSmallFont = new FontUIResource(name, Font.PLAIN, 11);
        Object menuFont = new FontUIResource(name, Font.PLAIN, 14);
        Object alertHeaderFont = new FontUIResource(name, Font.BOLD, 13);

        // Set font sizes according to default size style.
        if (QuaquaManager.getProperty("Quaqua.sizeStyle", "regular").equals("small")) {
            viewFont = smallSystemFont;
        }

         Object[] uiDefaults = {
//             "SystemFont", systemFont,
//             "EmphasizedSystemFont", emphasizedSystemFont,
//             "SmallSystemFont", smallSystemFont,
//             "EmphasizedSmallSystemFont", emphasizedSmallSystemFont,
//             "MiniSystemFont", miniSystemFont,
//             "EmphasizedMiniSystemFont", emphasizedMiniSystemFont,
//             "ApplicationFont", applicationFont,
//             "LabelFont", labelFont,
//             "ViewFont", viewFont,
//             "MenuFont", menuFont,
             "Browser.font", viewFont,
             "Button.font", controlFont,
//             "Button.smallFont", smallSystemFont, // Maybe we should use Component.smallFont instead?
//
             "CheckBox.font", controlFont,
             "CheckBoxMenuItem.acceleratorFont", menuFont,
             "CheckBoxMenuItem.font", menuFont,
             "ColorChooser.font", smallSystemFont,
             "ColorChooser.crayonsFont", systemFont,
             "ComboBox.font", controlFont,
             "EditorPane.font", controlFont,
             "FormattedTextField.font", controlFont,
             "FileChooser.previewLabelFont", smallSystemFont,
             "FileChooser.previewValueFont", smallSystemFont,
             "IconButton.font", controlSmallFont,
//             "InternalFrame.optionDialogTitleFont", menuFont,
             "InternalFrame.titleFont", menuFont,
             "InternalFrame.paletteTitleFont", menuFont,
             "InternalFrame.optionDialogTitleFont", menuFont,
             "Label.font", controlFont,
             "List.font", viewFont,
//             "List.focusCellHighlightBorder",
//             new UIDefaults.ProxyLazyValue(
//             "javax.swing.plaf.BorderUIResource$LineBorderUIResource",
//             new Object[]{table.get("listHighlightBorder")}),
//             "List.cellNoFocusBorder",
//             new UIDefaults.ProxyLazyValue(
//             "javax.swing.plaf.BorderUIResource$EmptyBorderUIResource",
//             new Object[]{1, 1, 1, 1}),
//             "Menu.acceleratorFont", menuFont,
             "Menu.font", menuFont,
             "MenuBar.font", menuFont,
             "MenuItem.acceleratorFont", menuFont,
             "MenuItem.font", menuFont,
             "OptionPane.buttonFont", controlFont,
             "OptionPane.font", alertHeaderFont,
//             // We use a plain font for HTML messages to make the examples in the
//             // Java Look and Feel Guidelines work.
             "OptionPane.messageFont", controlFont,
//             "OptionPane.htmlMessageFont", systemFont,
             "Panel.font", controlFont,
             "PasswordField.font", controlFont,
//             "PopupMenu.font", menuFont,
             "ProgressBar.font", controlFont,
             "RadioButton.font", controlFont,
             "RadioButtonMenuItem.acceleratorFont", menuFont,
             "RadioButtonMenuItem.font", menuFont,
//             "RootPane.font", systemFont,
//             "ScrollBar.font", systemFont,
             "ScrollPane.font", controlFont,
             "Slider.font", controlSmallFont,
//             "Slider.labelFont", labelFont,
             "Spinner.font", controlFont,
             "TabbedPane.font", controlFont,
             "TabbedPane.smallFont", controlSmallFont,
//             "TabbedPane.wrap.font", systemFont,
//             "TabbedPane.wrap.smallFont", smallSystemFont, // ??
//             "TabbedPane.scroll.font", systemFont,
//             "TabbedPane.scroll.smallFont", smallSystemFont, // ??
             "Table.font", viewFont,
             "TableHeader.font", controlSmallFont,
             "TextArea.font", controlFont,
             "TextField.font", controlFont,
             "TextPane.font", controlFont,
             "TitledBorder.font", controlFont,
             "ToggleButton.font", controlFont,
             "ToolBar.font",  controlFont,
//             "ToolBar.titleFont", miniSystemFont,
             "ToolTip.font", controlSmallFont,
             "Tree.font", viewFont,
             "Tree.sideBarCategory.font", new FontUIResource(name, Font.BOLD, 11),
             "Tree.sideBar.font", new FontUIResource(name, Font.PLAIN, 13),
             "Tree.sideBar.selectionFont", new FontUIResource(name, Font.BOLD, 13),
//             "Viewport.font", systemFont,
            };

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

    /**
     * The defaults initialized here are common to all Quaqua Look and Feels.
     * @param table Table onto which defaults are to be appended.
     */
    protected void initGeneralDefaults(UIDefaults table) {

        // Focus behavior
        Boolean isRequestFocusEnabled = OSXConfiguration.isRequestFocusEnabled();

        // True if all controls are focusable,
        // false if only text boxes and lists are focusable.
        Boolean allControlsFocusable = OSXConfiguration.isFullKeyboardAccess();

        Object dialogBorder = new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaBorders$DialogBorder");

        Object questionDialogBorder = new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaBorders$QuestionDialogBorder");
        // Shared colors
        ColorUIResource listSelectionBorderColor = (ColorUIResource) table.get("listHighlightBorder");
        Color menuBackground = (Color) table.get("menu");

        // Set visual margin.
        int[] values = QuaquaManager.getProperty("Quaqua.visualMargin", new int[]{3, 3, 3, 3});
        InsetsUIResource visualMargin = new InsetsUIResource(values[0], values[1], values[2], values[3]);

        // Opaqueness
        Boolean opaque = Boolean.valueOf(QuaquaManager.getProperty("Quaqua.opaque", "false"));

        // Autovalidation
        Boolean autovalidate = Boolean.valueOf(QuaquaManager.getProperty("Quaqua.FileChooser.autovalidate", "true"));

        // Popup menus for all text components
        Object textComponentPopupHandler = new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.QuaquaTextComponentPopupHandler");
        // Focus handler for all text fields
        Object textFieldFocusHandler = new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaTextFieldFocusHandler");

        // TextField auto selection
        Boolean autoselect = Boolean.valueOf(QuaquaManager.getProperty("Quaqua.TextComponent.autoSelect", "true"));
        // *** Shared Borders
        Object textFieldBorder = new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaNativeTextFieldBorder$UIResource",
                new Object[]{new Insets(0,0,0,0), new Insets(6,8,6,8), true});

        Object buttonBorder = new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaNativeButtonBorder$UIResource"
                );

        // True if file choosers orders by type
        boolean isOrderFilesByType = false;
        // True if file choosers shows all files by default
        boolean isFileHidingEnabled = OSXConfiguration.isFileHidingEnabled();
        boolean isQuickLookEnabled = OSXConfiguration.isIsQuickLookEnabled();

        // Enforce visual margin
        // Set this to true, to workaround Matisse issue #
        //
        // Enforce margin is used to workaround a workaround in the Matisse
        // design tool for NetBeans. Matisse removes borders from some
        // components in order to workaround some ugliness in the look
        // and feels that ship with the J2SE.
        Boolean enforceVisualMargin = Boolean.valueOf(QuaquaManager.getProperty("Quaqua.enforceVisualMargin", "false"));

        String commonDir = "/ch/randelshofer/quaqua/images/";
        String leopardDir = "/ch/randelshofer/quaqua/leopard/images/";
        String colorChooserDir = "/ch/randelshofer/quaqua/yosemite/colorchooser/images/";

        Object[] uiDefaults = {
            //"Browser.sizeHandleIcon", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.QuaquaIconFactory", "createIcon",
            //new Object[]{commonDir + "Browser.sizeHandleIcon.png", 1, Boolean.TRUE, 1}),
            //
//            "Button.actionMap", new QuaquaLazyActionMap(QuaquaButtonListener.class),
//            "Button.border", buttonBorder,
//            //
//            // This must be set to false to make default button on option panes
//            // work as expected when running Java 1.5.
//            "Button.defaultButtonFollowsFocus", Boolean.FALSE,
//            "Button.margin", new InsetsUIResource(2, 2, 2, 2),
//            "Button.opaque", opaque,
//            "Button.textIconGap", 4,
//            "Button.textShiftOffset", 0,
//            "Button.helpIcon",  makeNativeButtonStateIcon(OSXAquaPainter.Widget.buttonRoundHelp,0,1,21,21,true),
//            "Button.smallHelpIcon",  makeNativeButtonStateIcon(OSXAquaPainter.Widget.buttonRoundHelp,0,1,18,18,true),
//            "Button.miniHelpIcon",  makeNativeButtonStateIcon(OSXAquaPainter.Widget.buttonRoundHelp,1,1,15,15,true),
//            "Button.requestFocusEnabled", isRequestFocusEnabled,
//            // Note: Minimum width only affects regular sized buttons with push button style
//            "Button.minimumWidth", 80,
//            "Button.focusable", allControlsFocusable,
//            //
//            //"CheckBox.background", ...,
//            "CheckBox.border", new VisualMarginBorder(0, 0, 0, 0),
//            "CheckBox.icon", makeNativeButtonStateIcon(OSXAquaPainter.Widget.buttonCheckBox, 0,0,16,20,true),
//            "CheckBox.smallIcon", makeNativeButtonStateIcon(OSXAquaPainter.Widget.buttonCheckBox, 0,0,14,16,true),
//            "CheckBox.miniIcon", makeNativeButtonStateIcon(OSXAquaPainter.Widget.buttonCheckBox, 0,-1,10,15,true),
//            "CheckBox.margin", new InsetsUIResource(0, 0, 0, 0),
//            "CheckBox.opaque", opaque,
//            //"CheckBox.select", ...,
//            "CheckBox.textIconGap", 4,
//            "CheckBox.textShiftOffset", 0,
//            "CheckBox.requestFocusEnabled", isRequestFocusEnabled,
//            "CheckBoxMenuItem.borderPainted", Boolean.TRUE,
//            "CheckBox.focusable", allControlsFocusable,
//            // Set this to true, to workaround Matisse issue #
//            // Enforce margin is used to workaround a workaround in the Matisse
//            // design tool for NetBeans. Matisse removes borders from some
//            // components in order to workaround some ugliness in the look
//            // and feels that ship with the J2SE.
//            "CheckBox.enforceVisualMargin", enforceVisualMargin,
            //
            // class names of default choosers
            "ColorChooser.defaultChoosers", new String[]{
                "ch.randelshofer.quaqua.colorchooser.ColorWheelChooser",
                "ch.randelshofer.quaqua.colorchooser.ColorSlidersChooser",
                "ch.randelshofer.quaqua.colorchooser.ColorPalettesChooser",
                "ch.randelshofer.quaqua.colorchooser.SwatchesChooser",
                "ch.randelshofer.quaqua.colorchooser.CrayonsChooser",
                "ch.randelshofer.quaqua.colorchooser.QuaquaColorPicker",},
            //"ColorChooser.swatchesDefaultRecentColor", ...,
            //"ColorChooser.swatchesRecentSwatchSize", ...,
            "ColorChooser.swatchesSwatchSize", new DimensionUIResource(5, 5),
            "ColorChooser.resetMnemonic", -1,
            "ColorChooser.crayonsImage", makeIcon(colorChooserDir + "ColorChooser.crayons.png"),
            "ColorChooser.textSliderGap", 0,
            "ColorChooser.colorPalettesIcon", makeButtonStateIcon(commonDir + "ColorChooser.colorPalettesIcons.png", 3),
            "ColorChooser.colorSlidersIcon", makeButtonStateIcon(commonDir + "ColorChooser.colorSlidersIcons.png", 3),
            "ColorChooser.colorSwatchesIcon", makeButtonStateIcon(commonDir + "ColorChooser.colorSwatchesIcons.png", 3),
            "ColorChooser.colorWheelIcon", makeIcon(colorChooserDir + "ColorChooser.colorWheelIcon.png"),
            "ColorChooser.crayonsIcon", makeButtonStateIcon(commonDir + "ColorChooser.crayonsIcons.png", 3),
            "ColorChooser.imagePalettesIcon", makeButtonStateIcon(commonDir + "ColorChooser.imagePalettesIcons.png", 3),
            // Icon of the color picker tool
            "ColorChooser.colorPickerIcon", makeIcon(getClass(), commonDir + "ColorChooser.colorPickerIcon.png"),
            // Magnifying glass used as the cursor image
            "ColorChooser.colorPickerMagnifier", makeBufferedImage(commonDir + "ColorChooser.colorPickerMagnifier.png"),
            // Hot spot of the magnifier cursor
            "ColorChooser.colorPickerHotSpot", new UIDefaults.ProxyLazyValue("java.awt.Point", new Object[]{29, 29}),
            // Pick point relative to hot spot
            "ColorChooser.colorPickerPickOffset", new UIDefaults.ProxyLazyValue("java.awt.Point", new Object[]{-13, -13}),
            // Rectangle used for drawing the mask of the magnifying glass
            "ColorChooser.colorPickerGlassRect", new UIDefaults.ProxyLazyValue("java.awt.Rectangle", new Object[]{2, 2, 29, 29}),
            // Capture rectangle. Width and height must be equal sized and must be odd.
            // The position of the capture rectangle is relative to the hot spot.
            "ColorChooser.colorPickerCaptureRect", new UIDefaults.ProxyLazyValue("java.awt.Rectangle", new Object[]{-15, -15, 5, 5}),
            // Zoomed (magnified) capture image. Width and height must be a multiple of the capture rectangles size.
            "ColorChooser.colorPickerZoomRect", new UIDefaults.ProxyLazyValue("java.awt.Rectangle", new Object[]{4, 4, 25, 25}),
            "ColorChooser.ColorSlider.uiClassName", "ch.randelshofer.quaqua.quaqua18.ColorSliderUI18",
            "ColorChooser.ColorSlider.northThumb.small", makeSliderThumbIcon(commonDir + "Slider.northThumbs.small.png"),
            "ColorChooser.ColorSlider.westThumb.small", makeSliderThumbIcon(commonDir + "Slider.westThumbs.small.png"),
            //
//            "ComboBox.buttonBorder", makeNativeButtonStateBorder(OSXAquaPainter.Widget.buttonComboBox, new Insets(0, -10, 0, -2), new Insets(0, 0, 0, 0), true),
//            "ComboBox.smallButtonBorder", makeNativeButtonStateBorder(OSXAquaPainter.Widget.buttonComboBox, new Insets(0, -20, 0, -1), new Insets(0, 0, 0, 0), true),
//            "ComboBox.miniButtonBorder", makeNativeButtonStateBorder(OSXAquaPainter.Widget.buttonComboBox, new Insets(0, -12, 0, -2), new Insets(0, 0, 0, 0), true),
//            "ComboBox.cellBorder", null,
//            "ComboBox.editorBorder", textFieldBorder,
//            "ComboBox.smallCellBorder", null,
//            "ComboBox.cellAndButtonBorder", makeNativeButtonStateBorder(OSXAquaPainter.Widget.buttonPopUp, new Insets(1, 1, 0, 0), new Insets(3, 3, 3, 3), true),
//            "ComboBox.smallCellAndButtonBorder", makeNativeButtonStateBorder(OSXAquaPainter.Widget.buttonPopUp, new Insets(1, 0, 0, 0), new Insets(3, 3, 3, 3), true),
//            "ComboBox.miniCellAndButtonBorder", makeNativeButtonStateBorder(OSXAquaPainter.Widget.buttonPopUp, new Insets(1, 2, 0, 1), new Insets(3, 3, 3, 3), true),
//            "ComboBox.buttonInsets", new Insets(-3, -3, -3, -3),
//            "ComboBox.border", new VisualMarginBorder(2, 0, 2, 0),
//            "ComboBox.dropDownIcon", null,
//            "ComboBox.opaque", opaque,
//            "ComboBox.popupIcon", null,
//            "ComboBox.smallPopupIcon", null,
//            "ComboBox.miniPopupIcon", null,
//            "ComboBox.cellEditorPopupIcon", makeButtonStateIcon(commonDir + "ComboBox.small.popupIcons.png", 6),
//            "ComboBox.smallDropDownIcon", null,
//            "ComboBox.miniDropDownIcon", null,
//            "ComboBox.dropDownWidth",18,
//            "ComboBox.smallDropDownWidth",16,
//            "ComboBox.miniDropDownWidth",14,
//            "ComboBox.popupWidth",19,
//            "ComboBox.smallPopupWidth",17,
//            "ComboBox.miniPopupWidth",16,
//            "ComboBox.maximumRowCount", 8,
//            "ComboBox.arrowButtonInsets",new InsetsUIResource(4, 8,3,5),
//            "ComboBox.smallArrowButtonInsets",new InsetsUIResource(4, 6,3,5),
//            "ComboBox.miniArrowButtonInsets",new InsetsUIResource(4, 3,3,5),
//            "ComboBox.requestFocusEnabled", isRequestFocusEnabled,
//            "ComboBox.showPopupOnNavigation", Boolean.TRUE,
//            // Set this to Boolean.TRUE to get the same preferred height for
//            // non-editable combo boxes and editable-combo boxes.
//            "ComboBox.harmonizePreferredHeight", Boolean.FALSE,
//            // The values for this margin are ignored. We dynamically compute a margin
//            // for the various button styles that we support, if we encounter a
//            // a margin that is an instanceof a UIResource.
//            "ComboBoxButton.margin", new InsetsUIResource(0, 0, 0, 0),
//            // Setting this to true makes the combo box UI change the foreground
//            // color of the editor to the the foreground color of the JComboBox.
//            // True is needed for rendering of combo boxes in JTables.
//            "ComboBox.changeEditorForeground", Boolean.TRUE,
//
//            "ComboBox.focusable", allControlsFocusable,


//            //
//            // The visual margin is used to allow each component having room
//            // for a cast shadow and a focus ring, and still supporting a
//            // consistent visual arrangement of all components aligned to their
//            // visualy perceived lines.
//            // FIXME: This should be either a global system property
//            // "Quaqua.visualMargin" or a per-component property e.g.
//            // "Button.visualMargin".
            "Component.visualMargin", visualMargin,
//            //
//            //"DesktopIcon.border", ...
//
//            //"EditorPane.border", ...
//            //"EditorPane.caretBlinkRate", ...
//            "EditorPane.margin", new InsetsUIResource(1, 3, 1, 3),
//            "EditorPane.popupHandler", textComponentPopupHandler,
            //
            "FileChooser.autovalidate", autovalidate,
            //
            "FileChooser.browserFocusCellHighlightBorder",
            new UIDefaults.ProxyLazyValue(
            "javax.swing.plaf.BorderUIResource$LineBorderUIResource",
            new Object[]{table.get("listHighlightBorder")}),
            "FileChooser.browserCellBorder",
            new UIDefaults.ProxyLazyValue(
            "javax.swing.plaf.BorderUIResource$EmptyBorderUIResource",
            new Object[]{new Insets(1, 1, 1, 1)}),
            "FileChooser.disclosureButtonIcon", makeButtonStateIcon(
            leopardDir + "FileChooser.disclosureButtonIcons.png", 10),
            //
            "FileChooser.fileHidingEnabled", isFileHidingEnabled,
            "FileChooser.quickLookEnabled", isQuickLookEnabled,
            "FileChooser.homeFolderIcon", makeIcon(getClass(), commonDir + "FileChooser.homeFolderIcon.png"),
            "FileChooser.orderByType", isOrderFilesByType,
            "FileChooser.previewLabelForeground", new ColorUIResource(0x000000),
            "FileChooser.previewValueForeground", new ColorUIResource(0x000000),
            "FileChooser.previewLabelInsets", new InsetsUIResource(1, 0, 0, 1),
            "FileChooser.previewLabelDelimiter", ":",
            "FileChooser.splitPaneDividerSize", 4,
            "FileChooser.speed", (QuaquaManager.getProperty("Quaqua.FileChooser.speed") != null && QuaquaManager.getProperty("Quaqua.FileChooser.speed").equals("true")),
            //
            "FileView.computerIcon", makeIcon(getClass(), commonDir + "FileView.computerIcon.png"),
            "FileView.directoryIcon", makeIcon(getClass(), commonDir + "FileView.directoryIcon.png"),
            "FileView.fileIcon", makeIcon(getClass(), commonDir + "FileView.fileIcon.png"),
            "FileView.aliasBadge", makeIcon(getClass(), commonDir + "FileView.aliasBadge.png"),
            //
//           "FormattedTextField.border", textFieldBorder,
//            "FormattedTextField.opaque", opaque,
//            "FormattedTextField.focusHandler", textFieldFocusHandler,
//            "FormattedTextField.popupHandler", textComponentPopupHandler,
//            "FormattedTextField.autoSelect", autoselect,
//            "Label.border", new VisualMarginBorder(0, 0, 0, 0),
//            "Label.opaque", opaque,
            //
            "List.cellRenderer", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.QuaquaDefaultListCellRenderer"),
            //
//            "Menu.borderPainted", Boolean.TRUE,
//            "MenuItem.borderPainted", Boolean.TRUE,
//            // The negative values are used to take account for the visual margin
//            "OptionPane.border", new BorderUIResource.EmptyBorderUIResource(15 - 3, 24 - 3, 20 - 3, 24 - 3),
//            "OptionPane.messageAreaBorder", new BorderUIResource.EmptyBorderUIResource(0, 0, 0, 0),
//            "OptionPane.buttonAreaBorder", new BorderUIResource.EmptyBorderUIResource(16 - 3, 0, 0, 0),
//            "OptionPane.errorIcon", new UIDefaults.ProxyLazyValue(
//            "ch.randelshofer.quaqua.QuaquaIconFactory", "createOptionPaneIcon", new Object[]{JOptionPane.ERROR_MESSAGE}),
//            "OptionPane.errorIconResource", "/ch/randelshofer/quaqua/images/OptionPane.errorIcon.png",
//            "OptionPane.informationIcon", new UIDefaults.ProxyLazyValue(
//            "ch.randelshofer.quaqua.QuaquaIconFactory", "createOptionPaneIcon", new Object[]{JOptionPane.INFORMATION_MESSAGE}),
//            "OptionPane.questionIcon", new UIDefaults.ProxyLazyValue(
//            "ch.randelshofer.quaqua.QuaquaIconFactory", "createOptionPaneIcon", new Object[]{JOptionPane.QUESTION_MESSAGE}),
//            "OptionPane.warningIcon", new UIDefaults.ProxyLazyValue(
//            "ch.randelshofer.quaqua.QuaquaIconFactory", "createOptionPaneIcon", new Object[]{JOptionPane.WARNING_MESSAGE}),
//            "OptionPane.warningIconResource", "/ch/randelshofer/quaqua/images/OptionPane.warningIcon.png",
//            "OptionPane.css", "<head>"
//            + "<style type=\"text/css\">"
//            + "b { font: 13pt \"" + systemFontName + "\" }"
//            + "p { font: 11pt \"" + systemFontName + "\"; margin-top: 8px }"
//            + "</style>"
//            + "</head>",
//            "OptionPane.messageLabelWidth", 360,
//            "OptionPane.maxCharactersPerLineCount", 60,
//            "Panel.opaque", opaque,
//            //
//            "PopupMenu.enableHeavyWeightPopup", Boolean.TRUE,
//            //
//            "PasswordField.border", textFieldBorder,
//            "PasswordField.opaque", opaque,
//            "PasswordField.focusHandler", textFieldFocusHandler,
//            "PasswordField.popupHandler", textComponentPopupHandler,
//            "PasswordField.autoSelect", autoselect,
//            //
//            "RadioButton.border", new VisualMarginBorder(0, 0, 0, 0),
//            // The values for this margin are ignored. We dynamically compute a margin
//            // for the various button styles that we support, if we encounter a
//            // a margin that is an instanceof a UIResource.
//            "RadioButton.margin", new InsetsUIResource(0, 0, 0, 0),
//            "RadioButton.icon", makeNativeButtonStateIcon(OSXAquaPainter.Widget.buttonRadio, 0,1,16,20,true),
//            "RadioButton.smallIcon", makeNativeButtonStateIcon(OSXAquaPainter.Widget.buttonRadio, 0,1,14,17,true),
//            "RadioButton.miniIcon", makeNativeButtonStateIcon(OSXAquaPainter.Widget.buttonRadio, 0,1,10,16,true),
//            "RadioButton.opaque", opaque,
//            "RadioButton.textIconGap", 4,
//            "RadioButton.textShiftOffset", 0,
//            "RadioButton.requestFocusEnabled", isRequestFocusEnabled,
//            "RadioButtonMenuItem.borderPainted", Boolean.TRUE,
//            "RadioButton.enforceVisualMargin", enforceVisualMargin,
//            "RadioButton.focusable", allControlsFocusable,
//            //
//            // RootPane
//            "RootPane.opaque", Boolean.TRUE,
//            "RootPane.frameBorder", new UIDefaults.ProxyLazyValue(
//            "ch.randelshofer.quaqua.QuaquaBorders$FrameBorder"),
//            "RootPane.plainDialogBorder", dialogBorder,
//            "RootPane.informationDialogBorder", dialogBorder,
//            "RootPane.errorDialogBorder", new UIDefaults.ProxyLazyValue(
//            "ch.randelshofer.quaqua.QuaquaBorders$ErrorDialogBorder"),
//            "RootPane.colorChooserDialogBorder", questionDialogBorder,
//            "RootPane.fileChooserDialogBorder", questionDialogBorder,
//            "RootPane.questionDialogBorder", questionDialogBorder,
//            "RootPane.warningDialogBorder", new UIDefaults.ProxyLazyValue(
//            "ch.randelshofer.quaqua.QuaquaBorders$WarningDialogBorder"),
//            // These bindings are only enabled when there is a default
//            // button set on the rootpane.
//            "RootPane.defaultButtonWindowKeyBindings", new Object[]{
//                "ENTER", "press",
//                "released ENTER", "release",
//                "ctrl ENTER", "press",
//                "ctrl released ENTER", "release"
//            },
//            // Setting this property to null disables snapping
//            // Note: snapping is only in effect for look and feel decorated
//            // windows
//            "RootPane.windowSnapDistance", 10,
//            // Default value for "apple.awt.draggableWindowBackground"
//            "RootPane.draggableWindowBackground", Boolean.FALSE,
//            // Default value for "apple.awt.windowShadow"
//            "RootPane.windowShadow", Boolean.TRUE,
//            "ScrollBar.focusable", Boolean.FALSE,
//            //
//            "ScrollPane.requesFocusEnabled", Boolean.FALSE,
//            "ScrollPane.focusable", Boolean.FALSE,
//            "ScrollPane.opaque", opaque,
//            "ScrollPane.growBoxSize",new DimensionUIResource(0,0),
//            //
//            "Separator.border", new VisualMarginBorder(),
//            //
//            "Sheet.showAsSheet", Boolean.TRUE,
//            //
//            "Slider.roundThumb", makeSliderThumbIcon(commonDir + "Slider.roundThumbs.png"),
//            "Slider.roundThumb.small", makeSliderThumbIcon(commonDir + "Slider.roundThumbs.small.png"),
//            "Slider.southThumb", makeSliderThumbIcon(commonDir + "Slider.southThumbs.png"),
//            "Slider.eastThumb", makeSliderThumbIcon(commonDir + "Slider.eastThumbs.png"),
//            "Slider.northThumb", makeSliderThumbIcon(commonDir + "Slider.northThumbs.png"),
//            "Slider.westThumb", makeSliderThumbIcon(commonDir + "Slider.westThumbs.png"),
//            "Slider.eastThumb.small", makeSliderThumbIcon(commonDir + "Slider.eastThumbs.small.png"),
//            "Slider.southThumb.small", makeSliderThumbIcon(commonDir + "Slider.southThumbs.small.png"),
//            "Slider.northThumb.small", makeSliderThumbIcon(commonDir + "Slider.northThumbs.small.png"),
//            "Slider.westThumb.small", makeSliderThumbIcon(commonDir + "Slider.westThumbs.small.png"),
//            "Slider.opaque", opaque,
//            "Slider.requestFocusEnabled", isRequestFocusEnabled,
//            "Slider.tickColor", new ColorUIResource(0x808080),
//            "Slider.focusInsets", new Insets(0, 0, 0, 0),
//            "Slider.verticalTracks", makeImageBevelBorders(commonDir + "Slider.verticalTracks.png", new Insets(4, 5, 4, 0), 2, true),
//            "Slider.horizontalTracks", makeImageBevelBorders(commonDir + "Slider.horizontalTracks.png", new Insets(5, 4, 0, 4), 2, false),
//            "Slider.focusable", allControlsFocusable,
//            //
//            "Spinner.arrowButtonBorder", null,
//            "Spinner.arrowButtonInsets", null,
//            "Spinner.border", null,
//            "Spinner.editorBorderPainted", Boolean.TRUE,
//            "Spinner.opaque", opaque,
//            "Spinner.north", makeButtonStateIcon(commonDir + "Spinner.north.png", 10),
//            "Spinner.south", makeButtonStateIcon(commonDir + "Spinner.south.png", 10),
//            "Spinner.smallNorth", makeButtonStateIcon(commonDir + "Spinner.small.north.png", 10),
//            "Spinner.smallSouth", makeButtonStateIcon(commonDir + "Spinner.small.south.png", 10),
//            //"SplitPane.actionMap", ???,
//            //"SplitPane.ancestorInputMap", ???,
//            "SplitPane.opaque", opaque,
//            "SplitPane.border", null,
//            "SplitPane.dividerSize", 10,
//            "SplitPane.thumbDimple", makeIcon(getClass(), commonDir + "SplitPane.thumbDimple.png"),
//            "SplitPane.barDimple", makeIcon(getClass(), commonDir + "SplitPane.barDimple.png"),
//            "SplitPane.hBar", makeImageBevelBorder(commonDir + "SplitPane.hBar.png", new Insets(4, 0, 5, 0), true),
//            "SplitPane.vBar", makeImageBevelBorder(commonDir + "SplitPane.vBar.png", new Insets(0, 4, 0, 5), true),
//            "SplitPane.upArrow", makeIcon(getClass(), commonDir + "SplitPane.upArrow.png"),
//            "SplitPane.downArrow", makeIcon(getClass(), commonDir + "SplitPane.downArrow.png"),
//            "SplitPane.rightArrow", makeIcon(getClass(), commonDir + "SplitPane.rightArrow.png"),
//            "SplitPane.leftArrow", makeIcon(getClass(), commonDir + "SplitPane.leftArrow.png"),
//            "SplitPane.focusable", Boolean.FALSE,
//            "SplitPane.requestFocusEnabled", Boolean.FALSE,
//            "SplitPaneDivider.border", null,
//            "SplitPaneDivider.focusable", Boolean.FALSE,
//            "SplitPaneDivider.requestFocusEnabled", Boolean.FALSE,
//            //
//            "TabbedPane.opaque", opaque,
//            "TabbedPane.wrap.opaque", opaque,
//            "TabbedPane.scroll.opaque", opaque,
//            "TabbedPane.requestFocusEnabled", isRequestFocusEnabled,
//            "TabbedPane.textIconGap", 4,
//            "TabbedPane.scroll.textIconGap", 4,
//            "TabbedPane.wrap.textIconGap", 4,
//            "Table.focusCellHighlightBorder", new BorderUIResource.LineBorderUIResource(listSelectionBorderColor),
//            //"Table.focusCellHighlightBorder", new BorderUIResource.LineBorderUIResource(Color.black),
//            //"TableHeader.cellBorder", new UIDefaults.ProxyLazyValue(
//            //"ch.randelshofer.quaqua.QuaquaTableHeaderBorder$UIResource",
//            //new Object[]{commonDir + "TableHeader.borders.png", new Insets(6, 1, 9, 1)}),
//            //
//            "TextArea.margin", new InsetsUIResource(1, 3, 1, 3),
//            "TextArea.opaque", Boolean.TRUE,
//            "TextArea.popupHandler", textComponentPopupHandler,
//            //
//            "TextComponent.showNonEditableCaret",QuaquaManager.getProperty("Quaqua.showNonEditableCaret", "true").equals("true"),
//            //
//            "TextField.border", textFieldBorder,
//            "TextField.opaque", opaque,
//            "TextField.focusHandler", textFieldFocusHandler,
//            "TextField.popupHandler", textComponentPopupHandler,
//            "TextField.autoSelect", autoselect,
//            //
//            "TextPane.margin", new InsetsUIResource(1, 3, 1, 3),
//            "TextPane.opaque", Boolean.TRUE,
//            "TextPane.popupHandler", textComponentPopupHandler,
//            //
//            "ToggleButton.border", buttonBorder,
//            "ToggleButton.margin", new InsetsUIResource(2, 2, 2, 2),
//            "ToggleButton.opaque", opaque,
//            "ToggleButton.textIconGap", 4,
//            "ToggleButton.textShiftOffset", 0,
//            "ToggleButton.requestFocusEnabled", isRequestFocusEnabled,
//            "ToggleButton.focusable", allControlsFocusable,
//            //
//            "ToolBar.border", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.QuaquaNativeToolBarBorder$UIResource"),
//            // The separatorSize is set to null, because we dynamically compute different
//            // sizes depending on the orientation of the separator.
//            "ToolBar.separatorSize", null,
//            "ToolBar.margin", new InsetsUIResource(0, 0, 0, 0),
//            "ToolBar.borderBright", new AlphaColorUIResource(0x999999),
//            "ToolBar.borderDark", new ColorUIResource(0x8c8c8c),
//            "ToolBar.borderDivider", new ColorUIResource(0x9f9f9f),
//            "ToolBar.borderDividerInactive", new ColorUIResource(0x9f9f9f),
//            "ToolBar.bottom.gradient", new Color[]{new Color(0xd8d8d8), new Color(0xbdbdbd), new Color(0xaeaeae), new Color(0x969696)},
//            "ToolBar.bottom.gradientInactive", new Color[]{new Color(0xeeeeee), new Color(0xe4e4e4), new Color(0xcfcfcf)},
//            // The toolbar is opaque because of the gradient that we want to paint.
//            "ToolBar.opaque", Boolean.TRUE,
//            //
//            "ToolTip.border", new BorderUIResource.LineBorderUIResource(new ColorUIResource(0x303030)),
            //
            //"Tree.collapsedIcon", makeIcon(getClass(), commonDir + "Tree.collapsedIcon.png"),
            //"Tree.expandedIcon", makeIcon(getClass(), commonDir + "Tree.expandedIcon.png"),
            //"Tree.leftChildIndent", 7,
            "Tree.line", new AlphaColorUIResource(0x00000000),
            //"Tree.paintLines", Boolean.FALSE,
            //"Tree.rightChildIndent", 13,
            //"Tree.rowHeight", 19,
            //"Tree.leafIcon", makeIcon(getClass(), commonDir + "Tree.leafIcon.png"),
            //"Tree.openIcon", makeIcon(getClass(), commonDir + "Tree.openIcon.png"),
            //"Tree.closedIcon", makeIcon(getClass(), commonDir + "Tree.closedIcon.png"),
            "Tree.showsRootHandles", Boolean.TRUE,
            //"Tree.editorBorder", new VisualMarginBorder(3,3,3,3),

//            "Viewport.opaque", Boolean.TRUE,
            "Quaqua.Debug.colorizePaintEvents", (QuaquaManager.getProperty("Quaqua.Debug.colorizePaintEvents", "false")),
            "Quaqua.Debug.showClipBounds", (QuaquaManager.getProperty("Quaqua.Debug.showClipBounds", "false").equals("true")),
            "Quaqua.Debug.showVisualBounds", (QuaquaManager.getProperty("Quaqua.Debug.showVisualBounds", "false").equals("true")),
            "Quaqua.Debug.clipBoundsForeground", new AlphaColorUIResource(0, 0, 255, 128),
            "Quaqua.Debug.componentBoundsForeground", new AlphaColorUIResource(255, 0, 0, 128),
            "Quaqua.Debug.textBoundsForeground", new AlphaColorUIResource(255, 0, 0, 128),
            "ClassLoader", getClass().getClassLoader(),};
        putDefaults(table, uiDefaults);
    }

    protected void initDesignDefaults(UIDefaults table) {
        ColorUIResource disabledForeground = new ColorUIResource(128, 128, 128);
        Object menuBackground = new ColorUIResource(0xffffff);
        ColorUIResource menuSelectionForeground = new ColorUIResource(0xffffff);
        Object toolBarBackground = table.get("control");
        Object panelBackground = new ColorUIResource(0xededed);
        ColorUIResource listAlternateBackground = new ColorUIResource(0xf3f6fa);

        BorderUIResource.CompoundBorderUIResource browserCellBorder = new BorderUIResource.CompoundBorderUIResource(
                new BorderUIResource.MatteBorderUIResource(0, 0, 1, 0, new ColorUIResource(0xffffff)),
                new BorderUIResource.EmptyBorderUIResource(0, 4, 1, 0));
        Object scrollPaneBorder = new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaNativeScrollPaneBorder"
                );
        String sideBarIconsPrefix = "/System/Library/CoreServices/CoreTypes.bundle/Contents/Resources/Sidebar";
        ColorUIResource sideBarIconColor = new ColorUIResource(125,134,147);
        ColorUIResource sideBarIconSelectionColor = new ColorUIResource(0xffffff);
        Object scrollBarThumb=new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.lion.QuaquaLionScrollBarThumbBorder");
        Object scrollBarTrack=new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.lion.QuaquaLionScrollBarTrackBorder");

        String iconPrefix = "/System/Library/CoreServices/CoreTypes.bundle/Contents/Resources/";

        String leopardDir = "/ch/randelshofer/quaqua/leopard/images/";
        String lionDir = "/ch/randelshofer/quaqua/lion/images/";
        String snowLeopardDir = "/ch/randelshofer/quaqua/snowleopard/images/";
        String expandedDir = "/ch/randelshofer/quaqua/images/Browser.expanded/";

        Object[] uiDefaults = {
            "Browser.expandedIcon", makeIcon(expandedDir + "Unselected.png"),
            "Browser.expandingIcon", makeIcon(expandedDir + "Unselected.png"),
            "Browser.focusedSelectedExpandedIcon", makeIcon(expandedDir + "SelectedFocused.png"),
            "Browser.focusedSelectedExpandingIcon", makeIcon(expandedDir + "SelectedFocused.png"),
            "Browser.selectedExpandedIcon", makeIcon(expandedDir + "SelectedUnfocused.png"),
            "Browser.selectedExpandingIcon", makeIcon(expandedDir + "SelectedUnfocused.png"),
            //
            "Browser.selectionBackground", new ColorUIResource(30, 107, 214),
            "Browser.selectionForeground", new ColorUIResource(255, 255, 255),
            "Browser.inactiveSelectionBackground", new ColorUIResource(220, 220, 220),
            "Browser.inactiveSelectionForeground", new ColorUIResource(0, 0, 0),
            "Browser.sizeHandleIcon", makeIcon(getClass(), lionDir + "Browser.sizeHandleIcon.png"),
            //
            "ColorChooser.unifiedTitleBar", Boolean.TRUE,
            //
//            "ComboBox.selectionBackground", new GradientColor(new ColorUIResource(0x4455f0), new ColorUIResource(0x5a7eeb), new ColorUIResource(0x2560f3)),
//            "ComboBox.selectionForeground", menuSelectionForeground,
//            "ComboBox.popupBorder", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.leopard.QuaquaLeopardComboBoxPopupBorder"),
//            "ComboBox.maximumRowCount",10,
            //
            "FileChooser.autovalidate", Boolean.TRUE,
            "FileChooser.enforceQuaquaTreeUI", Boolean.TRUE,
            //
            "FileChooser.previewLabelForeground", new ColorUIResource(0x6d6d6d),
            "FileChooser.previewValueForeground", new ColorUIResource(0x000000),
            "FileChooser.previewLabelInsets", new InsetsUIResource(1, 0, 0, 4),
            "FileChooser.previewLabelDelimiter", "",
            "FileChooser.cellTipOrigin", new Point(18, 1),
            "FileChooser.splitPaneDividerSize", 1,
            "FileChooser.splitPaneBackground", new ColorUIResource(0xa5a5a5),
            "FileChooser.browserCellFocusBorder", browserCellBorder,
            "FileChooser.browserCellFocusBorderGrayed", browserCellBorder,
            "FileChooser.browserCellBorder", browserCellBorder,
            "FileChooser.browserUseUnselectedExpandIconForLabeledFile", Boolean.FALSE,
            "FileChooser.browserCellColorLabelInsets", new InsetsUIResource(0, 1, -1, 1),
            "FileChooser.browserCellSelectedColorLabelInsets", new InsetsUIResource(1, 0, 0, 0),
            "FileChooser.browserCellTextIconGap", 6,
            "FileChooser.browserCellTextArrowIconGap", 5,
            "FileChooser.browserUseUnselectedExpandIconForLabeledFile", Boolean.TRUE,
            "FileChooser.sheetErrorFont", new FontUIResource("Lucida Grande", Font.PLAIN, 10),
            "FileChooser.sideBarIcon.Applications", makeNativeSidebarIcon(sideBarIconsPrefix + "ApplicationsFolder.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.Desktop", makeNativeSidebarIcon(sideBarIconsPrefix + "DesktopFolder.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.Documents", makeNativeSidebarIcon(sideBarIconsPrefix + "DocumentsFolder.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.Downloads", makeNativeSidebarIcon(sideBarIconsPrefix + "DownloadsFolder.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.Dropbox", makeNativeSidebarIcon(sideBarIconsPrefix + "DropBoxFolder.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.Home", makeNativeSidebarIcon(sideBarIconsPrefix + "HomeFolder.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.Library", makeNativeSidebarIcon(sideBarIconsPrefix + "GenericFolder.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.Movies", makeNativeSidebarIcon(sideBarIconsPrefix + "MoviesFolder.icns", 18, sideBarIconColor, sideBarIconSelectionColor), // Note: no "s" in "Movie"
            "FileChooser.sideBarIcon.Music", makeNativeSidebarIcon(sideBarIconsPrefix + "MusicFolder.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.Network", makeNativeSidebarIcon(sideBarIconsPrefix + "Network.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.Pictures", makeNativeSidebarIcon(sideBarIconsPrefix + "PicturesFolder.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.Public", makeNativeSidebarIcon(sideBarIconsPrefix + "DropBoxFolder.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.Shared", makeNativeSidebarIcon(sideBarIconsPrefix + "GenericFolder.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.Sites", makeNativeSidebarIcon(sideBarIconsPrefix + "GenericFolder.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.Utilities", makeNativeSidebarIcon(sideBarIconsPrefix + "UtilitiesFolder.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.GenericFolder", makeNativeSidebarIcon(sideBarIconsPrefix + "GenericFolder.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.GenericFile", makeNativeSidebarIcon(sideBarIconsPrefix + "GenericFile.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.GenericVolume", makeNativeSidebarIcon(sideBarIconsPrefix + "InternalDisk.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.SmartFolder", makeNativeSidebarIcon(sideBarIconsPrefix + "SmartFolder.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.TimeMachineVolume", makeNativeSidebarIcon(sideBarIconsPrefix + "TimeMachine.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            //
            "FileChooser.sideBarIcon.Imac", makeNativeSidebarIcon(sideBarIconsPrefix + "iMac.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.MacPro", makeNativeSidebarIcon(sideBarIconsPrefix + "MacPro.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.MacMini", makeNativeSidebarIcon(sideBarIconsPrefix + "MacMini.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            "FileChooser.sideBarIcon.Laptop", makeNativeSidebarIcon(sideBarIconsPrefix + "Laptop.icns", 18, sideBarIconColor, sideBarIconSelectionColor),
            //
            "FileChooser.sideBarRowHeight", 24,
            //
            "FileView.computerIcon", makeIcon(getClass(), snowLeopardDir + "FileView.computerIcon.png"),
            "FileView.fileIcon", makeNativeIcon(iconPrefix + "GenericDocumentIcon.icns", 16),
            "FileView.directoryIcon", makeNativeIcon(iconPrefix + "GenericFolderIcon.icns", 16),
            "FileView.hardDriveIcon", makeIcon(getClass(), snowLeopardDir + "FileView.hardDriveIcon.png"),
            "FileView.floppyDriveIcon", makeIcon(getClass(), snowLeopardDir + "FileView.floppyDriveIcon.png"),
            "FileView.aliasBadgeIcon", makeNativeIcon(iconPrefix + "AliasBadgeIcon.icns", 16),
            "FileView.networkIcon", makeNativeIcon(iconPrefix + "GenericNetworkIcon.icns", 16),
            //
            "FileView.macbookIcon", makeNativeIcon(iconPrefix + "com.apple.macbook-white.icns", 16),
            "FileView.macbookAirIcon", makeNativeIcon(iconPrefix + "com.apple.macbookair-13-unibody.icns", 16),
            "FileView.macbookProIcon", makeNativeIcon(iconPrefix + "com.apple.macbookpro-13-unibody.icns", 16),
            "FileView.macproIcon", makeNativeIcon(iconPrefix + "com.apple.macpro.icns", 16),
            "FileView.macminiIcon", makeNativeIcon(iconPrefix + "com.apple.macmini-unibody-no-optical.icns", 16),
            "FileView.imacIcon", makeNativeIcon(iconPrefix + "com.apple.imac-unibody-21.icns", 16),
            //
//            "FormattedTextField.border", textFieldBorder,
            //
//            "Frame.titlePaneBorders", makeImageBevelBorders(snowLeopardDir + "Frame.titlePaneBorders.png", new Insets(0, 0, 22, 0), 2, true),
//            "Frame.titlePaneBorders.small", makeImageBevelBorders(snowLeopardDir + "Frame.titlePaneBorders.small.png", new Insets(0, 0, 16, 0), 2, true),
//            "Frame.titlePaneBorders.mini", makeImageBevelBorders(snowLeopardDir + "Frame.titlePaneBorders.mini.png", new Insets(0, 0, 12, 0), 2, true),
//            "Frame.titlePaneBorders.vertical", makeImageBevelBorders(snowLeopardDir + "Frame.titlePaneBorders.vertical.png", new Insets(0, 0, 0, 22), 2, false),
//            "Frame.titlePaneBorders.vertical.small", makeImageBevelBorders(snowLeopardDir + "Frame.titlePaneBorders.vertical.small.png", new Insets(0, 0, 0, 16), 2, false),
//            "Frame.titlePaneBorders.vertical.mini", makeImageBevelBorders(snowLeopardDir + "Frame.titlePaneBorders.vertical.mini.png", new Insets(0, 0, 0, 12), 2, false),
//            "Frame.titlePaneEmbossForeground", new AlphaColorUIResource(0x7effffff),
            //
//            "InternalFrame.titlePaneBorders", makeImageBevelBorders(snowLeopardDir + "Frame.titlePaneBorders.png", new Insets(0, 0, 22, 0), 2, true),
//            "InternalFrame.titlePaneBorders.small", makeImageBevelBorders(snowLeopardDir + "Frame.titlePaneBorders.small.png", new Insets(0, 0, 16, 0), 2, true),
//            "InternalFrame.titlePaneBorders.mini", makeImageBevelBorders(snowLeopardDir + "Frame.titlePaneBorders.mini.png", new Insets(0, 0, 10, 0), 2, true),
//            "InternalFrame.closeIcon", makeFrameButtonStateIcon(snowLeopardDir + "Frame.closeIcons.png", 12),
//            "InternalFrame.maximizeIcon", makeFrameButtonStateIcon(snowLeopardDir + "Frame.maximizeIcons.png", 12),
//            "InternalFrame.iconifyIcon", makeFrameButtonStateIcon(snowLeopardDir + "Frame.iconifyIcons.png", 12),
//            "InternalFrame.closeIcon.small", makeFrameButtonStateIcon(snowLeopardDir + "Frame.closeIcons.small.png", 12),
//            "InternalFrame.maximizeIcon.small", makeFrameButtonStateIcon(snowLeopardDir + "Frame.maximizeIcons.small.png", 12),
//            "InternalFrame.iconifyIcon.small", makeFrameButtonStateIcon(snowLeopardDir + "Frame.iconifyIcons.small.png", 12),
//            "InternalFrame.resizeIcon", makeIcon(getClass(), leopardDir + "Frame.resize.png"),
            //
//            "Label.embossForeground", new AlphaColorUIResource(0x7effffff),
//            "Label.shadowForeground", new AlphaColorUIResource(0x7e000000),
            //
//            "List.alternateBackground.0", listAlternateBackground,
//            "List.cellNoFocusBorder", new BorderUIResource.EmptyBorderUIResource(1,3,1,3),
//            "List.focusSelectedCellHighlightBorder", new BorderUIResource.EmptyBorderUIResource(1,3,1,3),
//            "List.focusCellHighlightBorder", new BorderUIResource.EmptyBorderUIResource(1,3,1,3),
            //
//            "Menu.background", menuBackground,
//            "MenuItem.background", menuBackground,
//            "CheckBoxMenuItem.background", menuBackground,
//            "RadioButtonMenuItem.background", menuBackground,
            //
//            "OptionPane.errorIconResource", leopardDir + "OptionPane.errorIcon.png",
//            "OptionPane.warningIconResource", leopardDir + "OptionPane.warningIcon.png",
            //
//            "PopupMenu.background", menuBackground,
            //
//            "Panel.background", panelBackground,
            //
//            "PasswordField.border", textFieldBorder,
            //
//            "PopupMenu.border", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.leopard.QuaquaLeopardMenuBorder"),
            //
//            "RootPane.background", panelBackground,
            //
//            "ScrollBar.placeButtonsTogether",new UIDefaults.ProxyLazyValue(
//                "ch.randelshofer.quaqua.osx.OSXPreferences","isStringEqualTo",
//                new Object[]{OSXPreferences.GLOBAL_PREFERENCES, "AppleScrollBarVariant", "DoubleMax","DoubleMax"}),
//            "ScrollBar.supportsAbsolutePositioning",new UIDefaults.ProxyLazyValue(
//                "ch.randelshofer.quaqua.osx.OSXPreferences","isStringEqualTo",
//                new Object[]{OSXPreferences.GLOBAL_PREFERENCES, "AppleScrollerPagingBehavior", "false","true"}),
//            "ScrollBar.minimumThumbSize", new DimensionUIResource(18, 18),
//            "ScrollBar.minimumThumbSize.small", new DimensionUIResource(14, 14),
//            "ScrollBar.maximumThumbSize", new DimensionUIResource(Integer.MAX_VALUE, Integer.MAX_VALUE),
//            "ScrollBar.thumb.hMiddle", null,
//            "ScrollBar.thumb.hFirst", null,
//            "ScrollBar.thumb.hLast", null,
//            "ScrollBar.track.h", scrollBarTrack,
//            "ScrollBar.thumb.h", scrollBarThumb,
//            "ScrollBar.thumb.h.small", scrollBarThumb,
//            "ScrollBar.thumb.hInactive", scrollBarThumb,
//            "ScrollBar.buttons.hSep", null,
//            "ScrollBar.buttons.hTog", null,
//            "ScrollBar.thumb.vMiddle", null,
//            "ScrollBar.thumb.vFirst", null,
//            "ScrollBar.thumb.vLast", null,
//            "ScrollBar.track.v", scrollBarTrack,
//            "ScrollBar.thumb.v", scrollBarThumb,
//            "ScrollBar.thumb.v.small", scrollBarThumb,
//            "ScrollBar.thumb.vInactive", scrollBarThumb,
//            "ScrollBar.buttons.vSep", null,
//            "ScrollBar.buttons.vTog", null,
//            "ScrollBar.thumb.hMiddle.small", null,
//            "ScrollBar.thumb.hFirst.small", null,
//            "ScrollBar.thumb.hLast.small", null,
//            "ScrollBar.track.h.small", scrollBarTrack,
//            "ScrollBar.thumb.hInactive.small", scrollBarThumb,
//            "ScrollBar.buttons.hSep.small", null,
//            "ScrollBar.buttons.hTog.small", null,
//            "ScrollBar.thumb.vMiddle.small", null,
//            "ScrollBar.thumb.vLast.small", null,
//            "ScrollBar.track.v.small", scrollBarTrack,
//            "ScrollBar.thumb.vInactive.small", scrollBarThumb,
//            "ScrollBar.buttons.vSep.small", null,
//            "ScrollBar.buttons.vTog.small", null,
//            "ScrollBar.buttonHeight", 0,
//            "ScrollBar.buttonHeight.small", 0,
//            "ScrollBar.trackInsets.tog", new Insets(0,0,0,0),
//            "ScrollBar.trackInsets.tog.small", new Insets(0,0,0,0),
//            "ScrollBar.trackInsets.tog.mini", new Insets(0,0,0,0),
//            "ScrollBar.preferredSize", new Dimension(15,15),
//            "ScrollBar.preferredSize.small", new Dimension(13,13),
//            "ScrollBar.preferredSize.mini", new Dimension(9,9),
//            "ScrollBar.focusable", Boolean.FALSE,
//            "ScrollBar.thumbInsets", new Insets(2,4,2,3),
//            "ScrollBar.thumbInsets.small", new Insets(2,3,2,3),
//            "ScrollBar.thumbInsets.mini", new Insets(2,3,2,2),
//            //
//            "ScrollPane.border", scrollPaneBorder,
            //
//            "Separator.highlight", new ColorUIResource(0xe3e3e3),
//            "Separator.foreground", new ColorUIResource(0xd4d4d4),
//            "Separator.shadow", new AlphaColorUIResource(0x0),
//            "Separator.border", new VisualMarginBorder(),
            //
//            "TabbedPane.disabledForeground", disabledForeground,
//            "TabbedPane.tabInsets", new InsetsUIResource(1, 10, 4, 9),
//            "TabbedPane.selectedTabPadInsets", new InsetsUIResource(2, 2, 2, 1),
//            "TabbedPane.tabAreaInsets", new InsetsUIResource(4, 16, 0, 16),
//            "TabbedPane.contentBorderInsets", new InsetsUIResource(5, 6, 6, 6),
//            //"TabbedPane.background", (isBrushedMetal) ? table.get("TabbedPane.background") : makeTextureColor(0xf4f4f4, pantherDir+"Panel.texture.png"),
//            "TabbedPane.tabLayoutPolicy", (isJaguarTabbedPane()) ? JTabbedPane.WRAP_TAB_LAYOUT : JTabbedPane.SCROLL_TAB_LAYOUT,
//            "TabbedPane.wrap.disabledForeground", disabledForeground,
//            "TabbedPane.wrap.tabInsets", new InsetsUIResource(1, 10, 4, 9),
//            "TabbedPane.wrap.selectedTabPadInsets", new InsetsUIResource(2, 2, 2, 1),
//            "TabbedPane.wrap.tabAreaInsets", new InsetsUIResource(4, 16, 0, 16),
//            "TabbedPane.wrap.contentBorderInsets", new InsetsUIResource(2, 3, 3, 3),
//            //"TabbedPane.wrap.background", (isBrushedMetal) ? table.get("TabbedPane.background") : makeTextureColor(0xf4f4f4, pantherDir+"Panel.texture.png"),
//            "TabbedPane.scroll.selectedTabPadInsets", new InsetsUIResource(0, 0, 0, 0),
//            "TabbedPane.scroll.tabRunOverlay", 0,
//            "TabbedPane.scroll.tabInsets", new InsetsUIResource(1, 7, 2, 7),
//            "TabbedPane.scroll.smallTabInsets", new InsetsUIResource(1, 5, 2, 5),
//            "TabbedPane.scroll.outerTabInsets", new InsetsUIResource(1, 11, 2, 11),
//            "TabbedPane.scroll.smallOuterTabInsets", new InsetsUIResource(1, 9, 2, 9),
//            "TabbedPane.scroll.contentBorderInsets", new InsetsUIResource(2, 2, 2, 2),
//            "TabbedPane.scroll.tabAreaInsets", new InsetsUIResource(-2, 16, 1, 16),
//            "TabbedPane.scroll.contentBorder", makeNativeImageBevelBorder(
//            OSXAquaPainter.Widget.frameGroupBox,new Insets(0,0,0,0), new Insets(7, 7, 7, 7),new Insets(7, 7, 7, 7), true),
//            "TabbedPane.scroll.emptyContentBorder", makeNativeImageBevelBorder(
//            OSXAquaPainter.Widget.frameGroupBox,new Insets(0,0,0,0), new Insets(7, 7, 7, 7),new Insets(7, 7, 7, 7), true),
//            "TabbedPane.scroll.tabBorders", makeImageBevelBorders(commonDir + "Toggle.borders.png",
//            new Insets(8, 10, 15, 10), 10, true),
//            "TabbedPane.scroll.tabFocusRing", makeImageBevelBorder(commonDir + "Toggle.focusRing.png",
//            new Insets(8, 10, 15, 10), true),
//            "TabbedPane.scroll.eastTabBorders", makeImageBevelBorders(commonDir + "Toggle.east.borders.png",
//            new Insets(8, 1, 15, 10), 10, true),
//            "TabbedPane.scroll.eastTabFocusRing", makeImageBevelBorder(commonDir + "Toggle.east.focusRing.png",
//            new Insets(8, 4, 15, 10), true),
//            "TabbedPane.scroll.centerTabBorders", makeImageBevelBorders(commonDir + "Toggle.center.borders.png",
//            new Insets(8, 0, 15, 1), 10, true),
//            "TabbedPane.scroll.centerTabFocusRing", makeImageBevelBorder(commonDir + "Toggle.center.focusRing.png",
//            new Insets(8, 4, 15, 4), false),
//            "TabbedPane.scroll.westTabBorders", makeImageBevelBorders(commonDir + "Toggle.west.borders.png",
//            new Insets(8, 10, 15, 1), 10, true),
//            "TabbedPane.scroll.westTabFocusRing", makeImageBevelBorder(commonDir + "Toggle.west.focusRing.png",
//            new Insets(8, 10, 15, 4), true),
            //
//            "Table.ascendingSortIcon", makeIcon(getClass(), snowLeopardDir + "Table.ascendingSortIcon.png"),
//            "Table.descendingSortIcon", makeIcon(getClass(), snowLeopardDir + "Table.descendingSortIcon.png"),
//            "Table.scrollPaneBorder", scrollPaneBorder,
            //
//            "TextField.border", textFieldBorder,
//            "Table.alternateBackground.0", listAlternateBackground,
//            "TextField.borderInsets", new InsetsUIResource(3,6,3,6),
//            "TextField.smallBorderInsets", new InsetsUIResource(3, 5, 2, 5),
//            "TextField.miniBorderInsets", new InsetsUIResource(3, 5, 2, 5),
//            "TextField.searchBorderInsets", new InsetsUIResource(6,12,5,12),
            //
            //"TableHeader.cellBorder", new UIDefaults.ProxyLazyValue(
            //"ch.randelshofer.quaqua.QuaquaTableHeaderBorder$UIResource",
            //new Object[]{snowLeopardDir + "TableHeader.borders.png", new Insets(6, 1, 9, 1)}),
            //
//            "TitledBorder.border", new GroupBox(),
//            "TitledBorder.titleColor", new ColorUIResource(0x303030),
            //
//            "ToolBar.background", panelBackground,
//            "ToolBar.borderBright", new AlphaColorUIResource(0x999999),
//            "ToolBar.borderDark", new ColorUIResource(0x8c8c8c),
//            "ToolBar.borderDivider", new ColorUIResource(0x9f9f9f),
//            "ToolBar.borderDividerInactive", new ColorUIResource(0x9f9f9f),
//            "ToolBar.title.borderDivider", new ColorUIResource(0x515151),
//            "ToolBar.title.borderDividerInactive", new ColorUIResource(0x999999),
//            "ToolBar.title.background", toolBarBackground,
//            "ToolBar.bottom.borderDivider", new ColorUIResource(0x515151),
//            "ToolBar.bottom.borderDividerInactive", new ColorUIResource(0x999999),
//            "ToolBar.bottom.gradient", new Color[]{new Color(0xcbcbcb), new Color(0xa7a7a7)},
//            "ToolBar.bottom.gradientInactive", new Color[]{new Color(0xeaeaea), new Color(0xd8d8d8)},
//            "ToolBar.gradient.borderDivider", new ColorUIResource(0xd4d4d4),
//            "ToolBar.gradient.borderDividerInactive", new ColorUIResource(0xd4d4d4),
//            "ToolBar.textured.dragMovesWindow", Boolean.TRUE,
            //
//            "ToolBarSeparator.foreground", new ColorUIResource(0x808080),
            //
            //"Tree.collapsedIcon", makeIcon(getClass(), leopardDir + "Tree.collapsedIcon.png"),
            //"Tree.expandedIcon", makeIcon(getClass(), leopardDir + "Tree.expandedIcon.png"),
            //"Tree.leafIcon", makeIcon(getClass(), leopardDir + "Tree.leafIcon.png"),
            "Tree.alternateBackground.0", listAlternateBackground,
            //"Tree.openIcon", makeIcon(getClass(), leopardDir + "Tree.openIcon.png"),
            //"Tree.closedIcon", makeIcon(getClass(), leopardDir + "Tree.closedIcon.png"),
            "Tree.sideBar.background", new InactivatableColorUIResource(//
            new GradientColor(0xe8ecf1, 0xe8ecf1, 0xdadfe6),//
            new GradientColor(0xf7f7f7, 0xf7f7f7, 0xeeeeee)),
            "Tree.sideBar.selectionBorder", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.lion.QuaquaLionSideBarSelectionBorder"),
            "Tree.leftChildIndent", 8, // 7
            "Tree.rightChildIndent", 12, // 13
            "Tree.icons", makeIcons(lionDir + "Tree.icons.png", 15, true),
            "Tree.sideBar.icons", makeIcons(lionDir + "Tree.sideBar.icons.png", 15, true),
            "Tree.sideBarCategory.foreground", new InactivatableColorUIResource(0x707e8b, 0x868b92),
            "Tree.sideBarCategory.selectionForeground", new InactivatableColorUIResource(0xffffff, 0xffffff),
            "Tree.sideBarCategory.selectionFont", new FontUIResource("Lucida Grande", Font.BOLD, 11),
            "Tree.sideBar.foreground", new InactivatableColorUIResource(0x000000, 0x000000),
            "Tree.sideBar.selectionForeground", new InactivatableColorUIResource(0xffffff, 0xffffff),
            "Tree.sideBarCategory.selectionForeground", new InactivatableColorUIResource(0xffffff, 0xffffff),//
            "Tree.rendererMargins", new InsetsUIResource(0,0,0,0),
        };

        putDefaults(table, uiDefaults);

//        // FIXME Implement a screen menu bar by myself. We lose too many features here.
//        if (isUseScreenMenuBar()) {
//            uiDefaults = new Object[]{
//                        "CheckBoxMenuItem.checkIcon", makeButtonStateIcon(snowLeopardDir + "CheckBoxMenuItem.icons.png", 6, new Rectangle(1, 0, 12, 12)),
//                        "CheckBoxMenuItem.border", new BorderUIResource.EmptyBorderUIResource(0, 5, 2, 0),
//                        "CheckBoxMenuItem.margin", new InsetsUIResource(0, 8, 0, 8),
//                        "Menu.checkIcon", makeIcon(getClass(), commonDir + "MenuItem.checkIcon.png", new Point(1, 0)),
//                        "Menu.arrowIcon", makeButtonStateIcon(commonDir + "MenuItem.arrowIcons.png", 2, new Rectangle(-6, 1, 6, 12)),
//                        "Menu.border", new BorderUIResource.EmptyBorderUIResource(0, 5, 2, 4),
//                        "Menu.margin", new InsetsUIResource(0, 8, 0, 8),
//                        "Menu.menuPopupOffsetX", 0,
//                        "Menu.menuPopupOffsetY", 1,
//                        "Menu.submenuPopupOffsetX", 0,
//                        "Menu.submenuPopupOffsetY", -4,
//                        "MenuItem.checkIcon", makeIcon(getClass(), commonDir + "MenuItem.checkIcon.png", new Point(1, 0)),
//                        "MenuItem.border", new BorderUIResource.EmptyBorderUIResource(0, 5, 2, 0),
//                        "RadioButtonMenuItem.checkIcon", makeButtonStateIcon(snowLeopardDir + "RadioButtonMenuItem.icons.png", 6, new Rectangle(0, 0, 12, 12)),
//                        "RadioButtonMenuItem.border", new BorderUIResource.EmptyBorderUIResource(0, 5, 2, 0),
//                        "RadioButtonMenuItem.margin", new InsetsUIResource(0, 8, 0, 8), //
//                    };
//        } else {
//            Border menuBorder = new BorderUIResource.EmptyBorderUIResource(1, 1, 1, 1);
//            GradientColor.UIResource menuSelectionBackground = new GradientColor.UIResource(0x3875d7, 0x5170f6, 0x1a43f3);
//            uiDefaults = new Object[]{
//                        "CheckBoxMenuItem.checkIcon", makeButtonStateIcon(commonDir + "CheckBoxMenuItem.icons.png", 6, new Point(0, 1)),
//                        "CheckBoxMenuItem.border", menuBorder,
//                        "CheckBoxMenuItem.selectionBackground", menuSelectionBackground,
//                        "Menu.checkIcon", makeIcon(getClass(), commonDir + "MenuItem.checkIcon.png"),
//                        "Menu.arrowIcon", makeButtonStateIcon(commonDir + "MenuItem.arrowIcons.png", 2, new Point(0, 1)),
//                        "Menu.margin", new InsetsUIResource(0, 5, 0, 8),
//                        "Menu.menuPopupOffsetX", 0,
//                        "Menu.menuPopupOffsetY", 0,
//                        "Menu.submenuPopupOffsetX", 0,
//                        "Menu.submenuPopupOffsetY", -4,
//                        "Menu.useMenuBarBackgroundForTopLevel", Boolean.TRUE,
//                        "Menu.border", menuBorder,
//                        "Menu.selectionBackground", menuSelectionBackground,
//                        //"MenuBar.background", new TextureColorUIResource(0xf4f4f4, getClass().getResource(pantherDir+"MenuBar.texture.png")),
//                        //"MenuBar.border", new BorderUIResource.MatteBorderUIResource(0,0,1,0,new Color(128,128,128)),
//                        "MenuBar.border", makeImageBevelBackgroundBorder(snowLeopardDir + "MenuBar.border.png", new Insets(18, 0, 2, 0), new Insets(0, 0, 0, 0), true),
//                        "MenuBar.selectedBorder", makeImageBevelBackgroundBorder(snowLeopardDir + "MenuBar.selectedBorder.png", new Insets(18, 0, 2, 0), new Insets(0, 0, 0, 0), true),
//                        "MenuBar.margin", new InsetsUIResource(1, 8, 2, 8),
//                        "MenuBar.shadow", null,
//                        "MenuItem.acceleratorSelectionForeground", menuSelectionForeground,
//                        "MenuItem.checkIcon", makeIcon(getClass(), commonDir + "MenuItem.checkIcon.png"),
//                        "MenuItem.border", menuBorder,
//                        "MenuItem.selectionBackground", menuSelectionBackground,
//                        "RadioButtonMenuItem.checkIcon", makeButtonStateIcon(commonDir + "RadioButtonMenuItem.icons.png", 6),
//                        "RadioButtonMenuItem.border", menuBorder,
//                        "RadioButtonMenuItem.selectionBackground", menuSelectionBackground,
//                        /* */
//            };
//        }
//        putDefaults(table, uiDefaults);

        int design = QuaquaManager.getDesign();

        if (design >= QuaquaManager.MAVERICKS) {
            Object[] defaults = Quaqua16MavericksLookAndFeel.getConfiguredDesignDefaults();
            putDefaults(table, defaults);
        }

        if (design >= QuaquaManager.YOSEMITE) {
            Object[] defaults = Quaqua16YosemiteLookAndFeel.getConfiguredDesignDefaults();
            putDefaults(table, defaults);
        }
    }

    protected static Object makeIcon(String location) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory", "getIcon",
                new Object[]{location});
    }

    protected static Object makeIcons(String location, int states, boolean horizontal) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory", "createIcons",
                new Object[]{location, states, horizontal});
    }

    protected static Object makeButtonStateIcon(String location, int states) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory", "createButtonStateIcon",
                new Object[]{location, states});
    }

    protected Object makeImage(String location) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory", "createImage",
                new Object[]{location});
    }

    protected Object makeBufferedImage(String location) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory", "createBufferedImage",
                new Object[]{location});
    }

    protected static Object makeSliderThumbIcon(String location) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory", "createSliderThumbIcon",
                new Object[]{location});
    }

//    @Override
//    public boolean getSupportsWindowDecorations() {
//        return true;
//    }

//    @Override
//    public LayoutStyle getLayoutStyle() {
//        if (layoutStyle == null) {
//            layoutStyle = new QuaquaLayoutStyle();
//        }
//        return layoutStyle;
//    }

}
