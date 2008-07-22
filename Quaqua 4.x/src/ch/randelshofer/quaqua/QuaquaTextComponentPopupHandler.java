/*
 * @(#)QuaquaTextComponentPopupHandler.java  1.0.1  2007-02-09
 *
 * Copyright (c) 2006 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
/**
 * TextComponentHandler displays a popup menu on a JTextComponent with the
 * cut/copy and paste actions.
 * The Quaqua text component UI's register a shared instance of
 * QuaquaTextComponentPopupHandler as a mouse listener on all JTextComponent's.
 *
 * @author Werner Randelshofer.
 * @version 1.0.1 2007-02-09 Don't display popup if component is not focusable.
 * <br>1.0 April 23, 2006 Created.
 */
public class QuaquaTextComponentPopupHandler extends MouseAdapter {
    private JPopupMenu popupMenu;
    private AbstractAction cutAction;
    private AbstractAction copyAction;
    private AbstractAction pasteAction;
    
    /** Creates a new instance. */
    public QuaquaTextComponentPopupHandler() {
        popupMenu = new JPopupMenu();
        popupMenu.add(cutAction = new DefaultEditorKit.CutAction());
        popupMenu.add(copyAction = new DefaultEditorKit.CopyAction());
        popupMenu.add(pasteAction = new DefaultEditorKit.PasteAction());
        
        cutAction.putValue(Action.NAME, UIManager.getString("TextComponent.cut"));
        copyAction.putValue(Action.NAME, UIManager.getString("TextComponent.copy"));
        pasteAction.putValue(Action.NAME, UIManager.getString("TextComponent.paste"));
    }
    
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showPopup(e);
        }
    }
    
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            showPopup(e);
        }
    }
    
    protected void showPopup(MouseEvent e) {
        JTextComponent src = (JTextComponent) e.getSource();
        
        boolean isFocusable = Methods.invokeGetter(src, "isFocusable", true);
        
        if (src.getClientProperty("Quaqua.TextComponent.showPopup") != Boolean.FALSE &&
                src.isEnabled() &&
                isFocusable &&
                Methods.invokeGetter(src,"getComponentPopupMenu",null) == null) {
            cutAction.setEnabled(! (src instanceof JPasswordField) &&
                    src.getSelectionEnd() > src.getSelectionStart() &&
                    src.isEditable()
                    );
            copyAction.setEnabled(! (src instanceof JPasswordField) &&
                    src.getSelectionEnd() > src.getSelectionStart()
                    );
            pasteAction.setEnabled(src.isEditable()
            );
            src.requestFocus();
            popupMenu.show(src, e.getX(), e.getY());
            e.consume();
        }
    }
}
