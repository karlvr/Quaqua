/*
 * @(#)ComboBoxFullscreenTest.java  1.0  November 13, 2006
 *
 * Copyright (c) 2006 Werner Randelshofer
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
import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.event.*;
import javax.swing.*;

/**
 * ComboBoxFullscreenTest.
 *
 * @author Werner Randelshofer
 * @version 1.0 2008-01-07 Created.
 */
public class ComboBoxFullscreenTest extends JFrame {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(QuaquaManager.getLookAndFeel());
            UIManager.put("PopupMenu.enableHeavyWeightPopup", Boolean.FALSE);
        } catch (Exception e) {

        }


        final JFrame fen = new JFrame();
        JButton openButton = new JButton("Open...");
        openButton.addActionListener(new ActionListener() {

            private JFileChooser chooser;

            public void actionPerformed(ActionEvent e) {
                if (chooser == null) {
                    chooser = new JFileChooser();
                    chooser.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            fen.getContentPane().remove(chooser);
                            fen.getContentPane().validate();
                            fen.getContentPane().repaint();
                        }
                    });
                }

                fen.getContentPane().add(chooser, BorderLayout.CENTER);
                chooser.revalidate();
                chooser.repaint();
            }
        });
        fen.getContentPane().add(
                openButton, BorderLayout.SOUTH);

        GraphicsDevice myDevice = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        myDevice.setFullScreenWindow(fen);
        fen.setSize(
                400, 300);
        fen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fen.setVisible(
                true);
    }
}
