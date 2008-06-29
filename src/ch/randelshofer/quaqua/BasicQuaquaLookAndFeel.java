/*
 * @(#)BasicQuaquaLookAndFeel.java  1.27  2008-03-21
 *
 * Copyright (c) 2005-2008 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.util.*;
import ch.randelshofer.quaqua.util.AlphaColorUIResource;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.security.*;

/**
 * The BasicQuaquaLookAndFeel contains the look and feel properties that are
 * commonly uses by all the specific QuaquaLookAndFeel incarnations.
 *
 * @author  Werner Randelshofer
 * @version 1.27 2008-03-21 Added table input map. 
 * <br>1.26 2008-02-05 Explicitly defined TabbedPane.textIconGap = 4. 
 * <br>1.23 2007-12-23 Set "ClassLoader" property to class loader of
 * this LookAndFeel class. 
 * <br>1.23 2007-12-01 Streamlined property names for Slider.
 * <br>1.22 2007-11-25 Support for Darwin added.
 * <br>1.21 2007-10-30 Support for Mac OS X 10.5 (Leopard) added.
 * <br>1.20.1 2007-07-25 Use "BACK_SPACE" instead of "typed \010" to
 * specify the input map for the backspace key.
 * <br>1.20 2007-07-23 Use Quaqua16LayoutStyle and Quaqua16Baseline,
 * when running under Java 1.6.
 * <br>1.19.1 2007-02-17 Don't load ResourceBundle using Reflection because
 * we are not allowed to use Reflection when running as an unsigned Java
 * Webstart application.
 * <br>1.19 2007-02-09 Fixed combo box background and opaqueness.
 * <br>1.18 2007-01-17 Reimplemented fix for issue #6 in a different way.
 * <br>1.17 2007-01-07 Property "ComboBox.maximumRowCount" added.
 * Issue #6: colors for inactive selections in lists and trees added.
 * Support for "requestFocusEnabled" added.
 * <br>1.16 2006-12-24 Support for Aqua Graphite Appearance added.
 * <br>1.15 2006-09-04 Fixed input map problems of JTabbedPane.
 * <br>1.14 2006-05-07 Table.focusCellHighlightBorder set back to the
 * same color as for list selection highlight border. Shift-Enter and
 * Alt-Enter inserts a line break into a multiline text field.
 * FileChooser.previewLabelDelimiter added.
 * <br>1.13 2006-04-23 Property "popupHandler" for all the various text
 * component UI's added.
 * <br>1.12.2 2006-04-15 Table.focusCellHighlightBorder set to black.
 * <br>1.12.1 2006-04-08 TableHeaderBorder must be an UIResource. Set
 * values for Table.focusCellBackground and Table.focusCellForeground.
 * <br>1.12 2006-03-29 InputMap for combo box editor must contain
 * a binding for the ENTER key.
 * <br>1.11 2006-02-06 TabbedPane.opaque property added. Support for
 * the following system properties added: Quaqua.FileChooser.speed,
 * Quaqua.FileChooser.orderByType.
 * <br>1.10 2005-12-19 Fixed separator colors. Fixed tree icons.
 * <br>2.0 2005-12-04 Reduced start up latency by using ProxyLazyValue
 * instead of referring classes directly.
 * <br>1.9 2005-11-27 FileView.directoryIcon and FileView.fileIcon added.
 * <br>1.8 2005-11-07 Resource bundle "Labels" added.
 * <br>1.7 2005-10-17 Further reduce startup latency.
 * <br>1.6 2005-09-25 Make use of UIDefaults.LazyValue to reduce startup
 * latency. ColorChooser.crayonsFont property added. Read Mac OS X Preferences
 * to derive selection colors and ScrollBar properties. Properties for
 * OptionPane extended.
 * <br>1.5 2005-09-04 Support for GroupLayout added.
 * <br>1.4 2005-08-30 Added "ColorChooser.crayonsImage".
 * <br>1.3 2005-08-25 Explicitly set "Viewport.background" to white,
 * Explicitly setting more colors.
 * <br>1.2.2 2005-07-06 Readded input map entry for ENTER key on text fields.
 * <br>1.2.1 2005-06-22 Beep when cutting/copying from password field. Micro-alignment
 * for text components improved. Do not set Button.margin and ToolBar.separatorSize to
 * to null, even if we don't use these values, because they make the Java 1.3 VM crash.
 * <br>1.2 2005-05-28 Added support for new system properties.
 * <br>1.1 2005-05-21 Fixed tool tip font. Moved common ui properties
 * from subclasses into this class.
 * <br>1.0  15 April 2005  Created.
 */
public class BasicQuaquaLookAndFeel extends LookAndFeelProxy {
    protected final static String commonDir = "/ch/randelshofer/quaqua/images/";
    protected final static String jaguarDir = "/ch/randelshofer/quaqua/jaguar/images/";
    protected final static String pantherDir = "/ch/randelshofer/quaqua/panther/images/";
    protected final static String tigerDir = "/ch/randelshofer/quaqua/tiger/images/";
    protected final static String leopardDir = "/ch/randelshofer/quaqua/leopard/images/";
    
    /** Creates a new instance. */
    public BasicQuaquaLookAndFeel(String targetClassName) {
        try {
            setTarget((LookAndFeel) Class.forName(targetClassName).newInstance());
        } catch (Exception e) {
            throw new InternalError(
                    "Unable to instanciate target Look and Feel \""
                    +targetClassName
                    +"\". "+e.getMessage()
                    );
        }
    }
    
