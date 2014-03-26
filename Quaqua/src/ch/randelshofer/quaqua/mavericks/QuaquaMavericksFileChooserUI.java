/*
 * Copyright (c) 2014 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement. For details see accompanying license terms.
 */
package ch.randelshofer.quaqua.mavericks;

import ch.randelshofer.quaqua.lion.QuaquaLionFileChooserUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * A replacement for the AquaFileChooserUI. Emulates the list and column views of the native Aqua user interface on Mac
 * OS X 10.9 (Mavericks).
 */

public class QuaquaMavericksFileChooserUI extends QuaquaLionFileChooserUI {

    public static ComponentUI createUI(JComponent c) {
        return new QuaquaMavericksFileChooserUI((JFileChooser) c);
    }

    public QuaquaMavericksFileChooserUI(JFileChooser filechooser) {
        super(filechooser);
    }
}
