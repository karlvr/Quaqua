/*
 * @(#)QuaquaColorChooserUI.java  1.2.4  2007-02-16
 *
 * Copyright (c) 2005-2010 Werner Randelshofer
 * Hausmatt 10, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.colorchooser.*;
import ch.randelshofer.quaqua.util.Methods;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.security.*;
import java.util.*;
/**
 * QuaquaColorChooserUI.
 *
 * @author  Werner Randelshofer
 * @version 1.2.4 2007-02-16 Catch Throwable in method createDefaultChoosers(). 
 * <br>1.2.3 2007-02-15 Install Listeners add the very end of method
 * installUI.
 * <br>1.2.2 2007-02-10 Don't abort when unable to instantiate color chooser
 * panel.
 * <br>1.2.1 2006-05-10 Method createDefaultChoosers mustn't return an
 * an array with null entries in it.
 * <br>1.2 2005-09-18 Read class names of default choosers from UIManager.
 * <br>1.1 2005-08-28 ColorWheelChooser and CrayonsChooser added.
 * <br>1.0  29 March 2005  Created.
 */
public class Quaqua13ColorChooserUI extends ColorChooserUI {
    protected ColorChooserMainPanel mainPanel;
    protected JColorChooser chooser;
    protected ChangeListener previewListener;
    protected PropertyChangeListener propertyChangeListener;
    protected AbstractColorChooserPanel[] defaultChoosers;
    protected JComponent previewPanel;
    
    public static ComponentUI createUI(JComponent c) {
        return new Quaqua13ColorChooserUI();
    }
    
    public void installUI( JComponent c ) {
        chooser = (JColorChooser)c;
                AbstractColorChooserPanel[] oldPanels = chooser.getChooserPanels();
        
        installDefaults();
        
        chooser.setLayout( new BorderLayout() );
        mainPanel = new ColorChooserMainPanel();
        chooser.add(mainPanel);
        defaultChoosers = createDefaultChoosers();
        chooser.setChooserPanels(defaultChoosers);
        
        installPreviewPanel();
                AbstractColorChooserPanel[] newPanels = (AbstractColorChooserPanel[]) chooser.getChooserPanels();
                updateColorChooserPanels(oldPanels, newPanels);
        
        // Note: install listeners only after we have fully installed
        //       all chooser panels. If we do it earlier, we send property
        //       events too early.
        installListeners();
    }
    
    protected AbstractColorChooserPanel[] createDefaultChoosers() {
        String[] defaultChoosers = (String[]) UIManager.get("ColorChooser.defaultChoosers");
        ArrayList panels = new ArrayList(defaultChoosers.length);
        for (int i=0; i < defaultChoosers.length; i++) {
            try {
                
                //panels.add(Class.forName(defaultChoosers[i]).newInstance());
                panels.add(Methods.newInstance(defaultChoosers[i]));
            } catch (AccessControlException e) {
                // suppress
                System.err.println("Quaqua13ColorChooserUI warning: unable to instantiate "+defaultChoosers[i]);
                e.printStackTrace();
            } catch (Exception e) {
                // throw new InternalError("Unable to instantiate "+defaultChoosers[i]);
                // suppress
                System.err.println("Quaqua13ColorChooserUI warning: unable to instantiate "+defaultChoosers[i]);
                e.printStackTrace();
            } catch (UnsupportedClassVersionError e) {
                // suppress
                System.err.println("Quaqua13ColorChooserUI warning: unable to instantiate "+defaultChoosers[i]);
                //e.printStackTrace();
            } catch (Throwable t) {
                System.err.println("Quaqua13ColorChooserUI warning: unable to instantiate "+defaultChoosers[i]);
            }
        }
        //AbstractColorChooserPanel[] panels = new AbstractColorChooserPanel[defaultChoosers.length];
        return (AbstractColorChooserPanel[]) panels.toArray(new AbstractColorChooserPanel[panels.size()]);
    }
    
    
    public void uninstallUI( JComponent c ) {
        chooser.remove(mainPanel);
        
        uninstallListeners();
        uninstallDefaultChoosers();
        uninstallDefaults();
        
        mainPanel.setPreviewPanel(null);
        if (previewPanel instanceof UIResource) {
            chooser.setPreviewPanel(null);
        }
        
        mainPanel = null;
        previewPanel = null;
        defaultChoosers = null;
        chooser = null;
    }
    protected void installDefaults() {
        LookAndFeel.installColorsAndFont(chooser, "ColorChooser.background",
                "ColorChooser.foreground",
                "ColorChooser.font");
    }
    
