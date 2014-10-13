/*
 * @(#)Quaqua16MountainLionLookAndFeel.java
 *
 * Copyright (c) 2011-2013 Werner Randelshofer, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package ch.randelshofer.quaqua.mountainlion;

import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;

import ch.randelshofer.quaqua.QuaquaManager;
import ch.randelshofer.quaqua.color.InactivatableColorUIResource;
import ch.randelshofer.quaqua.lion.Quaqua16LionLookAndFeel;

/**
 * {@code Quaqua16MountainLionLookAndFeel}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Quaqua16MountainLionLookAndFeel extends Quaqua16LionLookAndFeel {

	public Quaqua16MountainLionLookAndFeel() {
		super();
	}

	protected Quaqua16MountainLionLookAndFeel(String className) {
		super(className);
	}

    @Override
    public String getDescription() {
        return "The Quaqua Mountain Lion Look and Feel "
                + QuaquaManager.getVersion()
                + " for J2SE 6 and above";
    }

    @Override
    public String getName() {
        return "Quaqua Mountain Lion";
    }

	@Override
	protected void initDesignDefaults(UIDefaults table) {
		super.initDesignDefaults(table);

        Object toolBarTitleBackground = toolBarTitleBackground(table);

        Object[] uiDefaults = new Object[] {
                "control", toolBarTitleBackground,
                "ToolBar.title.background", toolBarTitleBackground,
        };

        putDefaults(table, uiDefaults);
    }

	protected Object toolBarTitleBackground(UIDefaults table) {
        final String javaVersion = QuaquaManager.getProperty("java.version", "");
        if (javaVersion.startsWith("1.5") || javaVersion.startsWith("1.6")) {
            return table.get("control");
        } else {
            return new InactivatableColorUIResource(new ColorUIResource(222, 222, 222), new ColorUIResource(246, 246, 246));
        }
	}
}
