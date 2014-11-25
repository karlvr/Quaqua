package ch.randelshofer.quaqua.quaqua18;

import com.apple.laf.AquaComboBoxUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

/**
 * Custom ComboBox UI.
 */
public class QuaquaComboBoxUI extends AquaComboBoxUI {

    public static ComponentUI createUI(JComponent c) {
        return new QuaquaComboBoxUI();
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        comboBox.setMaximumRowCount(UIManager.getInt("ComboBox.maximumRowCount"));
    }
}
