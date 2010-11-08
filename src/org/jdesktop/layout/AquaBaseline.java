/*
 * Copyright (C) 2005-2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.layout;

import org.jdesktop.layout.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import java.util.Dictionary;
import java.util.Enumeration;
import javax.swing.border.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Convenience class that can be used to determine the baseline of a
 * particular component.
 * <p>
 * This class is primarily useful for JREs prior to 1.6.  In 1.6 API for this
 * was added directly to Component, JComponent and the
 * appropriate ComponentUIs.  When run on a JRE of 1.6 or greater this will directly
 * call into the getBaseline method of Component.
 *
 * @author  Werner Randelshofer
 * @version $Revision$
 */
class AquaBaseline extends Baseline {
    static final AquaBaseline INSTANCE = new AquaBaseline();
    
    //
    // Used by button and label baseline code, cached to avoid excessive
    // garbage.
    //
    private static final Rectangle viewRect = new Rectangle();
    private static final Rectangle textRect = new Rectangle();
    private static final Rectangle iconRect = new Rectangle();

    // 
    // These come from TitleBorder.  NOTE that these are NOT final in
    // TitledBorder
    //
    private static final int EDGE_SPACING = 2;
    private static final int TEXT_SPACING = 2;


    private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

    // Prototype label for calculating baseline of tables.
    private static JLabel TABLE_LABEL;

    // Prototype label for calculating baseline of lists.
    private static JLabel LIST_LABEL;

    // Prototype label for calculating baseline of trees.
    private static JLabel TREE_LABEL;


    /**
     * Returns the baseline for the specified component, or -1 if the
     * baseline can not be determined.  The baseline is measured from
     * the top of the component.
     *
     * @param component JComponent to calculate baseline for
     * @param width Width of the component to determine baseline for.
     * @param height Height of the component to determine baseline for.
     * @return baseline for the specified component
     */
    public int getComponentBaseline(JComponent component, int width,
            int height) {
        String uid = component.getUIClassID();
        int baseline = -1;
        if (uid == "ButtonUI" || uid == "ToggleButtonUI") {
            baseline = getButtonBaseline((AbstractButton)component,
                                         height);
        }
        else if (uid == "CheckBoxUI" || uid == "RadioButtonUI") {
            baseline = getCheckBoxBaseline((AbstractButton)component, height) + 1;
        }
        else if (uid == "ComboBoxUI") {
            return getComboBoxBaseline((JComboBox)component,
                                       height);
        }
        else if (uid == "TextAreaUI") {
            return getTextAreaBaseline((JTextArea)component, height);
        }
        else if (uid == "FormattedTextFieldUI" ||
                 uid == "PasswordFieldUI" ||
                 uid == "TextFieldUI") {
            baseline = getSingleLineTextBaseline((JTextComponent)component,
                                                 height);
        }
        else if (uid == "LabelUI") {
            baseline = getLabelBaseline((JLabel)component, height);
        }
        else if (uid == "ListUI") {
            baseline = getListBaseline((JList)component, height);
        }
        else if (uid == "PanelUI") {
            baseline = getPanelBaseline((JPanel)component, height);
        }
        else if (uid == "ProgressBarUI") {
            baseline = getProgressBarBaseline((JProgressBar)component, height);
        }
        else if (uid == "SliderUI") {
            baseline = getSliderBaseline((JSlider)component, height);
        }
        else if (uid == "SpinnerUI") {
            baseline = getSpinnerBaseline((JSpinner)component, height);
        }
        else if (uid == "ScrollPaneUI") {
            baseline = getScrollPaneBaseline((JScrollPane)component, height);
        }
        else if (uid == "TabbedPaneUI") {
            baseline = getTabbedPaneBaseline((JTabbedPane)component, height);
        }
        else if (uid == "TableUI") {
            baseline = getTableBaseline((JTable)component, height);
        }
        else if (uid == "TreeUI") {
            baseline = getTreeBaseline((JTree)component, height);
        }
        return Math.max(baseline, -1);
    }

    private static Insets rotateInsets(Insets topInsets, int targetPlacement) {
        switch(targetPlacement) {
          case JTabbedPane.LEFT:
              return new Insets(topInsets.left, topInsets.top, 
                                topInsets.right, topInsets.bottom);
          case JTabbedPane.BOTTOM:
              return new Insets(topInsets.bottom, topInsets.left,
                                topInsets.top, topInsets.right);
          case JTabbedPane.RIGHT:
              return new Insets(topInsets.left, topInsets.bottom,
                                topInsets.right, topInsets.top);
          default:
              return new Insets(topInsets.top, topInsets.left,
                                topInsets.bottom, topInsets.right);
        }
    }
    
