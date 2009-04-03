/*
 * @(#)BrowserTest3.java  1.0  August 25, 2005
 *
 * Copyright (c) 2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package test;

import ch.randelshofer.quaqua.*;
import javax.swing.*;
/**
 * BrowserTest3.
 *
 * @author  Werner Randelshofer
 * @version 1.0 August 25, 2005 Created.
 */
public class BrowserTest3 {
    public static void main(String[] args) {
        JFrame f = new JFrame("Browsertest 3");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JBrowser browser = new JBrowser();
        browser.setFixedCellWidth(100);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewport(new JBrowserViewport());
        scrollPane.setViewportView(browser);
        
        f.getContentPane().add(scrollPane);
        f.setSize(400,200);
        f.setVisible(true);
    }
}

