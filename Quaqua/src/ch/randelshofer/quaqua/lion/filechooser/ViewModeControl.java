/*
 * Copyright (c) 2014 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement. For details see accompanying license terms.
 */
package ch.randelshofer.quaqua.lion.filechooser;

import ch.randelshofer.quaqua.QuaquaNativeButtonBorder;
import ch.randelshofer.quaqua.QuaquaToggleButtonUI;
import ch.randelshofer.quaqua.osx.OSXConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

/**
 * Control to select list or column view in the file chooser.
 */
public class ViewModeControl extends ch.randelshofer.quaqua.filechooser.ViewModeControl {

    private static Image listViewImage;
    private static Image columnViewImage;

    private ButtonGroup group;
    private JToggleButton listViewButton;
    private JToggleButton columnViewButton;

    private int selectedViewMode = -1;  // an invalid value to trigger initialization

    public ViewModeControl () {
        initComponents();
        setFocusable(false);
        setSelectedViewMode(COLUMN_VIEW);
    }

    @Override
    public synchronized void addKeyListener(KeyListener l) {
        listViewButton.addKeyListener(l);
        columnViewButton.addKeyListener(l);
    }

    @Override
    public void setSelectedViewMode(int mode) {
        if (mode != selectedViewMode && (mode == LIST_VIEW || mode == COLUMN_VIEW)) {
            selectedViewMode = mode;
            int count = getComponentCount();
            String action = "" + mode;
            for (int i = 0; i < count; i++) {
                Component c = getComponent(i);
                if (c instanceof JToggleButton) {
                    JToggleButton b = (JToggleButton) c;
                    if (action.equals(b.getActionCommand())) {
                        b.setSelected(true);
                    }
                }
            }
            selectedViewModeChanged();
        }
    }

    @Override
    public int getSelectedViewMode() {
        return selectedViewMode;
    }

    private void initComponents() {

        if (listViewImage == null) {
            listViewImage = Toolkit.getDefaultToolkit().getImage("NSImage://NSListViewTemplate");
        }

        if (columnViewImage == null) {
            columnViewImage = Toolkit.getDefaultToolkit().getImage("NSImage://NSColumnViewTemplate");
        }

        group = new ButtonGroup();

        ImageIcon listViewIcon = new ImageIcon(listViewImage);
        ImageIcon columnViewIcon = new ImageIcon(columnViewImage);
        listViewButton = createButton(listViewIcon, "first", LIST_VIEW);
        columnViewButton = createButton(columnViewIcon, "last", COLUMN_VIEW);

        add(listViewButton);
        add(columnViewButton);

        setLayout(new FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
    }

    protected JToggleButton createButton(ImageIcon ic, String position, final int viewMode) {
        JToggleButton b = new JToggleButton(ic);
        b.putClientProperty("JButton.buttonType", "segmented");
        b.putClientProperty("JButton.segmentPosition", position);
        // The following explicit assignments support the file chooser only JAR
        b.setUI(new QuaquaToggleButtonUI());
        b.setOpaque(true);
        b.setBorder(new QuaquaNativeButtonBorder());
        b.setFocusable(OSXConfiguration.isFullKeyboardAccess());

        Insets s;
        if (position.equals("first")) {
            s = new Insets(7, 12, 7, 8);
        } else {
            s = new Insets(7, 7, 7, 12);
        }

        b.putClientProperty("Quaqua.Border.insets", s);

        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectedViewMode(viewMode);
            }
        });

        b.setActionCommand("" + viewMode);

        group.add(b);
        return b;
    }
}
