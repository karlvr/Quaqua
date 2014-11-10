/*
 * Copyright (c) 2014 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.lion.filechooser;

import ch.randelshofer.quaqua.*;
import ch.randelshofer.quaqua.filechooser.CellRenderer;
import ch.randelshofer.quaqua.filechooser.FileInfo;
import ch.randelshofer.quaqua.filechooser.FileSystemTreeModel;
import ch.randelshofer.quaqua.filechooser.QuaquaFileChooserTreeTableUI;
import ch.randelshofer.quaqua.osx.OSXFile;
import com.apple.laf.AquaTableHeaderUI;
import de.sciss.treetable.j.AbstractTreeColumnModel;
import de.sciss.treetable.j.DefaultTreeTableCellRenderer;
import de.sciss.treetable.j.TreeColumnModel;
import de.sciss.treetable.j.TreeTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.KeyListener;
import java.io.File;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * List view for Lion and Mountain Lion (others by subclassing).
 */

public class ListView extends ch.randelshofer.quaqua.filechooser.ListView {

    protected final TreeTable tree;
    protected final JScrollPane listViewScrollPane;

    protected final MyTableColumnModel tableColumnModel;
    private Color labelColor;
    private Font labelFont;
    private Font headerFont;
    private Color headerColor;
    private int COLUMN_MARGIN = 10;
    private CellRenderer fileRenderer;
    protected final JFileChooser fc;
    private final TreeSelectionListener treeSelectionListener;


    public ListView(JFileChooser fc) {

        this.fc = fc;

        labelColor = UIManager.getColor("FileChooser.listView.extraColumnTextColor");
        headerColor = UIManager.getColor("FileChooser.listView.headerColor");

        labelFont = UIManager.getFont("ViewFont");  // TBD
        headerFont = UIManager.getFont("EmphasizedSmallSystemFont");    // TBD
        treeSelectionListener = new MyTreeSelectionListener();
        Color selectionForeground = Color.WHITE;

        setFocusable(false);

        /*
          A fake tree model is needed to work around a problem. JTree maintains a cache of tree model nodes for layout
          purposes. The cache is not flushed when a new tree model is supplied. The result is that the model adapter in
          TreeTable tries to ask the new model whether a cached node is a leaf. This produces a ClassCastException in
          the new model.
        */

        TreeModel fakeTreeModel = new FileSystemTreeModel(fc);
        tableColumnModel = createColumnModel();
        tree = new MyTreeTable(fakeTreeModel, new MyTreeColumnModel(), tableColumnModel);
        tree.setUI(new MyTreeTableUI(fc, tree));
        tree.putClientProperty("Quaqua.Tree.style", "striped");
        tree.setRootVisible(false);
        tree.setAlternateRowColor(tree.getBackground());
        tree.setBackground(UIManager.getColor("List.alternateBackground.0"));
        tree.setNodeSortingEnabled(false);  // very important, otherwise column layout is screwy
        tree.setRowMargin(0);
        tree.setRowHeight(18);

        /*
          Issue: I would like to get the Aqua table header background, but I would also like to control the font,
          indentation, and alignment of the column titles. Is this possible? Defining my own table header renderer
          inhibits the Aqua table header background.
        */

        tree.getTableHeader().setUI(new AquaTableHeaderUI());
        tree.setSelectionForeground(selectionForeground);
        tree.setOpaque(false);

        listViewScrollPane = new JScrollPane();
        QuaquaManager.updateNestedComponentUI(listViewScrollPane);

        JViewport vp = listViewScrollPane.getViewport();
        QuaquaManager.updateNestedComponentUI(vp);

        listViewScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        listViewScrollPane.setViewportView(tree);
        listViewScrollPane.setColumnHeaderView(tree.getTableHeader());

        setLayout(new BorderLayout());
        add(listViewScrollPane);
    }

    @Override
    public void setActive(boolean b) {
        TreeSelectionModel sm = tree.getSelectionModel();

        if (b) {
            sm.addTreeSelectionListener(treeSelectionListener);
        } else {
            sm.removeTreeSelectionListener(treeSelectionListener);
        }
    }

    @Override
    public boolean requestFocusInWindow() {
        return tree.requestFocusInWindow();
    }

    @Override
    public synchronized void addKeyListener(KeyListener l) {
        tree.addKeyListener(l);
    }

    protected class MyTreeTable extends TreeTable {
        public MyTreeTable(TreeModel tm, TreeColumnModel tcm, TableColumnModel cm) {
            super(tm, tcm, cm);
        }

