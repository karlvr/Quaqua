/*
 * @(#)QuaquaLabelUI.java  1.6  2007-11-18
 *
 * Copyright (c) 2005-2007 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.util.*;
import ch.randelshofer.quaqua.border.BackgroundBorder;
import ch.randelshofer.quaqua.util.Debug;
import ch.randelshofer.quaqua.color.PaintableColor;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.*;

/**
 * QuaquaLabelUI.
 *
 * @author  Werner Randelshofer
 * @version 1.6.1 2009-02-01 Added support for property "Quaqua.Label.style"=
 * "row", "rowSelected", "category" and "categorySelected" to support side bar JTrees.
 * <br>1.6 2007-11-18 Added support for property "Quaqua.Label.style"=
 * "emboss" and "shadow".
 * <br>1.5.1 2007-01-15 Perceiced bounds.height must reflect font size
 * even if the label is empty.
 * <br>1.5 2006-02-18 Tweaked perceived text bounds. Draw disabled label
 * with disabled text color. Draw background again if we are opaque.
 * <br>1.4 2005-12-08 Support for background border added.
 * <br>1.3 2005-07-17 Adapted to changes in interface VisuallyLayoutable.
 * <br>1.2 2006-06-20 Paint text antialiased.
 * <br>1.0  02 April 2005  Created.
 */
public class QuaquaLabelUI extends BasicLabelUI implements VisuallyLayoutable {

    protected static QuaquaLabelUI labelUI = new QuaquaLabelUI();
    /* These rectangles/insets are allocated once for this shared LabelUI
     * implementation.  Re-using rectangles rather than allocating
     * them in each getPreferredSize call sped up the method substantially.
     */
    private static Rectangle iconR = new Rectangle();
    private static Rectangle textR = new Rectangle();
    private static Rectangle viewR = new Rectangle();
    private static Insets viewInsets = new Insets(0, 0, 0, 0);

    /**
     * Preferred spacing between labels and other components.
     * Pixels from colon and associated controls (RadioButton,
     * CheckBox)
     * /
     * private final static Insets associatedRegularSpacing = new Insets(8,8,8,8);
     * private final static Insets associatedSmallSpacing = new Insets(6,6,6,6);
     * private final static Insets associatedMiniSpacing = new Insets(5,5,5,5);
     */
    public static ComponentUI createUI(JComponent c) {
        return labelUI;
    }

    protected void installDefaults(JLabel b) {
        super.installDefaults(b);

        // load shared instance defaults
        LookAndFeel.installBorder(b, "Label.border");

        // FIXME - Very, very dirty trick to achieve small labels on sliders
        //         This hack should be removed, when we implement a SliderUI
        //         on our own.
        if (b.getClass().getName().endsWith("LabelUIResource")) {
            b.setFont(UIManager.getFont("Slider.labelFont"));
        }
    }

    public void paint(Graphics gr, JComponent c) {
        Graphics2D g = (Graphics2D) gr;
        Object oldHints = QuaquaUtilities.beginGraphics(g);

        // Paint background again so that the texture paint is drawn
        if (c.isOpaque()) {
            g.setPaint(PaintableColor.getPaint(c.getBackground(), c));
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }

        // Paint background border
        Border b = c.getBorder();
        if (b != null && b instanceof BackgroundBorder) {
            ((BackgroundBorder) b).getBackgroundBorder().paintBorder(c, g, 0, 0, c.getWidth(), c.getHeight());
        }

        super.paint(g, c);
        QuaquaUtilities.endGraphics(g, oldHints);
        Debug.paint(g, c, this);
    }

    /**
     * Paint label with disabled text color.
     *
     * @see #paint
     * @see #paintEnabledText
     */
    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        // Make sure we render with the right drawing properties and make sure
        // we can edit them by client properties
        Font font = l.getFont();
        Color foreground = UIManager.getColor("Label.disabledForeground");
        int accChar = -1; //l.getDisplayedMnemonicIndex();
        
        String style = (String) l.getClientProperty("Quaqua.Label.style");
        if (style != null) {
            boolean selected = style.endsWith("Selected");

            if ((style.equals("category") || style.equals("categorySelected"))
                    && UIManager.getFont("Tree.category.font.sideBar") != null
                    && UIManager.getColor("Tree.category.foreground.sideBar") != null) {
                
                s = s.toUpperCase();
                font = UIManager.getFont("Tree.category.font.sideBar");
                style = (selected) ? "shadow" : "emboss";
            } else if ((style.equals("row") || style.equals("rowSelected"))
                    && UIManager.getFont("Tree.font.sideBar") != null
                    && UIManager.getFont("Tree.font.selected.sideBar") != null
                && UIManager.getColor("Tree.foreground.sideBar") != null) {
                
                font = selected ? UIManager
                        .getFont("Tree.font.selected.sideBar") : UIManager
                        .getFont("Tree.font.sideBar");
                style = selected ? "shadow" : null;
            }

            if (style != null && style.equals("emboss")
                    && UIManager.getColor("Label.embossForeground") != null) {
                g.setFont(font);
                g.setColor(UIManager.getColor("Label.embossForeground"));
                QuaquaUtilities.drawString(g, s, accChar, textX, textY + 1);
            } else if (style != null && style.equals("shadow")
                    && UIManager.getColor("Label.shadowForeground") != null) {
                g.setFont(font);
                g.setColor(UIManager.getColor("Label.shadowForeground"));
                QuaquaUtilities.drawString(g, s, accChar, textX, textY + 1);
            }
        }

