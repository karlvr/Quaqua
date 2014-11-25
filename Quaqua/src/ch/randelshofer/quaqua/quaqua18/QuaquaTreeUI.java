package ch.randelshofer.quaqua.quaqua18;

import ch.randelshofer.quaqua.SelectionRepaintable;
import ch.randelshofer.quaqua.QuaquaDropTargetListener;
import ch.randelshofer.quaqua.QuaquaUtilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.TooManyListenersException;

/**
 * A tree UI based on AquaTreeUI for Yosemite. It supports filled cells. It supports the striped and sidebar styles.
 * It supports a more compatible mouse behavior. It displays using an inactive style based on focus ownership. It
 * configures cell renderers, as possible, to conform to the tree style.
 */

public class QuaquaTreeUI extends QuaquaAquaTreeUI implements SelectionRepaintable {

    public final static Color TRANSPARENT_COLOR = new Color(0, true);

    protected boolean isCellFilled;
    protected boolean isSideBar;
    protected boolean isStriped;

    protected boolean isActive;     // for communication between paint() and paintRow()
    protected boolean isFocused;    // ditto
    protected Color foreground;
    protected Color selectionBackground;
    protected Color selectionForeground;

    private static DropTargetListener defaultDropTargetListener = null;

    public QuaquaTreeUI() {
    }

    public static ComponentUI createUI(JComponent c) {
        return new QuaquaTreeUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        updateProperties();
    }

    protected void updateProperties() {
        if (tree != null) {
            isCellFilled = Boolean.TRUE.equals(tree.getClientProperty("Quaqua.Tree.isCellFilled"));
            Object style = tree.getClientProperty("Quaqua.Tree.style");
            isSideBar = style != null && (style.equals("sideBar")
                    || style.equals("sourceList"));
            isStriped = style != null && style.equals("striped");
        }
    }

    @Override
    protected MouseListener createMouseListener() {
        return new QuaquaTreeMouseBehavior(tree);
    }

    @Override
    protected FocusListener createFocusListener() {
        FocusListener base = super.createFocusListener();
        return new TreeFocusListener(base);
    }

    @Override
    protected PropertyChangeListener createPropertyChangeListener() {
        PropertyChangeListener base = super.createPropertyChangeListener();
        return new TreePropertyChangeListener(base);
    }

    protected AbstractLayoutCache.NodeDimensions createNodeDimensions() {
        return new TreeNodeDimensions();
    }

    @Override
    protected void updateDropTargetListener() {
        DropTarget dropTarget = tree.getDropTarget();
        if (dropTarget instanceof UIResource) {
            if (defaultDropTargetListener == null) {
                defaultDropTargetListener = new TreeDropTargetListener();
            }
            try {
                dropTarget.addDropTargetListener(defaultDropTargetListener);
            } catch (TooManyListenersException tmle) {
                // should not happen... swing drop target is multicast
            }
        }
    }

    protected class TreeFocusListener implements FocusListener {
        private FocusListener base;

        public TreeFocusListener(FocusListener base) {
            this.base = base;
        }

        @Override
        public void focusGained(FocusEvent e) {
            base.focusGained(e);
            repaintSelection();
        }

        @Override
        public void focusLost(FocusEvent e) {
            base.focusLost(e);
            repaintSelection();
        }
    }

    protected class TreePropertyChangeListener extends MyPropertyChangeListener {

        public TreePropertyChangeListener(PropertyChangeListener base) {
            super(base);
        }

        @Override
        protected void treePropertyChanged(PropertyChangeEvent evt, String name) {

            if (name.equals("Quaqua.Tree.isCellFilled")) {
                updateProperties();
                tree.repaint();
                if (treeState != null) {
                    treeState.invalidateSizes();
                }
            }

            if (name.equals("Quaqua.Tree.style")) {
                updateProperties();
                tree.repaint();
            }

            super.treePropertyChanged(evt, name);
        }
    }

    public void repaintSelection() {
        if (tree == null) {
            return;
        }

        // Support custom repainting, needed by TreeTable
        Object o = tree.getClientProperty("Tree.selectionRepainter");
        if (o instanceof SelectionRepaintable) {
            SelectionRepaintable sp = (SelectionRepaintable) o;
            sp.repaintSelection();
            return;
        }

        Rectangle pBounds = null;

        TreePath[] selectionPaths = tree.getSelectionPaths();
        if (selectionPaths != null) {
            for (int i = 0; i < selectionPaths.length; i++) {
                if (i == 0) {
                    pBounds = getPathBounds(tree, selectionPaths[i]);
                } else {
                    pBounds.add(getPathBounds(tree, selectionPaths[i]));
                }
            }
            if (pBounds != null) {
                tree.repaint(0, pBounds.y, tree.getWidth(), pBounds.height);
            }
        }
     }

