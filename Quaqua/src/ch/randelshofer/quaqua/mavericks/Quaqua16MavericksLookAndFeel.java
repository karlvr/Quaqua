package ch.randelshofer.quaqua.mavericks;

import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;

import ch.randelshofer.quaqua.QuaquaManager;
import ch.randelshofer.quaqua.color.InactivatableColorUIResource;
import ch.randelshofer.quaqua.mountainlion.Quaqua16MountainLionLookAndFeel;

public class Quaqua16MavericksLookAndFeel extends Quaqua16MountainLionLookAndFeel {

	public Quaqua16MavericksLookAndFeel() {
		super();
	}

	protected Quaqua16MavericksLookAndFeel(String className) {
		super(className);
	}

	@Override
	protected void initDesignDefaults(UIDefaults table) {
		super.initDesignDefaults(table);
		
		String javaVersion = QuaquaManager.getProperty("java.version", "");
		if (javaVersion.startsWith("1.5") || javaVersion.startsWith("1.6")) {
			Object toolBarBackground = table.get("control");
	        
			Object[] uiDefaults = new Object[] {
				"ToolBar.title.background", toolBarBackground
			};
			
			putDefaults(table, uiDefaults);
		} else {
	        Object toolBarBackground = new InactivatableColorUIResource(new ColorUIResource(222, 222, 222), new ColorUIResource(246, 246, 246));
	        
			Object[] uiDefaults = new Object[] {
				"ToolBar.title.background", toolBarBackground
			};
			
			putDefaults(table, uiDefaults);
		}
	}

}
