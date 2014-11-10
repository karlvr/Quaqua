/*
 * @(#)ColorSliderUI.java
 *
 * Copyright (c) 2004-2013 Werner Randelshofer, Switzerland.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package ch.randelshofer.quaqua.quaqua18;

import apple.laf.JRSUIConstants;
import ch.randelshofer.quaqua.border.VisualMarginBorder;
import ch.randelshofer.quaqua.colorchooser.ColorSliderModel;
import ch.randelshofer.quaqua.colorchooser.ColorTrackImageProducer;
import com.apple.laf.AquaSliderUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A UI delegate for color sliders. The track of the slider visualizes how changing the value of the slider affects the
 * color. This version makes minimal changes to the Aqua slider UI, which supports Retina displays but does not support
 * a flipped (ticks on top, pointer upwards) horizontal orientation.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ColorSliderUI18 extends AquaSliderUI {
    private ColorTrackImageProducer colorTrackImageProducer;
    private Image colorTrackImage;

    /** Creates a new instance. */
    public ColorSliderUI18(JSlider b)   {
        super(b);
    }

    public static ComponentUI createUI(JComponent b)    {
        return new ColorSliderUI18((JSlider)b);
    }

    @Override
    protected void installDefaults(JSlider slider) {
        super.installDefaults(slider);

        slider.putClientProperty("JComponent.sizeVariant", "small");
        slider.putClientProperty("Slider.paintThumbArrowShape", true);
        slider.setPaintTicks(false);
    }

    @Override
    public void paintTicks(Graphics graphics) {
    }

    @Override
    public void paintTrack(final Graphics g, final JComponent c, final JRSUIConstants.Orientation orientation, final JRSUIConstants.State state) {
        int cx, cy, cw, ch;
        int pad;

        Rectangle trackBounds = trackRect;
        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            pad = trackBuffer;
            cx = trackBounds.x - pad + 1;
            cy = trackBounds.y + 8;
            cw = trackBounds.width + pad * 2 - 2;
            ch = trackBounds.height - 8;
        } else {
            pad = trackBuffer;
            cx = trackBounds.x + 6;
            cy = contentRect.y + 2;
            cw = trackBounds.width - 7;
            ch = trackBounds.height + pad * 2 - 5;
        }
        paintColorTrack(g, cx + 2, cy + 2, cw - 4, ch - 4, trackBuffer);
    }

    public void paintColorTrack(Graphics g, int x, int y, int width, int height, int buffer) {
        if (colorTrackImage==null||colorTrackImageProducer == null
        || colorTrackImageProducer.getWidth() != width
        || colorTrackImageProducer.getHeight() != height) {
            if (colorTrackImage != null) {
                colorTrackImage.flush();
            }
            colorTrackImageProducer = new ColorTrackImageProducer(width, height, buffer + 2, slider.getOrientation() == JSlider.HORIZONTAL);
            if (slider.getClientProperty("ColorSliderModel") != null) {
                colorTrackImageProducer.setColorSliderModel((ColorSliderModel) slider.getClientProperty("ColorSliderModel"));
            }
            if (slider.getClientProperty("ColorComponentIndex") != null) {
                colorTrackImageProducer.setColorComponentIndex(((Integer) slider.getClientProperty("ColorComponentIndex")));
            }
            colorTrackImageProducer.generateColorTrack();
            colorTrackImage = slider.createImage(colorTrackImageProducer);
        } else {
            colorTrackImageProducer.regenerateColorTrack();
        }
        g.drawImage(colorTrackImage, x, y, null);
    }

    @Override
    protected PropertyChangeListener createPropertyChangeListener( JSlider slider ) {
        return new CSUIPropertyChangeHandler();
    }

    public class CSUIPropertyChangeHandler extends PropertyChangeHandler {
        @Override
        public void propertyChange( PropertyChangeEvent e ) {
            String propertyName = e.getPropertyName();

            if (propertyName.equals( "Frame.active" )) {
                //calculateGeometry();
                slider.repaint();
            } else if (propertyName.equals( "ColorSliderModel" )) {
                if (colorTrackImageProducer != null) {
                    colorTrackImageProducer.setColorSliderModel(((ColorSliderModel) e.getNewValue()));
                    if (colorTrackImageProducer.needsGeneration()) {
                        slider.repaint();
                    }
                }
            } else if (propertyName.equals( "snapToTicks" )) {
                if (colorTrackImageProducer != null) {
                    colorTrackImageProducer.markAsDirty();
                    slider.repaint();
                }
            } else if (propertyName.equals( "ColorComponentIndex" )) {
                if (colorTrackImageProducer != null && e.getNewValue() != null) {
                    colorTrackImageProducer.setColorComponentIndex(((Integer) e.getNewValue()));
                    if (colorTrackImageProducer.needsGeneration()) {
                        slider.repaint();
                    }
                }
            } else if (propertyName.equals( "ColorComponentChange" )) {
                Integer value = (Integer) e.getNewValue();
                if (value != null && colorTrackImageProducer != null) {
                    colorTrackImageProducer.componentChanged(value);
                    if (colorTrackImageProducer.needsGeneration()) {
                        slider.repaint();
                    }
                }
            } else if (propertyName.equals( "ColorComponentValue" )) {
                Integer value = (Integer) slider.getClientProperty("ColorComponentChange");
                if (value != null && colorTrackImageProducer != null) {
                    colorTrackImageProducer.componentChanged(value);
                    if (colorTrackImageProducer.needsGeneration()) {
                        slider.repaint();
                    }
                }
            } else if (propertyName.equals( "Orientation" )) {
                if (slider.getOrientation() == JSlider.HORIZONTAL) {
                    slider.setBorder(new VisualMarginBorder(0,1,-1,1));
                } else {
                    slider.setBorder(new VisualMarginBorder(0,0,0,1));
                }
            }

            super.propertyChange(e);
        }
    }
}
