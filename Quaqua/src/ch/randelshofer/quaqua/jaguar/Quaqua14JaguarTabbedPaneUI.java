/*
 * @(#)Quaqua14JaguarTabbedPaneUI.java 1.0.1  2007-11-01
 *
 * Copyright (c) 2001-2010 Werner Randelshofer
 * Hausmatt 10, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua.jaguar;

import ch.randelshofer.quaqua.*;
import ch.randelshofer.quaqua.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.plaf.*;
import java.io.Serializable;
import javax.swing.plaf.basic.*;
import java.util.*;
/**
 * A replacement for the AquaTabbedPaneUI for Mac OS X 10.2 Jaguar.
 * Tabs of tabbed panes are stacked instead of moved into a popup menu,
 * if not enough space is available to render all tabs in a single line.
 * <p>
 * Supports the following client properties on the children of the JTabbedPane:
 * <code>Quaqua.TabbedPaneChild.contentBackground</code> specifies the background 
 * Color to be used to fill the content border.
 * <code>Quaqua.TabbedPaneChild.contentInsets</code> specifies the insets 
 * to be used to lay out the child component inside the JTabbedPane.
 *
 * @author Werner Randelshofer, Hausmatt 10, CH-6405 Immensee, Switzerland
 * @version 1.0.1 2007-11-01 Fixed NPE which occurs when TabLayoutPolicy is set 
 *to Scroll. 
 * <br>1.0 2006-09-05 Created.
 */
public class Quaqua14JaguarTabbedPaneUI extends Quaqua13JaguarTabbedPaneUI {
    public static ComponentUI createUI(JComponent x) {
        return new Quaqua14JaguarTabbedPaneUI();
    }
    
    protected LayoutManager createLayoutManager() {
        /* XXX - This needs JDK 1.4 to work. We do not support scroll tab layout.
        if (tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT) {
            return super.createLayoutManager();
        }*/
        return new TabbedPaneLayout();
    }
    
    /**
     * Reloads the mnemonics. This should be invoked when a memonic changes,
     * when the title of a mnemonic changes, or when tabs are added/removed.
     */
    protected void updateMnemonics() {
        // XXX - This needs JDK 1.4 to work.
        resetMnemonics();
        for (int counter = tabPane.getTabCount() - 1; counter >= 0;
        counter--) {
            int mnemonic = tabPane.getMnemonicAt(counter);
         
            if (mnemonic > 0) {
                addMnemonic(counter, mnemonic);
            }
        }
    }
    
    protected void paintText(Graphics g, int tabPlacement,
    Font font, FontMetrics metrics, int tabIndex,
    String title, Rectangle textRect,
    boolean isSelected) {
        
        g.setFont(font);
        
        // This needs JDK 1.4 to work.
        View v = getTextViewForTab(tabIndex);

        if (v != null) {
            // html
            v.paint(g, textRect);
        } else {
            // plain text
            // This needs JDK 1.4 to work.
            int mnemIndex = tabPane.getDisplayedMnemonicIndexAt(tabIndex);
             
            if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
                g.setColor(tabPane.getForegroundAt(tabIndex));
                QuaquaUtilities.drawStringUnderlineCharAt(g,
                title, mnemIndex,
                textRect.x, textRect.y + metrics.getAscent());
                
            } else { // tab disabled
                g.setColor(disabledForeground);
                QuaquaUtilities.drawStringUnderlineCharAt(g,
                title, mnemIndex,
                textRect.x, textRect.y + metrics.getAscent());
            }
        }
    }
    
    protected void installComponents() {
        // empty
        // We must not call super, because with Java 1.4 and higher,
        // this would set the 'tabScroller' variable with a non-null value.
    }
    protected ChangeListener createChangeListener() {
        return new TabSelectionHandler();
    }
    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicTabbedPaneUI.
     */  
    public class TabSelectionHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JTabbedPane tabPane = (JTabbedPane)e.getSource();
            tabPane.revalidate();
            tabPane.repaint();
            /*
            if (tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT) {
                int index = tabPane.getSelectedIndex();
                if (index < rects.length && index != -1) {
                    tabScroller.tabPanel.scrollRectToVisible(rects[index]);
                }
            }*/
        }
    }
}
