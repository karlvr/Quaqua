/*
 * @(#)Quaqua14KeyboardFocusManager.java  1.1  2007-08-13
 *
 * Copyright (c) 2007 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
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
import javax.swing.*;

/**
 * Quaqua14KeyboardFocusManager.
 *
 * @author Werner Randelshofer
 * @version 1.1 2007-08-13 Added setter for lastTraversingComponent.
 * <br>1.0 8. August 2007 Created.
 */
public class Quaqua14KeyboardFocusManager extends DefaultKeyboardFocusManager {
    /**
     * Holds the most recent component, for which focusPreviousComponent
     * or focusNextComponent was invoked.
     */
    private Component lastTraversingComponent;
    
    
    /** Creates a new instance. */
    public Quaqua14KeyboardFocusManager() {
        initDefaults();
    }
    
    /**
     * Initializes the keyboard focus manager with default values.
     */
    protected void initDefaults() {
        setDefaultFocusTraversalPolicy(
                new LayoutFocusTraversalPolicy());

    }
    
    /**
     * Focuses the Component before aComponent, typically based on a
     * FocusTraversalPolicy.
     *
     * @param aComponent the Component that is the basis for the focus
     *        traversal operation
     * @see FocusTraversalPolicy
     * @see Component#transferFocusBackward
     */
    public void focusPreviousComponent(Component aComponent) {
        lastTraversingComponent = aComponent;
        super.focusPreviousComponent(aComponent);
    }
    
    /**
     * Focuses the Component after aComponent, typically based on a
     * FocusTraversalPolicy.
     *
     * @param aComponent the Component that is the basis for the focus
     *        traversal operation
     * @see FocusTraversalPolicy
     * @see Component#transferFocus
     */
    public void focusNextComponent(Component aComponent) {
        lastTraversingComponent = aComponent;
        super.focusNextComponent(aComponent);
    }
    
    /**
     * Returns the most recent component, for which focusPreviousComponent
     * or focusNextComponent was invoked.
     */
    public Component getLastKeyboardTraversingComponent() {
        return lastTraversingComponent;
    }
    /**
     * Sets the most recent component, for which focusPreviousComponent
     * or focusNextComponent was invoked.
     */
    public void setLastKeyboardTraversingComponent(Component newValue) {
        lastTraversingComponent = newValue;
    }
    
}
