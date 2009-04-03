/*
 * @(#)QuaquaComboBoxCellRenderer.java  1.0.3  2006-02-13
 *
 * Copyright (c) 2004-2006 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

/**
 * QuaquaComboBoxCellRenderer.
 *
 * @author  Werner Randelshofer
 * @version 1.0.3 2006-02-13 Fixed background color. 
 * <br>1.0.2 2005-05-28 Enforce default foreground color on the renderer.
 * <br>1.0.1 2004-10-06 Border insets adjusted because of changes in class
 * QuaquaComboPopup.
 * <br>1.0 April 11, 2004 Created.
 */
public class QuaquaComboBoxCellRenderer implements ListCellRenderer {
    private ListCellRenderer valueRenderer;
    private JPanel panel;
    
    public QuaquaComboBoxCellRenderer(ListCellRenderer valueRenderer, boolean isInTable, boolean isEditable) {
        this.valueRenderer = valueRenderer;
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        if (isInTable) {
            panel.setBorder(null);
        } else {
            if (isEditable) {
                panel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
            } else {
                panel.setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 7));
            }
        }
        panel.setOpaque(true);
    }
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component valueComponent = valueRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        panel.removeAll();
        
        panel.add(valueComponent);
        panel.setBackground((isSelected) ? 
        valueComponent.getBackground() :
            UIManager.getColor("PopupMenu.background")
        );

        return panel;
        
    }
}
