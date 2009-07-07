/**
 * @(#)BrowserCellRenderer.java
 *
 * Copyright (c) 2008 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import java.awt.*;

/**
 * Defines the requirements for an object that displays a tree node in a JBrowser.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface BrowserCellRenderer {

    /**
     * Sets the value of the current tree cell to <code>value</code>.
     * If <code>selected</code> is true, the cell will be drawn as if
     * selected. If <code>expanded</code> is true the node is currently
     * expanded and if <code>leaf</code> is true the node represets a
     * leaf and if <code>hasFocus</code> is true the node currently has
     * focus. <code>tree</code> is the <code>JTree</code> the receiver is being
     * configured for.  Returns the <code>Component</code> that the renderer
     * uses to draw the value.
     *
     * @return	the <code>Component</code> that the renderer uses to draw the value
     */
    Component getBrowserCellRendererComponent(JBrowser browser, Object value,
				   boolean selected, boolean expanded,
				   boolean leaf, int row, boolean hasFocus);

}
