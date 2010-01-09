/*
 * @(#)ViewportPainter.java  1.0  14 December 2004
 *
 * Copyright (c) 2004-2010 Werner Randelshofer
 * Hausmatt 10, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua.util;

import java.awt.*;
import javax.swing.*;
/**
 * This interface is implemented by user interface delegates that wish to
 * paint onto the content area of a JViewport.
 *
 * @author  Werner Randelshofer
 * @version 1.0  14 December 2004  Created.
 */
public interface ViewportPainter {
    /**
     * Paints the viewport of a JViewport, that contains the component of the
     * user interface delegate.
     * This method is invoked by QuaquaViewportUI.
     */
    public void paintViewport(Graphics g, JViewport c);
}
