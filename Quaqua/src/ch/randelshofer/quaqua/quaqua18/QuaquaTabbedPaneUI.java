package ch.randelshofer.quaqua.quaqua18;

import ch.randelshofer.quaqua.SlicedImagePainter;
import com.apple.laf.AquaTabbedPaneUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * Customizes the Aqua tabbed pane UI to draw a border using a custom image. Needed to draw a proper border on Yosemite.
 * Not sure why that doesn't happen automatically using the Aqua native painter...
 */

public class QuaquaTabbedPaneUI extends AquaTabbedPaneUI {

    private SlicedImagePainter painter;

    public static ComponentUI createUI(JComponent b)    {
        return new QuaquaTabbedPaneUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        painter = (SlicedImagePainter) UIManager.get("TabbedPane.slicedImagePainter");
    }

    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {

        if (painter != null) {

            final int TAB_BORDER_INSET = 9;

            final int width = tabPane.getWidth();
            final int height = tabPane.getHeight();
            final Insets insets = tabPane.getInsets();

            int x = insets.left;
            int y = insets.top;
            int w = width - insets.right - insets.left;
            int h = height - insets.top - insets.bottom;

            switch (tabPlacement) {
                case TOP:
                    y += TAB_BORDER_INSET;
                    h -= TAB_BORDER_INSET;
                    break;
                case BOTTOM:
                    h -= TAB_BORDER_INSET;
                    break;
                case LEFT:
                    x += TAB_BORDER_INSET;
                    w -= TAB_BORDER_INSET;
                    break;
                case RIGHT:
                    w -= TAB_BORDER_INSET;
                    break;
            }

            painter.paint(g, x, y, w, h);

        } else {
            super.paintContentBorder(g, tabPlacement, selectedIndex);
        }
    }
}
