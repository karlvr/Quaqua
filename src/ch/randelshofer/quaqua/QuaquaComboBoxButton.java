/*
 * @(#)QuaquaComboBoxButton.java	1.3.2  2008-01-04
 *
 * Copyright (c) 2004-2008 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.ButtonStateBorder;
import java.awt.*;

import javax.swing.CellRendererPane;
import javax.swing.DefaultButtonModel;
import javax.swing.FocusManager;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;

import ch.randelshofer.quaqua.util.Images;

/**
 * JButton subclass to help out QuaquaComboBoxUI.
 *
 * @author  Werner Randelshofer
 * @version 1.3.2 2008-01-04 Set the enabled state of the cell renderer to
 * the enabled state of the combo box.
 * <br>1.3.1 2007-01-30 Use QuaquaUtilities.isFocused to determine whether
 * focus drawing is needed. 
 * <br>1.3 2006-12-24 by Karl von Randow: Use Images class to create artwork. 
 * <br>1.2 2005-11-30 Create a border only when it is needed.
 * <br>1.1.2 2005-09-11 Streamlined image file names with the rest of
 * Quaqua.
 * <br>1.1.1 2005-06-25 Method setBorder must not set a border, because this will result in
 * bad insets, if the Look and Feel is changed after the button has been created.
 * <br>1.1 2005-03-26 FocusBorder renamed to FocusRing. Because it only
 * contains a ring and not a complete border. Honours Component.visualMargin property.
 * <br>1.0 2004-04-27 Created.
 */
public class QuaquaComboBoxButton extends JButton {
    protected JComboBox comboBox;
    protected JList listBox;
    protected CellRendererPane rendererPane;
    protected Icon comboIcon;
    protected boolean iconOnly = false;
    
    /**
     * This is the focus border painted around the button when it has focus.
     */
    private static Border focusRing;
    private static Border getFocusRing() {
        if (focusRing == null) {
            focusRing = QuaquaBorderFactory.create(
            Images.createImage(QuaquaComboBoxButton.class.getResource("images/ComboBox.focusRing.png")),
            new Insets(4, 6, 4, 6),
            new Insets(0, 0, 0, 0),
            false
            );
        }
        return focusRing;
    }
    /**
     * This is the border painted around the cell area.
     */
    private static Border cellBorder;
    private static Border getCellBorder() {
        if (cellBorder == null) {
            cellBorder = new ButtonStateBorder(
            Images.split(
            Images.createImage(QuaquaComboBoxButton.class.getResource("images/ComboBox.cellBorders.png")),
            10, true
            ),
            new Insets(10, 8, 14, 0), new Insets(1, 1, 1, 1), true
            );
        }
        return cellBorder;
    }
    /**
     * This is the border painted around the button area.
     */
    private static Border buttonBorder;
    private static Border getButtonBorder() {
        if (buttonBorder == null) {
            buttonBorder = new ButtonStateBorder(
            Images.split(
            Images.createImage(QuaquaComboBoxButton.class.getResource("images/ComboBox.buttonBorders.png")),
            10, true
            ),
            new Insets(10, 1, 14, 8), new Insets(1, 1, 1, 1), true
            );
        }
        return buttonBorder;
    }
    /**
     * This is the border painted around the cell area.
     */
    private static Border smallCellBorder;
    private static Border getSmallCellBorder() {
        if (smallCellBorder == null) { 
            smallCellBorder = new ButtonStateBorder(
        Images.split(
        Images.createImage(QuaquaComboBoxButton.class.getResource("images/ComboBox.cellBorders.png")),
        10, true
        ),
        new Insets(8, 8, 16, 0), new Insets(1, 1, 1, 1), true
        );
        }
        return smallCellBorder;
    }
    /**
     * This is the border painted around the button area.
     */
    private static Border smallButtonBorder;
    private static Border getSmallButtonBorder() {
        if (smallButtonBorder == null) { 
            smallButtonBorder = new ButtonStateBorder(
        Images.split(
        Images.createImage(QuaquaComboBoxButton.class.getResource("images/ComboBox.buttonBorders.png")),
        10, true
        ),
        new Insets(8, 1, 16, 8), new Insets(1, 1, 1, 1), true
        );
        }
        return smallButtonBorder;
    }
    
    public final JComboBox getComboBox() { return comboBox;}
    public final void setComboBox( JComboBox cb ) { comboBox = cb;}
    
    public final Icon getComboIcon() { return comboIcon;}
    public final void setComboIcon( Icon i ) { comboIcon = i;}
    
    public final boolean isIconOnly() { return iconOnly;}
    public final void setIconOnly( boolean isIconOnly ) { iconOnly = isIconOnly;}
    
    //QuaquaComboBoxButton() {
    public QuaquaComboBoxButton(QuaquaComboBoxUI ui, JComboBox cb, Icon i, boolean onlyIcon, CellRendererPane pane, JList list) {
        super( "" );
        
        DefaultButtonModel model = new DefaultButtonModel() {
            public void setArmed( boolean armed ) {
                super.setArmed( isPressed() ? true : armed );
            }
        };
        setModel(model);
        setBorder(null); // We do all the border handling in QuaquaComboBoxUI
        comboBox = cb;
        comboIcon = i;
        rendererPane = pane;
        listBox = list;
        setEnabled( comboBox.isEnabled() );
        iconOnly = onlyIcon;
    }
    
