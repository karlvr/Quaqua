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
    
    public NativeSheetTest() {
        super(new FlowLayout(FlowLayout.CENTER));
        
        UIManager.put("Sheet.optionPaneMapping", Boolean.TRUE);
        
        JButton multipleSelectionValuesButton = new JButton("Multiple selections");
        multipleSelectionValuesButton.setActionCommand(MULTIPLE_SELECTION_VALUES);
        multipleSelectionValuesButton.addActionListener(this);
        add(multipleSelectionValuesButton);
        
        JButton multipleOptionsButton = new JButton("Multiple options");
        multipleOptionsButton.setActionCommand(MULTIPLE_OPTIONS);
        multipleOptionsButton.addActionListener(this);
        add(multipleOptionsButton);
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
        }
    }
    
    public void optionSelected(SheetEvent e) {
        System.out.println(e);
        if (!e.getOptionPane().getWantsInput())
            JSheet.showMessageSheet(this, "You chose "+ e.getValue() +".");
        else
            JSheet.showMessageSheet(this, "You entered "+ e.getInputValue() +".");
    }
}
