/*
 * @(#)QuaquaListUI.java
 *
 * Copyright (c) 2004-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.quaqua18;

import ch.randelshofer.quaqua.QuaquaListMouseBehavior;
import ch.randelshofer.quaqua.QuaquaUtilities;
import com.apple.laf.AquaBorder;
import com.apple.laf.AquaListUI;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicListUI;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * QuaquaListUI for Java 1.8.
 *
 * The contributions of this class: it implements a striped style on vertical lists; it fixes a problem that lists that
 * do not own the keyboard focus use the active selected colors; it supports OS X mouse behaviors more accurately.
 */
public class QuaquaListUI extends AquaListUI {

    private boolean isStriped = false;
    private boolean isComboPopup = false;
    private boolean isStripedColorInstalled = false;

    public QuaquaListUI() {
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        updateStriped();
    }

    public static ComponentUI createUI(JComponent list) {
        return new QuaquaListUI();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        paintStripes(g, c);
        super.paint(g, c);
    }

    @Override
    protected void paintCell(
            Graphics g,
            int row,
            Rectangle rowBounds,
            ListCellRenderer cellRenderer,
            ListModel dataModel,
            ListSelectionModel selModel,
            int leadIndex) {
        Object value = dataModel.getElementAt(row);
        boolean isEnabled = list.isEnabled();
        boolean isFocused = isEnabled && QuaquaUtilities.isFocused(list);
        boolean cellHasFocus = isFocused && (row == leadIndex);
        boolean isSelected = selModel.isSelectedIndex(row);

        updateListColors(isFocused, row);

        int cx = rowBounds.x;
        int cy = rowBounds.y;
        int cw = rowBounds.width;
        int ch = rowBounds.height;

        /*
         * Paint the background in case the cell renderer is not-opaque. Code written for previous versions of Quaqua
         * using the striped style may use not-opaque cell renderers.
         */

        if (isSelected) {
            g.setColor(list.getSelectionBackground());
            g.fillRect(cx, cy, cw, ch);
        } else {
            g.setColor(list.getBackground());
            g.fillRect(cx, cy, cw, ch);
        }

        if (isComboPopup) {
            cx += 7;
            cw -= 14;
        }

        Component rendererComponent =
                cellRenderer.getListCellRendererComponent(list, value, row, isSelected, cellHasFocus);

        rendererPane.paintComponent(g, rendererComponent, list, cx, cy, cw, ch, true);
    }

    /**
     * Paint stripes where there are no list cells, if appropriate.
     */
    public void paintStripes(Graphics g, JComponent c) {
        if (isStriped && list.getModel() != null) {
            // Now check if we need to paint some stripes
            Dimension vs = c.getSize();
            Dimension ts = list.getSize();

            Point p = list.getLocation();
            int rh = list.getFixedCellHeight();
            int n = list.getModel().getSize();
            if (rh <= 0) {
                rh = (n == 0) ? 12 : getCellBounds(list, 0, 0).height;
            }
            int row = Math.abs(p.y / rh);
            int th = n * rh - row * rh;

            // Fill the background of the list with stripe color 1
            g.setColor(getAlternateColor(1));
            g.fillRect(0, 0, ts.width, ts.height);

            // Fill rectangles with stripe color 0
            g.setColor(getAlternateColor(0));

            // Paint empty rows at the right to fill the viewport
            if (ts.width < vs.width) {
                int y = p.y + row * rh;
                while (y < th) {
                    if (row % 2 == 0) {
                        g.fillRect(0, y, vs.width, rh);
                    }
                    y += rh;
                    row++;
                }
            }

            // Paint empty rows at the bottom to fill the viewport
            if (th < vs.height) {
                row = n;
                int y = th;
                while (y < vs.height) {
                    if (row % 2 == 0) {
                        g.fillRect(0, y, vs.width, rh);
                    }
                    y += rh;
                    row++;
                }
            }
        }
    }

    private Color getAlternateColor(int modulo) {
        if (modulo == 0) {
            return UIManager.getColor("List.alternateBackground.0");
        } else {
            return UIManager.getColor("List.alternateBackground.1");
        }
    }

    private void updateStriped() {
        Object value = list.getClientProperty("Quaqua.List.style");
        isStriped = value != null && value.equals("striped") && list.getLayoutOrientation() == JList.VERTICAL;
    }

    /**
     * Update the list colors based on the focus state of the list and the row being painted.
     *
     * AquaLookAndFeel implements the inactive selection color by changing the list selected-item color attributes.
     * DefaultListCellRenderer works by obtaining its background and foreground colors from the list component, using
     * the selected-item colors when the item is selected. To support the striped style, we extend this solution to
     * update the basic (non-selected-item) background color as well.
     */

    protected void updateListColors(boolean isFocused, int row) {

        // TBD: do we need a special color for combo boxes?

        if (isFocused) {
            list.setSelectionForeground(UIManager.getColor("List.selectionForeground"));
            list.setSelectionBackground(UIManager.getColor("List.selectionBackground"));
        } else {
            list.setSelectionForeground(UIManager.getColor("List.inactiveSelectionForeground"));
            list.setSelectionBackground(UIManager.getColor("List.inactiveSelectionBackground"));
        }

        if (isStriped) {
            list.setBackground(getAlternateColor(row % 2));
            isStripedColorInstalled = true;
        } else if (isStripedColorInstalled) {
            list.setBackground(UIManager.getColor("List.background"));
            isStripedColorInstalled = false;
        }
    }

    @Override
    protected MouseInputListener createMouseInputListener() {
        return new QuaquaListMouseBehavior(list);
    }

    /**
     * This custom focus listener extends the behavior of the AquaListUI focus listener to repaint all of the
     * selected cells, not just the lead cell, since the selected cell background depends upon the list focus state.
     */
    protected class FocusHandler implements FocusListener {

        protected void repaintCellFocus() {
            java.util.List cells = list.getSelectedValuesList();
            if (cells.size() > 1) {
                list.repaint();
                return;
            }

            int leadIndex = list.getLeadSelectionIndex();
            if (leadIndex != -1) {
                Rectangle r = getCellBounds(list, leadIndex, leadIndex);
                if (r != null) {
                    list.repaint(r.x, r.y, r.width, r.height);
                }
            }
        }

        public void focusGained(FocusEvent event) {
            repaintCellFocus();
            AquaBorder.repaintBorder(list);
        }

        public void focusLost(FocusEvent event) {
            repaintCellFocus();
            AquaBorder.repaintBorder(list);
        }
    }

    @Override
    protected FocusListener createFocusListener() {
        return new FocusHandler();
    }

    public class PropertyChangeHandler extends BasicListUI.PropertyChangeHandler {
        @Override
        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();

            if (name.equals("Quaqua.List.style")) {
                updateStriped();
            } else if ("layoutOrientation".equals(name)) {
                updateStriped();
            }
            super.propertyChange(e);
        }
    }

    @Override
    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }
}
