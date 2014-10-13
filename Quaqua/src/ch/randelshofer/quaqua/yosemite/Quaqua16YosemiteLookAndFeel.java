package ch.randelshofer.quaqua.yosemite;

import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;

import ch.randelshofer.quaqua.QuaquaManager;
import ch.randelshofer.quaqua.color.InactivatableColorUIResource;
import ch.randelshofer.quaqua.mavericks.Quaqua16MavericksLookAndFeel;

public class Quaqua16YosemiteLookAndFeel extends Quaqua16MavericksLookAndFeel {

    @Override
    public String getDescription() {
        return "The Quaqua Yosemite Look and Feel "
                + QuaquaManager.getVersion();
    }

    @Override
    public String getName() {
        return "Quaqua Yosemite";
    }

	@Override
	protected Object toolBarTitleBackground(UIDefaults table) {
		final String javaVersion = QuaquaManager.getProperty("java.version", "");
        if (javaVersion.startsWith("1.5") || javaVersion.startsWith("1.6")) {
            return table.get("control");
        } else {
            return new InactivatableColorUIResource(new ColorUIResource(211, 211, 211), new ColorUIResource(246, 246, 246));
        }
	}
    
}
