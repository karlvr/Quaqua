/*
 * Copyright (c) 2014 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.filechooser;

import javax.swing.*;
import java.io.File;

/**
 * These are the methods used by other parts of Quaqua on sidebar tree nodes that represent files.
 * (Previously all FileInfo methods were nominally supported.)
 */

public interface SidebarTreeFileNode {
    /**
     * Returns the resolved file object.
     */
    public File getResolvedFile();
    /**
     * Returns the user name of the file.
     */
    public String getUserName();

    /**
     * Returns the icon of the file.
     * Returns a proxy icon if the real icon has not yet been fetched from the
     * file system.
     */
    public Icon getIcon();
}
