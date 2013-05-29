/*
 * @(#)AlphaColorUIResource.java 
 *
 * Copyright (c) 2004-2013 Werner Randelshofer, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */

package ch.randelshofer.quaqua.color;

import java.awt.*;
import javax.swing.plaf.*;
/**
 * A ColorUIResource whith an alpha channel.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class AlphaColorUIResource extends Color implements UIResource {
    public AlphaColorUIResource(int r, int g, int b, int a) {
        super(r, g, b, a);
    }
    public AlphaColorUIResource(int rgba) {
        super(rgba, true);
    }
}
