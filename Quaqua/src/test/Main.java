/*
 * @(#)Main.java
 * 
 * Copyright (c) 2009-2010 Werner Randelshofer
 * Hausmatt 10, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 * 
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms.
 */
package test;

import ch.randelshofer.quaqua.QuaquaManager;
import com.sun.java.swing.SwingUtilities3;
import java.security.AccessControlException;
import java.util.Arrays;
import java.util.HashSet;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

/**
 * Main.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Main extends javax.swing.JPanel {

    private static class Item extends DefaultMutableTreeNode {

        private String label;
        private String clazz;
        private JComponent component;

        public Item(String label, String clazz) {
            this.label = label;
            this.clazz = clazz;
        }

        @Override
        public String toString() {
            return label;
        }

        public JComponent getComponent() {
            if (component == null) {
                try {
                    component = (JComponent) Class.forName(clazz).newInstance();
                } catch (Throwable ex) {
                    component = new JLabel(ex.toString());
                    ex.printStackTrace();
                }
            }
            return component;
        }
    }

    public static void main(String[] args) {
        final long start = System.currentTimeMillis();

        final java.util.List argList = Arrays.asList(args);
        // Explicitly turn on font antialiasing.
        try {
            System.setProperty("swing.aatext", "true");
        } catch (AccessControlException e) {
            // can't do anything about this
        }

        // Use screen menu bar, if not switched off explicitly
        try {
            if (System.getProperty("apple.laf.useScreenMenuBar") == null
                    && System.getProperty("com.apple.macos.useScreenMenuBar") == null) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            }
        } catch (AccessControlException e) {
            // can't do anything about this
        }

        // Turn on look and feel decoration when not running on Mac OS X or Darwin.
        // This will still not look pretty, because we haven't got cast shadows
        // for the frame on other operating systems.
        boolean useDefaultLookAndFeelDecoration =
                !System.getProperty("os.name").toLowerCase().startsWith("mac")
                && !System.getProperty("os.name").toLowerCase().startsWith("darwin");
        int index = argList.indexOf("-decoration");
        if (index != -1 && index < argList.size() - 1) {
            useDefaultLookAndFeelDecoration = argList.get(index + 1).equals("true");
        }

        if (useDefaultLookAndFeelDecoration) {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }

        // Launch the test program
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                long edtEnd = System.currentTimeMillis();
                int index;
                index = argList.indexOf("-include");
                if (index != -1 && index < argList.size() - 1) {
                    HashSet includes = new HashSet();
                    includes.addAll(Arrays.asList(((String) argList.get(index + 1)).split(",")));

                    QuaquaManager.setIncludedUIs(includes);
                }
                index = argList.indexOf("-exclude");
                if (index != -1 && index < argList.size() - 1) {
                    HashSet excludes = new HashSet();
                    excludes.addAll(Arrays.asList(((String) argList.get(index + 1)).split(",")));

                    QuaquaManager.setExcludedUIs(excludes);
                }
                index = argList.indexOf("-laf");
                String lafName;
                if (index != -1 && index < argList.size() - 1) {
                    lafName = (String) argList.get(index + 1);
                } else {
                    lafName = QuaquaManager.getLookAndFeelClassName();
                }
                long lafCreate = 0;
                if (!lafName.equals("default")) {

                    if (lafName.equals("system")) {
                        lafName = UIManager.getSystemLookAndFeelClassName();
                    } else if (lafName.equals("crossplatform")) {
                        lafName = UIManager.getCrossPlatformLookAndFeelClassName();
                    }

                    try {
                        //UIManager.setLookAndFeel(lafName);
                        System.out.println("   CREATING LAF   " + lafName);

                        LookAndFeel laf = (LookAndFeel) Class.forName(lafName).newInstance();
                        lafCreate = System.currentTimeMillis();
                        System.out.println("   LAF CREATED   ");
                        System.out.println("   SETTING LAF  ");
                        UIManager.setLookAndFeel(laf);
                        System.out.println("   LAF SET   ");
                    } catch (Exception e) {
                        e.printStackTrace();
                        // can't do anything about this
                    }
                }
                long lafEnd = System.currentTimeMillis();
                JFrame f = new JFrame();
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setTitle(UIManager.getLookAndFeel().getName() + " "
                        + QuaquaManager.getVersion()
                        + " on Java " + System.getProperty("java.version")
                        + " " + System.getProperty("os.arch"));
                Main ex = new Main();
                f.add(ex);
                long createEnd = System.currentTimeMillis();
                //f.pack();
                f.setSize(740, 480);
                long packEnd = System.currentTimeMillis();
                f.setVisible(true);
                long end = System.currentTimeMillis();
                System.out.println("QuaquaTest EDT latency=" + (edtEnd - start));
                if (!lafName.equals("default")) {
                    System.out.println("QuaquaTest laf create latency=" + (lafCreate - edtEnd));
                    System.out.println("QuaquaTest set laf latency=" + (lafEnd - lafCreate));
                }
                System.out.println("QuaquaTest create latency=" + (createEnd - lafEnd));
                //System.out.println("Main pack latency  ="+(packEnd - createEnd));
                System.out.println("QuaquaTest total startup latency=" + (end - start));
            }
        });
    }

    /** Creates new form Main */
    public Main() {
        initComponents();
        treeScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        splitPane.setDividerSize(1);
        splitPane.putClientProperty("Quaqua.SplitPane.style", "bar");
        splitPane.setOneTouchExpandable(false);
        tree.putClientProperty("Quaqua.Tree.style", "sideBar");
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
       // tree.setRequestFocusEnabled(false);

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
        n.add(new Item("Spinner", "test.SpinnerTest"));
        n.add(new Item("Progress Bar", "test.ProgressBarTest"));
        n.add(new Item("Scroll Bar", "test.ScrollBarTest"));
        root.add(n = new DefaultMutableTreeNode("TEXT"));
        n.add(new Item("Editor Pane", "test.EditorPaneTest"));
        n.add(new Item("Formatted Text Field", "test.FormattedTextFieldTest"));
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
        n.add(new Item("Box", "test.BoxTest"));
        root.add(n = new DefaultMutableTreeNode("WINDOWS"));
        n.add(new Item("Desktop Pane", "test.DesktopPaneTest"));
        n.add(new Item("Root Pane", "test.RootPaneTest"));
        n.add(new Item("Popup Menu", "test.PopupMenuTest"));
        n.add(new Item("Tool Bar", "test.ToolBarTest"));
        n.add(new Item("Color Chooser", "test.ColorChooserTest"));
        n.add(new Item("File Chooser", "test.FileChooserTest"));
        n.add(new Item("OptionPane", "test.OptionPaneTest"));
        n.add(new Item("Dialog", "test.DialogTest"));
        n.add(new Item("Sheet", "test.SheetTest"));
        n.add(new Item("Palette", "test.PaletteTest"));
        root.add(n = new DefaultMutableTreeNode("LAYOUT"));
        n.add(new Item("Alignment", "test.AlignmentTest"));
        n.add(new Item("Margin", "test.VisualMarginTest"));
        n.add(new Item("Matisse J2SE5", "test.MatisseTest15"));
        n.add(new Item("Matisse J2SE6", "test.MatisseTest16"));
        n.add(new Item("Margin", "test.VisualMarginTest"));
        root.add(n = new DefaultMutableTreeNode("BEHAVIOR"));
        n.add(new Item("Drag and Drop", "test.DnDTest"));
        n.add(new Item("Input Verifier", "test.InputVerifierTest"));
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
        rightPane = new javax.swing.JPanel();
        viewPane = new javax.swing.JPanel();
        controlPanel = new javax.swing.JPanel();
        showClipBoundsBox = new javax.swing.JCheckBox();
        showVisualBoundsBox = new javax.swing.JCheckBox();

        FormListener formListener = new FormListener();

        setLayout(new java.awt.BorderLayout());

        splitPane.setDividerLocation(200);

        treeScrollPane.setMinimumSize(new java.awt.Dimension(0, 0));

        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        treeScrollPane.setViewportView(tree);

        splitPane.setLeftComponent(treeScrollPane);

        rightPane.setMinimumSize(new java.awt.Dimension(0, 1));
        rightPane.setLayout(new java.awt.BorderLayout());

        viewPane.setLayout(new java.awt.BorderLayout());
        rightPane.add(viewPane, java.awt.BorderLayout.CENTER);

        showClipBoundsBox.setText("Show Clip Bounds");
        showClipBoundsBox.addActionListener(formListener);
        controlPanel.add(showClipBoundsBox);

        showVisualBoundsBox.setText("Show Visual Bounds");
        showVisualBoundsBox.addActionListener(formListener);
        controlPanel.add(showVisualBoundsBox);

        rightPane.add(controlPanel, java.awt.BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPane);

        add(splitPane, java.awt.BorderLayout.CENTER);
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == showClipBoundsBox) {
                Main.this.showClipBounds(evt);
            }
            else if (evt.getSource() == showVisualBoundsBox) {
                Main.this.showVisualBounds(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    private void showClipBounds(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showClipBounds
        UIManager.put("Quaqua.Debug.showClipBounds", showClipBoundsBox.isSelected() ? Boolean.TRUE : Boolean.FALSE);
        repaint();
    }//GEN-LAST:event_showClipBounds

    private void showVisualBounds(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showVisualBounds
        UIManager.put("Quaqua.Debug.showVisualBounds", showVisualBoundsBox.isSelected() ? Boolean.TRUE : Boolean.FALSE);
        repaint();
    }//GEN-LAST:event_showVisualBounds
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel controlPanel;
    private javax.swing.JPanel rightPane;
    private javax.swing.JCheckBox showClipBoundsBox;
    private javax.swing.JCheckBox showVisualBoundsBox;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTree tree;
    private javax.swing.JScrollPane treeScrollPane;
    private javax.swing.JPanel viewPane;
    // End of variables declaration//GEN-END:variables
}
