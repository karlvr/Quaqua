/*
 * @(#)QuaquaIconFactory.java  4.0.1 2007-09-08
 *
 * Copyright (c) 2005-2007 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.icon.ShiftedIcon;
import ch.randelshofer.quaqua.osx.Application;
import ch.randelshofer.quaqua.util.*;
import ch.randelshofer.quaqua.icon.ButtonFocusIcon;
import ch.randelshofer.quaqua.icon.ButtonStateIcon;
import ch.randelshofer.quaqua.icon.FrameButtonStateIcon;
import ch.randelshofer.quaqua.icon.OverlayIcon;
import ch.randelshofer.quaqua.icon.SliderThumbIcon;
import java.net.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.plaf.*;
//import javax.imageio.*;
//import javax.imageio.stream.*;
import java.io.*;
import java.util.*;
import ch.randelshofer.quaqua.ext.batik.ext.awt.image.codec.tiff.*;
import ch.randelshofer.quaqua.ext.batik.ext.awt.image.codec.util.*;

/**
 * QuaquaIconFactory.
 *
 * @author  Werner Randelshofer, Christopher Atlan
 * @version 4.0.1 2007-09-08 Option pane icons were empty when Quaqua ran
 * in a restriced Java WebStart sandbox. 
 * <br>4.0 2007-04-28 Removed Java-Cocoa code.
 * <br>3.2.1 2007-02-23 Don't pop an autorelease pool if its identifier is 0.
 * <br>3.2 2007-01-05 Issue #1: Changed LazyOptionPaneIcon to load image
 * asynchronously before paintIcon is invoked.
 * <br>3.1 2006-12-24 by Karl von Randow: Use Images class to create artwork.
 * <br>3.0.2 2006-11-01 Use Graphics2D.drawImage() to scale application
 * image icon instead of using Image.getScaledInstance().
 * <br>3.0.1 2006-05-14 Application icon was unnecessarily created multiple
 * times.
 * <br>3.0 2006-05-12 Added support for file icon images. Renamed some
 * methods.
 * <br>2.1 2006-02-14 Added method createFrameButtonStateIcon.
 * <br>2.0 2006-02-12 Added methods createApplicationIcon, compose,
 * createOptionPaneIcon. These methods were contributed by Christopher Atlan.
 * <br>1.0 December 4, 2005 Created.
 */
public class QuaquaIconFactory {
    private static BufferedImage applicationImage64;
    private static BufferedImage applicationImage32;
    
    /**
     * Lazy option pane icon.
     * The creation of an option pane icon is a potentially slow operation.
     * This icon class will load the icon image in a worker thread and paint it,
     * when it is ready.
     */
    private static class LazyOptionPaneIcon implements Icon {
        private ImageIcon realIcon;
        private int messageType;
        private ch.randelshofer.quaqua.util.SwingWorker worker;
        
        public LazyOptionPaneIcon(final int messageType) {
            this.messageType = messageType;
            worker = new ch.randelshofer.quaqua.util.SwingWorker() {
                public Object construct() {
                    try {
                        switch (messageType) {
                            case JOptionPane.WARNING_MESSAGE :
                                return createWarningIcon();
                            case JOptionPane.ERROR_MESSAGE :
                                return createErrorIcon();
                            default :
                                return createApplicationIcon();
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                        return null;
                    }
                }
            };
            worker.start();
        }
        
        public int getIconHeight() {
            return 64;
        }
        
        public int getIconWidth() {
            return 64;
        }
        
        public void paintIcon(final Component c, Graphics g, int x, int y) {
            if (realIcon == null) {
                realIcon = (ImageIcon) worker.get();
            }
            if (realIcon != null) {
                realIcon.paintIcon(c, g, x, y);
            }
        }
    }
    
    /**
     * Prevent instance creation.
     */
    private QuaquaIconFactory() {
    }
    
    public static URL getResource(String location) {
        URL url = QuaquaIconFactory.class.getResource(location);
        if (url == null) {
            throw new InternalError("image resource missing: "+location);
        }
        return url;
    }
    
    public static Image createImage(String location) {
        return Images.createImage(QuaquaIconFactory.class, location);
    }
    public static Image createImage(Class baseClass, String location) {
        return Images.createImage(baseClass, location);
    }
    public static Image createBufferedImage(String location) {
        return Images.toBufferedImage(createImage(location));
    }
    