    private int getMaxTabHeight(JTabbedPane tp) {
        int fontHeight = tp.getFontMetrics(tp.getFont()).getHeight();
        int height = fontHeight;
        boolean tallerIcons = false;
        for (int counter = tp.getTabCount() - 1; counter >= 0; counter--) {
            Icon icon = tp.getIconAt(counter);
            if (icon != null) {
                int iconHeight = icon.getIconHeight();
                height = Math.max(height, iconHeight);
                if (iconHeight > fontHeight) {
                    tallerIcons = true;
                }
            }
        }
        ///Insets tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
        height += 2;
        /*
        if (!isMetal() || !tallerIcons) {
            height += tabInsets.top + tabInsets.bottom;
        }*/
        return height;
    }

    private int getTabbedPaneBaseline(JTabbedPane tp, int height) {
        if (tp.getTabCount() > 0) {
            Insets insets = tp.getInsets();
            /*Insets contentBorderInsets = UIManager.getInsets(
                "TabbedPane.contentBorderInsets");*/
            Insets tabAreaInsets = rotateInsets(UIManager.getInsets(
                                                 "TabbedPane.tabAreaInsets"),
                                                tp.getTabPlacement());
            FontMetrics metrics = tp.getFontMetrics(tp.getFont());
            int maxHeight = getMaxTabHeight(tp);
            iconRect.setBounds(0, 0, 0, 0);
            textRect.setBounds(0, 0, 0, 0);
            viewRect.setBounds(0, 0, Short.MAX_VALUE, maxHeight);
            SwingUtilities.layoutCompoundLabel(tp, metrics, "A", null,
                                               SwingUtilities.CENTER,
                                               SwingUtilities.CENTER,
                                               SwingUtilities.CENTER,
                                               SwingUtilities.TRAILING,
                                               viewRect,
                                               iconRect,
                                               textRect,
                                               0);
            int baseline = textRect.y + metrics.getAscent();
            switch(tp.getTabPlacement()) {
            case JTabbedPane.TOP:
                baseline += insets.top + tabAreaInsets.top + 3;
                /*
                if (isWindows()) {
                    if (tp.getTabCount() > 1) {
                        baseline += 1;
                    }
                    else {
                        baseline -= 1;
                    }
                }*/
                return baseline;
            case JTabbedPane.BOTTOM:
                baseline = tp.getHeight() - insets.bottom -
                    tabAreaInsets.bottom - maxHeight + baseline;
                /*
                if (isWindows()) {
                    if (tp.getTabCount() > 1) {
                        baseline += -1;
                    }
                    else {
                        baseline += 1;
                    }
                }*/
                return baseline;
            case JTabbedPane.LEFT:
            case JTabbedPane.RIGHT:
                baseline += insets.top + tabAreaInsets.top;
                /*
                if (isWindows()) {
                    baseline += (maxHeight % 2);
                }*/
                return baseline;
            }
        }
        return -1;
    }

