/*
 * @(#)Main.java  1.0  13 February 2005
 *
 * Copyright (c) 2004 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package test;

import ch.randelshofer.quaqua.*;
import ch.randelshofer.quaqua.filechooser.QuaquaFileSystemView;
import ch.randelshofer.quaqua.leopard.filechooser.LeopardFileRenderer;
import ch.randelshofer.quaqua.leopard.filechooser.OSXLeopardFileSystemView;
import ch.randelshofer.quaqua.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URLClassLoader;
import java.security.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import java.util.*;

/**
 * Main.
 *
 * @author  Werner Randelshofer
 * @version 1.0  13 February 2005  Created.
 */
public class Main extends javax.swing.JFrame {

    Dimension preferredSize;

    /** Creates new form. */
    public Main() {
        /*
        SwingUtilities.invokeLater(new Runnable() {
        public void run() { init(); }
        });*/
        //getRootPane().putClientProperty("apple.awt.delayWindowOrdering", Boolean.TRUE);
        init();
    //setResizable(false);
    }

    public void init() {
        initComponents();

        // setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        });


        tabbedPane.putClientProperty("Quaqua.Component.visualMargin", new Insets(3, -5, -4, -5));
        tabbedPane.add(new LazyPanel("test.ButtonsTest"), "Buttons");
        tabbedPane.add(new LazyPanel("test.AdjustmentControlsTest"), "Adjustors");
        tabbedPane.add(new LazyPanel("test.TextControlsTest"), "Text");
        tabbedPane.add(new LazyPanel("test.ViewControlsTest"), "Views");
        tabbedPane.add(new LazyPanel("test.GroupingControlsTest"), "Grouping");
        tabbedPane.add(new LazyPanel("test.WindowsTest"), "Windows");
        tabbedPane.add(new LazyPanel("test.LayoutTest"), "Layout");
        tabbedPane.add(new LazyPanel("test.BehaviorsTest"), "Behavior");
        tabbedPane.add(new LazyPanel("test.NativeTest"), "Native");
        Methods.invokeIfExists(tabbedPane, "setTabLayoutPolicy", 1); // JTabbedPane.SCROLL_TAB_LAYOUT);

        Component[] panes = tabbedPane.getComponents();
        for (int i = 0; i < panes.length; i++) {
            JComponent c = (JComponent) panes[i];
            if (!(c instanceof UIResource)) {
                c.setBorder(new EmptyBorder(12, 20, 20, 20));
            }
        }
        setTitle(UIManager.getLookAndFeel().getName() + " " +
                QuaquaManager.getVersion() +
                " on Java " + System.getProperty("java.version"));

        jMenuItem1.setAccelerator(KeyStroke.getKeyStroke("meta I"));
        jMenuItem1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                JOptionPane.showMessageDialog(Main.this, "jMenuItem 1");
            }
        });
    }

    public void setPreferredSize(Dimension d) {
        preferredSize = d;
    }

    public Dimension getPreferredSize() {
        if (preferredSize != null) {
            return (Dimension) preferredSize.clone();
        } else {
            return super.getPreferredSize();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        clipRectCheckBox = new javax.swing.JCheckBox();
        layoutRectCheckBox = new javax.swing.JCheckBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        tabbedPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(11, 0, 0, 0));
        getContentPane().add(tabbedPane, java.awt.BorderLayout.CENTER);

        clipRectCheckBox.setText("Show Clip Rects");
        clipRectCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                clipRectStateChanged(evt);
            }
        });
        jPanel1.add(clipRectCheckBox);

        layoutRectCheckBox.setText("Show Layout Rects");
        layoutRectCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                layoutRectStateChanged(evt);
            }
        });
        jPanel1.add(layoutRectCheckBox);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        jMenu1.setText("Menu");

        jMenuItem1.setText("Item");
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void layoutRectStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_layoutRectStateChanged
        UIManager.put("Quaqua.Debug.showVisualBounds", new Boolean(evt.getStateChange() == evt.SELECTED));
        repaint();
    }//GEN-LAST:event_layoutRectStateChanged

    private void clipRectStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_clipRectStateChanged
        UIManager.put("Quaqua.Debug.showClipBounds", new Boolean(evt.getStateChange() == evt.SELECTED));
        repaint();
    }//GEN-LAST:event_clipRectStateChanged
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args ) {
//System.out.println("Classpath:"+System.getProperty("java.class.path"));        
        final long start = System.currentTimeMillis();
        
        // System.setSecurityManager(new StrictSecurityManager());
        
        //System.out.println("class-path=\n"+System.getProperty("java.class.path").replace(':','\n'));
        
        
        final java.util.List argList = Arrays.asList(args);
        // Explicitly turn on font antialiasing.
        try {
            System.setProperty("swing.aatext", "true");
        } catch (AccessControlException e) {
            // can't do anything about this
        }
        
        // Use screen menu bar, if not switched off explicitly
        try {
            if (System.getProperty("apple.laf.useScreenMenuBar") == null &&
                    System.getProperty("com.apple.macos.useScreenMenuBar") == null) {
                System.setProperty("apple.laf.useScreenMenuBar","true");
                System.setProperty("com.apple.macos.useScreenMenuBar","true");
            }
        } catch (AccessControlException e) {
            // can't do anything about this
        }
        
        // Turn on look and feel decoration when not running on Mac OS X or Darwin.
        // This will still not look pretty, because we haven't got cast shadows
        // for the frame on other operating systems.
        boolean useDefaultLookAndFeelDecoration =
                ! System.getProperty("os.name").toLowerCase().startsWith("mac") &&
                ! System.getProperty("os.name").toLowerCase().startsWith("darwin")
                ;
        int index = argList.indexOf("-decoration");
        if (index != -1 && index < argList.size() - 1) {
            useDefaultLookAndFeelDecoration = argList.get(index + 1).equals("true");
        }
        
        if (useDefaultLookAndFeelDecoration) {
            try {
                Methods.invokeStatic(JFrame.class, "setDefaultLookAndFeelDecorated", Boolean.TYPE, Boolean.TRUE);
                Methods.invokeStatic(JDialog.class, "setDefaultLookAndFeelDecorated", Boolean.TYPE, Boolean.TRUE);
            } catch (NoSuchMethodException e) {
                // can't do anything about this
                e.printStackTrace();
            }
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
                if (! lafName.equals("default")) {
                    
                    if (lafName.equals("system")) {
                        lafName = UIManager.getSystemLookAndFeelClassName();
                    } else if (lafName.equals("crossplatform")) {
                        lafName = UIManager.getCrossPlatformLookAndFeelClassName();
                    }
                    
                    try {
                        //UIManager.setLookAndFeel(lafName);
                        System.out.println("   CREATING LAF   "+lafName);

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
                Main f = new Main();
                long createEnd = System.currentTimeMillis();
                //f.pack();
                f.setSize(640,640);
                long packEnd = System.currentTimeMillis();
                f.setVisible(true);
                long end = System.currentTimeMillis();
                System.out.println("QuaquaTest EDT latency="+(edtEnd - start));
                if (! lafName.equals("default")) {
                    System.out.println("QuaquaTest laf create latency="+(lafCreate - edtEnd));
                    System.out.println("QuaquaTest set laf latency="+(lafEnd - lafCreate));
                }
                System.out.println("QuaquaTest create latency="+(createEnd - lafEnd));
                //System.out.println("Main pack latency  ="+(packEnd - createEnd));
                System.out.println("QuaquaTest total startup latency="+(end - start));
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox clipRectCheckBox;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JCheckBox layoutRectCheckBox;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
    
}
