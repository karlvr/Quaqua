/*

 * @(#)OSXSheetSupport.java
 * 
 * Copyright (c) 2009-2010 Werner Randelshofer
 * Hausmatt 10, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 * 
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms.
 */
package ch.randelshofer.quaqua.osx;


import java.awt.Window;
import java.security.AccessControlException;

import javax.swing.JDialog;

import ch.randelshofer.quaqua.JSheet;
import ch.randelshofer.quaqua.QuaquaManager;

/**
 * {@link OSXSheetSupport} provides support for native {@link JDialog JDialogs} for Java 5 and
 * lower.
 * <p>
 * See {@link #showAsSheet(JDialog)} and {@link #hideSheet(JDialog)} for
 * further information.
 * <p>
 * Please note: Sheets shown by this classed have no resize indicator and block
 * their owner window from user interaction.
 * 
 * @version $Id: OSXSheetSupport.java 106 2009-07-19 20:34:09Z fedr $
 * @author Felix Draxler
 */
public class OSXSheetSupport {
    /**
     * This variable is set to true, if native code is available.
     */
    private static Boolean isNativeCodeAvailable;
    /**
     * Version of the native code library.
     */
    private final static int EXPECTED_NATIVE_CODE_VERSION = 0;

    private OSXSheetSupport() {
    }

    /**
     * Checks if the native code was loaded and loads it if it has not been yet.
     * 
     * @return <code>true</code>, if it has been loaded or could be loaded;
     *         <code>false</code> otherwise.
     **/
    private final static boolean isNativeCodeAvailable() {
        if (isNativeCodeAvailable == null) {
            synchronized (OSXSheetSupport.class) {
                if (isNativeCodeAvailable == null) {
                    boolean success = false;
                    try {

                        String value = QuaquaManager.getProperty("Quaqua.jniIsPreloaded");
                        if (value == null) {
                            value = QuaquaManager.getProperty("Quaqua.JNI.isPreloaded");
                        }
                        if (value != null && value.equals("true")) {
                            success = true;
                        } else {
                            // Use quaqua64 JNI-lib on x86_64 processors on Mac
                            // OS X 10.5 and higher
                            String libraryName = (QuaquaManager.getOS() >= QuaquaManager.LEOPARD)
                                    && QuaquaManager.getProperty("os.arch").equals("x86_64") ? "quaqua64"
                                    : "quaqua";
                            try {
                                System.loadLibrary(libraryName);
                                success = true;
                            } catch (UnsatisfiedLinkError e) {
                                System.err.println("Warning: " + OSXSheetSupport.class
                                        + " couldn't load library \"" + libraryName + "\". " + e);
                                success = false;
                            } catch (AccessControlException e) {
                                System.err.println("Warning: " + OSXSheetSupport.class
                                        + " access controller denied loading library \""
                                        + libraryName + "\". " + e);
                                success = false;
                            } catch (Throwable e) {
                                e.printStackTrace();
                                System.err.println("Warning: " + OSXSheetSupport.class
                                        + " couldn't load library \"" + libraryName + "\". " + e);
                                success = false;
                            }
                        }

                        if (success) {
                            int nativeCodeVersion = nativeGetNativeCodeVersion();
                            if (nativeCodeVersion != EXPECTED_NATIVE_CODE_VERSION) {
                                System.err.println("Warning: " + OSXSheetSupport.class
                                        + " can't use library libquaqua.jnilib. It has version "
                                        + nativeCodeVersion + " instead of "
                                        + EXPECTED_NATIVE_CODE_VERSION);
                                success = false;
                            }
                        }

                    } finally {
                        isNativeCodeAvailable = Boolean.valueOf(success);
                    }
                }
            }
        }
        return isNativeCodeAvailable == Boolean.TRUE;
    }

    /**
     * Returns the version of the native code library. If the version does not
     * match with the version that we expect, we can not use it.
     * 
     * @return The version number of the native code.
     */
    private static native int nativeGetNativeCodeVersion();

    /**
     * Shows a sheet on the given owner and immediately returns.
     * <p>
     * The native part of this method gets the NSWindow peers from the sheet and
     * its owner. Then it tells the shared NSApplication to show the sheet as a
     * standard native sheet.<br>
     * See <a href="http://developer.apple.com/documentation/Cocoa/Reference/ApplicationKit/Classes/NSApplication_Class/Reference/Reference.html#//apple_ref/occ/instm/NSApplication/beginSheet:modalForWindow:modalDelegate:didEndSelector:contextInfo:">NSApplication reference</a>
     * <p>
     * You have to call {@link JDialog#setVisible(boolean) setVisible(true)}
     * after showing the sheet to enable lightweight components. That method
     * must not have been called before this method.
     * <p>
     * In addition, {@link #hideSheet(JDialog)} must be called to hide the sheet
     * before you call {@link JDialog#setVisible(boolean) setVisible(false)} on
     * it.
     * <p>
     * The dialog must be undecorated.
     * <p>
     * {@link JSheet} handles all those details, but the method is enabled for
     * any {@link JDialog}.
     * 
     * @see JDialog#setVisible(boolean)
     * @see #hideSheet(JDialog)
     * @see JDialog#setUndecorated(boolean)
     * @param sheet
     * @return <code>true</code>, if showing the sheet succeeds.
     *         <code>false</code> otherwise.
     */
    public static boolean showAsSheet(JDialog sheet) {
        Window owner = sheet.getOwner();
        if (isNativeCodeAvailable() && owner != null) {
            if (!sheet.isDisplayable())
                sheet.addNotify();
            try {
                // Start showing the sheet
                nativeShowSheet(sheet, owner);
            } catch (UnsatisfiedLinkError e) {
                System.err.println("Warning: " + OSXSheetSupport.class
                        + " could not show a sheet with the native method.");
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Native method to show a sheet.
     * 
     * @param sheet
     *            The sheet.
     * @param owner
     *            The owner.
     */
    private static native void nativeShowSheet(JDialog sheet, Window owner);

    /**
     * Hides a sheet.
     * <p>
     * See <a href="http://developer.apple.com/documentation/Cocoa/Reference/ApplicationKit/Classes/NSApplication_Class/Reference/Reference.html#//apple_ref/occ/instm/NSApplication/endSheet:">NSApplication reference</a>
     * 
     * @see #showAsSheet(JDialog)
     * @param sheet
     *            The sheet to hide.
     */
    public static void hideSheet(JDialog sheet) {
        if (isNativeCodeAvailable() && sheet.isVisible()) {
            nativeHideSheet(sheet);
        }
    }

    /**
     * Native method to hide a sheet.
     * 
     * @param sheet
     *            The sheet.
     */
    private static native void nativeHideSheet(JDialog sheet);

    // Callback support removed - not needed
    // @SuppressWarnings("unused")
    // private static void fireSheetFinished(JSheet sheet) {
    // // Just post a String on the Console.
    // System.out.println(sheet + " was closed.");
    // }
}