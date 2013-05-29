/*
 * @(#)QuaquaBorderFactory.java  
 *
 * Copyright (c) 2005-2013 Werner Randelshofer, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.border.QuaquaNativeButtonStateBorder;
import ch.randelshofer.quaqua.border.CachedImageBevelBorder;
import ch.randelshofer.quaqua.util.*;
import ch.randelshofer.quaqua.border.BackgroundBorderUIResource;
import ch.randelshofer.quaqua.border.ButtonStateBorder;
import ch.randelshofer.quaqua.border.FocusedBorder;
import ch.randelshofer.quaqua.border.ImageBevelBorder;
import ch.randelshofer.quaqua.border.QuaquaNativeImageBevelBorder;
import ch.randelshofer.quaqua.osx.OSXAquaPainter;
import java.awt.*;
import javax.swing.border.*;
import java.awt.image.*;

/**
 * Creates an ImageBevelBorder instance optimized for this JVM.
 *
 * @author  Werner Randelshofer
 * @version $Id$
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
        return create(img, imageInsets, borderInsets, true, null, true);
    }

    /**
     * Creates a new instance of an ImageBevelBorder with the given image and insets.
     * The image has different insets than the border.
     */
    public static Border create(Image img, Insets borderInsets, boolean fillContentArea, boolean isCaching) {
        return create(img, borderInsets, borderInsets, fillContentArea, null, isCaching);
    }

    /**
     * Creates a new instance of an ImageBevelBorder with the given image and insets.
     * The image has different insets than the border.
     */
    public static Border create(Image img, Insets imageInsets, Insets borderInsets, boolean fillContentArea) {
        return create(img, imageInsets, borderInsets, fillContentArea, null, true);
    }

    /**
     * Creates a new instance of an ImageBevelBorder with the given image and insets.
     * The image has different insets than the border.
     */
    public static Border create(Image img, Insets imageInsets, Insets borderInsets, boolean fillContentArea, Color fillColor, boolean isCaching) {
        if (isCaching) {
            return new CachedImageBevelBorder.UIResource(img, imageInsets, borderInsets, fillContentArea);
        } else {
            return new ImageBevelBorder.UIResource(img, imageInsets, borderInsets, fillContentArea, fillColor);
        }
    }
    /**
     * Creates a new instance of NativeImageBevelBorder with the given widget and insets.
     * The image has different insets than the border.
     */
    public static Border createNativeImageBevelBorder(OSXAquaPainter.Widget widget, Insets painterInsets, Insets imageBevel, Insets borderInsets, boolean fillContentArea) {
            return new QuaquaNativeImageBevelBorder.UIResource(widget, painterInsets,imageBevel, borderInsets, fillContentArea);
    }

    /**
     * Creates a new instance of a border for square buttons.
     */
    public static Border createSquareButtonBorder() {
        return new QuaquaSquareButtonBorder();
    }

    /**
     * Creates a new instance of a border for placard buttons.
     */
    public static Border createPlacardButtonBorder() {
        return new QuaquaPlacardButtonBorder();
    }

    public static Border create(String location, Insets borderInsets, boolean fill) {
        return create(QuaquaIconFactory.createImage(location), borderInsets, borderInsets, fill, null, false);
    }

    public static Border create(String location, Insets imageInsets, Insets borderInsets, boolean fill) {
        return create(QuaquaIconFactory.createImage(location), imageInsets, borderInsets, fill, null, false);
    }

    public static Border create(String location, Insets imageInsets, Insets borderInsets, boolean fill, Color fillColor) {
        return create(QuaquaIconFactory.createImage(location), imageInsets, borderInsets, fill, fillColor, false);
    }
    public static Border create(String location, Rectangle subimage, Insets imageInsets, Insets borderInsets, boolean fill) {
        return create(QuaquaIconFactory.createBufferedImage(location, subimage), imageInsets, borderInsets, fill, null, false);
    }
    public static Border create(String location, Rectangle subimage, Insets imageInsets, Insets borderInsets, boolean fill, Color fillColor) {
        return create(QuaquaIconFactory.createBufferedImage(location, subimage), imageInsets, borderInsets, fill, fillColor, false);
    }

    public static Border createBackgroundBorder(String location, Insets imageInsets, Insets borderInsets, boolean fill) {
        return new BackgroundBorderUIResource(create(QuaquaIconFactory.createImage(location), imageInsets, borderInsets, fill, null, false));
    }

    public static Border createBackgroundBorder(String location, Insets imageInsets, Insets borderInsets, boolean fill, Color fillColor) {
        return new BackgroundBorderUIResource(create(QuaquaIconFactory.createImage(location), imageInsets, borderInsets, fill, fillColor, false));
    }

    public static Border createButtonStateBorder(String location, int tileCount, boolean isTiledHorizontaly,
            Insets imageInsets, Insets borderInsets, boolean fill) {
        return new ButtonStateBorder.UIResource(QuaquaIconFactory.createImage(location), tileCount, isTiledHorizontaly,
                imageInsets, borderInsets, fill);
    }

    public static Border createNativeButtonStateBorder(OSXAquaPainter.Widget widget,
            Insets imageInsets, Insets borderInsets, boolean drawFocusRing) {
        try {
            Border border = new QuaquaNativeButtonStateBorder.UIResource(widget,
                    imageInsets, borderInsets);

            if (drawFocusRing) {
                border = new FocusedBorder.UIResource(border);
            }

            return border;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
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
                count, horizontal);
        Border[] borders = new Border[count];
        for (int i = 0; i < count; i++) {
            borders[i] = create(images[i], insets, insets, fill, null, isCaching);
        }
        return borders;
    }

    public static Border createButtonBorder(String type) {
        return new BackgroundBorderUIResource(new QuaquaButtonBorder("push"));
    }
}
