/*
 * @(#)QuaquaButtonUI.java  1.0  05 March 2005
 *
 * Copyright (c) 2004-2010 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */

package ch.randelshofer.quaqua;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.io.Serializable;
import java.beans.*;

/**
 * QuaquaButtonUI.
 *
 * @author  Werner Randelshofer
 * @version 1.0  05 March 2005  Created.
 */
public class QuaquaToggleButtonUI extends QuaquaButtonUI {
    // Shared UI object
    private final static QuaquaToggleButtonUI buttonUI = new QuaquaToggleButtonUI();
    
    
    // ********************************
    //          Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent c) {
        return buttonUI;
    }
    
    protected String getPropertyPrefix() {
        return "ToggleButton.";
    }
}
