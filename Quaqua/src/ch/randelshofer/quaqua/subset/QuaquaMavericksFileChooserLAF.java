/*
 * @(#)QuaquaLeopardFileChooserLAF.java
 *
 * Copyright (c) 2009-2010 Werner Randelshofer, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package ch.randelshofer.quaqua.subset;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

/**
 * The QuaquaMavericksFileChooserLAF is an extension for Apple's Aqua Look and Feel
 * for Mac OS X 10.9 (Mavericks).
 * <p>
 * The Quaqua Look and Feel can not be used on other platforms than Mac OS X.
 * <p>
 * <b>Important:</b> This class is a cut down version of the
 * Quaqua16LionLookAndFeel. It is for use in environments, where the size of
 * the whole Quaqua look and feel would be too excessive.
 * <p>
 * <h3>Fixes and Enhancements</h3>
 * This class provides the following bug fixes end enhancements to Apple's Aqua
 * Look and Feel:
 *
 * <h4>FileChooserUI</h4>
 * <ul>
 * <li>FileChooserUI supports a list view and a column view similar to the native file dialog of
 * Mac OS X 10.9 Mavericks.</li>
 * <li>The FileChooserUI resolves aliases to files and folders.</li>
 * </ul>
 *
 * <h3>Usage</h3>
 * Please use the <code>QuaquaManager</code> to activate this look and feel in
 * your application. Or use the generic <code>QuaquaLookAndFeel</code>. Both
 * are designed to automatically detect the appropriate Quaqua Look and Feel
 * implementation for current Java VM.
 *
 * @see ch.randelshofer.quaqua.QuaquaManager
 * @see ch.randelshofer.quaqua.QuaquaLookAndFeel
 *
 * @author Werner Randelshofer
 */
public class QuaquaMavericksFileChooserLAF extends Quaqua16LionFileChooserLAF {

    /**
     * Creates a new instance.
     */
    public QuaquaMavericksFileChooserLAF() {
        String targetClassName = "apple.laf.AquaLookAndFeel";
        try {
            setTarget((LookAndFeel) Class.forName(targetClassName).newInstance());
        } catch (Exception e) {
            throw new InternalError(
                    "Unable to instantiate target Look and Feel \"" + targetClassName + "\". " + e.getMessage());
        }
    }

    /**
     * Return a one line description of this look and feel implementation,
     * e.g. "The CDE/Motif Look and Feel".   This string is intended for
     * the user, e.g. in the title of a window or in a ToolTip message.
     */
    @Override
    public String getDescription() {
        return "The Quaqua Mavericks FileChooser Look and Feel";
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
        return "Quaqua Mavericks FileChooser-only LAF";
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
        String quaquaPrefix = "ch.randelshofer.quaqua.Quaqua";
        String quaquaMavericksPrefix = "ch.randelshofer.quaqua.mavericks.QuaquaMavericks";

        Object[] uiDefaults = {
            "BrowserUI", quaquaPrefix + "BrowserUI",
            "FileChooserUI", quaquaMavericksPrefix + "FileChooserUI",};
        table.putDefaults(uiDefaults);
    }

    @Override
    protected void initComponentDefaults(UIDefaults table) {

        super.initComponentDefaults(table);

        Object[] uiDefaults = {
            "FileChooser.listView.extraColumnTextColor", new ColorUIResource(80, 80, 80),
            "FileChooser.listView.headerColor", new ColorUIResource(120, 120, 120),
            "FileChooser.listView.headerBorderColor", new ColorUIResource(190, 190, 190),
            //
        };
        table.putDefaults(uiDefaults);
    }
}
