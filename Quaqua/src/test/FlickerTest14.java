package test;
/*
 * @(#)FlickerTest14.java  1.0  2. März 2007
 *
 * Copyright (c) 2006 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * mailto:werner.randelshofer@bluewin.ch
 */

import javax.swing.*;
/**
 * FlickerTest14 demonstrates the flickering that occurs, when a look and feel
 * decorated frame is resized.
 * <p>
 * Opens two JFrames. One with native frame decoration, and one with look and 
 * feel decoration. Resizing the frame with native decoration does not flicker.
 * Resizing the frame with look and feel decoration causes its content to flicker.
 * <p>
 * This problem affects: J2SE 1.4, J2SE 5 and J2SE 6 on Mac OS X.
 * 
 * @author Werner Randelshofer
 * @version 1.0 2. März 2007 Created.
 */
public class FlickerTest14 {
    
    /** Creates a new instance. */
    public FlickerTest14() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                JFrame f = new JFrame("Frame with native decoration");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.getContentPane().add(new JLabel("Resize me, I don't flicker"));
                f.pack();
                f.setVisible(true);
                
                f = new JFrame("Frame with look and feel decoration");
                f.setUndecorated(true);
                f.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.getContentPane().add(new JLabel("Resize me, and see me flicker"));
                f.pack();
                f.setLocation(0,80);
                f.setVisible(true);
                
                
            }
        });
        // TODO code application logic here
    }
    
}
