/*
 * @(#)FilePreview.java  2.2.1  2007-11-25
 *
 * Copyright (c) 2005-2006 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua.panther.filechooser;

import ch.randelshofer.quaqua.*;
import ch.randelshofer.quaqua.filechooser.*;
import ch.randelshofer.quaqua.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.tree.*;
/**
 * The FilePreview is used to render the preview column in the JBrowser in
 * Quaqua's FileChooserUI's.
 *
 * @author  Werner Randelshofer
 * @version 2.2.1 2007-11-25 Preview icon was rendered with a size of 64
 * pixels instead of 128 pixels. 
 * <br>2.2 2007-02-04 Double click on file preview approves the selection. 
 * <br>2.1 2006-09-23 Display nothing, if the file is not acceptable by
 * the JFileChooser.
 * <br>2.0 2006-05-07 Render an icon image. Display a label delimiter
 * provided by the UIManager "FileChooser.previewLabelDelimiter".
 * <br>1.3.1 2006-04-23 Labels are now retrieved directly from UIManager.
 * <br>1.3 2005-11-26 Get information about the file object from FileInfo
 * interface instead of casting to a AliasFileSystemTreeModel.Node.
 * <br>1.2.2 2005-11-07 Get "Labels" ResourceBundle from UIManager.
 * <br>1.2.1 2005-07-13 Fixed NullPointerException when multiple files
 * are selected in the file chooser.
 * <br>1.2 2005-06-18 Added support for different colors and fonts for
 * preview value and preview label.
 * <br>1.1.1 2005-06-15 Added commented out code which invokes
 * getTypeDescription on the FileView object instead of using
 * text constants. Unfortunately we don't get a good description; just
 * "Directory" and "Generic File".
 * <br>1.1 2005-03-21 Layout did not work as expected with Java 1.3.
 * <br>1.0  26 January 2005  Created.
 */
public class FilePreview extends JPanel implements BrowserPreviewRenderer {
    private JFileChooser fileChooser;
    private static Icon placeholderIcon = new Icon() {
        public void paintIcon(Component c, Graphics g, int x, int y) {
        }
        
        public int getIconWidth() {
            return 128;
        }
        
        public int getIconHeight() {
            return 128;
        }
        
    };
    private boolean isFileIconAvailable = true;
    private JPanel emptyPreview;
    
