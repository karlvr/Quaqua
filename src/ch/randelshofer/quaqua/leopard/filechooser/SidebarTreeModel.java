/*
 * @(#)SidebarTreeModel.java  3.1  2008-05-09
 *
 * Copyright (c) 2007-2008 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package ch.randelshofer.quaqua.leopard.filechooser;

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
 * SidebarTreeModel.
 *
 * @author Werner Randelshofer
 * @version 3.1 2008-05-09 FileNode reads value of variable userName lazily. 
 * <br>3.0.1 2008-04-17 Method FileNode.getIcon fired wrong event. 
 * <br>3.0 2008-03-26 Method treeNodesChanged() must map model viewIndices
 * to view viewIndices for the change event that it fires on its own. Method 
 * updateSystemItem must attempt to merge existing view nodes, because the JTree
 * might have stored selection paths to the nodes.
 * <br>2.0 2007-11-24 Added support for Darwin. 
 * <br>1.0 November 10, 2007 Created.
 */
public class SidebarTreeModel extends DefaultTreeModel implements TreeModelListener {

    /**
     * This file contains information about the system list and holds the aliases
     * for the user list.
     */
    private final static File sidebarFile = new File(QuaquaManager.getProperty("user.home"), "Library/Preferences/com.apple.sidebarlists.plist");
    /**
     * Holds the tree volumesPath to the /Volumes folder.
     */
    private TreePath volumesPath;
    /**
     * Holds the AliasFileSystemTreeModel.
     */
    private TreeModel model;
    private DefaultMutableTreeNode devicesNode;
    private DefaultMutableTreeNode placesNode;
    /**
     * Intervals between validations.
     */
    private final static long VALIDATION_TTL = 60000;
    /**
     * Time for next validation of the model.
     */
    private long bestBefore;
    /**
     * The JFileChooser.
     */
    private JFileChooser fileChooser;
    /**
     * Sequential dispatcher for the lazy creation of icons.
     */
    private SequentialDispatcher dispatcher = new SequentialDispatcher();
    /**
     * This hash map is used to determine the sequence and visibility of the
     * items in the system list.
     * HashMap<String,SystemItemInfo>
     */
    private HashMap systemItemsMap = new HashMap();
    /**
     * This array holds the view to model mapping of the system items.
     */
    private Row[] viewToModel = new Row[0];
    /**
     * This array holds the model to view mapping of the system items.
     */
    private int[] modelToView = new int[0];
    private final static File[] defaultUserItems;

    static {
        if (QuaquaManager.isOSX() ||
                QuaquaManager.getOS() == QuaquaManager.DARWIN) {
            defaultUserItems = new File[]{
                new File(QuaquaManager.getProperty("user.home"), "Desktop"),
                new File(QuaquaManager.getProperty("user.home"), "Documents"),
                new File(QuaquaManager.getProperty("user.home"))
            };
        } else if (QuaquaManager.getOS() == QuaquaManager.WINDOWS) {
            defaultUserItems = new File[]{
                new File(QuaquaManager.getProperty("user.home"), "Desktop"),
                // Japanese ideographs for Desktop:
                new File(QuaquaManager.getProperty("user.home"), "\u684c\u9762"),
                new File(QuaquaManager.getProperty("user.home"), "My Documents"),
                new File(QuaquaManager.getProperty("user.home"))
            };
        } else {
            defaultUserItems = new File[]{
                new File(QuaquaManager.getProperty("user.home"))
            };
        }
    }

    /** Creates a new instance. */
    public SidebarTreeModel(JFileChooser fileChooser, TreePath path, TreeModel model) {
        super(new DefaultMutableTreeNode(), true);

        this.fileChooser = fileChooser;
        this.volumesPath = path;
        this.model = model;

        devicesNode = new DefaultMutableTreeNode(UIManager.getString("FileChooser.devices"));
        devicesNode.setAllowsChildren(true);
        placesNode = new DefaultMutableTreeNode(UIManager.getString("FileChooser.places"));
        placesNode.setAllowsChildren(true);

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
        root.add(devicesNode);
        root.add(placesNode);

        validate();
        updateSystemItems();

        model.addTreeModelListener(this);
    }

    public void lazyValidate() {
    // throw new UnsupportedOperationException("Not yet implemented");
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
                            System.err.println("Warning: SidebarTreeModel uses default user items.");
                            freshUserItems = new ArrayList(defaultUserItems.length);
                            for (int i = 0; i < defaultUserItems.length; i++) {
                                if (defaultUserItems[i] == null) {
                                    freshUserItems.add(null);
                                } else if (defaultUserItems[i].exists()) {
                                    freshUserItems.add(new FileNode(defaultUserItems[i]));
                                }
                            }
                        } else {
                            systemItemsMap = (HashMap) ((Object[]) value)[0];
                            freshUserItems = (ArrayList) ((Object[]) value)[1];
                        }

