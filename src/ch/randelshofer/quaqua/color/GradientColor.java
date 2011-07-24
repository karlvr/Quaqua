/**
 * @(#)GradientColor.java 
 *
 * Copyright (c) 2008-2011 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package ch.randelshofer.quaqua.color;

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

    public GradientColor(int plainColor, int gradientNorth, int gradientSouth) {
        super(plainColor, (plainColor&0xff000000) != 0xff000000);
        this.color1 = new Color(gradientNorth);
        this.color2 = new Color(gradientSouth);
    }
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
