/*
 * @(#)QuaquaScrollPaneUI.java  
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
import ch.randelshofer.quaqua.util.Debug;
import java.awt.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.border.*;
/**
 * QuaquaScrollPaneUI.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class QuaquaScrollPaneUI extends BasicScrollPaneUI implements VisuallyLayoutable {
    //private HierarchyListener hierarchyListener;
    
    /** Creates a new instance. */
    public QuaquaScrollPaneUI() {
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new QuaquaScrollPaneUI();
    }
    
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
	QuaquaUtilities.installProperty(c, "opaque", UIManager.get("ScrollPane.opaque"));
        c.setFocusable(UIManager.getBoolean("ScrollPane.focusable"));
    }
    @Override
    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler(super.createPropertyChangeListener());
    }
    @Override
    protected void installDefaults(JScrollPane scrollpane) {
        super.installDefaults(scrollpane);
        if (scrollpane.getLayout() instanceof UIResource) {
            ScrollPaneLayout layout = new QuaquaScrollPaneLayout.UIResource();
            scrollpane.setLayout(layout);
            layout.syncWithScrollPane(scrollpane);
        }
    }
    @Override
    protected void uninstallDefaults(JScrollPane scrollpane) {
        super.uninstallDefaults(scrollpane);
        if (scrollpane.getLayout() instanceof UIResource) {
            ScrollPaneLayout layout = new ScrollPaneLayout.UIResource();
            scrollpane.setLayout(layout);
            layout.syncWithScrollPane(scrollpane);
        }
    }
    
    /*
    protected HierarchyListener createHierarchyListener(JScrollPane c) {
        // FIXME: The ComponentActivationHandler repaints the _whole_ JScrollPane.
        // This is inefficient. We only need the border area of the JScrollPane
        // to be repainted.
        return new ComponentActivationHandler(c);
    }*/
    /*
    protected void installListeners(JScrollPane c) {
        hierarchyListener = createHierarchyListener(c);
        if (hierarchyListener != null) {
            c.addHierarchyListener(hierarchyListener);
        }
        super.installListeners(c);
    }
     
    protected void uninstallListeners(JScrollPane c) {
        if (hierarchyListener != null) {
            c.removeHierarchyListener(hierarchyListener);
            hierarchyListener = null;
        }
        super.uninstallListeners(c);
    }*/
    public Insets getVisualMargin(Component c) {
        Insets margin = (Insets) ((JComponent) c).getClientProperty("Quaqua.Component.visualMargin");
        if (margin == null) margin = UIManager.getInsets("Component.visualMargin");
        return (margin == null) ? new Insets(0, 0, 0 ,0) : margin;
    }
    public void update(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            g.setColor(c.getBackground());
            Insets margin = getVisualMargin(c);
            g.fillRect(margin.left, margin.top, c.getWidth() - margin.left - margin.right, c.getHeight() - margin.top - margin.bottom);
            paint(g, c);
            Debug.paint(g, c, this);
        }
    }
    
    public int getBaseline(JComponent c, int width, int height) {
        return -1;
    }
    public Rectangle getVisualBounds(JComponent c, int type, int width, int height) {
        Rectangle bounds = new Rectangle(0,0,width,height);
        if (type == VisuallyLayoutable.CLIP_BOUNDS) {
            return bounds;
        }
        
        JScrollPane b = (JScrollPane) c;
        
        if (type == VisuallyLayoutable.COMPONENT_BOUNDS
        && b.getBorder() != null) {
            Border border = b.getBorder();
            if (border instanceof UIResource) {
                InsetsUtil.subtractInto(getVisualMargin(b), bounds);
            }
            return bounds;
        }
        
        return bounds;
    }
    
    /**
     * PropertyChangeListener for the ScrollBars.
     */
    private class PropertyChangeHandler implements PropertyChangeListener {
        PropertyChangeListener target;
        public PropertyChangeHandler(PropertyChangeListener target) {
            this.target = target;
        }
        // Listens for changes in the model property and reinstalls the
        // horizontal/vertical PropertyChangeListeners.
        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();
            Object source = e.getSource();
            
            if ("Frame.active".equals(name)) {
                QuaquaUtilities.repaintBorder((JComponent) source);
       } else if (name.equals("JComponent.sizeVariant")) {
            QuaquaUtilities.applySizeVariant(scrollpane);
            }
            target.propertyChange(e);
        }
    }
}