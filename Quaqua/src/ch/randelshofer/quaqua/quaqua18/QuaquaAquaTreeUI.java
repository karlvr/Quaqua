package ch.randelshofer.quaqua.quaqua18;

import com.apple.laf.AquaTreeUI;

import javax.swing.*;
import javax.swing.plaf.UIResource;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.TooManyListenersException;

/**
 * A base class that reveals or simulates needed parts of AquaTreeUI.
 */

public class QuaquaAquaTreeUI extends AquaTreeUI {

    // TBD: this needs to influence the behavior of the basic property change listener

    /** If true, the property change event for LEAD_SELECTION_PATH_PROPERTY,
     * or ANCHOR_SELECTION_PATH_PROPERTY will not generate a repaint. */
    private boolean ignoreLAChange;

    @Override
    protected PropertyChangeListener createPropertyChangeListener() {
        PropertyChangeListener base = super.createPropertyChangeListener();
        return new MyPropertyChangeListener(base);
    }

    protected class MyPropertyChangeListener
        implements PropertyChangeListener
    {
        private PropertyChangeListener base;

        public MyPropertyChangeListener(PropertyChangeListener base) {
            this.base = base;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();

            if (name == null) {
                return;
            }

            // The following imitates BasicTreeUI because we have own ignoreLAChange variable.

            if (evt.getSource() == tree) {
                if (name.equals(JTree.LEAD_SELECTION_PATH_PROPERTY) && ignoreLAChange) {
                    return;
                }

                if (name.equals(JTree.ANCHOR_SELECTION_PATH_PROPERTY) && ignoreLAChange) {
                    return;
                }
            }

            if (evt.getSource() == tree) {
                treePropertyChanged(evt, name);
            } else {
                base.propertyChange(evt);
            }
        }

        protected void treePropertyChanged(PropertyChangeEvent evt, String name) {
            if (name.equals("Frame.active")) {
                tree.repaint();
            } else if (name.equals("transferHandler")) {
                updateDropTargetListener();
            }
            base.propertyChange(evt);
        }
    }

    //
    // The following method are made public so that QuaquaTreeMouseBehavior can access them.
    //

    @Override
    public boolean startEditing(TreePath path, MouseEvent event) {
        return super.startEditing(path, event);
    }

    @Override
    public void completeEditing() {
        super.completeEditing();
    }

    @Override
    public boolean isLocationInExpandControl(TreePath path, int mouseX, int mouseY) {
        return super.isLocationInExpandControl(path, mouseX, mouseY);
    }

    @Override
    public void checkForClickInExpandControl(TreePath path, int mouseX, int mouseY) {
        super.checkForClickInExpandControl(path, mouseX, mouseY);
    }

    //
    // The following selection methods (lead/anchor) are covers for the
    // methods in JTree.
    //
    public void setAnchorSelectionPath(TreePath newPath) {
        ignoreLAChange = true;
        try {
            tree.setAnchorSelectionPath(newPath);
        } finally {
            ignoreLAChange = false;
        }
    }

    public TreePath getAnchorSelectionPath() {
        return tree.getAnchorSelectionPath();
    }

    public void setLeadSelectionPath(TreePath newPath, boolean repaint) {
        Rectangle bounds = repaint ? getPathBounds(tree, getLeadSelectionPath()) : null;

        ignoreLAChange = true;
        try {
            tree.setLeadSelectionPath(newPath);
        } finally {
            ignoreLAChange = false;
        }

        if (repaint) {
            if (bounds != null) {
                tree.repaint(bounds);
            }
            bounds = getPathBounds(tree, newPath);
            if (bounds != null) {
                tree.repaint(bounds);
            }
        }
    }

    public TreePath getLeadSelectionPath() {
        return tree.getLeadSelectionPath();
    }

    public void setLeadSelectionPath(TreePath newPath) {
        setLeadSelectionPath(newPath, false);
    }

    //
    // Extensibility
    //

    protected void updateDropTargetListener() {
    }
}
