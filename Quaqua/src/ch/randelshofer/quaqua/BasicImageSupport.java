package ch.randelshofer.quaqua;

import java.awt.*;

/**
    Basic support for painting using nine-slice images.
*/

public class BasicImageSupport
	extends ImageSupport
{
	public Image createSlice(Image source, int x, int y, int w, int h) {
		return basicCreateSlice(source, x, y, w, h);
	}

	@Override
	public Image createScaled(Image source, int width, int height) {
		return basicCreateScaled(source, width, height);
	}
}
