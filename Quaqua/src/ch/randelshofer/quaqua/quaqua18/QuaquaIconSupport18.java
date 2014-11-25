package ch.randelshofer.quaqua.quaqua18;

import ch.randelshofer.quaqua.ImageProvider;
import ch.randelshofer.quaqua.QuaquaIconFactory;
import ch.randelshofer.quaqua.QuaquaIconSupport;
import ch.randelshofer.quaqua.icon.ListStateIcon;
import ch.randelshofer.quaqua.osx.OSXImageIO;
import ch.randelshofer.quaqua.util.Images;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
    This class provides Java 1.8 specific support for multiresolution images for use on a Retina display.

    This code refers to classes that exist only in specific releases of Java 1.8, such as MultiResolutionImage.

    Better support is coming in Java 1.9...
 */

public class QuaquaIconSupport18 implements QuaquaIconSupport {

    @Override
    public Icon createNativeIcon(String path, int width, int height) {
        Image img = createNativeImage(path, width, height);
        return img != null ? new ImageIcon(img) : null;
    }

    @Override
    public Image createNativeImage(String path, int width, int height) {
        // This code requires Java 1.8 (perhaps specific releases of 1.8)
        // Better support is coming in Java 1.9
        try {
            File f = new File(path);
            Image baseImage = OSXImageIO.read(f, width, height);
            if (baseImage == null) {
                return null;
            }

            Image variantImage = OSXImageIO.read(f, width*2, height*2);
            if (variantImage == null) {
                return baseImage;
            }

            return new MyMultiResolutionImage(baseImage, variantImage);
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Create an image that may support multiple resolutions.
     * @param width The base image width.
     * @param height The base image height.
     * @param provider Provides variant images of specified sizes.
     * @return the image.
     */

    public Image createImage(int width, int height, ImageProvider provider) {
        Image baseImage = provider.getImage(width, height);
        if (baseImage == null) {
            return null;
        }

        Image variantImage = provider.getImage(width * 2, height * 2);
        if (variantImage == null) {
            return baseImage;
        }

        return new MyMultiResolutionImage(baseImage, variantImage);
    }





    public static class MyMultiResolutionImage
        extends Image
        implements sun.awt.image.MultiResolutionImage
    {
        private Image baseImage;
        private int width;
        private int height;
        private Image variantImage;

        public MyMultiResolutionImage(Image baseImage, Image variantImage) {
            this.baseImage = baseImage;
            this.width = baseImage.getWidth(null);
            this.height = baseImage.getHeight(null);
            this.variantImage = variantImage;
        }

        @Override
        public int getWidth(ImageObserver observer) {
            return width;
        }

        @Override
        public int getHeight(ImageObserver observer) {
            return height;
        }

        @Override
        public ImageProducer getSource() {
            return baseImage.getSource();
        }

        @Override
        public Graphics getGraphics() {
            return baseImage.getGraphics();
        }

        @Override
        public Object getProperty(String name, ImageObserver observer) {
            return baseImage.getProperty(name, observer);
        }

        @Override
        public Image getResolutionVariant(int width, int height) {
            return ((width <= this.width && height <= this.height)) ? baseImage : variantImage;
        }

        @Override
        public java.util.List<Image> getResolutionVariants() {
            return Arrays.<Image>asList(baseImage, variantImage);
        }
    }

    @Override
    public Icon createNativeSidebarIcon(String path, int width, int height, Color color, Color selectedColor) {
        Image im = createNativeImage(path, width, height);

        if (im instanceof MyMultiResolutionImage) {
            MyMultiResolutionImage base = (MyMultiResolutionImage) im;

            Image basicBase = createSidebarImage(base.baseImage, color);
            Image basicVariant = createSidebarImage(base.variantImage, color);
            Image basic = new MyMultiResolutionImage(basicBase, basicVariant);

            Image selectedBase = createSidebarImage(base.baseImage, selectedColor);
            Image selectedVariant = createSidebarImage(base.variantImage, selectedColor);
            Image selected = new MyMultiResolutionImage(selectedBase, selectedVariant);

            return new ListStateIcon(new ImageIcon(basic), new ImageIcon(selected));
        } else {
            Image basic = createSidebarImage(im, color);
            Image selected = createSidebarImage(im, selectedColor);
            return new ListStateIcon(new ImageIcon(basic), new ImageIcon(selected));
        }
    }

    /**
     * Create a sidebar icon for Yosemite. Light areas remain transparent. Dark areas are mapped to the specified color.
     */
    public static Image createSidebarImage(Image source, Color color) {
        BufferedImage img = Images.toBufferedImage(source);
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage iconImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g = iconImg.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(img, 0, 0, null);
        g.setComposite(AlphaComposite.SrcIn);
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        g.dispose();
        return iconImg;
    }
}
