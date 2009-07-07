/*
 * @(#)GrayColorSliderModel.java  1.0  May 22, 2005
 *
 * Copyright (c) 2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua.colorchooser;

import javax.swing.*;
/**
 * A ColorSliderModel for a gray color model (brightness).
 *
 * @author  Werner Randelshofer
 * @version 1.0 May 22, 2005 Created.
 */
public class GrayColorSliderModel extends ColorSliderModel {
    
    /**
     * Creates a new instance.
     */
    public GrayColorSliderModel() {
        super(new DefaultBoundedRangeModel[] {
            new DefaultBoundedRangeModel(0, 0, 0, 100)
        });
    }
    
    public int getRGB() {
        int br = (int) (components[0].getValue() * 2.55f);
        return 0xff000000 | (br << 16) | (br << 8) | (br);
    }
    
    public void setRGB(int rgb) {
        components[0].setValue((int)
        (
        (((rgb & 0xff0000) >> 16) + ((rgb & 0x00ff00) >> 8) + (rgb & 0x0000ff))
        / 3f / 2.55f
        )
        );
    }
    
    public int toRGB(int[] values) {
        int br = (int) (values[0] * 2.55f);
        return 0xff000000 | (br << 16) | (br << 8) | (br);
    }
}
