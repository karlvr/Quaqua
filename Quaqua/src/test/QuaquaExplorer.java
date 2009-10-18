/*
 * @(#)QuaquaExplorer.java  1.0  2009-10-04
 * 
 * Copyright (c) 2009 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 * 
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms.
 */
package test;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

/**
 * QuaquaExplorer.
 *
 * @author Werner Randelshofer
 * @version 1.0 2009-10-04 Created.
 */
public class QuaquaExplorer extends javax.swing.JPanel {

    private static class Item extends DefaultMutableTreeNode {

        private String label;
        private String clazz;
        private JComponent component;

        public Item(String label, String clazz) {
            this.label = label;
            this.clazz = clazz;
        }

        public String toString() {
            return label;
        }

        public JComponent getComponent() {
            if (component == null) {
                try {
                    component = (JComponent) Class.forName(clazz).newInstance();
                } catch (Exception ex) {
                    component = new JLabel(ex.toString());
                }
            }
            return component;
        }
    }

    /** Creates new form QuaquaExplorer */
    public QuaquaExplorer() {
        initComponents();
        treeScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        splitPane.setDividerSize(1);
        splitPane.putClientProperty("Quaqua.SplitPane.style", "bar");
        tree.putClientProperty("Quaqua.Tree.style", "sideBar");
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setRequestFocusEnabled(false);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultMutableTreeNode n;
        root.add(n = new DefaultMutableTreeNode("BUTTONS"));
        n.add(new Item("Push Button", "test.PushButtonTest"));
        n.add(new Item("Special Buttons", "test.SpecialButtonTest"));
        n.add(new Item("Toggle Button", "test.ToggleButtonTest"));
        n.add(new Item("Check Box", "test.CheckBoxTest"));
        n.add(new Item("Radio Button", "test.RadioButtonTest"));
        n.add(new Item("Combo Box", "test.ComboBoxTest"));
        n.add(new Item("Editable Combo Box", "test.EditableComboBoxTest"));
        root.add(n = new DefaultMutableTreeNode("ADJUSTORS"));
        n.add(new Item("Slider", "test.SliderTest"));
        n.add(new Item("Spinner", "test.SpinnerTest14"));
        n.add(new Item("Progress Bar", "test.ProgressBarTest14"));
        n.add(new Item("Scroll Bar", "test.ScrollBarTest"));
        root.add(n = new DefaultMutableTreeNode("TEXT"));
        n.add(new Item("Editor Pane", "test.EditorPaneTest"));
        n.add(new Item("Formatted Text Field", "test.FormattedTextFieldTest14"));
        n.add(new Item("Password Field", "test.PasswordFieldTest"));
        n.add(new Item("Text Area", "test.TextAreaTest"));
        n.add(new Item("Text Field", "test.TextFieldTest"));
        n.add(new Item("Text Pane", "test.TextPaneTest"));
        root.add(n = new DefaultMutableTreeNode("VIEWS"));
        n.add(new Item("List", "test.ListTest"));
        n.add(new Item("Table", "test.TableTest"));
        n.add(new Item("Tree", "test.TreeTest"));
        n.add(new Item("Scroll Pane", "test.ScrollPaneTest"));
        n.add(new Item("Browser", "test.BrowserTest"));
        root.add(n = new DefaultMutableTreeNode("GROUPING"));
        n.add(new Item("Scrollable Tabbed Pane", "test.TabbedPaneTestScroll"));
        n.add(new Item("Wrapped Tabbed Pane", "test.TabbedPaneTestWrap"));
        n.add(new Item("Split Pane", "test.SplitPaneTest"));
        n.add(new Item("Border", "test.BorderTest"));
        n.add(new Item("Box", "test.BoxTest14"));
        root.add(n = new DefaultMutableTreeNode("WINDOWS"));
        n.add(new Item("Desktop Pane", "test.DesktopPaneTest"));
        n.add(new Item("Root Pane", "test.RootPaneTest"));
        n.add(new Item("Popup Menu", "test.PopupMenuTest"));
        n.add(new Item("Tool Bar", "test.ToolBarTest"));
        n.add(new Item("Color Chooser", "test.ColorChooserTest"));
        n.add(new Item("File Chooser", "test.FileChooserTest"));
        n.add(new Item("OptionPane", "test.OptionPaneTest"));
        n.add(new Item("Dialog", "test.DialogTest"));
        n.add(new Item("Sheet", "test.SheetTest14"));
        n.add(new Item("Palette", "test.PaletteTest14"));
        root.add(n = new DefaultMutableTreeNode("LAYOUT"));
        n.add(new Item("Alignment", "test.AlignmentTest"));
        n.add(new Item("Margin", "test.VisualMarginTest"));
        root.add(n = new DefaultMutableTreeNode("BEHAVIOR"));
        n.add(new Item("Drag and Drop", "test.DnDTest14"));
        n.add(new Item("Input Verifier", "test.InputVerifierTest"));
        n.add(new Item("Focus Traversal", "test.FocusTraversalTest"));
        root.add(n = new DefaultMutableTreeNode("NATIVE CODE"));
        n.add(new Item("File System", "test.FileSystemTest"));
        n.add(new Item("Clipboard", "test.ClipboardTest"));
        DefaultTreeModel tm = new DefaultTreeModel(root);
        tree.setModel(tm);

        tree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = tree.getSelectionPath();
                viewPane.removeAll();
                if (path != null && path.getPathCount() > 0 && (path.getLastPathComponent() instanceof Item)) {
                    viewPane.add(((Item) path.getLastPathComponent()).getComponent());
                }
                viewPane.revalidate();
                viewPane.repaint();
            }
        });

        for (int i = tree.getRowCount(); i >= 0; i--) {
            tree.expandRow(i);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
  
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        treeScrollPane = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        viewPane = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        splitPane.setDividerLocation(200);

        treeScrollPane.setMinimumSize(new java.awt.Dimension(0, 0));

        tree.setRootVisible(false);
        treeScrollPane.setViewportView(tree);

        splitPane.setLeftComponent(treeScrollPane);

        viewPane.setMinimumSize(new java.awt.Dimension(0, 1));
        viewPane.setLayout(new java.awt.BorderLayout());
        splitPane.setRightComponent(viewPane);

        add(splitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTree tree;
    private javax.swing.JScrollPane treeScrollPane;
    private javax.swing.JPanel viewPane;
    // End of variables declaration//GEN-END:variables
}
