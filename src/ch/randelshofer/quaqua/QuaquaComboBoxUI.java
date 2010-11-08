/*
 * @(#)QuaquaComboBoxUI.java 
 *
 * Copyright (c) 2004-2010 Werner Randelshofer
 * Hausmatt 10, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.util.*;
import ch.randelshofer.quaqua.util.Debug;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.beans.*;

/**
 * Quaqua UI for JComboBox.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class QuaquaComboBoxUI extends BasicComboBoxUI implements VisuallyLayoutable {
    //private HierarchyListener hierarchyListener;
    //MetalComboBoxUI
    // Control the selection behavior of the JComboBox when it is used
    // in the JTable DefaultCellEditor.

    private boolean isTableCellEditor = false;
    public static final String IS_TABLE_CELL_EDITOR = "JComboBox.isTableCellEditor";
    private final static Border tableCellEditorBorder = new EmptyBorder(0, 2, 0, 0);
    static final StringBuffer HIDE_POPUP_KEY = new StringBuffer("HidePopupKey");

    /**
     * Preferred spacing between combo boxes and other components.
     * /
     * private final static Insets regularSpacing = new Insets(12,12,12,12);
     * private final static Insets smallSpacing = new Insets(10,10,10,10);
     * private final static Insets miniSpacing = new Insets(8,8,8,8);
     */
    public static ComponentUI createUI(JComponent c) {
        return new QuaquaComboBoxUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);

        // Is this combo box a cell editor?
        Boolean value = (Boolean) c.getClientProperty(IS_TABLE_CELL_EDITOR);
        if (value == null) {
            value = (Boolean) c.getClientProperty("JComboBox.lightweightKeyboardNavigation");
        }
        setTableCellEditor(value != null && value.equals(Boolean.TRUE));

        // Note: we need to invoke c.setOpaque explicitly, installProperty does
        //       not seem to work.
        //LookAndFeel.installProperty(c, "opaque", UIManager.get("ComboBox.opaque"));
        c.setOpaque(UIManager.getBoolean("ComboBox.opaque"));

        comboBox.setRequestFocusEnabled(UIManager.getBoolean("ComboBox.requestFocusEnabled"));

        // We can't set this property because it breaks the behavior of editable
        // combo boxes.
        comboBox.setFocusable(comboBox.isEditable() || UIManager.getBoolean("ComboBox.focusable"));
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        comboBox.setMaximumRowCount(UIManager.getInt("ComboBox.maximumRowCount"));
    }

    /**
     * Create and install the listeners for the combo box and its model.
     * This method is called when the UI is installed.
     */
    @Override
    protected void installListeners() {
        if ((itemListener = createItemListener()) != null) {
            comboBox.addItemListener(itemListener);
        }
        if ((propertyChangeListener = createPropertyChangeListener()) != null) {
            comboBox.addPropertyChangeListener(propertyChangeListener);
        }
        if ((keyListener = createKeyListener()) != null) {
            comboBox.addKeyListener(keyListener);
        }
        if ((focusListener = createFocusListener()) != null) {
            comboBox.addFocusListener(focusListener);
        }
        if ((popupMouseListener = popup.getMouseListener()) != null) {
            comboBox.addMouseListener(popupMouseListener);
        }
        if ((popupMouseMotionListener = popup.getMouseMotionListener()) != null) {
            comboBox.addMouseMotionListener(popupMouseMotionListener);
        }
        if ((popupKeyListener = popup.getKeyListener()) != null) {
            comboBox.addKeyListener(popupKeyListener);
        }
        /*
        if ((hierarchyListener = createHierarchyListener()) != null) {
        comboBox.addHierarchyListener(hierarchyListener);
        }
         */
        if (comboBox.getModel() != null) {
            if ((listDataListener = createListDataListener()) != null) {
                comboBox.getModel().addListDataListener(listDataListener);
            }
        }
    }

    /**
     * Remove the installed listeners from the combo box and its model.
     * The number and types of listeners removed and in this method should be
     * the same that was added in <code>installListeners</code>
     */
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        /*
        if (hierarchyListener != null) {
        comboBox.removeHierarchyListener(hierarchyListener);
        hierarchyListener = null;
        }*/
    }

    public KeyListener getKeyListener() {
        return keyListener;
    }
    /*
    protected HierarchyListener createHierarchyListener() {
    return new ComponentActivationHandler(comboBox);
    }*/

    boolean isTableCellEditor() {
        return isTableCellEditor;
    }

    @Override
    protected ComboBoxEditor createEditor() {
        return new QuaquaComboBoxEditor.UIResource();
    }

    @Override
    protected ComboPopup createPopup() {
        QuaquaComboPopup p = new QuaquaComboPopup(comboBox, this);
        p.getAccessibleContext().setAccessibleParent(comboBox);
        return p;
    }

    @Override
    protected JButton createArrowButton() {
        JButton button = new QuaquaComboBoxButton(this, comboBox, getArrowIcon(),
                comboBox.isEditable(),
                currentValuePane,
                listBox);
        button.setMargin(new Insets(0, 1, 1, 3));
        return button;
    }

    @Override
    public PropertyChangeListener createPropertyChangeListener() {
        return new QuaquaPropertyChangeListener();
    }

    private void setTableCellEditor(boolean b) {
        isTableCellEditor = b;
        updateTableCellEditor();
    }

    private void updateTableCellEditor() {
        boolean b = isTableCellEditor();
        //comboBox.setOpaque(b);
        if (editor instanceof JComponent) {
            JComponent jeditor = (JComponent) editor;
            jeditor.setBorder(b ? tableCellEditorBorder : UIManager.getBorder("TextField.border"));
        }
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        if (editor != null
                && UIManager.getBoolean("ComboBox.changeEditorForeground")) {
            editor.setForeground(c.getForeground());
        }
        Debug.paint(g, c, this);
    }

    @Override
    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
    }

    /**
     * Paints the background of the currently selected item.
     */
    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
     */
    public class QuaquaPropertyChangeListener extends BasicComboBoxUI.PropertyChangeHandler {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            super.propertyChange(e);
            String name = e.getPropertyName();

            if (name.equals("editable")) {
                QuaquaComboBoxButton button = (QuaquaComboBoxButton) arrowButton;
                button.setIconOnly(comboBox.isEditable());
                updateTableCellEditor();

                // FIXME - This may cause mayhem!
                comboBox.setFocusable(comboBox.isEditable() || UIManager.getBoolean("ComboBox.focusable"));

                comboBox.repaint();
            } else if (name.equals("background")) {
                Color color = (Color) e.getNewValue();
                arrowButton.setBackground(color);
            } else if (name.equals("foreground")) {
                Color color = (Color) e.getNewValue();
                arrowButton.setForeground(color);
                listBox.setForeground(color);
            } else if (name.equals(IS_TABLE_CELL_EDITOR)) {
                Boolean inTable = (Boolean) e.getNewValue();
                setTableCellEditor(inTable.equals(Boolean.TRUE) ? true : false);
            } else if (name.equals("JComboBox.lightweightKeyboardNavigation")) {
                // In Java 1.3 we have to use this property to guess whether we
                // are a table cell editor or not.
                setTableCellEditor(e.getNewValue() != null && e.getNewValue().equals("Lightweight"));
            } else if (name.equals("JComponent.sizeVariant")) {
                QuaquaUtilities.applySizeVariant(comboBox);
            }
        }
    }

    /**
     * As of Java 2 platform v1.4 this method is no longer used. Do not call or
     * override. All the functionality of this method is in the
     * QuaquaPropertyChangeListener.
     *
     * @deprecated As of Java 2 platform v1.4.
     */
    protected void editablePropertyChanged(PropertyChangeEvent e) {
    }

    @Override
    protected LayoutManager createLayoutManager() {
        return new QuaquaComboBoxLayoutManager();
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
     */
    public class QuaquaComboBoxLayoutManager extends BasicComboBoxUI.ComboBoxLayoutManager {

        @Override
        public void layoutContainer(Container parent) {
            layoutComboBox(parent, this);
        }

        public void superLayout(Container parent) {
            JComboBox cb = (JComboBox) parent;
            int width = cb.getWidth();
            int height = cb.getHeight();

            Insets insets = getInsets();
            int buttonSize = height - (insets.top + insets.bottom);

            Rectangle cvb;
            if (arrowButton != null) {
                if (QuaquaUtilities.isLeftToRight(cb)) {
                    // FIXME - This should be 6 minus 2, whereas two needs to be
                    // derived from the TextFieldUI
                    //int plusHeight = (isSmallSizeVariant()) ? 4 : 4;
                    int plusHeight = (isSmall()) ? - 2 : - 2;
                    arrowButton.setBounds(
                            width - getArrowWidth() - insets.right,
                            insets.top /*+ margin.top - 3*/,
                            getArrowWidth(),
                            buttonSize /*- margin.top - margin.bottom*/ + plusHeight);
                } else {
                    arrowButton.setBounds(insets.left, insets.top,
                            getArrowWidth(), buttonSize);
                }
            }
            if (editor != null) {
                cvb = rectangleForCurrentValue();
                editor.setBounds(cvb);
            }
        }
    }

    // This is here because of a bug in the compiler.
    // When a protected-inner-class-savvy compiler comes out we
    // should move this into QuaquaComboBoxLayoutManager.
    public void layoutComboBox(Container parent, QuaquaComboBoxLayoutManager manager) {
        if (comboBox.isEditable()) {
            manager.superLayout(parent);
        } else {
            if (arrowButton != null) {
                Insets insets = comboBox.getInsets();
                int width = comboBox.getWidth();
                int height = comboBox.getHeight();
                arrowButton.setBounds(insets.left, insets.top,
                        width - (insets.left + insets.right),
                        height - (insets.top + insets.bottom));
            }
        }
    }

    protected Icon getArrowIcon() {
        if (isTableCellEditor()) {
            return UIManager.getIcon("ComboBox.smallPopupIcon");
            /* The following does not work as expected:
            if (comboBox.isEditable()) {
            return UIManager.getIcon("ComboBox.smallDropDownIcon");
            } else {
            return UIManager.getIcon("ComboBox.smallPopupIcon");
            }*/
        } else {
            if (comboBox.isEditable()) {
                if (isSmall()) {
                    return UIManager.getIcon("ComboBox.smallDropDownIcon");
                } else {
                    return UIManager.getIcon("ComboBox.dropDownIcon");
                }
            } else {
                if (isSmall()) {
                    return UIManager.getIcon("ComboBox.smallPopupIcon");
                } else {
                    return UIManager.getIcon("ComboBox.popupIcon");
                }
            }
        }
    }

    protected int getArrowWidth() {
        if (isTableCellEditor()) {
            return 7;
        } else {
            if (comboBox.isEditable()) {
                if (isSmall()) {
                    return 17;
                } else {
                    return 19;
                }
            } else {
                if (isSmall()) {
                    return 17 + 3;
                } else {
                    return 19 + 4;
                }
            }
        }
    }

    /**
     * As of Java 2 platform v1.4 this method is no
     * longer used.
     *
     * @deprecated As of Java 2 platform v1.4.
     */
    protected void removeListeners() {
        if (propertyChangeListener != null) {
            comboBox.removePropertyChangeListener(propertyChangeListener);
        }
    }

    protected boolean isSmall() {
        return QuaquaUtilities.isSmallSizeVariant(comboBox);
    }

    /**
     * Returns the area that is reserved for drawing the currently selected item.
     * Note: Changes in this method also require changes in method getMinimumSize.
     */
    @Override
    protected Rectangle rectangleForCurrentValue() {
        return rectangleForCurrentValue(comboBox.getWidth(), comboBox.getHeight());
    }

    /**
     * Returns the area that is reserved for drawing the currently selected item.
     * Note: Changes in this method also require changes in method getMinimumSize.
     */
    protected Rectangle rectangleForCurrentValue(int width, int height) {
        Insets insets = getInsets();
        Insets margin = getMargin();
        if (comboBox.isEditable()) {
            if (!isTableCellEditor()) {
                insets.right -= margin.right;
                /*
                insets.left--;
                insets.top--;
                insets.bottom--;*/
                insets.left -= margin.left - 2;
                insets.top -= margin.top - 2;
                insets.bottom -= margin.bottom - 2;
            }
        } else {

            if (isTableCellEditor()) {
                insets.top -= 1;
            } else {

                //insets.right += margin.right; no right-margin because we
                // want no gap between button and renderer!
                insets.left += 6;
                insets.top += margin.top;
                insets.left += margin.left;
                insets.bottom += margin.bottom;
            }


        }
        return new Rectangle(
                insets.left,
                insets.top,
                width - getArrowWidth() - insets.right - insets.left,
                height - insets.top - insets.bottom);
    }

    protected Insets getMargin() {
        Insets margin = (Insets) comboBox.getClientProperty("Quaqua.Component.visualMargin");
        if (margin == null) {
            margin = UIManager.getInsets("Component.visualMargin");
        }
        return (margin==null)?new Insets(0,0,0,0):(Insets) margin.clone();
    }

    /**
     * Note: Changes in this method also require changes in method rectangelForCurrentValue.
     */
    @Override
    public Dimension getMinimumSize(JComponent c) {
        if (!isMinimumSizeDirty) {
            return new Dimension(cachedMinimumSize);
        }

        Dimension size = null;
        if (!comboBox.isEditable()
                && arrowButton != null
                && arrowButton instanceof QuaquaComboBoxButton) {

            Insets buttonInsets = new Insets(4, 11, 3, getArrowWidth() + 5);
            if (isSmall()) {
                buttonInsets.bottom -= 1;
            }
            Insets insets = getInsets();
            size = getDisplaySize();
            size.width += insets.left + insets.right
                    + buttonInsets.left + buttonInsets.right;
            size.height += insets.top + insets.bottom
                    + buttonInsets.top + buttonInsets.bottom;

        } else if (comboBox.isEditable()
                && arrowButton != null
                && editor != null) {
            Insets buttonInsets;
            Insets insets = comboBox.getInsets();
            Insets margin = getMargin();
            buttonInsets = new Insets(2 - margin.top, 4 - margin.left, 2 - margin.bottom, getArrowWidth());

            // Margin is included in display size, therefore no need to add
            // it to size. We subtract the margin at the right, because we
            // want the text field's focus ring to glow over the right button.
            size = getDisplaySize();
            size.width += insets.left + insets.right
                    + buttonInsets.left + buttonInsets.right;
            size.height += insets.top + insets.bottom
                    + buttonInsets.top + buttonInsets.bottom;
        } else {
            size = super.getMinimumSize(c);
            if (size == null) {
                size = new Dimension(0, 0);
            }
        }

        cachedMinimumSize.setSize(size.width, size.height);
        isMinimumSizeDirty = false;
        return new Dimension(cachedMinimumSize);
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        Dimension size = getPreferredSize(c);
        if (size!=null && !(c.getParent() instanceof JToolBar)) {
            size.width = Short.MAX_VALUE;
        }
        return size;
    }

    /**
     * Creates a <code>FocusListener</code> which will be added to the combo box.
     * If this method returns null then it will not be added to the combo box.
     *
     * @return an instance of a <code>FocusListener</code> or null
     */
    @Override
    protected FocusListener createFocusListener() {
        return new GlowFocusHandler();
    }

    @Override
    public int getBaseline(JComponent c, int width, int height) {
        Rectangle vb = getVisualBounds(c, VisuallyLayoutable.TEXT_BOUNDS, width, height);
        return (vb == null) ? -1 : vb.y + vb.height;
    }

    public Rectangle getVisualBounds(JComponent c, int layoutType, int width, int height) {
        Rectangle bounds = new Rectangle(0, 0, width, height);
        if (layoutType == VisuallyLayoutable.CLIP_BOUNDS) {
            return bounds;
        }

        JComboBox cb = (JComboBox) c;

        Rectangle buttonRect = new Rectangle();
        Rectangle editorRect = null;

        Insets insets = getInsets();
        Insets margin = getMargin();
        int buttonSize = height - (insets.top + insets.bottom);
        Rectangle cvb;
        if (arrowButton != null) {
            if (QuaquaUtilities.isLeftToRight(cb)) {
                int plusHeight = (isSmall()) ? 5 : 4;
                buttonRect.setBounds(
                        width - getArrowWidth() - insets.right,
                        insets.top + margin.top - 2,
                        getArrowWidth(),
                        buttonSize - margin.top - margin.bottom + plusHeight);
            } else {
                buttonRect.setBounds(insets.left, insets.top,
                        getArrowWidth(), buttonSize);
            }
        }
        editorRect = rectangleForCurrentValue(width, height);

        // FIXME we shouldn't hardcode this and determine the real visual
        // bounds of the renderer instead.
        // Subtract 2 from x because of the insets of the renderer
        editorRect.x += 1;
        editorRect.width -= 2;

        switch (layoutType) {
            case VisuallyLayoutable.COMPONENT_BOUNDS:
                if (!isTableCellEditor()) {
                    if (editor != null) {
                        bounds.x += margin.left;
                        bounds.y += margin.top;
                        bounds.width -= margin.left + margin.right;
                        bounds.height -= margin.top + margin.bottom + 1;
                    } else {
                        bounds.x += margin.left;
                        bounds.y += margin.top;
                        bounds.width -= margin.left + margin.right;
                        bounds.height -= margin.top + margin.bottom;
                    }
                }
                break;
            case VisuallyLayoutable.TEXT_BOUNDS:
                Object renderer = (editor == null)
                        ? (Object) cb.getRenderer().getListCellRendererComponent(listBox, cb.getSelectedItem(), cb.getSelectedIndex(), false, cb.hasFocus())
                        : (Object) editor;
                if ((renderer instanceof JComponent)
                        && (Methods.invokeGetter(renderer, "getUI", null) instanceof VisuallyLayoutable)) {
                    bounds = ((VisuallyLayoutable) Methods.invokeGetter(renderer, "getUI", null)).getVisualBounds((JComponent) renderer, layoutType, editorRect.width, editorRect.height);
                    bounds.x += editorRect.x;
                    bounds.y += editorRect.y;
                } else {
                    bounds.setBounds(editorRect);
                }
                break;
        }
        return bounds;
    }

    /**
     * This listener hides the popup when the focus is lost.  It also repaints
     * when focus is gained or lost.
     *
     * This public inner class should be treated as protected.
     * Instantiate it only within subclasses of
     * <code>BasicComboBoxUI</code>.
     */
    public class GlowFocusHandler extends BasicComboBoxUI.FocusHandler {

        @Override
        public void focusGained(FocusEvent e) {
            super.focusGained(e);
            glowyRepaint();
        }

        @Override
        public void focusLost(FocusEvent e) {
            super.focusLost(e);
            glowyRepaint();
        }

        private void glowyRepaint() {
            if (comboBox.getParent() != null) {
                Rectangle r = comboBox.getBounds();
                r.grow(2, 2);
                comboBox.getParent().repaint(r.x, r.y, r.width, r.height);
            }
        }
    }
}


