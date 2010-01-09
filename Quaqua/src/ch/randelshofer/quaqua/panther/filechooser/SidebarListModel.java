/*
 * @(#)SidebarListModel.java  3.0.3  2008-04-17
 *
 * Copyright (c) 2004-2010 Werner Randelshofer
 * Hausmatt 10, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua.panther.filechooser;

import ch.randelshofer.quaqua.osx.OSXFile;
import ch.randelshofer.quaqua.filechooser.*;
import ch.randelshofer.quaqua.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.io.*;
import java.util.*;
import ch.randelshofer.quaqua.*;
import ch.randelshofer.quaqua.ext.base64.*;
import ch.randelshofer.quaqua.ext.nanoxml.*;

/**
 * This is the list model used to display a sidebar in the PantherFileChooserUI.
 * The list consists of two parts: the system items and the user items.
 * The user items are read from the file "~/Library/Preferences/com.apple.sidebarlists.plist".
 * The system items is the contents of the "/Volumes" directory plus the
 * "/Networks" directory.
 * <p>
 * Each element of the SidebarListModel implements the interface FileInfo.
 *
 *
 * @author  Werner Randelshofer
 * @version 3.0.3 2008-04-17 Method FileItem invoked its worker too many times. 
 * <br>3.0.2 2008-03-17 Method validate() fired intervalAdded event with 
 * wrong value. 
 * <br>3.0.1 2007-11-02 Remove leaf nodes from system items. 
 * <br>3.0 2007-06-18 Resolve aliases lazily.
 * <br>2.2.1 2007-01-25 Don't fill userItems with File object's that we
 * can't use anyway.
 * <br>2.2 2006-09-23 Show "Computer" as first item in system list.
 * <br>2.1 2006-05-07 Fixed sorting of "Computer" item in system list.
 * <br>2.0.1 2006-02-17 Add only those defaultUserItems that exist. Added
 * support for Japanese Desktop directory on Windows XPJ.
 * <br>2.0 2005-11-26 Rewritten to better support system items.
 * <br>1.1 2005-08-26 Support for Windows added.
 * <br>1.0.6 2005-07-07 Suppress error message when we aren't able to
 * build a sidebar because native support for the File object is not available.
 * <br>1.0.5 2005-06-20 Throw an exception, if alias resolution is not supported by
 * class OSXFile. In this case, we are unable to create a sidebar list based on user preferences,
 * and we have to fall back to a predefined list.
 * <br>1.0.4 2005-06-20 Don't throw an exception when we can't resolve an alias.
 * <br>1.0.3 2005-06-05 Moved System.getProperty calls into QuaquaManager.
 * <br>1.0.2 2004-12-26 Method read left the input stream to the sidebar
 * file open.
 * <br>1.0.1 2004-11-28 Removed unnecessary main method.
 * <br>1.0  November 4, 2004  Created.
 */