    /**
     * Return a string that identifies this look and feel.  This string
     * will be used by applications/services that want to recognize
     * well known look and feel implementations.  Presently
     * the well known names are "Motif", "Windows", "Mac", "Metal".  Note
     * that a LookAndFeel derived from a well known superclass
     * that doesn't make any fundamental changes to the look or feel
     * shouldn't override this method.
     */
    public String getID() {
        return "Aqua";
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
    public UIDefaults getDefaults() {
        UIDefaults table = target.getDefaults();
        
        initClassDefaults(table);
        initSystemColorDefaults(table);
        initComponentDefaults(table);
        
        // Only install our own KeyboardFocusManager, if it is wanted
        if (QuaquaManager.getProperty("Quaqua.TextComponent.autoSelect","true").
                equals("true")) {
            installKeyboardFocusManager();
        }
        
        return table;
    }
    
    protected final void initComponentDefaults(UIDefaults table) {
        initResourceBundle(table);
        initColorDefaults(table);
        initInputMapDefaults(table);
        initFontDefaults(table);
        initGeneralDefaults(table);
        initDesignDefaults(table);
    }
    
    protected void initResourceBundle(UIDefaults table) {
        ResourceBundle bundle = ResourceBundle.getBundle(
                "ch.randelshofer.quaqua.Labels",
                Locale.getDefault(),
                getClass().getClassLoader()
                );
        for (Enumeration i = bundle.getKeys(); i.hasMoreElements(); ) {
            String key = (String) i.nextElement();
            table.put(key, bundle.getObject(key));
        }
    }
    
    
    /**
     * List of well known highlight colors.
     *
     * The values in the second dimension of this array are used as follows:
     * 0 = Highlight color for text
     * 1 = Highlight color for lists, tables and trees
     *
     * If an other color is chosen, we compute the color values
     * using the following algorithm.
     * color, color - 10 % brightness, color - 20% brightness
     */
    private final static int[][] selectionColors = {
        // Graphite
        { 0xffc7d0db, 0x778da8},
        // Silver
        { 0xffc6c6c6, 0x7f7f7f},
        // Blue
        { 0xffb5d5ff, 0x3875d7},
        // Gold
        { 0xfffbed73, 0xffc11f},
        // Red
        { 0xffffb18c, 0xf34648},
        // Orange
        { 0xffffd281, 0xff8a22},
        // Green
        { 0xffc3f991, 0x66c547},
        // Purple
        { 0xffe9b8ff, 0x8c4eb8},
    };
    
    /**
     * Load the SystemColors into the defaults table.  The keys
     * for SystemColor defaults are the same as the names of
     * the public fields in SystemColor.  If the table is being
     * created on a native Windows platform we use the SystemColor
     * values, otherwise we create color objects whose values match
     * the defaults Windows95 colors.
     */
    protected void initSystemColorDefaults(UIDefaults table) {
        ColorUIResource textSelectionBackground;
        ColorUIResource listSelectionBackground;
        ColorUIResource listSelectionForeground;
        ColorUIResource listSelectionBorderColor;
        ColorUIResource inactiveSelectionBackground = new ColorUIResource(0xd0d0d0);
        
        // Get text selection background from Mac OS X system preferences
        String colorValues = Preferences.getString("AppleHighlightColor");
        try {
            float[] rgb = new float[3];
            StringTokenizer tt = new StringTokenizer(colorValues);
            for (int i=0; i < 3; i++) {
                String value = tt.nextToken();
                rgb[i] = Float.valueOf(value).floatValue();
            }
            textSelectionBackground = new ColorUIResource(rgb[0],rgb[1],rgb[2]);
        } catch (Exception e) {
            textSelectionBackground = new ColorUIResource(0xffb5d5ff); // blue
        }
        
        // Derive list selection colors from text selection background
        listSelectionBorderColor = new ColorUIResource(0x808080);
        if (QuaquaManager.getProperty("Quaqua.selectionStyle", "auto").equals("bright")) {
            listSelectionForeground = new ColorUIResource(0x000000);
            listSelectionBackground = textSelectionBackground;
        } else {
            listSelectionForeground = new ColorUIResource(0xffffff);
            
            int textSelectionRGB = textSelectionBackground.getRGB() | 0xff000000;
            
            // For some well known text selection colors, we look up a table
            // to determine the color for list selection backgrounds.
            listSelectionBackground = null;
            for (int i=0; i < selectionColors.length; i++) {
                if (selectionColors[i][0] == textSelectionRGB) {
                    listSelectionBackground = new ColorUIResource(selectionColors[i][1]);
                    break;
                }
            }
            
            // If it is not a well known color, we use a 20 percent darker color
            // for the list selection background.
            if (listSelectionBackground == null) {
                float[] hsb = Color.RGBtoHSB(
                        textSelectionBackground.getRed(),
                        textSelectionBackground.getGreen(),
                        textSelectionBackground.getBlue(),
                        null
                        );
                listSelectionBackground = new ColorUIResource(Color.getHSBColor(hsb[0], hsb[1], hsb[2] * 0.8f));
            }
        }
        
        boolean isGraphite = Preferences.getString("AppleAquaColorVariant").equals("6");
        
        
        Object[] objects = {
            "desktop", new ColorUIResource(isGraphite ? 0x647185 : 0x3a69aa), /* Color of the desktop background */
            "activeCaption", table.get("InternalFrame.activeTitleBackground"), /* Color for captions (title bars) when they are active. */
            "activeCaptionText", new ColorUIResource(0x000000), /* Text color for text in captions (title bars). */
            "activeCaptionBorder", table.get("InternalFrame.borderColor"), /* Border color for caption (title bar) window borders. */
            "inactiveCaption", table.get("InternalFrame.inactiveTitleBackground"), /* Color for captions (title bars) when not active. */
            "inactiveCaptionText", new ColorUIResource(0x666666), /* Text color for text in inactive captions (title bars). */
            "inactiveCaptionBorder", table.get("InternalFrame.borderColor"), /* Border color for inactive caption (title bar) window borders. */
            "window", table.get("control"), /* Default color for the interior of windows */
            "windowBorder", table.get("control"), /* ??? */
            "windowText", new ColorUIResource(0x000000), /* ??? */
            "menu", table.get("MenuItem.background"), /* Background color for menus */
            "menuText", new ColorUIResource(0x000000), /* Text color for menus  */
            "text", new ColorUIResource(0xffffff), /* Text background color */
            "textText", new ColorUIResource(0x000000), /* Text foreground color */
            "textHighlight", textSelectionBackground, /* Text background color when selected */
            "textHighlightText", new ColorUIResource(0x000000), /* Text color when selected */
            "textInactiveText", new ColorUIResource(0x808080), /* Text color when disabled */
            "control", table.get("control"), /* Default color for controls (buttons, sliders, etc) */
            "controlText", new ColorUIResource(0x000000), /* Default color for text in controls */
            "controlHighlight", new ColorUIResource(0xC0C0C0), /* Specular highlight (opposite of the shadow) */
            "controlLtHighlight", new ColorUIResource(0xFFFFFF), /* Highlight color for controls */
            "controlShadow", new ColorUIResource(0x808080), /* Shadow color for controls */
            "controlDkShadow", new ColorUIResource(0x000000), /* Dark shadow color for controls */
            "scrollbar", table.get("control"), /* Scrollbar background (usually the "track") */
            "info", new ColorUIResource(0xffffc1), /* ??? */
            "infoText", new ColorUIResource(0x000000),  /* ??? */
            
            // Quaqua specific 'system' colors
            "list", new ColorUIResource(0xffffff), /* List background color */
            "listText", new ColorUIResource(0x000000), /* List foreground color */
            "listHighlight", listSelectionBackground, /* List background color when selected */
            "listHighlightText", listSelectionForeground, /* List color when selected */
            "listHighlightBorder", listSelectionBorderColor, /* List color when selected */
            "listInactiveHighlight", inactiveSelectionBackground, /* List color when selected */
            "listInactiveText", new ColorUIResource(0x808080), /* List color when disabled */
            
            "menuHighlightText", new ColorUIResource(0xffffff), /* Menu text color when selected */
            "menuHighlight", table.get("Menu.selectionBackground"), /* Menu background color when selected */
        };
        table.putDefaults(objects);
    }
    protected void initColorDefaults(UIDefaults table) {
        // Shared Colors
        Object controlForeground = table.get("controlText");
        Object controlBackground = table.get("control");
        
        Object textBackground = table.get("text");
        Object textForeground = table.get("textText");
        Object textSelectionBackground = table.get("textHighlight");
        Object translucentColor = new AlphaColorUIResource(0x00000000);
        
        Object disabledForeground = table.get("textInactiveText");
        ColorUIResource inactiveSelectionBackground = new ColorUIResource(208,208,208);
        Object inactiveSelectionForeground = controlForeground;
        
        Object menuBackground = table.get("menu");
        Object menuForeground = table.get("menuText");
        Object menuSelectionForeground = table.get("menuHighlightText");
        Object menuSelectionBackground = table.get("menuHighlight");
        Object menuDisabledBackground = menuBackground;
        Object menuDisabledForeground = disabledForeground;
        
        Object listBackground = table.get("list");
        Object listForeground = table.get("listText");
        
        Object listSelectionBackground = new InactivatableColorUIResource(
                ((Color) table.get("listHighlight")).getRGB(),
                inactiveSelectionBackground.getRGB()
                );
        Object listSelectionForeground = new InactivatableColorUIResource(
                ((Color) table.get("listHighlightText")).getRGB(),
                ((Color) inactiveSelectionForeground).getRGB()
                );
        //Object listSelectionBackground = table.get("listHighlight");
        //Object listSelectionForeground = table.get("listHighlightText");
        //Object listInactiveSelectionBackground = inactiveSelectionBackground;
        //Object listInactiveSelectionForeground = inactiveSelectionForeground;
        
        ColorUIResource listSelectionBorderColor = (ColorUIResource) table.get("listHighlightBorder");
        ColorUIResource listAlternateBackground = Preferences.get("AppleAquaColorVariant").equals("6") ?
            new ColorUIResource(0xf0f0f0) :
            new ColorUIResource(0xedf3fe);
        
        
        
        // Init
        Object[] objects = {
            "Browser.selectionBackground", listSelectionBackground,
            "Browser.selectionForeground", listSelectionForeground,
            "Browser.selectionBorderColor", listSelectionBorderColor,
            "Browser.inactiveSelectionBackground", inactiveSelectionBackground,
            "Browser.inactiveSelectionForeground", inactiveSelectionForeground,
            
            "Button.background", controlBackground,
            "Button.foreground", controlForeground,
            "Button.disabledForeground", disabledForeground,
            //"Button.shadow", ???,
            //"Button.darkShadow", ???,
            //"Button.light", ???,
            //"Button.highlight", ???,
            
            "CheckBox.background", controlBackground,
            "CheckBox.foreground", controlForeground,
            "CheckBox.disabledForeground", disabledForeground,
            //"CheckBox.shadow", ???,
            //"CheckBox.darkShadow", ???,
            //"CheckBox.light", ???,
            //"CheckBox.highlight", ???,
            
            "CheckBoxMenuItem.background", menuBackground,
            "CheckBoxMenuItem.foreground", menuForeground,
            "CheckBoxMenuItem.selectionForeground", menuSelectionForeground,
            "CheckBoxMenuItem.selectionBackground", menuSelectionBackground,
            "CheckBoxMenuItem.disabledForeground", disabledForeground,
            "CheckBoxMenuItem.acceleratorForeground", menuForeground,
            "CheckBoxMenuItem.acceleratorSelectionForeground", menuSelectionForeground,
            
            "ColorChooser.background", controlBackground,
            "ColorChooser.foreground", controlForeground,
            //"ColorChooser.swatchesDefaultRecentColor", ...,
            //"ColorChooser.swatchesRecentSwatchSize", ...,
            
            // Note: The following colors are used in color lists.
            //       It is important that these colors are neutral (black, white
            //       or a shade of gray with saturation 0).
            //       If they aren't neutral, human perception of the color
            //       is negatively affected.
            "ColorChooser.listSelectionBackground", new ColorUIResource(0xd4d4d4),
            "ColorChooser.listSelectionForeground", new ColorUIResource(0x000000),
            
            
            "ComboBox.background", controlBackground,
            "ComboBox.foreground", controlForeground,
            //"ComboBox.buttonBackground", ...,
            //"ComboBox.buttonDarkShadow", ...,
            //"ComboBox.buttonHighlight", ...,
            //"ComboBox.buttonShadow", ...,
            "ComboBox.disabledBackground", controlBackground,
            "ComboBox.disabledForeground", disabledForeground,
            "ComboBox.selectionBackground", menuSelectionBackground,
            "ComboBox.selectionForeground", menuSelectionForeground,
            
            "Dialog.background", controlBackground,
            "Dialog.foreground", controlForeground,
            
            "Desktop.background", table.get("desktop"),
            
            "EditorPane.background", textBackground,
            "EditorPane.caretForeground", textForeground,
            "EditorPane.foreground", textForeground,
            "EditorPane.inactiveBackground", textBackground,
            "EditorPane.inactiveForeground", disabledForeground,
            "EditorPane.selectionBackground", textSelectionBackground,
            "EditorPane.selectionForeground", textForeground,
            
            "FileChooser.previewLabelForeground", textForeground,
            "FileChooser.previewValueForeground", textForeground,
            
            "FormattedTextField.background", textBackground,
            "FormattedTextField.foreground", textForeground,
            "FormattedTextField.inactiveBackground", textBackground,
            "FormattedTextField.inactiveForeground", disabledForeground,
            "FormattedTextField.selectionBackground", textSelectionBackground,
            "FormattedTextField.selectionForeground", textForeground,
            
            "InternalFrame.titlePaneBackground.small", makeTextureColor(0xf4f4f4, commonDir+"Frame.titlePane.small.png"),
            "InternalFrame.vTitlePaneBackground.small", makeTextureColor(0xf4f4f4, commonDir+"Frame.vTitlePane.small.png"),
            "InternalFrame.titlePaneForeground.small", controlForeground,
            "InternalFrame.titlePaneShadow.small", new ColorUIResource(0x8e8e8e),
            "InternalFrame.closeIcon.small", makeFrameButtonStateIcon(commonDir+"Frame.closeIcons.small.png", 12),
            "InternalFrame.maximizeIcon.small", makeFrameButtonStateIcon(commonDir+"Frame.maximizeIcons.small.png", 12),
            "InternalFrame.iconifyIcon.small", makeFrameButtonStateIcon(commonDir+"Frame.iconifyIcons.small.png", 12),
            "InternalFrame.titlePaneBackground", makeTextureColor(0xf4f4f4, commonDir+"Frame.titlePane.png"),
            "InternalFrame.vTitlePaneBackground", makeTextureColor(0xf4f4f4, commonDir+"Frame.vTitlePane.png"),
            "InternalFrame.titlePaneForeground", controlForeground,
            "InternalFrame.titlePaneShadow", new ColorUIResource(0x8e8e8e),
            "InternalFrame.closeIcon", makeFrameButtonStateIcon(commonDir+"Frame.closeIcons.png", 12),
            "InternalFrame.maximizeIcon", makeFrameButtonStateIcon(commonDir+"Frame.maximizeIcons.png", 12),
            "InternalFrame.iconifyIcon", makeFrameButtonStateIcon(commonDir+"Frame.iconifyIcons.png", 12),
            "InternalFrame.titlePaneBackground.mini", makeTextureColor(0xf4f4f4, commonDir+"Frame.titlePane.mini.png"),
            "InternalFrame.vTitlePaneBackground.mini", makeTextureColor(0xf4f4f4, commonDir+"Frame.vTitlePane.mini.png"),
            "InternalFrame.titlePaneForeground.mini", controlForeground,
            "InternalFrame.titlePaneShadow.mini", new ColorUIResource(0x8e8e8e),
            "InternalFrame.closeIcon.mini", makeFrameButtonStateIcon(commonDir+"Frame.closeIcons.mini.png", 12),
            "InternalFrame.maximizeIcon,mini", makeFrameButtonStateIcon(commonDir+"Frame.maximizeIcons.mini.png", 12),
            "InternalFrame.iconifyIcon.mini", makeFrameButtonStateIcon(commonDir+"Frame.iconifyIcons.mini.png", 12),
            "InternalFrame.resizeIcon", makeIcon(getClass(), commonDir+"Frame.resize.png"),
            
            "Label.background", controlBackground,
            "Label.foreground", controlForeground,
            "Label.disabledForeground", disabledForeground,
            //"Label.disabledShadow", ???,
            
            "List.alternateBackground.0", listAlternateBackground,
            "List.alternateBackground.1", listBackground,
            "List.background", textBackground,
            "List.foreground", controlForeground,
            "List.selectionBackground", listSelectionBackground,
            "List.selectionForeground", listSelectionForeground,
            //"List.inactiveSelectionBackground", listInactiveSelectionBackground,
            //"List.inactiveSelectionForeground", listInactiveSelectionForeground,
            
            "Menu.background", menuBackground,
            "Menu.foreground", menuForeground,
            "Menu.acceleratorForeground", menuForeground,
            "Menu.acceleratorSelectionForeground", menuSelectionForeground,
            "Menu.selectionBackground", menuSelectionBackground,
            "Menu.selectionForeground", menuSelectionForeground,
            "Menu.disabledBackground", menuDisabledBackground,
            "Menu.disabledForeground", menuDisabledForeground,
            
            //"MenuBar.background", table.get("MenuBar.background"),
            "MenuBar.background", menuBackground,
            "MenuBar.foreground", menuForeground,
            
            "MenuItem.background", menuBackground,
            "MenuItem.foreground", menuForeground,
            "MenuItem.acceleratorForeground", menuForeground,
            "MenuItem.acceleratorSelectionForeground", menuSelectionForeground,
            "MenuItem.selectionBackground", menuSelectionBackground,
            "MenuItem.selectionForeground", menuSelectionForeground,
            "MenuItem.disabledBackground", menuDisabledBackground,
            "MenuItem.disabledForeground", menuDisabledForeground,
            
            "MenuSeparator.background", menuBackground,
            
            "OptionPane.background", controlBackground,
            "OptionPane.foreground", controlForeground,
            "OptionPane.messageForeground", controlForeground,
            
            "Panel.background", controlBackground,
            "Panel.foreground", controlForeground,
            
            "PasswordField.background", textBackground,
            "PasswordField.foreground", textForeground,
            "PasswordField.caretForeground", textForeground,
            "PasswordField.inactiveBackground", textBackground,
            "PasswordField.inactiveForeground", disabledForeground,
            "PasswordField.selectionBackground", textSelectionBackground,
            "PasswordField.selectionForeground", textForeground,
            
            "PopupMenu.foreground", menuForeground,
            "PopupMenu.background", menuBackground,
            "PopupMenu.selectionBackground", menuSelectionBackground,
            
            "RadioButton.disabledForeground", disabledForeground,
            "RadioButton.background", controlBackground,
            "RadioButton.foreground", controlForeground,
            //"RadioButton.shadow", ???,
            //"RadioButton.darkShadow", ???,
            //"RadioButton.light", ???,
            //"RadioButton.highlight", ???,
            
            "RadioButtonMenuItem.foreground", controlForeground,
            "RadioButtonMenuItem.selectionForeground", menuSelectionForeground,
            "RadioButtonMenuItem.background", menuBackground,
            "RadioButtonMenuItem.foreground", menuForeground,
            "RadioButtonMenuItem.acceleratorForeground", menuForeground,
            "RadioButtonMenuItem.acceleratorSelectionForeground", menuSelectionForeground,
            "RadioButtonMenuItem.selectionBackground", menuSelectionBackground,
            "RadioButtonMenuItem.selectionForeground", menuSelectionForeground,
            "RadioButtonMenuItem.disabledBackground", menuDisabledBackground,
            "RadioButtonMenuItem.disabledForeground", menuDisabledForeground,
            
            "RootPane.background", controlBackground,
            
            "ScrollBar.background", controlBackground,
            "ScrollBar.foreground", controlForeground,
            //"ScrollBar.track", ???,
            //"ScrollBar.trackHighlight", ???,
            //"ScrollBar.thumb", ???,
            //"ScrollBar.thumbHighlight", ???,
            //"ScrollBar.thumbDarkShadow", ???,
            //"ScrollBar.thumbShadow", ???,
            
            "ScrollPane.background", controlBackground,
            "ScrollPane.foreground", controlForeground,
            
            "Separator.background", controlBackground,
            "Separator.foreground", new ColorUIResource(0x808080),
            "Separator.highlight", new ColorUIResource(0xe0e0e0),
            "Separator.shadow", new ColorUIResource(0x808080),
            
            "Slider.background", controlBackground,
            "Slider.foreground", controlForeground,
            //"Slider.highlight", ???,
            //"Slider.shadow", ???,
            //"Slider.focus", ???,
            
            "Spinner.background", textBackground,
            "Spinner.foreground", controlForeground,
            "Spinner.borderPainted", Boolean.TRUE,
            
            "SplitPane.background", controlBackground,
            "SplitPane.foreground", controlForeground,
            //"SplitPane.highlight", ???,
            //"SplitPane.darkHighlight", ???,
            //"SplitPane.shadow", ???,
            //"SplitPane.darkShadow", ???,
            "SplitPaneDivider.draggingColor", new AlphaColorUIResource(0xa0666666),
            
            "TabbedPane.background", controlBackground,
            "TabbedPane.disabledForeground", disabledForeground,
            "TabbedPane.foreground", controlForeground,
            "TabbedPane.wrap.background", controlBackground,
            "TabbedPane.wrap.disabledForeground", disabledForeground,
            "TabbedPane.wrap.foreground", controlForeground,
            "TabbedPane.wrap.contentBorder", makeImageBevelBorder(
                    jaguarDir+"TabbedPane.contentBorder.png", new Insets(8, 7, 8, 7), new Insets(1, 3, 3, 3), false
                    ),
            "TabbedPane.wrapBarTopBorders", makeImageBevelBorders(
                    jaguarDir+"TabbedPane.wrapBarsTop.png", new Insets(0, 1, 0, 1), 3, true
                    ),
            "TabbedPane.wrapBarBottomBorders", makeImageBevelBorders(
                    jaguarDir+"TabbedPane.wrapBarsBottom.png", new Insets(0, 1, 0, 1), 3, false
                    ),
            "TabbedPane.wrapBarRightBorders", makeImageBevelBorders(
                    jaguarDir+"TabbedPane.wrapBarsRight.png", new Insets(1, 0, 1, 0), 3, true
                    ),
            "TabbedPane.wrapBarLeftBorders", makeImageBevelBorders(
                    jaguarDir+"TabbedPane.wrapBarsLeft.png", new Insets(1, 0, 1, 0), 3, true
                    ),
            "TabbedPane.scroll.background", controlBackground,
            "TabbedPane.scroll.disabledForeground", disabledForeground,
            "TabbedPane.scroll.foreground", controlForeground,
            
            "Table.alternateBackground.0", listAlternateBackground,
            "Table.alternateBackground.1", listBackground,
            "Table.focusCellBackground", listBackground,
            "Table.focusCellForeground", listForeground,
            "Table.background", listBackground,
            "Table.foreground", listForeground,
            "Table.selectionBackground", listSelectionBackground,
            "Table.selectionForeground", listSelectionForeground,
            //"Table.inactiveSelectionBackground", listInactiveSelectionBackground,
            //"Table.inactiveSelectionForeground", listInactiveSelectionForeground,
//            "Table.gridColor", new ColorUIResource(0xcccccc),
            "Table.gridColor", new AlphaColorUIResource(0x33000000),
            "Table.focusCellForeground", listSelectionForeground,
            "Table.focusCellBackground", listSelectionBackground,
            
            "TableHeader.background", controlBackground,
            "TableHeader.foreground", controlForeground,
            
            "TextArea.background", textBackground,
            "TextArea.foreground", textForeground,
            "TextArea.inactiveForeground", disabledForeground,
            "TextArea.selectionBackground", textSelectionBackground,
            "TextArea.selectionForeground", textForeground,
            
            "TextField.background", textBackground,
            "TextField.foreground", textForeground,
            "TextField.inactiveBackground", textBackground,
            "TextField.inactiveForeground", disabledForeground,
            "TextField.inactiveSelectionBackground", inactiveSelectionBackground,
            "TextField.selectionBackground", textSelectionBackground,
            "TextField.selectionForeground", textForeground,
            
            "TextPane.background", textBackground,
            "TextPane.foreground", textForeground,
            "TextPane.inactiveForeground", disabledForeground,
            "TextPane.selectionBackground", textSelectionBackground,
            "TextPane.selectionForeground", textForeground,
            
            "ToggleButton.background", controlBackground,
            "ToggleButton.disabledForeground", disabledForeground,
            "ToggleButton.foreground", controlForeground,
            //"ToggleButton.shadow", ???,
            //"ToggleButton.darkShadow", ???,
            //"ToggleButton.light", ???,
            //"ToggleButton.highlight", ???,
            
            //"ToolBar.background", table.get("control"),
            //"ToolBar.foreground", table.get("controlText"),
            //"ToolBar.shadow", table.getColor("controlShadow"),
            //"ToolBar.darkShadow", table.getColor("controlDkShadow"),
            //"ToolBar.light", table.getColor("controlHighlight"),
            //"ToolBar.highlight", table.getColor("controlLtHighlight"),
            //"ToolBar.dockingBackground", table.get("control"),
            //"ToolBar.floatingBackground", table.get("control"),
            "ToolBar.dockingForeground", listSelectionBackground,
            "ToolBar.floatingForeground", new AlphaColorUIResource(0x00000000),
            
            "ToolTip.foreground", table.get("infoText"),
            "ToolTip.background", table.get("info"),
            
            "Tree.alternateBackground.0", listAlternateBackground,
            "Tree.alternateBackground.1", listBackground,
            "Tree.selectionBackground", new MutableColorUIResource(((Color) table.get("listHighlight")).getRGB()),
            "Tree.activeSelectionBackground", table.get("listHighlight"),
            "Tree.inactiveSelectionBackground", inactiveSelectionBackground,
            "Tree.selectionBorderColor", listSelectionBorderColor,
            "Tree.selectionForeground", new MutableColorUIResource(((Color) table.get("listHighlightText")).getRGB()),
            "Tree.activeSelectionForeground", table.get("listHighlightText"),
            "Tree.inactiveSelectionForeground", inactiveSelectionForeground,
            //"Tree.inactiveSelectionBackground", listInactiveSelectionBackground,
            //"Tree.inactiveSelectionForeground", listInactiveSelectionForeground,
            "Tree.controlForeground", listForeground,
            "Tree.textBackground", translucentColor,
            "Tree.textForeground", listForeground,
            
            "Viewport.background", listBackground,
            "Viewport.foreground", listForeground,
        };
        table.putDefaults(objects);
    }
    
    protected void initInputMapDefaults(UIDefaults table) {
        // Input map for text fields
        Object fieldInputMap = new UIDefaults.LazyInputMap(new String[] {
            //, DefaultEditorKit.insertContentAction,
            //, DefaultEditorKit.insertBreakAction,
            //, DefaultEditorKit.insertTabAction,
            "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
            "DELETE", DefaultEditorKit.deleteNextCharAction,
            //, DefaultEditorKit.readOnlyAction,
            //, DefaultEditorKit.writableAction,
            "meta X", DefaultEditorKit.cutAction,
            "meta C", DefaultEditorKit.copyAction,
            "meta V", DefaultEditorKit.pasteAction,
            "CUT", DefaultEditorKit.cutAction,
            "COPY", DefaultEditorKit.copyAction,
            "PASTE", DefaultEditorKit.pasteAction,
            //, DefaultEditorKit.beepAction,
            //, DefaultEditorKit.pageUpAction,
            //, DefaultEditorKit.pageDownAction,
            //, DefaultEditorKit.selectionPageUpAction,
            //, DefaultEditorKit.selectionPageDownAction,
            //, DefaultEditorKit.selectionPageLeftAction,
            //, DefaultEditorKit.selectionPageRightAction,
            "RIGHT", DefaultEditorKit.forwardAction,
            "KP_RIGHT", DefaultEditorKit.forwardAction,
            "LEFT", DefaultEditorKit.backwardAction,
            "KP_LEFT", DefaultEditorKit.backwardAction,
            "shift RIGHT", DefaultEditorKit.selectionForwardAction,
            "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
            "shift LEFT", DefaultEditorKit.selectionBackwardAction,
            "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
            "UP", DefaultEditorKit.upAction,
            "KP_UP", DefaultEditorKit.upAction,
            "DOWN", DefaultEditorKit.downAction,
            "KP_DOWN", DefaultEditorKit.downAction,
            "shift UP", DefaultEditorKit.selectionUpAction,
            "shift KP_UP", DefaultEditorKit.selectionUpAction,
            "shift DOWN", DefaultEditorKit.selectionDownAction,
            "shift KP_DOWN", DefaultEditorKit.selectionDownAction,
            //, DefaultEditorKit.beginWordAction,
            //, DefaultEditorKit.endWordAction,
            //, DefaultEditorKit.selectionBeginWordAction,
            //, DefaultEditorKit.selectionEndWordAction,
            "alt LEFT", DefaultEditorKit.previousWordAction,
            "alt KP_LEFT", DefaultEditorKit.previousWordAction,
            "alt RIGHT", DefaultEditorKit.nextWordAction,
            "alt KP_RIGHT", DefaultEditorKit.nextWordAction,
            "alt shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "alt shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "alt shift RIGHT", DefaultEditorKit.selectionNextWordAction,
            "alt shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
            "alt UP", DefaultEditorKit.beginLineAction,
            "alt KP_UP", DefaultEditorKit.beginLineAction,
            "ctrl LEFT", DefaultEditorKit.beginLineAction,
            "ctrl KP_LEFT", DefaultEditorKit.beginLineAction,
            "meta LEFT", DefaultEditorKit.beginLineAction,
            "meta KP_LEFT", DefaultEditorKit.beginLineAction,
            "alt DOWN", DefaultEditorKit.endLineAction,
            "alt KP_DOWN", DefaultEditorKit.endLineAction,
            "ctrl RIGHT", DefaultEditorKit.endLineAction,
            "ctrl KP_RIGHT", DefaultEditorKit.endLineAction,
            "meta RIGHT", DefaultEditorKit.endLineAction,
            "meta KP_RIGHT", DefaultEditorKit.endLineAction,
            "ctrl shift LEFT", DefaultEditorKit.selectionBeginLineAction,
            "ctrl shift KP_LEFT", DefaultEditorKit.selectionBeginLineAction,
            "meta shift LEFT", DefaultEditorKit.selectionBeginLineAction,
            "meta shift KP_LEFT", DefaultEditorKit.selectionBeginLineAction,
            "ctrl shift RIGHT", DefaultEditorKit.selectionEndLineAction,
            "ctrl shift KP_RIGHT", DefaultEditorKit.selectionEndLineAction,
            "meta shift RIGHT", DefaultEditorKit.selectionEndLineAction,
            "meta shift KP_RIGHT", DefaultEditorKit.selectionEndLineAction,
            //, DefaultEditorKit.beginParagraphAction,
            //, DefaultEditorKit.endParagraphAction,
            //, DefaultEditorKit.selectionBeginParagraphAction,
            //, DefaultEditorKit.selectionEndParagraphAction,
            "HOME", DefaultEditorKit.beginAction,
            "END", DefaultEditorKit.endAction,
            "meta UP", DefaultEditorKit.beginAction,
            "meta KP_UP", DefaultEditorKit.beginAction,
            "meta DOWN", DefaultEditorKit.endAction,
            "meta KP_DOWN", DefaultEditorKit.endAction,
            "shift HOME", DefaultEditorKit.selectionBeginAction,
            "shift END", DefaultEditorKit.selectionEndAction,
            //, DefaultEditorKit.selectWordAction,
            //, DefaultEditorKit.selectLineAction,
            //, DefaultEditorKit.selectParagraphAction,
            "meta A", DefaultEditorKit.selectAllAction,
            "meta shift A", "unselect"/*DefaultEditorKit.unselectAction*/,
            "controlBackground shift O", "toggle-componentOrientation", /*DefaultEditorKit.toggleComponentOrientation*/
            
            "alt DELETE", QuaquaEditorKit.deleteNextWordAction,
            "alt BACK_SPACE", QuaquaEditorKit.deletePrevWordAction,
            "ENTER", JTextField.notifyAction,
        }
        );
        // Input map for password fields
        Object passwordFieldInputMap = new UIDefaults.LazyInputMap(new String[] {
            //, DefaultEditorKit.insertContentAction,
            //, DefaultEditorKit.insertBreakAction,
            //, DefaultEditorKit.insertTabAction,
            "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
            "DELETE", DefaultEditorKit.deleteNextCharAction,
            //, DefaultEditorKit.readOnlyAction,
            //, DefaultEditorKit.writableAction,
            "meta X", DefaultEditorKit.beepAction,
            "meta C", DefaultEditorKit.beepAction,
            "meta V", DefaultEditorKit.pasteAction,
            "CUT", DefaultEditorKit.beepAction,
            "COPY", DefaultEditorKit.beepAction,
            "PASTE", DefaultEditorKit.pasteAction,
            //, DefaultEditorKit.beepAction,
            //, DefaultEditorKit.pageUpAction,
            //, DefaultEditorKit.pageDownAction,
            //, DefaultEditorKit.selectionPageUpAction,
            //, DefaultEditorKit.selectionPageDownAction,
            //, DefaultEditorKit.selectionPageLeftAction,
            //, DefaultEditorKit.selectionPageRightAction,
            "RIGHT", DefaultEditorKit.forwardAction,
            "KP_RIGHT", DefaultEditorKit.forwardAction,
            "LEFT", DefaultEditorKit.backwardAction,
            "KP_LEFT", DefaultEditorKit.backwardAction,
            "shift RIGHT", DefaultEditorKit.selectionForwardAction,
            "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
            "shift LEFT", DefaultEditorKit.selectionBackwardAction,
            "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
            "UP", DefaultEditorKit.upAction,
            "KP_UP", DefaultEditorKit.upAction,
            "DOWN", DefaultEditorKit.downAction,
            "KP_DOWN", DefaultEditorKit.downAction,
            "shift UP", DefaultEditorKit.selectionUpAction,
            "shift KP_UP", DefaultEditorKit.selectionUpAction,
            "shift DOWN", DefaultEditorKit.selectionDownAction,
            "shift KP_DOWN", DefaultEditorKit.selectionDownAction,
            //, DefaultEditorKit.beginWordAction,
            //, DefaultEditorKit.endWordAction,
            //, DefaultEditorKit.selectionBeginWordAction,
            //, DefaultEditorKit.selectionEndWordAction,
            "alt LEFT", DefaultEditorKit.previousWordAction,
            "alt KP_LEFT", DefaultEditorKit.previousWordAction,
            "alt RIGHT", DefaultEditorKit.nextWordAction,
            "alt KP_RIGHT", DefaultEditorKit.nextWordAction,
            "alt shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "alt shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "alt shift RIGHT", DefaultEditorKit.selectionNextWordAction,
            "alt shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
            "alt UP", DefaultEditorKit.beginLineAction,
            "alt KP_UP", DefaultEditorKit.beginLineAction,
            "ctrl LEFT", DefaultEditorKit.beginLineAction,
            "ctrl KP_LEFT", DefaultEditorKit.beginLineAction,
            "meta LEFT", DefaultEditorKit.beginLineAction,
            "meta KP_LEFT", DefaultEditorKit.beginLineAction,
            "alt DOWN", DefaultEditorKit.endLineAction,
            "alt KP_DOWN", DefaultEditorKit.endLineAction,
            "ctrl RIGHT", DefaultEditorKit.endLineAction,
            "ctrl KP_RIGHT", DefaultEditorKit.endLineAction,
            "meta RIGHT", DefaultEditorKit.endLineAction,
            "meta KP_RIGHT", DefaultEditorKit.endLineAction,
            "ctrl shift LEFT", DefaultEditorKit.selectionBeginLineAction,
            "ctrl shift KP_LEFT", DefaultEditorKit.selectionBeginLineAction,
            "meta shift LEFT", DefaultEditorKit.selectionBeginLineAction,
            "meta shift KP_LEFT", DefaultEditorKit.selectionBeginLineAction,
            "ctrl shift RIGHT", DefaultEditorKit.selectionEndLineAction,
            "ctrl shift KP_RIGHT", DefaultEditorKit.selectionEndLineAction,
            "meta shift RIGHT", DefaultEditorKit.selectionEndLineAction,
            "meta shift KP_RIGHT", DefaultEditorKit.selectionEndLineAction,
            //, DefaultEditorKit.beginParagraphAction,
            //, DefaultEditorKit.endParagraphAction,
            //, DefaultEditorKit.selectionBeginParagraphAction,
            //, DefaultEditorKit.selectionEndParagraphAction,
            "HOME", DefaultEditorKit.beginAction,
            "END", DefaultEditorKit.endAction,
            "meta UP", DefaultEditorKit.beginAction,
            "meta KP_UP", DefaultEditorKit.beginAction,
            "meta DOWN", DefaultEditorKit.endAction,
            "meta KP_DOWN", DefaultEditorKit.endAction,
            "shift HOME", DefaultEditorKit.selectionBeginAction,
            "shift END", DefaultEditorKit.selectionEndAction,
            //, DefaultEditorKit.selectWordAction,
            //, DefaultEditorKit.selectLineAction,
            //, DefaultEditorKit.selectParagraphAction,
            "meta A", DefaultEditorKit.selectAllAction,
            "meta shift A", "unselect"/*DefaultEditorKit.unselectAction*/,
            "controlBackground shift O", "toggle-componentOrientation", /*DefaultEditorKit.toggleComponentOrientation*/
            
            "alt DELETE", QuaquaEditorKit.deleteNextWordAction,
            "alt BACK_SPACE", QuaquaEditorKit.deletePrevWordAction,
            "ENTER", JTextField.notifyAction,
        }
        );
        // Input map for spinner editors
        Object spinnerInputMap = new UIDefaults.LazyInputMap(new String[] {
            //, DefaultEditorKit.insertContentAction,
            //, DefaultEditorKit.insertBreakAction,
            //, DefaultEditorKit.insertTabAction,
            "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
            "DELETE", DefaultEditorKit.deleteNextCharAction,
            //, DefaultEditorKit.readOnlyAction,
            //, DefaultEditorKit.writableAction,
            "meta X", DefaultEditorKit.cutAction,
            "meta C", DefaultEditorKit.copyAction,
            "meta V", DefaultEditorKit.pasteAction,
            "CUT", DefaultEditorKit.cutAction,
            "COPY", DefaultEditorKit.copyAction,
            "PASTE", DefaultEditorKit.pasteAction,
            //, DefaultEditorKit.beepAction,
            //, DefaultEditorKit.pageUpAction,
            //, DefaultEditorKit.pageDownAction,
            //, DefaultEditorKit.selectionPageUpAction,
            //, DefaultEditorKit.selectionPageDownAction,
            //, DefaultEditorKit.selectionPageLeftAction,
            //, DefaultEditorKit.selectionPageRightAction,
            "RIGHT", DefaultEditorKit.forwardAction,
            "KP_RIGHT", DefaultEditorKit.forwardAction,
            "LEFT", DefaultEditorKit.backwardAction,
            "KP_LEFT", DefaultEditorKit.backwardAction,
            "shift RIGHT", DefaultEditorKit.selectionForwardAction,
            "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
            "shift LEFT", DefaultEditorKit.selectionBackwardAction,
            "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
            //"UP", DefaultEditorKit.upAction,
            //"KP_UP", DefaultEditorKit.upAction,
            //"DOWN", DefaultEditorKit.downAction,
            //"KP_DOWN", DefaultEditorKit.downAction,
            "UP", "increment",
            "KP_UP", "increment",
            "DOWN", "decrement",
            "KP_DOWN", "decrement",
            
            "shift UP", DefaultEditorKit.selectionUpAction,
            "shift KP_UP", DefaultEditorKit.selectionUpAction,
            "shift DOWN", DefaultEditorKit.selectionDownAction,
            "shift KP_DOWN", DefaultEditorKit.selectionDownAction,
            //, DefaultEditorKit.beginWordAction,
            //, DefaultEditorKit.endWordAction,
            //, DefaultEditorKit.selectionBeginWordAction,
            //, DefaultEditorKit.selectionEndWordAction,
            "alt LEFT", DefaultEditorKit.previousWordAction,
            "alt KP_LEFT", DefaultEditorKit.previousWordAction,
            "alt RIGHT", DefaultEditorKit.nextWordAction,
            "alt KP_RIGHT", DefaultEditorKit.nextWordAction,
            "alt shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "alt shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "alt shift RIGHT", DefaultEditorKit.selectionNextWordAction,
            "alt shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
            "alt UP", DefaultEditorKit.beginLineAction,
            "alt KP_UP", DefaultEditorKit.beginLineAction,
            "ctrl LEFT", DefaultEditorKit.beginLineAction,
            "ctrl KP_LEFT", DefaultEditorKit.beginLineAction,
            "meta LEFT", DefaultEditorKit.beginLineAction,
            "meta KP_LEFT", DefaultEditorKit.beginLineAction,
            "alt DOWN", DefaultEditorKit.endLineAction,
            "alt KP_DOWN", DefaultEditorKit.endLineAction,
            "ctrl RIGHT", DefaultEditorKit.endLineAction,
            "ctrl KP_RIGHT", DefaultEditorKit.endLineAction,
            "meta RIGHT", DefaultEditorKit.endLineAction,
            "meta KP_RIGHT", DefaultEditorKit.endLineAction,
            "ctrl shift LEFT", DefaultEditorKit.selectionBeginLineAction,
            "ctrl shift KP_LEFT", DefaultEditorKit.selectionBeginLineAction,
            "meta shift LEFT", DefaultEditorKit.selectionBeginLineAction,
            "meta shift KP_LEFT", DefaultEditorKit.selectionBeginLineAction,
            "ctrl shift RIGHT", DefaultEditorKit.selectionEndLineAction,
            "ctrl shift KP_RIGHT", DefaultEditorKit.selectionEndLineAction,
            "meta shift RIGHT", DefaultEditorKit.selectionEndLineAction,
            "meta shift KP_RIGHT", DefaultEditorKit.selectionEndLineAction,
            //, DefaultEditorKit.beginParagraphAction,
            //, DefaultEditorKit.endParagraphAction,
            //, DefaultEditorKit.selectionBeginParagraphAction,
            //, DefaultEditorKit.selectionEndParagraphAction,
            "HOME", DefaultEditorKit.beginAction,
            "END", DefaultEditorKit.endAction,
            "meta UP", DefaultEditorKit.beginAction,
            "meta KP_UP", DefaultEditorKit.beginAction,
            "meta DOWN", DefaultEditorKit.endAction,
            "meta KP_DOWN", DefaultEditorKit.endAction,
            "shift HOME", DefaultEditorKit.selectionBeginAction,
            "shift END", DefaultEditorKit.selectionEndAction,
            //, DefaultEditorKit.selectWordAction,
            //, DefaultEditorKit.selectLineAction,
            //, DefaultEditorKit.selectParagraphAction,
            "meta A", DefaultEditorKit.selectAllAction,
            "meta shift A", "unselect"/*DefaultEditorKit.unselectAction*/,
            "controlBackground shift O", "toggle-componentOrientation", /*DefaultEditorKit.toggleComponentOrientation*/
            
            "alt DELETE", QuaquaEditorKit.deleteNextWordAction,
            "alt BACK_SPACE", QuaquaEditorKit.deletePrevWordAction,
            "ENTER", JTextField.notifyAction,
        }
        );
        // Input map for multiline text fields
        Object multilineInputMap = new UIDefaults.LazyInputMap(new String[] {
            //, DefaultEditorKit.insertContentAction,
            "shift ENTER", DefaultEditorKit.insertBreakAction,
            "alt ENTER", DefaultEditorKit.insertBreakAction,
            "ENTER", DefaultEditorKit.insertBreakAction,
            "TAB", DefaultEditorKit.insertTabAction,
            "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
            "DELETE", DefaultEditorKit.deleteNextCharAction,
            //, DefaultEditorKit.readOnlyAction,
            //, DefaultEditorKit.writableAction,
            "meta X", DefaultEditorKit.cutAction,
            "meta C", DefaultEditorKit.copyAction,
            "meta V", DefaultEditorKit.pasteAction,
            "CUT", DefaultEditorKit.cutAction,
            "COPY", DefaultEditorKit.copyAction,
            "PASTE", DefaultEditorKit.pasteAction,
            //, DefaultEditorKit.beepAction,
            "PAGE_UP", DefaultEditorKit.pageUpAction,
            "PAGE_DOWN", DefaultEditorKit.pageDownAction,
            "shift PAGE_UP", "selection-page-up",
            "shift PAGE_DOWN", "selection-page-down",
            "ctrl shift PAGE_UP", "selection-page-left",
            "ctrl shift PAGE_DOWN", "selection-page-right",
            "RIGHT", DefaultEditorKit.forwardAction,
            "KP_RIGHT", DefaultEditorKit.forwardAction,
            "LEFT", DefaultEditorKit.backwardAction,
            "KP_LEFT", DefaultEditorKit.backwardAction,
            "shift RIGHT", DefaultEditorKit.selectionForwardAction,
            "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
            "shift LEFT", DefaultEditorKit.selectionBackwardAction,
            "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
            "UP", DefaultEditorKit.upAction,
            "KP_UP", DefaultEditorKit.upAction,
            "DOWN", DefaultEditorKit.downAction,
            "KP_DOWN", DefaultEditorKit.downAction,
            "shift UP", DefaultEditorKit.selectionUpAction,
            "shift KP_UP", DefaultEditorKit.selectionUpAction,
            "shift DOWN", DefaultEditorKit.selectionDownAction,
            "shift KP_DOWN", DefaultEditorKit.selectionDownAction,
            //, DefaultEditorKit.beginWordAction,
            //, DefaultEditorKit.endWordAction,
            //, DefaultEditorKit.selectionBeginWordAction,
            //, DefaultEditorKit.selectionEndWordAction,
            "alt LEFT", DefaultEditorKit.previousWordAction,
            "alt KP_LEFT", DefaultEditorKit.previousWordAction,
            "alt RIGHT", DefaultEditorKit.nextWordAction,
            "alt KP_RIGHT", DefaultEditorKit.nextWordAction,
            "alt shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "alt shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "alt shift RIGHT", DefaultEditorKit.selectionNextWordAction,
            "alt shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
            "alt UP", DefaultEditorKit.beginLineAction,
            "alt KP_UP", DefaultEditorKit.beginLineAction,
            "ctrl LEFT", DefaultEditorKit.beginLineAction,
            "ctrl KP_LEFT", DefaultEditorKit.beginLineAction,
            "meta LEFT", DefaultEditorKit.beginLineAction,
            "meta KP_LEFT", DefaultEditorKit.beginLineAction,
            "alt DOWN", DefaultEditorKit.endLineAction,
            "alt KP_DOWN", DefaultEditorKit.endLineAction,
            "ctrl RIGHT", DefaultEditorKit.endLineAction,
            "ctrl KP_RIGHT", DefaultEditorKit.endLineAction,
            "meta RIGHT", DefaultEditorKit.endLineAction,
            "meta KP_RIGHT", DefaultEditorKit.endLineAction,
            "ctrl shift LEFT", DefaultEditorKit.selectionBeginLineAction,
            "ctrl shift KP_LEFT", DefaultEditorKit.selectionBeginLineAction,
            "meta shift LEFT", DefaultEditorKit.selectionBeginLineAction,
            "meta shift KP_LEFT", DefaultEditorKit.selectionBeginLineAction,
            "ctrl shift RIGHT", DefaultEditorKit.selectionEndLineAction,
            "ctrl shift KP_RIGHT", DefaultEditorKit.selectionEndLineAction,
            "meta shift RIGHT", DefaultEditorKit.selectionEndLineAction,
            "meta shift KP_RIGHT", DefaultEditorKit.selectionEndLineAction,
            //, DefaultEditorKit.beginParagraphAction,
            //, DefaultEditorKit.endParagraphAction,
            //, DefaultEditorKit.selectionBeginParagraphAction,
            //, DefaultEditorKit.selectionEndParagraphAction,
            "HOME", DefaultEditorKit.beginAction,
            "END", DefaultEditorKit.endAction,
            "meta UP", DefaultEditorKit.beginAction,
            "meta KP_UP", DefaultEditorKit.beginAction,
            "meta DOWN", DefaultEditorKit.endAction,
            "meta KP_DOWN", DefaultEditorKit.endAction,
            "shift HOME", DefaultEditorKit.selectionBeginAction,
            "shift END", DefaultEditorKit.selectionEndAction,
            //, DefaultEditorKit.selectWordAction,
            //, DefaultEditorKit.selectLineAction,
            //, DefaultEditorKit.selectParagraphAction,
            "meta A", DefaultEditorKit.selectAllAction,
            "meta shift A", "unselect"/*DefaultEditorKit.unselectAction*/,
            "controlBackground shift O", "toggle-componentOrientation", /*DefaultEditorKit.toggleComponentOrientation*/
            
            "alt DELETE", QuaquaEditorKit.deleteNextWordAction,
            "alt BACK_SPACE", QuaquaEditorKit.deletePrevWordAction,
        }
        );
        
        // Input map for the editors of combo boxes
        Object comboEditorInputMap = new UIDefaults.LazyInputMap(new String[] {
            //, DefaultEditorKit.insertContentAction,
            //, DefaultEditorKit.insertBreakAction,
            //, DefaultEditorKit.insertTabAction,
            "BACK_SPACE", DefaultEditorKit.deletePrevCharAction,
            "DELETE", DefaultEditorKit.deleteNextCharAction,
            //, DefaultEditorKit.readOnlyAction,
            //, DefaultEditorKit.writableAction,
            "meta X", DefaultEditorKit.cutAction,
            "meta C", DefaultEditorKit.copyAction,
            "meta V", DefaultEditorKit.pasteAction,
            "CUT", DefaultEditorKit.cutAction,
            "COPY", DefaultEditorKit.copyAction,
            "PASTE", DefaultEditorKit.pasteAction,
            //, DefaultEditorKit.beepAction,
            //, DefaultEditorKit.pageUpAction,
            //, DefaultEditorKit.pageDownAction,
            //, DefaultEditorKit.selectionPageUpAction,
            //, DefaultEditorKit.selectionPageDownAction,
            //, DefaultEditorKit.selectionPageLeftAction,
            //, DefaultEditorKit.selectionPageRightAction,
            "RIGHT", DefaultEditorKit.forwardAction,
            "KP_RIGHT", DefaultEditorKit.forwardAction,
            "LEFT", DefaultEditorKit.backwardAction,
            "KP_LEFT", DefaultEditorKit.backwardAction,
            "shift RIGHT", DefaultEditorKit.selectionForwardAction,
            "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
            "shift LEFT", DefaultEditorKit.selectionBackwardAction,
            "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
            //"UP", DefaultEditorKit.upAction,
            //"DOWN", DefaultEditorKit.downAction,
            //"shift UP", DefaultEditorKit.selectionUpAction,
            //"shift DOWN", DefaultEditorKit.selectionDownAction,
            //, DefaultEditorKit.beginWordAction,
            //, DefaultEditorKit.endWordAction,
            //, DefaultEditorKit.selectionBeginWordAction,
            //, DefaultEditorKit.selectionEndWordAction,
            "alt LEFT", DefaultEditorKit.previousWordAction,
            "alt KP_LEFT", DefaultEditorKit.previousWordAction,
            "alt RIGHT", DefaultEditorKit.nextWordAction,
            "alt KP_RIGHT", DefaultEditorKit.nextWordAction,
            "alt shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "alt shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
            "alt shift RIGHT", DefaultEditorKit.selectionNextWordAction,
            "alt shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
            //"alt UP", DefaultEditorKit.beginLineAction,
            "ctrl LEFT", DefaultEditorKit.beginLineAction,
            "meta LEFT", DefaultEditorKit.beginLineAction,
            //"alt DOWN", DefaultEditorKit.endLineAction,
            "ctrl RIGHT", DefaultEditorKit.endLineAction,
            "meta RIGHT", DefaultEditorKit.endLineAction,
            "ctrl shift LEFT", DefaultEditorKit.selectionBeginLineAction,
            "meta shift LEFT", DefaultEditorKit.selectionBeginLineAction,
            "ctrl shift RIGHT", DefaultEditorKit.selectionEndLineAction,
            "meta shift RIGHT", DefaultEditorKit.selectionEndLineAction,
            //, DefaultEditorKit.beginParagraphAction,
            //, DefaultEditorKit.endParagraphAction,
            //, DefaultEditorKit.selectionBeginParagraphAction,
            //, DefaultEditorKit.selectionEndParagraphAction,
            //"HOME", DefaultEditorKit.beginAction,
            //"END", DefaultEditorKit.endAction,
            //"meta UP", DefaultEditorKit.beginAction,
            //"meta DOWN", DefaultEditorKit.endAction,
            "shift HOME", DefaultEditorKit.selectionBeginAction,
            "shift END", DefaultEditorKit.selectionEndAction,
            //, DefaultEditorKit.selectWordAction,
            //, DefaultEditorKit.selectLineAction,
            //, DefaultEditorKit.selectParagraphAction,
            "meta A", DefaultEditorKit.selectAllAction,
            "meta shift A", "unselect"/*DefaultEditorKit.unselectAction*/,
            "controlBackground shift O", "toggle-componentOrientation", /*DefaultEditorKit.toggleComponentOrientation*/
            
            "alt DELETE", QuaquaEditorKit.deleteNextWordAction,
            "alt BACK_SPACE", QuaquaEditorKit.deletePrevWordAction,
            "ENTER", JTextField.notifyAction,
        }
        );
        
        UIDefaults.LazyInputMap tabbedPaneFocusInputMap =
                new UIDefaults.LazyInputMap(new Object[] {
            "RIGHT", "navigateRight",
            "KP_RIGHT", "navigateRight",
            "LEFT", "navigateLeft",
            "KP_LEFT", "navigateLeft",
            "UP", "navigateUp",
            "KP_UP", "navigateUp",
            "DOWN", "navigateDown",
            "KP_DOWN", "navigateDown",
            "ctrl DOWN", "requestFocusForVisibleComponent",
            "ctrl KP_DOWN", "requestFocusForVisibleComponent",
        });
        UIDefaults.LazyInputMap tabbedPaneAncestorInputMap =
                new UIDefaults.LazyInputMap(new Object[] {
            "ctrl PAGE_DOWN", "navigatePageDown",
            "ctrl PAGE_UP", "navigatePageUp",
            "ctrl UP", "requestFocus",
            "ctrl KP_UP", "requestFocus",
        });
        
	   UIDefaults.LazyInputMap tableAncestorInputMap = 
	       new UIDefaults.LazyInputMap(new Object[] {
                               "ctrl C", "copy",
                               "ctrl V", "paste",
                               "ctrl X", "cut",
                                 "COPY", "copy",
                                "PASTE", "paste",
                                  "CUT", "cut",
                                "RIGHT", "selectNextColumn",
                             "KP_RIGHT", "selectNextColumn",
                          "shift RIGHT", "selectNextColumnExtendSelection",
                       "shift KP_RIGHT", "selectNextColumnExtendSelection",
                     "ctrl shift RIGHT", "selectNextColumnExtendSelection",
                  "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection",
                           "ctrl RIGHT", "selectNextColumnChangeLead",
                        "ctrl KP_RIGHT", "selectNextColumnChangeLead",
                                 "LEFT", "selectPreviousColumn",
                              "KP_LEFT", "selectPreviousColumn",
                           "shift LEFT", "selectPreviousColumnExtendSelection",
                        "shift KP_LEFT", "selectPreviousColumnExtendSelection",
                      "ctrl shift LEFT", "selectPreviousColumnExtendSelection",
                   "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection",
                            "ctrl LEFT", "selectPreviousColumnChangeLead",
                         "ctrl KP_LEFT", "selectPreviousColumnChangeLead",
                                 "DOWN", "selectNextRow",
                              "KP_DOWN", "selectNextRow",
                           "shift DOWN", "selectNextRowExtendSelection",
                        "shift KP_DOWN", "selectNextRowExtendSelection",
                      "ctrl shift DOWN", "selectNextRowExtendSelection",
                   "ctrl shift KP_DOWN", "selectNextRowExtendSelection",
                            "ctrl DOWN", "selectNextRowChangeLead",
                         "ctrl KP_DOWN", "selectNextRowChangeLead",
                                   "UP", "selectPreviousRow",
                                "KP_UP", "selectPreviousRow",
                             "shift UP", "selectPreviousRowExtendSelection",
                          "shift KP_UP", "selectPreviousRowExtendSelection",
                        "ctrl shift UP", "selectPreviousRowExtendSelection",
                     "ctrl shift KP_UP", "selectPreviousRowExtendSelection",
                              "ctrl UP", "selectPreviousRowChangeLead",
                           "ctrl KP_UP", "selectPreviousRowChangeLead",
                                 "HOME", "selectFirstColumn",
                           "shift HOME", "selectFirstColumnExtendSelection",
                      "ctrl shift HOME", "selectFirstRowExtendSelection",
                            "ctrl HOME", "selectFirstRow",
                                  "END", "selectLastColumn",
                            "shift END", "selectLastColumnExtendSelection",
                       "ctrl shift END", "selectLastRowExtendSelection",
                             "ctrl END", "selectLastRow",
                              "PAGE_UP", "scrollUpChangeSelection",
                        "shift PAGE_UP", "scrollUpExtendSelection",
                   "ctrl shift PAGE_UP", "scrollLeftExtendSelection",
                         "ctrl PAGE_UP", "scrollLeftChangeSelection",
                            "PAGE_DOWN", "scrollDownChangeSelection",
                      "shift PAGE_DOWN", "scrollDownExtendSelection",
                 "ctrl shift PAGE_DOWN", "scrollRightExtendSelection",
                       "ctrl PAGE_DOWN", "scrollRightChangeSelection",
                                  "TAB", "selectNextColumnCell",
                            "shift TAB", "selectPreviousColumnCell",
                                "ENTER", "selectNextRowCell",
                          "shift ENTER", "selectPreviousRowCell",
                               "ctrl A", "selectAll",
                           "ctrl SLASH", "selectAll",
                      "ctrl BACK_SLASH", "clearSelection",
                               "ESCAPE", "cancel",
                                   "F2", "startEditing",
                                "SPACE", "addToSelection",
                           "ctrl SPACE", "toggleAndAnchor",
                          "shift SPACE", "extendTo",
                     "ctrl shift SPACE", "moveSelectionTo"
		 });
            
	   UIDefaults.LazyInputMap tableAncestorInputMapRightToLeft = 
	       new UIDefaults.LazyInputMap(new Object[] {
		                "RIGHT", "selectPreviousColumn",
		             "KP_RIGHT", "selectPreviousColumn",
                          "shift RIGHT", "selectPreviousColumnExtendSelection",
                       "shift KP_RIGHT", "selectPreviousColumnExtendSelection",
                     "ctrl shift RIGHT", "selectPreviousColumnExtendSelection",
                  "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection",
                          "shift RIGHT", "selectPreviousColumnChangeLead",
                       "shift KP_RIGHT", "selectPreviousColumnChangeLead",
		                 "LEFT", "selectNextColumn",
		              "KP_LEFT", "selectNextColumn",
		           "shift LEFT", "selectNextColumnExtendSelection",
		        "shift KP_LEFT", "selectNextColumnExtendSelection",
                      "ctrl shift LEFT", "selectNextColumnExtendSelection",
                   "ctrl shift KP_LEFT", "selectNextColumnExtendSelection",
                            "ctrl LEFT", "selectNextColumnChangeLead",
                         "ctrl KP_LEFT", "selectNextColumnChangeLead",
		         "ctrl PAGE_UP", "scrollRightChangeSelection",
		       "ctrl PAGE_DOWN", "scrollLeftChangeSelection",
		   "ctrl shift PAGE_UP", "scrollRightExtendSelection",
		 "ctrl shift PAGE_DOWN", "scrollLeftExtendSelection",
		 });
        
        // Assign the defaults
        Object[] objects = {
            //"Button.focusInputMap", ...,
            
            //"CheckBox.focusInputMap", ...,
            
            //"ComboBox.actionMap", ...,
            "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] {
                "ESCAPE", "hidePopup",
                "PAGE_UP", "pageUpPassThrough",
                "PAGE_DOWN", "pageDownPassThrough",
                "HOME", "homePassThrough",
                "END", "endPassThrough",
                "DOWN", "selectNext",
                "KP_DOWN", "selectNext",
                "alt DOWN", "togglePopup",
                "alt KP_DOWN", "togglePopup",
                "alt UP", "togglePopup",
                "alt KP_UP", "togglePopup",
                "SPACE", "spacePopup",
                "ENTER", "enterPressed",
                "UP", "selectPrevious",
                "KP_UP", "selectPrevious"
            }),
            "ComboBox.editorInputMap", comboEditorInputMap,
            
            //"Desktop.ancestorInputMap", ...,
            
            "FormattedTextField.focusInputMap", fieldInputMap,
            "FormattedTextField.keyBindings", null,
            
            "PasswordField.focusInputMap", passwordFieldInputMap,
            "PasswordField.keyBindings", null,
            
            // These bindings are only enabled when there is a default
            // button set on the RootPane.
            "RootPane.defaultButtonWindowKeyBindings", new Object[] {
                "ENTER", "press",
                "released ENTER", "release",
                "ctrl ENTER", "press",
                "ctrl released ENTER", "release"
            },
            
            "Spinner.ancestorInputMap",
            new UIDefaults.LazyInputMap(new Object[] {
                "UP", "increment",
                "KP_UP", "increment",
                "DOWN", "decrement",
                "KP_DOWN", "decrement",
            }),
            "Spinner.focusInputMap", spinnerInputMap,
            
            "TabbedPane.focusInputMap", tabbedPaneFocusInputMap,
            "TabbedPane.ancestorInputMap", tabbedPaneAncestorInputMap,
            "TabbedPane.actionMap", table.get("TabbedPane.actionMap"),
            "TabbedPane.wrap.focusInputMap", tabbedPaneFocusInputMap,
            "TabbedPane.wrap.ancestorInputMap", tabbedPaneAncestorInputMap,
            "TabbedPane.wrap.actionMap", table.get("TabbedPane.actionMap"),
            "TabbedPane.scroll.focusInputMap", tabbedPaneFocusInputMap,
            "TabbedPane.scroll.ancestorInputMap", tabbedPaneAncestorInputMap,
            "TabbedPane.scroll.actionMap", table.get("TabbedPane.actionMap"),
            
            "Table.ancestorInputMap", tableAncestorInputMap,
            "Table.ancestorInputMap.rightToLeft", tableAncestorInputMapRightToLeft,
            
            "TextArea.focusInputMap", multilineInputMap,
            "TextArea.keyBindings", null,
            
            "TextField.focusInputMap", fieldInputMap,
            "TextField.keyBindings", null,
            
            "TextPane.focusInputMap", multilineInputMap,
            "TextPane.keyBindings", null,
            
            "Tree.focusInputMap",
            new UIDefaults.LazyInputMap(new Object[] {
                "meta C", "copy",
                "meta V", "paste",
                "meta X", "cut",
                "COPY", "copy",
                "PASTE", "paste",
                "CUT", "cut",
                "UP", "selectPrevious",
                "KP_UP", "selectPrevious",
                "shift UP", "selectPreviousExtendSelection",
                "shift KP_UP", "selectPreviousExtendSelection",
                "DOWN", "selectNext",
                "KP_DOWN", "selectNext",
                "shift DOWN", "selectNextExtendSelection",
                "shift KP_DOWN", "selectNextExtendSelection",
                "RIGHT", "selectChild",
                "KP_RIGHT", "selectChild",
                "LEFT", "selectParent",
                "KP_LEFT", "selectParent",
                "PAGE_UP", "scrollUpChangeSelection",
                "shift PAGE_UP", "scrollUpExtendSelection",
                "PAGE_DOWN", "scrollDownChangeSelection",
                "shift PAGE_DOWN", "scrollDownExtendSelection",
                "HOME", "selectFirst",
                "alt UP", "selectFirst",
                "shift HOME", "selectFirstExtendSelection",
                "END", "selectLast",
                "alt DOWN", "selectLast",
                "shift END", "selectLastExtendSelection",
                "F2", "startEditing",
                "meta A", "selectAll",
                // "ctrl SLASH", "selectAll",
                "meta shift A", "clearSelection",
                "ctrl SPACE", "toggleSelectionPreserveAnchor",
                "shift SPACE", "extendSelection",
                "ctrl HOME", "selectFirstChangeLead",
                "ctrl END", "selectLastChangeLead",
                "ctrl UP", "selectPreviousChangeLead",
                "ctrl KP_UP", "selectPreviousChangeLead",
                "ctrl DOWN", "selectNextChangeLead",
                "ctrl KP_DOWN", "selectNextChangeLead",
                "ctrl PAGE_DOWN", "scrollDownChangeLead",
                "ctrl shift PAGE_DOWN", "scrollDownExtendSelection",
                "ctrl PAGE_UP", "scrollUpChangeLead",
                "ctrl shift PAGE_UP", "scrollUpExtendSelection",
                "ctrl LEFT", "scrollLeft",
                "ctrl KP_LEFT", "scrollLeft",
                "ctrl RIGHT", "scrollRight",
                "ctrl KP_RIGHT", "scrollRight",
                "SPACE", "toggleSelectionPreserveAnchor",
            }),
            
        };
        
