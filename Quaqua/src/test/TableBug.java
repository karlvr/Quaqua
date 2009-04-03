/*
 * @(#)TableBug.java  1.0  February 4, 2006
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

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.table.*;
/**
 * TableBug.
 *
 * @author  Werner Randelshofer
 * @version 1.0 February 4, 2006 Created.
 */
public class TableBug {
    
    /**
     * Creates a new instance.
     */
    public TableBug() {
    }
    
   public static void main(String[] args)  {
       try {
     UIManager.setLookAndFeel 
("ch.randelshofer.quaqua.QuaquaLookAndFeel");
     
  //   UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
} catch (Throwable t) {
    t.printStackTrace();
}
     JFrame frame = new JFrame();
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     frame.getContentPane().setLayout(new BorderLayout());
     JPanel content = new JPanel(new BorderLayout());

     DefaultTableModel model = new DefaultTableModel();
     model.addColumn("Test");

     JTable jTable = new JTable(model) {
       public Dimension getMinimumSize() {
         return new Dimension(100, 100);
       }
     };
     content.add(new JScrollPane(jTable), BorderLayout.NORTH);
     content.setBorder(new TitledBorder("Border"));

     frame.getContentPane().add(content, BorderLayout.CENTER);
     frame.setBounds(200, 200, 400, 400);
     frame.setVisible(true);
   }

    
}