public class SidebarListModel
        extends AbstractListModel
        implements TreeModelListener {
    private final static boolean DEBUG = false;
    /**
     * This file contains information about the system list and holds the aliases
     * for the user list.
     */
    private final static File sidebarFile = new File(QuaquaManager.getProperty("user.home"), "Library/Preferences/com.apple.sidebarlists.plist");
    
    /**
     * Holds the tree path to the /Volumes folder.
     */
    private TreePath path;
    
    /**
     * Holds the AliasFileSystemTreeModel.
     */
    private TreeModel model;
    
    /**
     * The JFileChooser.
     */
    private JFileChooser fileChooser;
    
    /**
     * Sequential dispatcher for the lazy creation of icons.
     */
    private SequentialDispatcher dispatcher = new SequentialDispatcher();
    
    /**
     * Set this to true, if the computer shall be listed in the sidebar.
     */
    private boolean isComputerVisible;
    
    private final static File[] defaultUserItems;
    static {
        if (QuaquaManager.getProperty("os.name").equals("Mac OS X")) {
            defaultUserItems = new File[] {
                null, // null is used to specify a divider
                new File(QuaquaManager.getProperty("user.home"), "Desktop"),
                new File(QuaquaManager.getProperty("user.home"), "Documents"),
                new File(QuaquaManager.getProperty("user.home"))
            };
        } else if (QuaquaManager.getProperty("os.name").startsWith("Windows")) {
            defaultUserItems = new File[] {
                null, // null is used to specify a divider
                new File(QuaquaManager.getProperty("user.home"), "Desktop"),
                // Japanese ideographs for Desktop:
                new File(QuaquaManager.getProperty("user.home"), "\u684c\u9762"),
                new File(QuaquaManager.getProperty("user.home"), "My Documents"),
                new File(QuaquaManager.getProperty("user.home"))
            };
        } else {
            defaultUserItems = new File[] {
                null, // null is used to specify a divider
                new File(QuaquaManager.getProperty("user.home"))
            };
        }
    }
    /**
     * This array list holds the user items.
     */
    private ArrayList userItems = new ArrayList();
    /**
     * This array holds the view to model mapping of the system items.
     */
    private Row[] viewToModel = new Row[0];
    /**
     * This array holds the model to view mapping of the system items.
     */
    private int[] modelToView = new int[0];
    
    /**
     * This hash map is used to determine the sequence and visibility of the
     * items in the system list.
     * HashMap<String,SystemItemInfo>
     */
    private HashMap systemItemsMap = new HashMap();
    
    private static class SystemItemInfo {
        String name = "";
        int sequenceNumber = 0;
        boolean isVisible = true;
    }
    
    /**
     * Intervals between validations.
     */
    private final static long VALIDATION_TTL = 60000;
    
    /**
     * Time for next validation of the model.
     */
    private long bestBefore;
    
    
    
    /** Creates a new instance. */
    public SidebarListModel(JFileChooser fileChooser, TreePath path, TreeModel model) {
        this.fileChooser = fileChooser;
        this.path = path;
        this.model = model;
        model.addTreeModelListener(this);
        sortSystemItems();
        validate();
    }
    
    public void dispose() {
        model.removeTreeModelListener(this);
    }
    
    public int getSize() {
        return (isComputerVisible) ?
            1 + viewToModel.length + userItems.size() :
            viewToModel.length + userItems.size();
    }
    
    
    private void sortSystemItems() {
        AliasFileSystemTreeModel.Node parent = (AliasFileSystemTreeModel.Node) path.getLastPathComponent();
        if (modelToView.length != parent.getChildCount()) {
            viewToModel = new Row[parent.getChildCount()];
            modelToView = new int[viewToModel.length];
        }
        for (int i=0; i < viewToModel.length; i++) {
            viewToModel[i] = new Row(i);
        }
        Arrays.sort(viewToModel);
        for (int i=0; i < viewToModel.length; i++) {
            modelToView[viewToModel[i].modelIndex] = i;
        }
        
        // remove leaf nodes from system items
        int j = 0;
        for (int i=0; i < viewToModel.length; i++) {
            AliasFileSystemTreeModel.Node node = (AliasFileSystemTreeModel.Node) parent.getChildAt(viewToModel[i].modelIndex);
            if (! node.isLeaf()) {
                viewToModel[j] = viewToModel[i];
                modelToView[viewToModel[j].modelIndex] = i;
                j++;
            }
        }
        if (j < viewToModel.length) {
            Row[] helper = new Row[j];
            System.arraycopy(viewToModel, 0, helper, 0, j);
            viewToModel = helper;
        }
    }
    
    public Object getElementAt(int row) {
        if (isComputerVisible) {
            if (row == 0) {
                return path.getPathComponent(0);
                
            } else if (row <= viewToModel.length) {
                return ((AliasFileSystemTreeModel.Node) model.getChild(path.getLastPathComponent(), viewToModel[row - 1].modelIndex));
                
            } else {
                return userItems.get(row - viewToModel.length - 1);
                
            }
        } else {
            return (row < viewToModel.length)
            ? ((AliasFileSystemTreeModel.Node) model.getChild(path.getLastPathComponent(), viewToModel[row].modelIndex))
            : userItems.get(row - viewToModel.length);
        }
    }
    
    public void treeNodesChanged(TreeModelEvent e) {
        if (e.getTreePath().equals(path)) {
            int[] indices = e.getChildIndices();
            fireContentsChanged(this, modelToView[indices[0]], modelToView[indices[indices.length - 1]]);
        }
    }
    
    public void treeNodesInserted(TreeModelEvent e) {
        if (e.getTreePath().equals(path)) {
            sortSystemItems();
            
            int[] indices = e.getChildIndices();
            for (int i=0; i < indices.length; i++) {
                int index = modelToView[indices[i]];
                fireIntervalAdded(this, index, index);
            }
        }
    }
    
    public void treeNodesRemoved(TreeModelEvent e) {
        if (e.getTreePath().equals(path)) {
            int[] indices = e.getChildIndices();
            int[] oldModelToView = (int[]) modelToView.clone();
            
            sortSystemItems();
            
            for (int i=0; i < indices.length; i++) {
                int index = oldModelToView[indices[i]];
                int offset = 0;
                for (int j=0; j < i; j++) {
                    if (oldModelToView[indices[i]] < index) {
                        offset++;
                    }
                }
                fireIntervalRemoved(this, index - offset, index - offset);
            }
        }
    }
    
    public void treeStructureChanged(TreeModelEvent e) {
        if (e.getTreePath().equals(path)) {
            sortSystemItems();
            fireContentsChanged(this, 0, getSize() - 1);
        }
    }
    
    private class FileItem implements FileInfo {
        private File file;
        private Icon icon;
        private String userName;
        private boolean isTraversable;
        /**
         * Holds a Finder label for the file represented by this node.
         * The label is a value in the interval from 0 through 7.
         * The value -1 is used, if the label could not be determined.
         */
        protected int fileLabel = -1;
        
        public FileItem(File file) {
            this.file = file;
            
            userName = fileChooser.getName(file);
            isTraversable = true;
            //isTraversable = file.isDirectory();
        }
        
        public File lazyGetResolvedFile() {
            return file;
        }
        public File getResolvedFile() {
            return file;
        }
        public File getFile() {
            return file;
        }
        
        
        public String getFileKind() {
            return null;
        }
        
        public int getFileLabel() {
            return -1;
        }
        
        public long getFileLength() {
            return -1;
        }
        
        public Icon getIcon() {
            if (icon == null) {
                    icon = (isTraversable())
                    ? UIManager.getIcon("FileView.directoryIcon")
                    : UIManager.getIcon("FileView.fileIcon");
                if (! QuaquaManager.getBoolean("FileChooser.speed")) {
                    dispatcher.dispatch(new Worker() {
                        public Object construct() {
                            return fileChooser.getIcon(file);
                        }
                        public void finished(Object value) {
                            icon = (Icon) value;
                            SidebarListModel.this.fireContentsChanged(SidebarListModel.this, 0, SidebarListModel.this.getSize() - 1);
                        }
                        
                    });
                }
            }
            return icon;
        }
        
        public String getUserName() {
            /*
            if (userName == null) {
                userName = fileChooser.getName(file);
            }*/
            return userName;
        }
        
        public boolean isTraversable() {
            return isTraversable;
        }
        public boolean isAcceptable() {
            return true;
        }
        
        public boolean isValidating() {
            return false;
        }
    }
    /**
     * An AliasItem is resolved as late as possible.
     */
    private class AliasItem implements FileInfo {
        private byte[] serializedAlias;
        private File file;
        private Icon icon;
        private String userName;
        private String aliasName;
        private boolean isTraversable;
        /**
         * Holds a Finder label for the file represented by this node.
         * The label is a value in the interval from 0 through 7.
         * The value -1 is used, if the label could not be determined.
         */
        protected int fileLabel = -1;
        
        public AliasItem(byte[] serializedAlias, String aliasName) {
            this.file = null;
            this.aliasName = aliasName;
            this.serializedAlias = serializedAlias;
            isTraversable = true;
        }
        
        public File lazyGetResolvedFile() {
            return getResolvedFile();
        }
        public File getResolvedFile() {
            if (file == null) {
                icon = null; // clear cached icon!
                file = OSXFile.resolveAlias(serializedAlias, false);
            }
            return file;
        }
        public File getFile() {
            return file;
        }
        
        
        public String getFileKind() {
            return null;
        }
        
        public int getFileLabel() {
            return -1;
        }
        
        public long getFileLength() {
            return -1;
        }
        
        public Icon getIcon() {
            if (icon == null) {
                    icon = (isTraversable())
                    ? UIManager.getIcon("FileView.directoryIcon")
                    : UIManager.getIcon("FileView.fileIcon");
                if (file != null && ! QuaquaManager.getBoolean("FileChooser.speed")) {
                    dispatcher.dispatch(new Worker() {
                        public Object construct() {
                            return fileChooser.getIcon(file);
                        }
                        public void finished(Object value) {
                            icon = (Icon) value;
                            SidebarListModel.this.fireContentsChanged(SidebarListModel.this, 0, SidebarListModel.this.getSize() - 1);
                        }
                        
                    });
                }
            }
            return icon;
        }
        
        public String getUserName() {
            if (userName == null) {
                if (file != null) {
                    userName = fileChooser.getName(file);
                }
            }
            return (userName == null) ? aliasName : userName;
        }
        
        public boolean isTraversable() {
            return isTraversable;
        }
        public boolean isAcceptable() {
            return true;
        }
        
        public boolean isValidating() {
            return false;
        }
    }
    /**
     * Validates the model if needed.
     */
    public void lazyValidate() {
        if (bestBefore < System.currentTimeMillis()) {
            validate();
        }
    }
    /**
     * Immediately validates the model.
     */
    private void validate() {
        // Prevent multiple invocations of this method by lazyValidate(),
        // while we are validating;
        bestBefore = Long.MAX_VALUE;
        
        dispatcher.dispatch(
                new Worker() {
            public Object construct() {
                try {
                    return read();
                } catch (Exception e) {
                    return e;
                }
            }
            public void finished(Object value) {
                ArrayList freshUserItems;
                
                if (value instanceof Throwable) {
                    freshUserItems = new ArrayList(defaultUserItems.length);
                    for (int i=0; i < defaultUserItems.length; i++) {
                        if (defaultUserItems[i] == null) {
                            freshUserItems.add(null);
                        } else if (defaultUserItems[i].exists()) {
                            freshUserItems.add(new FileItem(defaultUserItems[i]));
                        }
                    }
                } else {
                    systemItemsMap = (HashMap) ((Object[]) value)[0];
                    freshUserItems = (ArrayList) ((Object[]) value)[1];
                    freshUserItems.add(0, null);
                }
                
                int systemItemsSize = model.getChildCount(path.getLastPathComponent());
                int oldUserItemsSize = userItems.size();
                userItems.clear();
                if (oldUserItemsSize > 0) {
                    fireIntervalRemoved(
                            SidebarListModel.this,
                            systemItemsSize,
                            systemItemsSize + oldUserItemsSize - 1
                            );
                }
                userItems = freshUserItems;
                if (userItems.size() > 0) {
                    if (DEBUG) System.out.println("SidebarListModel.fireIntervalAdded "+systemItemsSize+".."+(systemItemsSize + + userItems.size() - 1)+", list size="+getSize());
                    fireIntervalAdded(
                            SidebarListModel.this,
                            systemItemsSize,
                            systemItemsSize + userItems.size() - 1
                            );
                }
                bestBefore = System.currentTimeMillis() + VALIDATION_TTL;
            }
            
        });
    }
    
    /**
     * Reads the sidebar preferences file.
     */
    private Object[] read() throws IOException {
        if (! OSXFile.canWorkWithAliases()) {
            throw new IOException("Unable to work with aliases");
        }
        
        HashMap systemItemsMap = new HashMap();
        ArrayList userItems = new ArrayList();
        
        FileReader reader = null;
        try {
            reader =  new FileReader(sidebarFile);
            XMLElement xml = new XMLElement(new HashMap(), false, false);
            try {
                xml.parseFromReader(reader);
            } catch (XMLParseException e) {
                xml = new BinaryPListParser().parse(sidebarFile);
            }
            String key2 = "", key3 = "", key5 = "";
            for (Iterator i0 = xml.iterateChildren(); i0.hasNext(); ) {
                XMLElement xml1 = (XMLElement) i0.next();
                
                for (Iterator i1 = xml1.iterateChildren(); i1.hasNext(); ) {
                    XMLElement xml2 = (XMLElement) i1.next();
                    
                    if (xml2.getName().equals("key")) {
                        key2 = xml2.getContent();
                    }
                    
                    if (xml2.getName().equals("dict") && key2.equals("systemitems")) {
                        for (Iterator i2 = xml2.iterateChildren(); i2.hasNext(); ) {
                            XMLElement xml3 = (XMLElement) i2.next();
                            if (xml3.getName().equals("key")) {
                                key3 = xml3.getContent();
                            }
                            if (xml3.getName().equals("array") && key3.equals("VolumesList")) {
                                for (Iterator i3 = xml3.iterateChildren(); i3.hasNext(); ) {
                                    XMLElement xml4 = (XMLElement) i3.next();
                                    
                                    if (xml4.getName().equals("dict")) {
                                        SystemItemInfo info = new SystemItemInfo();
                                        for (Iterator i4 = xml4.iterateChildren(); i4.hasNext(); ) {
                                            XMLElement xml5 = (XMLElement) i4.next();
                                            
                                            if (xml5.getName().equals("key")) {
                                                key5 = xml5.getContent();
                                            }
                                            
                                            info.sequenceNumber = systemItemsMap.size();
                                            if (xml5.getName().equals("string") && key5.equals("Name")) {
                                                info.name = xml5.getContent();
                                            }
                                            if (xml5.getName().equals("string") && key5.equals("Visibility")) {
                                                info.isVisible = xml5.getContent().equals("AlwaysVisible");
                                            }
                                        }
                                        if (info.name != null) {
                                            systemItemsMap.put(info.name, info);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (xml2.getName().equals("dict") && key2.equals("useritems")) {
                        for (Iterator i2 = xml2.iterateChildren(); i2.hasNext(); ) {
                            XMLElement xml3 = (XMLElement) i2.next();
                            for (Iterator i3 = xml3.iterateChildren(); i3.hasNext(); ) {
                                XMLElement xml4 = (XMLElement) i3.next();
                                String aliasName = null;
                                byte[] serializedAlias = null;
                                for (Iterator i4 = xml4.iterateChildren(); i4.hasNext(); ) {
                                    XMLElement xml5 = (XMLElement) i4.next();
                                    
                                    if (xml5.getName().equals("key")) {
                                        key5 = xml5.getContent();
                                    }
                                    if (xml5.getName().equals("string") && key5.equals("Name")) {
                                        aliasName = xml5.getContent();
                                    }
                                    if (! xml5.getName().equals("key") && key5.equals("Alias")) {
                                        serializedAlias = Base64.decode(xml5.getContent());
                                    }
                                }
                                if (serializedAlias != null && aliasName != null) {
                                    // Try to resolve the alias without user interaction
                                    File f = OSXFile.resolveAlias(serializedAlias, true);
                                    if (f != null) {
                                        userItems.add(new FileItem(f));
                                    } else {
                                        userItems.add(new AliasItem(serializedAlias, aliasName));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return new Object[] {systemItemsMap, userItems};
    }
    
    // Helper classes
    private class Row implements Comparable {
        private int modelIndex;
        
        public Row(int index) {
            this.modelIndex = index;
        }
        
        public int compareTo(Object o) {
            int row1 = modelIndex;
            int row2 = ((Row) o).modelIndex;
            
            AliasFileSystemTreeModel.Node o1 = ((AliasFileSystemTreeModel.Node) model.getChild(path.getLastPathComponent(), row1));
            AliasFileSystemTreeModel.Node o2 = ((AliasFileSystemTreeModel.Node) model.getChild(path.getLastPathComponent(), row2));
            
            SystemItemInfo i1 = (SystemItemInfo) systemItemsMap.get(o1.getUserName());
            if (i1 == null && o1.getResolvedFile().getName().equals("")) {
                i1 = (SystemItemInfo) systemItemsMap.get("Computer");
            }
            
            SystemItemInfo i2 = (SystemItemInfo) systemItemsMap.get(o2.getUserName());
            if (i2 == null && o2.getResolvedFile().getName().equals("")) {
                i2 = (SystemItemInfo) systemItemsMap.get("Computer");
            }
            
            if (i1 != null && i2 != null) {
                return i1.sequenceNumber - i2.sequenceNumber;
            }
            
            if (i1 != null) {
                return -1;
            }
            if (i2 != null) {
                return 1;
            }
            
            return row1 - row2;
        }
    }
}
