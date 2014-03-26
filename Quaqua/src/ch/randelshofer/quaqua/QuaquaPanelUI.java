/*
 * @(#)QuaquaPanelUI.java
 *
 * Copyright (c) 2005-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.border.BackgroundBorder;
import ch.randelshofer.quaqua.util.Debug;
import ch.randelshofer.quaqua.color.PaintableColor;
import java.awt.*;
import java.awt.geom.Path2D;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
/**
 * QuaquaPanelUI.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class QuaquaPanelUI extends BasicPanelUI {
    // Shared UI object
    private static PanelUI panelUI;

    public static ComponentUI createUI(JComponent c) {
        if(panelUI == null) {
            panelUI = new QuaquaPanelUI();
        }
        return panelUI;
    }
    @Override
    protected void installDefaults(JPanel p) {
        super.installDefaults(p);
	QuaquaUtilities.installProperty(p, "opaque", UIManager.get("Panel.opaque"));
    }

    @Override
    protected void uninstallDefaults(JPanel p) {
        super.uninstallDefaults(p);
    }

    public static boolean isInTabbedPane(Component comp) {
        if(comp == null)
            return false;
        Container parent = comp.getParent();
        while (parent != null) {
            if (parent instanceof JTabbedPane) {
                return true;
            } else if (parent instanceof JRootPane) {
                return false;
            } else if (parent instanceof RootPaneContainer) {
                return false;
            } else if (parent instanceof Window) {
                return false;
            }
            parent = parent.getParent();
        }
        return false;
    }

    @Override
    public void paint(Graphics gr, JComponent c) {
            Graphics2D g = (Graphics2D) gr;
        Object oldHints = QuaquaUtilities.beginGraphics(g);
        if (c.isOpaque()) {
            g.setPaint(PaintableColor.getPaint(c.getBackground(), c));
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }

        Border backgroundBorder = null;
        Graphics2D bg = (Graphics2D) g.create();
        Insets insets = new Insets(0,0,0,0);
        if (c.getBorder() instanceof BackgroundBorder) {
            backgroundBorder = ((BackgroundBorder) c.getBorder()).getBackgroundBorder();
        } else if (c.getBorder() instanceof TitledBorder) {
            TitledBorder tb = (TitledBorder) c.getBorder();
            Border titledBorderBorder = tb.getBorder();
            if (titledBorderBorder instanceof BackgroundBorder) {
                backgroundBorder = ((BackgroundBorder) titledBorderBorder).getBackgroundBorder();
                insets = getTitledBorderBorderPaintingInsets(c, tb);
                setTitledBorderClip(bg, c, tb);
            }
        }
        if (backgroundBorder != null) {
            backgroundBorder.paintBorder(c, bg, insets.left, insets.top, c.getWidth() - insets.left - insets.right, c.getHeight() - insets.top - insets.bottom);
        }

        Debug.paint(gr, c, this);
        QuaquaUtilities.endGraphics((Graphics2D) g, oldHints);
    }

    /**
     * Set a clipping region as appropriate for a titled border so that it does not conflict with the label.
     */

    private void setTitledBorderClip(Graphics2D g, JComponent c, TitledBorder tb) {
        int position = getTitlePosition(tb);
        if (position != TitledBorder.TOP && position != TitledBorder.BOTTOM) {
            return;
        }

        Rectangle bounds = getTitledBorderBorderTitleBounds(c, tb);
        if (bounds == null) {
            return;
        }

        Path2D path = new Path2D.Float();
        path.append(new Rectangle(0, 0, c.getWidth(), bounds.y), false);
        path.append(new Rectangle(0, bounds.y, bounds.x - 2, bounds.height), false);
        path.append(new Rectangle(bounds.x + bounds.width + 2, bounds.y, c.getWidth() - bounds.width - bounds.x - 2, bounds.height), false);
        path.append(new Rectangle(0, bounds.y + bounds.height, c.getWidth(), c.getHeight() - bounds.height - bounds.y), false);
        g.clip(path);
    }

    /**
     * Figure out where a TitledBorder would paint its title. This method basically attempts to simulate the behavior
     * of TitledBorder. There is no guarantee that TitledBorder won't change its behavior or be overridden.
     */

    private Rectangle getTitledBorderBorderTitleBounds(Component c, TitledBorder tb) {

        String title = tb.getTitle();
        if (title == null || title.isEmpty()) {
            return null;
        }

        JLabel label = new JLabel(title);
        Font f = tb.getTitleFont();

        // The following is a workaround for a problem in 1.7 that was corrected in 1.8
        if (f == null) {
            f = UIManager.getFont("TitledBorder.font");
        }

        label.setFont(f);
        label.setComponentOrientation(c.getComponentOrientation());

        Border b = tb.getBorder();
        int edge = (b instanceof TitledBorder) ? 0 : 2;
        Dimension size = label.getPreferredSize();
        Insets insets = getTitledBorderBorderPaintingInsets(c, tb);

        int labelY = 0;
        int labelH = size.height;
        int position = getTitlePosition(tb);
        switch (position) {
            case TitledBorder.ABOVE_TOP:
                insets.left = 0;
                insets.right = 0;
                break;
            case TitledBorder.TOP:
                insets.top = edge + insets.top/2 - labelH/2;
                if (insets.top < edge) {
                }
                else {
                    labelY += insets.top;
                }
                break;
            case TitledBorder.BELOW_TOP:
                labelY += insets.top + edge;
                break;
            case TitledBorder.ABOVE_BOTTOM:
                labelY += c.getHeight() - labelH - insets.bottom - edge;
                break;
            case TitledBorder.BOTTOM:
                labelY += c.getHeight() - labelH;
                insets.bottom = edge + (insets.bottom - labelH) / 2;
                if (insets.bottom < edge) {
                }
                else {
                    labelY -= insets.bottom;
                }
                break;
            case TitledBorder.BELOW_BOTTOM:
                insets.left = 0;
                insets.right = 0;
                labelY += c.getHeight() - labelH;
                break;
        }
        insets.left += edge + 5;
        insets.right += edge + 5;

        int labelX = 0;
        int labelW = c.getWidth() - insets.left - insets.right;
        if (labelW > size.width) {
            labelW = size.width;
        }
        switch (getJustification(c, tb)) {
            case TitledBorder.LEFT:
                labelX += insets.left;
                break;
            case TitledBorder.RIGHT:
                labelX += c.getWidth() - insets.right - labelW;
                break;
            case TitledBorder.CENTER:
                labelX += (c.getWidth() - labelW) / 2;
                break;
        }

        return new Rectangle(labelX, labelY, labelW, labelH);
    }

    private int getJustification(Component c, TitledBorder tb) {
        int justification = tb.getTitleJustification();
        if ((justification == TitledBorder.LEADING) || (justification == TitledBorder.DEFAULT_JUSTIFICATION)) {
            return c.getComponentOrientation().isLeftToRight() ? TitledBorder.LEFT : TitledBorder.RIGHT;
        }
        if (justification == TitledBorder.TRAILING) {
            return c.getComponentOrientation().isLeftToRight() ? TitledBorder.RIGHT : TitledBorder.LEFT;
        }
        return justification;

    }

    /**
     * Figure out where a TitledBorder would paint its border. This method basically attempts to simulate the behavior
     * of TitledBorder. There is no guarantee that TitledBorder won't change its behavior or be overridden.
     */

    private Insets getTitledBorderBorderPaintingInsets(Component c, TitledBorder tb) {
        String title = tb.getTitle();
        if (title == null || title.isEmpty()) {
            return new Insets(0, 0, 0, 0);
        }

        Insets borderInsets = tb.getBorder().getBorderInsets(c);
        Insets titledBorderInsets = tb.getBorderInsets(c);

        /*
          The border insets of the TitledBorder is larger than the insets of the painted border to make room for the
          label and to provide a margin around the border and a margin between the border and the contents. The left and
          right insets are based only on the margins, so we can figure out the total margin from that.
        */

        int margin = titledBorderInsets.left - borderInsets.left;
        int top = margin / 2;
        int left = margin / 2;
        int bottom = margin / 2;
        int right = margin / 2;

        /*
          The remaining margin provides a place for the title, depending upon the title position option.
        */

        switch (getTitlePosition(tb)) {
            case TitledBorder.ABOVE_TOP:
                top = titledBorderInsets.top - borderInsets.top - top;
                break;
            case TitledBorder.TOP: {
                top = (titledBorderInsets.top - top) / 2;
                break;
            }
            case TitledBorder.BOTTOM: {
                bottom = (titledBorderInsets.bottom - bottom) / 2;
                break;
            }
            case TitledBorder.BELOW_BOTTOM:
                bottom = titledBorderInsets.bottom - borderInsets.bottom;
                break;
        }

        return new Insets(top, left, bottom, right);
    }

    private int getTitlePosition(TitledBorder tb) {
        int position = tb.getTitlePosition();
        if (position != TitledBorder.DEFAULT_POSITION) {
            return position;
        }
        Object value = UIManager.get("TitledBorder.position");
        if (value instanceof Integer) {
            int i = (Integer) value;
            if ((0 < i) && (i <= 6)) {
                return i;
            }
        }
        else if (value instanceof String) {
            String s = (String) value;
            if (s.equalsIgnoreCase("ABOVE_TOP")) {
                return TitledBorder.ABOVE_TOP;
            }
            if (s.equalsIgnoreCase("TOP")) {
                return TitledBorder.TOP;
            }
            if (s.equalsIgnoreCase("BELOW_TOP")) {
                return TitledBorder.BELOW_TOP;
            }
            if (s.equalsIgnoreCase("ABOVE_BOTTOM")) {
                return TitledBorder.ABOVE_BOTTOM;
            }
            if (s.equalsIgnoreCase("BOTTOM")) {
                return TitledBorder.BOTTOM;
            }
            if (s.equalsIgnoreCase("BELOW_BOTTOM")) {
                return TitledBorder.BELOW_BOTTOM;
            }
        }
        return TitledBorder.TOP;
    }
}
