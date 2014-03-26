/*
 * @(#)OSXTigerFileSystemView.java 
 *
 * Copyright (c) 2006-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.quaqua.tiger.filechooser;

import ch.randelshofer.quaqua.filechooser.BasicOSXFileSystemView;
import ch.randelshofer.quaqua.osx.OSXFile;

import java.io.*;
import java.util.*;

/**
 * A file system view for Mac OS X 10.4 (Tiger).
 * <p>
 * Note: This file system view only works on top of Apple's Macintosh Runtime for Java.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class OSXTigerFileSystemView extends BasicOSXFileSystemView {

    public OSXTigerFileSystemView() {

        String[] names = {
            "AppleShare PDS",
            "automount",
            "bin",
            "Cleanup At Startup",
            "cores",
            "Desktop DB",
            "Desktop DF",
            "dev",
            "etc",
            "home",
            "mach",
            "mach_kernel",
            "mach_kernel.ctfsys",
            "mach.sym",
            "net",
            "opt",
            "private",
            "sbin",
            "Temporary Items",
            "TheVolumeSettingsFolder",
            "TheFindByContentFolder",
            "tmp",
            "Trash",
            "usr",
            "var",
            "Volumes",
            "\u0003\u0002\u0001Move&Rename",
        };

        hiddenTopLevelNames.addAll(Arrays.asList(names));

        names = new String[]{
                    "$RECYCLE.BIN",
                    "Thumbs.db",
                    "desktop.ini",};

        hiddenDirectoryNames.addAll(Arrays.asList(names));
    }

    /**
     * Returns whether a file is hidden or not.
     */
    @Override
    public boolean isHiddenFile(File f) {

        /*
          Does not test for file names starting with '.'. Is this intentional?
        */

        if (OSXFile.isInvisible(f)) {
            return true;
        } else {
            String name = f.getName();
            if (name.length() == 0) {
                return false;
            } else if (name.charAt(name.length() - 1) == (char) 0x0d) {
                // File names ending with 0x0d are considered as
                // hidden
                return true;
            } else if (hiddenTopLevelNames.contains(name)
            && (f.getParent() == null || isRoot(f.getParentFile()))) {
                return true;
            } else if (hiddenDirectoryNames.contains(name)) {
                return true;
            } else {
                return false;
            }
        }
    }
}
