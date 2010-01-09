/*
 * @(#)ShadowFilter.java  1.0  28 March 2005
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

package ch.randelshofer.quaqua.util;

import java.awt.*;
import java.awt.image.*;
/**
 * ShadowFilter changes the color of an image to all black, and 
 * reduces the alpha channel to 50 percent.
 * This is used by the Quaqua Look and Feel, to create a shadow image.
 *
 * @author  Werner Randelshofer
 * @version 1.0  28 March 2005  Created.
 */
public class ShadowFilter extends RGBImageFilter {
    
    /** Creates a new instance. */
    public ShadowFilter() {
        canFilterIndexColorModel = true;
    }
    
    /**
     * Creates a shadow image
     */
    public static Image createShadowImage (Image i) {
	ImageFilter filter = new ShadowFilter();
	ImageProducer prod = new FilteredImageSource(i.getSource(), filter);
	Image filteredImage = Toolkit.getDefaultToolkit().createImage(prod);
	return filteredImage;
    }
    
    public int filterRGB(int x, int y, int rgb) {
        return (rgb & 0xfe000000) >>> 1; 
    }    
}