    private int getSliderBaseline(JSlider slider, int height) {
        if (slider.getPaintLabels()) {
            FontMetrics metrics = slider.getFontMetrics(slider.getFont());
            Insets insets = slider.getInsets();
            Insets focusInsets = (Insets)UIManager.get("Slider.focusInsets");
	    if (slider.getOrientation() == JSlider.HORIZONTAL) {
                int tickLength = 8;
                int contentHeight = height - insets.top - insets.bottom -
                    focusInsets.top - focusInsets.bottom;
                int thumbHeight = 20;
                /*
                if (isMetal()) {
                    tickLength = ((Integer)UIManager.get(
                                      "Slider.majorTickLength")).intValue() + 5;
                    thumbHeight = UIManager.getIcon(
                        "Slider.horizontalThumbIcon" ).getIconHeight();
                }
                else if (isWindows() && isXP()) {
                    // PENDING: this is not correct, this should come from
                    // the skin (in >= 1.5), but short of reflection
                    // hacks we don't have access to the real value.
                    thumbHeight++;
                }*/
                int centerSpacing = thumbHeight;
                if (slider.getPaintTicks()) {
                    // centerSpacing += getTickLength();
                    centerSpacing += tickLength;
                }
                // Assume uniform labels.
                centerSpacing += metrics.getAscent() + metrics.getDescent();
                int trackY = insets.top + focusInsets.top +
                    (contentHeight - centerSpacing - 1) / 2;
                int trackHeight = thumbHeight;
                int tickY = trackY + trackHeight;
                int tickHeight = tickLength;
                if (!slider.getPaintTicks()) {
                    tickHeight = 0;
                }
                int labelY = tickY + tickHeight;
                return labelY + metrics.getAscent();
            }
            else { // vertical
                boolean inverted = slider.getInverted();
                Integer value = inverted ? getMinSliderValue(slider) :
                                           getMaxSliderValue(slider);
                if (value != null) {
                    int thumbHeight = 11;
                    /*
                    if (isMetal()) {
                        thumbHeight = UIManager.getIcon(
                            "Slider.verticalThumbIcon").getIconHeight();
                    }*/
                    int trackBuffer = Math.max(metrics.getHeight() / 2,
                                               thumbHeight / 2);
                    int contentY = focusInsets.top + insets.top;
                    int trackY = contentY + trackBuffer;
                    int trackHeight = height - focusInsets.top -
                        focusInsets.bottom - insets.top - insets.bottom -
                        trackBuffer - trackBuffer;
                    ///int maxValue = getMaxSliderValue(slider).intValue();
                    int min = slider.getMinimum();
                    int max = slider.getMaximum();
                    double valueRange = (double)max - (double)min;
                    double pixelsPerValue = (double)trackHeight /
                        (double)valueRange;
                    int trackBottom = trackY + (trackHeight - 1);
                    int yPosition;

                    if (!inverted) {
                        yPosition = trackY;
                        yPosition += Math.round(pixelsPerValue *
                                            ((double)max - value.intValue()));
                    }
                    else {
                        yPosition = trackY;
                        yPosition += Math.round(pixelsPerValue *
                                           ((double)value.intValue() - min) );
                    }
                    yPosition = Math.max(trackY, yPosition);
                    yPosition = Math.min(trackBottom, yPosition);
                    return yPosition - metrics.getHeight() / 2 +
                        metrics.getAscent();
                }
            }
        }
        return -1;
    }

    private Integer getMaxSliderValue(JSlider slider) {
        Dictionary dictionary = slider.getLabelTable();
        if (dictionary != null) {
            Enumeration keys = dictionary.keys();
            int max = slider.getMinimum() - 1;
            while (keys.hasMoreElements()) {
                max = Math.max(max, ((Integer)keys.nextElement()).intValue());
            }
            if (max == slider.getMinimum() - 1) {
                return null;
            }
            return max;
        }
        return null;
    }

    private Integer getMinSliderValue(JSlider slider) {
        Dictionary dictionary = slider.getLabelTable();
        if (dictionary != null) {
            Enumeration keys = dictionary.keys();
            int min = slider.getMaximum() + 1;
            while (keys.hasMoreElements()) {
                min = Math.min(min, ((Integer)keys.nextElement()).intValue());
            }
            if (min == slider.getMaximum() + 1) {
                return null;
            }
            return min;
        }
        return null;
    }

    private int getProgressBarBaseline(JProgressBar pb, int height) {
        if (pb.isStringPainted() &&
                pb.getOrientation() == JProgressBar.HORIZONTAL) {
            FontMetrics metrics = pb.getFontMetrics(pb.getFont());
            Insets insets = pb.getInsets();
            int y = insets.top;
            /*
            if (isWindows() && isXP()) {
                if (pb.isIndeterminate()) {
                    y = -1;
                    height--;
                }
                else {
                    y = 0;
                    height -= 3;
                }
            }
            else if (isGTK()) {
                return (height - metrics.getAscent() - 
                        metrics.getDescent()) / 2 + metrics.getAscent();
            }
            else {*/
                height -= insets.top + insets.bottom;
            //}
            return y + (height + metrics.getAscent() -
                        metrics.getLeading() -
                        metrics.getDescent()) / 2;
        }
        return -1;
    }

    private int getTreeBaseline(JTree tree, int height) {
        int rowHeight = tree.getRowHeight();
        if (TREE_LABEL == null) {
            TREE_LABEL = new JLabel("X");
            TREE_LABEL.setIcon(UIManager.getIcon("Tree.closedIcon"));
        }
        JLabel label = TREE_LABEL;
        label.setFont(tree.getFont());
        if (rowHeight <= 0) {
            rowHeight = label.getPreferredSize().height;
        }
        return getLabelBaseline(label, rowHeight) + tree.getInsets().top;
    }

