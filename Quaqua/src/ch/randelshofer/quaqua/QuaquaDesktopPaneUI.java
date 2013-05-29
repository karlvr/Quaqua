/*
 * @(#)QuaquaDesktopPaneUI.java 
 *
 * Copyright (c) 2004-2013 Werner Randelshofer, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */

package ch.randelshofer.quaqua;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
/**
 * QuaquaDesktopPaneUI.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class QuaquaDesktopPaneUI extends BasicDesktopPaneUI {
    
    /** Creates a new instance. */
    public QuaquaDesktopPaneUI() {
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new QuaquaDesktopPaneUI();
    }
}
