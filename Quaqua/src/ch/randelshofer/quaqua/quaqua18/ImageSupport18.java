package ch.randelshofer.quaqua.quaqua18;

import ch.randelshofer.quaqua.ImageSupport;

import java.awt.*;

/**
 * Java 1.8 support for sliced and scaled images. Supports HiDPI mode.
 */

public class ImageSupport18
	extends ImageSupport
{
	public Image createSlice(Image source, int x, int y, int w, int h) {
		if (w == 0 || h == 0) {
			return null;
		}

		if (source instanceof sun.awt.image.MultiResolutionToolkitImage) {
			sun.awt.image.MultiResolutionToolkitImage im = (sun.awt.image.MultiResolutionToolkitImage) source;
			Image hiRes = im.getResolutionVariant();
			Image loSlice = basicCreateSlice(im, x, y, w, h);
			Image hiSlice = basicCreateSlice(hiRes, x * 2, y * 2, w * 2, h * 2);
			return new MyMultiResolutionImage(loSlice, hiSlice);
		}

		return basicCreateSlice(source, x, y, w, h);
	}

	@Override
	public Image createScaled(Image source, int w, int h) {
		if (w == 0 || h == 0) {
			return null;
		}

		if (source instanceof sun.awt.image.MultiResolutionToolkitImage) {
			sun.awt.image.MultiResolutionToolkitImage im = (sun.awt.image.MultiResolutionToolkitImage) source;
			Image hiRes = im.getResolutionVariant();
			Image loSlice = basicCreateScaled(im, w, h);
			Image hiSlice = basicCreateScaled(hiRes, w * 2, h * 2);
			return new MyMultiResolutionImage(loSlice, hiSlice);
		}

		return basicCreateScaled(source, w, h);
	}

	@Override
	public Image createImage(Image basic, Image variant) {
		return variant != null ? new MyMultiResolutionImage(basic, variant) : basic;
	}

	/*
	  We need a custom policy for selecting the image variant because the standard policy can choose the variant image
	  when a reduction would be needed along one axis, and that produces a poorer result that the basic image.
	*/

	private static class MyMultiResolutionImage
		extends sun.awt.image.MultiResolutionToolkitImage {
		private MyMultiResolutionImage(Image lowResolutionImage, Image resolutionVariant) {
			super(lowResolutionImage, resolutionVariant);
		}

		@Override
		public Image getResolutionVariant(int width, int height) {
			Image variant = getResolutionVariant();
			int variantWidth = variant.getWidth(null);
			int variantHeight = variant.getHeight(null);
			if (variantWidth > 0 && variantHeight > 0 && width >= variantWidth && height >= variantHeight) {
				return variant;
			} else {
				return this;
			}
		}
	}
}
