/**
 * @(#)NonEDTTest.java  1.0  May 1, 2008
 *
 * Copyright (c) 2008 Werner Randelshofer
 * Hausmatt 10, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package test;

import ch.randelshofer.quaqua.QuaquaManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 * NonEDTTest.
 *
 * @author Werner Randelshofer
 * @version 1.0 May 1, 2008 Created.
 */
public class NonEDTTest {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(QuaquaManager.getLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(NonEDTTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        JFileChooser fc = new JFileChooser();
        
        JFrame f = new JFrame("NonEDTest");
        f.getContentPane().add(fc);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }
}
