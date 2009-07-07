/*
 * @(#)LayoutAHIG15_1Test.java  1.0  June 14, 2007
 *
 * Copyright (c) 2007 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package test;

import ch.randelshofer.quaqua.*;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import javax.swing.*;

/**
 * LayoutAHIG15_1Test: "A Simple Preferences Dialog".
 *
 * http://developer.apple.com/documentation/UserExperience/Conceptual/OSXHIGuidelines/XHIGLayout/chapter_20_section_2.html#//apple_ref/doc/uid/TP30000360-CHDEACGD
 *
 * @author Werner Randelshofer
 * @version 1.0 June 14, 2007 Created.
 */
public class LayoutAHIG15_1Test extends javax.swing.JPanel {
    /** Creates new instance. */
    public LayoutAHIG15_1Test() {
        initComponentsTweaked();
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new QuaquaLookAndFeel());
            // UIManager.put("Quaqua.Debug.showVisualBounds", true);
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        JFrame f = new JFrame("Imagetype Changer Preferences");
        f.add(new LayoutAHIG15_1Test());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }
    
    private void initComponentsTweaked() {
        imageGroup = new javax.swing.ButtonGroup();
        clipboardSelectionGroup = new javax.swing.ButtonGroup();
        generalEditingLabel = new javax.swing.JLabel();
        selectExistingImageRadio = new javax.swing.JRadioButton();
        addMarginRadio = new javax.swing.JRadioButton();
        sizeLabel = new javax.swing.JLabel();
        sizeField = new javax.swing.JTextField();
        pointsLabel = new javax.swing.JLabel();
        repositionWindowsCheck = new javax.swing.JCheckBox();
        rememberRecentCheck = new javax.swing.JCheckBox();
        separator1 = new javax.swing.JSeparator();
        clipboardSettingsLabel = new javax.swing.JLabel();
        copySelectionRadio = new javax.swing.JRadioButton();
        eraseSelectionRadio = new javax.swing.JRadioButton();
        ditherContentCheck = new javax.swing.JCheckBox();
        separator2 = new javax.swing.JSeparator();
        colorOptimizationLabel = new javax.swing.JLabel();
        calculateBestCheck = new javax.swing.JCheckBox();
        verifyColorCheck = new javax.swing.JCheckBox();
        notifyOnLossCheck = new javax.swing.JCheckBox();
        notifyBeforeConversion = new javax.swing.JCheckBox();
        helpButton = new javax.swing.JButton();
        
        generalEditingLabel.setText("General Editing:");
        
       imageGroup.add(selectExistingImageRadio);
        selectExistingImageRadio.setSelected(true);
         selectExistingImageRadio.setText("Select existing image");
        
        
        
        imageGroup.add(addMarginRadio);
        addMarginRadio.setText("Add a margin around image");
        
        
        
        sizeLabel.setText("Size:");
        
        sizeField.setColumns(4);
       sizeField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        
        pointsLabel.setText("points");
        
        repositionWindowsCheck.setText("Reposition windows after change");
        
        
        
        rememberRecentCheck.setText("Remember recent items");
        
        
        
        clipboardSettingsLabel.setText("Clipboard Settings:");
        
        clipboardSelectionGroup.add(copySelectionRadio);
        copySelectionRadio.setSelected(true);
        copySelectionRadio.setText("Copy selection from image only");
        
        
        clipboardSelectionGroup.add(eraseSelectionRadio);
        eraseSelectionRadio.setText("Erase selection from image");
        
        
        
        ditherContentCheck.setText("Dither content of clipboard");
        
        
        
        colorOptimizationLabel.setText("Color Optimization:");
        
        calculateBestCheck.setText("Calculate best color table");
        
        
        
        verifyColorCheck.setText("Verify color table integrity");
        notifyOnLossCheck.setText("Notify on loss of color information");
        
        
        
        notifyBeforeConversion.setText("Notify before CMYK to RGB conversion");
        
        
        
        helpButton.setText("Help");
        helpButton.setToolTipText("Help");
        helpButton.putClientProperty("Quaqua.Button.style","help");
        
        int labelAlignment = UIManager.getLookAndFeel().getID().equals("Aqua") ?
            GroupLayout.TRAILING : GroupLayout.LEADING;
        
        
        
        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                .add(layout.createParallelGroup(labelAlignment)
                .add(generalEditingLabel)
                .add(clipboardSettingsLabel)
                .add(colorOptimizationLabel)
                )
                .add(helpButton)
                )
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                .add(selectExistingImageRadio)
                .add(addMarginRadio)
                .add(layout.createSequentialGroup()
                .add(LayoutStyle.getSharedInstance().getPreferredGap(addMarginRadio, sizeLabel, LayoutStyle.INDENT, SwingConstants.EAST, this))
                .add(sizeLabel)
                .add(sizeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .add(pointsLabel)
                )
                .add(repositionWindowsCheck)
                .add(rememberRecentCheck)
                .add(copySelectionRadio)
                .add(eraseSelectionRadio)
                .add(ditherContentCheck)
                .add(calculateBestCheck)
                .add(verifyColorCheck)
                .add(notifyOnLossCheck)
                .add(notifyBeforeConversion)
                )
                )
                .add(separator1)
                .add(separator2)
                )
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                .add(generalEditingLabel)
                .add(selectExistingImageRadio)
                )
                .add(addMarginRadio)
                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                .add(sizeLabel)
                .add(sizeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .add(pointsLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(repositionWindowsCheck)
                .add(rememberRecentCheck)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(separator1)
                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                .add(clipboardSettingsLabel)
                .add(copySelectionRadio))
                .add(eraseSelectionRadio)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(ditherContentCheck)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(separator2)
                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                .add(colorOptimizationLabel)
                .add(calculateBestCheck))
                .add(verifyColorCheck)
                .add(notifyOnLossCheck)
                .add(notifyBeforeConversion, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
                .add(helpButton)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imageGroup = new javax.swing.ButtonGroup();
        clipboardSelectionGroup = new javax.swing.ButtonGroup();
        generalEditingLabel = new javax.swing.JLabel();
        selectExistingImageRadio = new javax.swing.JRadioButton();
        addMarginRadio = new javax.swing.JRadioButton();
        sizeLabel = new javax.swing.JLabel();
        sizeField = new javax.swing.JTextField();
        pointsLabel = new javax.swing.JLabel();
        repositionWindowsCheck = new javax.swing.JCheckBox();
        rememberRecentCheck = new javax.swing.JCheckBox();
        separator1 = new javax.swing.JSeparator();
        clipboardSettingsLabel = new javax.swing.JLabel();
        copySelectionRadio = new javax.swing.JRadioButton();
        eraseSelectionRadio = new javax.swing.JRadioButton();
        ditherContentCheck = new javax.swing.JCheckBox();
        separator2 = new javax.swing.JSeparator();
        colorOptimizationLabel = new javax.swing.JLabel();
        calculateBestCheck = new javax.swing.JCheckBox();
        verifyColorCheck = new javax.swing.JCheckBox();
        notifyOnLossCheck = new javax.swing.JCheckBox();
        notifyBeforeConversion = new javax.swing.JCheckBox();
        helpButton = new javax.swing.JButton();

        generalEditingLabel.setText("General Editing:");

        imageGroup.add(selectExistingImageRadio);
        selectExistingImageRadio.setSelected(true);
        selectExistingImageRadio.setText("Select existing image");
        selectExistingImageRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));

        imageGroup.add(addMarginRadio);
        addMarginRadio.setText("Add a margin around image");
        addMarginRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));

        sizeLabel.setText("Size:");

        sizeField.setColumns(4);
        sizeField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        pointsLabel.setText("points");

        repositionWindowsCheck.setText("Reposition windows after change");
        repositionWindowsCheck.setMargin(new java.awt.Insets(0, 0, 0, 0));

        rememberRecentCheck.setText("Remember recent items");
        rememberRecentCheck.setMargin(new java.awt.Insets(0, 0, 0, 0));

        clipboardSettingsLabel.setText("Clipboard Settings:");

        clipboardSelectionGroup.add(copySelectionRadio);
        copySelectionRadio.setSelected(true);
        copySelectionRadio.setText("Copy selection from image only");
        copySelectionRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));

        clipboardSelectionGroup.add(eraseSelectionRadio);
        eraseSelectionRadio.setText("Erase selection from image");
        eraseSelectionRadio.setMargin(new java.awt.Insets(0, 0, 0, 0));

        ditherContentCheck.setText("Dither content of clipboard");
        ditherContentCheck.setMargin(new java.awt.Insets(0, 0, 0, 0));

        colorOptimizationLabel.setText("Color Optimization:");

        calculateBestCheck.setText("Calculate best color table");
        calculateBestCheck.setMargin(new java.awt.Insets(0, 0, 0, 0));

        verifyColorCheck.setText("Verify color table integrity");
        verifyColorCheck.setMargin(new java.awt.Insets(0, 0, 0, 0));

        notifyOnLossCheck.setText("Notify on loss of color information");
        notifyOnLossCheck.setMargin(new java.awt.Insets(0, 0, 0, 0));

        notifyBeforeConversion.setText("Notify before CMYK to RGB conversion");
        notifyBeforeConversion.setMargin(new java.awt.Insets(0, 0, 0, 0));

        helpButton.setText("Help");
        helpButton.setToolTipText("Help");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(25, 25, 25)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(clipboardSettingsLabel)
                            .add(generalEditingLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(calculateBestCheck)
                            .add(ditherContentCheck)
                            .add(eraseSelectionRadio)
                            .add(separator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(rememberRecentCheck)
                            .add(repositionWindowsCheck)
                            .add(layout.createSequentialGroup()
                                .add(sizeLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(sizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(pointsLabel))
                            .add(addMarginRadio)
                            .add(selectExistingImageRadio)
                            .add(copySelectionRadio)
                            .add(separator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(verifyColorCheck)
                            .add(notifyOnLossCheck)
                            .add(notifyBeforeConversion)))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(colorOptimizationLabel))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(helpButton)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(selectExistingImageRadio)
                    .add(generalEditingLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(addMarginRadio)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sizeLabel)
                    .add(sizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pointsLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(repositionWindowsCheck)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rememberRecentCheck)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(separator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(clipboardSettingsLabel)
                    .add(copySelectionRadio))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(eraseSelectionRadio)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ditherContentCheck)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(separator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(1, 1, 1)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(colorOptimizationLabel)
                    .add(calculateBestCheck))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(verifyColorCheck)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(notifyOnLossCheck)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(notifyBeforeConversion, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(helpButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton addMarginRadio;
    private javax.swing.JCheckBox calculateBestCheck;
    private javax.swing.ButtonGroup clipboardSelectionGroup;
    private javax.swing.JLabel clipboardSettingsLabel;
    private javax.swing.JLabel colorOptimizationLabel;
    private javax.swing.JRadioButton copySelectionRadio;
    private javax.swing.JCheckBox ditherContentCheck;
    private javax.swing.JRadioButton eraseSelectionRadio;
    private javax.swing.JLabel generalEditingLabel;
    private javax.swing.JButton helpButton;
    private javax.swing.ButtonGroup imageGroup;
    private javax.swing.JCheckBox notifyBeforeConversion;
    private javax.swing.JCheckBox notifyOnLossCheck;
    private javax.swing.JLabel pointsLabel;
    private javax.swing.JCheckBox rememberRecentCheck;
    private javax.swing.JCheckBox repositionWindowsCheck;
    private javax.swing.JRadioButton selectExistingImageRadio;
    private javax.swing.JSeparator separator1;
    private javax.swing.JSeparator separator2;
    private javax.swing.JTextField sizeField;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JCheckBox verifyColorCheck;
    // End of variables declaration//GEN-END:variables
    
}
