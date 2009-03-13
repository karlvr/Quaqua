/*
 * @(#)Quaqua14PopupMenuUI.java  2.0.1  2007-02-15
 *
 * Copyright (c) 2004-2007 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.util.Methods;
import ch.randelshofer.quaqua.color.PaintableColor;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
/**
 * Quaqua14PopupMenuUI.
 *
 * @author Werner Randelshofer
 * @version 2.0.1 2007-02-15 Mouse release was not forwarded to components.
 * <br>2.0 2007-02-08 Mouse release on a JPopupMenu must trigger the
 * current menu item.
 * <br>1.0  13 February 2005  Created.
 */
public class Quaqua14PopupMenuUI extends BasicPopupMenuUI implements QuaquaMenuPainterClient {
    private static boolean checkedUnpostPopup;
    private static boolean unpostPopup;
    private transient PopupMenuListener popupMenuListener = null;
    private MenuKeyListener menuKeyListener = null;
    static boolean menuKeyboardHelperInstalled = false;
    static MenuKeyboardHelper menuKeyboardHelper = null;
    
    public static ComponentUI createUI(JComponent x) {
        return new Quaqua14PopupMenuUI();
    }
    /** Creates a new instance. */
    public Quaqua14PopupMenuUI() {
    }
    public void installDefaults() {
        super.installDefaults();
    }
    
    protected void installListeners() {
        if (popupMenuListener == null) {
            popupMenuListener = new QuaquaPopupMenuListener();
        }
        popupMenu.addPopupMenuListener(popupMenuListener);
        
        if (menuKeyListener == null) {
            menuKeyListener = new QuaquaMenuKeyListener();
        }
            Methods.invokeIfExists(popupMenu, "addMenuKeyListener", MenuKeyListener.class, menuKeyListener);
            /*
        try {
            Methods.invoke(popupMenu, "addMenuKeyListener", MenuKeyListener.class, menuKeyListener);
        } catch (NoSuchMethodException ex) {
            popupMenu.addMenuKeyListener(menuKeyListener);
        }
        */
        if (mouseGrabber == null) {
            mouseGrabber = new MouseGrabber();
        }
        
        if (!menuKeyboardHelperInstalled) {
            if (menuKeyboardHelper == null) {
                menuKeyboardHelper = new MenuKeyboardHelper();
            }
            MenuSelectionManager msm = MenuSelectionManager.defaultManager();
            msm.addChangeListener(menuKeyboardHelper);
            menuKeyboardHelperInstalled = true;
        }
    }
    
    protected void uninstallListeners() {
        if (popupMenuListener != null) {
            popupMenu.removePopupMenuListener(popupMenuListener);
        }
        if (menuKeyListener != null) {
            Methods.invokeIfExists(popupMenu, "removeMenuKeyListener", MenuKeyListener.class, menuKeyListener);
            //popupMenu.removeMenuKeyListener(menuKeyListener);
        }
        if(mouseGrabber != null) {
            MenuSelectionManager msm = MenuSelectionManager.defaultManager();
            msm.removeChangeListener(mouseGrabber);
            mouseGrabber.ungrabWindow();
            mouseGrabber = null;
        }
    }
    
    public void paintBackground(Graphics g, JComponent component, int menuWidth, int menuHeight) {
        Color bgColor = UIManager.getColor("PopupMenu.selectionBackground");
        AbstractButton menuItem = (AbstractButton) component;
        ButtonModel model = menuItem.getModel();
        Color oldColor = g.getColor();
        
        if(menuItem.isOpaque()) {
            if (model.isArmed() || model.isSelected()) {
                ((Graphics2D) g).setPaint(PaintableColor.getPaint(bgColor, component));
                g.fillRect(0,0, menuWidth, menuHeight);
            } else {
                ((Graphics2D) g).setPaint(PaintableColor.getPaint(menuItem.getBackground(), component));
                g.fillRect(0,0, menuWidth, menuHeight);
            }
            g.setColor(oldColor);
        }
    }
    static JPopupMenu getLastPopup() {
        MenuSelectionManager msm = MenuSelectionManager.defaultManager();
        MenuElement[] p = msm.getSelectedPath();
        JPopupMenu popup = null;
        
        for(int i = p.length - 1; popup == null && i >= 0; i--) {
            if (p[i] instanceof JPopupMenu)
                popup = (JPopupMenu)p[i];
        }
        return popup;
    }
    private static MenuElement nextEnabledChild(MenuElement e[],
            int fromIndex, int toIndex) {
        for (int i=fromIndex; i<=toIndex; i++) {
            if (e[i] != null) {
                Component comp = e[i].getComponent();
                if (comp != null && comp.isEnabled() && comp.isVisible()) {
                    return e[i];
                }
            }
        }
        return null;
    }
    