        g.setFont(font);
        g.setColor(foreground);
        QuaquaUtilities.drawString(g, s, accChar,
                textX, textY);
    }

    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        int mnemIndex = l.getDisplayedMnemonicIndex();

        // Make sure we render with the right drawing properties and make sure
        // we can edit them by client properties
        Font font = l.getFont();
        Color foreground = l.getForeground();
        
        String style = (String) l.getClientProperty("Quaqua.Label.style");
        if (style != null) {
            boolean selected = style.endsWith("Selected");

            if ((style.equals("category") || style.equals("categorySelected"))
                    && UIManager.getFont("Tree.category.font.sideBar") != null
                    && UIManager.getColor("Tree.category.foreground.sideBar") != null) {
                
                s = s.toUpperCase();
                font = UIManager.getFont("Tree.category.font.sideBar");
                foreground = UIManager
                        .getColor("Tree.category.foreground.sideBar");
                if (foreground instanceof InactivatableColorUIResource)
                    ((InactivatableColorUIResource) foreground)
                            .setActive(selected);
                style = (selected) ? "shadow" : "emboss";
                
            } else if ((style.equals("row") || style.equals("rowSelected"))
                    && UIManager.getFont("Tree.font.sideBar") != null
                    && UIManager.getColor("Tree.foreground.sideBar") != null) {
                
                font = UIManager.getFont("Tree.font.sideBar");
                foreground = UIManager.getColor("Tree.foreground.sideBar");
                if (foreground instanceof InactivatableColorUIResource)
                    ((InactivatableColorUIResource) foreground)
                            .setActive(selected);
                style = (selected) ? "shadow" : null;
            }

            if (style != null && style.equals("emboss")
                    && UIManager.getColor("Label.embossForeground") != null) {
                g.setFont(font);
                g.setColor(UIManager.getColor("Label.embossForeground"));
                QuaquaUtilities.drawString(g, s, mnemIndex, textX, textY + 1);
            } else if (style != null && style.equals("shadow")
                    && UIManager.getColor("Label.shadowForeground") != null) {
                g.setFont(font);
                g.setColor(UIManager.getColor("Label.shadowForeground"));
                QuaquaUtilities.drawString(g, s, mnemIndex, textX, textY + 1);
            }
        }

        g.setFont(font);
        g.setColor(foreground);
        QuaquaUtilities.drawString(g, s, mnemIndex, textX, textY);
    //SwingUtilities2.drawStringUnderlineCharAt(l, g, s, mnemIndex,
    //                                             textX, textY);
    }

    /**
     * Forwards the call to SwingUtilities.layoutCompoundLabel().
     * This method is here so that a subclass could do Label specific
     * layout and to shorten the method name a little.
     *
     * @see SwingUtilities#layoutCompoundLabel
     */
    protected String layoutCL(
            JLabel label,
            FontMetrics fontMetrics,
            String text,
            Icon icon,
            Rectangle viewR,
            Rectangle iconR,
            Rectangle textR) {
        return SwingUtilities.layoutCompoundLabel(
                (JComponent) label,
                fontMetrics,
                text,
                icon,
                label.getVerticalAlignment(),
                label.getHorizontalAlignment(),
                label.getVerticalTextPosition(),
                label.getHorizontalTextPosition(),
                viewR,
                iconR,
                textR,
                label.getIconTextGap());
    }

    public int getBaseline(JComponent c, int width, int height) {
        Rectangle vb = getVisualBounds(c, VisuallyLayoutable.TEXT_BOUNDS, width, height);
        return (vb == null) ? -1 : vb.y + vb.height;
    }

    public Rectangle getVisualBounds(JComponent c, int type, int width, int height) {
        Rectangle rect = new Rectangle(0, 0, width, height);
        if (type == VisuallyLayoutable.CLIP_BOUNDS) {
            return rect;
        }

        JLabel b = (JLabel) c;
        String text = b.getText();
        boolean isEmpty = (text == null || text.length() == 0);
        if (isEmpty) {
            text = " ";
        }
        Icon icon = (b.isEnabled()) ? b.getIcon() : b.getDisabledIcon();

        Font f = c.getFont();
        FontMetrics fm = c.getFontMetrics(f);
        Insets insets = c.getInsets(viewInsets);

        viewR.x = insets.left;
        viewR.y = insets.top;
        viewR.width = width - (insets.left + insets.right);
        viewR.height = height - (insets.top + insets.bottom);

        iconR.x = iconR.y = iconR.width = iconR.height = 0;
        textR.x = textR.y = textR.width = textR.height = 0;

        String clippedText =
                layoutCL(b, fm, text, icon, viewR, iconR, textR);

        Rectangle textBounds = Fonts.getPerceivedBounds(text, f, c);
        if (isEmpty) {
            textBounds.width = 0;
        }
        int ascent = fm.getAscent();
        textR.x += textBounds.x;
        textR.width = textBounds.width;
        textR.y += ascent + textBounds.y;
        textR.height -= fm.getHeight() - textBounds.height;

        // Determine rect rectangle
        switch (type) {
            case VisuallyLayoutable.COMPONENT_BOUNDS:
                if (icon != null) {
                    rect = textR.union(iconR);
                } else {
                    rect.setBounds(textR);
                }
                break;
            case VisuallyLayoutable.TEXT_BOUNDS:
                if (text == null) {
                    return rect;
                }
                rect.setBounds(textR);
                break;
        }

        return rect;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();

        if (name.equals("JComponent.sizeVariant")) {
            QuaquaUtilities.applySizeVariant((JLabel) evt.getSource());
        } else {
            super.propertyChange(evt);
        }
    }
}
