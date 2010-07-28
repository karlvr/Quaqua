/*
 * @(#)QuaquaHighlighter.java  
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

package ch.randelshofer.quaqua;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.text.*;

/**
 * QuaquaHighlighter.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class QuaquaHighlighter extends DefaultHighlighter implements UIResource
{
    public static LayeredHighlighter.LayerPainter painterInstance
	= new QuaquaHighlightPainter(null);
    
    public static class QuaquaHighlightPainter
	extends DefaultHighlighter.DefaultHighlightPainter
    {
	Color highlightColor;
	
	public QuaquaHighlightPainter(Color color) {
	    super(color);
	}
	
	public Color getColor() {
	    return highlightColor == null ? super.getColor() : highlightColor;
	}
	
	void setColor(JTextComponent c) {
	    highlightColor = super.getColor();
	    if (highlightColor == null) {
		highlightColor = c.getSelectionColor();
            }
            if (! QuaquaUtilities.isFocused(c)) {
		highlightColor = UIManager.getColor("TextField.inactiveSelectionBackground");
            }
	}
	
	public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
	    setColor(c);
	    super.paint(g, offs0, offs1, bounds, c);
	}
	
	public Shape paintLayer(Graphics g, int offs0, int offs1,
				Shape bounds, JTextComponent c, View view) {
	    setColor(c);
	    return super.paintLayer(g, offs0, offs1, bounds, c,
				    view);
	}
    }
}
