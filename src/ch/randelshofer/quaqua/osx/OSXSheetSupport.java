/*

 * @(#)OSXSheetSupport.java
 * 
 * Copyright (c) 2009 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 * 
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms.
 */
package ch.randelshofer.quaqua.osx;


import java.util.HashMap;
import java.util.regex.*;
import java.awt.*;
import java.security.AccessControlException;
import javax.swing.*;

import ch.randelshofer.quaqua.*;

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
    private static class SessionInfo {
        /*private final static int NSAlertFirstButtonReturn  = 1000;
        private final static int NSAlertSecondButtonReturn  = 1001; // Might be needed in a future version with more than 3 buttons supported.
        private final static int NSAlertThirdButtonReturn  = 1002;*/
        private final static int NSAlertDefaultReturn = 1;
        private final static int NSAlertAlternateReturn = 0;
        private final static int NSAlertOtherReturn = -1;
        private final static int NSAlertErrorReturn = -2;
        
        final JOptionPane pane;
        final SheetListener listener;
        
        SessionInfo(JOptionPane pane, SheetListener listener) {
            this.pane = pane;
            this.listener = listener;
        }
        
        void notifyListener(int returnCode, Object returnValue) {
            if (listener != null) {
                if (pane.getOptions() == null) {
                    int code = mapCode(returnCode);
                    if (returnValue != null && code == JOptionPane.CANCEL_OPTION)
                        returnValue = JOptionPane.UNINITIALIZED_VALUE;
                    listener.optionSelected(new SheetEvent(SessionInfo.this, pane, mapCode(returnCode), null, returnValue));
                } else {
                    listener.optionSelected(new SheetEvent(SessionInfo.this, pane, mapCode(returnCode), pane.getOptions()[mapOption(returnCode)], returnValue));
                }
            }
        }
        
        private int mapCode(int nativeCode) {
            if (nativeCode == NSAlertDefaultReturn)
                return JOptionPane.YES_OPTION; // same as JOptionPane.OK_OPTION
            else if (nativeCode == NSAlertOtherReturn)
                return JOptionPane.CANCEL_OPTION;
            else
                return JOptionPane.NO_OPTION;
        }
        
        private int mapOption(int nativeCode) {
            if (nativeCode == NSAlertDefaultReturn)
                return 0;
            else if (nativeCode == NSAlertOtherReturn)
                return 1;
            else
                return 2;
        }
    }
    
    /**
     * This variable is set to true, if native code is available.
     */
    private static Boolean isNativeCodeAvailable;
    /**
     * Version of the native code library.
     */
    private final static int EXPECTED_NATIVE_CODE_VERSION = 0;
    
    private static int lastID = 0;
    private static HashMap listeners = new HashMap();
    private static Pattern htmlPattern = Pattern.compile("<html>.*<head>.*<style.*>.+</style>.*</head>.*<b>(.*)</b>(<p>.*(</p>)?)*.*");

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
     * /
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
     * /
    private static native void nativeShowSheet(JDialog sheet, Window owner);

    /**
     * Hides a sheet.
     * <p>
     * See <a href="http://developer.apple.com/documentation/Cocoa/Reference/ApplicationKit/Classes/NSApplication_Class/Reference/Reference.html#//apple_ref/occ/instm/NSApplication/endSheet:">NSApplication reference</a>
     * 
     * @see #showAsSheet(JDialog)
     * @param sheet
     *            The sheet to hide.
     * /
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
     * /
    private static native void nativeHideSheet(JDialog sheet);*/

    // Callback support removed - not needed
    // @SuppressWarnings("unused")
    // private static void fireSheetFinished(JSheet sheet) {
    // // Just post a String on the Console.
    // System.out.println(sheet + " was closed.");
    // }
    
    /**
     * Returns whether the passed JOptionPane is likely to be valid shown as a native Sheet by NSAlert.
     *
     * @return true if the tests passed; false if not
    **/
    public static boolean supportsNativeOptionPane(JOptionPane pane) {
        // Native code must be available show native things at all
        if (!isNativeCodeAvailable())
            return false;
        
        // Check options if they are specified
        Object[] options = pane.getOptions();
        if (options != null) {
            // Native dialog has only three buttons and must have at least one
            if (options.length > 3 || options.length == 0)
                return false;
            
            // Buttons may only have Strings displayed
            for (int i = 0; i < options.length; i++) {
                if (!(options[i] instanceof String))
                    return false;
            }
        }
            
        // Native dialog only supports input for null selection values
        if (pane.getWantsInput() && pane.getSelectionValues() != null)
            return false;
        
        // Message must not be a HTML String and not conform to the example given in 
        if (pane.getMessage().toString().startsWith("<html>") && !htmlPattern.matcher(pane.getMessage().toString()).matches())
            return false;
        
        // Passed all checks - let's hope it works out!
        return true;
    }
    
    /**
     * Calls the native code
    **/
    public static void showOptionSheet(JOptionPane pane, Component parentComponent, SheetListener listener) {
        // Native code must be available show native things at all
        if (!isNativeCodeAvailable())
            throw new UnsatisfiedLinkError("Quaqua's Native code is not available! Please ensure the libquaqua.jnilib and libquaqua64.jnilib are in the proper locations.");
        
        String[] options = new String[3];
        if (pane.getOptions() == null) {
            switch (pane.getOptionType()) {
                case JOptionPane.DEFAULT_OPTION:
                    options[0] = UIManager.getString("OptionPane.okButtonText");
                    break;
                case JOptionPane.YES_NO_OPTION:
                    options[0] = UIManager.getString("OptionPane.yesButtonText");
                    options[2] = UIManager.getString("OptionPane.noButtonText");
                    break;
                case JOptionPane.YES_NO_CANCEL_OPTION:
                    options[0] = UIManager.getString("OptionPane.yesButtonText");
                    options[2] = UIManager.getString("OptionPane.noButtonText");
                    options[1] = UIManager.getString("OptionPane.cancelButtonText");
                    break;
                case JOptionPane.OK_CANCEL_OPTION:
                    options[0] = UIManager.getString("OptionPane.okButtonText");
                    options[2] = UIManager.getString("OptionPane.cancelButtonText");
                    break;
                default:
                    options = null;
                    break;
            }
        } else {
            Object[] opts = pane.getOptions();
            for (int i = 0; i < opts.length; i++) {
                options[i] = opts[i].toString();
            }
        }
        boolean wantsInput = pane.getWantsInput();
        String[] selectionValues;
        String initialSelectionValue;
        if (wantsInput && pane.getSelectionValues() != null && pane.getSelectionValues().length != 0 && pane.getWantsInput()) {
            selectionValues = new String[pane.getSelectionValues().length];
            for (int i = 0; i < selectionValues.length; i++) {
                selectionValues[i] = pane.getSelectionValues()[i].toString();
            }
            initialSelectionValue = pane.getInitialSelectionValue().toString();
        } else {
            selectionValues = null;
            initialSelectionValue = null;
        }
        
        String message = pane.getMessage().toString();
        String defaultButton = options[0];
        String alternateButton = options[2];
        String otherButton = options[1];
        String format;
        if (!message.startsWith("<html>")) {
            // If message has multiple lines, split the first one to be the bold message
            // and the rest to be the informative text
            if (message.indexOf('\n') != -1) {
                format = message.substring(message.indexOf('\n')).trim();
                message = message.substring(0, message.indexOf('\n'));
            } else
                format = null;
        } else {
            format = "";
            Matcher m = htmlPattern.matcher(message);
            m.matches();
            for (int i = 1; i <= m.groupCount(); i++) {
                String g = m.group(i);
                if (g == null)
                    continue;
                if (i == 1)
                    message = g.replaceAll("<br>", " ");
                else
                    format = (i == 2) ? g.trim() : (format +"\n"+ g.trim());
            }
            if (format.length() > 0)
                format = format.replaceAll("<br>", " ").replaceAll("<p>", "\n").trim();
        }
        int id = ++lastID;
        listeners.put(new Integer(id), new SessionInfo(pane, listener));
        nativeShowOptionSheet(message, defaultButton, alternateButton, otherButton, format, wantsInput, selectionValues, initialSelectionValue, SwingUtilities.getWindowAncestor(parentComponent), id);
    }
    
    private static native void nativeShowOptionSheet(String message, String defaultButton, String alternateButton, String otherButton, String format, boolean wantsInput, String[] selectionValues, String initialSelectionValue, Component parentComponent, int id);
    
    private static void performListenerForID(final int id, final int returnCode, final String returnValue) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Integer i = new Integer(id);
                SessionInfo info = (SessionInfo)listeners.get(i);
                if (info != null) {
                    info.notifyListener(returnCode, returnValue);
                }
                listeners.remove(i);
            }
        });
    }
}