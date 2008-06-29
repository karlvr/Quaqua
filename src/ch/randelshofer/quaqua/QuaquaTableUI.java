/*
 * @(#)QuaquaTableUI.java  1.9  2008-05-10
 *
 * Copyright (c) 2004-2008 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.util.InactivatableColorUIResource;
import ch.randelshofer.quaqua.util.ViewportPainter;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * QuaquaTableUI.
 *
 * @author  Werner Randelshofer
 * @version 1.9 2008-05-10 Treat table as focused, if it is focused or if
 * it is editing a table cell.
 * <br>1.8.1 2008-05-03 Multiple cell selection did not work.
 * <br>1.8 2008-04-21 Set client property "terminateEditOnFocusLost" to
 * true on initDefaults. On mousePressed, requestFocusInWindow.
 * <br>1.7 2008-03-21 Made selection behavior more consistent with native
 * NSTable control. 
 * <br>1.6 2008-02-07 Reworked drawing of list selection. Implemented
 * ListSelectionListener to ensure that selection changes are properly repainted. 
 * <br>1.5 2008-01-13 Set 'showHorizontalLines' and 'showVerticalLines' to
 * false once when installing the UI, instead of overwriting these properties every
 * time when the client property "Quaqua.Table.style" is changed. 
 * <br>1.4 2007-01-16 Focus border repainting factored out into QuaquaViewportUI.
 * <br>1.3.3 2007-01-15 Change foreground color of cell renderer even if
 * it is not an UIResource.
 * <br>1.3.2 2007-01-05 Issue #6: Selection needs to be drawn differently
 * when table hasn't focus or is disabled or is on an inactive window.
 * Issue #10: Table cells mustn't draw selection background when
 * rowSelectionAllowed is false.
 * <br>1.3.1 2006-05-04 EditorCell was always drawn with alternating
 * row2 color even when the table style was not set to striped.
 * <br>1.3 2006-02-07 Support for client property "Table.isFileList" added.
 * <br>1.2.1 2005-08-25 If the table is not striped, fill the viewport with
 * the background color of the table.
 * <br>1.2 2005-03-11 LnF Property "Table.alternateBackground" replaced
 * by "Table.alternateBackground.0" and "Table.alternateBackground.1".
 * <br>1.1 2004-07-04 FocusHandler added.
 * <br>1.0  June 22, 2004  Created.
 */
