package ch.randelshofer.quaqua;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Support for sliced and scaled images.
 */

public abstract class ImageSupport {

    /**
     * Obtain the appropriate sliced image support for this platform.
     */
	public static ImageSupport getImageSupport() {
		try {
			Class cl = Class.forName("sun.awt.image.MultiResolutionToolkitImage");
			Class sc = Class.forName("ch.randelshofer.quaqua.quaqua18.ImageSupport18");
			return (ImageSupport) sc.newInstance();
		} catch (Exception ex) {
		}

		return new BasicImageSupport();
	}

    /**
   	 * Create an image that contains a subregion of the specified image.
   	 */
	public abstract Image createSlice(Image source, int x, int y, int w, int h);

	protected Image basicCreateSlice(Image source, int x, int y, int w, int h) {
		if (w == 0 || h == 0) {
			return null;
		}

		// wait for the image to be loaded, if necessary
		QuaquaUtilities.loadImage(source);

		BufferedImage slice = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D g2d = slice.createGraphics();
		g2d.drawImage(source, 0, 0, w, h, x, y, x + w, y + h, null);
		g2d.dispose();

		return slice;
	}

	/**
	 * Create a scaled image. Eventually, Java will support this natively.
	 */
	public abstract Image createScaled(Image source, int width, int height);

	protected Image basicCreateScaled(Image source, int width, int height) {
		return source.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	}

	/**
	 * Create a possibly multiresolution image.
	 */
	public Image createImage(Image basic, Image variant) {
		return basic;
	}
}
