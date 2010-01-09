/*
 * @(#)Quaqua14PantherTabbedPaneUI.java  1.1.1  2006-09-16
 *
 * Copyright (c) 2006-2010 Werner Randelshofer
 * Hausmatt 10, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua.panther;

import ch.randelshofer.quaqua.util.NavigatableTabbedPaneUI;
import ch.randelshofer.quaqua.QuaquaManager;
import ch.randelshofer.quaqua.jaguar.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
/**
 * The Quaqua14PantherTabbedPaneUI uses to the Quaqua13JaguarTabbedPaneUI for
 * the WRAP_TAB_LAYOUT policy and the Quaqua14PantherScrollTabbedPaneUI for
 * the SCROLL_TAB_LAYOUT policy.
 * 
 * @author Werner Randelshofer
 * @version 1.1.1 2006-09-16 Use Quaqua14JaguarTabbedPaneUI instead of 
 * Quaqua13JaguarTabbedPaneUI. 
 * <br>1.1 2006-09-04 Fixed keyboard navigation problems. 
 * <br>1.0 February 5, 2006 Created.
 */
public class Quaqua14PantherTabbedPaneUI extends TabbedPaneUI 
        implements NavigatableTabbedPaneUI {
    private JTabbedPane tabPane;
    private TabbedPaneUI currentUI;
    private PropertyChangeListener propertyChangeListener;
    //private FocusListener focusListener;
    
    /**
     * Creates a new instance.
     */
    public Quaqua14PantherTabbedPaneUI() {
    }
    
    public static ComponentUI createUI(JComponent c) {
	return new Quaqua14PantherTabbedPaneUI();
    }

    public void installUI(JComponent c) {
        this.tabPane = (JTabbedPane) c;
        
        // Tag the tabbed pane with a client property in order to prevent
        // that we are getting in an endless loop, when the layout policy
        // of the tabbed pane is changed.
        if (tabPane.getClientProperty("Quaqua.TabbedPane.tabLayoutPolicy") == null) {
            tabPane.putClientProperty("Quaqua.TabbedPane.tabLayoutPolicy", UIManager.get("TabbedPane.tabLayoutPolicy"));
            tabPane.setTabLayoutPolicy(UIManager.getInt("TabbedPane.tabLayoutPolicy"));
        }
        
        
        if (tabPane.getTabLayoutPolicy() == JTabbedPane.WRAP_TAB_LAYOUT) {
           Quaqua13JaguarTabbedPaneUI qjtpui = (Quaqua13JaguarTabbedPaneUI) Quaqua14JaguarTabbedPaneUI.createUI(c);
           qjtpui.setPropertyPrefix("TabbedPane.wrap.");
           currentUI = qjtpui;
            
        } else {
           Quaqua14PantherScrollTabbedPaneUI qptpui = (Quaqua14PantherScrollTabbedPaneUI) Quaqua14PantherScrollTabbedPaneUI.createUI(c);
           qptpui.setPropertyPrefix("TabbedPane.scroll.");
           currentUI = qptpui;
        }
        currentUI.installUI(c);
        
        tabPane.setRequestFocusEnabled(QuaquaManager.getBoolean("TabbedPane.requestFocusEnabled"));
        
	//installComponents();
        //installDefaults(); 
        installListeners();
        //installKeyboardActions();
    }

    public void uninstallUI(JComponent c) {
        //uninstallKeyboardActions();
        uninstallListeners();
        //uninstallDefaults();
	//uninstallComponents();

        if (currentUI != null) {
            currentUI.uninstallUI(c);
        }
        
        this.tabPane = null;
    }
    
    protected void installListeners() {
        if ((propertyChangeListener = createPropertyChangeListener()) != null) {
            tabPane.addPropertyChangeListener(propertyChangeListener);
        }
      /*  
        if ((focusListener = createFocusListener()) != null) {
            tabPane.addFocusListener(focusListener);
        }*/
    }

    protected void uninstallListeners() {
        if (propertyChangeListener != null) {
            tabPane.removePropertyChangeListener(propertyChangeListener);
            propertyChangeListener = null;
        }
        /*
        if (focusListener != null) {
            tabPane.removeFocusListener(focusListener);
            focusListener = null;
        }*/
    }

    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }
    /*
    protected FocusListener createFocusListener() {
        return new FocusHandler();
    }*/
    

    public Rectangle getTabBounds(JTabbedPane pane, int index) {
        return currentUI.getTabBounds(pane, index);
    }
    
    public int getTabRunCount(JTabbedPane pane) {
        return currentUI.getTabRunCount(pane);
    }
    
    public int tabForCoordinate(JTabbedPane pane, int x, int y) {
        return currentUI.tabForCoordinate(pane, x, y);
    }
    public void paint(Graphics g, JComponent c) {
        currentUI.paint(g, c);
    }
    public void navigateSelectedTab(int direction) {
        ((NavigatableTabbedPaneUI) currentUI).navigateSelectedTab(direction);
    }

    public Integer getIndexForMnemonic(int mnemonic) {
        return ((NavigatableTabbedPaneUI) currentUI).getIndexForMnemonic(mnemonic);
    }

    public boolean requestFocusForVisibleComponent() {
       return ((NavigatableTabbedPaneUI) currentUI).requestFocusForVisibleComponent();
    }
    
    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicTabbedPaneUI.
     */  
    public class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
	    JTabbedPane pane = (JTabbedPane)e.getSource();
	    String name = e.getPropertyName();
            if (name.equals("tabLayoutPolicy")) {
	        Quaqua14PantherTabbedPaneUI.this.uninstallUI(pane);
		Quaqua14PantherTabbedPaneUI.this.installUI(pane);
	    }
	}
    }
}
