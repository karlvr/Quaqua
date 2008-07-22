/*
 * @(#)WindowsFileSystemView.java  1.1  2005-12-09
 *
 * Copyright (c) 2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua.filechooser;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.plaf.*;
import java.io.*;
import java.util.*;
/**
 * WindowsFileSystemView provides a Aqua-style view on the windows file system.
 * 
 * 
 * @author Werner Randelshofer
 * @version 1.1 2005-12-09 Moved from ch.randelshofer.quaqua.metal.filechooser
 * package into ch.randelshofer.quaqua.filechooser package.
 * <br>1.0 August 26, 2005 Created.
 */
public class WindowsFileSystemView extends QuaquaFileSystemView {
    private File computer = new File("\\");
    private File volumesFolder;
    private File desktop;
    private File systemVolume = new File("C:\\");
    private final static boolean DEBUG = false;
    
    /**
     * Creates a new instance.
     */
    public WindowsFileSystemView() {
        volumesFolder = getParentDirectory(systemVolume);
        desktop = new File(systemVolume,"WINDOWS\\Desktop");
    }
    
    public File getComputer() {
        return computer;
    }
    
    public File getSystemVolume() {
        return volumesFolder;
    }

    /**
     * Returns all root partitions on this system. For example, on
     * Windows, this would be the "Desktop" folder, while on DOS this
     * would be the A: through Z: drives.
     */
    public File[] getRoots() {
        return getFiles(volumesFolder, true);
        /*
        File[] roots1 = getFiles(volumesFolder, true);
        File[] roots2 = new File[roots1.length + 1];
        roots2[0] = volumesFolder;
        System.arraycopy(roots1, 0, roots2, 1, roots1.length);
        return roots2;*/
    }
    
    /**
     * Returns true if the file (directory) can be visited.
     * Returns false if the directory cannot be traversed.
     *
     * @param f the <code>File</code>
     * @return <code>true</code> if the file/directory can be traversed, otherwise <code>false</code>
     * @see JFileChooser#isTraversable
     * @see FileView#isTraversable
     */
    public Boolean isTraversable(File f) {
        return target.isTraversable(f);
    }
    
    /**
     * Name of a file, directory, or folder as it would be displayed in
     * a system file browser. Example from Windows: the "M:\" directory
     * displays as "CD-ROM (M:)"
     *
     * The default implementation gets information from the ShellFolder class.
     *
     * @param f a <code>File</code> object
     * @return the file name as it would be displayed by a native file chooser
     * @see JFileChooser#getName
     */
    public String getSystemDisplayName(File f) {
        return target.getSystemDisplayName(f);
    }
    
    /**
     * Type description for a file, directory, or folder as it would be displayed in
     * a system file browser. Example from Windows: the "Desktop" folder
     * is desribed as "Desktop".
     *
     * Override for platforms with native ShellFolder implementations.
     *
     * @param f a <code>File</code> object
     * @return the file type description as it would be displayed by a native file chooser
     * or null if no native information is available.
     * @see JFileChooser#getTypeDescription
     */
    public String getSystemTypeDescription(File f) {
        return target.getSystemTypeDescription(f);
    }
    
    /**
     * Icon for a file, directory, or folder as it would be displayed in
     * a system file browser. Example from Windows: the "M:\" directory
     * displays a CD-ROM icon.
     *
     * The default implementation gets information from the ShellFolder class.
     *
     * @param f a <code>File</code> object
     * @return an icon as it would be displayed by a native file chooser
     * @see JFileChooser#getIcon
     */
    public Icon getSystemIcon(File f) {
        try {
            return target.getSystemIcon(f);
        } catch (Exception e) {
            if (DEBUG) System.out.println("WindowsFileSystemView.getSystemIcon "+f+" "+e.getClass());
            return null;
        }
    }
    
