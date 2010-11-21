/*
 * @(#)InactivatableColorUIResource.java  
 *
 * Copyright (c) 2007-2010 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */

package ch.randelshofer.quaqua.color;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.plaf.UIResource;

/**
 * InactivatableColorUIResource is a color, that can be rendered using an
 * an active state and an inactive state.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class InactivatableColorUIResource extends Color implements UIResource {
    private boolean isActive;
    private boolean isTransparent;
    private int inactiveRGB;
    
    /** Creates a new instance. */
    public InactivatableColorUIResource(int activeRGB, int inactiveRGB) {
        super(activeRGB);
        this.inactiveRGB = inactiveRGB | 0xff000000;
    }
    public InactivatableColorUIResource(int activeRGB, int inactiveRGB, boolean hasAlpha) {
        super(activeRGB, hasAlpha);
        this.inactiveRGB = (hasAlpha) ? inactiveRGB : inactiveRGB | 0xff000000;
    }
    
    public void setActive(boolean newValue) {
        isActive = newValue;
    }
    public void setTransparent(boolean newValue) {
        isTransparent = newValue;
    }
    
    public int getTransparency() {
        return (isTransparent) ? Paint.TRANSLUCENT : super.getTransparency();
    }
    
    public int getAlpha() {
        return (isTransparent) ? 0x0 : super.getAlpha();
    }
    
    public int getRGB() {
        return (isTransparent) ? 0x0 : ((isActive) ? super.getRGB() : inactiveRGB);
        
    }
    
    public PaintContext createContext(ColorModel cm, Rectangle r, Rectangle2D r2d, AffineTransform xform, RenderingHints hints) {
        return (isActive) ? super.createContext(cm, r, r2d, xform, hints) :
            new Color(inactiveRGB, true).createContext(cm, r, r2d, xform, hints);
    }
    
}
