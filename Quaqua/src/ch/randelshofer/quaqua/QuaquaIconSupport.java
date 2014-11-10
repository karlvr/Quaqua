package ch.randelshofer.quaqua;

import javax.swing.*;
import java.awt.*;

/**

 */

public interface QuaquaIconSupport {
    Icon createNativeIcon(String path, int width, int height);

    Image createNativeImage(String path, int width, int height);

    Icon createNativeSidebarIcon(String path, int width, int height, Color color, Color selectedColor);

    Image createImage(int width, int height, ImageProvider provider);
}
