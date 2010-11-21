/*
 * @(#)QuaquaKeyboardFocusManager.java
 *
 * Copyright (c) 2007-2010 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */

package ch.randelshofer.quaqua;

import java.awt.*;

/**
 * QuaquaKeyboardFocusManager.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class QuaquaKeyboardFocusManager extends DefaultKeyboardFocusManager {
    /**
     * Holds the most recent component for which focusPreviousComponent
     * or focusNextComponent was invoked.
     */
    private Component lastTraversingComponent;
    
    
    /** Creates a new instance. */
    public QuaquaKeyboardFocusManager() {
        initDefaults();
    }
    
    /**
     * Initializes the keyboard focus manager with default values.
     */
    protected void initDefaults() {
        setDefaultFocusTraversalPolicy(
                new DefaultFocusTraversalPolicy());

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
    @Override
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
    @Override
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
