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
import javax.swing.tree.TreePath;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**

*/

public class QuaquaTreeTableUI extends BasicTreeTableUI {

    protected MyHandler handler;
    protected MyTableUI tableUI;
    protected MyTreeUI treeUI;

    public QuaquaTreeTableUI() {
        handler = new MyHandler();
        tableUI = new MyTableUI();
        treeUI = new MyTreeUI();
    }

    @Override
    protected Handler createHandler() {
        return handler;
    }

    @Override
    protected JTree createAndConfigureTree() {
        JTree tree = super.createAndConfigureTree();
        tree.setFocusable(false);
        tree.setUI(treeUI);
        return tree;
    }

    @Override
    protected JTable createAndConfigureTable() {
        JTable table = super.createAndConfigureTable();
        table.setFocusable(false);
        table.setUI(tableUI);
        return table;
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

        /*
          The background of the selected row depends upon the window active state.
          It shouldn't - focus should be enough.
        */

        public void propertyChange(PropertyChangeEvent event) {
//            String name = event.getPropertyName();
//            if (event.getSource() == treeTable) {
//                if (name != null && name.equals("Frame.active")) {
//                    System.err.println("Repainting list view on Frame.active change");  // debug
//                    treeTable.repaint();
//                }
//            }
            handler.propertyChange(event);
        }
    }

    protected class MyHandler extends Handler {

        @Override
        protected void focusChanged() {
            treeUI.repaintSelection();
            tableUI.repaintSelection();
        }
    }

    protected class MyTreeUI extends QuaquaTreeUI {
        protected MyHandler handler;

        @Override
        protected QuaquaTreeUI.Handler createHandler() {
            return handler = new MyHandler();
        }

        @Override
        protected boolean shouldDisplayAsFocused(Component c) {
            return QuaquaUtilities.isFocused(treeTable);
        }

        @Override
        protected boolean shouldPaintSelectionBackground(Component c) {
            return false;
        }

        public void repaintSelection() {
            tableUI.repaintSelection();
        }

        protected class MyHandler extends QuaquaTreeUI.Handler {
        }
    }

    protected class MyTableUI extends QuaquaTableUI {
        protected MyHandler handler;

        @Override
        protected Handler createHandler() {
            return handler = new MyHandler();
        }

        public void repaintSelection() {
            handler.repaintSelection();
        }

        @Override
        protected boolean isFocused() {
            return table.isEditing() || QuaquaUtilities.isFocused(treeTable);
        }

        protected class MyHandler extends Handler {
        }
    }

    protected void handleDoubleClick(TreePath path) {
    }
}
