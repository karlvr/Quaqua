package test;

//
//  NativeSheetTest.java
//  J5Sheet
//
//  Created by Felix Draxler on 29.11.09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ch.randelshofer.quaqua.*;

/**
 * NativeSheetTest.
 *
 * @autor Felix Draxler
**/
public class NativeSheetTest extends JPanel implements ActionListener, SheetListener {
    private final static String MULTIPLE_SELECTION_VALUES = "Multiple selection values";
    private final static String MULTIPLE_OPTIONS = "Multiple options";
    private final static String FILE_CHOOSER = "File chooser";
    
    public NativeSheetTest() {
        super(new GridBagLayout());
        
        UIManager.put("Sheet.optionPaneMapping", Boolean.TRUE);
        UIManager.put("Sheet.fileChooserEmbedding", Boolean.TRUE);
        
        GridBagConstraints c = new GridBagConstraints();
        
        c.gridx = 0;
        JButton multipleSelectionValuesButton = new JButton("Multiple selections");
        multipleSelectionValuesButton.setActionCommand(MULTIPLE_SELECTION_VALUES);
        multipleSelectionValuesButton.addActionListener(this);
        add(multipleSelectionValuesButton, c);
        
        c.gridx = 1;
        JButton multipleOptionsButton = new JButton("Multiple options");
        multipleOptionsButton.setActionCommand(MULTIPLE_OPTIONS);
        multipleOptionsButton.addActionListener(this);
        add(multipleOptionsButton, c);
        
        c.gridx = 2;
        JButton fileChooserButton = new JButton("File chooser");
        fileChooserButton.setActionCommand(FILE_CHOOSER);
        fileChooserButton.addActionListener(this);
        add(fileChooserButton, c);
        
        c.gridx = GridBagConstraints.REMAINDER;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 1;
        JLabel helpText = new JLabel();
        helpText.setText("<html>This test sets the UIManager properties \"Sheet.optionPaneMapping\" "
                         + "and \"Sheet.fileChooserEmbedding\" to <code>Boolean.TRUE</code>.<br>"
                         + "To activate the behaviour on this page please set the properties to <code>true</code>."
                         + "<p>Please note:"
                         + "<p>The properties are in stage of development and only fairly stable. If you encounter any issues, "
                         + "we encourage you to write about it in the project forum or the bug reporter.");
        add(helpText, c);
    }
    
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(MULTIPLE_SELECTION_VALUES)) {
            JSheet.showInputSheet(this,
                                  "Please choose a fruit.",
                                  JOptionPane.QUESTION_MESSAGE,
                                  null,
                                  new String[] { "Apple", "Banana", "Grape", "Orange" },
                                  "Grape",
                                  this);
        } else if (command.equals(MULTIPLE_OPTIONS)) {
            JSheet.showOptionSheet(this,
                                   "Pick a number.",
                                   JOptionPane.DEFAULT_OPTION,
                                   JOptionPane.QUESTION_MESSAGE,
                                   null,
                                   new String[] { "One", "Two", "Three", "Four" },
                                   "One",
                                   this);
        } else if (command.equals(FILE_CHOOSER)) {
            JFileChooser chooser = new JFileChooser();
            JSheet.showSaveSheet(chooser,
                                 this,
                                 this);
        }
    }
    
    public void optionSelected(SheetEvent e) {
        System.out.println(e);
        if (e.getPane() instanceof JOptionPane) {
            if (!e.getOptionPane().getWantsInput())
                JSheet.showMessageSheet(this, "You chose "+ e.getValue() +".");
            else
                JSheet.showMessageSheet(this, "You entered "+ e.getInputValue() +".");
        } else {
            JSheet.showMessageSheet(this, "You chose "+ e.getFileChooser().getSelectedFile() +".");
        }
    }
}
