/*
 * @(#)OSXJaguarFileSystemView.java
 *
 * Copyright (c) 2001-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.quaqua.jaguar.filechooser;

import ch.randelshofer.quaqua.filechooser.*;

import java.io.*;
import java.util.*;

/**
 * A file system view for Mac OS X 10.2 (Jaguar).
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class OSXJaguarFileSystemView extends BasicOSXFileSystemView {

    public OSXJaguarFileSystemView() {

        String[] names = {
            "automount",
            "bin",
            "Cleanup At Startup",
            "cores",
            "Desktop DB",
            "Desktop DF",
            "Desktop Folder",
            "dev",
            "etc",
            "mach",
            "mach_kernel",
            "mach.sym",
            "private",
            "sbin",
            "Temporary Items",
            "TheVolumeSettingsFolder",
            "tmp",
            "Trash",
            "usr",
            "var",
            "Volumes",
        };

        hiddenTopLevelNames.addAll(Arrays.asList(names));

        names = new String[]{
                    "$RECYCLE.BIN",
                    "Thumbs.db",
                    "desktop.ini",};

        hiddenDirectoryNames.addAll(Arrays.asList(names));
    }

    /**
     * On Jaguar, we can't determine the system volume.
     * We use the file system root instead.
     */
    @Override
    protected File determineSystemVolume() {
        return unixRoot;
    }

    @Override
    protected List<File> getRootList() {
        List<File> roots = super.getRootList();

        /*
          The following was commented out in all other OS releases. Should it have been commented out here also?
        */

        File[] fs = target.getRoots();
        if (fs != null) {
            roots.addAll(Arrays.asList(fs));
        }

        return roots;
    }
}