    /** Creates new form. */
    public FilePreview(JFileChooser fileChooser) {
        this.fileChooser = fileChooser;
        initComponents();
        
        Color bg = UIManager.getColor("List.background");
        Color fgl = UIManager.getColor("FileChooser.previewLabelForeground");
        Color fgv = UIManager.getColor("FileChooser.previewValueForeground");
        Font fl = UIManager.getFont("FileChooser.previewLabelFont");
        Font fv = UIManager.getFont("FileChooser.previewValueFont");
        
        emptyPreview = new JPanel();
        emptyPreview.setBackground(bg);
        emptyPreview.setOpaque(true);
        
        Insets labelInsets = UIManager.getInsets("FileChooser.previewLabelInsets");
        GridBagLayout layout = (GridBagLayout) northPanel.getLayout();
        
        String delimiter = UIManager.getString("FileChooser.previewLabelDelimiter");
        if (delimiter == null) delimiter = "";
        
        for (int i=0, n=northPanel.getComponentCount(); i < n; i++) {
            JComponent c = (JComponent) northPanel.getComponent(i);
            if (c != iconLabel) {
                if (c instanceof JLabel) {
                    JLabel label = (JLabel) c;
                    c.setFont(fl);
                    c.setForeground(fgl);
                    if (labelInsets != null) {
                        GridBagConstraints gbc = layout.getConstraints(c);
                        gbc.insets = labelInsets;
                        layout.setConstraints(c, gbc);
                    }
                    label.setText(label.getText()+delimiter);
                } else {
                    c.setFont(fv);
                    c.setForeground(fgv);
                }
            }
            c.setBackground(bg);
        }
        
        // We do not show the location of the file, because this information
        // is already provided by the file chooser.
        whereLabel.setVisible(false);
        whereText.setVisible(false);
        
        // Remove border Margin
        Insets borderMargin = new Insets(0,0,0,0);
        kindLabel.putClientProperty("Quaqua.Component.visualMargin", borderMargin);
        modifiedLabel.putClientProperty("Quaqua.Component.visualMargin", borderMargin);
        nameLabel.putClientProperty("Quaqua.Component.visualMargin", borderMargin);
        originalLabel.putClientProperty("Quaqua.Component.visualMargin", borderMargin);
        sizeLabel.putClientProperty("Quaqua.Component.visualMargin", borderMargin);
        whereLabel.putClientProperty("Quaqua.Component.visualMargin", borderMargin);
        
        // Disable focus traversability
       Methods.invokeIfExists(kindText, "setFocusable", false);
       Methods.invokeIfExists(modifiedText, "setFocusable", false);
       Methods.invokeIfExists(nameText, "setFocusable", false);
       Methods.invokeIfExists(originalText, "setFocusable", false);
       Methods.invokeIfExists(sizeText, "setFocusable", false);
       Methods.invokeIfExists(whereText, "setFocusable", false);
        
        setBackground(bg);
        northPanel.setBackground(bg);
        setOpaque(true);
        
        MouseListener mouseHandler = new MouseAdapter() {
          public void mouseClicked(MouseEvent evt) {
              if (evt.getClickCount() == 2) {
                  FilePreview.this.fileChooser.approveSelection();
              }
          }  
        };
        addMouseListener(mouseHandler);
        Component[] c = getComponents();
        for (int i=0; i < c.length; i++) {
            c[i].addMouseListener(mouseHandler);
        }
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        northPanel = new javax.swing.JPanel();
        iconLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        nameText = new javax.swing.JTextArea();
        kindLabel = new javax.swing.JLabel();
        kindText = new javax.swing.JTextArea();
        sizeLabel = new javax.swing.JLabel();
        sizeText = new javax.swing.JTextArea();
        modifiedLabel = new javax.swing.JLabel();
        modifiedText = new javax.swing.JTextArea();
        whereLabel = new javax.swing.JLabel();
        whereText = new javax.swing.JTextArea();
        originalLabel = new javax.swing.JLabel();
        originalText = new javax.swing.JTextArea();

        setLayout(new java.awt.BorderLayout());

        setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 4, 4, 4));
        northPanel.setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 12, 0);
        northPanel.add(iconLabel, gridBagConstraints);

        nameLabel.setText(UIManager.getString("FileChooser.name"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        northPanel.add(nameLabel, gridBagConstraints);

        nameText.setEditable(false);
        nameText.setLineWrap(true);
        nameText.setWrapStyleWord(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        northPanel.add(nameText, gridBagConstraints);

        kindLabel.setText(UIManager.getString("FileChooser.kind"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        northPanel.add(kindLabel, gridBagConstraints);

        kindText.setEditable(false);
        kindText.setLineWrap(true);
        kindText.setWrapStyleWord(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        northPanel.add(kindText, gridBagConstraints);

        sizeLabel.setText(UIManager.getString("FileChooser.size"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        northPanel.add(sizeLabel, gridBagConstraints);

        sizeText.setEditable(false);
        sizeText.setLineWrap(true);
        sizeText.setWrapStyleWord(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        northPanel.add(sizeText, gridBagConstraints);

        modifiedLabel.setText(UIManager.getString("FileChooser.modified"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        northPanel.add(modifiedLabel, gridBagConstraints);

        modifiedText.setEditable(false);
        modifiedText.setLineWrap(true);
        modifiedText.setWrapStyleWord(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        northPanel.add(modifiedText, gridBagConstraints);

        whereLabel.setText(UIManager.getString("FileChooser.whereLabelText"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        northPanel.add(whereLabel, gridBagConstraints);

        whereText.setEditable(false);
        whereText.setLineWrap(true);
        whereText.setWrapStyleWord(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        northPanel.add(whereText, gridBagConstraints);

        originalLabel.setText(UIManager.getString("FileChooser.original"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        northPanel.add(originalLabel, gridBagConstraints);

        originalText.setEditable(false);
        originalText.setLineWrap(true);
        originalText.setWrapStyleWord(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        northPanel.add(originalText, gridBagConstraints);

        add(northPanel, java.awt.BorderLayout.NORTH);

    }// </editor-fold>//GEN-END:initComponents
    
    private String toOSXPath(File file) {
        StringBuffer buf = new StringBuffer();
        FileSystemView fsv = QuaquaFileSystemView.getQuaquaFileSystemView();
        if (file != null && file.isDirectory()) {
            buf.append(':');
        }
        while (file != null) {
            buf.insert(0, fileChooser.getName(file));
            file = fsv.getParentDirectory(file);
            if (file != null) buf.insert(0, ':');
        }
        return buf.toString();
    }
    
    public Component getPreviewRendererComponent(JBrowser browser, TreePath[] paths) {
        Locale locale = Locale.getDefault();
        NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setMaximumFractionDigits(1);
        FileInfo info = (FileInfo) paths[0].getLastPathComponent();

        if (! info.isAcceptable()) {
            return emptyPreview;
        }
        
        // We do not display the location of the file, because this is already
        // provided by the file chooser.
        //whereText.setText(toOSXPath(QuaquaManager.getFileSystemView().getParentDirectory(info.getUnresolvedFile())));
        
        long fileLength = 0;
        if (paths.length == 1) {
            nameLabel.setVisible(true);
            modifiedLabel.setVisible(true);
            modifiedText.setVisible(true);
            nameText.setText(info.getUserName());
            File file = info.getFile();
            fileLength = info.getFileLength();
            if (file != null) {
            modifiedText.setText(DateFormat.getInstance().format(new Date(file.lastModified())));
            } else {
                modifiedText.setText(UIManager.getString("FileChooser.modifiedUnknown"));
            }
            String kind = info.getFileKind();
            kindText.setText(Files.getKindString(file));
            if (kind == "alias") {
                originalText.setText(toOSXPath(info.lazyGetResolvedFile()));
                originalLabel.setVisible(true);
                originalText.setVisible(true);
            } else {
                originalLabel.setVisible(false);
                originalText.setVisible(false);
            }
        } else {
            nameLabel.setVisible(false);
            modifiedLabel.setVisible(false);
            modifiedText.setVisible(false);
            nameText.setText(
                    MessageFormat.format(
                    UIManager.getString("FileChooser.items"),
                    new Object[] {nf.format(paths.length)}
            )
            );
            TreeMap kinds = new TreeMap();
            for (int i=0; i < paths.length; i++) {
                info = (AliasFileSystemTreeModel.Node) paths[i].getLastPathComponent();
                if (fileLength != -1) {
                    if (info.getFileLength() == -1) {
                        fileLength = -1;
                    } else {
                        fileLength += info.getFileLength();
                    }
                }
                String kind = info.getFileKind();
                Integer kindCount = (Integer) kinds.get(kind);
                kinds.put(kind, (kindCount == null) ? new Integer(1) : new Integer(kindCount.intValue() + 1));
            }
            StringBuffer buf = new StringBuffer();
            for (Iterator i = kinds.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry entry = (Map.Entry) i.next();
                buf.append(MessageFormat.format(
                        UIManager.getString("FileChooser."+entry.getKey()+"Count"),
                        new Object[] {entry.getValue()}
                ));
                if (i.hasNext()) {
                    buf.append(", ");
                }
            }
            kindText.setText(buf.toString());
            originalLabel.setVisible(false);
            originalText.setVisible(false);
        }
        
        String label;
        float scaledLength;
        if (fileLength == -1) {
            label = "FileChooser.sizeUnknown";
            scaledLength = 0f;
        } else {
            if (fileLength >= 1024*1024*1024) {
                label = "FileChooser.sizeGBytes";
                scaledLength = fileLength / (1024f*1024f*1024f);
            } else if (fileLength >= 1024*1024) {
                label = "FileChooser.sizeMBytes";
                scaledLength = fileLength / (1024f*1024f);
            } else if (fileLength >= 1024) {
                label = "FileChooser.sizeKBytes";
                scaledLength = fileLength / (1024f);
            } else {
                label = "FileChooser.sizeBytes";
                scaledLength = (float) fileLength;
            }
        }

        sizeText.setText(
                MessageFormat.format(UIManager.getString(label), new Object[] {
            new Float(scaledLength),
            new Long(fileLength),
            new Integer(paths.length)
        })
        );
        
        // Retrieving the file icon requires some potentially lengthy I/O
        // operations. Therefore we do this in a worker thread.
        final File file = info.lazyGetResolvedFile();
        iconLabel.setVisible(isFileIconAvailable);
        iconLabel.setIcon(placeholderIcon);
        if (file != null) {
        new SwingWorker() {
            public Object construct() {
                return Files.getIconImage(file, 128);
            }
            public void finished() {
                Image fileIconImage = (Image) getValue();
                isFileIconAvailable = fileIconImage != null;
                if (isFileIconAvailable) {
                    iconLabel.setIcon(new ImageIcon(fileIconImage));
                } else {
                    iconLabel.setVisible(false);
                }
                iconLabel.getParent().validate();
            }
        }.start();
        }
        return this;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel iconLabel;
    private javax.swing.JLabel kindLabel;
    private javax.swing.JTextArea kindText;
    private javax.swing.JLabel modifiedLabel;
    private javax.swing.JTextArea modifiedText;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextArea nameText;
    private javax.swing.JPanel northPanel;
    private javax.swing.JLabel originalLabel;
    private javax.swing.JTextArea originalText;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JTextArea sizeText;
    private javax.swing.JLabel whereLabel;
    private javax.swing.JTextArea whereText;
    // End of variables declaration//GEN-END:variables
    
}
