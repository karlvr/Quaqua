/*
 * @(#)QuaquaPasswordFieldUI.java  1.6.1  2009-02-16
 *
 * Copyright (c) 2005-2009 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.util.*;
import ch.randelshofer.quaqua.border.BackgroundBorder;
import ch.randelshofer.quaqua.util.Debug;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.*;
import javax.swing.border.*;
/**
 * QuaquaPasswordFieldUI.
 *
 * @author  Werner Randelshofer
 * @version 1.6.1 2009-02-16 Override method paintBackground to make it do
 * nothing.
 * <br>1.6 2007-08-06 Select all text when the user tabs into the field.
 * <br>1.5 2007-07-27 Added 2 pixels to the preferred width.
 * <br>1.4 2006-04-24 Added support for .popupHandler UIManager property.
 * <br>1.3 2005-10-01 Tweaked due to changes in QuaquaTreeUI. Non-opaque
 * background painting delegated to QuaquaTextFieldBorder.
 * <br>1.2 2005-07-17 Adapted to changes in interface VisuallyLayoutable.
 * <br>1.1 2005-02-27 Support for margin added.
 * <br>1.0  06 March 2005  Created.
 */
public class QuaquaPasswordFieldUI extends BasicPasswordFieldUI implements VisuallyLayoutable {
    private FocusListener focusListener;
    //private HierarchyListener hierarchyListener;
    private MouseListener popupListener;
    
    /**
     * Creates a UI for a JPasswordField.
     *
     * @param c the JPasswordField
     * @return the UI
     */
    public static ComponentUI createUI(JComponent c) {
        return new QuaquaPasswordFieldUI();
    }
    
    public void installUI(JComponent c) {
        super.installUI(c);
	QuaquaUtilities.installProperty(c, "opaque", UIManager.get(getPropertyPrefix()+".opaque"));
        //c.setOpaque(QuaquaManager.getBoolean(getPropertyPrefix()+".opaque"));
    }
    
    protected void installDefaults() {
        super.installDefaults();
        /* Unfortunately we can not set the echo char from the UI. :(
        JPasswordField t = (JPasswordField) getComponent();
        t.setEchoChar('\u2022');
         */
    }
    
    protected void uninstallDefaults() {
        super.uninstallDefaults();
    }
    
    protected void installListeners() {
        focusListener = createFocusListener();
        if (focusListener != null) {
            getComponent().addFocusListener(focusListener);
        }
        popupListener = createPopupListener();
        if (popupListener != null) {
            getComponent().addMouseListener(popupListener);
        }
        super.installListeners();
    }
    
    protected void uninstallListeners() {
        if (focusListener != null) {
            getComponent().removeFocusListener(focusListener);
            focusListener = null;
        }
        if (popupListener != null) {
            getComponent().removeMouseListener(popupListener);
            popupListener = null;
        }
        super.uninstallListeners();
    }
    
    protected FocusListener createFocusListener() {
        try {
            return (FocusListener) Methods.invokeStatic("ch.randelshofer.quaqua.Quaqua14TextFieldFocusHandler","getInstance");
        } catch (NoSuchMethodException ex) {
            return QuaquaFocusHandler.getInstance();
        }
    }
    protected MouseListener createPopupListener() {
        return (MouseListener) UIManager.get(getPropertyPrefix()+".popupHandler");
    }
    /**
     * Fetches the EditorKit for the UI.
     *
     * @param tc the text component for which this UI is installed
     * @return the editor capabilities
     * @see TextUI#getEditorKit
     */
    public EditorKit getEditorKit(JTextComponent tc) {
        return QuaquaTextFieldUI.defaultKit;
    }
    
    public Insets getVisualMargin(JTextComponent tc) {
        Insets margin = (Insets) tc.getClientProperty("Quaqua.Component.visualMargin");
        if (margin == null) {
            margin = UIManager.getInsets("Component.visualMargin");
        }
        return (margin == null) ? new Insets(0, 0, 0 ,0) : (Insets) margin.clone();
    }
    
    protected void paintSafely(Graphics g) {
        Object oldHints = QuaquaUtilities.beginGraphics((Graphics2D) g);
        JTextComponent editor = getComponent();
        
        // Fill the background
        Border border = editor.getBorder();
        if (border != null && border instanceof BackgroundBorder) {
            Border bb = ((BackgroundBorder) border).getBackgroundBorder();
            bb.paintBorder(editor, g, 0, 0, editor.getWidth(), editor.getHeight());
             
        }
        
        super.paintSafely(g);
        QuaquaUtilities.endGraphics((Graphics2D) g, oldHints);
        Debug.paint(g, editor, this);
    }
    /**
     * Paints a background for the view.  This will only be
     * called if isOpaque() on the associated component is
     * true.  The default is to paint the background color
     * of the component.
     *
     * @param g the graphics context
     */
    protected void paintBackground(Graphics g) {
        // This method is overriden here, to make it do nothing.
        // We already paint the background in method paintSafely();
    }
    
    public void propertyChange(PropertyChangeEvent event) {
        String name = event.getPropertyName();
        if (name.equals("Frame.active")) {
            QuaquaUtilities.repaintBorder(getComponent());
       } else if (name.equals("JComponent.sizeVariant")) {
            QuaquaUtilities.applySizeVariant(getComponent());
       } else {
            super.propertyChange(event);
            }
    }
    
