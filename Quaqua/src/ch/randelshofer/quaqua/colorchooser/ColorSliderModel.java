/*
 * @(#)ColorSliderModel.java
 *
 * Copyright (c) 2005-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.quaqua.colorchooser;

import java.awt.*;
import java.io.Serializable;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.SliderUI;
import java.lang.reflect.Constructor;
import java.util.*;
/**
 * Abstract super class for ColorModels which can be used in conjunction with
 * ColorSliderUI user interface delegates.
 * <p>
 * Colors are represented as arrays of color components represented as
 * BoundedRangeModel's. Each BoundedRangeModel can be visualized using a JSlider
 * having a ColorSliderUI.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public abstract class ColorSliderModel implements Serializable {
    /**
     * JSlider's associated to this ColorSliderModel.
     */
    private LinkedList sliders = new LinkedList();
    /**
     * ChangeListener's listening to changes in this ColorSliderModel.
     */
    private LinkedList listeners = new LinkedList();

    /**
     * Components of the color model.
     */
    protected DefaultBoundedRangeModel[] components;

    /**
     * Speed optimization. This way, we do not need to create a new array
     * for each invocation of method getInterpolatedRGB().
     * Note: This variable must not use in reentrant methods.
     */
    protected int[] values;

    /**
     * Creates a new ColorSliderModel with an array of BoundedRangeModel's
     * for the color components.
     */
    protected ColorSliderModel(DefaultBoundedRangeModel[] components) {
        this.components = components;
        values = new int[components.length];

        for (int i=0; i < components.length; i++) {
            final int componentIndex = i;
            components[i].addChangeListener(
            new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    fireColorChanged(componentIndex);
                    fireStateChanged();
                }
            });
        }
    }

    /**
     * Configures a JSlider for this ColorSliderModel.
     * If the JSlider is already configured for another ColorSliderModel,
     * it is unconfigured first.
     */
    public void configureColorSlider(int component, JSlider slider) {
        if (slider.getClientProperty("ColorSliderModel") != null) {
            ((ColorSliderModel) slider.getClientProperty("ColorSliderModel"))
            .unconfigureColorSlider(slider);
        }

        Class uiClass = ColorSliderUI.class;
        String className = UIManager.getString("ColorChooser.ColorSlider.uiClassName");
        if (className != null) {
            try {
                uiClass = Class.forName(className);
            } catch (Exception ex) {
            }
        }

        if (!uiClass.isInstance(slider.getUI())) {
            try {
                Constructor cons = uiClass.getConstructor(JSlider.class);
                slider.setUI((SliderUI) cons.newInstance(slider));
            } catch (Exception ex) {
            }
        }

        slider.setModel(getBoundedRangeModel(component));
        slider.putClientProperty("ColorSliderModel", this);
        slider.putClientProperty("ColorComponentIndex", component);
        addColorSlider(slider);
    }

    /**
     * Unconfigures a JSlider from this ColorSliderModel.
     */
    public void unconfigureColorSlider(JSlider slider) {
        if (slider.getClientProperty("ColorSliderModel") == this) {
            // XXX - This creates a NullPointerException ??
            //slider.setUI((SliderUI) UIManager.getUI(slider));
            slider.setModel(new DefaultBoundedRangeModel());
            slider.putClientProperty("ColorSliderModel", null);
            slider.putClientProperty("ColorComponentIndex", null);
            removeColorSlider(slider);
        }
    }

    /**
     * Returns the number of components of this color component model.
     */
    public int getComponentCount() {
        return components.length;
    }
    /**
     * Returns the bounded range model of the specified color component.
     */
    public DefaultBoundedRangeModel getBoundedRangeModel(int component) {
        return components[component];
    }
    /**
     * Returns the value of the specified color component.
     */
    public int getValue(int component) {
        return components[component].getValue();
    }
    /**
     * Sets the value of the specified color component.
     */
    public void setValue(int component, int value) {
        components[component].setValue(value);
    }

    /**
     * Returns an interpolated RGB value by using the values of the color
     * components of this ColorSliderModel except for the component specified
     * as an argument. For this component the ratio between zero
     * and the maximum of its BoundedRangeModel is used.
     */
    public int getInterpolatedRGB(int component, float ratio) {
        for (int i=0, n = getComponentCount(); i < n; i++) {
            values[i] = components[i].getValue();
        }
        values[component] = (int) (ratio * components[component].getMaximum());
        return toRGB(values);
    }

    protected void addColorSlider(JSlider slider) {
        sliders.add(slider);
    }
    protected void removeColorSlider(JSlider slider) {
        sliders.remove(slider);
    }
    public void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }


    protected void fireColorChanged(int componentIndex) {
        Integer index = componentIndex;
        Color value = getColor();
        for (Iterator i = sliders.iterator(); i.hasNext(); ) {
            JSlider slider = (JSlider) i.next();
            slider.putClientProperty("ColorComponentChange", index);
            slider.putClientProperty("ColorComponentValue", value);
        }
    }
    public void fireStateChanged() {
        ChangeEvent event = new ChangeEvent(this);
        for (Iterator i=listeners.iterator(); i.hasNext(); ) {
            ChangeListener l = (ChangeListener) i.next();
            l.stateChanged(event);
        }
    }

    public Color getColor() {
        return new Color(getRGB());
    }

    public void setColor(Color color) {
        int rgb = color.getRGB();
        if (rgb != getRGB()) {
            setRGB(rgb);
        }
    }

    public abstract void setRGB(int rgb);
    public abstract int getRGB();
    public abstract int toRGB(int[] values);
}
