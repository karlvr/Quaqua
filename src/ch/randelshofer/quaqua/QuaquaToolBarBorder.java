/*
 * @(#)QuaquaToolBarBorder.java  1.1  2005-12-18
 *
 * Copyright (c) 2004 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
/**
 * QuaquaToolBarBorder.
 *
 * @author  Werner Randelshofer
 * @version 1.1 2005-12-18 Tweaked insets.
 * <br>1.0.4 2005-12-09 Inner class UIResource added.
 * <br>1.0.3 2005-09-10 Dont' implement UIResource.
 * <br>1.0.2 2005-05-28 Fixed class cast exceptions in methods paintBorder
 * and getBorderInsets.
 * <br>1.0.1 2005-04-21 Fixed insets.
 * <br>1.0  2005-03-30  Created.
 */
public class QuaquaToolBarBorder
extends AbstractBorder
implements SwingConstants {
    
    private final static Color bright = new Color(0x999999, true);
    private final static Color dark = new Color(0x8c8c8c);
    private final static Color divider = new Color(0x9f9f9f);
    /*
    private final static Color dark = new Color(0x999999);
    private final static Color bright = new Color(0xb3b3b3);
    /*
     *//*
    private final static Color dark = new Color(0x808080);
    private final static Color bright = new Color(0xcccccc);
      **/
    public void paintBorder(Component component, Graphics g, int x, int y, int w, int h) {
        if ((component instanceof JToolBar) 
        && ((((JToolBar) component).getUI()) instanceof QuaquaToolBarUI)) {
            JToolBar c = (JToolBar) component;
            boolean isDividerDrawn = isDividerDrawn(c);
            int dividerLocation = getDividerLocation(c);
            if (c.isFloatable() ) {
                int hx = x, hy = y, hw = w, hh = h;
                if (isDividerDrawn) {
                    hx = (dividerLocation == WEST) ? x + 1 : x;
                    hy = (dividerLocation == NORTH) ? y + 1 : y;
                    hw = (dividerLocation == EAST || dividerLocation == WEST) ? w - 1 : w;
                    hh = (dividerLocation == SOUTH || dividerLocation == NORTH) ? h - 1 : h;
                }
                if (c.getOrientation() == HORIZONTAL ) {
                    if( QuaquaUtilities.isLeftToRight(c) ) {
                        g.setColor(bright);
                        g.fillRect(hx+2,hy+2,1,hh - 4);
                        g.fillRect(hx+5,hy+2,1,hh - 4);
                        g.setColor(dark);
                        g.fillRect(hx+3,hy+2,1,hh - 4);
                        g.fillRect(hx+6,hy+2,1,hh - 4);
                    } else {
                        g.setColor(bright);
                        g.fillRect(hw - hx - 3,hy+2,1,hh - 4);
                        g.fillRect(hw - hx - 5,hy+2,1,hh - 4);
                        g.setColor(dark);
                        g.fillRect(hw - hx - 2,hy+2,1,hh - 4);
                        g.fillRect(hw - hx - 6,hy+2,1,hh - 4);
                    }
                }
                else // vertical
                {
                    g.setColor(bright);
                    g.fillRect(hx+2,hy+2,hw-4,1);
                    g.fillRect(hx+2,hy+5,hw-4,1);
                    g.setColor(dark);
                    g.fillRect(hx+2,hy+3,hw-4,1);
                    g.fillRect(hx+2,hy+6,hw-4,1);
                }
            }
            if (isDividerDrawn) {
                g.setColor(divider);
                switch (dividerLocation) {
                    case NORTH :
                        g.fillRect(x,y,w,1);
                        break;
                    case EAST  :
                        g.fillRect(x + w - 1,y, 1, h);
                        break;
                    case SOUTH :
                        g.fillRect(x,y + h - 1,w,1);
                        break;
                    case WEST  :
                        g.fillRect(x,y,1,h);
                        break;
                    default :
                        break;
                }
            }
        }
    }
    
    public Insets getBorderInsets(Component c) {
        return getBorderInsets(c, new Insets(0,0,0,0));
    }
    
    private boolean isDividerDrawn(JToolBar c) {
        Object value = c.getClientProperty(QuaquaToolBarUI.IS_DIVIDER_DRAWN);
        
        return value == null || value.equals(Boolean.TRUE);
    }
    
    /**
     * Returns SwingConstants.NORTH, .SOUTH, .EAST, .WEST or -1.
     */
    private int getDividerLocation(JToolBar c) {
        if (! ((BasicToolBarUI) c.getUI()).isFloating()
        && c.getParent() != null) {
            Dimension parentSize = c.getParent().getSize();
            Insets parentInsets = c.getParent().getInsets();
            Rectangle bounds = c.getBounds();
            
            boolean fillsWidth = bounds.width >= parentSize.width - parentInsets.left - parentInsets.right;
            boolean fillsHeight = bounds.height >= parentSize.height - parentInsets.top - parentInsets.bottom;
            
            if (fillsWidth && fillsHeight) {
                return -1;
            }
            
            if (fillsWidth) {
                if (bounds.y == parentInsets.top) {
                    return SOUTH;
                } else {
                    return NORTH;
                }
            }
            
            if (fillsHeight) {
                if (bounds.x == parentInsets.left) {
                    return EAST;
                } else {
                    return WEST;
                }
            }
        }
        return -1;
    }
    
    public Insets getBorderInsets(Component component, Insets newInsets) {
        if ((component instanceof JToolBar) 
        && ((((JToolBar) component).getUI()) instanceof QuaquaToolBarUI)) {
            JToolBar c = (JToolBar) component;
            newInsets.top = newInsets.left = newInsets.bottom = newInsets.right = 0;
            boolean isFloatable = c.isFloatable();
            if (isFloatable) {
                if (c.getOrientation() == HORIZONTAL ) {
                    if (c.getComponentOrientation().isLeftToRight()) {
                        newInsets.left = 16;
                    } else {
                        newInsets.right = 16;
                    }
                } else {// vertical
                    newInsets.top = 16;
                }
            } else {
                if (c.getOrientation() == HORIZONTAL ) {
                    if (c.getComponentOrientation().isLeftToRight()) {
                        //newInsets.left = 7;
                        newInsets.left = 4;
                    } else {
                        //newInsets.right = 7;
                        newInsets.right = 4;
                    }
                } else {// vertical
                    //newInsets.top = 16;
                }
            }
            if (isDividerDrawn(c)) {
                if (isFloatable && ((QuaquaToolBarUI) c.getUI()).isFloating()) {
                    newInsets.top++;
                    //newInsets.bottom++;
                    newInsets.right++;
                    //newInsets.left++;
                } else {
                    switch (getDividerLocation(c)) {
                        case SOUTH : newInsets.bottom++; break;
                        case EAST  : newInsets.right++; break;
                        case NORTH : newInsets.top++; break;
                        case WEST  : newInsets.left++; break;
                        default : break;
                    }
                }
            }
            Insets margin = c.getMargin();
            
            if (margin != null ) {
                newInsets.left   += margin.left;
                newInsets.top    += margin.top;
                newInsets.right  += margin.right;
                newInsets.bottom += margin.bottom;
            }
            return newInsets;
        } else {
            return new Insets(0,0,0,0);
        }
    }
    
    public static class UIResource extends QuaquaToolBarBorder implements javax.swing.plaf.UIResource {
        
    }
}
