/*
 * @(#)QuaquaViewportUI.java  1.4  2007-11-1
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

import ch.randelshofer.quaqua.util.*;
import ch.randelshofer.quaqua.util.ViewportPainter;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.event.*;

/**
 * The Quaqua user interface delegate for a JViewport.
 *
 * @author  Werner Randelshofer
 * @version 1.4 2007-11-11 Added property change listener. 
 * <br>1.3 2007-01-16 Listen to focus changes on the viewport and its
 * child component, and repaint the parent scroll pane border on focus change.
 * <br>1.2.2 2005-11-26 Retrieve default opaqueness from UIManager.
 * Fixed broken invocation of ViewportPainter.
 * <br>1.2.1 2005-09-17 Only paint viewport if it is opaque.
 * <br>1.2 2005-08-25 Fill the viewport with the background color of its
 * viewport view if the UI of the viewport is not an instance of ViewportPainter.
 * <br>1.1 2004-12-14 Do not check whether the view is striped or not,
 * always use the view for drawing the viewport.
 * <br>1.0  June 22, 2004  Created.
 */
public class QuaquaViewportUI extends BasicViewportUI {
    private ChangeListener changeListener;
    private ContainerListener containerListener;
    private FocusListener focusListener;
    private PropertyChangeListener propertyChangeListener;
    private JViewport viewport;
    
    public static ComponentUI createUI(JComponent c) {
        return new QuaquaViewportUI();
    }
    public void paint(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            Component view = viewport.getView();
            Object ui = (view == null) ? null : Methods.invokeGetter(view, "getUI", null);
            if (ui instanceof ViewportPainter) {
                ((ViewportPainter) ui).paintViewport(g, viewport);
            } else {
                if (viewport.getView() != null) {
                    g.setColor(viewport.getView().getBackground());
                    g.fillRect(0,0,c.getWidth(),c.getHeight());
                }
            }
        }
        Debug.paint(g, c, this);
    }
    
    public void installUI(JComponent c) {
        viewport = (JViewport) c;
        super.installUI(c);
        //c.setOpaque(QuaquaManager.getBoolean("Viewport.opaque"));
	QuaquaUtilities.installProperty(c, "opaque", UIManager.get("Viewport.opaque"));
        installListeners();
    }
    
    public void uninstallUI(JComponent c) {
        viewport = (JViewport) c;
        super.uninstallUI(c);
        uninstallListeners();
    }
    /**
     * Attaches listeners to the JTable.
     */
    protected void installListeners() {
        changeListener = createChangeListener();
        viewport.addChangeListener(changeListener);
        containerListener = createContainerListener();
        viewport.addContainerListener(containerListener);
        focusListener = createFocusListener();
        viewport.addFocusListener(focusListener);
        propertyChangeListener = createPropertyChangeListener();
        viewport.addPropertyChangeListener(propertyChangeListener);
        if (viewport.getView() != null) {
            viewport.getView().addFocusListener(focusListener);
        }
    }
    protected void uninstallListeners() {
        viewport.removeChangeListener(changeListener);
        viewport.removeContainerListener(containerListener);
        viewport.removeFocusListener(focusListener);
        viewport.removePropertyChangeListener(propertyChangeListener);
        changeListener = null;
        containerListener = null;
        
    }
    protected PropertyChangeListener createPropertyChangeListener() {
        return new QuaquaPropertyChangeHandler();
    }
    
    private ChangeListener createChangeListener() {
        return new ChangeHandler();
    }
    private ContainerListener createContainerListener() {
        return new ContainerHandler();
    }
    private FocusListener createFocusListener() {
        return QuaquaFocusHandler.getInstance();
    }
    
    /**
     * We need to repaint the viewport if the location of a striped view
     * changes.
     */
    private class ChangeHandler implements ChangeListener {
        private Point previousLocation = new Point();
        public void stateChanged(ChangeEvent e) {

            if (viewport.getView() != null) {
                Component view = viewport.getView();
                
                Point newLocation = view.getLocation();
                if (! previousLocation.equals(newLocation)) {
                    if (view.getHeight() < viewport.getHeight()) {
                        if (newLocation.x > previousLocation.x) {
                            viewport.repaint(0, view.getHeight(), newLocation.x - previousLocation.x, viewport.getHeight() - view.getHeight());
                        }
                        if (newLocation.x < previousLocation.x) {
                            viewport.repaint(viewport.getWidth() + newLocation.x - previousLocation.x, view.getHeight(), previousLocation.x - newLocation.x, viewport.getHeight() - view.getHeight());
                        }
                    }
                    if (view.getWidth() < viewport.getWidth()) {
                        if (newLocation.y > previousLocation.y) {
                            viewport.repaint(view.getWidth(), 0, viewport.getWidth() - view.getWidth(), Math.min(view.getHeight(),newLocation.y - previousLocation.y));
                        }
                        if (newLocation.y < previousLocation.y) {
                            viewport.repaint(
                            view.getWidth(),
                            Math.min(view.getHeight(),viewport.getHeight()) - previousLocation.y + newLocation.y,
                            viewport.getWidth() - view.getWidth(),
                            previousLocation.y - newLocation.y
                            );
                        }
                    }
                    previousLocation = newLocation;
                }
            }
        }
    }
    private class ContainerHandler implements ContainerListener {
        public void componentRemoved(ContainerEvent e) {
            e.getChild().removeFocusListener(focusListener);
        }

        public void componentAdded(ContainerEvent e) {
            e.getChild().addFocusListener(focusListener);
        }
    }
    public class QuaquaPropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();
            if ("Frame.active" == name) {
                // we don't need to do anything here yet.
       } else if (name.equals("JComponent.sizeVariant")) {
            QuaquaUtilities.applySizeVariant(viewport);
            }
        }
    }
}