        @Override
        public Icon getIcon(Object node, boolean expanded, boolean leaf) {
            if (node instanceof FileInfo) {
                FileInfo info = (FileInfo) node;
                return info.getIcon();
            }

            return super.getIcon(node, expanded, leaf);
        }
    }

    @Override
    protected void updateForNewModel() {
        tree.setTreeModel(model);
        tree.revalidate();
        tree.repaint();
    }

    @Override
    public void reconfigure() {
        setMultipleSelection(fc.isMultiSelectionEnabled());
        tree.repaint();
    }

    @Override
    public void setFileRenderer(CellRenderer r) {
        fileRenderer = r;
        tree.repaint();
    }

    @Override
    public void setMultipleSelection(boolean b) {
        tree.getSelectionModel().setSelectionMode(b ? TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION : TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    @Override
    public void setSelection(TreePath path) {
        tree.setSelectionPath(path);
    }

    @Override
    public void setSelection(List<TreePath> paths) {
        TreePath[] ps = paths.toArray(new TreePath[paths.size()]);
        tree.setSelectionPaths(ps);
    }

    @Override
    public List<TreePath> getSelection() {
        TreePath[] ps = tree.getSelectionPaths();
        return ps != null ? new ArrayList<TreePath>(Arrays.asList(ps)) : new ArrayList<TreePath>();
    }

    @Override
    public void ensurePathIsVisible(TreePath path) {
        tree.scrollPathToVisible(path);
    }

    protected class MyTreeSelectionListener implements TreeSelectionListener {
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            ListView.this.selectionChanged();
        }
    }

    protected class MyTreeTableUI extends QuaquaFileChooserTreeTableUI {

        public MyTreeTableUI(JFileChooser fc, TreeTable tt) {
            super(fc, tt);
        }

        @Override
        protected void select(TreePath path) {
            ListView.this.select(path);
        }
    }

    protected MyTableColumnModel createColumnModel() {
        return new MyTableColumnModel();
    }

    protected class MyTableColumnModel extends DefaultTableColumnModel
    {
        public TableColumn nameColumn;
        public TableColumn dateModifiedColumn;
        public TableColumn sizeColumn;
        public TableColumn kindColumn;

        public MyTableColumnModel() {
            nameColumn = new MyTableColumn(0, 200, true, "Name");
            dateModifiedColumn = new MyTableColumn(1, 80, false, "Date Modified");
            sizeColumn = new MyTableColumn(2, 65, false, "Size");
            kindColumn = new MyTableColumn(3, 150, false, "Kind");

            MyCellRenderer nameRenderer = new MyNameCellRenderer(SwingConstants.LEFT);
            MyCellRenderer leftRenderer = new MyCellRenderer(labelColor, SwingConstants.LEFT);
            MyCellRenderer rightRenderer = new MyCellRenderer(labelColor, SwingConstants.RIGHT);
            nameColumn.setCellRenderer(nameRenderer);
            dateModifiedColumn.setCellRenderer(leftRenderer);
            sizeColumn.setCellRenderer(rightRenderer);
            kindColumn.setCellRenderer(leftRenderer);

            // see comment above about Aqua table header background

            if (false) {
                DefaultTableCellRenderer leftHeaderRenderer = createHeaderCellRenderer(SwingConstants.LEFT);
                DefaultTableCellRenderer rightHeaderRenderer = createHeaderCellRenderer(SwingConstants.RIGHT);
                nameColumn.setHeaderRenderer(leftHeaderRenderer);
                dateModifiedColumn.setHeaderRenderer(leftHeaderRenderer);
                sizeColumn.setHeaderRenderer(rightHeaderRenderer);
                kindColumn.setHeaderRenderer(leftHeaderRenderer);
            }

            addColumn(nameColumn);
            addColumn(dateModifiedColumn);
            addColumn(sizeColumn);
            addColumn(kindColumn);

            setColumnMargin(COLUMN_MARGIN);
        }

        protected DefaultTableCellRenderer createHeaderCellRenderer(int align) {
            return new MyHeaderRenderer(align);
        }
    }

    protected class MyTableColumn extends TableColumn {

        /*
          Seems like a bug in JTable that the column margin is not included in the column layout calculation.
        */

        public MyTableColumn(int modelIndex, int width, boolean canWiden, String name) {
            super(modelIndex, width+ COLUMN_MARGIN);
            if (!canWiden) {
                setMaxWidth(getWidth());
            }
            setHeaderValue(name);
        }
    }

    protected class MyCellRenderer extends DefaultTreeTableCellRenderer {
        protected Color fg;

        public MyCellRenderer(Color fg, int alignment) {
            this.fg = fg;
            QuaquaManager.updateNestedComponentUI(this);
            setHorizontalAlignment(alignment);
            setFont(labelFont);
        }

        @Override
        public Component getTreeTableCellRendererComponent(TreeTable treeTable, Object value, boolean selected, boolean hasFocus, int row, int column) {
            hasFocus = false;   // avoid special display of focused cell
            Component c = super.getTreeTableCellRendererComponent(treeTable, value, selected, hasFocus, row, column);
            return fix(c, selected);
        }

        @Override
        public Component getTreeTableCellRendererComponent(TreeTable treeTable, Object value, boolean selected, boolean hasFocus, int row, int column, boolean expanded, boolean leaf) {
            Component c = super.getTreeTableCellRendererComponent(treeTable, value, selected, hasFocus, row, column, expanded, leaf);
            return fix(c, selected);
        }

        protected Component fix(Component c, boolean selected) {
            if (fg != null && !selected) {
                c.setForeground(fg);
            }

            return c;
        }
    }

    private class MyNameCellRenderer extends MyCellRenderer {
        private MyNameCellRenderer(int alignment) {
            super(null, alignment);
        }

        @Override
        public Component getTreeTableCellRendererComponent(TreeTable treeTable, Object value, boolean selected, boolean hasFocus, int row, int column, boolean expanded, boolean leaf) {
            Component c = fileRenderer.getCellRendererComponent(treeTable, value, selected, hasFocus);
            if (c == null) {
                c = super.getTreeTableCellRendererComponent(treeTable, value, selected, hasFocus, row, column, expanded, leaf);
            }

            return c;
        }
    }

    protected class MyHeaderRenderer extends DefaultTableCellRenderer {

        public MyHeaderRenderer(int alignment) {
            setHorizontalAlignment(alignment);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (headerFont != null) {
                c.setFont(headerFont);
            }
            if (headerColor != null) {
                c.setForeground(headerColor);
            }
            int columnMargin = tableColumnModel.getColumnMargin();
            c.setBorder(new EmptyBorder(0, columnMargin/2, 0, columnMargin/2));
            return c;
        }
    }

    protected class MyTreeColumnModel extends AbstractTreeColumnModel {
        private List<String> columnNames = new ArrayList<String>();

        public MyTreeColumnModel() {
            columnNames.add("Name");
            columnNames.add("Date Modified");
            columnNames.add("Size");
            columnNames.add("Kind");
        }

        @Override
      	public int getColumnCount() {
      		return columnNames.size();
      	}

      	@Override
      	public String getColumnName(int column) {
      		return columnNames.get(column);
      	}

      	@Override
      	public Object getValueAt(Object node, int column) {
      		FileSystemTreeModel.Node pn = (FileSystemTreeModel.Node)node;
            File f = pn.lazyGetResolvedFile();
            switch (column) {
                case 0:
                    return pn;  // expected by our file renderer
                case 1:
                    return f != null ? getModifiedString(f, tableColumnModel.dateModifiedColumn.getWidth()) : "";
                case 2:
                    return getLengthString(pn.getFileLength());
                case 3:
                    return getKindString(f);
            }
      		throw new IllegalArgumentException();
      	}
    }

    // Copied from FilePreview

    protected String getLengthString(long fileLength) {
        if (fileLength < 0) {
            return "--";
        } else {
            float scaledLength;
            String label;
            if (fileLength >= 1000000000l) {
                label = "FileChooser.sizeGBytesOnly";
                scaledLength = (float) fileLength / 1000000000l;
            } else if (fileLength >= 1000000l) {
                label = "FileChooser.sizeMBytesOnly";
                scaledLength = (float) fileLength / 1000000l;
            } else if (fileLength >= 1024) {
                label = "FileChooser.sizeKBytesOnly";
                scaledLength = (float) fileLength / 1000;
            } else {
                label = "FileChooser.sizeBytesOnly";
                scaledLength = (float) fileLength;
            }

        return MessageFormat.format(UIManager.getString(label), scaledLength, fileLength);
        }
    }

    protected String getModifiedString(File f, int width) {
        if (f != null) {
            Date d = new Date(f.lastModified());
            if (width < 150) {
                DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
                return df.format(d);
            } else {
                DateFormat df = DateFormat.getDateInstance();
                return df.format(d);
            }
        } else {
            return "";
        }
    }

    protected String getKindString(File f) {
        return f != null ? OSXFile.getKindString(f) : "";
    }
}
