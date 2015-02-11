/*
 * @(#)RoundRectangleBorder.java
 *
 * Copyright (c) 2015 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.Border;

/**
 * RoundRectangleBorder.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RoundRectangleBorder implements Border {
    private Insets borderInsets;
private double arcw; private double arch;
private Color strokeColor;
private Color fillColor;

    public RoundRectangleBorder(double arcw, double arch, Color fillColor, Color strokeColor, Insets borderInsets) {
        this.arcw=arcw;
        this.arch=arch;
        this.fillColor=fillColor;
        this.strokeColor=strokeColor;
        this.borderInsets = borderInsets;
        
    }
   
    

    @Override
    public void paintBorder(Component c, Graphics gr, int x, int y, int width, int height) {
        Graphics2D g = (Graphics2D) gr;
       RoundRectangle2D.Double rr = new RoundRectangle2D.Double(x,y,width,height,arcw,arch);
       if (fillColor!=null) {
           g.setColor(fillColor);
           g.fill(rr);
       }
       if (strokeColor!=null) {
           g.setColor(strokeColor);
           g.draw(rr);
       }
    }

    @Override
    public Insets getBorderInsets(Component c) {
 return borderInsets;
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

}