    protected void uninstallDefaults() {
    }
    
    
    protected void installListeners() {
        propertyChangeListener = createPropertyChangeListener();
        chooser.addPropertyChangeListener( propertyChangeListener );
        
        previewListener = new PreviewListener();
        chooser.getSelectionModel().addChangeListener(previewListener);
    }
    
    protected void uninstallListeners() {
        chooser.removePropertyChangeListener( propertyChangeListener );
        chooser.getSelectionModel().removeChangeListener(previewListener);
    }
    
    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyHandler();
    }
    
    protected void installPreviewPanel() {
        if (previewPanel != null) {
            mainPanel.setPreviewPanel(null);
        }
        
        previewPanel = chooser.getPreviewPanel();
        if ((previewPanel != null) && (mainPanel != null) && (chooser != null) && (previewPanel.getSize().getHeight()+previewPanel.getSize().getWidth() == 0)) {
            mainPanel.setPreviewPanel(null);
            return;
        }
        if (previewPanel == null || previewPanel instanceof UIResource) {
            //previewPanel = ColorChooserComponentFactory.getPreviewPanel(); // get from table?
            previewPanel = new QuaquaColorPreviewPanel();
            chooser.setPreviewPanel(previewPanel);
        }
        previewPanel.setForeground(chooser.getColor());
        mainPanel.setPreviewPanel(previewPanel);
    }
    
    class PreviewListener implements ChangeListener {
        public void stateChanged( ChangeEvent e ) {
            ColorSelectionModel model = (ColorSelectionModel)e.getSource();
            if (previewPanel != null) {
                previewPanel.setForeground(model.getSelectedColor());
                previewPanel.repaint();
            }
        }
    }
    protected void uninstallDefaultChoosers() {
        for( int i = 0 ; i < defaultChoosers.length; i++) {
            chooser.removeChooserPanel( defaultChoosers[i] );
        }
    }
    private void updateColorChooserPanels(
            AbstractColorChooserPanel[] oldPanels,  
            AbstractColorChooserPanel[] newPanels) {
        for (int i = 0; i < oldPanels.length; i++) {  // remove old panels
            Container wrapper = oldPanels[i].getParent();
            if (wrapper != null) {
                Container parent = wrapper.getParent();
                if (parent != null)
                    parent.remove(wrapper);  // remove from hierarchy
                oldPanels[i].uninstallChooserPanel(chooser); // uninstall
            }
        }
        
        mainPanel.removeAllColorChooserPanels();
        for (int i = 0; i < newPanels.length; i++) {
            if (newPanels[i] != null) {
                mainPanel.addColorChooserPanel(newPanels[i]);
            }
        }
        
        for (int i = 0; i < newPanels.length; i++) {
            if (newPanels[i] != null) {
                newPanels[i].installChooserPanel(chooser);
            }
        }
    }
    
    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    public class PropertyHandler implements PropertyChangeListener {
        
        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();
            if (name.equals( JColorChooser.CHOOSER_PANELS_PROPERTY)) {
                updateColorChooserPanels(
                        (AbstractColorChooserPanel[]) e.getOldValue(),
                        (AbstractColorChooserPanel[]) e.getNewValue()
                        );
            }
            
            if (name.equals( JColorChooser.PREVIEW_PANEL_PROPERTY)) {
                if (e.getNewValue() != previewPanel) {
                    installPreviewPanel();
                }
            }
        }
    }
}
