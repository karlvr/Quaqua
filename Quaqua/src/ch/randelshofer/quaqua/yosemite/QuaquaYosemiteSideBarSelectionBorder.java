/*
 * @(#)QuaquaYosemiteSideBarSelectionBorder.java 
 *
 * Copyright (c) 2015 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.yosemite;

import ch.randelshofer.quaqua.lion.*;
import ch.randelshofer.quaqua.*;
import ch.randelshofer.quaqua.ext.batik.ext.awt.LinearGradientPaint;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.border.*;
import javax.swing.plaf.UIResource;

/**
 * QuaquaYosemiteSideBarSelectionBorder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class QuaquaYosemiteSideBarSelectionBorder implements Border, UIResource {
    private final Color fillColor = new Color(0xcecece);

    /** Creates a new instance. */
    public QuaquaYosemiteSideBarSelectionBorder() {
    }

    public void paintBorder(Component c, Graphics gr, int x, int y, int width, int height) {
        Graphics2D g = (Graphics2D) gr;
        g.setColor(fillColor);
        g.fillRect(x,y,width,height);
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 0, 0);
    }

    public boolean isBorderOpaque() {
        return true;
    }
}
