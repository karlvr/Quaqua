package com.xk72.swing;

import java.awt.image.ImageFilter;

import javax.swing.Icon;

/**
 * A FilterableIcon provides a method for creating a filtered version of the icon. This enables
 * the icon to control the way it is rendered, such as rendering on a retina screen.
 * @author karlvr
 *
 */
public interface FilterableIcon extends Icon {

	public Icon filter(ImageFilter filter);

}