    public boolean isFocusTraversable() {
        return false;
    }
    
    public void setBorder(Border b) {
        // Empty. We do all border handling in QuaquaComboBoxUI
    }
    /*
    public boolean isOpaque() {
        return false;
    }*/
    
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        // Set the background and foreground to the combobox colors.
        if (enabled) {
            setBackground(comboBox.getBackground());
            setForeground(comboBox.getForeground());
        } else {
            setBackground(UIManager.getColor("ComboBox.disabledBackground"));
            setForeground(UIManager.getColor("ComboBox.disabledForeground"));
        }
    }
    
    public void paintBorder(Graphics g) {
        // Empty: We paint the border in paintComponent.
    }
    
    public void paintComponent(Graphics g) {
       Object savedHints = QuaquaUtilities.beginGraphics((Graphics2D) g);
        QuaquaComboBoxUI ui = (QuaquaComboBoxUI) comboBox.getUI();
        int buttonWidth = ui.getArrowWidth();
        boolean isFrameActive = QuaquaUtilities.isOnActiveWindow(comboBox);
        boolean isTableCellEditor = ui.isTableCellEditor();
        boolean isEditable = comboBox.isEditable();
        boolean isSmall = ui.isSmall();
        Insets insets = getInsets();
        // Paint background and borders
        int x, y, width, height;
        x = insets.left;
        y = insets.top;
        width = getWidth() - insets.left - insets.right;
        height = getHeight() - insets.top - insets.bottom;
        
        if (comboBox.isOpaque()) {
            g.setColor(comboBox.getBackground());
            g.fillRect(0, 0, width, height);
        }
        if (! isTableCellEditor) {
            if (iconOnly) {
                getButtonBorder().paintBorder(this, g, x, y, width, height);
            } else {
                Border border = (isSmall) ? getSmallCellBorder() : getCellBorder();
                border.paintBorder(this, g,
                x, y, width - buttonWidth, height
                );
                border = (isSmall) ? getSmallButtonBorder() : getButtonBorder();
                border.paintBorder(this, g,
                width - buttonWidth, y, buttonWidth, height
                );
            }
        }
        
        
        boolean leftToRight = QuaquaUtilities.isLeftToRight(comboBox);
        
        
        // Paint the icon
        comboIcon = ui.getArrowIcon();
        if ( comboIcon != null ) {
            int iconWidth = comboIcon.getIconWidth();
            int iconHeight = comboIcon.getIconHeight();
            int iconTop = 0;
            int iconLeft = 0;
            
            if (iconOnly) {
                iconLeft = x + (width - buttonWidth) / 2 + (buttonWidth - iconWidth) / 2 - 2;
                iconTop = y + (height - iconHeight) / 2;
            } else {
                if (leftToRight) {
                    iconLeft = x + width - buttonWidth + (buttonWidth - iconWidth) / 2 - 1;
                } else {
                    iconLeft = 0;
                }
                iconTop = y + (height - iconHeight) / 2;
                //if (isSmallSizeVariant) iconTop--;
            }
            comboIcon.paintIcon( this, g, iconLeft, iconTop );
            
            // Paint the focus
            if (QuaquaUtilities.isFocused(comboBox) && ! isTableCellEditor) {
                Border border = null;
                border = getFocusRing();
                if (border != null) {
                    border.paintBorder(this, g, x, y, width, height);
                }
            }
        }
        
        // Let the renderer paint
        if (! iconOnly && comboBox != null) {
            ListCellRenderer renderer = comboBox.getRenderer();
            
            Component c;
            boolean renderPressed = getModel().isPressed();
            c = renderer.getListCellRendererComponent(listBox,
            comboBox.getSelectedItem(),
            -1,
            renderPressed,
            false);
            c.setFont(comboBox.getFont());
            c.setEnabled(comboBox.isEnabled());
            
            Rectangle cellBounds = ((QuaquaComboBoxUI) comboBox.getUI()).rectangleForCurrentValue();
            
            // Fix for 4238829: should lay out the JPanel.
            boolean shouldValidate = false;
            if (c instanceof JPanel)  {
                shouldValidate = true;
            }
            
            
            boolean wasOpaque = c.isOpaque();
            
            if (c instanceof JComponent) {
                ((JComponent) c).setOpaque(false);
            }
            if (leftToRight) {
                rendererPane.paintComponent(g, c, this,
                cellBounds.x - getX(), cellBounds.y - getY(), cellBounds.width, cellBounds.height, shouldValidate);
            } else {
                rendererPane.paintComponent( g, c, this,
                cellBounds.x, cellBounds.y, cellBounds.width, cellBounds.height, shouldValidate);
            }
            if (c instanceof JComponent) {
                ((JComponent) c).setOpaque(wasOpaque);
            }
        }
        QuaquaUtilities.endGraphics((Graphics2D) g, savedHints);
    }
}