    /**
     * A custom NodeDimensions that implements filled cells as well as special configuration of the renderer that might
     * affect node size.
     */

    protected class TreeNodeDimensions
        extends NodeDimensionsHandler
    {
        @Override
        public Rectangle getNodeDimensions(Object value, int row, int depth, boolean expanded, Rectangle size) {
            Rectangle r = getBasicNodeDimensions(value, row, depth, expanded, size);

            /*
             * Implement the filled cell option. Does not affect editing. (Should it?)
             */

            if (isCellFilled && r != null && (editingComponent == null || editingRow != row)) {
                Insets s = tree.getInsets();
                int width = tree.getWidth() - s.left - s.right;
                r.width = width;
            }

            return r;
        }

        // copied from BasicTreeUI with alterations to configure the renderer
        public Rectangle getBasicNodeDimensions(Object value, int row,
                                           int depth, boolean expanded,
                                           Rectangle size) {
            // Return size of editing component, if editing and asking
            // for editing row.
            if(editingComponent != null && editingRow == row) {
                Dimension        prefSize = editingComponent.
                                              getPreferredSize();
                int              rh = getRowHeight();

                if(rh > 0 && rh != prefSize.height)
                    prefSize.height = rh;
                if(size != null) {
                    size.x = getRowX(row, depth);
                    size.width = prefSize.width;
                    size.height = prefSize.height;
                }
                else {
                    size = new Rectangle(getRowX(row, depth), 0,
                                         prefSize.width, prefSize.height);
                }
                return size;
            }
            // Not editing, use renderer.
            if(currentCellRenderer != null) {
                Component          aComponent;

                /*
                 * A bit of a hack. Because the font may change to bold when selected and the bold font is probably
                 * larger, use the selected font when calculating the label size.
                 */

                boolean isSelected = true;

                aComponent = currentCellRenderer.getTreeCellRendererComponent
                    (tree, value, isSelected,
                     expanded, treeModel.isLeaf(value), row,
                     false);

                configureForLayout(aComponent, row, depth, isSelected);

                if(tree != null) {
                    // Only ever removed when UI changes, this is OK!
                    rendererPane.add(aComponent);
                    aComponent.validate();
                }
                Dimension        prefSize = aComponent.getPreferredSize();

                if(size != null) {
                    size.x = getRowX(row, depth);
                    size.width = prefSize.width;
                    size.height = prefSize.height;
                }
                else {
                    size = new Rectangle(getRowX(row, depth), 0,
                                         prefSize.width, prefSize.height);
                }
                return size;
            }
            return null;
        }

        /**
         * Customize the X offset for the sidebar, to take into account that category rows have no icons.
         */
        @Override
        protected int getRowX(int row, int depth) {
            if (isSideBar && depth > 1) {
                --depth;
            }

            return super.getRowX(row, depth);
        }
    }

    @Override
    protected void updateDepthOffset() {
        if (isSideBar) {
            depthOffset = -2;
        } else {
            super.updateDepthOffset();
        }
    }

    /**
     * Customized painting. Set the variables for use by paintRow.
     */

    @Override
    public void paint(Graphics g, JComponent c) {

        isActive = Quaqua18Utilities.isActive(c);
        isFocused = shouldDisplayAsFocused(c);

        if (isSideBar) {
            foreground = UIManager.getColor("Tree.sideBar.foreground");
            selectionForeground = UIManager.getColor("Tree.sideBar.selectionForeground");
            selectionBackground = UIManager.getColor(isActive ? "Tree.sideBar.selectionBackground" : "Tree.sideBar.inactiveSelectionBackground");
        } else {
            foreground = UIManager.getColor("Tree.foreground");
            selectionForeground = UIManager.getColor("Tree.selectionForeground");
            selectionBackground = UIManager.getColor(isFocused ? "Tree.selectionBackground" : "Tree.inactiveSelectionBackground");
        }

        paintBackground(g);

        super.paint(g, c);
    }

    protected void paintBackground(Graphics g) {
        /*
         * TBD: In Yosemite, the sidebar background is a translucent blurry thing. One wonders if this could ever be
         * simulated in Java.
         */

        Color background;

        if (isSideBar) {
            background = UIManager.getColor(isActive ? "Tree.sideBar.background" : "Tree.sideBar.inactiveBackground");
        } else {
            background = tree.getBackground();
        }

        if (tree.isOpaque()) {
            int width = tree.getWidth();
            int height = tree.getHeight();
            g.setColor(background);
            g.fillRect(0, 0, width, height);
        }

        Rectangle paintBounds = g.getClipBounds();
        TreePath initialPath = getClosestPathForLocation(tree, 0, paintBounds.y);
        Enumeration paintingEnumerator = treeState.getVisiblePathsFrom(initialPath);
        if (initialPath != null && paintingEnumerator != null) {
            paintRowBackgrounds(g, initialPath, paintingEnumerator);
        } else if (isStriped) {
            paintEmptyTreeStripes(g);
        }
    }

