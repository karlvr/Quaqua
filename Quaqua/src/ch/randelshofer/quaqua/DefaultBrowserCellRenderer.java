/**
 * @(#)DefaultBrowserCellRenderer.java 
 * Copyright (c) 2008-2010 Werner Randelshofer
 * Hausmatt 10, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package ch.randelshofer.quaqua;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import java.awt.*;

/**
 * DefaultBrowserCellRenderer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DefaultBrowserCellRenderer extends JLabel implements BrowserCellRenderer {

    /** Last browser the renderer was painted in. */
    private JBrowser browser;
    /** Is the value currently selected. */
    protected boolean selected;
    /** True if has focus. */
    protected boolean hasFocus;
    /** True if draws focus border around icon as well. */
    private boolean drawsFocusBorderAroundIcon;
    /** If true, a dashed line is drawn as the focus indicator. */
    private boolean drawDashedFocusIndicator;
    // If drawDashedFocusIndicator is true, the following are used.
    /**
     * Background color of the browser.
     */
    private Color treeBGColor;
    /**
     * Color to draw the focus indicator in, determined from the background.
     * color.
     */
    private Color focusBGColor;
    // Icons
    /** Icon used to show non-leaf nodes that aren't expanded. */
    transient protected Icon closedIcon;
    /** Icon used to show leaf nodes. */
    transient protected Icon leafIcon;
    /** Icon used to show non-leaf nodes that are expanded. */
    transient protected Icon openIcon;
    // Colors
    /** Color to use for the foreground for selected nodes. */
    protected Color textSelectionColor;
    /** Color to use for the foreground for non-selected nodes. */
    protected Color textNonSelectionColor;
    /** Color to use for the background when a node is selected. */
    protected Color backgroundSelectionColor;
    /** Color to use for the background when the node isn't selected. */
    protected Color backgroundNonSelectionColor;
    /** Color to use for the focus indicator when the node has focus. */
    protected Color borderSelectionColor;

    /**
     * Returns a new instance of DefaultTreeCellRenderer.  Alignment is
     * set to left aligned. Icons and text color are determined from the
     * UIManager.
     */
    public DefaultBrowserCellRenderer() {
        setHorizontalAlignment(JLabel.LEFT);

        setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
        setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
        setOpenIcon(UIManager.getIcon("Tree.openIcon"));

        setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
        setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
        setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
        setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
        setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
        Object value = UIManager.get("Tree.drawsFocusBorderAroundIcon");
        drawsFocusBorderAroundIcon = (value != null && ((Boolean) value).booleanValue());
        value = UIManager.get("Tree.drawDashedFocusIndicator");
        drawDashedFocusIndicator = (value != null && ((Boolean) value).booleanValue());
        setBorder(null);
    }

    /**
     * Returns the default icon, for the current laf, that is used to
     * represent non-leaf nodes that are expanded.
     */
    public Icon getDefaultOpenIcon() {
        return UIManager.getIcon("Tree.openIcon");
    }

    /**
     * Returns the default icon, for the current laf, that is used to
     * represent non-leaf nodes that are not expanded.
     */
     public Icon getDefaultClosedIcon() {
        return UIManager.getIcon("Tree.closedIcon");
    }

    /**
     * Returns the default icon, for the current laf, that is used to
     * represent leaf nodes.
     */
    public Icon getDefaultLeafIcon() {
        return UIManager.getIcon("Tree.leafIcon");
    }

    /**
     * Sets the icon used to represent non-leaf nodes that are expanded.
     */
    public void setOpenIcon(Icon newIcon) {
        openIcon = newIcon;
    }

    /**
     * Returns the icon used to represent non-leaf nodes that are expanded.
     */
    public Icon getOpenIcon() {
        return openIcon;
    }

    /**
     * Sets the icon used to represent non-leaf nodes that are not expanded.
     */
    public void setClosedIcon(Icon newIcon) {
        closedIcon = newIcon;
    }

    /**
     * Returns the icon used to represent non-leaf nodes that are not
     * expanded.
     */
    public Icon getClosedIcon() {
        return closedIcon;
    }

    /**
     * Sets the icon used to represent leaf nodes.
     */
    public void setLeafIcon(Icon newIcon) {
        leafIcon = newIcon;
    }

    /**
     * Returns the icon used to represent leaf nodes.
     */
    public Icon getLeafIcon() {
        return leafIcon;
    }

    /**
     * Sets the color the text is drawn with when the node is selected.
     */
    public void setTextSelectionColor(Color newColor) {
        textSelectionColor = newColor;
    }

    /**
     * Returns the color the text is drawn with when the node is selected.
     */
    public Color getTextSelectionColor() {
        return textSelectionColor;
    }

    /**
     * Sets the color the text is drawn with when the node isn't selected.
     */
    public void setTextNonSelectionColor(Color newColor) {
        textNonSelectionColor = newColor;
    }

    /**
     * Returns the color the text is drawn with when the node isn't selected.
     */
    public Color getTextNonSelectionColor() {
        return textNonSelectionColor;
    }

    /**
     * Sets the color to use for the background if node is selected.
     */
    public void setBackgroundSelectionColor(Color newColor) {
        backgroundSelectionColor = newColor;
    }

    /**
     * Returns the color to use for the background if node is selected.
     */
    public Color getBackgroundSelectionColor() {
        return backgroundSelectionColor;
    }

    /**
     * Sets the background color to be used for non selected nodes.
     */
    public void setBackgroundNonSelectionColor(Color newColor) {
        backgroundNonSelectionColor = newColor;
    }

    /**
     * Returns the background color to be used for non selected nodes.
     */
    public Color getBackgroundNonSelectionColor() {
        return backgroundNonSelectionColor;
    }

    /**
     * Sets the color to use for the border.
     */
    public void setBorderSelectionColor(Color newColor) {
        borderSelectionColor = newColor;
    }

    /**
     * Returns the color the border is drawn.
     */
    public Color getBorderSelectionColor() {
        return borderSelectionColor;
    }

    /**
     * Gets the font of this component.
     * @return this component's font; if a font has not been set
     * for this component, the font of its parent is returned
     */
    @Override public Font getFont() {
        Font font = super.getFont();

        if (font == null && browser != null) {
            // Strive to return a non-null value, otherwise the html support
            // will typically pick up the wrong font in certain situations.
            font = browser.getFont();
        }
        return font;
    }

    /**
     * Subclassed to map <code>ColorUIResource</code>s to null. If 
     * <code>color</code> is null, or a <code>ColorUIResource</code>, this
     * has the effect of letting the background color of the JBrowser show
     * through. On the other hand, if <code>color</code> is non-null, and not
     * a <code>ColorUIResource</code>, the background becomes
     * <code>color</code>.
     */
    @Override public void setBackground(Color color) {
        if (color instanceof ColorUIResource) {
            color = null;
        }
        super.setBackground(color);
    }

    /**
     * Configures the renderer based on the passed in components.
     * The value is set from messaging the browser with
     * <code>convertValueToText</code>, which ultimately invokes
     * <code>toString</code> on <code>value</code>.
     * The foreground color is set based on the selection and the icon
     * is set based on on leaf and expanded.
     */
    @Override public Component getBrowserCellRendererComponent(JBrowser browser, Object value,
            boolean sel,
            boolean expanded,
            boolean leaf, int row,
            boolean hasFocus) {
        String stringValue = browser.convertValueToText(value, sel,
                expanded, leaf, row, hasFocus);

        this.browser = browser;
        this.hasFocus = hasFocus;
        this.selected = sel;

        setFont(browser.getFont());
        setText(stringValue);
        if (sel) {
            setForeground(getTextSelectionColor());
        } else {
            setForeground(getTextNonSelectionColor());
        }
        // There needs to be a way to specify disabled icons.
        if (!browser.isEnabled()) {
            setEnabled(false);
            if (leaf) {
                setDisabledIcon(getLeafIcon());
            } else if (expanded) {
                setDisabledIcon(getOpenIcon());
            } else {
                setDisabledIcon(getClosedIcon());
            }
        } else {
            setEnabled(true);
            if (leaf) {
                setIcon(getLeafIcon());
            } else if (expanded) {
                setIcon(getOpenIcon());
            } else {
                setIcon(getClosedIcon());
            }
        }
        setComponentOrientation(browser.getComponentOrientation());

        return this;
    }

    /**
     * Paints the value.  The background is filled based on selected.
     */
    @Override public void paintComponent(Graphics g) {
        Color bColor;

        if (selected) {
            bColor = getBackgroundSelectionColor();
        } else {
            bColor = getBackgroundNonSelectionColor();
            if (bColor == null) {
                bColor = getBackground();
            }
        }
        int imageOffset = -1;
        if (bColor != null) {
            Icon currentI = getIcon();

            imageOffset = getLabelStart();
        }

        if (hasFocus) {
            if (drawsFocusBorderAroundIcon) {
                imageOffset = 0;
            } else if (imageOffset == -1) {
                imageOffset = getLabelStart();
            }
            if (getComponentOrientation().isLeftToRight()) {
                paintFocus(g, imageOffset, 0, getWidth() - imageOffset,
                        getHeight());
            } else {
                paintFocus(g, 0, 0, getWidth() - imageOffset, getHeight());
            }
        }
        super.paintComponent(g);
    }

    private void paintFocus(Graphics g, int x, int y, int w, int h) {
        if (true) return;
        Color bsColor = getBorderSelectionColor();

        if (bsColor != null && (selected || !drawDashedFocusIndicator)) {
            g.setColor(bsColor);
            g.drawRect(x, y, w - 1, h - 1);
        }
        if (drawDashedFocusIndicator) {
            Color color;
            if (selected) {
                color = getBackgroundSelectionColor();
            } else {
                color = getBackgroundNonSelectionColor();
                if (color == null) {
                    color = getBackground();
                }
            }

            if (treeBGColor != color) {
                treeBGColor = color;
                focusBGColor = new Color(~color.getRGB());
            }
            g.setColor(focusBGColor);
            BasicGraphicsUtils.drawDashedRect(g, x, y, w, h);
        }
    }

    private int getLabelStart() {
        Icon currentI = getIcon();
        if (currentI != null && getText() != null) {
            return currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
        }
        return 0;
    }

    /**
     * Overrides <code>JComponent.getPreferredSize</code> to
     * return slightly wider preferred size value.
     */
    @Override public Dimension getPreferredSize() {
        Dimension retDimension = super.getPreferredSize();

        if (retDimension != null) {
            retDimension = new Dimension(retDimension.width + 3,
                    retDimension.height);
        }
        return retDimension;
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override public void validate() {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     *
     * @since 1.5
     */
    @Override public void invalidate() {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override public void revalidate() {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override public void repaint(long tm, int x, int y, int width, int height) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override public void repaint(Rectangle r) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     *
     * @since 1.5
     */
    @Override public void repaint() {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (propertyName != null && propertyName.equals("text")) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override public void firePropertyChange(String propertyName, char oldValue, char newValue) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override public void firePropertyChange(String propertyName, short oldValue, short newValue) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override public void firePropertyChange(String propertyName, int oldValue, int newValue) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override public void firePropertyChange(String propertyName, long oldValue, long newValue) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override public void firePropertyChange(String propertyName, float oldValue, float newValue) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override public void firePropertyChange(String propertyName, double oldValue, double newValue) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }
}
