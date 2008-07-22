/*
 * @(#)AlphaColorUIResource.java  1.0  06 February 2005
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

package ch.randelshofer.quaqua.util;

import java.awt.*;
import javax.swing.plaf.*;
/**
 * A ColorUIResource whith an alpha channel.
 *
 * @author  Werner Randelshofer
 * @version 1.0  06 February 2005  Created.
 */
public class AlphaColorUIResource extends Color implements UIResource {
    public AlphaColorUIResource(int r, int g, int b, int a) {
        super(r, g, b, a);
    }
    public AlphaColorUIResource(int rgba) {
        super(rgba, true);
    }
}
