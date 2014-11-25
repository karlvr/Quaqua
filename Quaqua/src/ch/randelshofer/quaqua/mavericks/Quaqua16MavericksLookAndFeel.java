/*
 * Copyright (c) 2014 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement. For details see accompanying license terms.
 */
package ch.randelshofer.quaqua.mavericks;

import ch.randelshofer.quaqua.QuaquaManager;
import ch.randelshofer.quaqua.mountainlion.Quaqua16MountainLionLookAndFeel;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

/**
 * Quaqua look and feel for Mavericks.
 */
public class Quaqua16MavericksLookAndFeel extends Quaqua16MountainLionLookAndFeel {

	public Quaqua16MavericksLookAndFeel() {
		super();
	}

	protected Quaqua16MavericksLookAndFeel(String className) {
		super(className);
	}

    @Override
    public String getDescription() {
        return "The Quaqua Mavericks Look and Feel "
                + QuaquaManager.getVersion();
    }

    @Override
    public String getName() {
        return "Quaqua Mavericks";
    }

    @Override
    protected void initDesignDefaults(UIDefaults table) {

        super.initDesignDefaults(table);

        Object[] uiDefaults = getConfiguredDesignDefaults();

        putDefaults(table, uiDefaults);
    }

    public static Object[] getConfiguredDesignDefaults() {

        Object[] uiDefaults = {
            "FileChooser.listView.extraColumnTextColor", new ColorUIResource(80, 80, 80),
            "FileChooser.listView.headerColor", new ColorUIResource(100, 100, 120),
            "FileChooser.listView.headerBorderColor", new ColorUIResource(190, 190, 190),
            //
        };

        return uiDefaults;
    }
}
