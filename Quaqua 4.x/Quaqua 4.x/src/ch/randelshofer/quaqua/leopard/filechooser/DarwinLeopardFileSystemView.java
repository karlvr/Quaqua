/*
 * @(#)DarwinLeopardFileSystemView.java  1.0  November 24, 2007
 *
 * Copyright (c) 2007 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua.leopard.filechooser;

import ch.randelshofer.quaqua.filechooser.Files;
import ch.randelshofer.quaqua.filechooser.QuaquaFileSystemView;
import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import javax.swing.*;

/**
 * DarwinLeopardFileSystemView.
 * 
 * @author Werner Randelshofer
 * @version 1.0 November 24, 2007 Created.
 */
public class DarwinLeopardFileSystemView extends QuaquaFileSystemView {
    private static final File volumesFolder = new File("/Volumes");
    private static final File networkFolder = new File("/Network");
    private static final File computer = new File("/");
    private static File systemVolume;
    /**
     * This is a list of file names that are treated as invisible by the AWT
     * FileDialog when they are at the top directory level of a volume.
     * The file names are wrongly treated as visible by
     * Apple's implementation FileSystemView, so we use this HashSet here, to
     * hide them 'manually'.
     */
    private final static HashSet hiddenTopLevelNames = new HashSet();
    static {
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
    };
    
    /** Creates a new instance. */
    public DarwinLeopardFileSystemView() {
    }
    
    public File getComputer() {
        return computer;
    }
    
    public File getSystemVolume() {
        if (systemVolume == null) {
            File[] volumes = volumesFolder.listFiles();
            File sys = null;
            for (int i=0; i < volumes.length; i++) {
                try {
                    if (volumes[i].getCanonicalFile().equals(computer)) {
                        sys = volumes[i];
                        break;
                    }
                } catch (IOException e) {
                    // We get here because we can't determine the
                    // canonical path for the volume. We suppress this
                    // exception, in the hope that it did not happen for
                    // the system volume. If it happened for the system
                    // volume, there is fallback code in method
                    // getSystemVolume() that handles this problem.
                    
                    // System.err.println(
                    //   "Unable to canonicalize volume "+volumes[i]
                    // );
                    // e.printStackTrace();
                } catch (SecurityException e) {
                    // We get here because we are not allowed to read the
                    // file. We suppress this exception, in the hope that
                    // it did not happen for the system volume. If it
                    // happened for the system volume, there is fallback
                    // code in method getSystemVolume() that handles this
                    // problem.
                }
            }
            // If we couldn't determine the system volume, we use the
            // root folder instead.
            systemVolume = (sys == null) ? computer : sys;
        }
        return systemVolume;
    }
    
    public Boolean isTraversable(File f) {
        if (f.isDirectory()) {
            String name = f.getName();
            if (name.endsWith(".app")) {
                return Boolean.FALSE;
            }
        }
        // FIXME - Determine traversability
        return Boolean.valueOf(f.isDirectory());
    }
    
    public String getSystemDisplayName(File f) {
        // FIXME - Determine display name
        if (f.equals(computer)) {
            return getSystemVolume().getName();
        } else {
            return f.getName();
        }
    }
    
    public String getSystemTypeDescription(File f) {
        // FIXME - Determine system type description
        return f.isDirectory() ? "directory" : "file";
    }
    
    public Icon getSystemIcon(File f) {
        if (Files.canWorkWithAliases()) {
            if (f.equals(computer)) {
                return UIManager.getIcon("FileView.computerIcon");
            } else {
                return Files.getIcon(f, 16);
            }
        } else {
            if (f.equals(computer)) {
                return UIManager.getIcon("FileView.computerIcon");
            } else if (f.equals(systemVolume)) {
                return UIManager.getIcon("FileView.hardDriveIcon");
            } else if (f.isDirectory()) {
                return UIManager.getIcon(f.getParent().equals(volumesFolder) ? "FileView.floppyDiskIcon" : "FileView.directoryIcon");
            } else {
                return UIManager.getIcon("FileView.fileIcon");
            }
        }
    }
    
    public boolean isParent(File folder, File file) {
        if (folder == null || file == null) {
            return false;
        } else {
            return folder.equals(file.getParentFile());
        }
    }
    
    public File getChild(File parent, String fileName) {
        return new File(parent, fileName);
    }
    
    public boolean isFileSystem(File f) {
        return true;
    }
    
    public boolean isFileSystemRoot(File dir) {
        File parentFile = dir.getParentFile();
        return parentFile == null || parentFile.equals(volumesFolder);
    }
    
    public boolean isDrive(File dir) {
        return false;
    }
    
    public boolean isFloppyDrive(File dir) {
        return false;
    }
    
    public boolean isComputerNode(File dir) {
        return false;
    }
    
    public File getDefaultDirectory() {
        return getHomeDirectory();
    }
    
    /**
     * Returns whether a file is hidden or not.
     */
    public boolean isHiddenFile(File f) {
        if (f.isHidden()) {
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
            && (f.getParent() == null ||
                    isRoot(f.getParentFile()) ||
                    f.getParentFile().equals(systemVolume) ||
                    f.getParentFile().equals(volumesFolder)
                    )) {
                return true;
            } else {
                return false;
            }
        }
    }
}
