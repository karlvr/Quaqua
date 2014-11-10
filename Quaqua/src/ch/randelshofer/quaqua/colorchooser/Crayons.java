/*
 * @(#)Crayons.java
 *
 * Copyright (c) 2005-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.quaqua.colorchooser;

import ch.randelshofer.quaqua.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * A panel which displays a selection of color crayons. The user can click at
 * a crayon to pick a color.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class Crayons extends javax.swing.JPanel {

    /**
     * Shared crayons image.
     */
    private Image crayonsImage;

    /**
     * Coordinates of crayon shaped polygon.
     */
    private final static int[] crayonXPoints = { 10, 12, 20, 22,  22,   0,  0,  2 }; // xpoints
    private final static int[] crayonYPoints = { 0,  0, 21, 21, 104, 104, 21, 21 }; // ypoints

    /**
     * Current color.
     */
    private Color color = Color.white;
    /**
     * Selected crayon.
     */
    private Crayon selectedCrayon = null;

    /**
     * Crayon.
     */
    private static class Crayon {
        Polygon shape;
        Color color;
        String name;

        public Crayon(Color color, String name, Polygon shape) {
            this.color = color;
            this.name = name;
            this.shape = shape;
        }
    }

    private class MouseHandler extends MouseAdapter {
        public void mousePressed(MouseEvent evt) {
            int x = evt.getX();
            int y = evt.getY();
            if (x > 0 && x < crayonsImage.getWidth(Crayons.this)
            && y > 0 && y < crayonsImage.getHeight(Crayons.this)
            ) {
                for (int i=crayons.length - 1; i >= 0; i--) {
                    if (crayons[i].shape.contains(x, y)) {
                        setColor(crayons[i].color);
                        break;
                    }
                }
            }
        }
    }

    private MouseHandler mouseHandler;

    /**
     * Crayons.
     */
    private Crayon[] crayons;

    /**
     * Creates a new instance.
     */
    public Crayons() {
        initComponents();

        setForeground(new Color(0x808080));
        setPreferredSize(new Dimension(195, 208));
        setFont(UIManager.getFont("ColorChooser.crayonsFont"));

        crayonsImage = createCrayonsImage();
        crayons = createCrayons();

        mouseHandler = new MouseHandler();
        addMouseListener(mouseHandler);
    }

    protected Image createCrayonsImage() {
        Object o = UIManager.get("ColorChooser.crayonsImage");
        if (o instanceof ImageIcon) {
            return ((ImageIcon) o).getImage();
        } else {
            return (Image) o;
        }
    }

    /**
     * Creates the crayons.
     * @return Array of crayons in z-order from bottom to top.
     */
    protected Crayon[] createCrayons() {
        Color[] colors = DefaultPalettes.CRAYONS;
        crayons = new Crayon[colors.length];
        for (int i=0; i < colors.length; i++) {
            crayons[i] = new Crayon(
            colors[i],
            UIManager.getString("ColorChooser.crayon."+Integer.toHexString(0xff000000|colors[i].getRGB()).substring(2)),
            new Polygon((int[]) crayonXPoints.clone(), (int[]) crayonYPoints.clone(), crayonXPoints.length));
            crayons[i].shape.translate(
            (i % 8) * 22 + 4 +((i / 8) % 2) * 11,
            (i / 8) * 20 + 23
            );
        }

        return crayons;
    }

    /**
     * Sets the current color.
     * This results in a selection of a crayon, if a crayon with the same
     * RGB values exists.
     */
    public void setColor(Color newValue) {
        Color oldValue = color;
        color = newValue;

        Crayon newSelectedCrayon = null;
        int newRGB = newValue.getRGB() & 0xffffff;
        for (int i=0; i < crayons.length; i++) {
            if ((crayons[i].color.getRGB() & 0xffffff) == newRGB) {
                newSelectedCrayon = crayons[i];
            }
        }
        if (newSelectedCrayon != selectedCrayon) {
            selectedCrayon = newSelectedCrayon;
            repaint();
        }

        firePropertyChange("Color", oldValue, newValue);
    }

    /**
     * Returns the current color.
     */
    public Color getColor() {
        return color;
    }

    public void paintComponent(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        Object oldHints = QuaquaUtilities.beginGraphics((Graphics2D) g);

        g.drawImage(crayonsImage, 0, 0, this);


        if (selectedCrayon != null) {
            /*
            g.setColor(new Color(0x60ffffff & selectedCrayon.color.getRGB(),true));
            g.fill(selectedCrayon.shape);
             */
            g.setColor(getForeground());
            FontMetrics fm = g.getFontMetrics();
            int nameWidth = fm.stringWidth(selectedCrayon.name);
            g.drawString(
            selectedCrayon.name,
            (crayonsImage.getWidth(this) - nameWidth) / 2,
            fm.getAscent() + 1
            );
        }
        QuaquaUtilities.endGraphics((Graphics2D) g, oldHints);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

        setLayout(new java.awt.BorderLayout());

    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
