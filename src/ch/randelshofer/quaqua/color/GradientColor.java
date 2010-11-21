/**
 * @(#)GradientColor.java 
 *
 * Copyright (c) 2008-2010 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package ch.randelshofer.quaqua.color;

import ch.randelshofer.quaqua.util.*;
import java.awt.*;
import javax.swing.plaf.UIResource;

/**
 * GradientColor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GradientColor extends PaintableColor {

    protected Color color1;
    protected Color color2;

    public GradientColor(Color plainColor, Color gradientNorth, Color gradientSouth) {
        super(plainColor.getRGB(), plainColor.getAlpha() != 255);
        this.color1 = gradientNorth;
        this.color2 = gradientSouth;
    }

    public Paint getPaint(Component c, int x, int y, int widht, int height) {
        return new GradientPaint(x, y, color1, x, y + height, color2, true);
    }

    public static class UIResource extends GradientColor implements javax.swing.plaf.UIResource {

        public UIResource(int plainColor, int gradientNorth, int gradientSouth) {
            super(new Color(plainColor), new Color(gradientNorth), new Color(gradientSouth));
        }
    }
}
