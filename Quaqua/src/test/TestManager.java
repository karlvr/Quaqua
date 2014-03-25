package test;

import ch.randelshofer.quaqua.util.Methods;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.util.Set;

/**
    Support loose coupling between the test program and Quaqua APIs, allowing tests to run without the Quaqua code.
*/

public class TestManager {

    public static String getJavaVersion() {
        return getProperty("java.version");
    }

    public static String getUserHome() {
        return getProperty("user.home");
    }

    public static String getLookAndFeelClassName() {
        try {
            Class quaquaManagerClass = Class.forName("ch.randelshofer.quaqua.QuaquaManager");
            return (String) Methods.invokeStatic(quaquaManagerClass, "getLookAndFeelClassName");
        } catch (Exception ex) {
            try {
                return UIManager.getLookAndFeel().getName();
            } catch (Exception ex1) {
                return null;
            }
        }
    }

    public static String getQuaquaLookAndFeelClassName() {
        try {
            Class quaquaManagerClass = Class.forName("ch.randelshofer.quaqua.QuaquaManager");
            return (String) Methods.invokeStatic(quaquaManagerClass, "getLookAndFeelClassName");
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getQuaquaVersion() {
        try {
            Class quaquaManagerClass = Class.forName("ch.randelshofer.quaqua.QuaquaManager");
            return (String) Methods.invokeStatic(quaquaManagerClass, "getVersion");
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getProperty(String key) {
        try {
            Class quaquaManagerClass = Class.forName("ch.randelshofer.quaqua.QuaquaManager");
            return (String) Methods.invokeStatic(quaquaManagerClass, "getProperty", String.class, key);
        } catch (Exception ex) {
            try {
                return System.getProperty(key);
            } catch (Exception ex1) {
                return null;
            }
        }
    }

    public static void setIncludedUIs(Set includes) {
        try {
            Class quaquaManagerClass = Class.forName("ch.randelshofer.quaqua.QuaquaManager");
            Methods.invokeStatic(quaquaManagerClass, "setIncludedUIs", Set.class, includes);
        } catch (Exception ex) {
        }
    }

    public static void setExcludedUIs(Set includes) {
        try {
            Class quaquaManagerClass = Class.forName("ch.randelshofer.quaqua.QuaquaManager");
            Methods.invokeStatic(quaquaManagerClass, "setExcludedUIs", Set.class, includes);
        } catch (Exception ex) {
        }
    }

    public static Font getSmallDialogFont() {
        return new Font("Dialog", Font.PLAIN, 11);
    }

    public static FileSystemView getQuaquaFileSystemView() {
        try {
            Class quaquaFileSystemViewClass = Class.forName("ch.randelshofer.quaqua.filechooser.QuaquaFileSystemViewClass");
            return (FileSystemView) Methods.invokeStatic(quaquaFileSystemViewClass, "getQuaquaFileSystemView");
        } catch (Exception ex) {
            return null;
        }
    }
}
