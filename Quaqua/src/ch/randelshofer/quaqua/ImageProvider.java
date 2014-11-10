package ch.randelshofer.quaqua;

import java.awt.*;

/**
    A provider of variant images. Inspired by MultiResolutionCachedImage in Java 1.9.
*/

public interface ImageProvider {
    Image getImage(int width, int height);
}
