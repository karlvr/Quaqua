/*
 * @(#)QuaquaDesktopPaneUI.java 
 *
 * Copyright (c) 2004-2009 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
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
