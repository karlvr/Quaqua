/*
 * @(#)OSXPantherFileSystemView.java
 *
 * Copyright (c) 2001-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.panther.filechooser;

import ch.randelshofer.quaqua.filechooser.*;

import java.util.*;

/**
 * A file system view for Mac OS X 10.3 (Panther).
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class OSXPantherFileSystemView extends BasicOSXFileSystemView {

    public OSXPantherFileSystemView() {

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
            "mach",
            "mach_kernel",
            "mach.sym",
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
}
