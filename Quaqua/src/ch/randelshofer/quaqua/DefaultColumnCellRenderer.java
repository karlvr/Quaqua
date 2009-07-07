/*
 * @(#)DefaultColumnCellRenderer.java 
 *
 * Copyright (c) 2003-2009 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * http://www.randelshofer.ch
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * DefaultColumnCellRenderer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DefaultColumnCellRenderer implements ListCellRenderer  {
    private JPanel panel;
    private JLabel textLabel;
    private JLabel arrowLabel;
    private EmptyBorder noFocusBorder;
    private JBrowser browser;
    protected Icon expandedIcon = null;
    protected Icon selectedExpandedIcon = null;
    protected Icon expandingIcon = null;
    
    public DefaultColumnCellRenderer(JBrowser browser) {
        this.browser = browser;
        
        BufferedImage iconImages[] = Images.split(
                Toolkit.getDefaultToolkit().createImage(
                DefaultColumnCellRenderer.class.getResource("images/Browser.disclosureIcons.png")
                
                
                
                
                
                
                
                ),
                4, true
                
                
                
                
                
                
                
                );
        
        expandedIcon = new ImageIcon(iconImages[0]);
        selectedExpandedIcon = new ImageIcon(iconImages[2]);
        
        noFocusBorder = new EmptyBorder(1, 1, 1, 1);
        panel = new JPanel(new BorderLayout()) {
            // Overridden for performance reasons.
            //public void validate() {}
            public void revalidate() {}
            public void repaint(long tm, int x, int y, int width, int height) {}
            public void repaint(Rectangle r) {}
            protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {}
            public void firePropertyChange(String propertyName, short oldValue, short newValue) {}
            public void firePropertyChange(String propertyName, int oldValue, int newValue) {}
            public void firePropertyChange(String propertyName, long oldValue, long newValue) {}
            public void firePropertyChange(String propertyName, float oldValue, float newValue) {}
            public void firePropertyChange(String propertyName, double oldValue, double newValue) {}
            public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
        };
        
        textLabel = new JLabel() {
            // Overridden for performance reasons.
            public void validate() {}
            public void revalidate() {}
            public void repaint(long tm, int x, int y, int width, int height) {}
            public void repaint(Rectangle r) {}
            protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
                // Strings get interned...
                if (propertyName=="text")
                    super.firePropertyChange(propertyName, oldValue, newValue);
            }
            public void firePropertyChange(String propertyName, short oldValue, short newValue) {}
            public void firePropertyChange(String propertyName, int oldValue, int newValue) {}
            public void firePropertyChange(String propertyName, long oldValue, long newValue) {}
            public void firePropertyChange(String propertyName, float oldValue, float newValue) {}
            public void firePropertyChange(String propertyName, double oldValue, double newValue) {}
            public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
        };
        arrowLabel = new JLabel() {
            // Overridden for performance reasons.
            public void validate() {}
            public void revalidate() {}
            public void repaint(long tm, int x, int y, int width, int height) {}
            public void repaint(Rectangle r) {}
            protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
                // Strings get interned...
                if (propertyName=="text")
                    super.firePropertyChange(propertyName, oldValue, newValue);
            }
            public void firePropertyChange(String propertyName, short oldValue, short newValue) {}
            public void firePropertyChange(String propertyName, int oldValue, int newValue) {}
            public void firePropertyChange(String propertyName, long oldValue, long newValue) {}
            public void firePropertyChange(String propertyName, float oldValue, float newValue) {}
            public void firePropertyChange(String propertyName, double oldValue, double newValue) {}
            public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
        };
        
        panel.setOpaque(true);
        panel.setBorder(noFocusBorder);
        
        textLabel.putClientProperty("Quaqua.Component.visualMargin", new Insets(0,0,0,0));
        arrowLabel.putClientProperty("Quaqua.Component.visualMargin", new Insets(0,0,0,0));
        
        panel.add(textLabel, BorderLayout.CENTER);
        arrowLabel.setIcon(expandedIcon);
        panel.add(arrowLabel, BorderLayout.EAST);
    }
    
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected,
            boolean cellHasFocus) {
        //setComponentOrientation(list.getComponentOrientation());
        boolean isFocused = QuaquaUtilities.isFocused(list);
        
        if (isSelected) {
            panel.setBackground(list.getSelectionBackground());
            Color foreground = (! isFocused && UIManager.getColor("List.inactiveSelectionForeground") != null) ?
                UIManager.getColor("List.inactiveSelectionForeground") :
                list.getSelectionForeground();
            textLabel.setForeground(foreground);
            arrowLabel.setForeground(foreground);
            arrowLabel.setIcon(selectedExpandedIcon);
        } else {
            panel.setBackground(list.getBackground());
          Color foreground = list.getForeground();
            textLabel.setForeground(foreground);
            arrowLabel.setForeground(foreground);
            arrowLabel.setIcon(expandedIcon);
        }
        
        textLabel.setText((value == null) ? "null" : value.toString());
        //textLabel.setIcon(getFileChooser().getIcon(file));
        
        arrowLabel.setVisible(! browser.getModel().isLeaf(value));
        
        
        textLabel.setEnabled(list.isEnabled());
        textLabel.setFont(list.getFont());
        panel.setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
        
        return panel;
    }
    
    public static class UIResource extends DefaultColumnCellRenderer implements javax.swing.plaf.UIResource {
        public UIResource(JBrowser browser) {
            super(browser);
        }
    }
}

