/*
 * @(#)BrowserUI.java  2.0  2008-07-18
 *
 * Copyright (c) 2005-2008 Werner Randelshofer
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
/**
 * BrowserUI.
 *
 * @author  Werner Randelshofer
 * @version 2.0 2008-07-18 Added size handle icon. 
 * <br>1.0 August 25, 2005 Created.
 */
public class BrowserUI extends ComponentUI {
    
    /**
     * Creates a new instance.
     */
    public BrowserUI() {
    }
    
    public Icon getSizeHandleIcon() {
        return null;
    }

}
