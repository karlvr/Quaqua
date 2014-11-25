/*
 * Copyright (c) 2014 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */
package ch.randelshofer.quaqua;

import de.sciss.treetable.j.TreeTable;
import de.sciss.treetable.j.TreeTableCellRenderer;
import de.sciss.treetable.j.ui.BasicTreeTableUI;

import javax.swing.*;
import javax.swing.plaf.TableUI;
import javax.swing.tree.TreePath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Customize the tree table UI. The internal tree and table components should not be focusable themselves, but they
 * should use inactive colors based on the focus state of the tree table. The table (not the tree) is fully responsible
 * for painting the selection background. The selection background must be repainted when the focus state changes.
 */

public class QuaquaTreeTableUI extends BasicTreeTableUI {

    protected MyHandler handler;

    public QuaquaTreeTableUI() {
        handler = new MyHandler();
    }

    @Override
    protected Handler createHandler() {
        return handler;
    }

    @Override
    protected JTree createAndConfigureTree() {
        JTree tree = super.createAndConfigureTree();
        tree.setFocusable(false);
        // Let the focus state of the tree table determine when to use inactive colors in the tree.
        tree.putClientProperty("Quaqua.Component.cellRendererFor", treeTable);
        // Prevent the tree from painting selection backgrounds.
        tree.putClientProperty("Tree.paintSelectionBackground", false);
        return tree;
    }

    @Override
    protected JTable createAndConfigureTable() {
        JTable table = super.createAndConfigureTable();
        table.setFocusable(false);
        // Let the focus state of the tree table determine when to use inactive colors in the tree.
        table.putClientProperty("Quaqua.Component.cellRendererFor", treeTable);
        return table;
    }

    @Override
    protected void finishConfiguration(JTree tree, JTable table) {
        // The tree does not paint the selection background, the table does.
        // If the tree is asked to repaint the selection, it should delegate to the table.
        // This is not actually needed at present, but who knows about the future?
        tree.putClientProperty("Tree.selectionRepainter", table);
    }

    @Override
    protected TreeTableCellRenderer createFocusRenderer() {
        return null;
    }

    @Override
    protected boolean hasTreeHandle(TreeTable treeTable, TreePath path) {
        return !treeTable.isLeaf(path);
    }

    @Override
    protected List<String> getProperties() {
        List<String> props = new ArrayList<String>(super.getProperties());
        props.add("Frame.active");
        return props;
    }

    protected PropertyChangeListener createPropertyChangeListener() {
   		return new MyPropertyChangeListener();
   	}

    protected class MyPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent event) {
            handler.propertyChange(event);
        }
    }

    protected class MyHandler extends Handler {

        @Override
        protected void focusChanged() {
            repaintSelection();
        }

        @Override
      	public void propertyChange(PropertyChangeEvent evt) {
            super.propertyChange(evt);
            if ("Frame.active".equals(evt.getPropertyName())) {
                // Need to propagate manually because the tree and table are not child components
                getTree().putClientProperty("Frame.active", evt.getNewValue());
                getTable().putClientProperty("Frame.active", evt.getNewValue());
            }
        }
    }

    public void repaintSelection() {
        TableUI tableUI = getTable().getUI();
        if (tableUI instanceof SelectionRepaintable) {
            SelectionRepaintable sp = (SelectionRepaintable) tableUI;
            sp.repaintSelection();
        }
    }

    protected void handleDoubleClick(TreePath path) {
    }
}
