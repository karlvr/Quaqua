/*
 * @(#)QuaquaLookAndFeel.java  
 *
 * Copyright (c) 2003-2010 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */

package ch.randelshofer.quaqua;

/**
 * A J2SE5 backwards compatible version of the {@link QuaquaLookAndFeel} class.
 *
 * @author Werner Randelshofer, Hausmatt 10, CH-6405 Immensee, Switzerland
 * @version $Id$
 */
public class QuaquaLookAndFeel15 extends LookAndFeelProxy15 {
    
    /** Creates a new instance of QuaquaLookAndFeel */
    public QuaquaLookAndFeel15() {
        super(QuaquaManager.getLookAndFeel());
    }
    
}