        table.putDefaults(objects);
    }
    
    /**
     * Returns the base font for which system fonts are derived.
     * This is Lucida Grande, Plain, 13.
     */
    protected Font getBaseSystemFont() {
        return new Font("Lucida Grande", Font.PLAIN, 13);
    }
    
    protected void initFontDefaults(UIDefaults table) {
        Font baseSystemFont = getBaseSystemFont();
        
        // *** Shared Fonts
        // Some of the following comments have been taken from Apples Human Interface
        // Guidelines, Revision 2004-12-02.
        float fourteen = 14f;
        float thirteen = 13f;
        float twelve = 12f;
        float eleven = 11f;
        float ten = 11f;
        float nine = 9f;
        int fontPlain = Font.PLAIN;
        int fontBold = Font.BOLD;
        // The system font (Lucida Grande Regular 13 pt) is used for text in
        // menus, dialogs, and full-size controls.
        Object systemFont = new UIDefaults.ProxyLazyValue(
                "javax.swing.plaf.FontUIResource",
                null,
                new Object[] {baseSystemFont.deriveFont(fontPlain, thirteen)});
        // Use the emphasized system font (Lucida Grande Bold 13 pt) sparingly. It
        // is used for the message text in alerts.
        Object emphasizedSystemFont = new UIDefaults.ProxyLazyValue(
                "javax.swing.plaf.FontUIResource",
                null,
                new Object[] {baseSystemFont.deriveFont(fontBold, thirteen)});
        // The small system font (Lucida Grande Regular 11 pt) is used for
        // informative text in alerts. It is also the default font for column
        // headings in lists, for help tags, and for small controls. You can also
        // use it to provide additional information about settings in various
        // windows, such as the QuickTime pane in System Preferences.
        Object smallSystemFont =  new UIDefaults.ProxyLazyValue(
                "javax.swing.plaf.FontUIResource",
                null,
                new Object[] {baseSystemFont.deriveFont(fontPlain, eleven)});
        // Use the emphasized small system font (Lucida Grande Bold 11 pt)
        // sparingly. You might use it to title a group of settings that appear
        // without a group box, or for brief informative text below a text field.
        Object emphasizedSmallSystemFont = new UIDefaults.ProxyLazyValue(
                "javax.swing.plaf.FontUIResource",
                null,
                new Object[] {baseSystemFont.deriveFont(fontBold, eleven)});
        // The mini system font (Lucida Grande Regular 9 pt) is used for mini
        // controls. It can also be used for utility window labels and text.
        Object miniSystemFont = new UIDefaults.ProxyLazyValue(
                "javax.swing.plaf.FontUIResource",
                null,
                new Object[] {baseSystemFont.deriveFont(fontPlain, nine)});
        // An emphasized mini system font (Lucida Grande Bold 9 pt) is available for
        // cases in which the emphasized small system font is too large.
        Object emphasizedMiniSystemFont = new UIDefaults.ProxyLazyValue(
                "javax.swing.plaf.FontUIResource",
                null,
                new Object[] {baseSystemFont.deriveFont(fontBold, nine)});
        // If your application creates text documents, use the application font
        // (Lucida Grande Regular 13 pt) as the default font for user-created
        // content.
        Object applicationFont = baseSystemFont;
        // The label font (Lucida Grande Regular 10 pt) is used for the labels on
        // toolbar buttons and to label tick marks on full-size sliders. You should
        // rarely need to use this font. For an example of this font used to label a
        // slider controlBackground, see the Spoken User Interface pane in Speech preferences.
        Object labelFont = new UIDefaults.ProxyLazyValue(
                "javax.swing.plaf.FontUIResource",
                null,
                new Object[] {baseSystemFont.deriveFont(fontPlain, ten)});
        // Use the view font (Lucida Grande Regular 12pt) as the default font of
        // text in lists and tables.
        Object viewFont = new UIDefaults.ProxyLazyValue(
                "javax.swing.plaf.FontUIResource",
                null,
                new Object[] {baseSystemFont.deriveFont(fontPlain, twelve)});
        // The menu font (Lucida Grande Regular 14 pt) is used for text in menus and
        // window title bars.
        Object menuFont = new UIDefaults.ProxyLazyValue(
                "javax.swing.plaf.FontUIResource",
                null,
                new Object[] {baseSystemFont.deriveFont(fontPlain, fourteen)});
        
        // Set font sizes according to default size style.
        if (QuaquaManager.getProperty("Quaqua.sizeStyle","regular").equals("small")) {
            viewFont = smallSystemFont;
            systemFont = smallSystemFont;
            emphasizedSystemFont = emphasizedSmallSystemFont;
            //smallSystemFont = smallSystemFont;
            menuFont = smallSystemFont;
            applicationFont = smallSystemFont;
            labelFont = labelFont;
        }
        
        Object[] objects = {
            // FIXME - These should be considered as private UIManager settings
            "SystemFont", systemFont,
            "EmphasizedSystemFont", emphasizedSystemFont,
            "SmallSystemFont", smallSystemFont,
            "EmphasizedSmallSystemFont", emphasizedSmallSystemFont,
            "MiniSystemFont", miniSystemFont,
            "EmphasizedMiniSystemFont", miniSystemFont,
            "ApplicationFont", applicationFont,
            "LabelFont", labelFont,
            "ViewFont", viewFont,
            "MenuFont", menuFont,
            
            
            // This must be set to false to make default button on option panes
            // work as expected when running Java 1.5.
            "Button.defaultButtonFollowsFocus", Boolean.FALSE,
            "Browser.font", viewFont,
            "Button.font", systemFont,
            "Button.smallFont", smallSystemFont, // Maybe we should use Component.smallFont instead?
            
            "CheckBox.font", systemFont,
            "CheckBoxMenuItem.acceleratorFont", menuFont,
            "CheckBoxMenuItem.font", menuFont,
            "ColorChooser.font", smallSystemFont,
            "ColorChooser.crayonsFont", systemFont,
            "ComboBox.font", systemFont,
            
            "EditorPane.font", applicationFont,
            
            "FormattedTextField.font", applicationFont,
            "FileChooser.previewLabelFont", smallSystemFont,
            "FileChooser.previewValueFont", smallSystemFont,
            
            "IconButton.font", smallSystemFont, // ??
            "InternalFrame.optionDialogTitleFont", menuFont,
            "InternalFrame.titleFont", menuFont,
            
            "Label.font", systemFont,
            
            "List.font", viewFont,
            "List.focusCellHighlightBorder",
            new UIDefaults.ProxyLazyValue(
                    "javax.swing.plaf.BorderUIResource$LineBorderUIResource",
                    new Object[] { table.get("listHighlightBorder") }
            ),
            
            "Menu.acceleratorFont", menuFont,
            "Menu.font", menuFont,
            "MenuBar.font", menuFont,
            "MenuItem.acceleratorFont", menuFont,
            "MenuItem.font", menuFont,
            
            "OptionPane.buttonFont", systemFont,
            "OptionPane.font", systemFont,
            // We use a plain font for HTML messages to make the examples in the
            // Java Look and Feel Guidelines work.
            "OptionPane.messageFont", emphasizedSystemFont,
            "OptionPane.htmlMessageFont", systemFont,
            
            "Panel.font", systemFont,
            "PasswordField.font", applicationFont,
            "PopupMenu.font", menuFont,
            "ProgressBar.font", systemFont,
            
            "RadioButton.font", systemFont,
            "RadioButtonMenuItem.acceleratorFont", menuFont,
            "RadioButtonMenuItem.font", menuFont,
            
            "RootPane.font", systemFont,
            
            "ScrollBar.font", systemFont,
            "ScrollPane.font", systemFont,
            "Slider.font", systemFont,
            "Slider.labelFont", labelFont,
            "Spinner.font", systemFont,
            
            "TabbedPane.font", systemFont,
            "TabbedPane.smallFont", smallSystemFont, // ??
            "TabbedPane.wrap.font", systemFont,
            "TabbedPane.wrap.smallFont", smallSystemFont, // ??
            "TabbedPane.scroll.font", systemFont,
            "TabbedPane.scroll.smallFont", smallSystemFont, // ??
            "Table.font", viewFont,
            "TableHeader.font", smallSystemFont,
            
            "TextArea.font", applicationFont,
            "TextField.font", applicationFont,
            "TextPane.font", applicationFont,
            
            "TitledBorder.font", smallSystemFont,
            
            "ToggleButton.font", systemFont,
            "ToolBar.font", miniSystemFont,
            "ToolBar.titleFont", miniSystemFont,
            "ToolTip.font", smallSystemFont,
            "Tree.font", viewFont,
            "Viewport.font", systemFont,
        };
        
        table.putDefaults(objects);
    }
    
    /**
     * The defaults initialized here are common to all Quaqua Look and Feels.
     */
    protected void initGeneralDefaults(UIDefaults table) {
        String systemFontName = getBaseSystemFont().getName();
        Boolean isRequestFocusEnabled = new Boolean(QuaquaManager.getProperty("Quaqua.requestFocusEnabled", "false").equals("true"));
        
        Object dialogBorder = new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaBorders$DialogBorder");
        
        Object questionDialogBorder = new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaBorders$QuestionDialogBorder");
        // Shared colors
        ColorUIResource listSelectionBorderColor = (ColorUIResource) table.get("listHighlightBorder");
        Color menuBackground = (Color) table.get("menu");
        
        // Shared numbers
        Integer zero = new Integer(0);
        Integer one = new Integer(1);
        Integer two = new Integer(2);
        Integer three = new Integer(3);
        Integer four = new Integer(4);
        Integer ten = new Integer(10);
        
        // Set visual margin.
        int[] values = QuaquaManager.getProperty("Quaqua.visualMargin", new int[] { 3, 3, 3, 3});
        InsetsUIResource visualMargin = new InsetsUIResource(values[0], values[1], values[2], values[3]);
        
        // Opaqueness
        Boolean opaque = new Boolean(QuaquaManager.getProperty("Quaqua.opaque", "false").equals("true"));
        
        // Autovalidation
        Boolean autovalidate = new Boolean(QuaquaManager.getProperty("Quaqua.FileChooser.autovalidate", "true").equals("true"));
        
        // Popup menus
        Object textComponentPopupHandler = new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.QuaquaTextComponentPopupHandler");
        
        // TextField auto selection
        Boolean autoselect = new Boolean(QuaquaManager.getProperty("Quaqua.TextComponent.autoSelect","true").
                equals("true"));
        // *** Shared Borders
        Object scrollPaneBorder = new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaScrollPaneBorder$UIResource",
                new Object[] { commonDir+"ScrollPane.borders.png", commonDir+"TextField.borders.png" }
        );
        Object textFieldBorder = new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaTextFieldBorder$UIResource",
                new Object[] { commonDir+"TextField.borders.png",
                commonDir+"TextField.searchBorders.png",
                commonDir+"TextField.small.searchBorders.png",
        }
        );
        
        // Enforce visual margin
        // Set this to true, to workaround Matisse issue #
        //
        // Enforce margin is used to workaround a workaround in the Matisse
        // design tool for NetBeans. Matisse removes borders from some
        // components in order to workaround some ugliness in the look
        // and feels that ship with the J2SE.
        Boolean enforceVisualMargin = new Boolean(QuaquaManager.getProperty("Quaqua.enforceVisualMargin", "false").equals("true"));
        
        Object[] objects = {
            "Browser.expandedIcon", new UIDefaults.ProxyLazyValue(
                    "ch.randelshofer.quaqua.QuaquaIconFactory","createIcon",
                    new Object[] {commonDir+"Browser.disclosureIcons.png", four, Boolean.TRUE, zero}
            ),
            "Browser.expandingIcon", new UIDefaults.ProxyLazyValue(
                    "ch.randelshofer.quaqua.QuaquaIconFactory","createIcon",
                    new Object[] {commonDir+"Browser.disclosureIcons.png", four, Boolean.TRUE, one}
            ),
            "Browser.selectedExpandedIcon", new UIDefaults.ProxyLazyValue(
                    "ch.randelshofer.quaqua.QuaquaIconFactory","createIcon",
                    new Object[] {commonDir+"Browser.disclosureIcons.png", four, Boolean.TRUE, two}
            ),
            "Browser.selectedExpandingIcon", new UIDefaults.ProxyLazyValue(
                    "ch.randelshofer.quaqua.QuaquaIconFactory","createIcon",
                    new Object[] {commonDir+"Browser.disclosureIcons.png", four, Boolean.TRUE, three}
            ),
            
            
            //"Button.actionMap", ...,
            "Button.border", new UIDefaults.ProxyLazyValue(
                    "ch.randelshofer.quaqua.QuaquaBorderFactory","createButtonBorder", new Object[] {"push"}
            ),
            //"Button.border", new BorderUIResource.LineBorderUIResource(Color.black),
            "Button.defaultButtonFollowsFocus", Boolean.FALSE,
            // The values for this margin are ignored. We dynamically compute a margin
            // for the various button styles that we support, if we encounter a
            // a margin that is an instanceof a UIResource.
            "Button.margin", new InsetsUIResource(0,0,0,0),
            "Button.opaque", opaque,
            "Button.textIconGap", four,
            "Button.textShiftOffset", zero,
            "Button.helpIcon", makeOverlaidButtonStateIcon(
                    commonDir+"Button.helpIcons.png", 10,
                    commonDir+"Button.helpFocusRings.png", 2,
                    new Rectangle(0,0,25,25)
                    ),
            "Button.requestFocusEnabled", isRequestFocusEnabled,
            
            //"CheckBox.background", ...,
            "CheckBox.border", new VisualMargin(0,0,0,0),
            "CheckBox.icon", makeOverlaidButtonStateIcon(
                    commonDir+"CheckBox.icons.png", 10,
                    commonDir+"CheckBox.focusRings.png", 2,
                    new Rectangle(2,2,14,14)
                    ),
            "CheckBox.margin", new InsetsUIResource(0, 0, 0, 0),
            "CheckBox.opaque", opaque,
            //"CheckBox.select", ...,
            "CheckBox.smallIcon", makeOverlaidButtonStateIcon(
                    commonDir+"CheckBox.small.icons.png", 10,
                    commonDir+"CheckBox.small.focusRings.png", 2,
                    new Rectangle(2,2,12,12)
                    ),
            "CheckBox.textIconGap", four,
            "CheckBox.textShiftOffset", zero,
            "CheckBox.requestFocusEnabled", isRequestFocusEnabled,
            
            "CheckBoxMenuItem.borderPainted", Boolean.TRUE,
            
            // class names of default choosers
            "ColorChooser.defaultChoosers", new String[] {
                "ch.randelshofer.quaqua.colorchooser.ColorWheelChooser",
                "ch.randelshofer.quaqua.colorchooser.ColorSlidersChooser",
                "ch.randelshofer.quaqua.colorchooser.ColorPalettesChooser",
                "ch.randelshofer.quaqua.colorchooser.SwatchesChooser",
                "ch.randelshofer.quaqua.colorchooser.CrayonsChooser",
                "ch.randelshofer.quaqua.colorchooser.Quaqua15ColorPicker",
            },
            //"ColorChooser.swatchesDefaultRecentColor", ...,
            //"ColorChooser.swatchesRecentSwatchSize", ...,
            "ColorChooser.swatchesSwatchSize", new DimensionUIResource(5,5),
            "ColorChooser.resetMnemonic", new Integer(-1),
            "ColorChooser.crayonsImage", makeImage(commonDir+"ColorChooser.crayons.png"),
            "ColorChooser.textSliderGap", zero,
            "ColorChooser.colorPalettesIcon", makeButtonStateIcon(commonDir+"ColorChooser.colorPalettesIcons.png",3),
            "ColorChooser.colorSlidersIcon", makeButtonStateIcon(commonDir+"ColorChooser.colorSlidersIcons.png",3),
            "ColorChooser.colorSwatchesIcon", makeButtonStateIcon(commonDir+"ColorChooser.colorSwatchesIcons.png",3),
            "ColorChooser.colorWheelIcon", makeButtonStateIcon(commonDir+"ColorChooser.colorWheelIcons.png",3),
            "ColorChooser.crayonsIcon", makeButtonStateIcon(commonDir+"ColorChooser.crayonsIcons.png",3),
            "ColorChooser.imagePalettesIcon", makeButtonStateIcon(commonDir+"ColorChooser.imagePalettesIcons.png",3),
            
            // Icon of the color picker tool
            "ColorChooser.colorPickerIcon", makeIcon(getClass(), commonDir+"ColorChooser.colorPickerIcon.png"),
            // Magnifying glass used as the cursor image
            "ColorChooser.colorPickerMagnifier", makeBufferedImage(commonDir+"ColorChooser.colorPickerMagnifier.png"),
            // Hot spot of the magnifier cursor
            "ColorChooser.colorPickerHotSpot", new UIDefaults.ProxyLazyValue("java.awt.Point", new Object[] { new Integer(29), new Integer(29) }),
            // Pick point relative to hot spot
            "ColorChooser.colorPickerPickOffset", new UIDefaults.ProxyLazyValue("java.awt.Point", new Object[] { new Integer(-13), new Integer(-13) }),
            // Rectangle used for drawing the mask of the magnifying glass
            "ColorChooser.colorPickerGlassRect", new UIDefaults.ProxyLazyValue("java.awt.Rectangle", new Object[] { new Integer(2), new Integer(2), new Integer(29), new Integer(29) }),
            // Capture rectangle. Width and height must be equal sized and must be odd.
            // The position of the capture rectangle is relative to the hot spot.
            "ColorChooser.colorPickerCaptureRect", new UIDefaults.ProxyLazyValue("java.awt.Rectangle", new Object[] { new Integer(-15), new Integer(-15), new Integer(5), new Integer(5) }),
            // Zoomed (magnified) capture image. Width and height must be a multiple of the capture rectangles size.
            "ColorChooser.colorPickerZoomRect", new UIDefaults.ProxyLazyValue("java.awt.Rectangle", new Object[] { new Integer(4), new Integer(4), new Integer(25), new Integer(25) }),
            
            "ComboBox.border", new QuaquaComboBoxVisualMargin(2,2,2,2),
            "ComboBox.dropDownIcon", makeButtonStateIcon(commonDir+"ComboBox.dropDownIcons.png", 6),
            "ComboBox.opaque", opaque,
            "ComboBox.popupIcon", makeButtonStateIcon(commonDir+"ComboBox.popupIcons.png", 6),
            "ComboBox.smallPopupIcon", makeButtonStateIcon(commonDir+"ComboBox.small.popupIcons.png", 6),
            "ComboBox.smallDropDownIcon", makeButtonStateIcon(commonDir+"ComboBox.small.dropDownIcons.png", 6),
            //"ComboBox.timeFactor", ...
            "ComboBox.maximumRowCount", new Integer(8),
            "ComboBox.requestFocusEnabled", isRequestFocusEnabled,
            
            // Set this to Boolean.TRUE to get the same preferred height for
            // non-editable combo boxes and editable-combo boxes.
            "ComboBox.harmonizePreferredHeight", Boolean.FALSE,
            
            // The values for this margin are ignored. We dynamically compute a margin
            // for the various button styles that we support, if we encounter a
            // a margin that is an instanceof a UIResource.
            "ComboBoxButton.margin", new InsetsUIResource(0,0,0,0),
            
            // Setting this to true makes the combo box UI change the foreground
            // color of the editor to the the foreground color of the JComboBox.
            // True is needed for rendering of combo boxes in JTables.
            "ComboBox.changeEditorForeground", Boolean.TRUE,
            
            // The visual margin is used to allow each component having room
            // for a cast shadow and a focus ring, and still supporting a
            // consistent visual arrangement of all components aligned to their
            // visualy perceived lines.
            // FIXME: This should be either a global system property
            // "Quaqua.visualMargin" or a per-component property e.g.
            // "Button.visualMargin".
            "Component.visualMargin", visualMargin,
            
            // Set this to true, to workaround Matisse issue #
            // Enforce margin is used to workaround a workaround in the Matisse
            // design tool for NetBeans. Matisse removes borders from some
            // components in order to workaround some ugliness in the look
            // and feels that ship with the J2SE.
            "CheckBox.enforceVisualMargin", enforceVisualMargin,
            "RadioButton.enforceVisualMargin", enforceVisualMargin,
            
            //"DesktopIcon.border", ...
            
            //"EditorPane.border", ...
            //"EditorPane.caretBlinkRate", ...
            "EditorPane.margin", new InsetsUIResource(1,3,1,3),
            "EditorPane.popupHandler", textComponentPopupHandler,
            
            "FileChooser.homeFolderIcon", makeIcon(getClass(), commonDir+"FileChooser.homeFolderIcon.png"),
            "FileChooser.autovalidate", autovalidate,
            "FileChooser.previewLabelForeground", new ColorUIResource(0x000000),
            "FileChooser.previewValueForeground", new ColorUIResource(0x000000),
            "FileChooser.splitPaneDividerSize", four,
            "FileChooser.previewLabelInsets",new InsetsUIResource(1,0,0,1),
            "FileChooser.previewLabelDelimiter",":",
            "FileChooser.speed", new Boolean(QuaquaManager.getProperty("Quaqua.FileChooser.speed") != null && QuaquaManager.getProperty("Quaqua.FileChooser.speed").equals("true")),
            "FileView.computerIcon", makeIcon(getClass(), commonDir+"FileView.computerIcon.png"),
            "FileView.fileIcon", makeIcon(getClass(), commonDir+"FileView.fileIcon.png"),
            "FileView.directoryIcon", makeIcon(getClass(), commonDir+"FileView.directoryIcon.png"),
            "FileChooser.orderByType", new Boolean(QuaquaManager.getProperty("Quaqua.FileChooser.speed") != null && QuaquaManager.getProperty("Quaqua.FileChooser.orderByType").equals("true")),
            "FileChooser.browserFocusCellHighlightBorder",
            new UIDefaults.ProxyLazyValue(
                    "javax.swing.plaf.BorderUIResource$LineBorderUIResource",
                    new Object[] { table.get("listHighlightBorder") }
            ),
            "FileChooser.browserCellBorder",
            new UIDefaults.ProxyLazyValue(
                    "javax.swing.plaf.BorderUIResource$EmptyBorderUIResource",
                    new Object[] { new Insets(1,1,1,1) }
            ),
            
            "FormattedTextField.border", textFieldBorder,
            "FormattedTextField.opaque", opaque,
            "FormattedTextField.popupHandler", textComponentPopupHandler,
            "FormattedTextField.autoSelect", autoselect,
            
            "Label.border", new VisualMargin(0,0,0,0),
            "Label.opaque", opaque,
            
            "List.cellRenderer", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.QuaquaDefaultListCellRenderer"),
            
            "Menu.borderPainted", Boolean.TRUE,
            "MenuItem.borderPainted", Boolean.TRUE,
            
            // The negative values are used to take account for the visual margin
            "OptionPane.border", new BorderUIResource.EmptyBorderUIResource(15-3,24-3,20-3,24-3),
            "OptionPane.messageAreaBorder", new BorderUIResource.EmptyBorderUIResource(0,0,0,0),
            "OptionPane.buttonAreaBorder", new BorderUIResource.EmptyBorderUIResource(16-3,0,0,0),
            "OptionPane.errorIcon", new UIDefaults.ProxyLazyValue(
                    "ch.randelshofer.quaqua.QuaquaIconFactory","createOptionPaneIcon", new Object[]{new Integer(JOptionPane.ERROR_MESSAGE)}),
            "OptionPane.errorIconResource", "/ch/randelshofer/quaqua/images/OptionPane.errorIcon.png",
            "OptionPane.informationIcon",  new UIDefaults.ProxyLazyValue(
                    "ch.randelshofer.quaqua.QuaquaIconFactory","createOptionPaneIcon", new Object[]{new Integer(JOptionPane.INFORMATION_MESSAGE)}),
            "OptionPane.questionIcon", new UIDefaults.ProxyLazyValue(
                    "ch.randelshofer.quaqua.QuaquaIconFactory","createOptionPaneIcon", new Object[]{new Integer(JOptionPane.QUESTION_MESSAGE)}),
            "OptionPane.warningIcon", new UIDefaults.ProxyLazyValue(
                    "ch.randelshofer.quaqua.QuaquaIconFactory","createOptionPaneIcon", new Object[]{new Integer(JOptionPane.WARNING_MESSAGE)}),
            "OptionPane.warningIconResource", "/ch/randelshofer/quaqua/images/OptionPane.warningIcon.png",
            "OptionPane.css", "<head>"+
                    "<style type=\"text/css\">"+
                    "b { font: 13pt \""+systemFontName+"\" }"+
                    "p { font: 11pt \""+systemFontName+"\"; margin-top: 8px }"+
                    "</style>"+
                    "</head>",
            "OptionPane.messageLabelWidth", new Integer(360),
            "OptionPane.maxCharactersPerLineCount", new Integer(60),
            
            "Panel.opaque", opaque,
            
            "PopupMenu.enableHeavyWeightPopup", Boolean.TRUE,
            
            "PasswordField.border", textFieldBorder,
            "PasswordField.opaque", opaque,
            "PasswordField.popupHandler", textComponentPopupHandler,
            "PasswordField.autoSelect", autoselect,
            
            "RadioButton.border", new VisualMargin(0,0,0,0),
            // The values for this margin are ignored. We dynamically compute a margin
            // for the various button styles that we support, if we encounter a
            // a margin that is an instanceof a UIResource.
            "RadioButton.margin", new InsetsUIResource(0, 0, 0, 0),
            "RadioButton.icon", makeOverlaidButtonStateIcon(
                    commonDir+"RadioButton.icons.png", 10,
                    commonDir+"RadioButton.focusRing.png", 1,
                    new Rectangle(2,2,14,15)
                    ),
            "RadioButton.smallIcon", makeOverlaidButtonStateIcon(
                    commonDir+"RadioButton.small.icons.png", 10,
                    commonDir+"RadioButton.small.focusRing.png", 1,
                    new Rectangle(2,1,12,13)
                    ),
            "RadioButton.opaque", opaque,
            "RadioButton.textIconGap", four,
            "RadioButton.textShiftOffset", zero,
            "RadioButton.requestFocusEnabled", isRequestFocusEnabled,
            
            "RadioButtonMenuItem.borderPainted", Boolean.TRUE,
            
            // RootPane
            "RootPane.opaque", Boolean.TRUE,
            "RootPane.frameBorder", new UIDefaults.ProxyLazyValue(
                    "ch.randelshofer.quaqua.QuaquaBorders$FrameBorder"),
            "RootPane.plainDialogBorder", dialogBorder,
            "RootPane.informationDialogBorder", dialogBorder,
            "RootPane.errorDialogBorder", new UIDefaults.ProxyLazyValue(
                    "ch.randelshofer.quaqua.QuaquaBorders$ErrorDialogBorder"),
            "RootPane.colorChooserDialogBorder", questionDialogBorder,
            "RootPane.fileChooserDialogBorder", questionDialogBorder,
            "RootPane.questionDialogBorder", questionDialogBorder,
            "RootPane.warningDialogBorder", new UIDefaults.ProxyLazyValue(
                    "ch.randelshofer.quaqua.QuaquaBorders$WarningDialogBorder"),
            // These bindings are only enabled when there is a default
            // button set on the rootpane.
            "RootPane.defaultButtonWindowKeyBindings", new Object[] {
                "ENTER", "press",
                "released ENTER", "release",
                "ctrl ENTER", "press",
                "ctrl released ENTER", "release"
            },
            // Setting this property to null disables snapping
            // Note: snapping is only in effect for look and feel decorated
            // windows
            "RootPane.windowSnapDistance", new Integer(10),
            
            // Default value for "apple.awt.draggableWindowBackground"
            "RootPane.draggableWindowBackground", Boolean.FALSE,
            // Default value for "apple.awt.windowShadow"
            "RootPane.windowShadow", Boolean.TRUE,
            
            "ScrollBar.placeButtonsTogether", new UIDefaults.LazyValue() {
                public Object createValue(UIDefaults table) {
                    return new Boolean(Preferences.getString("AppleScrollBarVariant").equals("DoubleMax"));
                }
            },
            "ScrollBar.supportsAbsolutePositioning", new UIDefaults.LazyValue() {
                public Object createValue(UIDefaults table) {
                    return new Boolean(Preferences.getString("AppleScrollerPagingBehavior").equals("true"));
                }
            },
            
            "ScrollBar.minimumThumbSize", new DimensionUIResource(24,24),
            "ScrollBar.smallMinimumThumbSize", new DimensionUIResource(18,18),
            "ScrollBar.maximumThumbSize", new DimensionUIResource(Integer.MAX_VALUE,Integer.MAX_VALUE),
            
            "ScrollBar.hThumbBody", makeBufferedImage(commonDir+"ScrollBar.hThumbBody.png"),
            "ScrollBar.hThumbLeft", makeIcons(commonDir+"ScrollBar.hThumbLeft.png", 5, false),
            "ScrollBar.hThumbRight", makeIcons(commonDir+"ScrollBar.hThumbRight.png", 5, false),
            "ScrollBar.hTrack", makeImageBevelBorder(commonDir+"ScrollBar.hTrack.png", new Insets(15,0,0,0)),
            "ScrollBar.ihThumb", makeImageBevelBorder(commonDir+"ScrollBar.ihThumb.png", new Insets(15,11,0,11)),
            "ScrollBar.sep.hButtons", makeImageBevelBorders(commonDir+"ScrollBar.sep.hButtons.png", new Insets(15,28,0,28), 4, false),
            "ScrollBar.tog.hButtons", makeImageBevelBorders(commonDir+"ScrollBar.tog.hButtons.png", new Insets(15,18,0,44), 4, false),
            
            "ScrollBar.vThumbBody", makeBufferedImage(commonDir+"ScrollBar.vThumbBody.png"),
            "ScrollBar.vThumbTop", makeIcons(commonDir+"ScrollBar.vThumbTop.png", 5, true),
            "ScrollBar.vThumbBottom", makeIcons(commonDir+"ScrollBar.vThumbBottom.png", 5, true),
            "ScrollBar.vTrack", makeImageBevelBorder(commonDir+"ScrollBar.vTrack.png", new Insets(0,15,0,0)),
            "ScrollBar.ivThumb", makeImageBevelBorder(commonDir+"ScrollBar.ivThumb.png", new Insets(11,15,11,0)),
            "ScrollBar.sep.vButtons", makeImageBevelBorders(commonDir+"ScrollBar.sep.vButtons.png", new Insets(28,15,28,0), 4, true),
            "ScrollBar.tog.vButtons", makeImageBevelBorders(commonDir+"ScrollBar.tog.vButtons.png", new Insets(18,15,44,0), 4, true),
            
            "ScrollBar.small.hThumbBody", makeBufferedImage(commonDir+"ScrollBar.small.hThumbBody.png"),
            "ScrollBar.small.hThumbLeft", makeIcons(commonDir+"ScrollBar.small.hThumbLeft.png", 5, false),
            "ScrollBar.small.hThumbRight", makeIcons(commonDir+"ScrollBar.small.hThumbRight.png", 5, false),
            "ScrollBar.small.hTrack", makeImageBevelBorder(commonDir+"ScrollBar.small.hTrack.png", new Insets(11,0,0,0)),
            "ScrollBar.small.ihThumb", makeImageBevelBorder(commonDir+"ScrollBar.small.ihThumb.png", new Insets(11,8,0,8)),
            "ScrollBar.smallSep.hButtons", makeImageBevelBorders(commonDir+"ScrollBar.smallSep.hButtons.png", new Insets(11,21,0,21), 4, false),
            "ScrollBar.smallTog.hButtons", makeImageBevelBorders(commonDir+"ScrollBar.smallTog.hButtons.png", new Insets(11,14,0,34), 4, false),
            
            "ScrollBar.small.vThumbBody", makeBufferedImage(commonDir+"ScrollBar.small.vThumbBody.png"),
            "ScrollBar.small.vThumbTop", makeIcons(commonDir+"ScrollBar.small.vThumbTop.png", 5, true),
            "ScrollBar.small.vThumbBottom", makeIcons(commonDir+"ScrollBar.small.vThumbBottom.png", 5, true),
            "ScrollBar.small.vTrack", makeImageBevelBorder(commonDir+"ScrollBar.small.vTrack.png", new Insets(0,11,0,0)),
            "ScrollBar.small.ivThumb", makeImageBevelBorder(commonDir+"ScrollBar.small.ivThumb.png", new Insets(8,11,8,0)),
            "ScrollBar.smallSep.vButtons", makeImageBevelBorders(commonDir+"ScrollBar.smallSep.vButtons.png", new Insets(21,11,21,0), 4, true),
            "ScrollBar.smallTog.vButtons", makeImageBevelBorders(commonDir+"ScrollBar.smallTog.vButtons.png", new Insets(14,11,34,0), 4, true),
            "ScrollBar.focusable", Boolean.FALSE,
            
            "ScrollPane.border", scrollPaneBorder,
            "ScrollPane.requesFocusEnabled", Boolean.FALSE,
            "ScrollPane.focusable", Boolean.FALSE,
            "ScrollPane.opaque", opaque,
            
            "Separator.border", new VisualMargin(),
            
            "Sheet.showAsSheet", Boolean.TRUE,
            
            "Slider.roundThumb", makeSliderThumbIcon(commonDir+"Slider.roundThumbs.png"),
            "Slider.roundThumb.small", makeSliderThumbIcon(commonDir+"Slider.roundThumbs.small.png"),
            "Slider.southThumb", makeSliderThumbIcon(commonDir+"Slider.southThumbs.png"),
            "Slider.eastThumb", makeSliderThumbIcon(commonDir+"Slider.eastThumbs.png"),
            "Slider.northThumb", makeSliderThumbIcon(commonDir+"Slider.northThumbs.png"),
            "Slider.westThumb", makeSliderThumbIcon(commonDir+"Slider.westThumbs.png"),
            "Slider.eastThumb.small", makeSliderThumbIcon(commonDir+"Slider.eastThumbs.small.png"),
            "Slider.southThumb.small", makeSliderThumbIcon(commonDir+"Slider.southThumbs.small.png"),
            "Slider.northThumb.small", makeSliderThumbIcon(commonDir+"Slider.northThumbs.small.png"),
            "Slider.westThumb.small", makeSliderThumbIcon(commonDir+"Slider.westThumbs.small.png"),
            "Slider.opaque", opaque,
            "Slider.requestFocusEnabled", isRequestFocusEnabled,
            "Slider.tickColor", new ColorUIResource(0x808080),
            "Slider.focusInsets", new Insets(0, 0, 0, 0),
            "Slider.verticalTracks", makeImageBevelBorders(commonDir+"Slider.verticalTracks.png", new Insets(4,5,4,0),2,true),
            "Slider.horizontalTracks", makeImageBevelBorders(commonDir+"Slider.horizontalTracks.png", new Insets(5,4,0,4),2,false),
            
            "Spinner.arrowButtonBorder", null,
            "Spinner.arrowButtonInsets", null,
            "Spinner.border", null,
            "Spinner.editorBorderPainted", Boolean.TRUE,
            "Spinner.opaque", opaque,
            "Spinner.north", makeButtonStateIcon(commonDir+"Spinner.north.png", 10),
            "Spinner.south", makeButtonStateIcon(commonDir+"Spinner.south.png", 10),
            "Spinner.smallNorth", makeButtonStateIcon(commonDir+"Spinner.small.north.png", 10),
            "Spinner.smallSouth", makeButtonStateIcon(commonDir+"Spinner.small.south.png", 10),
            
            //"SplitPane.actionMap", ???,
            //"SplitPane.ancestorInputMap", ???,
            "SplitPane.opaque", opaque,
            "SplitPane.border", null,
            "SplitPane.dividerSize", ten,
            "SplitPane.thumbDimple", makeIcon(getClass(), commonDir+"SplitPane.thumbDimple.png"),
            "SplitPane.barDimple", makeIcon(getClass(), commonDir+"SplitPane.barDimple.png"),
            "SplitPane.hBar", makeImageBevelBorder(commonDir+"SplitPane.hBar.png", new Insets(4,0,5,0), true),
            "SplitPane.vBar", makeImageBevelBorder(commonDir+"SplitPane.vBar.png", new Insets(0,4,0,5), true),
            "SplitPane.upArrow", makeIcon(getClass(), commonDir+"SplitPane.upArrow.png"),
            "SplitPane.downArrow", makeIcon(getClass(), commonDir+"SplitPane.downArrow.png"),
            "SplitPane.rightArrow", makeIcon(getClass(), commonDir+"SplitPane.rightArrow.png"),
            "SplitPane.leftArrow", makeIcon(getClass(), commonDir+"SplitPane.leftArrow.png"),
            "SplitPane.focusable", Boolean.FALSE,
            "SplitPane.requestFocusEnabled", Boolean.FALSE,
            
            
            "SplitPaneDivider.border", null,
            "SplitPaneDivider.focusable", Boolean.FALSE,
            "SplitPaneDivider.requestFocusEnabled", Boolean.FALSE,
            
            "TabbedPane.opaque", opaque,
            "TabbedPane.wrap.opaque", opaque,
            "TabbedPane.scroll.opaque", opaque,
            "TabbedPane.requestFocusEnabled", isRequestFocusEnabled,
            "TabbedPane.textIconGap", four,
            "TabbedPane.scroll.textIconGap", four,
            "TabbedPane.wrap.textIconGap", four,
            
            "Table.focusCellHighlightBorder", new BorderUIResource.LineBorderUIResource(listSelectionBorderColor),
            //"Table.focusCellHighlightBorder", new BorderUIResource.LineBorderUIResource(Color.black),
            "Table.scrollPaneBorder", scrollPaneBorder,
            
            "TableHeader.cellBorder", new UIDefaults.ProxyLazyValue(
                    "ch.randelshofer.quaqua.QuaquaTableHeaderBorder$UIResource",
                    new Object[] {commonDir+"TableHeader.borders.png", new Insets(6,1,9,1) }
            ),
            
            "TextArea.margin", new InsetsUIResource(1,3,1,3),
            "TextArea.opaque", Boolean.TRUE,
            "TextArea.popupHandler", textComponentPopupHandler,
            
            "TextField.border", textFieldBorder,
            "TextField.opaque", Boolean.FALSE,
            "TextField.popupHandler", textComponentPopupHandler,
            "TextField.autoSelect", autoselect,
            
            "TextPane.margin", new InsetsUIResource(1,3,1,3),
            "TextPane.opaque", Boolean.TRUE,
            "TextPane.popupHandler", textComponentPopupHandler,
            
            "ToggleButton.border", new UIDefaults.ProxyLazyValue(
                    "ch.randelshofer.quaqua.QuaquaBorderFactory","createButtonBorder",
                    new Object[] {"toggle"}
            ),
            //"ToggleButton.border", new BorderUIResource.LineBorderUIResource(Color.black),
            // The values for this margin are ignored. We dynamically compute a margin
            // for the various button styles that we support, if we encounter a
            // a margin that is an instanceof a UIResource.
            "ToggleButton.margin", new InsetsUIResource(0,0,0,0),
            "ToggleButton.opaque", opaque,
            "ToggleButton.textIconGap", four,
            "ToggleButton.textShiftOffset", zero,
            "ToggleButton.requestFocusEnabled", isRequestFocusEnabled,
            
            "ToolBar.border", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.QuaquaToolBarBorder$UIResource"),
            
            // The separatorSize is set to null, because we dynamically compute different
            // sizes depending on the orientation of the separator.
            "ToolBar.separatorSize", null,
            "ToolBar.margin", new InsetsUIResource(0,0,0,0),
            
            "ToolTip.border", new BorderUIResource.LineBorderUIResource(new ColorUIResource(0x303030)),
            
            "Tree.collapsedIcon", makeIcon(getClass(), commonDir+"Tree.collapsedIcon.png"),
            "Tree.expandedIcon", makeIcon(getClass(), commonDir+"Tree.expandedIcon.png"),
            "Tree.leftChildIndent", new Integer(7),
            "Tree.line", new AlphaColorUIResource(0x00000000),
            "Tree.paintLines", Boolean.FALSE,
            "Tree.rightChildIndent", new Integer(13),
            "Tree.rowHeight", new Integer(19),
            "Tree.leafIcon", makeIcon(getClass(), commonDir+"Tree.leafIcon.png"),
            "Tree.openIcon", makeIcon(getClass(), commonDir+"Tree.openIcon.png"),
            "Tree.closedIcon", makeIcon(getClass(), commonDir+"Tree.closedIcon.png"),
            //"Tree.editorBorder", new VisualMargin(3,3,3,3),
            
            "Viewport.opaque", Boolean.TRUE,
            
            "Quaqua.Debug.colorizePaintEvents", new Boolean(QuaquaManager.getProperty("Quaqua.Debug.colorizePaintEvents","false").equals("true")),
            "Quaqua.Debug.showClipBounds", new Boolean(QuaquaManager.getProperty("Quaqua.Debug.showClipBounds","false").equals("true")),
            "Quaqua.Debug.showVisualBounds", new Boolean(QuaquaManager.getProperty("Quaqua.Debug.showVisualBounds","false").equals("true")),
            "Quaqua.Debug.clipBoundsForeground", new AlphaColorUIResource(0,0,255,128),
            "Quaqua.Debug.componentBoundsForeground", new AlphaColorUIResource(255,0,0,128),
            "Quaqua.Debug.textBoundsForeground", new AlphaColorUIResource(255,0,0,128),
            
            "ClassLoader", getClass().getClassLoader(),
        };
        table.putDefaults(objects);

        // Support for GroupLayout
        String javaVersion = QuaquaManager.getProperty("java.version","");
        if (javaVersion.startsWith("1.3") ||
                javaVersion.startsWith("1.4") ||
                javaVersion.startsWith("1.5")) {
            objects = new Object[] {
                "LayoutStyle.instance", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.Quaqua14LayoutStyle"),
                "Baseline.instance", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.QuaquaBaseline"),
            };
        } else {
            objects = new Object[] {
                "LayoutStyle.instance", new UIDefaults.ProxyLazyValue("ch.randelshofer.quaqua.Quaqua16LayoutStyle"),
            };
        }
        table.putDefaults(objects);
    }
    
    protected Object makeImage(String location) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory","createImage",
                new Object[] {location}
        );
    }
    protected Object makeBufferedImage(String location) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory","createBufferedImage",
                new Object[] {location}
        );
    }
    public static Object makeIcon(Class baseClass, String location) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory","createIcon",
                new Object[] { baseClass, location }
        );
    }
    public static Object makeIcon(Class baseClass, String location, Point shift) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory","createIcon",
                new Object[] { baseClass, location, shift }
        );
    }
    public static Object makeIcon(Class baseClass, String location, Rectangle shiftAndSize) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory","createIcon",
                new Object[] { baseClass, location, shiftAndSize }
        );
    }
    protected static Object makeIcons(String location, int states, boolean horizontal) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory","createIcons",
                new Object[] {location, new Integer(states), new Boolean(horizontal)}
        );
    }
    protected static Object makeButtonStateIcon(String location, int states) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory","createButtonStateIcon",
                new Object[] {location, new Integer(states)}
        );
    }
    protected static Object makeButtonStateIcon(String location, int states, Point shift) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory","createButtonStateIcon",
                new Object[] {location, new Integer(states), shift}
        );
    }
    protected static Object makeButtonStateIcon(String location, int states, Rectangle shift) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory","createButtonStateIcon",
                new Object[] {location, new Integer(states), shift}
        );
    }
    protected static Object makeFrameButtonStateIcon(String location, int states) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory","createFrameButtonStateIcon",
                new Object[] {location, new Integer(states)}
        );
    }
    protected static Object makeSliderThumbIcon(String location) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory","createSliderThumbIcon",
                new Object[] { location }
        );
    }
    protected Object makeOverlaidButtonStateIcon(
            String location1, int states1,
            String location2, int states2,
            Rectangle layoutRect
            ) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaIconFactory","createOverlaidButtonStateIcon",
                new Object[] {
            location1, new Integer(states1),
            location2, new Integer(states2),
            layoutRect
        }
        );
    }
    protected Object makeImageBevelBorder(String location, Insets insets) {
        return makeImageBevelBorder(location, insets, false);
    }
    protected Object makeImageBevelBorder(String location, Insets insets, boolean fill) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaBorderFactory","create",
                new Object[] { location, insets, new Boolean(fill) }
        );
    }
    protected Object makeImageBevelBorder(String location, Insets imageInsets, Insets borderInsets, boolean fill) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaBorderFactory","create",
                new Object[] { location, imageInsets, borderInsets, new Boolean(fill) }
        );
    }
    protected Object makeImageBevelBackgroundBorder(String location, Insets imageInsets, Insets borderInsets, boolean fill) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaBorderFactory","createBackgroundBorder",
                new Object[] { location, imageInsets, borderInsets, new Boolean(fill) }
        );
    }
    protected Object makeImageBevelBorders(String location, Insets insets, int states, boolean horizontal) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.QuaquaBorderFactory","create",
                new Object[] { location, insets, new Integer(states), new Boolean(horizontal) }
        );
    }
    protected Object makeTextureColor(int rgb, String location) {
        return new UIDefaults.ProxyLazyValue(
                "ch.randelshofer.quaqua.util.TextureColor$UIResource",
                new Object[] { new Integer(rgb), location }
        );
    }
    /**
     * Init design specific look and feel defaults.
     */
    protected void initDesignDefaults(UIDefaults table) {
        
    }
    
    /**
     * Returns true if the <code>LookAndFeel</code> returned
     * <code>RootPaneUI</code> instances support providing Window decorations
     * in a <code>JRootPane</code>.
     * <p>
     * This implementation returns true, since it does support providing
     * these border and window title pane decorations.
     *
     * @return True if the RootPaneUI instances created support client side
     *              decorations
     * @see JDialog#setDefaultLookAndFeelDecorated
     * @see JFrame#setDefaultLookAndFeelDecorated
     * @see JRootPane#setWindowDecorationStyle
     * @since 1.4
     */
    public boolean getSupportsWindowDecorations() {
        return false;
    }
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
        return property.equals("jaguar") || property.equals("wrap");
    }
    
    protected void installKeyboardFocusManager() {
    }
}
