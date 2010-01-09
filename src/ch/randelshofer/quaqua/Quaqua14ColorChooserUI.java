/*
 * @(#)QuaquaColorChooserUI.java  1.1  2005-12-18
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

import ch.randelshofer.quaqua.colorchooser.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
/**
 * QuaquaColorChooserUI with enhancements for Java 1.4.
 *
 * @author  Werner Randelshofer
 * @version 1.1 2005-12-18 Gracefully handle instantiation failures of 
 * color chooser panels.
 * <br>1.0  29 March 2005  Created.
 */
public class Quaqua14ColorChooserUI extends Quaqua13ColorChooserUI {
    private static TransferHandler defaultTransferHandler = new ColorTransferHandler();
    private MouseListener previewMouseListener;
    
    public static ComponentUI createUI(JComponent c) {
        return new Quaqua14ColorChooserUI();
    }
    
    public void installUI(JComponent c) {
        super.installUI(c);
        chooser.applyComponentOrientation(c.getComponentOrientation());
    }
    
    protected void installDefaults() {
        super.installDefaults();
        TransferHandler th = chooser.getTransferHandler();
        if (th == null || th instanceof UIResource) {
            chooser.setTransferHandler(defaultTransferHandler);
        }
    }
    
    protected void uninstallDefaults() {
        if (chooser.getTransferHandler() instanceof UIResource) {
            chooser.setTransferHandler(null);
        }
    }
    
    protected void installListeners() {
        super.installListeners();
        
        previewMouseListener = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (chooser.getDragEnabled()) {
                    TransferHandler th = chooser.getTransferHandler();
                    th.exportAsDrag(chooser, e, TransferHandler.COPY);
                }
            }
        };
        
    }
    
    protected void uninstallListeners() {
        super.uninstallListeners();
        previewPanel.removeMouseListener(previewMouseListener);
    }
    
    protected void installPreviewPanel() {
        if (previewPanel != null) {
            previewPanel.removeMouseListener(previewMouseListener);
        }
        super.installPreviewPanel();
        previewPanel.addMouseListener(previewMouseListener);
    }
    
    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyHandler();
    }
    
    public class PropertyHandler implements PropertyChangeListener {
        
        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();
            if (name.equals( JColorChooser.CHOOSER_PANELS_PROPERTY ) ) {
                AbstractColorChooserPanel[] oldPanels = (AbstractColorChooserPanel[]) e.getOldValue();
                AbstractColorChooserPanel[] newPanels = (AbstractColorChooserPanel[]) e.getNewValue();
                
                for (int i = 0; i < oldPanels.length; i++) {  // remove old panels
                    if (oldPanels[i] != null) {
                        Container wrapper = oldPanels[i].getParent();
                        if (wrapper != null) {
                            Container parent = wrapper.getParent();
                            if (parent != null)
                                parent.remove(wrapper);  // remove from hierarchy
                            oldPanels[i].uninstallChooserPanel(chooser); // uninstall
                        }
                    }
                }
                
                mainPanel.removeAllColorChooserPanels();
                for (int i = 0; i < newPanels.length; i++) {
                    if (newPanels[i] != null) {
                        mainPanel.addColorChooserPanel(newPanels[i]);
                    }
                }
                
                chooser.applyComponentOrientation(chooser.getComponentOrientation());
                for (int i = 0; i < newPanels.length; i++) {
                    if (newPanels[i] != null) {
                        newPanels[i].installChooserPanel(chooser);
                    }
                }
            }
            if (name.equals( JColorChooser.PREVIEW_PANEL_PROPERTY ) ) {
                if (e.getNewValue() != previewPanel) {
                    installPreviewPanel();
                }
            }
            if (name.equals("componentOrientation")) {
                ComponentOrientation o = (ComponentOrientation)e.getNewValue();
                JColorChooser cc = (JColorChooser)e.getSource();
                if (o != (ComponentOrientation)e.getOldValue()) {
                    cc.applyComponentOrientation(o);
                    cc.updateUI();
                }
            }
        }
    }
    static class ColorTransferHandler extends TransferHandler implements UIResource {
        
        ColorTransferHandler() {
            super("color");
        }
    }
}