    private int getTableBaseline(JTable table, int height) {
        if (TABLE_LABEL == null) {
            TABLE_LABEL = new JLabel("");
            TABLE_LABEL.setBorder(new EmptyBorder(1, 1, 1, 1));
        }
        JLabel label = TABLE_LABEL;
        label.setFont(table.getFont());
        int rowMargin = table.getRowMargin();
        int baseline = getLabelBaseline(label, table.getRowHeight() -
                                        rowMargin);
        return baseline + rowMargin / 2;
    }

    private int getTextAreaBaseline(JTextArea text, int height) {
        Insets insets = text.getInsets();
        FontMetrics fm = text.getFontMetrics(text.getFont());
        return insets.top + fm.getAscent();
    }
    
    private int getListBaseline(JList list, int height) {
        int rowHeight = list.getFixedCellHeight();
        if (LIST_LABEL == null) {
            LIST_LABEL = new JLabel("X");
            LIST_LABEL.setBorder(new EmptyBorder(1, 1, 1, 1));
        }
        JLabel label = LIST_LABEL;
        label.setFont(list.getFont());
        // JList actually has much more complex behavior here.
        // If rowHeight != -1 the rowHeight is either the max of all cell
        // heights (layout orientation != VERTICAL), or is variable depending
        // upon the cell.  We assume a default size.
        // We could theoretically query the real renderer, but that would
        // not work for an empty model and the results may vary with 
        // the content.
        if (rowHeight == -1) {
            rowHeight = label.getPreferredSize().height;
        }
        return getLabelBaseline(label, rowHeight) + list.getInsets().top;
    }

    private int getScrollPaneBaseline(JScrollPane sp, int height) {
        Component view = sp.getViewport().getView();
        if (view instanceof JComponent) {
            int baseline = getBaseline((JComponent)view);
            if (baseline > 0) {
                return baseline + sp.getViewport().getY();
            }
        }
        return -1;
    }

    private int getPanelBaseline(JPanel panel, int height) {
        Border border = panel.getBorder();
        if (border instanceof TitledBorder) {
            TitledBorder titledBorder = (TitledBorder)border;
            if (titledBorder.getTitle() != null &&
                      !"".equals(titledBorder.getTitle())) {
                Font font = titledBorder.getTitleFont();
                if (font == null) {
                    font = panel.getFont();
                    if (font == null) {
                        font = new Font("Dialog", Font.PLAIN, 12);
                    }
                }
                Border border2 = titledBorder.getBorder();
                Insets borderInsets;
                if (border2 != null) {
                    borderInsets = border2.getBorderInsets(panel);
                }
                else {
                    borderInsets = EMPTY_INSETS;
                }
                FontMetrics fm = panel.getFontMetrics(font);
                int fontHeight = fm.getHeight();
                int descent = fm.getDescent();
                int ascent = fm.getAscent();
                int y = EDGE_SPACING;
                int h = height - EDGE_SPACING * 2;
                int diff;
                switch (((TitledBorder)border).getTitlePosition()) {
                case TitledBorder.ABOVE_TOP:
                    diff = ascent + descent + (Math.max(EDGE_SPACING,
                                    TEXT_SPACING*2) - EDGE_SPACING);
                    return y + diff - (descent + TEXT_SPACING);
                case TitledBorder.TOP:
                case TitledBorder.DEFAULT_POSITION:
                    diff = Math.max(0, ((ascent/2) + TEXT_SPACING) -
                                    EDGE_SPACING);
                    return (y + diff - descent) +
                           (borderInsets.top + ascent + descent)/2;
                case TitledBorder.BELOW_TOP:
                    return y + borderInsets.top + ascent + TEXT_SPACING;
                case TitledBorder.ABOVE_BOTTOM:
                    return (y + h) -
                        (borderInsets.bottom + descent + TEXT_SPACING);
                case TitledBorder.BOTTOM:
                    h -= fontHeight / 2;
                    return ((y + h) - descent) +
                           ((ascent + descent) - borderInsets.bottom)/2;
                case TitledBorder.BELOW_BOTTOM:
                    h -= fontHeight;
                    return y + h + ascent + TEXT_SPACING;
                }
            }
        }
        return -1;
    }

