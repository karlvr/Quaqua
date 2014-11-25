package ch.randelshofer.quaqua.quaqua18;

import com.apple.laf.AquaUtilControlSize;

import javax.swing.*;
import java.beans.PropertyChangeEvent;

/**
 *
 */
public class QuaquaUtilControlSize extends AquaUtilControlSize {

    public static void applyComponentSize(final JComponent c) {
        Object o = c.getClientProperty(CLIENT_PROPERTY_KEY);
        if (o != null) {
            PropertySizeListener l = getSizeListener();
            PropertyChangeEvent e = new PropertyChangeEvent(c, CLIENT_PROPERTY_KEY, null, o);
            l.propertyChange(e);
        }
    }
}
