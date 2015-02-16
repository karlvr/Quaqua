/*
 * @(#)LionFilePreview.java
 *
 * Copyright (c) 2009-2010 Werner Randelshofer, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package ch.randelshofer.quaqua.mavericks.filechooser;

import ch.randelshofer.quaqua.lion.filechooser.*;
import ch.randelshofer.quaqua.BrowserPreviewRenderer;
import ch.randelshofer.quaqua.JBrowser;
import ch.randelshofer.quaqua.filechooser.FileInfo;
import ch.randelshofer.quaqua.filechooser.QuaquaFileSystemView;
import ch.randelshofer.quaqua.osx.OSXFile;
import ch.randelshofer.quaqua.util.Worker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

/**
 * The LionFilePreview is used to render the preview column in the JBrowser in
 Quaqua's FileChooserUI's.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class MavericksFilePreview extends JPanel implements BrowserPreviewRenderer {

    private JFileChooser fileChooser;
    private boolean isFileIconAvailable = true;
    private JPanel emptyPreview;
    private FileInfo info;
    private JTable attributeView;
    private Font labelFont;
    private Font valueFont;
    private String labelDelimiter;
    private ScaledImageView previewImageView;

    public MavericksFilePreview(JFileChooser fileChooser) {
        this.fileChooser = fileChooser;

        previewImageView = new ScaledImageView();
        previewImageView.setMinimumSize(new Dimension(128, 128));
        previewImageView.setPreferredSize(new Dimension(128, 128));
        setBorder(BorderFactory.createEmptyBorder(3, 4, 4, 4));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        Color bg = UIManager.getColor("List.background");
        Color fgl = UIManager.getColor("FileChooser.previewLabelForeground");
        Color fgv = UIManager.getColor("FileChooser.previewValueForeground");
        labelFont = UIManager.getFont("FileChooser.previewLabelFont");
        valueFont = UIManager.getFont("FileChooser.previewValueFont");

        emptyPreview = new JPanel();
        emptyPreview.setBackground(bg);
        emptyPreview.setOpaque(true);

        Insets labelInsets = UIManager.getInsets("FileChooser.previewLabelInsets");

        labelDelimiter = UIManager.getString("FileChooser.previewLabelDelimiter");
        if (labelDelimiter == null) {
            labelDelimiter = "";
        }

        {
            TableColumnModel cm = new DefaultTableColumnModel();

            {
                TableColumn names = new TableColumn();
                SimpleTableCellRenderer r = new SimpleTableCellRenderer(labelFont, fgl);
                r.setHorizontalAlignment(SwingConstants.RIGHT);
                Insets borderMargin = new Insets(0, 0, 0, 0);
                r.putClientProperty("Quaqua.Component.visualMargin", borderMargin);
                names.setCellRenderer(r);
                names.setModelIndex(0);
                cm.addColumn(names);
            }

            {
                TableColumn values = new TableColumn();
                SimpleTableCellRenderer r = new SimpleTableCellRenderer(valueFont, fgv);
                r.setHorizontalAlignment(SwingConstants.LEFT);
                values.setCellRenderer(r);
                values.setModelIndex(1);
                cm.addColumn(values);
            }

            attributeView = new JTable(null, cm);
            attributeView.setIntercellSpacing(new Dimension(5, 0));
            attributeView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            attributeView.setFocusable(false);
        }

        setBackground(bg);
        attributeView.setBackground(bg);
        setOpaque(true);

        add(Box.createVerticalGlue());
        {
            Box p = new Box(BoxLayout.X_AXIS);
            p.add(Box.createHorizontalGlue());
            p.add(previewImageView);
            p.add(Box.createHorizontalGlue());
            add(p);
        }
        add(Box.createVerticalGlue());

        {
            GrayLine b = new GrayLine();
            b.setBorder(new EmptyBorder(5, 25, 5, 25));
            add(b);
        }

        {
            Box p = new Box(BoxLayout.X_AXIS);
            p.add(Box.createHorizontalGlue());
            p.add(attributeView);
            p.add(Box.createHorizontalGlue());
            p.setBorder(new EmptyBorder(0, 0, 40, 0));
            add(p);
        }

        MouseListener mouseHandler = new MouseAdapter() {

            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    MavericksFilePreview.this.fileChooser.approveSelection();
                }
            }
        };
        addMouseListener(mouseHandler);
        Component[] c = getComponents();
        for (int i = 0; i < c.length; i++) {
            c[i].addMouseListener(mouseHandler);
        }
    }

    private String toOSXPath(File file) {
        StringBuffer buf = new StringBuffer();
        FileSystemView fsv = QuaquaFileSystemView.getQuaquaFileSystemView();
        if (file != null && file.isDirectory()) {
            buf.append(':');
        }
        while (file != null) {
            buf.insert(0, fileChooser.getName(file));
            file = fsv.getParentDirectory(file);
            if (file != null) {
                buf.insert(0, ':');
            }
        }
        return buf.toString();
    }

    public Component getPreviewRendererComponent(JBrowser browser, TreePath[] paths) {

        if (paths.length > 1) {
            return emptyPreview;
        }

        Locale locale = Locale.getDefault();
        NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setMaximumFractionDigits(1);
        info = (FileInfo) paths[0].getLastPathComponent();

        if (!info.isAcceptable()) {
            return emptyPreview;
        }

        File file = info.getFile();

        AttributeTableModel m = new AttributeTableModel();
        m.add("name", info.getUserName());
        m.add("kind", OSXFile.getKindString(file));
        m.add("size", getLengthString(info.getFileLength()));
        m.add("modified", getModifiedString(file));

        {
            Date d = OSXFile.getLastUsedDate(file);
            if (d != null) {
                m.add("lastUsed", getLastUsedString(d));
            }
        }

        if (true) {
            // The original of a symlink is not displayed by NSOpenPanel, but it seems useful.
            String kind = info.getFileKind();
            if (kind == "alias") {
                File resolvedFile = info.lazyGetResolvedFile();
                if (resolvedFile != null) {
                    m.add("original", toOSXPath(resolvedFile));
                }
            }
        }

        attributeView.setModel(m);
        m.updatePreferredWidths();
        attributeView.revalidate();
        attributeView.repaint();
        updatePreviewImage();
        return this;
    }

    protected String getLengthString(long fileLength) {
        if (fileLength < 0) {
            return null;
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

    protected String getModifiedString(File f) {
        if (f != null) {
            return DateFormat.getInstance().format(new Date(f.lastModified()));
        } else {
            return null; // UIManager.getString("FileChooser.modifiedUnknown");
        }
    }

    protected String getLastUsedString(Date d) {
        return DateFormat.getInstance().format(d);
    }

    private class AttributeTableModel
        extends AbstractTableModel
    {
        private List<String> names = new ArrayList<String>();
        private List<String> values = new ArrayList<String>();
        private int nameWidth;
        private int valueWidth;

        public void add(String name, String value) {
            if (value != null && !value.isEmpty()) {
                String s = UIManager.getString("FileChooser." + name); // NOI18N
                String actualName = (s != null ? s : name) + labelDelimiter;
                names.add(actualName);
                values.add(value);
                nameWidth = Math.max(nameWidth, getTextWidth(actualName, labelFont, attributeView));
                valueWidth = Math.max(valueWidth, getTextWidth(value, valueFont, attributeView));
            }
        }

        public void updatePreferredWidths()
        {
            int fudge = 15;
            attributeView.getColumnModel().getColumn(0).setPreferredWidth(nameWidth + fudge);
            attributeView.getColumnModel().getColumn(1).setPreferredWidth(valueWidth + fudge);
        }

        @Override
        public int getRowCount() {
            return names.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int columnIndex) {
            if (columnIndex == 0) {
                return "Attribute";
            } else if (columnIndex == 1) {
                return "Value";
            } else {
                return null;
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return names.get(rowIndex);
            } else if (columnIndex == 1) {
                return values.get(rowIndex);
            } else {
                return null;
            }
        }
    }

    private void updatePreviewImage() {
        previewImageView.setVisible(isFileIconAvailable);
        previewImageView.setImage(null);

        if (info != null) {
            // Retrieving the file icon requires some potentially lengthy I/O
            // operations. Therefore we do this in a worker thread.
            final File file = info.lazyGetResolvedFile();
            if (file != null) {
                new Worker<BufferedImage>() {

                    public BufferedImage construct() {
                        BufferedImage o = null;
                        if (UIManager.getBoolean("FileChooser.quickLookEnabled") &&
                                System.getProperty("os.version").compareTo("10.6") >= 0) {
                            o = OSXFile.getQuickLookThumbnailImage(file, 800);
                        }
                        if (o == null) {
                            return OSXFile.getIconImage(file, 512);
                        } else {
                            return o;
                        }
                    }

                    @Override
                    public void done(BufferedImage value) {
                        BufferedImage fileIconImage = value;
                        isFileIconAvailable = fileIconImage != null;
                        if (isFileIconAvailable) {
                            previewImageView.setImage(fileIconImage);
                        } else {
                            previewImageView.setVisible(false);
                        }
                        previewImageView.getParent().validate();
                    }
                }.start();
            }
        }
    }


    public static int getTextWidth(String s, Font f, JComponent c) {
        Dimension size = getTextSize(s, f, c);
        return size != null ? size.width : 0;
    }

    public static Dimension getTextSize(String s, Font f, JComponent c)
   	{
   		if (f == null) {
   			f = c.getFont();
   			if (f == null) {
   				return null;
   			}
   		}

   		FontMetrics fm = null;

   		try {
   			// Workaround a Swing bug (probably old)
   			fm = c.getFontMetrics(f);
   		} catch (NullPointerException ex) {
   		}

   		if (fm == null) {
   			return null;
   		}

   		int w = fm.stringWidth(s);
   		int h = fm.getHeight();
   		return new Dimension(w, h);
   	}

    private static class SimpleTableCellRenderer extends JLabel implements TableCellRenderer {
        private Font f;
        private Color fg;

        public SimpleTableCellRenderer(Font f, Color fg) {
            this.f = f;
            this.fg = fg;
            setOpaque(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setFont(f);
            setForeground(fg);
            return this;
        }

        /**
         * Overridden for performance reasons.
         * See the <a href="#override">Implementation Note</a>
         * for more information.
         *
         * @since 1.5
         */
        public void invalidate() {}

        /**
         * Overridden for performance reasons.
         * See the <a href="#override">Implementation Note</a>
         * for more information.
         */
        public void validate() {}

        /**
         * Overridden for performance reasons.
         * See the <a href="#override">Implementation Note</a>
         * for more information.
         */
        public void revalidate() {}

        /**
         * Overridden for performance reasons.
         * See the <a href="#override">Implementation Note</a>
         * for more information.
         */
        public void repaint(long tm, int x, int y, int width, int height) {}

        /**
         * Overridden for performance reasons.
         * See the <a href="#override">Implementation Note</a>
         * for more information.
         */
        public void repaint(Rectangle r) {}

        /**
         * Overridden for performance reasons.
         * See the <a href="#override">Implementation Note</a>
         * for more information.
         */
        public void repaint() {}

        /**
         * Overridden for performance reasons.
         * See the <a href="#override">Implementation Note</a>
         * for more information.
         */
        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
            // Strings get interned...
            if (propertyName=="text"
                    || propertyName == "labelFor"
                    || propertyName == "displayedMnemonic"
                    || ((propertyName == "font" || propertyName == "foreground")
                        && oldValue != newValue
                        && getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey) != null)) {

                super.firePropertyChange(propertyName, oldValue, newValue);
            }
        }

        /**
         * Overridden for performance reasons.
         * See the <a href="#override">Implementation Note</a>
         * for more information.
         */
        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
    }

    private static class GrayLine extends JComponent {

        @Override
        public Dimension getMinimumSize() {
            Insets s = getInsets();
            return new Dimension(0, s.top + s.bottom + 1);
        }

        @Override
        public Dimension getPreferredSize() {
            return getMinimumSize();
        }

        @Override
        public Dimension getMaximumSize() {
            Insets s = getInsets();
            return new Dimension(100000, s.top + s.bottom + 1);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (isOpaque()) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            Insets s = getInsets();
            g.setColor(new Color(0xd9d9d9));
            g.drawLine(s.left, s.top, getWidth()-s.right, s.top);
        }
    }

    private static class ScaledImageView extends JComponent {
        private BufferedImage im;

        public void setImage(BufferedImage im) {
            this.im = im;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (isOpaque()) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            if (im != null) {
                Insets s = getInsets();
                int left = s.left;
                int top = s.top;
                int cwidth = getWidth() - s.left - s.right;
                int cheight = getHeight() - s.top - s.bottom;
                float imwidth = im.getWidth();
                float imheight = im.getHeight();
                if (imwidth > 0 && imheight > 0) {
                    int size = Math.min(cwidth, cheight);
                    float scale = Math.min(cwidth/imwidth, cheight/imheight);
                    int extraLeft = (int) Math.max(0, (cwidth - imwidth*scale) / 2);
                    int extraTop = (int) Math.max(0, (cheight - imheight*scale) / 2);
                    g.drawImage(im, left + extraLeft, top + extraTop, size, size, null);
                }
            }
        }
    }
}
