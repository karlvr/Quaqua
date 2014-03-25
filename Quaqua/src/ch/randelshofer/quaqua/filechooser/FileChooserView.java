/*
 * Copyright (c) 2014 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.filechooser;

import javax.swing.event.ChangeListener;
import javax.swing.tree.TreePath;
import java.util.List;

/**
 * Methods common to list view and column view
 */

public interface FileChooserView {

    void setActive(boolean b);

    void setModel(SubtreeTreeModel m);

    void setFileRenderer(CellRenderer r);

    void setMultipleSelection(boolean b);

    void setSelection(TreePath path);

    void setSelection(List<TreePath> paths);

    List<TreePath> getSelection();

    void ensurePathIsVisible(TreePath path);

    void ensureSelectionIsVisible();

    /**
     * Add a listener to be called when the selection changes.
     */
    void addSelectionChangeListener(ChangeListener l);

    /**
     * Add a listener to be called when the user selects a file or directory in a way
     * that has an effect beyond the view.
     */
    void addSelectListener(SelectListener l);

    public interface SelectListener {
        void select(TreePath path);
    }

    /**
     * Update the view in response to a change in the file chooser configuration.
     */
    public void reconfigure();

    public boolean requestFocusInWindow();
}
