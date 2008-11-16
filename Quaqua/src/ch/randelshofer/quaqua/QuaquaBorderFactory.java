/*
 * @(#)QuaquaBorderFactory.java  2.1  2008-10-04
 *
 * Copyright (c) 2005-2008 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.border.CachedImageBevelBorder;
import ch.randelshofer.quaqua.util.*;
import ch.randelshofer.quaqua.border.BackgroundBorderUIResource;
import ch.randelshofer.quaqua.border.ImageBevelBorder;
import java.awt.*;
import javax.swing.border.*;
import java.awt.image.*;
/**
 * Creates an ImageBevelBorder instance optimized for this JVM.
 *
 * @author  Werner Randelshofer
 * @version 2.1 2008-10-04 The factory didn't always create a border which 
 * is an instance of UIResource.
 * <br>2.0 2007-10-30 Dropped support for Java 1.3.
 * <br>1.3 2005-01-04 Method createSmallSquareBorder added.
 * <br>1.2 2005-11-30 Fixed broken creation of CachedImageBevelBorder.
 * <br>1.1 2005-09-07 Support for caching image bevel borders added.
 * <br>1.0  25 April 2005  Created.
 */
public class QuaquaBorderFactory {
    
    /**
     * Prevent instance creation of the factory.
     */
    private QuaquaBorderFactory() {
        //1.4.2_05
    }
    
    /** Creates a new instance of an ImageBevelBorder optimized for this JVM. */
    public static Border create(Image img, Insets borderInsets) {
        return create(img, borderInsets, borderInsets);
    }
    
    /**
     * Creates a new instance of an ImageBevelBorder with the given image and insets.
     * The image has different insets than the border.
     */
    public static Border create(Image img, Insets imageInsets, Insets borderInsets) {
        return create(img, imageInsets, borderInsets, false, true);
    }
    /**
     * Creates a new instance of an ImageBevelBorder with the given image and insets.
     * The image has different insets than the border.
     */
    public static Border create(Image img, Insets borderInsets, boolean fillContentArea, boolean isCaching) {
        return create(img, borderInsets, borderInsets, fillContentArea, isCaching);
    }
    /**
     * Creates a new instance of an ImageBevelBorder with the given image and insets.
     * The image has different insets than the border.
     */
    public static Border create(Image img, Insets imageInsets, Insets borderInsets, boolean fillContentArea) {
        return create(img, imageInsets, borderInsets, fillContentArea, true);
    }
    /**
     * Creates a new instance of an ImageBevelBorder with the given image and insets.
     * The image has different insets than the border.
     */
    public static Border create(Image img, Insets imageInsets, Insets borderInsets, boolean fillContentArea, boolean isCaching) {
        if (isCaching) {
            return new CachedImageBevelBorder.UIResource(img, imageInsets, borderInsets, fillContentArea);
        } else {
            return new ImageBevelBorder.UIResource(img, imageInsets, borderInsets, fillContentArea);
        }
    }
    /**
     * Creates a new instance of a border for square buttons.
     */
    public static Border createSquareButtonBorder() {
        return new Quaqua14SquareButtonBorder();
    }
    /**
     * Creates a new instance of a border for placard buttons.
     */
    public static Border createPlacardButtonBorder() {
        return new Quaqua14PlacardButtonBorder();
    }
    public static Border create(String location, Insets borderInsets, boolean fill) {
        return create(QuaquaIconFactory.createImage(location), borderInsets, borderInsets, fill, false);
    }
    public static Border create(String location, Insets imageInsets, Insets borderInsets, boolean fill) {
        return create(QuaquaIconFactory.createImage(location), imageInsets, borderInsets, fill, false);
    }
    public static Border createBackgroundBorder(String location, Insets imageInsets, Insets borderInsets, boolean fill) {
        return new BackgroundBorderUIResource(create(QuaquaIconFactory.createImage(location), imageInsets, borderInsets, fill, false));
    }
    
    /**
     * Creates an array of ImageBevelBorders.
     *
     * @param location URL of the image that contains the border images.
     * @param insets Insets of the borders.
     * @param count Number of borders to generate.
     * @param horizontal True, if the image is to be split horizontally to get
     * the individual image of each border. If set to false, the image is split
     * vertically.
     */
    public static Object create(String location, Insets insets, int count, boolean horizontal) {
        return create(location, insets, count, horizontal, true, true);
    }
    public static Object create(String location, Insets insets, int count, boolean horizontal, boolean fill, boolean isCaching) {
        BufferedImage[] images = Images.split(
                QuaquaIconFactory.createImage(location),
                count, horizontal
                );
        Border[] borders = new Border[count];
        for (int i=0; i < count; i++) {
            borders[i] = create(images[i], insets, insets, fill, isCaching);
        }
        return borders;
    }
    
    public static Border createButtonBorder(String type) {
        return new BackgroundBorderUIResource(new QuaquaButtonBorder("push"));
    }
}
