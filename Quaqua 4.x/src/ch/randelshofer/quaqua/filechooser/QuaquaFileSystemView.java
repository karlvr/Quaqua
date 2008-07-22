/*
 * @(#)QuaquaFileSystemView.java  2.1  2006-05-07
 *
 * Copyright (c) 2005-2006 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua.filechooser;

import ch.randelshofer.quaqua.QuaquaManager;
import ch.randelshofer.quaqua.panther.filechooser.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
/**
 * QuaquaFileSystemView is an enhanced FileSystemView, which provides additional
 * information about a file system required for Aqua file choosers.
 * QuaquaFileSystemView acts as a wrapper on platform specific file system views.
 * The resulting view is an Aqua-style view on the file system.
 *
 * @author  Werner Randelshofer
 * @version 2.1 2006-05-07 Use a specialized file system view for Mac OS X Tiger. 
 * <br>2.0 2005-09-10 Moved file system related methods from QuaquaManager
 * into this class.
 * <br>1.0 August 26, 2005 Created.
 */
public abstract class QuaquaFileSystemView extends FileSystemViewFilter {
    private static QuaquaFileSystemView instance;
    
    /**
     * Creates a new instance.
     */
    public QuaquaFileSystemView() {
    }
    
    /**
     * Returns the file that represents this computer node.
     */
    public abstract File getComputer();
    
    /**
     * Returns the file that represents the system (boot) volume of this
     * computer.
     */
    public abstract File getSystemVolume();
    
    /**
     * Creates a system specific file view for the specified JFileChooser.
     */
    public FileView createFileView(JFileChooser chooser) {
        return new QuaquaFileView(this);
    }

    private static QuaquaFileSystemView fileSystemView;
    
    /**
     * Returns a FileSystemView that can be cast into QuaquaFileSystemView.
     */
    public static QuaquaFileSystemView getQuaquaFileSystemView() {
        if (fileSystemView == null) {
            String className;
            int os = QuaquaManager.getOS();
            if (os == QuaquaManager.WINDOWS) {
                className = "ch.randelshofer.quaqua.filechooser.WindowsFileSystemView";
            } else {
                switch (os) {
                    case QuaquaManager.JAGUAR :
                        className = "ch.randelshofer.quaqua.jaguar.filechooser.OSXJaguarFileSystemView";
                        break;
                    case QuaquaManager.PANTHER :
                        className = "ch.randelshofer.quaqua.panther.filechooser.OSXPantherFileSystemView";
                        break;
                    case QuaquaManager.DARWIN :
                        className = "ch.randelshofer.quaqua.leopard.filechooser.DarwinLeopardFileSystemView";
                        break;
                    case QuaquaManager.LEOPARD :
                        // In theory, we could use the DarwinLeopardFileSystemView
                        // if native code is available for Quaqua. But it turns
                        // out, that Apple's FileView implementation can read
                        // icons much faster than our Files class does.
                        /*if (Files.canWorkWithAliases()) {
                        className = "ch.randelshofer.quaqua.leopard.filechooser.DarwinLeopardFileSystemView";
                        } else {*/
                        className = "ch.randelshofer.quaqua.leopard.filechooser.OSXLeopardFileSystemView";
                        //}
                        break;
                    case QuaquaManager.TIGER :
                    default :
                        className = "ch.randelshofer.quaqua.tiger.filechooser.OSXTigerFileSystemView";
                        break;
                }
            }
            try {
                fileSystemView = (QuaquaFileSystemView) Class.forName(className).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                throw new InternalError(e.getMessage());
            }
        }
        return fileSystemView;
    }
}