    public static Icon[] createIcons(String location, int count, boolean horizontal) {
        Icon[] icons = new Icon[count];
        
        BufferedImage[] images = Images.split(
                (Image) createImage(location),
                count, horizontal
                );
        
        for (int i=0; i < count; i++) {
            icons[i] = new IconUIResource(new ImageIcon(images[i]));
        }
        return icons;
    }
    
    public static Icon createIcon(String location, int count, boolean horizontal, int index) {
        return createIcons(location, count, horizontal)[index];
    }
    
    
    public static Icon createButtonStateIcon(String location, int states) {
        return new ButtonStateIcon(
                (Image) createImage(location),
                states, true
                );
    }
    public static Icon createButtonStateIcon(String location, int states, Point shift) {
        return new ShiftedIcon(
                new ButtonStateIcon(
                (Image) createImage(location),
                states, true
                ),
                shift
                );
    }
    public static Icon createButtonStateIcon(String location, int states, Rectangle shift) {
        return new ShiftedIcon(
                new ButtonStateIcon(
                (Image) createImage(location),
                states, true
                ),
                shift
                );
    }
    public static Icon createFrameButtonStateIcon(String location, int states) {
        return new FrameButtonStateIcon(
                (Image) createImage(location),
                states, true
                );
    }
    
    /**
     * Creates a button state icon overlaid with a button focus icon.
     */
    public static Icon createOverlaidButtonStateIcon(
            String location1, int states1,
            String location2, int states2,
            Rectangle layoutRect
            ) {
        return new IconUIResource(
                new VisuallyLayoutableIcon(
                new OverlayIcon(
                createButtonStateIcon(location1, states1),
                createButtonFocusIcon(location2, states2)
                ), layoutRect)
                );
    }
    
    public static Icon createButtonFocusIcon(String location, int states) {
        return new ButtonFocusIcon(
                (Image) createImage(location),
                states, true
                );
    }
    
    public static Icon createSliderThumbIcon(String location) {
        return new SliderThumbIcon(createImage(location), 6, true);
    }
    
    public static Icon createIcon(Class baseClass, String location) {
        return new ImageIcon(createImage(baseClass, location));
    }
    public static Icon createIcon(Class baseClass, String location, Point shift) {
        return new ShiftedIcon(
                new ImageIcon(createImage(baseClass, location)),
                shift
                );
    }
    public static Icon createIcon(Class baseClass, String location, Rectangle shiftAndSize) {
        return new ShiftedIcon(
                new ImageIcon(createImage(baseClass, location)),
                shiftAndSize
                );
    }
    
    public static Icon createOptionPaneIcon(int messageType) {
        return new LazyOptionPaneIcon(messageType);
    }
    
    private static ImageIcon createApplicationIcon() {
        // Workaround for a bug in method getScaledInstance() in Apple's Java VM.
        // Instead of using getScaledInstance(), we create a temporary image
        // and do the scaling using drawImage().
        
        /*
        BufferedImage image = getApplicationIconImage();
        return new ImageIcon(image.getScaledInstance(64, 64, Image.SCALE_SMOOTH));
         */
        /*
        BufferedImage result = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        g.drawImage(getApplicationIconImage(), 0, 0, 64, 64, null);
        g.dispose();
        return new ImageIcon(result);
         */
        return new ImageIcon(getApplicationIconImage());
    }
    
    private static ImageIcon createWarningIcon() {
        return composeOptionPaneIcon(UIManager.getString("OptionPane.warningIconResource"));
    }
    private static ImageIcon createErrorIcon() {
        return composeOptionPaneIcon(UIManager.getString("OptionPane.errorIconResource"));
    }
    private static ImageIcon composeOptionPaneIcon(String resource) {
        BufferedImage result = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        
        
        BufferedImage warningImage = Images.toBufferedImage(
                Images.createImage(
                QuaquaIconFactory.class.getResource(resource)
                )
                );
        g.drawImage(warningImage, 0, 0, 58, 58, null);
        
        BufferedImage appImage = Application.getIconImage(32);
        g.drawImage(appImage, 32, 32, 32, 32, null);
        
        
        g.dispose();
        return new ImageIcon(result);
    }
    
    /**
     * Gets the application image. This is a buffered image of size 128x128.
     * If the Cocoa Java bridge and the ImageIO API is present, this will get
     * the image from the OS X application bundle.
     * In all other cases this will return a default application image.
     */
    public static BufferedImage getApplicationIconImage() {
        if (applicationImage64 == null) {
            applicationImage64 = Application.getIconImage(64);
        }
        return applicationImage64;
    }
}
