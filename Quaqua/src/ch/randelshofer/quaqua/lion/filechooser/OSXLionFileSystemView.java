/*
 * @(#)OSXLionFileSystemView.java
 *
 * Copyright (c) 2009-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.lion.filechooser;

import ch.randelshofer.quaqua.filechooser.BasicOSXFileSystemView;

import java.io.*;
import java.util.*;

/**
 * OSXLionFileSystemView.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class OSXLionFileSystemView extends BasicOSXFileSystemView {

    public OSXLionFileSystemView() {

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
            "Network",
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

        names = new String[] {
            "$RECYCLE.BIN",
            "Thumbs.db",
            "desktop.ini",
        };

        hiddenDirectoryNames.addAll(Arrays.asList(names));

        File[] files={
            new File(System.getProperty("user.home"), "Library")
        };
        hiddenFiles.addAll(Arrays.asList(files));
    }
}
