/**
 * @(#)GradientColor.java 
 *
 * Copyright (c) 2008-2010 Werner Randelshofer
 * Hausmatt 10, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua.color;

import ch.randelshofer.quaqua.util.*;
import java.awt.*;

/**
 * GradientColor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GradientColor extends PaintableColor {
    protected Color color1;
    protected Color color2;
    
    public GradientColor(Color c, Color c1, Color c2) {
        super(c.getRGB(), c.getAlpha() != 255);
        this.color1 = c1;
        this.color2 = c2;
    }
    

    public Paint getPaint(Component c, int x, int y, int widht, int height) {
        return new GradientPaint(x,y,color1,x,y+height,color2,true);
    }

}
