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
import java.awt.event.*;
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
        private final static int NSAlertFirstButtonReturn  = 1000;
        private final static int NSAlertSecondButtonReturn  = 1001; // Might be needed in a future version with more than 3 buttons supported.
        private final static int NSAlertThirdButtonReturn  = 1002;
        
        final JOptionPane pane;
        final SheetListener listener;
        
        SessionInfo(JOptionPane pane, SheetListener listener) {
            this.pane = pane;
            this.listener = listener;
        }
        
        void notifyListener(int returnCode, Object returnValue) {
            if (listener != null) {
                Object inputValue;
                if (pane.getWantsInput()) {
                    inputValue = (returnValue != null) ? returnValue : JOptionPane.UNINITIALIZED_VALUE;
                } else {
                    inputValue = null;
                }
                
                Object value;
                int option;
                if (pane.getOptions() == null) {
                    option = mapCode(returnCode);
                    value = null;
                } else {
                    option = mapOption(returnCode);
                    value = pane.getOptions()[option];
                }
                
                listener.optionSelected(new SheetEvent(JSheet.NATIVE_SHEET_SOURCE, pane, option, value, inputValue));
            }
        }
        
        private int mapCode(int nativeCode) {
            if (nativeCode == NSAlertFirstButtonReturn)
                return JOptionPane.YES_OPTION; // same as JOptionPane.OK_OPTION
            else if (nativeCode == NSAlertSecondButtonReturn)
                return JOptionPane.CANCEL_OPTION;
            else
                return JOptionPane.NO_OPTION;
        }
        
        private int mapOption(int nativeCode) {
            return nativeCode - 1000;
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
    private static Pattern htmlPattern = Pattern.compile("<html>.*<head>.*<style.*>.+</style>.*</head>.*<b>(.*)</b>(<p>.*)*.*");

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
            if (options.length == 0)
                return false;
            
            // Buttons may only have Strings displayed
            for (int i = 0; i < options.length; i++) {
                if (!(options[i] instanceof String))
                    return false;
            }
        }
            
        // Message may only be a HTML String if it conforms to the example given at:
        // http://randelshofer.ch/quaqua/guide/joptionpane.html
        String message = pane.getMessage().toString();
        if (message.startsWith("<html>") && !htmlPattern.matcher(message).matches())
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
        
        String[] options;
        if (pane.getOptions() == null) {
            switch (pane.getOptionType()) {
                case JOptionPane.DEFAULT_OPTION:
                    options = new String[] { UIManager.getString("OptionPane.okButtonText") };
                    break;
                case JOptionPane.YES_NO_OPTION:
                    options = new String[] { UIManager.getString("OptionPane.yesButtonText"), UIManager.getString("OptionPane.noButtonText") };
                    break;
                case JOptionPane.YES_NO_CANCEL_OPTION:
                    options = new String[] { UIManager.getString("OptionPane.yesButtonText"), UIManager.getString("OptionPane.cancelButtonText"), UIManager.getString("OptionPane.noButtonText") };
                    break;
                case JOptionPane.OK_CANCEL_OPTION:
                    options = new String[] { UIManager.getString("OptionPane.okButtonText"), UIManager.getString("OptionPane.cancelButtonText") };
                    break;
                default:
                    options = null;
                    break;
            }
        } else {
            Object[] opts = pane.getOptions();
            options = new String[opts.length];
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
            // Format a HTML text so that the first part is in the bold
            // text and each other paragraph in a paragraph
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
        // Create ID and save the listener and option pane in a session info object
        int id = ++lastID;
        listeners.put(new Integer(id), new SessionInfo(pane, listener));
        // Show the alert
        nativeShowOptionSheet(message, options, format, wantsInput, selectionValues, initialSelectionValue, SwingUtilities.getWindowAncestor(parentComponent), id);
    }
    
    private static native void nativeShowOptionSheet(String message, String[] options, String format, boolean wantsInput, String[] selectionValues, String initialSelectionValue, Component parentComponent, int id);
    
    /**
     * This method serves as a callback for the native code to pass results from an NSAlert
     * back to the SheetListener.
    **/
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
    
    // #### Component embedding ####
    
    /**
     * Shows a component embedded in a sheet on the window of the parent Component.
    **/
    public static Frame showFileChooserSheet(final JFileChooser component, Component parent,
                                             final SheetListener listener) {
        if (!isNativeCodeAvailable()) {
            return null;
        }
        
        JRootPane p = new JRootPane();
        component.setVisible(true);
        p.getContentPane().add(component, BorderLayout.CENTER);
        p.setSize(p.getPreferredSize());
        
        long nativeView = createNativeView(p.getWidth(), p.getHeight());
        final SheetFrame frame = new SheetFrame(nativeView);
        frame.addNotify();
        frame.setVisible(true);
        
        final ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                int option;
                if (evt.getActionCommand().equals("ApproveSelection")) {
                    option = JFileChooser.APPROVE_OPTION;
                } else {
                    option = JFileChooser.CANCEL_OPTION;
                }
                frame.hide();
                fireOptionSelected(component, option, listener);
                component.removeActionListener(this);
            }
        };
        component.addActionListener(actionListener);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                fireOptionSelected(component, JFileChooser.CANCEL_OPTION, listener);
                component.removeActionListener(actionListener);
            }
        });
        component.rescanCurrentDirectory();
        
        showSheet(SwingUtilities.getWindowAncestor(parent), nativeView);
        
        frame.add(p);
        frame.privateSetSize(p.getWidth(), p.getHeight());
        
        return frame;
    }
    
    private static void fireOptionSelected(final JFileChooser pane, final int option, final SheetListener listener) {
        if (listener != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    listener.optionSelected(new SheetEvent(JSheet.NATIVE_SHEET_SOURCE, pane, option, null));
                }
            });
        }
    }
    
    private native static long createNativeView(int width, int height);
    
    /**
     * Hides a Frame created by {@link #showComponentSheet}.
    **/
    public static void hideSheet(Frame frame) {
        if (frame != null && isNativeCodeAvailable() && frame instanceof SheetFrame) {
            SheetFrame sheetFrame = (SheetFrame) frame;
            nativeHideSheet(sheetFrame.handle);
            sheetFrame.setVisible(false);
        }
    }
    
    private native static void nativeHideSheet(long view);
    
    private native static void nativeSetBounds(long handle, int width, int height);
    
    private native static void showSheet(Window owner, long view);

    /**
     * This class serves as the frame embedded in the native NSWindow.
     * <p>
     * As far as I understand the class loading mechanism, this class should never
     * come to be loaded except for if it is needed. If you do encounter
     * ClassNotFoundErrors for apple.awt.CEmbeddedFrame please write on the forum.
     **/
    private static final class SheetFrame extends apple.awt.CEmbeddedFrame {
        private long handle;
        
        public SheetFrame(long handle) {
            super(handle);
            this.handle = handle;
        }
        
        public void hide() {
            nativeHideSheet(handle);
            super.hide();
        }
        
        public void setBounds(int x, int y, int width, int height) {
            nativeSetBounds(handle, width, height);
            super.setBounds(0, 0, width, height);
        }
        
        public void paint(Graphics g) {
            super.paint(g);
            // Painting the resize indicator in the bottom right corner
            Icon icon = UIManager.getIcon("Frame.resize");
            if (icon != null)
                icon.paintIcon(this, g, getWidth() - icon.getIconWidth(), getHeight() - icon.getIconHeight());
        }
        
        private void privateSetSize(int width, int height) {
            super.setBounds(0, 0, width, height);
        }
    }
}