/*
 * @(#)OSXFile.java
 *
 * Copyright (c) 2009-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.osx;

import ch.randelshofer.quaqua.*;
import ch.randelshofer.quaqua.util.Images;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.charset.Charset;
import java.security.AccessControlException;
import java.util.Date;
import javax.swing.*;
import ch.randelshofer.quaqua.ext.batik.ext.awt.image.codec.tiff.*;
import ch.randelshofer.quaqua.ext.batik.ext.awt.image.codec.util.*;

/**
 * {@code OSXFile} provides access to Mac OS X file meta data and can resolve
 * file aliases.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class OSXFile {

    public final static int FILE_TYPE_ALIAS = 2;
    public final static int FILE_TYPE_DIRECTORY = 1;
    public final static int FILE_TYPE_FILE = 0;
    public final static int FILE_TYPE_UNKNOWN = -1;
    /**
     * Version of the native code library.
     */
    private final static int EXPECTED_NATIVE_CODE_VERSION = 6;
    /**
     * This array holds the colors used for drawing the gradients of a file
     * label.
     */
    private static volatile Color[][] labelColors;
    /**
     * This variable is set to true, if native code is available.
     */
    private static volatile Boolean isNativeCodeAvailable;

    private static Icon computerIcon;
    private static String computerModel;
    private static boolean computerModelInitialized;
    private static Icon computerSidebarIcon;

    private static final int kLSItemInfoIsPlainFile        = 0x00000001; /* Not a directory, volume, or symlink*/
    private static final int kLSItemInfoIsPackage          = 0x00000002; /* Packaged directory*/
    private static final int kLSItemInfoIsApplication      = 0x00000004; /* Single-file or packaged application*/
    private static final int kLSItemInfoIsContainer        = 0x00000008; /* Directory (includes packages) or volume*/
    private static final int kLSItemInfoIsAliasFile        = 0x00000010; /* Alias file (includes sym links)*/
    private static final int kLSItemInfoIsSymlink          = 0x00000020; /* UNIX sym link*/
    private static final int kLSItemInfoIsInvisible        = 0x00000040; /* Invisible by any known mechanism*/
    private static final int kLSItemInfoIsNativeApp        = 0x00000080; /* Carbon or Cocoa native app*/
    private static final int kLSItemInfoIsClassicApp       = 0x00000100; /* CFM/68K Classic app*/
    private static final int kLSItemInfoAppPrefersNative   = 0x00000200; /* Carbon app that prefers to be launched natively*/
    private static final int kLSItemInfoAppPrefersClassic  = 0x00000400; /* Carbon app that prefers to be launched in Classic*/
    private static final int kLSItemInfoAppIsScriptable    = 0x00000800; /* App can be scripted*/
    private static final int kLSItemInfoIsVolume           = 0x00001000; /* Item is a volume*/
    private static final int kLSItemInfoExtensionIsHidden  = 0x00100000; /* Item has a hidden extension*/

    /**
     * Returns true if native code is available.
     * This method also loads the native code.
     */
    private final static boolean isNativeCodeAvailable() {
        if (isNativeCodeAvailable == null) {
            synchronized (OSXFile.class) {
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
                            // Try to load 64-bit libraries if possible
                            String[] libraryNames;
                            String osArch = System.getProperty("os.arch");
                            if (osArch.equals("x86_64")) {
                                libraryNames = new String[]{"quaqua64"};
                            } else {
                                libraryNames = new String[]{"quaqua64", "quaqua"};
                            }
                            for (int i = 0; i < libraryNames.length; i++) {
                                libraryName = libraryNames[i];
                                try {
                                    JNILoader.loadLibrary(libraryName);
                                    success = true;
                                    break;
                                } catch (UnsatisfiedLinkError e) {
                                    System.err.println("Warning: " + OSXFile.class + " couldn't load library \"" + System.mapLibraryName(libraryName) + "\". " + e);
                                    success = false;
                                } catch (AccessControlException e) {
                                    System.err.println("Warning: " + OSXFile.class + " access controller denied loading library \"" + System.mapLibraryName(libraryName) + "\". " + e);
                                    success = false;
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                    System.err.println("Warning: " + OSXFile.class + " couldn't load library \"" + System.mapLibraryName(libraryName) + "\". " + e);
                                    success = false;
                                }
                            }
                        }

                        if (success) {
                            try {
                                int nativeCodeVersion = nativeGetNativeCodeVersion();
                                if (nativeCodeVersion != EXPECTED_NATIVE_CODE_VERSION) {
                                    System.err.println("Warning: " + OSXFile.class + " can't use library " + libraryName + ". It has version " + nativeCodeVersion + " instead of " + EXPECTED_NATIVE_CODE_VERSION);
                                    success = false;
                                }
                            } catch (UnsatisfiedLinkError e) {
                                System.err.println("Warning: " + OSXFile.class + " could load library " + libraryName + " but can't use it. " + e);
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
    private OSXFile() {
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


        } // Windows does not support relative path segments, so we quit here
        if (File.separatorChar == '\\') {
            return f;


        } // The following code assumes that absolute paths start with a
        // File.separatorChar.
        StringBuffer buf = new StringBuffer(f.getPath().length());


        int skip = 0;


        for (File i = f; i
                != null; i = i.getParentFile()) {
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
            int flags = nativeGetBasicItemInfoFlags(f.getAbsolutePath());

            if ((flags & kLSItemInfoIsAliasFile) != 0) {

                /*
                  Special case: the program wants to see volumes as directories.
                */

                if (f.getParent().equals("/Volumes")) {
                    return FILE_TYPE_DIRECTORY;
                }

                return FILE_TYPE_ALIAS;
            }

            if ((flags & kLSItemInfoIsContainer) != 0) {
                return FILE_TYPE_DIRECTORY;
            }

            if ((flags & kLSItemInfoIsPlainFile) != 0) {
                return FILE_TYPE_FILE;
            }

            return FILE_TYPE_UNKNOWN;
        } else {
            return (f.isDirectory()) ? FILE_TYPE_DIRECTORY : ((f.isFile()) ? FILE_TYPE_FILE : FILE_TYPE_UNKNOWN);
        }
    }

    /**
     * Resolve a path that may be absolute, relative, or start with ~user.
     */
    public static File resolvePath(String path, File currentDirectory) {
        if (path.startsWith("/")) {
            return new File(path);
        }

        if (path.startsWith("~")) {
            path = path.substring(1);
            if (path.startsWith("/")) {
                String home = System.getProperty("user.home");
                return new File(home + path);
            } else if (path.isEmpty()) {
                String home = System.getProperty("user.home");
                return new File(home);
            } else {
                return new File("/Users/" + path);
            }
        }

        if (currentDirectory == null) {
            String home = System.getProperty("user.home");
            currentDirectory = new File(home);
        }

        return new File(currentDirectory, path);
    }

    /**
     * Resolve a file by converting all valid aliases in the path.
     */

    public static File resolve(File f) {
        return resolveAlias(f, true);
    }

    /**
     * Resolve a file by converting all valid aliases in the path.
     *
     * @param f The file to be resolved.
     * @param noUI Set this to true, if the alias should be resolved without user interaction.
     * @return Returns the resolved File object.
     */
    public static File resolveAlias(File f, boolean noUI) {
        if (isNativeCodeAvailable()) {

            String path = nativeResolveAlias(f.getAbsolutePath(), noUI);

            if (path == null) {
                return null;
            }

            f = new File(path);

            /*
              Cocoa path resolution refuses to follow certain top level links. If the result is an alias, we may have
              one of those top level links. The JDK support for canonical files works better!
            */

            if (getFileType(f) != FILE_TYPE_ALIAS) {
                return f;
            }
        }

        try {
            return f.getCanonicalFile();
        } catch (IOException ex) {
            return f;
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
            String path = nativeResolveAlias(serializedAlias, noUI);


            return (path == null) ? null : new File(path);


        } else {
            return null;


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
            return nativeGetLabel(f.getAbsolutePath());


        } else {
            return -1;


        }
    }
    /**
     * Returns the tag names of the specified file.
     * R
     */
    public static String[] getTagNames(File f) {
        String[] result=null;
        if (isNativeCodeAvailable() && f != null) {
            result=nativeGetTagNames(f.getAbsolutePath());
        }
        return result==null?new String[0]:result;
    }

    /**
     * Returns the color of the specified label. Returns null, if the label
     * does not have a color.
     *
     * @param label value from 0 through 7
     * @param type 0=dark enabled,1=bright enabled,2=dark disabled,3=bright enabled
     */
    public static Color getLabelColor(int label, int type) {
        if (labelColors == null) {
            synchronized (OSXFile.class) {
                if (labelColors
                        == null) {
                    /* We use constants here, because the color values returned by the Carbon
                     * API do not match the colors used by the Finder.
                     */
                    // Colors for Mac OS X 10.3 Panther, 10.4 Tiger, and 10.5 Leopard.
                    if (QuaquaManager.getDesign() <= QuaquaManager.LEOPARD) {
                        labelColors = new Color[][]{
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
                    } else {
                        // Colors for Mac OS X 10.6 Snow Leopard.
                        labelColors = new Color[][]{
                                    // dark, bright, dark disabled, bright disabled
                                    {null, null, null, null}, // no label
                                    {new Color(0xb7b7b7), new Color(0xd8d8d8), new Color(0xe9e9e9), new Color(0xf3f3f3)}, // gray
                                    {new Color(0xc1d95e), new Color(0xdeebac), new Color(0xecf5ce), new Color(0xf5fae6)}, // green
                                    {new Color(0xcba3df), new Color(0xe7cdee), new Color(0xf0e2f6), new Color(0xf7f0fa)}, // purple
                                    {new Color(0x6db5fd), new Color(0xb8dbfe), new Color(0xd1e8ff), new Color(0xe9f4ff)}, // blue
                                    {new Color(0xf2dd60), new Color(0xfbf2ac), new Color(0xfcf6ce), new Color(0xfefce6)}, // yellow
                                    {new Color(0xfb7a70), new Color(0xfcb4ad), new Color(0xffd6d3), new Color(0xffe8e6)}, // red
                                    {new Color(0xf7b65b), new Color(0xfbd6a4), new Color(0xfee9cc), new Color(0xfff3e3)} // orange
                                };
                    }
                }
            }

        }

        return (label == -1) ? null : labelColors[label][type];
    }
    
    /**
     * Returns the color of the specified tag name. Returns null, if the tag
     * does not have a color.
     *
     * @param tagName a tag name
     * @param type 0=dark enabled,1=bright enabled,2=dark disabled,3=bright enabled
     * @return 
     */
    public static Color getTagColor(String tagName, int type) {
        int label = 0;
        switch (tagName) {
            case "Gray":label=1; break;
            case "Green":label=2; break;
            case "Purple":label=3; break;
            case "Blue":label=4; break;
            case "Yellow":label=5; break;
            case "Red":label=6; break;
            case "Orange":label=7; break;
        }
        return getLabelColor(label, type);
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
                byte[] tiffData = nativeGetIconImage(file.getAbsolutePath(), size);



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


                } // Scale the image
                if (image.getWidth() != size) {
                    image = Images.toBufferedImage(image.getScaledInstance(size, size, BufferedImage.SCALE_SMOOTH));


                }
                return image;



            } catch (IOException ex) {
                System.err.println("Image decoder failed: " + ex.getMessage());
                return null;


            }
        } else {
            return null;


        }
    }

    /**
     * Returns the QuickLook thumbnail image for the specified file.
     * If it could not be created, native code fetches the file's icon instead.
     * <p>
     * Please only call this method on Mac OS X 10.6 Snow Leopard and above.
     * Altough the native API is also present on Mac OS X 10.5 our code does
     * not run stable there.
     */
    public static BufferedImage getQuickLookThumbnailImage(File file, int size) {
        if (isNativeCodeAvailable() && file != null) {
            try {
                byte[] tiffData = nativeGetQuickLookThumbnailImage(file.getAbsolutePath(), size);



                if (tiffData == null) {
                    return null;


                }

                TIFFImageDecoder decoder = new TIFFImageDecoder(new MemoryCacheSeekableStream(new ByteArrayInputStream(tiffData)),
                        new TIFFDecodeParam());

                RenderedImage rImg = decoder.decodeAsRenderedImage(0);
                BufferedImage image;


                if (rImg instanceof BufferedImage) {
                    image = (BufferedImage) rImg;


                } else {
                    Raster r = rImg.getData();
                    WritableRaster wr = WritableRaster.createWritableRaster(r.getSampleModel(), null);
                    rImg.copyData(wr);
                    image = new BufferedImage(rImg.getColorModel(),
                            wr,
                            rImg.getColorModel().isAlphaPremultiplied(),
                            null);


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
        Image img=getIconImage(file, size);

        if (img == null) {
            System.err.println("Unable to get system icon for " + file);
        }

        return (img==null)?UIManager.getIcon("FileView.fileIcon"):new ImageIcon(img);
    }

    /**
     * Returns a QuickLook thumbnail for the specified file.
     */
    public static Icon getQuickLookThumbnail(File file, int size) {
        return new ImageIcon(getQuickLookThumbnailImage(file, size));


    }

    /**
     * Returns the kind string of the specified file. The description is
     * localized in the current Locale of the Finder.
     *
     * @return The kind or null, if it couldn't be determined.
     */
    public static String getKindString(File file) {
        if (isNativeCodeAvailable() && file != null) {
            return nativeGetKindString(file.getAbsolutePath());


        } else {
            return null;


        }
    }

    /**
     * Indicate whether the specified file is a directory that is normally viewed by the user as an ordinary file,
     * i.e., something that can be opened in an application.
     */
    public static boolean isVirtualFile(File file) {
        if (isNativeCodeAvailable() && file != null) {
            int flags = nativeGetBasicItemInfoFlags(file.getAbsolutePath());
            return (flags & kLSItemInfoIsPackage) != 0;
        } else {
            return false;
        }
    }

    public static boolean isInvisible(File file) {
        if (file != null) {
            if (isNativeCodeAvailable()) {
                int flags = nativeGetBasicItemInfoFlags(file.getAbsolutePath());
                return (flags & kLSItemInfoIsInvisible) != 0;
            } else {
                return file.isHidden();
            }
        }
        return false;
    }

    public static boolean isTraversable(File file) {
        return isTraversable(file, false, false);
    }

    public static boolean isTraversable(File file, boolean isPackageTraversable, boolean isApplicationTraversable) {
        if (file == null) {
            return false;

        } else if (isNativeCodeAvailable()) {
            int flags = nativeGetBasicItemInfoFlags(file.getAbsolutePath());

            if ((flags & kLSItemInfoIsAliasFile) != 0) {
                file = resolve(file);
                flags = nativeGetBasicItemInfoFlags(file.getAbsolutePath());
            }

            if ((flags & (kLSItemInfoIsPlainFile | kLSItemInfoIsContainer)) == kLSItemInfoIsContainer) {
                // The file is a container (a volume or directory).
                boolean isPackage = (flags & kLSItemInfoIsPackage) != 0;
                boolean isApplication = (flags & kLSItemInfoIsApplication) != 0;
                // All applications are packages, so the application option takes priority for applications.
                if (isApplication) {
                    return isApplicationTraversable;
                }
                if (isPackage) {
                    return isPackageTraversable;
                }
                return basicIsTraversable(file);

            } else if ((flags & kLSItemInfoIsPlainFile) != 0) {
                return basicIsTraversable(file);

            } else if (isVolumes(file.getParent())) {

                    /*
                      Special case: the program wants to see volumes as directories.
                    */

                    return true;

            } else {
                return false;
            }
        } else {
            return basicIsTraversable(file);
        }
    }

    private static boolean isVolumes(String s) {
        return s != null && s.equals("/Volumes");
    }

    private static String[] nonTraversableDirectories = { ".Spotlight-V100", ".DocumentRevisions", ".Trashes" };

    private static boolean basicIsTraversable(File f) {
        String name = f.getName();

        if (f.isDirectory()) {
            for (String s : nonTraversableDirectories) {
                if (s.equals(name)) {
                    return false;
                }
            }

            return true;

        } else if (isSavedSearch(f)) {
           return true;
        }

        return false;
    }

    public static boolean isSavedSearch(File f) {
        return f.getName().endsWith(".savedSearch");
    }

    public static Icon getComputerIcon() {
        if (computerIcon == null) {
            String model = getComputerModel();
            if (model != null) {
                if (model.startsWith("MacBookAir")) {
                    computerIcon = UIManager.getIcon("FileView.macbookAirIcon");
                } else if (model.startsWith("MacBookPro")) {
                    computerIcon = UIManager.getIcon("FileView.macbookProIcon");
                } else if (model.startsWith("MacBook")) {
                    computerIcon = UIManager.getIcon("FileView.macbookIcon");
                } else if (model.startsWith("MacPro")) {
                    computerIcon = UIManager.getIcon("FileView.macproIcon");
                } else if (model.startsWith("Macmini")) {
                    computerIcon = UIManager.getIcon("FileView.macminiIcon");
                } else if (model.startsWith("iMac")) {
                    computerIcon = UIManager.getIcon("FileView.imacIcon");
                }
            }

            if (computerIcon == null) {
                computerIcon = UIManager.getIcon("FileView.imacIcon");
            }

            if (computerIcon == null) {
                computerIcon = UIManager.getIcon("FileView.computerIcon");
            }
        }
        return computerIcon;
    }

    public static Icon getSidebarComputerIcon() {
        if (computerSidebarIcon == null) {
            String model = getComputerModel();
            if (model != null) {
                if (model.startsWith("MacBook")) {
                    computerSidebarIcon = UIManager.getIcon("FileChooser.sideBarIcon.Laptop");
                } else if (model.startsWith("MacPro")) {
                    computerSidebarIcon = UIManager.getIcon("FileChooser.sideBarIcon.MacPro");
                } else if (model.startsWith("Macmini")) {
                    computerSidebarIcon = UIManager.getIcon("FileChooser.sideBarIcon.MacMini");
                }
            }

            if (computerSidebarIcon == null) {
                computerSidebarIcon = UIManager.getIcon("FileChooser.sideBarIcon.Imac");
            }
        }
        return computerSidebarIcon;
    }

    private static String getComputerModel() {
        if (!computerModelInitialized) {
            computerModelInitialized = true;
            String[] cmd = { "/usr/sbin/sysctl", "hw.model" };
            Charset cs = Charset.forName("UTF-8");
            String result = QuaquaUtilities.exec(cmd, cs);
            if (result != null) {
                int pos = result.indexOf(":");
                computerModel = result.substring(pos+1).trim();
            }
        }
        return computerModel;
    }

    /**
     * Returns the file type: 0=file, 1=directory, 2=alias, -1=unknown.
     */
    private static native int nativeGetFileType(String path);

    /**
     * Resolves an alias to a path String.
     * Returns the same path if the provided path is not an alias.
     *
     * @param aliasPath the path to the alias to be resolved.
     * @param noUI Set this to true, if the alias should
     * be resolved without user interaction.
     * @return Returns the resolved path. Returns null, if the resolution failed.
     */
    private static native String nativeResolveAlias(String aliasPath, boolean noUI);

    /**
     * Resolves a serialized Alias to a path String.
     * Returns null if the resolution failed.
     *
     * @param serializedAlias the alias to be resolved.
     * @param noUI Set this to true, if the alias should
     * be resolved without user interaction.
     * @return Returns the resolved path.
     */
    private static native String nativeResolveAlias(byte[] serializedAlias, boolean noUI);

    /**
     * Returns the label of the file specified by the given path.
     * The label is a value in the interval from 0 through 7.
     * Returns -1 if the label could not be determined, e.g. if the file does
     * not exist.
     *
     * @param path the path to the file.
     */
    private static native int nativeGetLabel(String path);

    /**
     * Returns the tag names of the file specified by the given path.
     *
     * @param path the path to the file.
     */
    private static native String[] nativeGetTagNames(String path);

    /**
     * Returns the kind of the file specified by the given path.
     * The kind is localized in the current locale of the Finder.
     *
     * @param path the path to the file.
     */
    private static native String nativeGetKindString(String path);

    /**
     * Returns the icon image of a file as a byte array containing TIFF image
     * data. This method may return an image of a different size, if no
     * icon in the specified size is available.
     *
     * @param path the path to the file.
     * @param size the desired size of the icon in pixels (width and height).
     * @return Byte array with TIFF image data or null in case of failure.
     */
    private static native byte[] nativeGetIconImage(String path, int size);

    /**
     * Returns the QuickLook thumbnail image of a file as a byte array containing TIFF image
     * data.
     *
     * @param path the path to the file.
     * @param size the desired size of the icon in pixels (width and height).
     * @return Byte array with TIFF image data or null in case of failure.
     */
    private static native byte[] nativeGetQuickLookThumbnailImage(String path, int size);

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
    private static native int nativeGetBasicItemInfoFlags(String path);

    private static String computerName;
    private static boolean haveFetchedComputerName;

    /**
     * Returns the display name of the computer.
     */
    public static String getComputerName() {
        if (!haveFetchedComputerName) {
            haveFetchedComputerName = true;
            String[] cmd = { "/usr/sbin/scutil", "--get", "ComputerName" };
            Charset cs = Charset.forName("UTF-8");
            computerName = QuaquaUtilities.exec(cmd, cs).trim();
        }
        return computerName;
    }

    /**
     * Returns the localized display name of the specified file.
     */
    public static String getDisplayName(File f) {
        if (isNativeCodeAvailable()) {
            return nativeGetDisplayName(f.getAbsolutePath());


        } else {
            return f.getName();


        }
    }

    /**
     * Return the time of last use of a file, as recorded by Launch Services. Called the "Date Last Opened" by Finder.
     */
    public static Date getLastUsedDate(File f) {
        long t = nativeGetLastUsedDate(f.getAbsolutePath());
        return t > 0 ? new Date(t) : null;
    }

    /**
     * Perform a saved search.
     *
     * @param savedSearchFile The .savedSearch file that defines the query.
     * @return an array containing the files that satisfy the query or null if the query could not be performed.
     */
    public static File[] executedSavedSearch(File savedSearchFile) {
        String savedSearchPath = savedSearchFile.getAbsolutePath();
        String[] paths = nativeExecuteSavedSearch(savedSearchPath);
        if (paths != null) {
            int count = paths.length;
            File[] files = new File[count];
            for (int i = 0; i < count; i++) {
                files[i] = new File(paths[i]);
            }
            return files;
        }
        return null;
    }

    /**
     * Returns the name of the file or directory at a given path in a localized
     * form appropriate for presentation to the user.
     *
     * @param path
     * @return
     */
    private static native String nativeGetDisplayName(String path);

    /**
     * Return the time of last use, as recorded by Launch Services. Called the "Date Last Opened" by Finder.
     */
    private static native long nativeGetLastUsedDate(String path);

    /**
     * Perform a saved search.
     *
     * @param path The .savedSearch file that defines the query.
     * @return an array containing the paths of the files that satisfy the query or null if the query could not be
     * performed.
     */
    private static native String[] nativeExecuteSavedSearch(String path);


    /**
     * Returns the version of the native code library. If the version
     * does not match with the version that we expect, we can not use
     * it.
     * @return The version number of the native code.
     */
    private static native int nativeGetNativeCodeVersion();
}