    /**
     * Paint stripes (if appropriate) and selected row backgrounds
     */
    protected void paintRowBackgrounds(Graphics g, TreePath initialPath, Enumeration paintingEnumerator) {
        boolean shouldPaintSelection = !Boolean.FALSE.equals(tree.getClientProperty("Tree.paintSelectionBackground"));
        if (!isStriped && !shouldPaintSelection) {
            return;
        }

        int width = tree.getWidth();
        int height = tree.getHeight();
        Insets insets = tree.getInsets();

        int rwidth = width - insets.left - insets.left;
        int rheight = tree.getRowHeight();
        if (rheight <= 0) {
            // FIXME - Use the cell renderer to determine the height
            rheight = tree.getFont().getSize() + 4;
        }

        Color[] stripes = {UIManager.getColor("Tree.alternateBackground.0"), UIManager.getColor("Tree.alternateBackground.1")};

        int row = treeState.getRowForPath(initialPath);
        Rectangle paintBounds = g.getClipBounds();
        int endY = paintBounds.y + paintBounds.height;

        while (paintingEnumerator.hasMoreElements()) {
            TreePath path = (TreePath) paintingEnumerator.nextElement();
            if (path == null) {
                break;
            }
            Rectangle bounds = getPathBounds(tree, path);
            if (bounds == null) {
                return; // should not happen
            }

            bounds.x += insets.left;
            bounds.y += insets.top;

            if (tree.isRowSelected(row) && shouldPaintSelection) {
                g.setColor(selectionBackground);
                g.fillRect(insets.left, bounds.y, rwidth, bounds.height);
            } else if (isStriped) {
                g.setColor(stripes[row % 2]);
                g.fillRect(insets.left, bounds.y, rwidth, bounds.height);
            }

            if ((bounds.y + bounds.height) >= endY) {
                break;
            }

            row++;
        }
    }

    protected boolean shouldDisplayAsFocused(Component c) {
        return QuaquaUtilities.isFocused(c);
    }

    /**
     * Paint stripes for an empty tree.
     */
    protected void paintEmptyTreeStripes(Graphics g) {
        int width = tree.getWidth();
        int height = tree.getHeight();
        Insets insets = tree.getInsets();

        int rwidth = width - insets.left - insets.left;
        int rheight = tree.getRowHeight();
        if (rheight <= 0) {
            // FIXME - Use the cell renderer to determine the height
            rheight = tree.getFont().getSize() + 4;
        }

        Color[] stripes = {UIManager.getColor("Tree.alternateBackground.0"), UIManager.getColor("Tree.alternateBackground.1")};

        int row = 0;
        for (int y = 0; y < height; y += rheight) {
            g.setColor(stripes[row % 2]);
            g.fillRect(insets.left, y, rwidth, rheight);
            row++;
        }
    }

    /**
     * Customized row painting for sidebar trees. Various client properties are set. An attempt is made to prevent the
     * tree cell renderer from drawing a background or a border or (in the case of a top level node) an icon.
     */
    @Override
    protected void paintRow(Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds, TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {

        if(editingComponent != null && editingRow == row)
            return;

        int leadIndex;

        if(tree.hasFocus()) {
            leadIndex = getLeadSelectionRow();
        }
        else
            leadIndex = -1;

        Component component;

        component = currentCellRenderer.getTreeCellRendererComponent
                      (tree, path.getLastPathComponent(),
                       tree.isRowSelected(row), isExpanded, isLeaf, row,
                       (leadIndex == row));

        configureForPainting(component, path, row);

        rendererPane.paintComponent(g, component, tree, bounds.x, bounds.y,
                                    bounds.width, bounds.height, true);
    }

    protected void configureForLayout(Component component, int row, int depth, boolean isSelected) {
        if (component instanceof DefaultTreeCellRenderer) {
            DefaultTreeCellRenderer treeCellRenderer = (DefaultTreeCellRenderer) component;
            treeCellRenderer.setBorder(new EmptyBorder(0, 0, 0, 0));
        }

        if (isSideBar) {
            configureForLayoutSideBar(component, row, depth, isSelected);
        }
    }

    protected void configureForPainting(Component component, TreePath path, int row) {
        // We need to do some (very ugly) modifications because
        // DefaultTreeCellRenderers have their own paint-method
        // and paint a border around each item
        if (component instanceof DefaultTreeCellRenderer) {
            DefaultTreeCellRenderer treeCellRenderer = (DefaultTreeCellRenderer) component;
            treeCellRenderer.setBackgroundNonSelectionColor(TRANSPARENT_COLOR);
            treeCellRenderer.setBackgroundSelectionColor(TRANSPARENT_COLOR);
            treeCellRenderer.setBorderSelectionColor(TRANSPARENT_COLOR);
            treeCellRenderer.setBorder(new EmptyBorder(0, 0, 0, 0));
        }

        if (component instanceof JLabel) {
            boolean isRowSelected = tree.isRowSelected(row);
            JLabel label = (JLabel) component;
            label.setForeground(isRowSelected ? selectionForeground : foreground);
        }

        if (isSideBar) {
            configureForPaintingSideBar(component, path, row);
        }
    }

    protected void configureForLayoutSideBar(Component component, int row, int depth, boolean isRowSelected) {
        if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            boolean isTopLevel = depth == 1;

            Font f = getSideBarFont(isTopLevel, isRowSelected);
            if (f != null) {
                // Trick the renderer into accepting the font
                // It ignores instances of FontUIResource
                label.setFont(fixFont(f));
            }

            label.putClientProperty("Quaqua.Label.style",
                    isTopLevel ? isRowSelected ? (isActive ? "categorySelected" : "categoryInactiveSelected")
                            : (isActive ? "category" : "categoryInactive") : isRowSelected ? (isActive ? "rowSelected" : "rowInactiveSelected")
                            : (isActive ? "row" : "rowInactive"));

            if (isTopLevel) {
                label.setIcon(null);
                label.setDisabledIcon(null);
            }
        }
    }

