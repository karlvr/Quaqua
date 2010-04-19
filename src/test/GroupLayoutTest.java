package test;
/*
 * @(#)GroupLayoutTest.java  1.0  June 5, 2006
 *
 * Copyright (c) 2006 Werner Randelshofer
 * Hausmatt 10, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import javax.swing.*;
/**
 * GroupLayoutTest.
 * 
 * 
 * @author Werner Randelshofer
 * @version 1.0  June 5, 2006  Created.
 */
public class GroupLayoutTest extends javax.swing.JPanel {
    
    /** Creates a new instance. */
    public GroupLayoutTest() {
        initComponents();
    }
    
    
    public static void main(String[] args) {
        /*
        try {
            UIManager.setLookAndFeel(QuaquaManager.getLookAndFeelClassName());
        } catch (Exception e) {
            
        }*/
        
        JFrame f = new JFrame("Group Layout Test");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GroupLayoutTest nt = new GroupLayoutTest();
        f.getContentPane().add(nt);
        f.setSize(400,400);
        f.setVisible(true);
        //nt.test();
    }
    
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();

        jLabel1.setText("Radio Buttons Label");

        jRadioButton1.setText("Radio Button 1");

        jRadioButton2.setText("Radio Button 2");

        jRadioButton3.setText("Radio Button 3");

        radioButtonGroup = new javax.swing.ButtonGroup();
        radioButtonGroup.add(jRadioButton1);
        radioButtonGroup.add(jRadioButton2);
        radioButtonGroup.add(jRadioButton3);

        jLabel2.setText("Check Boxes Label");

        jCheckBox1.setText("Check Box 1");

        jCheckBox2.setText("Check Box 2");

        jCheckBox3.setText("Check Box 3");

        jLabel3.setText("Text Fields Label");

        jTextField1.setText("Text Field 1");

        jTextField2.setText("Text Field 2");

        jTextField3.setText("Text Field 3");

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jLabel2)
                    .add(jLabel3))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(layout.createParallelGroup(GroupLayout.LEADING)
                    .add(jTextField3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(jTextField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(jCheckBox3)
                    .add(jRadioButton3)
                    .add(jRadioButton2)
                    .add(jRadioButton1)
                    .add(layout.createParallelGroup(GroupLayout.TRAILING)
                        .add(jCheckBox2)
                        .add(jCheckBox1)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                     .add(jLabel1)
                    .add(layout.createSequentialGroup()
                        .add(true, jRadioButton1)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jRadioButton2)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jRadioButton3)
                    )
                )
                .addPreferredGap(LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(layout.createSequentialGroup()
                        .add(true, jCheckBox1)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jCheckBox2)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jCheckBox3)
                    )
                )
                .addPreferredGap(LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(layout.createSequentialGroup()
                        .add(true, jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jTextField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jTextField3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    )
                )
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     * /
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        radioButtonGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();

        jLabel1.setText("Radio Buttons Label");

        radioButtonGroup.add(jRadioButton1);
        jRadioButton1.setText("Radio Button 1");
        jRadioButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));

        radioButtonGroup.add(jRadioButton2);
        jRadioButton2.setText("Radio Button 2");
        jRadioButton2.setMargin(new java.awt.Insets(0, 0, 0, 0));

        radioButtonGroup.add(jRadioButton3);
        jRadioButton3.setText("Radio Button 3");
        jRadioButton3.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel2.setText("Check Boxes Label");

        jCheckBox1.setText("Check Box 1");
        jCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jCheckBox2.setText("Check Box 2");
        jCheckBox2.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jCheckBox3.setText("Check Box 3");
        jCheckBox3.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel3.setText("Text Fields Label");

        jTextField1.setText("Text Field 1");

        jTextField2.setText("Text Field 2");

        jTextField3.setText("Text Field 3");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox3)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton1)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jCheckBox2)
                        .addComponent(jCheckBox1)))
                .addContainerGap(2204, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jRadioButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jCheckBox1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(265, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    */
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.ButtonGroup radioButtonGroup;
    // End of variables declaration//GEN-END:variables
    
}
