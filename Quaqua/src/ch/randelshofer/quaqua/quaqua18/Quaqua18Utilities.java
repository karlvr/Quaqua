package ch.randelshofer.quaqua.quaqua18;

import javax.swing.*;

/**
 * Utilities based on Aqua look and feel in Java 1.8
 */

public class Quaqua18Utilities {

    // Duplicates method in AquaFocusHandler
    public static boolean isActive(final JComponent c) {
        if (c == null) return true;
        final Object activeObj = c.getClientProperty("Frame.active");
        if (Boolean.FALSE.equals(activeObj)) return false;
        return true;
    }
}