    /**
     * On Windows, a file can appear in multiple folders, other than its
     * parent directory in the filesystem. Folder could for example be the
     * "Desktop" folder which is not the same as file.getParentFile().
     *
     * @param folder a <code>File</code> object repesenting a directory or special folder
     * @param file a <code>File</code> object
     * @return <code>true</code> if <code>folder</code> is a directory or special folder and contains <code>file</code>.
     */
    public boolean isParent(File folder, File file) {
        return target.isParent(folder, file);
    }
    
    /**
     *
     * @param parent a <code>File</code> object repesenting a directory or special folder
     * @param fileName a name of a file or folder which exists in <code>parent</code>
     * @return a File object. This is normally constructed with <code>new
     * File(parent, fileName)</code> except when parent and child are both
     * special folders, in which case the <code>File</code> is a wrapper containing
     * a <code>ShellFolder</code> object.
     */
    public File getChild(File parent, String fileName) {
        return target.getChild(parent, fileName);
    }
    
    
    /**
     * Checks if <code>f</code> represents a real directory or file as opposed to a
     * special folder such as <code>"Desktop"</code>. Used by UI classes to decide if
     * a folder is selectable when doing directory choosing.
     *
     * @param f a <code>File</code> object
     * @return <code>true</code> if <code>f</code> is a real file or directory.
     */
    public boolean isFileSystem(File f) {
        return target.isFileSystem(f);
    }
    
    /**
     * Is dir the root of a tree in the file system, such as a drive
     * or partition. Example: Returns true for "C:\" on Windows 98.
     *
     * @param dir a <code>File</code> object representing a directory
     * @return <code>true</code> if <code>f</code> is a root of a filesystem
     * @see #isRoot
     */
    public boolean isFileSystemRoot(File dir) {
        return target.isFileSystemRoot(dir);
    }
    
    /**
     * Used by UI classes to decide whether to display a special icon
     * for drives or partitions, e.g. a "hard disk" icon.
     *
     * The default implementation has no way of knowing, so always returns false.
     *
     * @param dir a directory
     * @return <code>false</code> always
     */
    public boolean isDrive(File dir) {
        return target.isDrive(dir);
    }
    
    /**
     * Used by UI classes to decide whether to display a special icon
     * for a floppy disk. Implies isDrive(dir).
     *
     * The default implementation has no way of knowing, so always returns false.
     *
     * @param dir a directory
     * @return <code>false</code> always
     */
    public boolean isFloppyDrive(File dir) {
        return target.isFloppyDrive(dir);
    }
    
    /**
     * Used by UI classes to decide whether to display a special icon
     * for a volumesFolder node, e.g. "My Computer" or a network server.
     *
     * The default implementation has no way of knowing, so always returns false.
     *
     * @param dir a directory
     * @return <code>false</code> always
     */
    public boolean isComputerNode(File dir) {
        return target.isComputerNode(dir);
    }
    
    // Providing default implementations for the remaining methods
    // because most OS file systems will likely be able to use this
    // code. If a given OS can't, override these methods in its
    // implementation.
    
    public File getHomeDirectory() {
        return target.getHomeDirectory();
    }
    
    /**
     * Return the user's default starting directory for the file chooser.
     *
     * @return a <code>File</code> object representing the default
     *         starting folder
     */
    public File getDefaultDirectory() {
        return target.getDefaultDirectory();
    }
    public boolean isRoot(File f) {
	if (f == null || !f.isAbsolute()) {
	    return false;
	}
        
        if (f.equals(computer)) {
            return true;
        }
        
	File[] roots = getRoots();
	for (int i = 0; i < roots.length; i++) {
	    if (roots[i].equals(f)) {
		return true;
	    }
	}
	return false;
    }
    /**
     * Returns the parent directory of <code>dir</code>.
     * @param dir the <code>File</code> being queried
     * @return the parent directory of <code>dir</code>, or
     *   <code>null</code> if <code>dir</code> is <code>null</code>
     * /
    public File getParentDirectory(File dir) {
        if (dir == null || isRoot(dir)) { return null; }
        return target.getParentDirectory(dir);
    }*/
}
