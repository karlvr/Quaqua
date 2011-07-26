/*
 * @(#)Painter.java  1.0  2011-07-26
 * 
 * Copyright (c) 2011 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package ch.randelshofer.quaqua.border;

import java.awt.Graphics;

/**
 * {@code Painter}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-07-26 Created.
 */
public interface Painter {
    public void paint(Graphics g, int x, int y, int width, int height) ;
}
