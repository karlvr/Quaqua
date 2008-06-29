/*
 * @(#)ColorSliderHexTextFieldHandler.java  1.0  24. Februar 2007
 *
 * Copyright (c) 2006 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua.colorchooser;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

/**
 * ColorSliderHexTextFieldHandler.
 *
 * @author Werner Randelshofer
 * @version 1.0 24. Februar 2007 Created.
 */
public class ColorSliderHexTextFieldHandler extends ColorSliderTextFieldHandler {
    
    /** Creates a new instance. */
    public ColorSliderHexTextFieldHandler(JTextField textField, ColorSliderModel ccModel, int component) {
        super(textField, ccModel, component);
    }
    
    protected void docChanged() {
        if (textField.hasFocus()) {
            BoundedRangeModel brm = ccModel.getBoundedRangeModel(component);
            try {
                int value = Integer.decode("#"+textField.getText()).intValue();
                if (brm.getMinimum() <= value && value <= brm.getMaximum()) {
                    brm.setValue(value);
                }
            } catch (NumberFormatException e) {
                // Don't change value if it isn't numeric.
            }
        }
    }
    public void stateChanged(ChangeEvent e) {
        if (! textField.hasFocus()) {
            String str = Integer.toHexString(
                    ccModel.getBoundedRangeModel(component).getValue()).
                    toUpperCase();
            textField.setText(str.length() == 2 ? str : "0"+str);
        }
    }
}
