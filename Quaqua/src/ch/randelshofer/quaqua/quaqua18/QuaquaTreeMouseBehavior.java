package ch.randelshofer.quaqua.quaqua18;

/*
    TBD:

*/

import ch.randelshofer.quaqua.QuaquaDragRecognitionSupport;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EventObject;

/**

 */

public class QuaquaTreeMouseBehavior extends MouseInputAdapter
        implements QuaquaDragRecognitionSupport.BeforeDrag {

    protected JTree tree;
    protected QuaquaAquaTreeUI ui;

    private long lastTime = 0L;        // MouseListener & MouseMotionListener
    private boolean mouseReleaseDeselects;
    private boolean mouseDragSelects;
    private boolean isMouseReleaseStartsEditing;
    private MouseEvent releaseEvent;
    private boolean isDragRecognitionOngoing;

    public QuaquaTreeMouseBehavior(JTree tree) {
        this.tree = tree;
        this.ui = (QuaquaAquaTreeUI) tree.getUI();
    }


    public void dragStarting(MouseEvent me) {
    }

    public void mousePressed(MouseEvent e) {
        if (tree.isEnabled()) {
            // if we can't stop any ongoing editing, do nothing
            if (ui.isEditing(tree) && tree.getInvokesStopCellEditing() && !ui.stopEditing(tree)) {
                return;
            }

            ui.completeEditing();

            // Note: Some applications depend on selection changes only occuring
            // on focused components. Maybe we must not do any changes to the
            // selection changes at all, when the compnent is not focused?
            if (tree.isRequestFocusEnabled()) {
                tree.requestFocusInWindow();
            }


            TreePath path = getMouseClickedClosestPathForLocation(tree, e.getX(), e.getY());

            // Check for clicks in expand control
            if (ui.isLocationInExpandControl(path, e.getX(), e.getY())) {
                ui.checkForClickInExpandControl(path, e.getX(), e.getY());
                return;
            }

            int index = tree.getRowForPath(path);

            mouseDragSelects = false;
            mouseReleaseDeselects = false;
            isMouseReleaseStartsEditing = true;
            isDragRecognitionOngoing = false;
            if (index != -1) {
                boolean isRowAtIndexSelected = tree.isRowSelected(index);
                if (isRowAtIndexSelected && e.isPopupTrigger()) {
                    // Do not change the selection, if the item is already
                    // selected, and the user triggers the popup menu.
                } else {
                    int anchorIndex = tree.getRowForPath(tree.getAnchorSelectionPath());

                    if ((e.getModifiersEx() & (MouseEvent.META_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK)) == MouseEvent.META_DOWN_MASK) {
                        if (isRowAtIndexSelected) {
                            tree.removeSelectionInterval(index, index);
                        } else {
                            tree.addSelectionInterval(index, index);
                            mouseDragSelects = true;
                            isMouseReleaseStartsEditing = false;
                        }
                    } else if ((e.getModifiersEx() & (MouseEvent.SHIFT_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK)) == MouseEvent.SHIFT_DOWN_MASK
                            && anchorIndex != -1) {
                        tree.setSelectionInterval(anchorIndex, index);
                        ui.setLeadSelectionPath(path);
                        mouseDragSelects = true;
                        isMouseReleaseStartsEditing = false;
                    } else if ((e.getModifiersEx() & (MouseEvent.SHIFT_DOWN_MASK | MouseEvent.META_DOWN_MASK)) == 0) {
                        if (isRowAtIndexSelected) {
                            if (tree.getDragEnabled()) {
                                isDragRecognitionOngoing = QuaquaDragRecognitionSupport.mousePressed(e);
                                mouseDragSelects = mouseReleaseDeselects = false;
                            } else {
                                mouseReleaseDeselects = tree.isFocusOwner();
                            }
                        } else {
                            tree.setSelectionInterval(index, index);
                            if (tree.getDragEnabled()
                                    && ui.getPathBounds(tree, path).contains(e.getPoint())) {
                                isDragRecognitionOngoing = QuaquaDragRecognitionSupport.mousePressed(e);
                                mouseDragSelects = mouseReleaseDeselects = false;
                                isMouseReleaseStartsEditing = false;
                            } else {
                                mouseDragSelects = true;
                                isMouseReleaseStartsEditing = false;
                            }
                        }
                        ui.setAnchorSelectionPath(path);
                        ui.setLeadSelectionPath(path);
                    }
                }
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (tree.isEnabled()) {
            if (tree.getDragEnabled() && isDragRecognitionOngoing) {
                QuaquaDragRecognitionSupport.mouseDragged(e, this);
            }

            // Do nothing if we can't stop editing.
            if (ui.isEditing(tree) && tree.getInvokesStopCellEditing()
                    && !ui.stopEditing(tree)) {
                return;
            }

            TreePath leadPath = ui.getClosestPathForLocation(tree, e.getX(),
                    e.getY());

            // this is a dirty trick to reset the timer of the cell editor.
            if (tree.getCellEditor() != null) {
                tree.getCellEditor().isCellEditable(new EventObject(this));
            }

            mouseReleaseDeselects = false;
            isMouseReleaseStartsEditing = false;
            if (mouseDragSelects) {
                int index = tree.getRowForPath(leadPath);
                if (index != -1) {
                    Rectangle cellBounds = tree.getRowBounds(index);
                    tree.scrollRectToVisible(cellBounds);
                    TreePath anchorPath = tree.getAnchorSelectionPath();
                    int anchorIndex = tree.getRowForPath(anchorPath);
                    if (tree.getSelectionModel().getSelectionMode() == TreeSelectionModel.SINGLE_TREE_SELECTION) {
                        tree.setSelectionInterval(index, index);
                    } else {
                        if (anchorIndex < index) {
                            tree.setSelectionInterval(anchorIndex, index);
                        } else {
                            tree.setSelectionInterval(index, anchorIndex);
                        }
                        ui.setAnchorSelectionPath(anchorPath);
                        ui.setLeadSelectionPath(leadPath);
                    }
                }
            }
        }
    }

    /**
     * Invoked when the mouse button has been moved on a component
     * (with no buttons down).
     */
    public void mouseMoved(MouseEvent e) {
        isMouseReleaseStartsEditing = false;
        // this is a dirty trick to reset the timer of the cell editor.
        if (tree.getCellEditor() != null) {
            tree.getCellEditor().isCellEditable(new EventObject(this));
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (tree.isEnabled()) {
            if (ui.isEditing(tree) && tree.getInvokesStopCellEditing()
                    && !ui.stopEditing(tree)) {
                return;
            }
            TreePath path = getMouseClickedClosestPathForLocation(tree, e.getX(),
                    e.getY());
            if (startEditingOnRelease(path, e, e)) {
                return;
            }

            mouseDragSelects = false;
            if (mouseReleaseDeselects) {
                int index = tree.getRowForPath(path);
                tree.setSelectionInterval(index, index);
            }
            //tree.getSelectionModel().setValueIsAdjusting(false);
        }
        if (tree.isRequestFocusEnabled()) {
            tree.requestFocus();
        }
    }

    public void mouseExited(MouseEvent e) {
        isMouseReleaseStartsEditing = false;
    }

    // cover method for startEditing that allows us to pass extra
    // information into that method via a class variable
    private boolean startEditingOnRelease(TreePath path,
            MouseEvent event,
            MouseEvent releaseEvent) {
        this.releaseEvent = releaseEvent;
        try {
            if (isMouseReleaseStartsEditing) {
                return ui.startEditing(path, event);
            } else {
                return false;
            }
        } finally {
            this.releaseEvent = null;
        }
    }

    private TreePath getMouseClickedClosestPathForLocation(JTree tree, int x, int y) {
        final TreePath path = ui.getClosestPathForLocation(tree, x, y);
        if (path == null) {
            return null;
        }

        final Rectangle pathBounds = ui.getPathBounds(tree, path);
        if (y > pathBounds.y + pathBounds.height) {
            return null;
        }

        return path;
    }
}
