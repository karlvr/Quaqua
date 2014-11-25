/*
 * Copyright (c) 2014 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.leopard.filechooser;

import ch.randelshofer.quaqua.JBrowser;
import ch.randelshofer.quaqua.QuaquaManager;
import ch.randelshofer.quaqua.filechooser.AbstractFileChooserBrowserListUI;
import ch.randelshofer.quaqua.filechooser.QuaquaFileChooserBrowserListUI;
import ch.randelshofer.quaqua.filechooser.QuaquaFileChooserListMouseBehavior;

import javax.swing.*;
import javax.swing.plaf.ListUI;
import java.io.File;
import java.lang.reflect.Constructor;

/**
 * The browser in a file chooser. Implements special behavior for clicking on an ordinary file in a Save panel.
 */
public class QuaquaFileChooserBrowser extends JBrowser {
    protected JFileChooser fc;

    public QuaquaFileChooserBrowser(JFileChooser fc) {
        this.fc = fc;
    }

    @Override
    protected ListUI getColumnListUI(ListUI basicUI) {

        AbstractFileChooserBrowserListUI ui = null;

        Object uiClassName = UIManager.get("FileChooser.browserListUIClass");
        if (uiClassName instanceof String) {
            try {
                Class c = Class.forName((String) uiClassName);
                Constructor cons = c.getConstructor(JFileChooser.class);
                ui = (AbstractFileChooserBrowserListUI) cons.newInstance(fc);
            } catch (Exception ex) {
                System.err.println("Unable to instantiate " + uiClassName);
            }
        }

        if (ui == null) {
            ui = new QuaquaFileChooserBrowserListUI(fc);
        }

        ui.setFileSelectionHandler(new QuaquaFileChooserListMouseBehavior.FileSelectionHandler() {
            @Override
            public void fileSelected(File f) {
                QuaquaFileChooserBrowser.this.fileSelectedInSavePanel(f);
            }
        });

        return (ListUI) ui;
    }

    @Override
    protected JScrollPane createScrollPane(JComponent c, int columnIndex) {
        JScrollPane sp = super.createScrollPane(c, columnIndex);
        QuaquaManager.updateNestedComponentUI(sp);
        sp.setBorder(null);
        sp.setFocusable(false);
        sp.getVerticalScrollBar().setFocusable(false);
        sp.getHorizontalScrollBar().setFocusable(false);
        return sp;
    }

    protected void fileSelectedInSavePanel(File f) {
    }
}
