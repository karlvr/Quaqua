/*
 * Copyright (c) 2014 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement. For details see accompanying license terms.
 */
package ch.randelshofer.quaqua.mavericks.filechooser;

import ch.randelshofer.quaqua.QuaquaTableHeaderUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * List view for Mavericks.
 */
public class ListView extends ch.randelshofer.quaqua.lion.filechooser.ListView {

    protected Border tableHeaderBorder;
    protected Color tableHeaderLineColor;

    public ListView(JFileChooser fc) {
        super(fc);

        tableHeaderLineColor = UIManager.getColor("FileChooser.listView.headerBorderColor");
        int columnMargin = tableColumnModel.getColumnMargin();
        tableHeaderBorder = new EmptyBorder(5, columnMargin/2, 7, columnMargin/2);
        tree.getTableHeader().setUI(new MyTableHeaderUI());
    }

    protected MyTableColumnModel createColumnModel() {
        return new MyTableColumnModel();
    }

    protected class MyTableColumnModel extends ch.randelshofer.quaqua.lion.filechooser.ListView.MyTableColumnModel {
        @Override
        protected DefaultTableCellRenderer createHeaderCellRenderer(int align) {
            return new MyHeaderRenderer(align);
        }
    }

    protected class MyHeaderRenderer extends ch.randelshofer.quaqua.lion.filechooser.ListView.MyHeaderRenderer {
        public MyHeaderRenderer(int alignment) {
            super(alignment);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setBorder(tableHeaderBorder);
            return c;
        }
    }

    protected class MyTableHeaderUI extends QuaquaTableHeaderUI {
        @Override
        public void paint(Graphics g, JComponent c) {
            super.paint(g, c);
            g.setColor(tableHeaderLineColor);
            g.fillRect(0, c.getHeight()-2, c.getWidth(), 1);
        }
    }
}
