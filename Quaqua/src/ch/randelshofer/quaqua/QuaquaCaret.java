/*
 * @(#)QuaquaCaret.java  
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
import java.awt.event.*;
import java.beans.*;

import javax.swing.plaf.*;
import javax.swing.text.*;
/**
 * QuaquaCaret.
 *
 * @author  Werner Randelshofer
 * @version 1.0  July 5, 2004  Created.
 */
public class QuaquaCaret extends DefaultCaret
        implements UIResource, PropertyChangeListener {
    boolean isFocused = false;
    
    public QuaquaCaret(Window window, JTextComponent textComponent) {
        textComponent.addPropertyChangeListener(this);
    }
    
    protected Highlighter.HighlightPainter getSelectionPainter() {
        return QuaquaHighlighter.painterInstance;
    }
    
    public void setVisible(boolean bool) {
        if (bool == true) {
            // Don't display the caret, if text is selected
            bool = getDot() == getMark();
        }
        super.setVisible(bool);
    }
    
    protected void fireStateChanged() {
        if (isFocused)
            setVisible(getComponent().isEditable());
        super.fireStateChanged();
    }
    
    public void propertyChange(PropertyChangeEvent event) {
        // XXX - Do we need the code below or don't we??
        /*
        String name = event.getPropertyName();
        if ("Frame.active".equals(name)) {
            JTextComponent textComponent
                    = (JTextComponent) event.getSource();
            if (event.getNewValue() == Boolean.TRUE) {
                boolean isFocusOwner = Methods.invokeGetter(
                        textComponent,"isFocusOwner",
                        textComponent.hasFocus()
                        );
                setVisible(textComponent.hasFocus());
            } else {
                setVisible(false);
            }
            if (getDot() != getMark())
                textComponent.getUI().damageRange(textComponent, getDot(),
                        getMark());
        }*/
    }
    
    public void focusGained(FocusEvent focusevent) {
        JTextComponent textComponent = getComponent();
        if (textComponent.isEnabled()) {
            isFocused = true;
        }
        super.focusGained(focusevent);
    }

    public void focusLost(FocusEvent focusevent) {
        isFocused = false;
        super.focusLost(focusevent);
    }
    
    public void mousePressed(MouseEvent evt) {
        if (!evt.isPopupTrigger()) {
            super.mousePressed(evt);
        }
    }
    /*
    protected synchronized void damage(Rectangle r) {
        if (r != null) {
            x = r.x - 4;
            y = r.y;
            width = 10;
            height = r.height;
            Rectangle rectangle_0_ = new Rectangle(x, y, width, height);
            Border border = getComponent().getBorder();
            if (border != null) {
                Rectangle rectangle_1_ = getComponent().getBounds();
                rectangle_1_.x = rectangle_1_.y = 0;
                Insets insets = border.getBorderInsets(getComponent());
                rectangle_1_.x += insets.left;
                rectangle_1_.y += insets.top;
                rectangle_1_.width -= insets.left + insets.right;
                rectangle_1_.height -= insets.top + insets.bottom;
                Rectangle2D.intersect(rectangle_0_, rectangle_1_,
                                      rectangle_0_);
            }
            x = rectangle_0_.x;
            y = rectangle_0_.y;
            width = Math.max(rectangle_0_.width, 1);
            height = Math.max(rectangle_0_.height, 1);
            repaint();
        }
    }*/
}