    private static MenuElement previousEnabledChild(MenuElement e[],
            int fromIndex, int toIndex) {
        for (int i=fromIndex; i>=toIndex; i--) {
            if (e[i] != null) {
                Component comp = e[i].getComponent();
                if (comp != null && comp.isEnabled() && comp.isVisible()) {
                    return e[i];
                }
            }
        }
        return null;
    }
    static MenuElement findEnabledChild(MenuElement e[], int fromIndex,
            boolean forward) {
        MenuElement result = null;
        if (forward) {
            result = nextEnabledChild(e, fromIndex+1, e.length-1);
            if (result == null) result = nextEnabledChild(e, 0, fromIndex-1);
        } else {
            result = previousEnabledChild(e, fromIndex-1, 0);
            if (result == null) result = previousEnabledChild(e, e.length-1,
                    fromIndex+1);
        }
        return result;
    }
    static MenuElement getFirstPopup() {
        MenuSelectionManager msm = MenuSelectionManager.defaultManager();
        MenuElement[] p = msm.getSelectedPath();
        MenuElement me = null;
        
        for(int i = 0 ; me == null && i < p.length ; i++) {
            if (p[i] instanceof JPopupMenu)
                me = p[i];
        }
        
        return me;
    }
    
    private static boolean doUnpostPopupOnDeactivation() {
        if (!checkedUnpostPopup) {
            Boolean b = (Boolean) java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction() {
                public Object run() {
                    String pKey =
                            "sun.swing.unpostPopupsOnWindowDeactivation";
                    String value = System.getProperty(pKey, "true");
                    return Boolean.valueOf(value);
                }
            }
            );
            unpostPopup = b.booleanValue();
            checkedUnpostPopup = true;
        }
        return unpostPopup;
    }
    
    /**
     * Returns the <code>Popup</code> that will be responsible for
     * displaying the <code>JPopupMenu</code>.
     *
     * @param popup JPopupMenu requesting Popup
     * @param x     Screen x location Popup is to be shown at
     * @param y     Screen y location Popup is to be shown at.
     * @return Popup that will show the JPopupMenu
     * @since 1.4
     */
    public Popup getPopup(JPopupMenu popup, int x, int y) {
        if (! QuaquaManager.getBoolean("PopupMenu.enableHeavyWeightPopup")) {
        return new QuaquaPopupFactory().getPopup(popup.getInvoker(), popup, x, y);
        } else {
            return super.getPopup(popup, x, y);
        }
    }
    static java.util.List getPopups() {
        MenuSelectionManager msm = MenuSelectionManager.defaultManager();
        MenuElement[] p = msm.getSelectedPath();
        
        ArrayList list = new ArrayList(p.length);
        for(int i = 0; i < p.length; i++) {
            if (p[i] instanceof JPopupMenu) {
                list.add((JPopupMenu)p[i]);
            }
        }
        return list;
    }
    private transient static MouseGrabber mouseGrabber = null;
    
    private static class MouseGrabber implements ChangeListener,
            AWTEventListener, ComponentListener, WindowListener {
        
        Window grabbedWindow;
        MenuElement[] lastPathSelected;
        
        public MouseGrabber() {
            MenuSelectionManager msm = MenuSelectionManager.defaultManager();
            msm.addChangeListener(this);
            this.lastPathSelected = msm.getSelectedPath();
            if(this.lastPathSelected.length != 0) {
                grabWindow(this.lastPathSelected);
            }
        }
        
        void grabWindow(MenuElement[] newPath) {
            // A grab needs to be added
            java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction() {
                public Object run() {
                    Toolkit.getDefaultToolkit()
                    .addAWTEventListener(MouseGrabber.this,
                            AWTEvent.MOUSE_EVENT_MASK |
                            AWTEvent.MOUSE_MOTION_EVENT_MASK |
                            AWTEvent.MOUSE_WHEEL_EVENT_MASK);
                    return null;
                }
            }
            );
            
            Component invoker = newPath[0].getComponent();
            if (invoker instanceof JPopupMenu) {
                invoker = ((JPopupMenu)invoker).getInvoker();
            }
            grabbedWindow = invoker instanceof Window?
                (Window)invoker :
                SwingUtilities.getWindowAncestor(invoker);
            if(grabbedWindow != null) {
                grabbedWindow.addComponentListener(this);
                grabbedWindow.addWindowListener(this);
            }
        }
        
        void ungrabWindow() {
            // The grab should be removed
            java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction() {
                public Object run() {
                    Toolkit.getDefaultToolkit()
                    .removeAWTEventListener(MouseGrabber.this);
                    return null;
                }
            }
            );
            if(grabbedWindow != null) {
                grabbedWindow.removeComponentListener(this);
                grabbedWindow.removeWindowListener(this);
                grabbedWindow = null;
            }
        }
        
        public void stateChanged(ChangeEvent e) {
            MenuSelectionManager msm = MenuSelectionManager.defaultManager();
            MenuElement[] p = msm.getSelectedPath();
            
            if (lastPathSelected.length == 0 && p.length != 0) {
                grabWindow(p);
            }
            
            if (lastPathSelected.length != 0 && p.length == 0) {
                ungrabWindow();
            }
            
            lastPathSelected = p;
        }
        
        public void eventDispatched(AWTEvent ev) {
            switch (ev.getID()) {
                case MouseEvent.MOUSE_PRESSED:
                    Component src = (Component)ev.getSource();
                    if (isInPopup(src) ||
                            (src instanceof JMenu && ((JMenu)src).isSelected())) {
                        return;
                    }
                    if (!(src instanceof JComponent) ||
                            ! (((JComponent)src).getClientProperty("doNotCancelPopup")
                            == QuaquaComboBoxUI.HIDE_POPUP_KEY)) {
                        // Cancel popup only if this property was not set.
                        // If this property is set to TRUE component wants
                        // to deal with this event by himself.
                        cancelPopupMenu();
                        // Ask UIManager about should we consume event that closes
                        // popup. This made to match native apps behaviour.
                        boolean consumeEvent =
                                UIManager.getBoolean("PopupMenu.consumeEventOnClose");
                        // Consume the event so that normal processing stops.
                        if(consumeEvent && !(src instanceof MenuElement)) {
                            ((MouseEvent)ev).consume();
                        }
                    }
                    break;
                    
                case MouseEvent.MOUSE_RELEASED:
                    src = (Component)ev.getSource();
                    MouseEvent event = (MouseEvent) ev;
                    
                    if(src instanceof JMenu || !(src instanceof JMenuItem)) {
                        // Send a synthetic MouseDragged event in order to outsmart
                        // method JMenuItem.processMenuDragMouseEvent.
                        // Without this event, the JMenuItem will ignore the
                        // MouseReleased Event that we need to send to it.
                        if (event.getClickCount() == 0) {
                            Point p = event.getPoint();
                            MouseEvent dragEvent = new MouseEvent(src, MouseEvent.MOUSE_DRAGGED,
                                    event.getWhen(),
                                    event.getModifiers(), p.x, p.y,
                                    event.getClickCount(),
                                    event.isPopupTrigger());
                            MenuSelectionManager.defaultManager().
                                    processMouseEvent(dragEvent);
                        }
                        
                        // Now send the MouseReleased Event.
                        MenuSelectionManager.defaultManager().
                                processMouseEvent((MouseEvent)ev);
                    }
                    
                    break;
                case MouseEvent.MOUSE_DRAGGED:
                    MenuSelectionManager.defaultManager().
                            processMouseEvent((MouseEvent)ev);
                    break;
                case MouseEvent.MOUSE_WHEEL:
                    if (isInPopup((Component)ev.getSource())) {
                        return;
                    }
                    cancelPopupMenu();
                    break;
            }
        }
        
        boolean isInPopup(Component src) {
            for (Component c=src; c!=null; c=c.getParent()) {
                if (c instanceof Applet || c instanceof Window) {
                    break;
                } else if (c instanceof JPopupMenu) {
                    return true;
                }
            }
            return false;
        }
        
        void cancelPopupMenu() {
            JPopupMenu firstPopup = (JPopupMenu)getFirstPopup();
            // 4234793: This action should call firePopupMenuCanceled but it's
            // a protected method. The real solution could be to make
            // firePopupMenuCanceled public and call it directly.
            java.util.List popups = getPopups();
            Iterator iter = popups.iterator();
            while (iter.hasNext()) {
                JPopupMenu popup = (JPopupMenu)iter.next();
                popup.putClientProperty("JPopupMenu.firePopupMenuCanceled", Boolean.TRUE);
            }
            MenuSelectionManager.defaultManager().clearSelectedPath();
        }
        
        public void componentResized(ComponentEvent e) {
            cancelPopupMenu();
        }
        public void componentMoved(ComponentEvent e) {
            cancelPopupMenu();
        }
        public void componentShown(ComponentEvent e) {
            cancelPopupMenu();
        }
        public void componentHidden(ComponentEvent e) {
            cancelPopupMenu();
        }
        public void windowClosing(WindowEvent e) {
            cancelPopupMenu();
        }
        public void windowClosed(WindowEvent e) {
            cancelPopupMenu();
        }
        public void windowIconified(WindowEvent e) {
            cancelPopupMenu();
        }
        public void windowDeactivated(WindowEvent e) {
            if(doUnpostPopupOnDeactivation()) {
                cancelPopupMenu();
            }
        }
        public void windowOpened(WindowEvent e) {}
        public void windowDeiconified(WindowEvent e) {}
        public void windowActivated(WindowEvent e) {}
    }
    /**
     * This Listener fires the Action that provides the correct auditory
     * feedback.
     *
     * @since 1.4
     */
    private class QuaquaPopupMenuListener implements PopupMenuListener {
        public void popupMenuCanceled(PopupMenuEvent e) {
        }
        
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        }
        
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            /*
            QuaquaLookAndFeel.playSound((JPopupMenu)e.getSource(),
                                       "PopupMenu.popupSound");
             */
        }
    }
    /**
     * Handles mnemonic for children JMenuItems.
     * @since 1.5
     */
    private class QuaquaMenuKeyListener implements MenuKeyListener {
        MenuElement menuToOpen = null;
        
        public void menuKeyTyped(MenuKeyEvent e) {
            if (menuToOpen != null) {
                // we have a submenu to open
                JPopupMenu subpopup = ((JMenu)menuToOpen).getPopupMenu();
                MenuElement subitem = findEnabledChild(
                        subpopup.getSubElements(), -1, true);
                
                ArrayList lst = new ArrayList(Arrays.asList(e.getPath()));
                lst.add(menuToOpen);
                lst.add(subpopup);
                if (subitem != null) {
                    lst.add(subitem);
                }
                MenuElement newPath[] = new MenuElement[0];;
                newPath = (MenuElement[])lst.toArray(newPath);
                MenuSelectionManager.defaultManager().setSelectedPath(newPath);
                e.consume();
            }
            menuToOpen = null;
        }
        
        public void menuKeyPressed(MenuKeyEvent e) {
            // Handle the case for Escape or Enter...
            if (!Character.isLetterOrDigit(e.getKeyChar())) {
                return;
            }
            
            int keyCode = e.getKeyCode();
            MenuSelectionManager manager = e.getMenuSelectionManager();
            MenuElement path[] = e.getPath();
            MenuElement items[] = popupMenu.getSubElements();
            int currentIndex = -1;
            int matches = 0;
            int firstMatch = -1;
            int indexes[] = null;
            
            for (int j = 0; j < items.length; j++) {
                if (! (items[j] instanceof JMenuItem)) {
                    continue;
                }
                JMenuItem item = (JMenuItem)items[j];
                if (item.isEnabled() &&
                        item.isVisible() && keyCode == item.getMnemonic()) {
                    if (matches == 0) {
                        firstMatch = j;
                        matches++;
                    } else {
                        if (indexes == null) {
                            indexes = new int[items.length];
                            indexes[0] = firstMatch;
                        }
                        indexes[matches++] = j;
                    }
                }
                if (item.isArmed()) {
                    currentIndex = matches - 1;
                }
            }
            
            if (matches == 0) {
                ; // no op
            } else if (matches == 1) {
                // Invoke the menu action
                JMenuItem item = (JMenuItem)items[firstMatch];
                if (item instanceof JMenu) {
                    // submenus are handled in menuKeyTyped
                    menuToOpen = item;
                } else if (item.isEnabled()) {
                    // we have a menu item
                    manager.clearSelectedPath();
                    item.doClick();
                }
                e.consume();
            } else {
                // Select the menu item with the matching mnemonic. If
                // the same mnemonic has been invoked then select the next
                // menu item in the cycle.
                MenuElement newItem = null;
                
                newItem = items[indexes[(currentIndex + 1) % matches]];
                
                MenuElement newPath[] = new MenuElement[path.length+1];
                System.arraycopy(path, 0, newPath, 0, path.length);
                newPath[path.length] = newItem;
                manager.setSelectedPath(newPath);
                e.consume();
            }
            return;
        }
        
        public void menuKeyReleased(MenuKeyEvent e) {
        }
    }
    /**
     * This helper is added to MenuSelectionManager as a ChangeListener to
     * listen to menu selection changes. When a menu is activated, it passes
     * focus to its parent JRootPane, and installs an ActionMap/InputMap pair
     * on that JRootPane. Those maps are necessary in order for menu
     * navigation to work. When menu is being deactivated, it restores focus
     * to the component that has had it before menu activation, and uninstalls
     * the maps.
     * This helper is also installed as a KeyListener on root pane when menu
     * is active. It forwards key events to MenuSelectionManager for mnemonic
     * keys handling.
     */
    private static class MenuKeyboardHelper
            implements ChangeListener, KeyListener {
        
        private Component lastFocused = null;
        private MenuElement[] lastPathSelected = new MenuElement[0];
        private JPopupMenu lastPopup;
        
        private JRootPane invokerRootPane;
        private ActionMap menuActionMap = getActionMap();
        private InputMap menuInputMap;
        private boolean focusTraversalKeysEnabled;
        
        /*
         * Fix for 4213634
         * If this is false, KEY_TYPED and KEY_RELEASED events are NOT
         * processed. This is needed to avoid activating a menuitem when
         * the menu and menuitem share the same mnemonic.
         */
        private boolean receivedKeyPressed = false;
        
        void removeItems() {
            if (lastFocused != null) {
                if(!lastFocused.requestFocusInWindow()) {
                    // Workarounr for 4810575.
                    // If lastFocused is not in currently focused window
                    // requestFocusInWindow will fail. In this case we must
                    // request focus by requestFocus() if it was not
                    // transferred from our popup.
                    Window cfw = KeyboardFocusManager
                            .getCurrentKeyboardFocusManager()
                            .getFocusedWindow();
                    if(cfw != null &&
                            "###focusableSwingPopup###".equals(cfw.getName())) {
                        lastFocused.requestFocus();
                    }
                    
                }
                lastFocused = null;
            }
            if (invokerRootPane != null) {
                invokerRootPane.removeKeyListener(menuKeyboardHelper);
                invokerRootPane.setFocusTraversalKeysEnabled(focusTraversalKeysEnabled);
                removeUIInputMap(invokerRootPane, menuInputMap);
                removeUIActionMap(invokerRootPane, menuActionMap);
                invokerRootPane = null;
            }
            receivedKeyPressed = false;
        }
        
        private FocusListener rootPaneFocusListener = new FocusAdapter() {
            public void focusGained(FocusEvent ev) {
                Component opposite = ev.getOppositeComponent();
                if (opposite != null) {
                    lastFocused = opposite;
                }
                ev.getComponent().removeFocusListener(this);
            }
        };
        
        /**
         * Return the last JPopupMenu in <code>path</code>,
         * or <code>null</code> if none found
         */
        JPopupMenu getActivePopup(MenuElement[] path) {
            for (int i=path.length-1; i>=0; i--) {
                MenuElement elem = path[i];
                if (elem instanceof JPopupMenu) {
                    return (JPopupMenu)elem;
                }
            }
            return null;
        }
        
        void addUIInputMap(JComponent c, InputMap map) {
            InputMap lastNonUI = null;
            InputMap parent = c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            
            while (parent != null && !(parent instanceof UIResource)) {
                lastNonUI = parent;
                parent = parent.getParent();
            }
            
            if (lastNonUI == null) {
                c.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, map);
            } else {
                lastNonUI.setParent(map);
            }
            map.setParent(parent);
        }
        
        void addUIActionMap(JComponent c, ActionMap map) {
            ActionMap lastNonUI = null;
            ActionMap parent = c.getActionMap();
            
            while (parent != null && !(parent instanceof UIResource)) {
                lastNonUI = parent;
                parent = parent.getParent();
            }
            
            if (lastNonUI == null) {
                c.setActionMap(map);
            } else {
                lastNonUI.setParent(map);
            }
            map.setParent(parent);
        }
        
        void removeUIInputMap(JComponent c, InputMap map) {
            InputMap im = null;
            InputMap parent = c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            
            while (parent != null) {
                if (parent == map) {
                    if (im == null) {
                        c.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW,
                                map.getParent());
                    } else {
                        im.setParent(map.getParent());
                    }
                    break;
                }
                im = parent;
                parent = parent.getParent();
            }
        }
        
        void removeUIActionMap(JComponent c, ActionMap map) {
            ActionMap im = null;
            ActionMap parent = c.getActionMap();
            
            while (parent != null) {
                if (parent == map) {
                    if (im == null) {
                        c.setActionMap(map.getParent());
                    } else {
                        im.setParent(map.getParent());
                    }
                    break;
                }
                im = parent;
                parent = parent.getParent();
            }
        }
        
        public void stateChanged(ChangeEvent ev) {
            if (!(UIManager.getLookAndFeel() instanceof BasicLookAndFeel)) {
                MenuSelectionManager msm = MenuSelectionManager.
                        defaultManager();
                msm.removeChangeListener(this);
                menuKeyboardHelperInstalled = false;
                return;
            }
            MenuSelectionManager msm = (MenuSelectionManager)ev.getSource();
            MenuElement[] p = msm.getSelectedPath();
            JPopupMenu popup = getActivePopup(p);
            if (popup != null && !popup.isFocusable()) {
                // Do nothing for non-focusable popups
                return;
            }
            
            if   (lastPathSelected.length != 0 && p.length != 0 ) {
                if (!checkInvokerEqual(p[0],lastPathSelected[0])) {
                    removeItems();
                    lastPathSelected = new MenuElement[0];
                }
                
            }
            
            
            
            
            if (lastPathSelected.length == 0 && p.length > 0) {
                // menu posted
                JComponent invoker;
                
                if (popup == null) {
                    if (p.length == 2 && p[0] instanceof JMenuBar &&
                            p[1] instanceof JMenu) {
                        // a menu has been selected but not open
                        invoker = (JComponent)p[1];
                        popup = ((JMenu)invoker).getPopupMenu();
                    } else {
                        return;
                    }
                } else {
                    Component c = popup.getInvoker();
                    if(c instanceof JFrame) {
                        invoker = ((JFrame)c).getRootPane();
                    } else if(c instanceof JApplet) {
                        invoker = ((JApplet)c).getRootPane();
                    } else {
                        while (!(c instanceof JComponent)) {
                            if (c == null) {
                                return;
                            }
                            c = c.getParent();
                        }
                        invoker = (JComponent)c;
                    }
                }
                
                // remember current focus owner
                lastFocused = KeyboardFocusManager.
                        getCurrentKeyboardFocusManager().getFocusOwner();
                
                // request focus on root pane and install keybindings
                // used for menu navigation
                invokerRootPane = SwingUtilities.getRootPane(invoker);
                if (invokerRootPane != null) {
                    invokerRootPane.addFocusListener(rootPaneFocusListener);
                    invokerRootPane.requestFocus(true);
                    invokerRootPane.addKeyListener(menuKeyboardHelper);
                    focusTraversalKeysEnabled = invokerRootPane.
                            getFocusTraversalKeysEnabled();
                    invokerRootPane.setFocusTraversalKeysEnabled(false);
                    
                    menuInputMap = getInputMap(popup, invokerRootPane);
                    addUIInputMap(invokerRootPane, menuInputMap);
                    addUIActionMap(invokerRootPane, menuActionMap);
                }
            } else if (lastPathSelected.length != 0 && p.length == 0) {
                // menu hidden -- return focus to where it had been before
                // and uninstall menu keybindings
                removeItems();
            } else {
                if (popup != lastPopup) {
                    receivedKeyPressed = false;
                }
            }
            
            // Remember the last path selected
            lastPathSelected = p;
            lastPopup = popup;
        }
        
        public void keyPressed(KeyEvent ev) {
            receivedKeyPressed = true;
            MenuSelectionManager.defaultManager().processKeyEvent(ev);
        }
        
        public void keyReleased(KeyEvent ev) {
            if (receivedKeyPressed) {
                receivedKeyPressed = false;
                MenuSelectionManager.defaultManager().processKeyEvent(ev);
            }
        }
        
        public void keyTyped(KeyEvent ev) {
            if (receivedKeyPressed) {
                MenuSelectionManager.defaultManager().processKeyEvent(ev);
            }
        }
    }
    static ActionMap getActionMap() {
        // XXX - Implement me
        return new ActionMap();
        //   return (ActionMap) UIManager.get("PopupMenu.actionMap");
    }
    static InputMap getInputMap(JPopupMenu popup, JComponent c) {
        InputMap windowInputMap = null;
        Object[] bindings = (Object[])UIManager.get("PopupMenu.selectedWindowInputMapBindings");
        if (bindings != null) {
            windowInputMap = LookAndFeel.makeComponentInputMap(c, bindings);
            if (!popup.getComponentOrientation().isLeftToRight()) {
                Object[] km = (Object[])UIManager.get("PopupMenu.selectedWindowInputMapBindings.RightToLeft");
                if (km != null) {
                    InputMap rightToLeftInputMap = LookAndFeel.makeComponentInputMap(c, km);
                    rightToLeftInputMap.setParent(windowInputMap);
                    windowInputMap = rightToLeftInputMap;
                }
            }
        }
        return windowInputMap;
    }
    
    private static boolean checkInvokerEqual(MenuElement present, MenuElement last) {
        Component invokerPresent = present.getComponent();
        Component invokerLast = last.getComponent();
        
        if (invokerPresent instanceof JPopupMenu) {
            invokerPresent = ((JPopupMenu)invokerPresent).getInvoker();
        }
        if (invokerLast instanceof JPopupMenu) {
            invokerLast = ((JPopupMenu)invokerLast).getInvoker();
        }
        return (invokerPresent == invokerLast);
    }
}
