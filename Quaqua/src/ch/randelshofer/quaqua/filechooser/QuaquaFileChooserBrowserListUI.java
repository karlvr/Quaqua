/*
 * Copyright (c) 2014 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.filechooser;

import ch.randelshofer.quaqua.QuaquaListUI;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.io.File;

/**
    A UI for the lists in a file chooser browser.

    The primary difference from an ordinary JList is that selection is restricted to items that are acceptable
    selections to the file chooser. There are two exceptions: (1) A traversable directory may be selected in order to
    display the contents of that directory. (2) In a Save dialog, an item that is not a traversable directory may be
    clicked to transfer the item name into the file name text field.

    An obscure difference is that unacceptable items participate in defining the contiguous regions that are deselected
    by a Shift-click.
*/

public class QuaquaFileChooserBrowserListUI extends QuaquaListUI {

    private JFileChooser fc;
    private QuaquaFileChooserListMouseBehavior.FileSelectionHandler fileSelectionHandler;
    private QuaquaFileChooserListMouseBehavior mouseBehavior;

    public QuaquaFileChooserBrowserListUI(JFileChooser fc) {
        this.fc = fc;
    }

    public void setFileSelectionHandler(QuaquaFileChooserListMouseBehavior.FileSelectionHandler h) {
        fileSelectionHandler = h;
        if (mouseBehavior != null) {
            mouseBehavior.setFileSelectionHandler(h);
        }
    }

    @Override
    protected void installKeyboardActions() {
        super.installKeyboardActions();

        // Avoid conflict with Cmd-Shift-A in the file chooser
        InputMap map = list.getInputMap(JComponent.WHEN_FOCUSED).getParent();
        KeyStroke ks = KeyStroke.getKeyStroke("shift meta A");
        Object v = map.get(ks);
        if (v != null && v.equals("clearSelection")) {  // defined in BasicQuaquaNativeLookAndFeel List.focusInputMap
            InputMap newMap = new InputMap();
            newMap.setParent(map);
            newMap.put(ks, "selectApplicationsFolder"); // dummy name for now
            SwingUtilities.replaceUIInputMap(list, JComponent.WHEN_FOCUSED, newMap);
        }
    }

    @Override
    protected MouseInputListener createMouseInputListener() {
        if (mouseBehavior == null) {
            mouseBehavior = new QuaquaFileChooserListMouseBehavior(fc, list);
            mouseBehavior.setFileSelectionHandler(fileSelectionHandler);
        }
        return mouseBehavior;
    }
}
