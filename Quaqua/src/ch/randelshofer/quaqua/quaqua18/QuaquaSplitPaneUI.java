package ch.randelshofer.quaqua.quaqua18;

import com.apple.laf.AquaSplitPaneUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;

/**
 * Custom split pane UI. Supports Yosemite look and HiDPI.
 */
public class QuaquaSplitPaneUI extends AquaSplitPaneUI {

    public static ComponentUI createUI(JComponent x) {
        return new QuaquaSplitPaneUI();
    }

    @Override
    public BasicSplitPaneDivider createDefaultDivider() {
        return new QuaquaSplitPaneDividerUI(this);
    }
}
