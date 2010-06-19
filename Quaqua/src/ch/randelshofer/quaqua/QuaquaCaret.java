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

import javax.swing.plaf.*;
import javax.swing.text.*;

/**
 * QuaquaCaret.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class QuaquaCaret extends DefaultCaret
        implements UIResource {

    boolean isFocused = false;

    public QuaquaCaret(Window window, JTextComponent textComponent) {
    }

    @Override
    protected Highlighter.HighlightPainter getSelectionPainter() {
        return QuaquaHighlighter.painterInstance;
    }

    @Override
    public void setVisible(boolean bool) {
        if (bool == true) {
            // Don't display the caret, if text is selected
            bool = getDot() == getMark();
        }
        super.setVisible(bool);
    }

    @Override
    protected void fireStateChanged() {
        if (isFocused) {
            setVisible(getComponent().isEditable());
        }
        super.fireStateChanged();
    }

    @Override
    public void focusGained(FocusEvent focusevent) {
        JTextComponent textComponent = getComponent();
        if (textComponent.isEnabled()) {
            isFocused = true;
        }
        super.focusGained(focusevent);
    }

    @Override
    public void focusLost(FocusEvent focusevent) {
        isFocused = false;
        super.focusLost(focusevent);
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        if (!evt.isPopupTrigger()) {
            super.mousePressed(evt);
        }
    }
}
