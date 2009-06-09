/*
 * @(#)NSPasteboardTransferable.java  1.0  2009-06-09
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
package ch.randelshofer.quaqua.datatransfer;

import ch.randelshofer.quaqua.QuaquaManager;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.security.AccessControlException;

/**
 * NSPasteboardTransferable provides read access to the raw contents
 * of the  "General clipboard" Cocoa NSPasteboard.
 * <p>
 * All data flavors have the mime type application/octet-stream, and the
 * data is always delivered in a Java byte array.
 * <p>
 * The actual NSPasteboard type is stored in the human presentable name.
 * So, you have to look at this value, if you want to make sense of the
 * clipboard data.
 * <p>
 * While this class might not be immediately usable on its own, it
 * can be wrapped by a Transferable object of your own, which can convert
 * the raw data into a format that can be used by Java.
 *
 * See <a href="http://developer.apple.com/documentation/Cocoa/Reference/ApplicationKit/Classes/NSPasteboard_Class/Reference/Reference.html#//apple_ref/occ/instm/NSPasteboard/dataForType:"
 * >Apple NSPasteboard Class Reference</a>
 *
 * @author Werner Randelshofer
 * @version 1.0 2009-06-09 Created.
 */
public class NSPasteboardTransferable implements Transferable {

    /**
     * This variable is set to true, if native code is available.
     */
    private static Boolean isNativeCodeAvailable;
    private static int EXPECTED_NATIVE_CODE_VERSION = 1;

    /**
     * Load the native code.
     */
    public static boolean isNativeCodeAvailable() {
        if (isNativeCodeAvailable == null) {
            synchronized (NSPasteboardTransferable.class) {
                if (isNativeCodeAvailable == null) {
                    boolean success = false;
                    try {
                        String value = QuaquaManager.getProperty("Quaqua.jniIsPreloaded");
                        if (value == null) {
                            value = QuaquaManager.getProperty("Quaqua.JNI.isPreloaded");
                        }
                        String libraryName = null;
                        if (value != null && value.equals("true")) {
                            success = true;
                        } else {

                            // Use quaqua64 JNI-lib on x86_64 processors on Mac OS X 10.5 and higher
                            libraryName = (QuaquaManager.getOS() >= QuaquaManager.LEOPARD) &&
                                    QuaquaManager.getProperty("os.arch").equals("x86_64") ? "quaqua64" : "quaqua";
                            try {
                                System.loadLibrary(libraryName);
                                success = true;
                            } catch (UnsatisfiedLinkError e) {
                                System.err.println("Warning: " + NSPasteboardTransferable.class + " couldn't load library \"" + libraryName + "\". " + e);
                                success = false;
                            } catch (AccessControlException e) {
                                System.err.println("Warning: " + NSPasteboardTransferable.class + " access controller denied loading library \"" + libraryName + "\". " + e);
                                success = false;
                            } catch (Throwable e) {
                                e.printStackTrace();
                                System.err.println("Warning: " + NSPasteboardTransferable.class + " couldn't load library \"" + libraryName + "\". " + e);
                                success = false;
                            }
                        }

                        if (success) {
                            try {
                                int nativeCodeVersion = getNativeCodeVersion();
                                if (nativeCodeVersion != EXPECTED_NATIVE_CODE_VERSION) {
                                    System.err.println("Warning: " + NSPasteboardTransferable.class + " can't use library " + libraryName + ". It has version " + nativeCodeVersion + " instead of " + EXPECTED_NATIVE_CODE_VERSION);
                                    success = false;
                                }
                            } catch (UnsatisfiedLinkError e) {
                                System.err.println("Warning: " + NSPasteboardTransferable.class + " could load library " + libraryName + " but can't use it. " + e);
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

    /** An array of String objects containing the types of data declared for the
     * current contents of the clipboard. The returned types are listed in the
     * order they were declared.
     */
    private native static String[] getTypes();

    /** Returns the data for the specified type.
     *
     * @param dataType The type of data you want to read from the pasteboard.
     * This value should be one of the types returned by #getTypes.
     */
    private native static byte[] getDataForType(String dataType);

    /**
     * Returns the version of the native code library. If the version
     * does not match with the version that we expect, we can not use
     * it.
     * @return The version number of the native code.
     */
    private static native int getNativeCodeVersion();

    /** Returns the data flavors which are currently in the NSPasteboard.
     * The mime type of all flavors is application/octet-stream. The actual
     * type information is in the human presentable name!
     */
    public DataFlavor[] getTransferDataFlavors() {
        String[] types = NSPasteboardTransferable.getTypes();

        if (types == null) {

            return new DataFlavor[0];
        } else {
            DataFlavor[] flavors = new DataFlavor[types.length];

            for (int i = 0; i < types.length; i++) {
                flavors[i] = new DataFlavor("application/octet-stream", types[i]);
            }
            return flavors;
        }
    }

    /** Returns true if the "General Clipboard" Cocoa NSPasteboard currently
     * supports the specified data flavor.
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        DataFlavor[] f = getTransferDataFlavors();
        for (int i = 0; i < f.length; i++) {
            if (f[i].equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    /** Reads the data from the "General Clipboard" Cocoa NSPasteboard.
     * If the data flavor is supported, always returns it as a byte array. */
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor == null) {
            throw new NullPointerException("flavor");
        }

        byte[] data = getDataForType(flavor.getHumanPresentableName());

        if (data == null) {
            throw new UnsupportedFlavorException(flavor);
        }
        return data;
    }
}
