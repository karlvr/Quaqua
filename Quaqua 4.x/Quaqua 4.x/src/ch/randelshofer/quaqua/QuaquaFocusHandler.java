/*
 * @(#)QuaquaFocusHandler.java  1.1  2007-01-16
 *
 * Copyright (c) 2004-2007 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import java.awt.Component;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
/**
 * QuaquaFocusHandler.
 *
 * @author  Werner Randelshofer
 * @version 1.1 2007-01-16 Changed to singleton.
 * <br>1.0  July 4, 2004  Created.
 */
public class QuaquaFocusHandler implements FocusListener {
    private static QuaquaFocusHandler instance;
    
    public static QuaquaFocusHandler getInstance() {
        if (instance == null) {
            instance = new QuaquaFocusHandler();
        }
        return instance;
    }
    
    
    /**
     * Prevent instance creation.
     */
    private QuaquaFocusHandler() {
    }
    
    public void focusGained(FocusEvent event) {
            QuaquaUtilities.repaintBorder((JComponent) event.getComponent());
    }
    
    public void focusLost(FocusEvent event) {
            QuaquaUtilities.repaintBorder((JComponent) event.getComponent());
    }
}

