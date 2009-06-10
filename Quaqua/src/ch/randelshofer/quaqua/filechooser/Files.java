/*
 * @(#)Files.java  5.3  2009-01-19
 *
 * Copyright (c) 2004-2009 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package ch.randelshofer.quaqua.filechooser;

import ch.randelshofer.quaqua.*;
import ch.randelshofer.quaqua.util.Images;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.security.AccessControlException;
import javax.swing.*;
import ch.randelshofer.quaqua.ext.batik.ext.awt.image.codec.tiff.*;
import ch.randelshofer.quaqua.ext.batik.ext.awt.image.codec.util.*;

/**
 * This class contains various methods for manipulating Files.
 * The current implementation only works on Mac OS X.
 *
 * @author Werner Randelshofer
 * @version 5.4 2009-06-10 Added support for loading images with JNI.
 * <br>5.3 2009-01-19 Handle UnsatisfiedLinkError's.
 * <br>5.2 2008-06-22 Use quaqua64 JNI-lib on x86_64 processors on Mac
 * OS X 10.5 and higher.
 * <br>5.1 2008-05-31 Removed explicit 64 bit support for JNILib. This
 * is now handled by universal build of the native library.
 * <br>5.0 2008-05-09 Added method getBasicItemInfo.
 * <br>4.1 2008-05-01 Added support for 64 bit JNILib.
 * <br>4.0 2008-03-26 Added version check to native library. 
 * <br>3.1 2007-11-24 On UnsatisfiedLinkError retry with absolute
 * path to load libquaqua.jnilib. 
 * <br>3.0 2007-10-30 On Mac OS X Leopard method getIconImage(File, size)
 * may return icons which are too big.
 * <br>2.0 2007-04-18 Added methods getKind(), getIconImage(), getIcon(),
 * based on code contributed by Rolf Howarth.
 * <br>1.6 2006-09-17 Deprecated "Quaqua.JNI.isPreloaded". The new
 * system property name is "Quaqua.jniIsPreloaded".
 * <br>1.5 2006-03-12 Quaqua.JNI.isPreloaded can be set to true to
 * prevent Quaqua from loading the JNI library on its own.
 * <br>1.4.1 2006-02-13 Print a warning on System.err, if JNI library is not
 * available.
 * <br>1.4 205-08-26 Method getAbsoluteFile supports now Windows paths.
 * <br>1.3 2005-06-30 Methods canWorkWithAliases and canWorkWithLabels added.
 * <br>1.2.1 2005-06-5 Moved calls to System.getProperty into QuaquaManager.
 * <br>1.1 2005-01-22 Method getLabel added.
 * <br>1.0.1 2004-11-01 Renamed JNI Library from "QuaquaFiles"
 * to "quaqua".
 * <br>1.0  October 8, 2004  Created.
 */
public class Files {

    public final static int FILE_TYPE_ALIAS = 2;
    public final static int FILE_TYPE_DIRECTORY = 1;
    public final static int FILE_TYPE_FILE = 0;
    public final static int FILE_TYPE_UNKOWN = -1;
    /**
     * Version of the native code library.
     */
    private final static int EXPECTED_NATIVE_CODE_VERSION = 3;
    /**
     * This array holds the colors used for drawing the gradients of a file
     * label.
     * We use constants here, because the color values returned by the Carbon
     * API do not match the colors used by the Finder.
     * The colors are valid for Mac OS X 10.3 Panther.
     */
    private final static Color[][] labelColors = {
        // dark, bright, dark disabled, bright disabled
        {null, null, null, null}, // no label
        {new Color(0xb5b5b5), new Color(0xd7d7d7), new Color(0xe9e9e9), new Color(0xf3f3f3)}, // gray
        {new Color(0xbddc5a), new Color(0xdcedaa), new Color(0xecf5ce), new Color(0xf5fae6)}, // green
        {new Color(0xcb9dde), new Color(0xe3cbee), new Color(0xf0e2f6), new Color(0xf7f0fa)}, // purple
        {new Color(0x66b1ff), new Color(0xb6daff), new Color(0xd1e8ff), new Color(0xe9f4ff)}, // blue
        {new Color(0xf2df5a), new Color(0xfbf4aa), new Color(0xfcf6ce), new Color(0xfefce6)}, // yellow
        {new Color(0xff756c), new Color(0xffb2ac), new Color(0xffd6d3), new Color(0xffe8e6)}, // red
        {new Color(0xfab555), new Color(0xfcd6a2), new Color(0xfee9cc), new Color(0xfff3e3)} // orange
    };
    /**
     * This variable is set to true, if native code is available.
     */
    private static Boolean isNativeCodeAvailable;

