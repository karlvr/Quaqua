package ch.randelshofer.quaqua.yosemite;

import ch.randelshofer.quaqua.QuaquaManager;
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
    
}
