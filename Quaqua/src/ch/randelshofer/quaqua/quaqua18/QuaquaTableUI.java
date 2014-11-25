package ch.randelshofer.quaqua.quaqua18;

import ch.randelshofer.quaqua.QuaquaUtilities;
import ch.randelshofer.quaqua.SelectionRepaintable;
import com.apple.laf.AquaFocusHandler;
import com.apple.laf.AquaTableUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;

/**
 * A table UI based on AquaTableUI for Yosemite. It implements the striped style. It paints the selection background
 * behind the entire selected row, to avoid gaps between cells. It makes showing the grid disabled by default. It
 * displays using an inactive style when not the focus owner. It works around a problem in JTable that interprets Meta
 * (Command) as an ordinary key instead of a modifier.
 *
 * For best results using the striped style, cell renderer components should not be opaque.
 */

public class QuaquaTableUI extends AquaTableUI implements SelectionRepaintable {

    protected ListSelectionListener selectionListener;
    protected TableCellRenderer originalBooleanRenderer;

    public static ComponentUI createUI(final JComponent c) {
        return new QuaquaTableUI();
    }

    public QuaquaTableUI() {
        focusHandler = new QuaquaTableFocusHandler();
        selectionListener = new SelectionListener();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();

        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);

        originalBooleanRenderer = installRendererIfPossible(Boolean.class, new QuaquaBooleanRenderer());
    }

    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();

        table.setDefaultRenderer(Boolean.class, originalBooleanRenderer);
    }

    protected TableCellRenderer installRendererIfPossible(Class<?> objectClass, TableCellRenderer renderer) {
        TableCellRenderer currentRenderer = table.getDefaultRenderer( objectClass);
        if (currentRenderer instanceof UIResource) {
            table.setDefaultRenderer(objectClass, renderer);
        }
        return currentRenderer;
    }

    protected class QuaquaBooleanRenderer extends JCheckBox implements TableCellRenderer, UIResource {

        public QuaquaBooleanRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
            setBorderPainted(false);
            setOpaque(false);
            setBackground(new Color(0, 0, 0, 0));   // AquaButtonLabeledUI ignores opaque for cell renderers
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
            } else {
                setForeground(table.getForeground());
            }
            setSelected((value != null && (Boolean) value));
            return this;
        }
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        updateSelectionListener(null);
    }

    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        table.getSelectionModel().removeListSelectionListener(selectionListener);
    }

    @Override
    protected KeyListener createKeyListener() {
        KeyListener base = super.createKeyListener();
        return new QuaquaTableKeyHandler(base);
    }

    protected class QuaquaTableKeyHandler implements KeyListener {
        protected KeyListener base;

        public QuaquaTableKeyHandler(KeyListener base) {
            this.base = base;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // Eat away META down keys..
            // We need to do this, because the JTable.processKeyBinding(…)
            // method does not treat VK_META as a modifier key, and starts
            // editing a cell whenever this key is pressed.

            // XXX - This is bogus but seems to work. Consider disabling
            // automatic editing in JTable by setting the client property
            // "JTable.autoStartsEdit" to Boolean.FALSE and doing all the
            // processing here.

            if (e.getKeyCode() == KeyEvent.VK_META) {
                e.consume();
            } else if (base != null) {
                base.keyPressed(e);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (base != null) {
                base.keyReleased(e);
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
            if (base != null) {
                base.keyTyped(e);
            }
        }
    }

    protected FocusListener createFocusListener() {
        return new TableFocusHandler();
    }

    /**
     * The focus handler is both a focus listener and a property change listener.
     */
    protected class QuaquaTableFocusHandler extends AquaFocusHandler {
        public void propertyChange(final PropertyChangeEvent ev) {
            String pn = ev.getPropertyName();
            if (pn != null) {
                if (pn.equals(FRAME_ACTIVE_PROPERTY)) {
                    repaintSelection();
                    return;
                }
                if (pn.equals("selectionModel")) {
                    ListSelectionModel old = (ListSelectionModel) ev.getOldValue();
                    updateSelectionListener(old);
                }
            }

            super.propertyChange(ev);
        }
    }

    protected class TableFocusHandler extends BasicTableUI.FocusHandler {
        @Override
        public void focusGained(FocusEvent e) {
            super.focusGained(e);
            repaintSelection();
        }

        @Override
        public void focusLost(FocusEvent e) {
            super.focusLost(e);
            repaintSelection();
        }
    }

    protected void updateSelectionListener(ListSelectionModel old) {
        if (old != null) {
            old.removeListSelectionListener(selectionListener);
        }
        table.getSelectionModel().addListSelectionListener(selectionListener);
    }

    /**
     * Because JTable takes charge of repainting on a row selection change instead of deferring to the TableUI, we must
     * duplicate its code to ensure that the full width of the table is repainted, not just the cells.
     */
    protected class SelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (table.getRowCount() <= 0 || table.getColumnCount() <= 0) {
                return;
            }
            int firstIndex = limit(e.getFirstIndex(), 0, table.getRowCount() - 1);
            int lastIndex = limit(e.getLastIndex(), 0, table.getRowCount()-1);
            Rectangle firstRowRect = table.getCellRect(firstIndex, 0, true);    // was false
            Rectangle lastRowRect = table.getCellRect(lastIndex, table.getColumnCount() - 1, true); // was false
            Rectangle dirtyRegion = firstRowRect.union(lastRowRect);
            table.repaint(dirtyRegion);
        }

        protected int limit(int i, int a, int b) {
            return Math.min(b, Math.max(i, a));
        }
    }

    /**
     * This method is called after a possible change to the focus state that affects the display. It updates the table
     * selection colors to correspond to the new focus state.
     */
    @Override
    public void repaintSelection() {
        boolean isFocused = QuaquaUtilities.isFocused(table);
        QuaquaFocusHandler.swapSelectionColors("Table", table, isFocused);
        table.repaint();
    }

    public void paint(Graphics g, JComponent c) {

        Object property = table.getClientProperty("Quaqua.Table.style");
        boolean isStriped = property != null && property.equals("striped");
        boolean isSelection = table.getSelectedRowCount() > 0 && table.getRowSelectionAllowed()
                || table.getSelectedColumnCount() > 0 && table.getColumnSelectionAllowed();

        if (isStriped || isSelection) {
            paintBackground(g, c, isStriped);
        }

        super.paint(g, c);
    }

    protected void paintBackground(Graphics g, JComponent c, boolean paintStripes) {

        /*
         * All but the last line of this method is duplicated code. Alas, all of the helper methods in BasicTableUI are
         * private.
         */

        Rectangle clip = g.getClipBounds();

        Rectangle bounds = table.getBounds();
        // account for the fact that the graphics has already been translated
        // into the table's bounds
        bounds.x = bounds.y = 0;

        if (table.getRowCount() <= 0 || table.getColumnCount() <= 0 ||
                // this check prevents us from painting the entire table
                // when the clip doesn't intersect our bounds at all
                !bounds.intersects(clip)) {

            return;
        }

        boolean ltr = table.getComponentOrientation().isLeftToRight();

        Point upperLeft = clip.getLocation();
        Point lowerRight = new Point(clip.x + clip.width - 1,
                                     clip.y + clip.height - 1);

        int rMin = table.rowAtPoint(upperLeft);
        int rMax = table.rowAtPoint(lowerRight);
        // This should never happen (as long as our bounds intersect the clip,
        // which is why we bail above if that is the case).
        if (rMin == -1) {
            rMin = 0;
        }
        // If the table does not have enough rows to fill the view we'll get -1.
        // (We could also get -1 if our bounds don't intersect the clip,
        // which is why we bail above if that is the case).
        // Replace this with the index of the last row.
        if (rMax == -1) {
            rMax = table.getRowCount()-1;
        }

        int cMin = table.columnAtPoint(ltr ? upperLeft : lowerRight);
        int cMax = table.columnAtPoint(ltr ? lowerRight : upperLeft);
        // This should never happen.
        if (cMin == -1) {
            cMin = 0;
        }
        // If the table does not have enough columns to fill the view we'll get -1.
        // Replace this with the index of the last column.
        if (cMax == -1) {
            cMax = table.getColumnCount()-1;
        }

        paintBackground(g, c, paintStripes, rMin, rMax, cMin, cMax);
    }

    protected void paintBackground(Graphics g, JComponent c, boolean paintStripes,
                                   int rMin, int rMax, int cMin, int cMax) {
        Rectangle clip = g.getClipBounds();

        boolean isFocused = table.isEditing() || QuaquaUtilities.isFocused(table);
        Color[] stripes = {UIManager.getColor("Table.alternateBackground.0"), UIManager.getColor("Table.alternateBackground.1")};
        Color selectedBackground = UIManager.getColor(isFocused ? "Table.selectionBackground" : "Table.inactiveSelectionBackground");

        boolean isRowSelection = table.getSelectedRowCount() > 0 && table.getRowSelectionAllowed() && !table.getColumnSelectionAllowed();
        boolean isColumnSelection = table.getSelectedColumnCount() > 0 && table.getColumnSelectionAllowed() && !table.getRowSelectionAllowed();

        for(int row = rMin; row <= rMax; row++) {
            Rectangle cellRect = table.getCellRect(row, cMin, false);
            boolean isSelected = isRowSelection && table.isRowSelected(row);
            Color bg = isSelected ? selectedBackground : (paintStripes ? stripes[row % 2] : null);
            if (bg == null) {
                bg = table.getBackground();
            }
            g.setColor(bg);
            g.fillRect(clip.x, cellRect.y, clip.width, cellRect.height);
        }

        // TBD: should selected column be painted here or is it OK for just the cells to paint the selection background?
    }
}
