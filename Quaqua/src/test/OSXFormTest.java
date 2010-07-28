/*
 * @(#)OSXFormTest.java  1.0  June 15, 2007
 *
 * Copyright (c) 2007 Werner Randelshofer
 * Hausmatt 10, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package test;

import org.jdesktop.layout.GroupLayout;

import javax.swing.*;

import ch.randelshofer.quaqua.QuaquaLookAndFeel;

/**
*/
public class OSXFormTest
{
   public static void main(String[] args)
   {
       try
       {
           UIManager.setLookAndFeel(QuaquaLookAndFeel.class.getName());
       }
       catch(Exception e)
       {
                 }

       JFrame frame = new JFrame("OSXFORMTEST");
       JPanel panel = new JPanel();
       GroupLayout layout = new GroupLayout(panel);
       panel.setLayout(layout);

       if(UIManager.getLookAndFeel().getID().equals("Aqua"));
       {
           layout.setAutocreateGaps(true);
           layout.setAutocreateContainerGaps(true);
       }

       JLabel artistLabel = new JLabel("Artist");
       JLabel albumLabel = new JLabel("Album");
       JTextField artist = new JTextField("Sonic Youth");
       JTextField album = new JTextField("Bad Moon Rising");
       
       
       int labelAlignment = UIManager.getLookAndFeel().getID().equals("Aqua") ?
            GroupLayout.TRAILING : 
            GroupLayout.LEADING;
       
       GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
       hGroup.add(layout.createParallelGroup(labelAlignment).add(artistLabel).add(albumLabel));
       hGroup.add(layout.createParallelGroup().add(artist).add(album));
       layout.setHorizontalGroup(hGroup);

       GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
       vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(artistLabel).add(artist));
       vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(albumLabel).add(album));
       layout.setVerticalGroup(vGroup);
       frame.add(panel);
       frame.pack();
       frame.setVisible(true);
   }
}
