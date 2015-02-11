/*
 * Copyright (c) 2014 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.lion.filechooser;

import ch.randelshofer.quaqua.BrowserPreviewRenderer;
import ch.randelshofer.quaqua.JBrowser;
import ch.randelshofer.quaqua.QuaquaScrollPaneUI;
import ch.randelshofer.quaqua.filechooser.CellRenderer;
import ch.randelshofer.quaqua.filechooser.ColumnView;
import ch.randelshofer.quaqua.filechooser.FileSystemTreeModel;
import ch.randelshofer.quaqua.filechooser.SubtreeTreeModel;
import ch.randelshofer.quaqua.leopard.filechooser.QuaquaFileChooserBrowser;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The file chooser column view for Lion (and other releases by subclassing).
 */

public class LionColumnView extends ColumnView {

    protected final JFileChooser fc;
    protected final QuaquaFileChooserBrowser browser;
    protected final JScrollPane browserScrollPane;
    private final MouseListener mouseListener;
    private final TreeSelectionListener treeSelectionListener;
    private boolean isActive;

    public LionColumnView(JFileChooser fc) {
        this.fc = fc;

        mouseListener = createDoubleClickListener();
        treeSelectionListener = new MyTreeSelectionListener();

        setFocusable(false);

        browser = new QuaquaFileChooserBrowser(fc) {
            @Override
            protected void fileSelectedInSavePanel(File f) {
                if (isActive) {
                    SubtreeTreeModel model = (SubtreeTreeModel) getModel();
                    FileSystemTreeModel fullModel = (FileSystemTreeModel) model.getTargetModel();
                    TreePath path = fullModel.toPath(f, null);
                    select(path);
                }
            }
        };

        browser.setFixedCellWidth(170);
        browser.setShowCellTipOrigin((Point) UIManager.get("FileChooser.cellTipOrigin"));
        browser.setShowCellTips(true);
        browser.setPreviewColumnFilled(true);

        browserScrollPane = new JScrollPane();
        browserScrollPane.setUI(new QuaquaScrollPaneUI());  // for file chooser only JAR
        browserScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        browserScrollPane.setViewportView(browser);
        browserScrollPane.putClientProperty("Quaqua.Component.visualMargin", new Insets(3, 2, 3, 2));
        //browserScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        browserScrollPane.setFocusable(false);
        browserScrollPane.getVerticalScrollBar().setFocusable(false);
        browserScrollPane.getHorizontalScrollBar().setFocusable(false);
        browserScrollPane.getViewport().setLayout(new FillingViewportLayout());

        setLayout(new BorderLayout());
        add(browserScrollPane);
    }

    @Override
    public void setActive(boolean b) {
        this.isActive = b;
        if (b) {
            browser.addMouseListener(mouseListener);
            browser.addTreeSelectionListener(treeSelectionListener);
        } else {
            browser.removeMouseListener(mouseListener);
            browser.removeTreeSelectionListener(treeSelectionListener);
        }
    }

    @Override
    public void setModel(SubtreeTreeModel m) {
        browser.setModel(m);
        FileSystemTreeModel fullModel = (FileSystemTreeModel) m.getTargetModel();
        browser.setPrototypeCellValue(fullModel.getPrototypeValue());
    }

    @Override
    public void setFileRenderer(CellRenderer r) {
        browser.setColumnCellRenderer((ListCellRenderer) r);
    }

    @Override
    public void setMultipleSelection(boolean b) {
        if (b) {
            browser.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        } else {
            browser.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        }
    }

    @Override
    public List<TreePath> getSelection() {
        TreePath[] ps = browser.getSelectionPaths();
        return ps != null ? new ArrayList<TreePath>(Arrays.asList(ps)) : new ArrayList<TreePath>();
    }

    @Override
    public void setSelection(TreePath path) {
        browser.setSelectionPath(path);
    }

    @Override
    public void setSelection(List<TreePath> paths) {
        TreePath[] ps = paths.toArray(new TreePath[paths.size()]);
        browser.setSelectionPaths(ps);
    }

    @Override
    public void ensurePathIsVisible(TreePath path) {
        browser.ensurePathIsVisible(path);
    }

    private void installPreviewComponent() {
        final Component pv = (Component) fc.getClientProperty("Quaqua.FileChooser.preview");
        if (pv != null) {
            browser.setPreviewRenderer(new BrowserPreviewRenderer() {
                public Component getPreviewRendererComponent(JBrowser browser, TreePath[] paths) {
                    return pv;
                }
            });
            browser.setPreviewColumnWidth(Math.max(browser.getFixedCellWidth(), pv.getPreferredSize().width));
        } else {
            boolean isSave = isFileNameFieldVisible();
            browser.setPreviewRenderer((isSave) ? null : createFilePreview(fc));
            browser.setPreviewColumnWidth(browser.getFixedCellWidth());
        }
    }

    @Override
    public boolean requestFocusInWindow() {
        return browser.requestFocusInWindow();
    }

    @Override
    public synchronized void addKeyListener(KeyListener l) {
        browser.addKeyListener(l);
    }

    @Override
    public void reconfigure() {
        setMultipleSelection(fc.isMultiSelectionEnabled());
        browser.repaint();
        installPreviewComponent();
        browser.updatePreviewColumn();
    }

    @Override
    public synchronized void setDropTarget(DropTarget dt) {
        super.setDropTarget(dt);
        browser.setDropTarget(dt);
    }

    protected MouseListener createDoubleClickListener() {
        return new DoubleClickListener();
    }

    protected class MyTreeSelectionListener implements TreeSelectionListener {
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            selectionChanged();
        }
    }

    protected class DoubleClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            // Note: We must not react on mouse clicks with clickCount=1.
            //       Because this interferes with the mouse handling code in
            //       the JBrowser which does list selection.
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2 && fc.getDialogType() != JFileChooser.SAVE_DIALOG) {

                TreePath path = browser.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    // Only react on double click if all selected files are
                    // acceptable
                    for (TreePath tp : browser.getSelectionPaths()) {
                        FileSystemTreeModel.Node n = (FileSystemTreeModel.Node) tp.getLastPathComponent();
                        if (!fc.accept(n.getFile())) {
                            return;
                        }
                    }
                    LionColumnView.this.select(path);
                }
            }
        }
    }

    /**
     * Returns true, if the file name field is visible.
     */
    protected boolean isFileNameFieldVisible() {
        return (fc.getDialogType() == JFileChooser.SAVE_DIALOG) || (fc.getDialogType() == JFileChooser.CUSTOM_DIALOG);
    }

    protected BrowserPreviewRenderer createFilePreview(JFileChooser fc) {
        return new LionFilePreview(fc);
    }

    private class FillingViewportLayout extends javax.swing.ViewportLayout {

        /*
          Custom layout: If we are filling the preview column, then widen the view as needed to fill the viewport. This
          is like a conditional viewTracksViewportWidth.
        */

        @Override
        public void layoutContainer(Container parent) {
            super.layoutContainer(parent);

            if (browser.isPreviewColumnFilled()) {
                JViewport vp = (JViewport) parent;
                Dimension viewSize = vp.getViewSize();  // should match the preferred width
                Dimension extentSize = vp.getExtentSize();
                if (extentSize.width > viewSize.width) {
                    Dimension size = new Dimension(extentSize.width, viewSize.height);
                    vp.setViewSize(size);
                }
            }
        }

        /*
          The following method is not currently useful.
          QuaquaLionFileChooserUI sets a minimum size on the JSplitPane.
          Note that there is no way to control the minimum size of a dialog created by JFileChooser.
        */

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return super.preferredLayoutSize(parent);
        }
    }
}
