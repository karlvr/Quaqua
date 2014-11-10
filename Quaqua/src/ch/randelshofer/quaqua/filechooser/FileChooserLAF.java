package ch.randelshofer.quaqua.filechooser;

import ch.randelshofer.quaqua.*;

import javax.swing.*;
import java.awt.*;

/*
    TBD: The current file chooser only look and feel definitions don't really do what they claim to do because they
    set UI defaults that affect other components.
 */

/**
 * A base class for a file chooser only look and feel. The purpose of this base class is to allow the file chooser only
 * look and feel to determine the component UIs installed on components of the file chooser.
 */

public class FileChooserLAF extends LookAndFeelProxy {

    /**
     * Install a custom UI in a nested component of the file chooser, if appropriate. The goal of this method is to
     * support the File Chooser Only library. In the file chooser only library, the full Quaqua look and feel is not
     * installed, so that by default nested components will get pure Aqua UIs. However, in most cases, we actually want
     * the Quaqua component UI. This method figures out what to do.
     */
    public void updateNestedComponentUI(JComponent c) {
        if (c instanceof JScrollPane) {
            JScrollPane p = (JScrollPane) c;
            p.setUI(new QuaquaScrollPaneUI());
        } else if (c instanceof JViewport) {
            JViewport vp = (JViewport) c;
            vp.setUI(new QuaquaViewportUI());   // provides extra stripes to fill the viewport
        } else if (c instanceof JLabel) {
            JLabel l = (JLabel) c;
            l.setUI(new QuaquaLabelUI());
        } else if (c instanceof JPanel) {
            JPanel p = (JPanel) c;
            p.setUI(new QuaquaPanelUI());
        } else if (c instanceof JTextField) {
            JTextField tf = (JTextField) c;
            tf.setUI(new QuaquaTextFieldUI());
        } else if (c instanceof JButton) {
            JButton b = (JButton) c;
            b.setUI(new QuaquaButtonUI());
        } else if (c instanceof JComboBox) {
            JComboBox cb = (JComboBox) c;
            cb.setUI(new QuaquaComboBoxUI());
        } else if (c instanceof JToggleButton) {
            JToggleButton b = (JToggleButton) c;
            b.setUI(new QuaquaToggleButtonUI());
            b.setOpaque(true);
            b.setBorder(new QuaquaNativeButtonBorder());

            Object o = b.getClientProperty("JButton.segmentPosition");
            Insets s;
            if (o != null && o.equals("first")) {
                s = new Insets(7, 12, 7, 8);
            } else {
                s = new Insets(7, 7, 7, 12);
            }

            b.putClientProperty("Quaqua.Border.insets", s);
        }
    }
}
