/*
 * @(#)QuaquaPantherFileChooserLAF.java
 *
 * Copyright (c) 2004-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.subset;

import ch.randelshofer.quaqua.*;
import ch.randelshofer.quaqua.osx.OSXPreferences;
import ch.randelshofer.quaqua.util.*;
import javax.swing.*;
import javax.swing.plaf.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.net.*;
import java.security.*;

/**
 * The QuaquaPantherFileChooserLAF is an extension for Apple's Aqua Look and Feel
 * for Java 1.4 on Mac OS X 10.3 (Panther).
 * <p>
 * The Quaqua Look and Feel can not be used on other platforms than Mac OS X.
 * <p>
 * <b>Important:</b> This class is a cut down version of the
 * QuaquaPantherLookAndFeel. It is for use in environments, where the size of
 * the whole Quaqua look and feel would be too excessive.
 * <p>
 * <h3>Fixes and Enhancements</h3>
 * This class provides the following bug fixes end enhancements to Apple's Aqua
 * Look and Feel:
 *
 * <h4>ComboBoxUI</h4>
 * <ul>
 * <li>Combo boxes use the font "Lucida Grande 13" instead of "Lucida Grande 14".</li>
 * </ul>
 *
 * <h4>FileChooserUI</h4>
 * <ul>
 * <li>FileChooserUI uses a column view similar to the native file dialog of
 * Mac OS X 10.3 Panther.</li>
 * <li>The look and feel provides an image showing a house for
 * <code>FileChooser.homeFolderIcon</code> and an icon showing an iMac for
 * <code>FileView.computerIcon</code> instead of an icon showing a computer
 * desktop for both properties. The FileChooserUI with column view does not use
 * these images, but your application might.</li>
 * <li>The FileChooserUI resolves aliases to files and folders.</li>
 * </ul>
 *
 * <h4>TableUI</h4>
 * <ul>
 * <li>Table headers use the font "Lucida Grande 11" instead of "Lucida Grande 13".
 * </li>
 * </ul>
 *
 * <h3>Usage</h3>
 * Please use the <code>QuaquaManager</code> to activate this look and feel in
 * your application. Or use the generic <code>QuaquaLookAndFeel</code>. Both
 * are designed to automatically detect the appropriate Quaqua Look and Feel
 * implementation for current Java VM.
 *
 * @see QuaquaManager
 * @see QuaquaLookAndFeel
 *
 * @author Werner Randelshofer
 * @version  $Id$
 */
public class QuaquaPantherFileChooserLAF extends LookAndFeelProxy {
    protected final static String commonDir = "/ch/randelshofer/quaqua/images/";
    protected final static String jaguarDir = "/ch/randelshofer/quaqua/jaguar/images/";
    protected final static String pantherDir = "/ch/randelshofer/quaqua/panther/images/";
    /**
     * Holds a bug fixed version of the UIDefaults provided by the target
     * LookAndFeel.
     * @see #initialize
     * @see #getDefaults
     */
    private UIDefaults myDefaults;
    /**
     * The small system font (Lucida Grande Regular 11 pt) is used for
     * informative text in alerts. It is also the default font for column
     * headings in lists, for help tags, and for small controls. You can also
     * use it to provide additional information about settings in various
     * windows, such as the QuickTime pane in System Preferences.
     */
    protected static final FontUIResource SMALL_SYSTEM_FONT =
    new FontUIResource("Lucida Grande", Font.PLAIN, 11);
    
