/*
 * @(#)QuaquaDesktopPaneUI.java  1.0  06 February 2005
 *
 * Copyright (c) 2004 Werner Randelshofer
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
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
/**
 * QuaquaDesktopPaneUI.
 *
 * @author  Werner Randelshofer
 * @version 1.0  06 February 2005  Created.
 */
public class QuaquaDesktopPaneUI extends BasicDesktopPaneUI {
    
    /** Creates a new instance. */
    public QuaquaDesktopPaneUI() {
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new QuaquaDesktopPaneUI();
    }
}
