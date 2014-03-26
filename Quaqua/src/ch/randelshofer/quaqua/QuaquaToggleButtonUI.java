/*
 * @(#)QuaquaButtonUI.java  1.0  05 March 2005
 *
 * Copyright (c) 2004-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.quaqua;

import javax.swing.*;
import javax.swing.plaf.*;

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