    /**
     * Load the native code.
     */
    private final static boolean isNativeCodeAvailable() {
        if (isNativeCodeAvailable == null) {
            synchronized (Files.class) {
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
                                System.err.println("Warning: " + Files.class + " couldn't load library \"" + libraryName + "\". " + e);
                                success = false;
                            } catch (AccessControlException e) {
                                System.err.println("Warning: " + Files.class + " access controller denied loading library \"" + libraryName + "\". " + e);
                                success = false;
                            } catch (Throwable e) {
                                e.printStackTrace();
                                System.err.println("Warning: " + Files.class + " couldn't load library \"" + libraryName + "\". " + e);
                                success = false;
                            }
                        }

                        if (success) {
                            try {
                                int nativeCodeVersion = getNativeCodeVersion();
                                if (nativeCodeVersion != EXPECTED_NATIVE_CODE_VERSION) {
                                    System.err.println("Warning: " + Files.class + " can't use library " + libraryName + ". It has version " + nativeCodeVersion + " instead of " + EXPECTED_NATIVE_CODE_VERSION);
                                    success = false;
                                }
                            } catch (UnsatisfiedLinkError e) {
                                System.err.println("Warning: " + Files.class + " could load library " + libraryName + " but can't use it. " + e);
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

    /** Prevent instance creation. */
    private Files() {
    }

    /**
     * Converts the path name denoted by the file to an absolute path.
     * Relative paths are always resolved against the home directory of the
     * user and not against the current user.dir directory.
     * The returned file objects represents an absolute path containing no
     * '.' and '..' relative path components.
     * This method acts solely on the textual representation of the file and
     * therefore does does not necessarily canonicalize the path nor does it
     * resolve aliases.
     *
     * @param f The file which we must ensure contains an absolute path.
     */
    public static File getAbsoluteFile(File f) {
        if (!f.isAbsolute()) {
            f = new File(QuaquaManager.getProperty("user.home") + File.separatorChar + f.getPath());
        }
        // Windows does not support relative path segments, so we quit here
        if (File.separatorChar == '\\') {
            return f;
        }

        // The following code assumes that absolute paths start with a
        // File.separatorChar.
        StringBuffer buf = new StringBuffer(f.getPath().length());
        int skip = 0;
        for (File i = f; i != null; i = i.getParentFile()) {
            String name = i.getName();
            if (name.equals(".")) {
                if (skip > 0) {
                    skip--;
                }
            } else if (name.equals("..")) {
                skip++;
            } else {
                if (skip > 0) {
                    skip--;
                } else {
                    buf.insert(0, name);
                    buf.insert(0, File.separatorChar);
                }
            }
        }

        return f.getPath().equals(buf.toString()) ? f : new File(buf.toString());
    }

    /**
     * Returns true if this class can work with aliases.
     */
    public static boolean canWorkWithAliases() {
        return isNativeCodeAvailable();
    }

    /**
     * Returns the file type: 0=file, 1=directory, 2=alias, -1=unknown.
     */
    public static int getFileType(File f) {
        if (isNativeCodeAvailable()) {
            return getFileType(f.getPath());
        } else {
            return (f.isDirectory()) ? 1 : ((f.isFile()) ? 0 : -1);
        }
    }

    /**
     * Resolves an alias to a File object.
     *
     * @param alias the Alias file to be resolved.
     * @param noUI Set this to true, if the alias should
     * be resolved without user interaction.
     * @return Returns the resolved File object.
     */
    public static File resolveAlias(File alias, boolean noUI) {
        if (isNativeCodeAvailable()) {
            String path = resolveAlias(alias.getPath(), noUI);
            return path == null ? null : new File(path);
        } else {
            return alias;
        }
    }

    /**
     * Resolves an alias to a type info.
     * Resolves the type of the path if the provided path is not an alias.
     *
     * @param alias the path to the alias to be resolved.
     * @param noUI Set this to true, if the alias should
     * be resolved without user interaction.
     * @return Returns the resolved path.
     * @return Returns 0 for a file, 1 for a directory, -1 if the resolution failed.
     */
    public static int resolveAliasType(File alias, boolean noUI) {
        if (isNativeCodeAvailable()) {
            return resolveAliasType(alias.getPath(), noUI);
        } else {
            return (alias.isFile()) ? 0 : ((alias.isDirectory()) ? 1 : -1);
        }
    }

    /**
     * Creates a serialized Alias.
     * @return A serialized alias or null, if serialization could not be
     * done.
     */
    public static byte[] toSerializedAlias(File f) {
        if (isNativeCodeAvailable()) {
            return toSerializedAlias(f.getPath());
        } else {
            return null;
        }
    }

    /**
     * Resolves a serialized Alias to a File object.
     * @return A File or null, if the serialized Alias could not be
     * resolved.
     * @param noUI Set this to true, if the alias should
     * be resolved without user interaction.
     */
    public static File resolveAlias(byte[] serializedAlias, boolean noUI) {
        if (isNativeCodeAvailable()) {
            String path = jniResolveAlias(serializedAlias, noUI);
            return (path == null) ? null : new File(path);
        } else {
            return null;
        }
    }

    /**
     * Resolves an alias to a type info.
     * Resolves the type of the path if the provided path is not an alias.
     *
     * @param serializedAlias the path to the alias to be resolved.
     * @param noUI Set this to true, if the alias should
     * be resolved without user interaction.
     * @return Returns the resolved path.
     * @return Returns 0 for a file, 1 for a directory, -1 if the resolution failed.
     */
    public static int resolveAliasType(byte[] serializedAlias, boolean noUI) {
        if (isNativeCodeAvailable()) {
            return jniResolveAliasType(serializedAlias, noUI);
        } else {
            return -1;
        }
    }

    /**
     * Returns true if this class can work with labels.
     */
    public static boolean canWorkWithLabels() {
        return isNativeCodeAvailable();
    }

    /**
     * Returns the label of the specified file.
     * The label is a value in the interval from 0 through 7.
     * Returns -1 if the label could not be determined, e.g. if the file does
     * not exist.
     */
    public static int getLabel(File f) {
        if (isNativeCodeAvailable() && f != null) {
            return getLabel(f.getPath());
        } else {
            return -1;
        }
    }

    /**
     * Returns the color of the specified label. Returns null, if the label
     * does not have a color.
     *
     * @param label value from 0 through 7
     * @param type 0=dark enabled,1=bright enabld,2=dark disabled,3=bright enabled
     */
    public static Color getLabelColor(int label, int type) {

        return (label == -1) ? null : labelColors[label][type];
    }

    /**
     * Returns the icon image for the specified file.
     * If the file does not exist, a generic image is returned.
     * XXX - Returns null if it was not possible to get the icon image. We
     * should return a generic image in this case!
     */
    public static BufferedImage getIconImage(File file, int size) {
        if (isNativeCodeAvailable() && file != null) {
            try {
                byte[] tiffData = getIconImage(file.getPath(), size);

                if (tiffData == null) {
                    return null;
                }

                TIFFImageDecoder decoder = new TIFFImageDecoder(
                        new MemoryCacheSeekableStream(new ByteArrayInputStream(tiffData)),
                        new TIFFDecodeParam());

                RenderedImage rImg = decoder.decodeAsRenderedImage(0);
                BufferedImage image;
                if (rImg instanceof BufferedImage) {
                    image = (BufferedImage) rImg;
                } else {
                    Raster r = rImg.getData();
                    WritableRaster wr = WritableRaster.createWritableRaster(
                            r.getSampleModel(), null);
                    rImg.copyData(wr);
                    image = new BufferedImage(
                            rImg.getColorModel(),
                            wr,
                            rImg.getColorModel().isAlphaPremultiplied(),
                            null);
                }

                // Scale the image
                if (image.getWidth() != size) {
                    image = Images.toBufferedImage(image.getScaledInstance(size, size, BufferedImage.SCALE_SMOOTH));
                }
                return image;

            } catch (IOException ex) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Returns the icon for the specified file.
     * If the file does not exist, a generic icon is returned.
     */
    public static Icon getIcon(File file, int size) {
        return new ImageIcon(getIconImage(file, size));
    }

    /**
     * Returns the kind string of the specified file. The description is
     * localized in the current Locale of the Finder.
     *
     * @return The kind or null, if it couldn't be determined.
     */
    public static String getKindString(File file) {
        if (isNativeCodeAvailable() && file != null) {
            return getKindString(file.getPath());
        } else {
            return null;
        }
    }

    public static boolean isTraversable(File file) {
        if (isNativeCodeAvailable() && file != null) {
            int flags = getBasicItemInfoFlags(file.getPath());
            //   kLSItemInfoIsPlainFile = 0x00000001,
            //   kLSItemInfoIsPackage = 0x00000002,
            //   kLSItemInfoIsContainer = 0x00000008
            return (flags & (0x1 | 0x2 | 0x8)) == 8;
        } else {
            return file.isDirectory();
        }
    }

    /**
     * Returns the file type: 0=file, 1=directory, 2=alias, -1=unknown.
     */
    private static native int getFileType(String path);

    /**
     * Resolves an alias to a path String.
     * Returns the same path if the provided path is not an alias.
     *
     * @param aliasPath the path to the alias to be resolved.
     * @param noUI Set this to true, if the alias should
     * be resolved without user interaction.
     * @return Returns the resolved path. Returns null, if the resolution failed.
     */
    private static native String resolveAlias(String aliasPath, boolean noUI);

    /**
     * Resolves an alias to a type info.
     * Resolves the type of the path if the provided path is not an alias.
     *
     * @param aliasPath the path to the alias to be resolved.
     * @param noUI Set this to true, if the alias should
     * be resolved without user interaction.
     * @return Returns the resolved path.
     * @return Returns 0 for a file, 1 for a directory, -1 if the resolution failed.
     */
    private static native int resolveAliasType(String aliasPath, boolean noUI);

    /**
     * Converts a path into a serialized alias.
     * Returns null if the conversion failed.
     */
    private static native byte[] toSerializedAlias(String path);

    /**
     * Resolves a serialized Alias to a path String.
     * Returns null if the resolution failed.
     *
     * @param serializedAlias the alias to be resolved.
     * @param noUI Set this to true, if the alias should
     * be resolved without user interaction.
     * @return Returns the resolved path.
     */
    private static native String jniResolveAlias(byte[] serializedAlias, boolean noUI);

    /**
     * Resolves a serialized Alias to a type.
     * Returns -1 if the resolution failed.
     *
     * @param serializedAlias the alias to be resolved.
     * @param noUI Set this to true, if the alias should
     * be resolved without user interaction.
     * @return Returns 0 for a file, 1 for a directory, -1 if the resolution failed.
     */
    private static native int jniResolveAliasType(byte[] serializedAlias, boolean noUI);

    /**
     * Returns the label of the file specified by the given path.
     * The label is a value in the interval from 0 through 7.
     * Returns -1 if the label could not be determined, e.g. if the file does
     * not exist.
     *
     * @param path the path to the file.
     */
    private static native int getLabel(String path);

    /**
     * Returns the kind of the file specified by the given path.
     * The kind is localized in the current locale of the Finder.
     *
     * @param path the path to the file.
     */
    private static native String getKindString(String path);

    /**
     * Returns the icon image of a file as a byte array containing TIFF image 
     * data. This method may return an image of a different size, if no
     * icon in the specified size is available.
     *
     * @param path the path to the file.
     * @param size the desired size of the icon in pixels (width and height).
     * @return Byte array with TIFF image data or null in case of failure.
     */
    private static native byte[] getIconImage(String path, int size);

    /**
     * Returns the basic item-information flags of the file specified by the given path.
     * <p>
     * Requests all item-information flags that are not application-specific:
     * that is, all except kLSItemInfoIsNativeApp through kLSItemInfoAppIsScriptable.
     * <p>
     * The item-information flags can have the following values:
     * <pre>  
     * 
     *  Item-Information Flags
     * 
     *  typedef OptionBits LSItemInfoFlags;enum {
     *  kLSItemInfoIsPlainFile = 0x00000001,
     *  kLSItemInfoIsPackage = 0x00000002,
     *  kLSItemInfoIsApplication = 0x00000004,
     *  kLSItemInfoIsContainer = 0x00000008,
     *  kLSItemInfoIsAliasFile = 0x00000010,
     *  kLSItemInfoIsSymlink = 0x00000020,
     *  kLSItemInfoIsInvisible = 0x00000040,
     * 
     *  kLSItemInfoIsNativeApp = 0x00000080,
     *  kLSItemInfoIsClassicApp = 0x00000100,
     *  kLSItemInfoAppPrefersNative = 0x00000200,
     *  kLSItemInfoAppPrefersClassic = 0x00000400,
     *  kLSItemInfoAppIsScriptable = 0x00000800,
     * 
     *  kLSItemInfoIsVolume = 0x00001000,
     *  kLSItemInfoExtensionIsHidden = 0x00100000
     *  };
     * </pre>     
     * 
     * For more information see 
     * http://developer.apple.com/documentation/Carbon/Reference/LaunchServicesReference/Reference/reference.html#//apple_ref/c/tdef/LSItemInfoFlags 
     *
     * @param path the path to the file.
     */
    private static native int getBasicItemInfoFlags(String path);

    /**
     * Returns the localized display name of the specified file.
     */
    public static String getDisplayName(File f) {
        if (isNativeCodeAvailable()) {
            return getDisplayName(f.getPath());
        } else {
            return f.getName();
        }
    }

    /**
     * Returns the name of the file or directory at a given path in a localized
     * form appropriate for presentation to the user.
     * 
     * @param path
     * @return
     */
    private static native String getDisplayName(String path);
	
	/**
	 * JNI method for {@link #getImageFromFile(File, int, int)}.
	**/
	private static native byte[] nativeGetImageFromFile(String path, int width, int height);

    /**
     * Uses JNI to fetch the image data in the specified file.
     * <p>
     * A preferred size is used if it is stored in the image file, but you
     * cannot expect the resulting Image to have these dimensions.<br>
     * If your application needs a specific size, you should check the
     * dimension.
     * <p>
     * If JNI fails to load the image (e.g. the native code could not be
     * loaded), <code>null</code> is returned.
     * 
     * @param file
     *            The file containing the image.
     * @param width
     *            The preferred width.
     * @param height
     *            The preferred height.
     * @return The image loaded. <code>null</code>, if no image could be loaded
     *         by JNI.
     */
    public static Image getImageFromFile(File file, int width, int height) {
        if (!isNativeCodeAvailable())
            return null;

        try {
            byte[] tiffData = nativeGetImageFromFile(file.getAbsolutePath(), width, height);
            TIFFImageDecoder decoder = new TIFFImageDecoder(new MemoryCacheSeekableStream(
                    new ByteArrayInputStream(tiffData)), new TIFFDecodeParam());

            RenderedImage rImg = decoder.decodeAsRenderedImage(0);
            return Images.toBufferedImage(rImg);
        } catch (UnsatisfiedLinkError e) {
        	// TODO Maybe add a warning to update the libraries.
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Same as {@link #getImageFromFile(File, int, int)}, only with a square
     * size.
     * 
     * @param file
     *            The file containing the image.
     * @param size
     *            The preferred width and height.
     * @return The image loaded. <code>null</code>, if no image could be loaded
     *         by JNI.
     */
    public static Image getImageFromFile(File file, int size) {
        return getImageFromFile(file, size, size);
    }

    /**
     * Returns the version of the native code library. If the version
     * does not match with the version that we expect, we can not use
     * it.
     * @return The version number of the native code.
     */
    private static native int getNativeCodeVersion();
}