    private int getSpinnerBaseline(JSpinner spinner, int height) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor defaultEditor = (JSpinner.DefaultEditor)
                                          editor;
            JTextField tf = defaultEditor.getTextField();
            Insets spinnerInsets = spinner.getInsets();
            Insets editorInsets = defaultEditor.getInsets();
            int offset = spinnerInsets.top + editorInsets.top;
            height -= (offset + spinnerInsets.bottom + editorInsets.bottom);
            if (height <= 0) {
                return -1;
            }
            return offset + getSingleLineTextBaseline(tf, height);
        }
        Insets insets = spinner.getInsets();
        FontMetrics fm = spinner.getFontMetrics(spinner.getFont());
        return insets.top + fm.getAscent();
    }

    private int getLabelBaseline(JLabel label, int height) {
        Icon icon = (label.isEnabled()) ? label.getIcon() :
                           label.getDisabledIcon();
        FontMetrics fm = label.getFontMetrics(label.getFont());

        resetRects(label, height);

        SwingUtilities.layoutCompoundLabel(label, fm,
            "a", icon, label.getVerticalAlignment(),
            label.getHorizontalAlignment(), label.getVerticalTextPosition(),
            label.getHorizontalTextPosition(), viewRect, iconRect, textRect,
            label.getIconTextGap());

        return textRect.y + fm.getAscent();
    }

    private int getComboBoxBaseline(JComboBox combobox, int height) {
        Insets insets = combobox.getInsets();
        int y = insets.top;
        height -= (insets.top + insets.bottom);
        if (combobox.isEditable()) {
            ComboBoxEditor editor = combobox.getEditor();
            if (editor != null && (editor.getEditorComponent() instanceof
                                   JTextField)) {
                JTextField tf = (JTextField)editor.getEditorComponent();
                return y + getSingleLineTextBaseline(tf, height);
            }
        }
        y -= 1;
        // Use the renderer to calculate baseline
        ListCellRenderer renderer = combobox.getRenderer();
        if (renderer instanceof JLabel) {
            return y + getLabelBaseline((JLabel)renderer, height);
        }
        // Renderer isn't a label, use metrics directly.
        FontMetrics fm = combobox.getFontMetrics(combobox.getFont());
        return y + fm.getAscent();
    }

    /**
     * Returns the baseline for single line text components, like
     * <code>JTextField</code>.
     */
    private int getSingleLineTextBaseline(JTextComponent textComponent,
                                                 int h) {
        View rootView = textComponent.getUI().getRootView(textComponent);
        if (rootView.getViewCount() > 0) {
            Insets insets = textComponent.getInsets();
            int height = h - insets.top - insets.bottom;
            int y = insets.top;
            View fieldView = rootView.getView(0);
	    int vspan = (int)fieldView.getPreferredSpan(View.Y_AXIS);
	    if (height != vspan) {
		int slop = height - vspan;
		y += slop / 2;
	    }
            FontMetrics fm = textComponent.getFontMetrics(
                                 textComponent.getFont());
            y += fm.getAscent();
            return y;
        }
        return -1;
    }

    /**
     * Returns the baseline for buttons.
     */
    private int getCheckBoxBaseline(AbstractButton button, int height) {
        FontMetrics fm = button.getFontMetrics(button.getFont());

        resetRects(button, height);

        // NOTE: that we use "a" here to make sure we get a valid value, if
        // we were to pass in an empty string or null we would not get
        // back the right thing.
        SwingUtilities.layoutCompoundLabel(
            button, fm, "a", button.getIcon(), 
            button.getVerticalAlignment(), button.getHorizontalAlignment(),
            button.getVerticalTextPosition(),
            button.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, 
	    button.getText() == null ? 0 : button.getIconTextGap());

        return textRect.y + fm.getAscent();
        }
    /**
     * Returns the baseline for buttons.
     */
    private int getButtonBaseline(AbstractButton button, int height) {
        FontMetrics fm = button.getFontMetrics(button.getFont());

        resetRects(button, height);

        // NOTE: that we use "a" here to make sure we get a valid value, if
        // we were to pass in an empty string or null we would not get
        // back the right thing.
        SwingUtilities.layoutCompoundLabel(
            button, fm, "a", button.getIcon(), 
            button.getVerticalAlignment(), button.getHorizontalAlignment(),
            button.getVerticalTextPosition(),
            button.getHorizontalTextPosition(),
            viewRect, iconRect, textRect, 
	    button.getText() == null ? 0 : button.getIconTextGap());

        return textRect.y + fm.getAscent() + 1;
    }

    private void resetRects(JComponent c, int height) {
        Insets insets = c.getInsets();
        viewRect.x = insets.left;
        viewRect.y = insets.top;
        viewRect.width = c.getWidth() - (insets.right + viewRect.x);
        viewRect.height = height - (insets.bottom + viewRect.y);
        textRect.x = textRect.y = textRect.width = textRect.height = 0;
        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
    }
    
    private static boolean isMac() {
        return UIManager.getLookAndFeel().getID() == "Mac";
    }

    private static boolean isAqua() {
        return UIManager.getLookAndFeel().getID() == "Aqua";
    }
}
