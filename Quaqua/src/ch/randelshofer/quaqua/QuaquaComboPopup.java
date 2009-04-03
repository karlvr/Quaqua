/*
 * @(#)QuaquaComboPopup.java  1.2.2  2006-11-02
 *
 * Copyright (c) 2004-2006 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import java.lang.reflect.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.io.Serializable;
import java.beans.*;
/**
 * QuaquaComboPopup.
 *
 * @author  Werner Randelshofer
 * @version 1.2.2 2006-11-02 XXX - Ensure that large combo boxes fit on screen.
 * <br>1.2.1 2006-02-13 Fixed background color.
 * <br>1.2 2005-06-21 PropertyChangeHandler which is responsible for detecting
 * whether we are a table cell renderer changed, in order to detect cell rendering in Java 1.3.
 * <br>1.1 2004-10-06 Popup menu width extends itself to accomodate
 * the widest item.
 * <br>1.0 April 11, 2004 Created.
 */
public class QuaquaComboPopup extends BasicComboPopup {
    private QuaquaComboBoxUI qqui;
    
    public QuaquaComboPopup( JComboBox cBox, QuaquaComboBoxUI qqui) {
        super(cBox);
        this.qqui = qqui;
        updateCellRenderer(qqui.isTableCellEditor());
    }
    
    /**
     * Implementation of ComboPopup.show().
     */
    public void show() {
	setListSelection(comboBox.getSelectedIndex());

	Point location = getPopupLocation();
        show( comboBox, location.x, location.y );
        
        // This is required to properly render the selection, when the JComboBox
        // is used as a table cell editor.
        list.repaint();
    }
    
    private void updateCellRenderer(boolean isTableCellEditor) {
        list.setCellRenderer(
                new QuaquaComboBoxCellRenderer(
                comboBox.getRenderer(), isTableCellEditor, comboBox.isEditable()
                ));
    }
    /**
     * Creates a <code>PropertyChangeListener</code> which will be added to
     * the combo box. If this method returns null then it will not
     * be added to the combo box.
     *
     * @return an instance of a <code>PropertyChangeListener</code> or null
     */
    protected PropertyChangeListener createPropertyChangeListener() {
        return new BasicComboPopup.PropertyChangeHandler() {
            public void propertyChange( PropertyChangeEvent e ) {
                super.propertyChange(e);
                String propertyName = e.getPropertyName();
                JComboBox comboBox = (JComboBox)e.getSource();
                
                if ( propertyName.equals( "renderer" ) ||
                        propertyName.equals(QuaquaComboBoxUI.IS_TABLE_CELL_EDITOR)) {
                    updateCellRenderer(e.getNewValue().equals(Boolean.TRUE));
                } else if (propertyName.equals("JComboBox.lightweightKeyboardNavigation")) {
                    // In Java 1.3 we have to use this property to guess whether we
                    // are a table cell editor or not.
                    updateCellRenderer(e.getNewValue() != null && e.getNewValue().equals("Lightweight"));
                } else if ( propertyName.equals( "editable" )) {
                    updateCellRenderer(isTableCellEditor());
                }
            }
        };
    }
    
    private int getMaximumRowCount() {
        return (isEditable() || isTableCellEditor()) ? 
            comboBox.getMaximumRowCount() : 
            100;
    }
    