    protected Caret createCaret() {
        Window window = SwingUtilities.getWindowAncestor(getComponent());
        QuaquaCaret caret = new QuaquaCaret(window, getComponent());
        return caret;
    }
    
    protected Highlighter createHighlighter() {
        return new QuaquaHighlighter();
    }
    /**
     * Creates a view (PasswordView) for an element.
     *
     * @param elem the element
     * @return the view
     */
    public View create(Element elem) {
        return new QuaquaPasswordView(elem);
    }
    /**
     * Creates the keymap to use for the text component, and installs
     * any necessary bindings into it.  By default, the keymap is
     * shared between all instances of this type of TextUI. The
     * keymap has the name defined by the getKeymapName method.  If the
     * keymap is not found, then DEFAULT_KEYMAP from JTextComponent is used.
     * <p>
     * The set of bindings used to create the keymap is fetched
     * from the UIManager using a key formed by combining the
     * {@link #getPropertyPrefix} method
     * and the name <code>.keyBindings</code>.  The type is expected
     * to be <code>JTextComponent.KeyBinding[]</code>.
     *
     * @return the keymap
     * @see #getKeymapName
     * @see javax.swing.text.JTextComponent
     */
    protected Keymap createKeymap() {
        String nm = getKeymapName();
        Keymap map = JTextComponent.getKeymap(nm);
        if (map == null) {
            Keymap parent = JTextComponent.getKeymap(JTextComponent.DEFAULT_KEYMAP);
            map = JTextComponent.addKeymap(nm, parent);
            String prefix = getPropertyPrefix();
            Object o = UIManager.get(prefix + ".keyBindings");
            if ((o != null) && (o instanceof JTextComponent.KeyBinding[])) {
                JTextComponent.KeyBinding[] bindings = (JTextComponent.KeyBinding[]) o;
                JTextComponent.loadKeymap(map, bindings, getComponent().getActions());
            }
        }
        return map;
    }
    public int getBaseline(JComponent c, int width, int height) {
        JTextComponent textComponent = (JTextComponent) c;
        View rootView = textComponent.getUI().getRootView(textComponent);
        if (rootView.getViewCount() > 0) {
            Insets insets = textComponent.getInsets();
            int h = height - insets.top - insets.bottom;
            int y = insets.top;
            View fieldView = rootView.getView(0);
            int vspan = (int)fieldView.getPreferredSpan(View.Y_AXIS);
            if (height != vspan) {
                int slop = h - vspan;
                y += slop / 2;
            }
            FontMetrics fm = textComponent.getFontMetrics(
            textComponent.getFont());
            y += fm.getAscent();
            return y;
        }
        return -1;
    }
    public Rectangle getVisualBounds(JComponent c, int type, int width, int height) {
        Rectangle bounds = new Rectangle(0,0,width,height);
        if (type == VisuallyLayoutable.CLIP_BOUNDS) {
            return bounds;
        }
        
        JTextComponent b = (JTextComponent) c;
        if (type == VisuallyLayoutable.COMPONENT_BOUNDS
        && b.getBorder() != null) {
            Border border = b.getBorder();
            if (border instanceof UIResource) {
                InsetsUtil.subtractInto(getVisualMargin(b), bounds);
                // FIXME - Should derive this value from border
                // FIXME - If we had layout managers that supported baseline alignment,
                //              we wouldn't have to subtract one here
                bounds.height -= 1;
            }
        } else {
            bounds = getVisibleEditorRect();
            FontMetrics fm = c.getFontMetrics(c.getFont());
            
            int baseline = getBaseline(b, width, height);
            Rectangle textBounds = Fonts.getPerceivedBounds(b.getText(), b.getFont(), c);
            if (bounds == null) {
                bounds = textBounds;
                bounds.y += baseline;
            } else {
                bounds.y = baseline + textBounds.y;
                bounds.height = textBounds.height;
            }
            bounds.x += 1;
            bounds.width -= 2;
        }
        return bounds;
    }
    public Dimension getPreferredSize(JComponent c) {
        // The following code has been derived from BasicTextUI.
	Document doc = ((JTextComponent) c).getDocument();
	Insets i = c.getInsets();
	Dimension d = c.getSize();
        View rootView = getRootView((JTextComponent) c);

	if (doc instanceof AbstractDocument) {
	    ((AbstractDocument)doc).readLock();
	}
	try {
	    if ((d.width > (i.left + i.right)) && (d.height > (i.top + i.bottom))) {
		rootView.setSize(d.width - i.left - i.right, d.height - i.top - i.bottom);
	    }
            else if (d.width == 0 && d.height == 0) {
                // Probably haven't been layed out yet, force some sort of
                // initial sizing.
                rootView.setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
            }
	    d.width = (int) Math.min((long) rootView.getPreferredSpan(View.X_AXIS) +
				     (long) i.left + (long) i.right, Integer.MAX_VALUE);
	    d.height = (int) Math.min((long) rootView.getPreferredSpan(View.Y_AXIS) +
				      (long) i.top + (long) i.bottom, Integer.MAX_VALUE);
	} finally {
	    if (doc instanceof AbstractDocument) {
		((AbstractDocument)doc).readUnlock();
	    }
	}
        
        // Fix: The preferred width is always two pixels too small
        // on a Mac. 
        d.width += 2;
        
	return d;
    }
}
