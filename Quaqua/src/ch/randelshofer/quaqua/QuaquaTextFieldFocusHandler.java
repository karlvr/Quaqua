/*
 * @(#)QuaquaTextFieldFocusHandler.java
 *
 * Copyright (c) 2009-2010 Werner Randelshofer
 * Hausmatt 10, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package ch.randelshofer.quaqua;

import java.awt.KeyboardFocusManager;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import sun.awt.CausedFocusEvent;
import sun.awt.CausedFocusEvent.Cause;

/**
 * QuaquaTextFieldFocusHandler. Selects all text of a JTextComponent, if
 * the user used a keyboard focus traversal key, to transfer the focus on the
 * JTextComponent.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class QuaquaTextFieldFocusHandler implements FocusListener {

    private static QuaquaTextFieldFocusHandler instance;

    public static QuaquaTextFieldFocusHandler getInstance() {
        if (instance == null) {
            instance = new QuaquaTextFieldFocusHandler();
        }
        return instance;
    }

    /**
     * Allow instance creation by UIManager.
     */
    public QuaquaTextFieldFocusHandler() {
    }

    public void focusGained(FocusEvent event) {
        QuaquaUtilities.repaintBorder((JComponent) event.getComponent());

        final JTextComponent tc = (JTextComponent) event.getSource();
        if (tc.isEditable() && tc.isEnabled()) {

            String uiProperty;
            if (tc instanceof JPasswordField) {
                uiProperty = "PasswordField.autoSelect";
            } else if (tc instanceof JFormattedTextField) {
                uiProperty = "FormattedTextField.autoSelect";
            } else {
                uiProperty = "TextField.autoSelect";
            }

            if (tc.getClientProperty("Quaqua.TextComponent.autoSelect") == Boolean.TRUE ||
                    tc.getClientProperty("Quaqua.TextComponent.autoSelect") == null &&
                    QuaquaManager.getBoolean(uiProperty)) {
                if (event instanceof CausedFocusEvent) {
                    CausedFocusEvent cfEvent = (CausedFocusEvent) event;
                    if (cfEvent.getCause() == Cause.TRAVERSAL_FORWARD ||
                            cfEvent.getCause() == Cause.TRAVERSAL_BACKWARD) {
                        tc.selectAll();
                    }
                }
            }
        }
        if (KeyboardFocusManager.getCurrentKeyboardFocusManager() instanceof QuaquaKeyboardFocusManager) {
            QuaquaKeyboardFocusManager kfm = (QuaquaKeyboardFocusManager) KeyboardFocusManager.getCurrentKeyboardFocusManager();
            kfm.setLastKeyboardTraversingComponent(null);
        }
    }

    public void focusLost(FocusEvent event) {
        QuaquaUtilities.repaintBorder((JComponent) event.getComponent());
    }
}