public class QuaquaTableUI extends BasicTableUI
        implements ViewportPainter {

    private PropertyChangeListener propertyChangeListener;
    private ListSelectionListener listSelectionListener;
    private TableColumnModelListener columnModelListener;
    private Handler handler;
    private boolean isStriped = false;

    /** Creates a new instance. */
    public QuaquaTableUI() {
    }

    public static ComponentUI createUI(JComponent c) {
        return new QuaquaTableUI();
    }

    /**
     * Creates the key listener for handling keyboard navigation in the JTable.
     */
    protected KeyListener createKeyListener() {
        return new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                // Eat away META down keys..
                // We need to do this, because the JTable.processKeyBinding(…) 
                // method does not treat VK_META as a modifier key, and starts
                // editing a cell, whenever this key is pressed.
                if (e.getKeyCode() == KeyEvent.VK_META) {
                    e.consume();
                }
            }
        };
    }

    private Color getAlternateColor(int modulo) {
        if (modulo == 0) {
            return UIManager.getColor("Table.alternateBackground.0");
        } else {
            return UIManager.getColor("Table.alternateBackground.1");
        }
    }

    /**
     * Attaches listeners to the JTable.
     */
    protected void installListeners() {
        super.installListeners();
        propertyChangeListener = createPropertyChangeListener();
        table.addPropertyChangeListener(propertyChangeListener);
        listSelectionListener = createListSelectionListener();
        if (table.getSelectionModel() != null) {
            table.getSelectionModel().addListSelectionListener(listSelectionListener);
        }
        columnModelListener = createTableColumnModelListener();
        if (table.getColumnModel() != null) {
            table.getColumnModel().addColumnModelListener(columnModelListener);
        }
    // table.add
    }

    protected void uninstallListeners() {
        super.uninstallListeners();
        table.removePropertyChangeListener(propertyChangeListener);
        if (table.getSelectionModel() != null) {
            table.getSelectionModel().removeListSelectionListener(listSelectionListener);
        }
        if (table.getColumnModel() != null) {
            table.getColumnModel().removeColumnModelListener(columnModelListener);
        }
        propertyChangeListener = null;
        listSelectionListener = null;

    }

    protected void installDefaults() {
        super.installDefaults();
        Object property = table.getClientProperty("Quaqua.Table.style");
        isStriped = property != null && property.equals("striped");
        updateStriped();
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        // table.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);

        // By default, terminate editing on focus lost.
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

    // FIXME - Intercell spacings different from 1,1 don't work currently
    //table.setIntercellSpacing(new Dimension(4,4));
    }

    private void updateStriped() {
        /*if (isStriped) {
        table.setIntercellSpacing(new Dimension(1, 1));
        } else {
        //getTableHeader().setDefaultRenderer(new DefaultTableHeaderRenderer());
        table.setIntercellSpacing(new Dimension(1, 1));
        }*/
    }

    /** Paint a representation of the <code>table</code> instance
     * that was set in installUI().
     */
    public void paint(Graphics g, JComponent c) {
        if (table.getRowCount() <= 0 || table.getColumnCount() <= 0) {
            return;
        }
        Rectangle clip = g.getClipBounds();
        Point upperLeft = clip.getLocation();
        Point lowerRight = new Point(clip.x + clip.width - 1, clip.y + clip.height - 1);
        int rMin = table.rowAtPoint(upperLeft);
        int rMax = table.rowAtPoint(lowerRight);
        // This should never happen.
        if (rMin == -1) {
            rMin = 0;
        }
        // If the table does not have enough rows to fill the view we'll get -1.
        // Replace this with the row2 of the last row2.
        if (rMax == -1) {
            rMax = table.getRowCount() - 1;
        }

        boolean ltr = table.getComponentOrientation().isLeftToRight();
        int cMin = table.columnAtPoint(ltr ? upperLeft : lowerRight);
        int cMax = table.columnAtPoint(ltr ? lowerRight : upperLeft);
        // This should never happen.
        if (cMin == -1) {
            cMin = 0;
        }
        // If the table does not have enough columns to fill the view we'll get -1.
        // Replace this with the row2 of the last column.
        if (cMax == -1) {
            cMax = table.getColumnCount() - 1;
        }

        // Paint the cells.
        paintCells(g, rMin, rMax, cMin, cMax);
        // Paint the grid.
        paintGrid(g, rMin, rMax, cMin, cMax);
    }

    public void paintViewport(Graphics g, JViewport c) {
        Dimension vs = c.getSize();
        Dimension ts = table.getSize();
        Point p = table.getLocation();
        int rh = table.getRowHeight();
        int n = table.getRowCount();
        int row = Math.abs(p.y / rh);
        int th = n * rh - row * rh;


        if (isStriped) {
            // Fill the viewport with alternate color 1
            g.setColor(getAlternateColor(1));
            g.fillRect(0, 0, c.getWidth(), c.getHeight());

            // Now check if we need to paint some stripes
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
        } else {
            // Fill the viewport with the background color of the table
            g.setColor(table.getBackground());
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }

        // Paint the horizontal grid lines
        if (table.getShowHorizontalLines()) {
            g.setColor(table.getGridColor());
            if (ts.width < vs.width) {
                row = Math.abs(p.y / rh);
                int y = p.y + row * rh + rh - 1;
                while (y < th) {
                    g.drawLine(0, y, vs.width, y);
                    y += rh;
                }
            }
            if (th < vs.height) {
                int y = th + rh - 1;
                while (y < vs.height) {
                    g.drawLine(0, y, vs.width, y);
                    y += rh;
                }
            }
        }


        // Paint the vertical grid lines
        if (th < vs.height && table.getShowVerticalLines()) {
            g.setColor(table.getGridColor());
            TableColumnModel cm = table.getColumnModel();
            n = cm.getColumnCount();
            int y = th;
            int x = table.getX() - 1;
            for (int i = 0; i < n; i++) {
                TableColumn col = cm.getColumn(i);
                x += col.getWidth();
                g.drawLine(x, y, x, vs.height);
            }
        }
    }

    /*
     * Paints the grid lines within <I>aRect</I>, using the grid
     * color set with <I>setGridColor</I>. Paints vertical lines
     * if <code>getShowVerticalLines()</code> returns true and paints
     * horizontal lines if <code>getShowHorizontalLines()</code>
     * returns true.
     */
    private void paintGrid(Graphics g, int rMin, int rMax, int cMin, int cMax) {
        g.setColor(table.getGridColor());
        Rectangle minCell = table.getCellRect(rMin, cMin, true);
        Rectangle maxCell = table.getCellRect(rMax, cMax, true);
        Rectangle damagedArea = minCell.union(maxCell);

        if (table.getShowHorizontalLines()) {
            int tableWidth = damagedArea.x + damagedArea.width;
            int y = damagedArea.y;
            for (int row = rMin; row <= rMax; row++) {
                y += table.getRowHeight(row);
                g.drawLine(damagedArea.x, y - 1, tableWidth - 1, y - 1);
            }
        }
        if (table.getShowVerticalLines()) {
            JTableHeader header = table.getTableHeader();
            TableColumn draggedColumn = (header == null) ? null : header.getDraggedColumn();
            Rectangle vacatedColumnRect;
            if (draggedColumn != null) {
                int draggedColumnIndex = viewIndexForColumn(draggedColumn);

                Rectangle minDraggedCell = table.getCellRect(rMin, draggedColumnIndex, true);
                Rectangle maxDraggedCell = table.getCellRect(rMax, draggedColumnIndex, true);

                vacatedColumnRect = minDraggedCell.union(maxDraggedCell);

                // Move to the where the cell has been dragged.
                vacatedColumnRect.x += header.getDraggedDistance();
            } else {
                vacatedColumnRect = new Rectangle(0, 0, -1, -1);
            }

            TableColumnModel cm = table.getColumnModel();
            int tableHeight = damagedArea.y + damagedArea.height;
            int x;
            if (table.getComponentOrientation().isLeftToRight()) {
                x = damagedArea.x;
                for (int column = cMin; column <= cMax; column++) {
                    int w = cm.getColumn(column).getWidth();
                    x += w;
                    if (x < vacatedColumnRect.x || x > vacatedColumnRect.x + vacatedColumnRect.width) {
                        g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
                    }
                }
            } else {
                x = damagedArea.x + damagedArea.width;
                for (int column = cMin; column < cMax; column++) {
                    int w = cm.getColumn(column).getWidth();
                    x -= w;
                    if (x < vacatedColumnRect.x || x > vacatedColumnRect.x + vacatedColumnRect.width) {
                        g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
                    }
                }
                x -= cm.getColumn(cMax).getWidth();
                g.drawLine(x, 0, x, tableHeight - 1);
            }
        }
    }

    private void paintDraggedArea(Graphics g, int rMin, int rMax, TableColumn draggedColumn, int distance) {
        int draggedColumnIndex = viewIndexForColumn(draggedColumn);

        Rectangle minCell = table.getCellRect(rMin, draggedColumnIndex, true);
        Rectangle maxCell = table.getCellRect(rMax, draggedColumnIndex, true);

        Rectangle vacatedColumnRect = minCell.union(maxCell);

        // Paint a gray well in place of the moving column.
        g.setColor(table.getParent().getBackground());
        g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y,
                vacatedColumnRect.width, vacatedColumnRect.height);

        // Move to the where the cell has been dragged.
        vacatedColumnRect.x += distance;

        // Fill the background.
        g.setColor(table.getBackground());
        g.fillRect(vacatedColumnRect.x, vacatedColumnRect.y,
                vacatedColumnRect.width, vacatedColumnRect.height);

        // Paint the vertical grid lines if necessary.
        if (table.getShowVerticalLines()) {
            g.setColor(table.getGridColor());
            int x1 = vacatedColumnRect.x;
            int y1 = vacatedColumnRect.y;
            int x2 = x1 + vacatedColumnRect.width - 1;
            int y2 = y1 + vacatedColumnRect.height - 1;
            // Left
            g.drawLine(x1 - 1, y1, x1 - 1, y2);
            // Right
            g.drawLine(x2, y1, x2, y2);
        }

        boolean isFocused = isFocused();

        for (int row = rMin; row <= rMax; row++) {
            // Render the cell value
            Rectangle r = table.getCellRect(row, draggedColumnIndex, false);
            r.x += distance;
            paintCell(g, r, row, draggedColumnIndex, isFocused);

            // Paint the (lower) horizontal grid line if necessary.
            if (table.getShowHorizontalLines()) {
                g.setColor(table.getGridColor());
                Rectangle rcr = table.getCellRect(row, draggedColumnIndex, true);
                rcr.x += distance;
                int x1 = rcr.x;
                int y1 = rcr.y;
                int x2 = x1 + rcr.width - 1;
                int y2 = y1 + rcr.height - 1;
                g.drawLine(x1, y2, x2, y2);
            }
        }
    }

    private int viewIndexForColumn(TableColumn aColumn) {
        TableColumnModel cm = table.getColumnModel();
        for (int column = 0; column < cm.getColumnCount(); column++) {
            if (cm.getColumn(column) == aColumn) {
                return column;
            }
        }
        return -1;
    }

    private boolean isFocused() {
     return table.isEditing() || QuaquaUtilities.isFocused(table);
    }
    
    private void paintCells(Graphics g, int rMin, int rMax, int cMin, int cMax) {
        // Ugly dirty hack to get correct painting of inactive tables
        boolean isFocused = isFocused();

        JTableHeader header = table.getTableHeader();
        TableColumn draggedColumn = (header == null) ? null : header.getDraggedColumn();

        TableColumnModel cm = table.getColumnModel();
        int columnMargin = cm.getColumnMargin();

        Rectangle cellRect;
        TableColumn aColumn;
        int columnWidth;
        if (table.getComponentOrientation().isLeftToRight()) {
            for (int row = rMin; row <= rMax; row++) {
                cellRect = table.getCellRect(row, cMin, false);
                for (int column = cMin; column <= cMax; column++) {
                    aColumn = cm.getColumn(column);
                    columnWidth = aColumn.getWidth();
                    cellRect.width = columnWidth - columnMargin;
                    if (aColumn != draggedColumn) {
                        paintCell(g, cellRect, row, column, isFocused);
                    }
                    cellRect.x += columnWidth;
                }
            }
        } else {
            for (int row = rMin; row <= rMax; row++) {
                cellRect = table.getCellRect(row, cMin, false);
                aColumn = cm.getColumn(cMin);
                if (aColumn != draggedColumn) {
                    columnWidth = aColumn.getWidth();
                    cellRect.width = columnWidth - columnMargin;
                    paintCell(g, cellRect, row, cMin, isFocused);
                }
                for (int column = cMin + 1; column <= cMax; column++) {
                    aColumn = cm.getColumn(column);
                    columnWidth = aColumn.getWidth();
                    cellRect.width = columnWidth - columnMargin;
                    cellRect.x -= columnWidth;
                    if (aColumn != draggedColumn) {
                        paintCell(g, cellRect, row, column, isFocused);
                    }
                }
            }
        }

        // Paint the dragged column if we are dragging.
        if (draggedColumn != null) {
            paintDraggedArea(g, rMin, rMax, draggedColumn, header.getDraggedDistance());
        }

        // Remove any renderers that may be left in the rendererPane.
        rendererPane.removeAll();

        // Ugly dirty hack to get proper rendering of inactive tables
        // Here we clean up the values of the "active" property of the selection
        // colors.
        if (!isFocused) {
            Color background = UIManager.getColor("Table.selectionBackground");
            Color foreground = UIManager.getColor("Table.selectionForeground");
            if (background instanceof InactivatableColorUIResource) {
                ((InactivatableColorUIResource) background).setActive(true);
            }
            if (foreground instanceof InactivatableColorUIResource) {
                ((InactivatableColorUIResource) foreground).setActive(true);
            }
        }
    }

    private void paintCell(Graphics g, Rectangle cellRect, int row, int column, boolean isFocused) {
        Color background = UIManager.getColor("Table.selectionBackground");
        Color foreground = UIManager.getColor("Table.selectionForeground");
        if (background instanceof InactivatableColorUIResource) {
            ((InactivatableColorUIResource) background).setActive(isFocused && table.getRowSelectionAllowed());
        }
        if (foreground instanceof InactivatableColorUIResource) {
            ((InactivatableColorUIResource) foreground).setActive(isFocused && table.getRowSelectionAllowed());
        }

        Dimension spacing = table.getIntercellSpacing();
        if (table.getShowHorizontalLines()) {
            spacing.height -= 1;
        }
        if (table.getShowVerticalLines()) {
            spacing.width -= 1;
        }

        if (table.isEditing() && table.getEditingRow() == row &&
                table.getEditingColumn() == column) {
            Component component = table.getEditorComponent();
            //  component.setBackground((isStriped) ? getAlternateColor(row2 % 2) : table.getBackground());
            // We only need to paint the alternate background color for even
            // rows, because the background for uneven rows is painted by
            // method paintViewport().
/*
            if (isStriped && row2 % 2 == 0) {
            g.setColor(getAlternateColor(0));
            g.fillRect(cellRect.x - spacing.width, cellRect.y, cellRect.width + spacing.width * 2, cellRect.height + spacing.height);
            }*/
            component.setFont(table.getFont());
            component.setBounds(cellRect);
            component.validate();
        } else {
            TableCellRenderer renderer = table.getCellRenderer(row, column);
            Component component = table.prepareRenderer(renderer, row, column);

            if (isStriped) {
                g.setColor(getAlternateColor(row % 2));
                g.fillRect(cellRect.x - spacing.width, cellRect.y, cellRect.width + spacing.width * 2, cellRect.height + spacing.height);
            }
            if (/*!table.isEditing() &&*/table.isCellSelected(row, column)) {
                g.setColor(background);
                g.fillRect(cellRect.x - spacing.width, cellRect.y, cellRect.width + spacing.width * 2, cellRect.height);
            }

            if ((component instanceof UIResource) && (component instanceof JComponent)) {
                ((JComponent) component).setOpaque(false);
            }

            //component.setBackground(background);
            rendererPane.paintComponent(g, component, table, cellRect.x, cellRect.y,
                    cellRect.width, cellRect.height, true);

        }
    }

    /**
     * Creates the mouse listener for the JTable.
     */
    protected MouseInputListener createMouseInputListener() {
        // Compatibility with SoyLatte: 
        // Only use our own mouse listener on Java 1.4 and 1.5,
        // it does not work with J2SE6.
        if (System.getProperty("java.version").startsWith("1.4") ||
                System.getProperty("java.version").startsWith("1.5")) {
            return new MouseHandler();
        } else {
            return super.createMouseInputListener();
        }
    }

    /**
     * Creates the property change listener for the JTable.
     */
    private PropertyChangeListener createPropertyChangeListener() {
        return getHandler();
    }

    /**
     * Creates the list selection listener for the JTable.
     */
    private ListSelectionListener createListSelectionListener() {
        return getHandler();
    }

    /**
     * Creates the list selection listener for the JTable.
     */
    private TableColumnModelListener createTableColumnModelListener() {
        return getHandler();
    }

    /**
     * Lazily creates the handler.
     */
    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    /**
     * Creates the focus listener for handling keyboard navigation in the JTable.
     */
    protected FocusListener createFocusListener() {
        return new FocusHandler();
    }
    //
    //  The Table's focus listener
    //

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicTableUI.
     */
    /**
     * PropertyChangeListener for the table. Updates the appropriate
     * varaible, or TreeState, based on what changes.
     */
    private class Handler implements
            PropertyChangeListener, ListSelectionListener, TableColumnModelListener {

        private boolean rowSelectionAdjusting;

        public void propertyChange(PropertyChangeEvent event) {
            String name = event.getPropertyName();

            if (name.equals("Quaqua.Table.style")) {
                Object value = event.getNewValue();
                isStriped = value != null && value.equals("striped");
                updateStriped();
            } else if (name.equals("showVerticalLines") ||
                    name.equals("showHorizontalLines")) {
                if (table.getParent() instanceof JViewport) {
                    table.getParent().repaint();
                }
            } else if (name.equals("selectionModel")) {
                if (event.getOldValue() != null) {
                    ((ListSelectionModel) event.getOldValue()).removeListSelectionListener(listSelectionListener);
                }
                if (event.getNewValue() != null) {
                    ((ListSelectionModel) event.getNewValue()).addListSelectionListener(listSelectionListener);
                }
            } else if (name.equals("columnModel")) {
                if (event.getOldValue() != null) {
                    ((TableColumnModel) event.getOldValue()).removeColumnModelListener(columnModelListener);
                }
                if (event.getNewValue() != null) {
                    ((TableColumnModel) event.getNewValue()).addColumnModelListener(columnModelListener);
                }
            } else if (name.equals("tableCellEditor")) {
                table.repaint();
            }
        }

        public void columnAdded(TableColumnModelEvent e) {
        }

        public void columnRemoved(TableColumnModelEvent e) {
        }

        public void columnMoved(TableColumnModelEvent e) {
        }

        public void columnMarginChanged(ChangeEvent e) {
        }

        private int getAdjustedIndex(int index, boolean row) {
            int compare = row ? table.getRowCount() : table.getColumnCount();
            return index < compare ? index : -1;
        }

        public void columnSelectionChanged(ListSelectionEvent e) {
            ListSelectionModel selectionModel = table.getSelectionModel();
            int firstIndex = limit(e.getFirstIndex(), 0, table.getColumnCount() - 1);
            int lastIndex = limit(e.getLastIndex(), 0, table.getColumnCount() - 1);
            int minRow = 0;
            int maxRow = table.getRowCount() - 1;
            if (table.getRowSelectionAllowed()) {
                minRow = selectionModel.getMinSelectionIndex();
                maxRow = selectionModel.getMaxSelectionIndex();
                int leadRow = getAdjustedIndex(selectionModel.getLeadSelectionIndex(), true);

                if (minRow == -1 || maxRow == -1) {
                    if (leadRow == -1) {
                        // nothing to repaint, return
                        return;
                    }

                    // only thing to repaint is the lead
                    minRow = maxRow = leadRow;
                } else {
                    // We need to consider more than just the range between
                    // the min and max selected index. The lead row, which could
                    // be outside this range, should be considered also.
                    if (leadRow != -1) {
                        minRow = Math.min(minRow, leadRow);
                        maxRow = Math.max(maxRow, leadRow);
                    }
                }
            }
            Rectangle firstColumnRect = table.getCellRect(minRow, firstIndex, false);
            Rectangle lastColumnRect = table.getCellRect(maxRow, lastIndex, false);
            Rectangle dirtyRegion = firstColumnRect.union(lastColumnRect);
            Dimension intercellSpacing = table.getIntercellSpacing();
            if (intercellSpacing != null) {
            dirtyRegion.width += table.getIntercellSpacing().width;
            }
            table.repaint(dirtyRegion);
        }

        /**
         * This is a reimplementation of the JTable.valueChanged method,
         * with the only difference, that we repaint the cells _including_ the
         * intercell spacing.
         * 
         * @param e
         */
        public void valueChanged(ListSelectionEvent e) {
            boolean isAdjusting = e.getValueIsAdjusting();
            if (rowSelectionAdjusting && !isAdjusting) {
                // The assumption is that when the model is no longer adjusting
                // we will have already gotten all the changes, and therefore
                // don't need to do an additional paint.
                rowSelectionAdjusting = false;
                return;
            }
            rowSelectionAdjusting = isAdjusting;
            // The getCellRect() calls will fail unless there is at least one column.
            if (table.getRowCount() <= 0 || table.getColumnCount() <= 0) {
                return;
            }
            int firstIndex = limit(e.getFirstIndex(), 0, table.getRowCount() - 1);
            int lastIndex = limit(e.getLastIndex(), 0, table.getRowCount() - 1);
            Rectangle firstRowRect = table.getCellRect(firstIndex, 0, true);
            Rectangle lastRowRect = table.getCellRect(lastIndex, table.getColumnCount() - 1, true);
            Rectangle dirtyRegion = firstRowRect.union(lastRowRect);
            dirtyRegion.width += table.getIntercellSpacing().width;
            table.repaint(dirtyRegion);
        }

        private int limit(int i, int a, int b) {
            return Math.min(b, Math.max(i, a));
        }
    } // End of BasicTableUI.Handler


    public class MouseHandler implements MouseInputListener {

        // Component receiving mouse events during editing.
        // May not be editorComponent.
        private Component dispatchComponent;
        private boolean selectedOnPress;
        private boolean mouseReleaseDeselects;
        private boolean mouseDragSelects;

        //  The Table's mouse listener methods.
        public void mouseClicked(MouseEvent e) {
        }

        private void setDispatchComponent(MouseEvent e) {
            Component editorComponent = table.getEditorComponent();
            Point p = e.getPoint();
            Point p2 = SwingUtilities.convertPoint(table, p, editorComponent);
            dispatchComponent = SwingUtilities.getDeepestComponentAt(editorComponent,
                    p2.x, p2.y);
        }

        private boolean repostEvent(MouseEvent e) {
            // Check for isEditing() in case another event has
            // caused the editor to be removed. See bug #4306499.
            if (dispatchComponent == null || !table.isEditing()) {
                return false;
            }
            MouseEvent e2 = SwingUtilities.convertMouseEvent(table, e, dispatchComponent);
            dispatchComponent.dispatchEvent(e2);
            return true;
        }

        private void setValueIsAdjusting(boolean flag) {
            table.getSelectionModel().setValueIsAdjusting(flag);
            table.getColumnModel().getSelectionModel().setValueIsAdjusting(flag);
        }

        private boolean shouldIgnore(MouseEvent e) {
            return e.isConsumed() || (!(SwingUtilities.isLeftMouseButton(e) && table.isEnabled())) || e.isPopupTrigger() &&
                    (table.rowAtPoint(e.getPoint()) == -1 ||
                    table.isRowSelected(table.rowAtPoint(e.getPoint())));
        }

        public void mousePressed(MouseEvent e) {
            mouseDragSelects = false;
            mouseReleaseDeselects = false;

            Point p = e.getPoint();
            int row = table.rowAtPoint(p);
            int column = table.columnAtPoint(p);

            // Note: We must check for table.editCellAt, regardless whether
            // the table is currently editing or not.
            //---if (! table.isEditing()) {
//            table.requestFocus();
            if (table.editCellAt(row, column, e)) {
                setDispatchComponent(e);
                repostEvent(e);
            }
            //---}

            // Note: Some applications depend on selection changes only occuring
            // on focused components. Maybe we must not do any changes to the
            // selection changes at all, when the compnent is not focused?
            table.requestFocusInWindow();

            if (row != -1 && column != -1) {
                if (table.isRowSelected(row) && e.isPopupTrigger()) {
                    // Do not change the selection, if the item is already
                    // selected, and the user triggers the popup menu.
                } else {
                    int anchorIndex = table.getSelectionModel().getAnchorSelectionIndex();

                    if ((e.getModifiersEx() & (MouseEvent.META_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK)) == MouseEvent.META_DOWN_MASK) {
                        if (table.isCellSelected(row, column)) {
                            // deselect the cell:
                            table.changeSelection(row, column, true, false);
                        //table.getSelectionModel().removeSelectionInterval(row, row);
                        } else {
                            // add the cell to the selection:
                            table.changeSelection(row, column, true, false);
                            //table.addRowSelectionInterval(row, row);
                            mouseDragSelects = true;
                        }
                    } else if ((e.getModifiersEx() & (MouseEvent.SHIFT_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK)) == MouseEvent.SHIFT_DOWN_MASK &&
                            anchorIndex != -1) {
                        // add all rows to the selection from the anchor to the row
                        table.changeSelection(row, column, false, true);
                        //table.setRowSelectionInterval(anchorIndex, row);
                        mouseDragSelects = true;
                    } else if ((e.getModifiersEx() & (MouseEvent.SHIFT_DOWN_MASK | MouseEvent.META_DOWN_MASK)) == 0) {
                        if (table.isCellSelected(row, column)) {
                            mouseReleaseDeselects = table.isFocusOwner();
                        } else {
                            // Only select the cell
                            table.changeSelection(row, column, false, false);
                            //table.setRowSelectionInterval(row, row);
                            mouseDragSelects = true;
                        }
                    //table.getSelectionModel().setAnchorSelectionIndex(row);
                    }
                }
            }

            table.getSelectionModel().setValueIsAdjusting(mouseDragSelects);
        /*
        if (e.isConsumed()) {
        selectedOnPress = false;
        return;
        }
        selectedOnPress = true;
        mouseReleaseDeselects = true;
        adjustFocusAndSelection(e);
         */
        }

        public void mouseReleased(MouseEvent e) {
            repostEvent(e);
            mouseDragSelects = false;
            if (mouseReleaseDeselects) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());
                table.changeSelection(row, column, false, false);
            }
            table.getSelectionModel().setValueIsAdjusting(false);

            if (table.isRequestFocusEnabled() && !table.isEditing()) {
                table.requestFocus();
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        //  The Table's mouse motion listener methods.
        public void mouseMoved(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
            if (shouldIgnore(e)) {
                return;
            }
            /*
            mouseReleaseDeselects = false;
            repostEvent(e);
            CellEditor editor = table.getCellEditor();
            if (editor == null || editor.shouldSelectCell(e)) {
            Point p = e.getPoint();
            int row2 = table.rowAtPoint(p);
            int column = table.columnAtPoint(p);
            // The autoscroller can generate drag events outside the Table's range.
            if ((column == -1) || (row2 == -1)) {
            return;
            }
            // Fix for 4835633
            // Until we support drag-selection, dragging should not change
            // the selection (act like single-select).
            Object bySize = table.getClientProperty("Table.isFileList");
            if (bySize instanceof Boolean &&
            ((Boolean) bySize).booleanValue()) {
            return;
            }
            table.changeSelection(row2, column, false, true);
            }*/
            CellEditor editor = table.getCellEditor();
            if (editor == null || editor.shouldSelectCell(e)) {
                mouseReleaseDeselects = false;
                if (mouseDragSelects) {
                    int row = table.rowAtPoint(e.getPoint());
                    int column = table.columnAtPoint(e.getPoint());
                    if (row != -1 && column != -1) {
                        Rectangle cellBounds = table.getCellRect(row, column, true);
                        table.scrollRectToVisible(cellBounds);
//                        int anchorIndex = table.getSelectionModel().getAnchorSelectionIndex();
//                        table.setRowSelectionInterval(anchorIndex, row);
                        table.changeSelection(row, column, false, true);
                    }
                }
            }
        }
    }

    private class FocusHandler implements FocusListener {
        // FocusListener

        private void repaintSelection() {
            int[] rows = table.getSelectedRows();
            Rectangle dirtyRect = null;
            for (int r = 0; r < rows.length; r++) {
                for (int c = 0, n = table.getColumnCount(); c < n; c++) {
                    table.repaint(table.getCellRect(rows[r], c, false));
                }
            }
        }

        public void focusGained(FocusEvent e) {
            repaintSelection();
        }

        public void focusLost(FocusEvent e) {
            repaintSelection();
        }
    }
}
