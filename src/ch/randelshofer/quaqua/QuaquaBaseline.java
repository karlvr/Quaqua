/*
 * @(#)QuaquaBaseline.java 
 *
 * Copyright (c) 2005-2010 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */

package ch.randelshofer.quaqua;

import org.jdesktop.layout.*;
import java.awt.*;
import java.lang.reflect.*;
import javax.swing.*;
/**
 * QuaquaBaseline.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class QuaquaBaseline extends Baseline {
    
    /**
     * Creates a new instance.
     */
    public QuaquaBaseline() {
    }
    
    /**
     * Returns the baseline for the specified component, or -1 if the
     * baseline can not be determined.  The baseline is measured from
     * the top of the component.
     *
     * @param component JComponent to calculate baseline for
     * @param width Width of the component to determine baseline for.
     * @param height Height of the component to determine baseline for.
     * @return baseline for the specified component
     */
    @Override
    public int getComponentBaseline(JComponent component, int width, int height) {
        int baseline = getBaselineFromUI(component, width, height);
        return baseline;
    }
    
    private int getBaselineFromUI(JComponent component, int width, int height) {
        try {
            Method getUI = component.getClass().getMethod("getUI", new Class[0]);
            Object ui = getUI.invoke(component, new Object[0]);
            if (ui instanceof VisuallyLayoutable) {
                return ((VisuallyLayoutable) ui).getBaseline(component, width, height);
            }
        } catch (Exception e) {
            InternalError error = new InternalError();
            error.initCause(e);
            throw error;
        }
        return -1;
    }
}