    protected void configureForPaintingSideBar(Component component, TreePath path, int row) {
        if (component instanceof JLabel) {
            boolean isRowSelected = tree.isRowSelected(row);
            JLabel label = (JLabel) component;
            boolean isTopLevel = path.getPathCount() == (isRootVisible() ? 1 : 2);

            Font f = getSideBarFont(isTopLevel, isRowSelected);
            if (f != null) {
                // Trick the renderer into accepting the font
                // It ignores instances of FontUIResource
                label.setFont(fixFont(f));
            }

            Color c = getSideBarForeground(isTopLevel, isRowSelected);
            if (c != null) {
                label.setForeground(c);
            }

            label.putClientProperty("Quaqua.Label.style",
                    isTopLevel ? isRowSelected ? (isActive ? "categorySelected" : "categoryInactiveSelected")
                            : (isActive ? "category" : "categoryInactive") : isRowSelected ? (isActive ? "rowSelected" : "rowInactiveSelected")
                            : (isActive ? "row" : "rowInactive"));

            if (isTopLevel) {
                label.setIcon(null);
                label.setDisabledIcon(null);
            }
        }
    }

    protected Font getSideBarFont(boolean isTopLevel, boolean isSelected) {
        if (isTopLevel) {
            return UIManager.getFont("Tree.sideBarCategory.font");
        } else if (isSelected) {
            return UIManager.getFont("Tree.sideBar.selectionFont");
        } else {
            return UIManager.getFont("Tree.sideBar.font");
        }
    }

    protected Color getSideBarForeground(boolean isTopLevel, boolean isSelected) {
        if (isTopLevel) {
            return UIManager.getColor("Tree.sideBarCategory.foreground");
        } else if (isSelected) {
            return UIManager.getColor("Tree.sideBar.selectionForeground");
        } else {
            return UIManager.getColor("Tree.sideBar.foreground");
        }
    }

    protected Font fixFont(Font f) {
        return f instanceof FontUIResource ? new MyFont(f) : f;
    }

    protected class MyFont extends Font {
        public MyFont(Font f) {
            super(f);
        }
    }

    /**
     * A DropTargetListener to extend the default Swing handling of drop operations
     * by moving the tree selection to the nearest location to the mouse pointer.
     * Also adds autoscroll capability.
     */
    static class TreeDropTargetListener extends QuaquaDropTargetListener {

        /**
         * called to save the state of a component in case it needs to
         * be restored because a drop is not performed.
         */
        @Override
        protected void saveComponentState(JComponent comp) {
            JTree tree = (JTree) comp;
            selectedIndices = tree.getSelectionRows();
        }

        /**
         * called to restore the state of a component
         * because a drop was not performed.
         */
        @Override
        protected void restoreComponentState(JComponent comp) {
            JTree tree = (JTree) comp;
            tree.setSelectionRows(selectedIndices);
        }

        /**
         * called to set the insertion location to match the current
         * mouse pointer coordinates.
         */
        @Override
        protected void updateInsertionLocation(JComponent comp, Point p) {
            JTree tree = (JTree) comp;
            BasicTreeUI ui = (BasicTreeUI) tree.getUI();
            TreePath path = ui.getClosestPathForLocation(tree, p.x, p.y);
            if (path != null) {
                tree.setSelectionPath(path);
            }
        }
        private int[] selectedIndices;
    }
}