    /**
     * Creates a new instance.
     */
    public QuaquaPantherFileChooserLAF() {
        String targetClassName = "apple.laf.AquaLookAndFeel";
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
     * Return a one line description of this look and feel implementation,
     * e.g. "The CDE/Motif Look and Feel".   This string is intended for
     * the user, e.g. in the title of a window or in a ToolTip message.
     */
    @Override
    public String getDescription() {
        return "The Quaqua Panther FileChooser Look and Feel";
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
        return "Quaqua FileChooser-only LAF";
    }
    
    /**
     * UIManager.setLookAndFeel calls this method before the first
     * call (and typically the only call) to getDefaults().  Subclasses
     * should do any one-time setup they need here, rather than
     * in a static initializer, because look and feel class uiDefaults
     * may be loaded just to discover that isSupportedLookAndFeel()
     * returns false.
     *
     * @see #uninitialize
     * @see UIManager#setLookAndFeel
     */
    @Override
    public void initialize() {
        // Note: We initialize in a privileged block, because if we are
        //       installed as a Standard Extension in the Java VM, we
        //       are allowed to access our resources (i.e. images),
        //       even then, when the calling application is not allowed
        //       to do so.
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                target.initialize();
                myDefaults = target.getDefaults();
                initResourceBundle(myDefaults);
                initClassDefaults(myDefaults);
                initGeneralDefaults(myDefaults);
                initComponentDefaults(myDefaults);
                return null;
            }
        });
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
        return myDefaults;
    }
    protected void initResourceBundle(UIDefaults table) {
        // The following line of code does not work, when Quaqua has been loaded with
        // a custom class loader. That's why, we have to inject the labels
        // by ourselves:
        //table.addResourceBundle( "ch.randelshofer.quaqua.Labels" );
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
        String quaquaPantherPrefix = "ch.randelshofer.quaqua.panther.QuaquaPanther";
        
        // NOTE: Uncomment parts of the code below, to override additional
        // UI classes of the target look and feel.
        Object[] uiDefaults = {
            "BrowserUI", quaquaPrefix + "BrowserUI",
            "FileChooserUI", quaquaPantherPrefix + "FileChooserUI",
        };
        table.putDefaults(uiDefaults);
    }
    protected void initGeneralDefaults(UIDefaults table) {
        Object[] uiDefaults;
        uiDefaults = new Object[]{
            "ClassLoader", getClass().getClassLoader(),
        };
        table.putDefaults(uiDefaults);
    }
    @Override
    protected void initComponentDefaults(UIDefaults table) {
        String prefValue;
        // True if file choosers orders by type
        boolean isOrderFilesByType = false;
        // True if file choosers shows all files by default
        prefValue = OSXPreferences.getString(//
                OSXPreferences.FINDER_PREFERENCES, "AppleShowAllFiles", "false")//
                .toLowerCase();
        boolean isFileHidingEnabled = prefValue.equals("false") || prefValue.equals("no");
        boolean isQuickLookEnabled = Boolean.valueOf(QuaquaManager.getProperty("Quaqua.FileChooser.quickLookEnabled","true"));

        Font smallSystemFont = SMALL_SYSTEM_FONT;
        Color grayedFocusCellBorderColor = (Color) table.get("listHighlight");
        
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
            "Browser.selectionBackground", new ColorUIResource(56,117,215),
            "Browser.selectionForeground", new ColorUIResource(255,255,255),
            "Browser.inactiveSelectionBackground", new ColorUIResource(208,208,208),
            "Browser.inactiveSelectionForeground", new ColorUIResource(0,0,0),
            "Browser.sizeHandleIcon", makeIcon(getClass(), commonDir + "Browser.sizeHandleIcon.png"),

            "FileChooser.homeFolderIcon", LookAndFeel.makeIcon(getClass(), commonDir+"FileChooser.homeFolderIcon.png"),
            //
            "FileView.computerIcon", LookAndFeel.makeIcon(getClass(), commonDir+"FileView.computerIcon.png"),
            //
            "FileChooser.fileHidingEnabled", isFileHidingEnabled,
            "FileChooser.quickLookEnabled", isQuickLookEnabled,
            "FileChooser.orderByType", isOrderFilesByType,
            "FileChooser.previewLabelForeground", new ColorUIResource(0x000000),
            "FileChooser.previewValueForeground", new ColorUIResource(0x000000),
            "FileChooser.previewLabelFont", smallSystemFont,
            "FileChooser.previewValueFont", smallSystemFont,
            "FileChooser.splitPaneDividerSize", 6,
            "FileChooser.previewLabelInsets",new InsetsUIResource(0,0,0,4),
            "FileChooser.cellTipOrigin", new Point(18, 1),
            "FileChooser.autovalidate", Boolean.TRUE,
            "FileChooser.browserFocusCellHighlightBorder",
            new UIDefaults.ProxyLazyValue(
                    "javax.swing.plaf.BorderUIResource$EmptyBorderUIResource",
                    new Object[] { new Insets(1,1,1,1) }
            ),
            "FileChooser.browserFocusCellHighlightBorderGrayed",
            new UIDefaults.ProxyLazyValue(
                    "javax.swing.plaf.BorderUIResource$MatteBorderUIResource",
                   new Object[] { 1,1,1,1, grayedFocusCellBorderColor }
            ),
            "FileChooser.browserCellBorder",
            new UIDefaults.ProxyLazyValue(
                    "javax.swing.plaf.BorderUIResource$EmptyBorderUIResource",
                    new Object[] { new Insets(1,1,1,1) }
            ),
            "FileChooser.browserUseUnselectedExpandIconForLabeledFile", Boolean.TRUE,
            
            "Sheet.showAsSheet", Boolean.TRUE,
            
        };
        table.putDefaults(uiDefaults);
    }
    protected URL getResource(String location) {
        URL url = getClass().getResource(location);
        if (url == null) {
            throw new InternalError("image resource missing: "+location);
        }
        return url;
    }
    
    protected Image createImage(String location) {
        return Toolkit.getDefaultToolkit().createImage(getResource(location));
    }
    protected Icon[] makeIcons(String location, int count, boolean horizontal) {
        Icon[] icons = new Icon[count];
        
        BufferedImage[] images = Images.split(
        createImage(location),
        count, horizontal
        );
        
        for (int i=0; i < count; i++) {
            icons[i] = new IconUIResource(new ImageIcon(images[i]));
        }
        return icons;
    }

    @Override
    public String getID() {
        return "Aqua";
    }
}


