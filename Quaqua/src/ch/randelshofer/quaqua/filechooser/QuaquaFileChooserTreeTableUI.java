/*
 * Copyright (c) 2014 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.filechooser;

import ch.randelshofer.quaqua.QuaquaTreeTableUI;
import ch.randelshofer.quaqua.TreeTableModel;
import ch.randelshofer.quaqua.util.ViewportPainter;
import de.sciss.treetable.j.TreeTable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 *
 */

public abstract class QuaquaFileChooserTreeTableUI extends QuaquaTreeTableUI implements ViewportPainter {

    protected QuaquaFileChooserListMouseBehavior mouseBehavior;

    public QuaquaFileChooserTreeTableUI(JFileChooser fc, final TreeTable tt) {
        mouseBehavior = new QuaquaFileChooserListMouseBehavior(fc, new TreeTableModel(tt));
        mouseBehavior.setFileSelectionHandler(new QuaquaFileChooserListMouseBehavior.FileSelectionHandler() {
            @Override
            public void fileSelected(File f) {
                SubtreeTreeModel model = (SubtreeTreeModel) tt.getTreeModel();
                FileSystemTreeModel fullModel = (FileSystemTreeModel) model.getTargetModel();
                TreePath path = fullModel.toPath(f, null);
                QuaquaFileChooserTreeTableUI.this.select(path);
            }
        });
    }

    @Override
    protected JTree createAndConfigureTree() {
        JTree tree = super.createAndConfigureTree();
        tree.putClientProperty("Quaqua.Tree.isCellFilled", true);
        return tree;
    }

    @Override
    protected JTable createAndConfigureTable() {
        JTable table = super.createAndConfigureTable();
        table.putClientProperty("Quaqua.Table.style", "striped");
        return table;
    }

    @Override
    public void paintViewport(Graphics g, JViewport c) {
        tableUI.paintViewport(g, c);
    }

    @Override
    protected Handler createHandler() {
        return new MyHandler();
    }

    @Override
    protected void installListeners() {
        super.installListeners();

        // Avoid conflict with Cmd-Shift-A in the file chooser
        {
            JTable table = getTable();
            InputMap map = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).getParent();
            KeyStroke ks = KeyStroke.getKeyStroke("shift meta A");
            Object v = map.get(ks);
            if (v != null && v.equals("clearSelection")) {  // defined in BasicQuaquaNativeLookAndFeel
                InputMap newMap = new InputMap();
                newMap.setParent(map);
                newMap.put(ks, "selectApplicationsFolder"); // dummy name for now
                SwingUtilities.replaceUIInputMap(table, JComponent.WHEN_FOCUSED, newMap);
            }
        }

        {
            JTree tree = getTree();
            InputMap map = tree.getInputMap(JComponent.WHEN_FOCUSED).getParent();
            KeyStroke ks = KeyStroke.getKeyStroke("shift meta A");
            Object v = map.get(ks);
            if (v != null && v.equals("clearSelection")) {  // defined in BasicQuaquaNativeLookAndFeel
                InputMap newMap = new InputMap();
                newMap.setParent(map);
                newMap.put(ks, "selectApplicationsFolder"); // dummy name for now
                SwingUtilities.replaceUIInputMap(tree, JComponent.WHEN_FOCUSED, newMap);
            }
        }
    }

    protected class MyHandler extends QuaquaTreeTableUI.MyHandler {

        @Override
        protected void dispatchMouseEvent(MouseEvent e, JComponent c) {

            int id = e.getID();
            if (id == MouseEvent.MOUSE_CLICKED && e.getClickCount() == 2) {
                TreePath path = treeTable.getPathForLocation(e.getX(), e.getY());
                if (path != null) {
                    handleDoubleClick(path);
                    e.consume();
                    return;
                }
            }

            JTable table = getTable();
            if (c == table) {
                mouseBehavior.processMouseEvent(e);
                e.consume();
                return;
            }

            super.dispatchMouseEvent(e, c);
        }
    }

    protected void handleDoubleClick(TreePath path) {
        Object node = path.getLastPathComponent();
        if (node instanceof FileInfo) {
            FileInfo info = (FileInfo) node;
            if (info.isAcceptable() || info.isTraversable()) {
                select(path);
            }
        }
    }

    protected abstract void select(TreePath path);
}