    /**
     * Calculates the upper left location of the Popup.
     */
    private Point getPopupLocation() {
	Dimension popupSize = comboBox.getSize();
	Insets insets = getInsets();

	// reduce the width of the scrollpane by the insets so that the popup
	// is the same width as the combo box.
	popupSize.setSize(popupSize.width - (insets.right + insets.left), 
			  getPopupHeightForRowCount( getMaximumRowCount()));
	Rectangle popupBounds = computePopupBounds( 0, comboBox.getBounds().height,
                                                    popupSize.width, popupSize.height);
	Dimension scrollSize = popupBounds.getSize();
	Point popupLocation = popupBounds.getLocation();
	    
	scroller.setMaximumSize( scrollSize );
	scroller.setPreferredSize( scrollSize );
	scroller.setMinimumSize( scrollSize );
	
	list.revalidate();

	return popupLocation;
    }
    /**
     * Sets the list selection index to the selectedIndex. This 
     * method is used to synchronize the list selection with the 
     * combo box selection.
     * 
     * @param selectedIndex the index to set the list
     */
    private void setListSelection(int selectedIndex) {
        if ( selectedIndex == -1 ) {
            list.clearSelection();
        }
        else {
            list.setSelectedIndex( selectedIndex );
	    list.ensureIndexIsVisible( selectedIndex );
        }
    }
    /**
     * Calculate the placement and size of the popup portion of the combo box based
     * on the combo box location and the enclosing screen bounds. If
     * no transformations are required, then the returned rectangle will
     * have the same values as the parameters.
     *
     * @param px starting x location
     * @param py starting y location
     * @param pw starting width
     * @param ph starting height
     * @return a rectangle which represents the placement and size of the popup
     */
    protected Rectangle computePopupBounds(int px,int py,int pw,int ph) {
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Rectangle screenBounds;
        int listWidth = getList().getPreferredSize().width;
        Insets margin = qqui.getMargin();
        boolean isTableCellEditor = isTableCellEditor();
        boolean hasScrollBars = hasScrollBars();
        boolean isEditable = isEditable();
        boolean isSmall = QuaquaUtilities.isSmallSizeVariant(comboBox);

        
        if (isTableCellEditor) {
            if (hasScrollBars) {
                pw = Math.max(pw, listWidth + 16);
            } else {
                pw = Math.max(pw, listWidth);
            }
        } else {
            if (hasScrollBars) {
                px += margin.left;
                pw = Math.max(pw - margin.left - margin.right, listWidth + 16);
            } else {
                if (isEditable) {
                    px += margin.left;
                    pw = Math.max(pw - qqui.getArrowWidth() - margin.left, listWidth);
                } else {
                    px += margin.left;
                    pw = Math.max(pw - qqui.getArrowWidth() - margin.left, listWidth);
                }
            }
        }
        // Calculate the desktop dimensions relative to the combo box.
        GraphicsConfiguration gc = comboBox.getGraphicsConfiguration();
        Point p = new Point();
        SwingUtilities.convertPointFromScreen(p, comboBox);
        if (gc != null) {
            // Get the screen insets.
            // This method will work with JDK 1.4 only. Since we want to stay
            // compatible with JDk 1.3, we use the Reflection API to access it.
            //Insets screenInsets = toolkit.getScreenInsets(gc);
            Insets screenInsets;
            try {
                screenInsets = (Insets)
                Toolkit.class.getMethod("getScreenInsets",  new Class[] {GraphicsConfiguration.class})
                .invoke(toolkit, new Object[] {gc});
            } catch (Exception e) {
                //e.printStackTrace();
                screenInsets = new Insets(22,0,0,0);
            }
            // Note: We must create a new rectangle here, because method
            // getBounds does not return a copy of a rectangle on J2SE 1.3.
            screenBounds = new Rectangle(gc.getBounds());
            screenBounds.width -= (screenInsets.left + screenInsets.right);
            screenBounds.height -= (screenInsets.top + screenInsets.bottom);
            screenBounds.x += screenInsets.left;
            screenBounds.y += screenInsets.top;
        } else {
            screenBounds = new Rectangle(p, toolkit.getScreenSize());
        }
        
        if (isDropDown()) {
            if (! isTableCellEditor) {
                if (isEditable) {
                    py -= margin.bottom + 2;
                } else {
                    py -= margin.bottom;
                }
            }
        } else {
            int yOffset;
            if (isTableCellEditor) {
                yOffset = 7;
            } else {
                yOffset = 3 - margin.top;
            }
            int selectedIndex = comboBox.getSelectedIndex();
            if (selectedIndex <= 0) {
                py = -yOffset;
            } else {
                py = -yOffset - list.getCellBounds(0, selectedIndex - 1).height;
                
            }
        }
        
        // Compute the rectangle for the popup menu
        Rectangle rect = new Rectangle(
                px,
                Math.max(py, p.y + screenBounds.y),
                Math.min(screenBounds.width, pw),
                Math.min(screenBounds.height - 40, ph)
                );
        
        // Add the preferred scroll bar width, if the popup does not fit
        // on the available rectangle.
        if (rect.height < ph) {
            rect.width += 16;
        }
        
        return rect;
    }
    
    private boolean isDropDown() {
        return comboBox.isEditable() || hasScrollBars();
    }
    private boolean hasScrollBars() {
        return comboBox.getModel().getSize() > getMaximumRowCount();
    }
    private boolean isEditable() {
        return comboBox.isEditable();
    }
    private boolean isTableCellEditor() {
        return qqui.isTableCellEditor();
    }
    
    /**
     * Configures the popup portion of the combo box. This method is called
     * when the UI class is created.
     */
    protected void configurePopup() {
        super.configurePopup();
        // FIXME - We need to convert the border into a non-UIResource object.
        // An UIResourceObject will be removed from the popup.
        //setBorder( new CompoundBorder(UIManager.getBorder("PopupMenu.border"), new EmptyBorder(0,0,0,0)));
        setBorder(UIManager.getBorder("PopupMenu.border"));
    }
    
    protected void configureList() {
        super.configureList();
        list.setBackground(UIManager.getColor("PopupMenu.background"));
    }
    
}