                        int systemItemsSize = model.getChildCount(volumesPath.getLastPathComponent());
                        int oldUserItemsSize = placesNode.getChildCount();
                        if (oldUserItemsSize > 0) {
                            int[] removedIndices = new int[oldUserItemsSize];
                            Object[] removedChildren = new Object[oldUserItemsSize];
                            for (int i = 0; i < oldUserItemsSize; i++) {
                                removedIndices[i] = i;
                                removedChildren[i] = placesNode.getChildAt(i);
                            }
                            placesNode.removeAllChildren();
                            fireTreeNodesRemoved(
                                    SidebarTreeModel.this,
                                    placesNode.getPath(),
                                    removedIndices,
                                    removedChildren);
                        }
                        if (freshUserItems.size() > 0) {
                            int[] insertedIndices = new int[freshUserItems.size()];
                            Object[] insertedChildren = new Object[freshUserItems.size()];
                            for (int i = 0; i < freshUserItems.size(); i++) {
                                insertedIndices[i] = i;
                                insertedChildren[i] = freshUserItems.get(i);
                                if (freshUserItems.get(i) == null) {
                                    placesNode.add(new DefaultMutableTreeNode("null?"));
                                } else {
                                    placesNode.add((DefaultMutableTreeNode) freshUserItems.get(i));
                                }
                            }
                            fireTreeNodesInserted(
                                    SidebarTreeModel.this,
                                    placesNode.getPath(),
                                    insertedIndices,
                                    insertedChildren);
                        }
                        bestBefore = System.currentTimeMillis() + VALIDATION_TTL;
                    }
                });
    }

    private void updateSystemItems() {
        AliasFileSystemTreeModel.Node parent = (AliasFileSystemTreeModel.Node) volumesPath.getLastPathComponent();
        if (modelToView.length != parent.getChildCount()) {
            viewToModel = new Row[parent.getChildCount()];
            modelToView = new int[viewToModel.length];
        }
        for (int i = 0; i < viewToModel.length; i++) {
            viewToModel[i] = new Row(i);
        }
        Arrays.sort(viewToModel);
        for (int i = 0; i < viewToModel.length; i++) {
            modelToView[viewToModel[i].modelIndex] = i;
        }

        // remove leaf nodes from system items
        {
            int j = 0;
            for (int i = 0; i < viewToModel.length; i++) {
                AliasFileSystemTreeModel.Node node = (AliasFileSystemTreeModel.Node) parent.getChildAt(viewToModel[i].modelIndex);
                if (!node.isLeaf()) {
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


        // Merge the nodes from the model with the view
        LinkedList mergedChildren = new LinkedList();
        for (int i = 0; i < viewToModel.length; i++) {
            AliasFileSystemTreeModel.Node target = (AliasFileSystemTreeModel.Node) parent.getChildAt(viewToModel[i].modelIndex);
            FileSystemModelNode mergedChild = null;
            for (int j = 0; j < devicesNode.getChildCount(); j++) {
                FileSystemModelNode node = (FileSystemModelNode) devicesNode.getChildAt(j);
                if (node.target == target) {
                    mergedChild = node;
                    devicesNode.remove(j);
                    break;
                }
            }
            if (mergedChild == null) {
                mergedChild = new FileSystemModelNode(target);
            }
            mergedChildren.add(mergedChild);
        }
        devicesNode.removeAllChildren();
        for (int i = 0; i < mergedChildren.size(); i++) {
            devicesNode.add((MutableTreeNode) mergedChildren.get(i));
        }
    }

    /**
     * Reads the sidebar preferences file.
     */
    private Object[] read() throws IOException {
        if (!Files.canWorkWithAliases()) {
            throw new IOException("Unable to work with aliases");
        }

        HashMap systemItemsMap = new HashMap();
        ArrayList userItems = new ArrayList();

        FileReader reader = null;
        try {
            reader = new FileReader(sidebarFile);
            XMLElement xml = new XMLElement(new HashMap(), false, false);
            try {
                xml.parseFromReader(reader);
            } catch (XMLParseException e) {
                xml = new BinaryPListParser().parse(sidebarFile);
            }
            String key2 = "", key3 = "", key5 = "";
            for (Iterator i0 = xml.iterateChildren(); i0.hasNext();) {
                XMLElement xml1 = (XMLElement) i0.next();

                for (Iterator i1 = xml1.iterateChildren(); i1.hasNext();) {
                    XMLElement xml2 = (XMLElement) i1.next();

                    if (xml2.getName().equals("key")) {
                        key2 = xml2.getContent();
                    }

                    if (xml2.getName().equals("dict") && key2.equals("systemitems")) {
                        for (Iterator i2 = xml2.iterateChildren(); i2.hasNext();) {
                            XMLElement xml3 = (XMLElement) i2.next();
                            if (xml3.getName().equals("key")) {
                                key3 = xml3.getContent();
                            }
                            if (xml3.getName().equals("array") && key3.equals("VolumesList")) {
                                for (Iterator i3 = xml3.iterateChildren(); i3.hasNext();) {
                                    XMLElement xml4 = (XMLElement) i3.next();

                                    if (xml4.getName().equals("dict")) {
                                        SystemItemInfo info = new SystemItemInfo();
                                        for (Iterator i4 = xml4.iterateChildren(); i4.hasNext();) {
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
                        for (Iterator i2 = xml2.iterateChildren(); i2.hasNext();) {
                            XMLElement xml3 = (XMLElement) i2.next();
                            for (Iterator i3 = xml3.iterateChildren(); i3.hasNext();) {
                                XMLElement xml4 = (XMLElement) i3.next();
                                String aliasName = null;
                                byte[] serializedAlias = null;
                                for (Iterator i4 = xml4.iterateChildren(); i4.hasNext();) {
                                    XMLElement xml5 = (XMLElement) i4.next();

                                    if (xml5.getName().equals("key")) {
                                        key5 = xml5.getContent();
                                    }
                                    if (xml5.getName().equals("string") && key5.equals("Name")) {
                                        aliasName = xml5.getContent();
                                    }
                                    if (!xml5.getName().equals("key") && key5.equals("Alias")) {
                                        serializedAlias = Base64.decode(xml5.getContent());
                                    }
                                }
                                if (serializedAlias != null && aliasName != null) {
                                    // Try to resolve the alias without user interaction
                                    File f = Files.resolveAlias(serializedAlias, true);
                                    if (f != null) {
                                        userItems.add(new FileNode(f));
                                    } else {
                                        userItems.add(new AliasNode(serializedAlias, aliasName));
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
        return new Object[]{systemItemsMap, userItems};
    }

    public void treeNodesChanged(TreeModelEvent e) {
        if (e.getTreePath().equals(volumesPath)) {
            // updateSystemItems();

            int[] modelIndices = e.getChildIndices();
            int[] viewIndices = new int[devicesNode.getChildCount()];
            Object[] viewChildren = new Object[viewIndices.length];
            for (int i = 0; i < modelIndices.length; i++) {
                int index = modelToView[modelIndices[i]];
                viewIndices[i] = index;
                viewChildren[i] = devicesNode.getChildAt(index);
            }

            // sort the changed items by their view indices
            for (int i = 1; i < viewIndices.length; i++) {
                int tmpI = viewIndices[i];
                Object tmpO = viewChildren[i];
                int j = i;
                while (j > 0 && viewIndices[j - 1] > tmpI) {
                    viewIndices[j] = viewIndices[j - 1];
                    viewChildren[j] = viewChildren[j - 1];
                    j--;
                }
                viewIndices[j] = tmpI;
                viewChildren[j] = tmpO;
            }

            fireTreeNodesChanged(this,
                    devicesNode.getPath(), viewIndices, viewChildren);
        }
    }

    public void treeNodesInserted(TreeModelEvent e) {
        if (e.getTreePath().equals(volumesPath)) {
            updateSystemItems();

            int[] modelIndices = e.getChildIndices();
            int[] viewIndices = new int[modelIndices.length];
            Object[] viewChildren = new Object[viewIndices.length];
            for (int i = 0; i < viewIndices.length; i++) {
                int index = modelToView[modelIndices[i]];
                viewIndices[i] = index;
                viewChildren[i] = devicesNode.getChildAt(index);
            }

            // sort the inserted items by their view indices
            for (int i = 1; i < viewIndices.length; i++) {
                int tmpI = viewIndices[i];
                Object tmpO = viewChildren[i];
                int j = i;
                while (j > 0 && viewIndices[j - 1] > tmpI) {
                    viewIndices[j] = viewIndices[j - 1];
                    viewChildren[j] = viewChildren[j - 1];
                    j--;
                }
                viewIndices[j] = tmpI;
                viewChildren[j] = tmpO;
            }

            fireTreeNodesInserted(this,
                    devicesNode.getPath(), viewIndices, viewChildren);
        }
    }

    public void treeNodesRemoved(TreeModelEvent e) {
        if (e.getTreePath().equals(volumesPath)) {
            int[] modelIndices = e.getChildIndices();
            Object[] oldViewChildren = new Object[devicesNode.getChildCount()];
            for (int i = 0; i < oldViewChildren.length; i++) {
                oldViewChildren[i] = devicesNode.getChildAt(i);
            }
            int[] oldModelToView = (int[]) modelToView.clone();

            updateSystemItems();

            int[] viewIndices = new int[modelIndices.length];
            Object[] viewChildren = new Object[viewIndices.length];
            for (int i = 0; i < viewIndices.length; i++) {
                int index = oldModelToView[modelIndices[i]];
                viewIndices[i] = index;
                viewChildren[i] = oldViewChildren[index];
            }

            // sort the inserted items by their view indices
            for (int i = 1; i < viewIndices.length; i++) {
                int tmpI = viewIndices[i];
                Object tmpO = viewChildren[i];
                int j = i;
                while (j > 0 && viewIndices[j - 1] > tmpI) {
                    viewIndices[j] = viewIndices[j - 1];
                    viewChildren[j] = viewChildren[j - 1];
                    j--;
                }
                viewIndices[j] = tmpI;
                viewChildren[j] = tmpO;
            }


            fireTreeNodesRemoved(this,
                    devicesNode.getPath(), viewIndices, viewChildren);
        }
    }

    public void treeStructureChanged(TreeModelEvent e) {
        if (e.getTreePath().equals(volumesPath)) {
            updateSystemItems();
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) devicesNode.getParent();
            int[] viewIndices = new int[devicesNode.getChildCount()];
            Object[] viewChildren = new Object[viewIndices.length];
            for (int i = 0; i < viewIndices.length; i++) {
                viewIndices[i] = i;
                viewChildren[i] = devicesNode.getChildAt(i);
            }
            fireTreeStructureChanged(this,
                    devicesNode.getPath(),
                    viewIndices,
                    viewChildren);
        /*
        fireTreeStructureChanged(this, 
        parent.getPath(),
        new int[] { parent.getIndex(devicesNode) },
        new Object[] { devicesNode }
        );*/
        }
    }

    private class FileNode extends Node {

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

        public FileNode(File file) {
            this.file = file;
           // userName = fileChooser.getName(file);
            isTraversable = true;
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

        public boolean allowsChildren() {
            return false;
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
                            int[] changedIndices = { getParent().getIndex(FileNode.this) };
                            Object[] changedChildren = { FileNode.this };
                            SidebarTreeModel.this.fireTreeNodesChanged(
                                    SidebarTreeModel.this,
                                    placesNode.getPath(),
                                    changedIndices, changedChildren);
                        }
                    });
                }
            }
            return icon;
        }

        public String getUserName() {
            if (userName == null) {
            userName = fileChooser.getName(file);
            }
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
     * An AliasNode is resolved as late as possible.
     */
    private abstract class Node extends DefaultMutableTreeNode implements FileInfo {

        public boolean getAllowsChildren() {
            return false;
        }
    }

    /**
     * An AliasNode is resolved as late as possible.
     */
    private class AliasNode extends Node {

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

        public AliasNode(byte[] serializedAlias, String aliasName) {
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
                file = Files.resolveAlias(serializedAlias, false);
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
                    // Note: We clear this icon, when we resolve the alias
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

                            int[] changedIndices = new int[]{getParent().getIndex(AliasNode.this)};
                            Object[] changedChildren = new Object[]{AliasNode.this};
                            SidebarTreeModel.this.fireTreeNodesChanged(
                                    SidebarTreeModel.this,
                                    ((DefaultMutableTreeNode) AliasNode.this.getParent()).getPath(),
                                    changedIndices, changedChildren);
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

    private static class SystemItemInfo {

        String name = "";
        int sequenceNumber = 0;
        boolean isVisible = true;
    }

    private class FileSystemModelNode extends Node {

        private AliasFileSystemTreeModel.Node target;

        public FileSystemModelNode(AliasFileSystemTreeModel.Node target) {
            this.target = target;
        }

        public File getFile() {
            return target.getFile();
        }

        public File getResolvedFile() {
            return target.getResolvedFile();
        }

        public File lazyGetResolvedFile() {
            return target.lazyGetResolvedFile();
        }

        public boolean isTraversable() {
            return target.isTraversable();
        }

        public boolean isAcceptable() {
            return target.isAcceptable();
        }

        public int getFileLabel() {
            return target.getFileLabel();
        }

        public String getUserName() {
            return target.getUserName();
        }

        public Icon getIcon() {
            return target.getIcon();
        }

        public long getFileLength() {
            return target.getFileLength();
        }

        public String getFileKind() {
            return target.getFileKind();
        }

        public boolean isValidating() {
            return target.isValidating();
        }

        public String toString() {
            return target.toString();
        }
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

            AliasFileSystemTreeModel.Node o1 = ((AliasFileSystemTreeModel.Node) model.getChild(volumesPath.getLastPathComponent(), row1));
            AliasFileSystemTreeModel.Node o2 = ((AliasFileSystemTreeModel.Node) model.getChild(volumesPath.getLastPathComponent(), row2));

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
