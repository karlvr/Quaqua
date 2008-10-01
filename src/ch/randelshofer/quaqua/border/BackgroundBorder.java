/*
 * @(#)BackgroundBorder.java  1.0  September 25, 2005
 *
 * Copyright (c) 2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua.border;

import javax.swing.border.*;
/**
 * BackgroundBorder is used by the Quaqua Look And Feel to tag a
 * border which partially needs to be drawn on to the background of a JComponent.
 *
 * @author  Werner Randelshofer
 * @version 1.0 September 25, 2005 Created.
 */
public interface BackgroundBorder extends Border {
    
    /**
     * Returns the border that needs to be drawn onto the background.
     */
    public Border getBackgroundBorder();
}
