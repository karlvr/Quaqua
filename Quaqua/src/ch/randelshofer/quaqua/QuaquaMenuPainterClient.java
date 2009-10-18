/*
 * @(#)QuaquaMenuPainterClient.java  
 *
 * Copyright (c) 2003-2009 Werner Randelshofer
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
/**
 * QuaquaMenuPainterClient.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public interface QuaquaMenuPainterClient {
    
   public void paintBackground(Graphics g, JComponent c, int i, int j);
   
    //public ThemeMenu getTheme();
}
