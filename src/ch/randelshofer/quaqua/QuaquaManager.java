/*
 * @(#)QuaquaManager.java 4.1  2007-12-09
 *
 * Copyright (c) 2003-2007 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.filechooser.*;
import ch.randelshofer.quaqua.jaguar.filechooser.*;
import ch.randelshofer.quaqua.panther.filechooser.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import java.security.*;
import java.util.*;
import java.io.*;
/**
 * The QuaquaManager provides bug fixes and enhancements for the Mac Look and
 * Feel and for the Aqua Look and Feel on Mac OS X.
 * <p>
 * <b>Usage for Java Applications:</b>
 * <pre>
 * UIManager.setLookAndFeel(QuaquaManager.getLookAndFeelClassName());
 * </pre>
 * <p>
 * <b>Usage for Java Applets:</b>
 * <pre>
 * UIManager.put("ClassLoader", getClass().getClassLoader());
 * UIManager.setLookAndFeel(QuaquaManager.getLookAndFeel());
 * </pre>
 * <p>
 * <b>System Properties for Java Applications:</b><br>
 * You can customize the Quaqua Look and Feel using the following system properties:
 * <ul>
 * <li><code>Quaqua.design=jaguar</code> Enforces Jaguar design.</li>
 * <li><code>Quaqua.design=panther</code> Enforces Panther design.</li>
 * <li><code>Quaqua.design=tiger</code> Enforces Tiger design.</li>
 * <li><code><b>Quaqua.design=auto</b></code> Chooses design automatically.
 * This is the default value.</li>
 * </ul>
 * <ul>
 * <li><code>Quaqua.TabbedPane.design=jaguar</code> Enforces Jaguar design
 * for tabbed panes.</li>
 * <li><code>Quaqua.TabbedPane.design=panther</code> Enforces Panther design for
 * tabbed panes.</li>
 * <li><code><b>Quaqua.TabbedPane.design=auto</b></code> Chooses design
 * automatically. This is the default value.</li>
 * </ul>
 * <p>
 * <ul>
 * <li><code>Quaqua.FileChooser.autovalidate=false</code> FileChoosers do not
 * refresh their contents automatically. Users have to close and reopen a
 * file chooser to see changes in the file system.</li>
 * <li><code><b>Quaqua.FileChooser.autovalidate=true</b></code> FileChoosers
 * refresh their contents periodically. This is the default value.</li>
 * </ul>
 * <ul>
 * <li><code>Quaqua.Debug.crossPlatform=true</code> Enforces cross platform support.
 * This is a hack, useful only for testing an application with the Quaqua Look and
 * Feel on non-Mac OS X platforms.</li>
 * <li><code><b>Quaqua.Debug.crossPlatform=false</b></code> Chooses native support.
 * This is the default value.</li>
 * </ul>
 * Example:
 * <pre>
 * System.setProperty("Quaqua.design", "panther");
 * System.setProperty("Quaqua.TabbedPane.design", "jaguar");
 * System.setProperty("Quaqua.FileChooser.autovalidate", "true");
 * </pre>
 * <p>
 * <b>System Properties for Java Applets:</b><br>
 * In a secure environment, you are not allowed to change system properties.
 * Use <code>QuaquaManager.setProperty</code> to specify (or override)
 * system properties that are used by Quaqua (that is, for all system
 * properties listed above).
 * <p>
 * Example:
 * <pre>
 * QuaquaManager.setProperty("Quaqua.design", "panther");
 * QuaquaManager.setProperty("Quaqua.TabbedPane.design", "jaguar");
 * QuaquaManager.setProperty("Quaqua.FileChooser.autovalidate", "true");
 * </pre>
 * <p>
 * <b>Client Properties:</b><br>
 * You can customize some of the components by specifying client properties.
 * <ul>
 * <li><b>JTable</b>: <code>Quaqua.Table.style=striped</code>
 * displays rows with alternating colors.</li>
 * </ul>
 * <b>Specifying class loader (Java Applets):</b><br>
 * If your code runs as an Applet in a Java 1.3 VM, Swing attempts to load the
 * UI classes from the system class loader instead of from the class loader which
 * loads the applet classes. To have Swing load the UI classes using the same
 * class loader as your code, use the following code to set the
 * Quaqua look and feel on Swing's UIManager.
 * <pre>
 * UIManager.put("ClassLoader", getClass().getClassLoader());
 * UIManager.setLookAndFeel(QuaquaManager.getLookAndFeel());
 * </pre>
 *
 *
 * @author  Werner Randelshofer
 * @version 4.1 2007-12-09 Make a distinction between cross-platform designs,
 * instead of providing a single cross-platform design depending on the J2SE
 * version. 
 * <br>4.0 2007-11-24 Support for Darwin 9.1 added. 
 * <br>3.0 2007-10-30 Support for Mac OS X 10.5 Leopard added.
 * <br>2.2 2006-03-12 Method getVersion added.
 * <br>2.1.2 2006-02-13 Print warning message, when file "laf.txt" is missing.
 * <br>2.1.1 2005-12-09 Reorganized some packages and class names.
 * <br>2.1 2005-12-01 Method getLookAndFellClassName does not use
 * UIManager.getDefaultLookAndFeel anymore to decide which Quaqua look and feel
 * implementation to use.
 * <br>2.0 2005-09-10 Read L&F class names from file laf.txt. Moved
 * FileSystemView related methods out into class QuaquaFileSystemView.
 * Added method getOS(), and made OS Constants public.
 * <br>1.8.3 2005-08-03 Fall back to FileSystemView.getFileSystemView
 * when creating one of the Quaqua FileSystemView's fails.
 * <br>1.8.2 2005-06-20 Fixed bug in code that determines the OS.
 * <br>1.8.1 2005-06-19 OS and Design property are now updated each time
 * when a look and feel class name is requested.
 * <br>1.8 2005-05-29 Added method getProperty(String, int[]).
 * Fixed missing break statement which caused that
 * Quaqua13PantherLookAndFeel was never used.
 * <br>1.7 2005-05-16 System Property "Quaqua.Debug.crossPlatform" added.
 * <br>1.6 2005-05-15 Support for Mac OS X 10.4 Tiger added.
 * <br>1.5 2005-05-08 Method boolean get(String) added.
 * <br>1.4.1 2004-10-31 The file system view must be chosen on the OS in use,
 * and not on the Quaqua design in use.
 * <br>1.4 2004-09-10 Catched security exception caused by static
 * initializer. Method setProperty, getProperty and removeProperty added.
 * <br>1.3 2004-06-30 Revised.
 * <br>1.2.2 2004-02-06 Support for Java 1.4.2 added.
 * <br>1.2.1 2003-10-29 Support for Mac OS X 10.3 added.
 * <br>1.2 2003-09-28 Method getLookAndFeel added.
 * <br>1.1 2003-09-11 Workarounds for Java 1.4.1 Update 1 added.
 * <br>1.0 2003-07-20 Created.
 */
public class QuaquaManager {
    private static Properties properties;
    
    /**
     * Set<String> of included Quaqua UI's. If this variable is null, all UI's 
     * of the QuaquaLookAndFeel are included.
     */
    private static Set includedUIs;
    /**
     * Set<String> of excluded Quaqua UI's. If this variable is null, all UI's 
     * of the QuaquaLookAndFeel are excluded.
     */
    private static Set excludedUIs = Collections.EMPTY_SET;
    
    
    private final static String version;
    static {
        String v = null;
        try {
            InputStream s = QuaquaManager.class.getResourceAsStream("version.txt");
            if (s != null) {
                BufferedReader r = new BufferedReader(new InputStreamReader(s, "UTF8"));
                v = r.readLine();
                r.close();
            }
        } catch (IOException e) {
        }
        version = (v == null) ? "unknown" : v;
    }
    
    /**
     * Mac OS X 10.0 Cheetah.
     */
    public final static int CHEETAH = 0;
    /**
     * Mac OS X 10.1 Puma.
     */
    public final static int PUMA = 1;
    /**
     * Mac OS X 10.2 Jaguar.
     */
    public final static int JAGUAR = 2;
    /**
     * Mac OS X 10.3 Panther.
     */
    public final static int PANTHER = 3;
    /**
     * Mac OS X 10.4 Tiger.
     */
    public final static int TIGER = 4;
    /**
     * Mac OS X 10.5 Leopard or Darwin 9.1.0.
     */
    public final static int LEOPARD = 5;
    /**
     * Darwin.
     */
    public final static int DARWIN = -3;
    /**
     * Windows.
     */
    public final static int WINDOWS = -2;
    /**
     * Unknown.
     */
    public final static int UNKNOWN = -1;
    /**
     * Current operating system.
     */
    private static int OS;
    
    /**
     * True if Mac OS X.
     */
    private static boolean isOSX;
    /**
     * Current design. May differ from the operating system, by setting a
     * value in the "Quaqua.design" property.
     */
    private static int design;
    
    static {
        updateAvailableLAFs();
        updateDesignAndOS();
    }
    
    private static void updateDesignAndOS() {
        String osName = getProperty("os.name");
        String osVersion = getProperty("os.version");
        
        isOSX = osName.equals("Mac OS X");
        if (isOSX) {
            int p = osVersion.indexOf('.');
            p = osVersion.indexOf('.', p + 1);
            if (p != -1) {
                osVersion = osVersion.substring(0, p);
            }
            if (osVersion.equals("10.0")) {
                OS = CHEETAH;
            } else if (osVersion.equals("10.1")) {
                OS = PUMA;
            } else if (osVersion.equals("10.2")) {
                OS = JAGUAR;
            } else if (osVersion.equals("10.3")) {
                OS = PANTHER;
            } else if (osVersion.equals("10.4")) {
                OS = TIGER;
            } else if (osVersion.equals("10.5")) {
                OS = LEOPARD;
            } else {
                OS = TIGER;
            }
        } else if (osName.startsWith("Darwin")) {
            OS = DARWIN;
        } else if (osName.startsWith("Windows")) {
            OS = WINDOWS;
        } else {
            OS = UNKNOWN;
        }
        
        String osDesign = getProperty("Quaqua.design", "auto").toLowerCase();
        if (osDesign.equals("cheetah")) {
            design = JAGUAR;
        } else if (osDesign.equals("puma")) {
            design = JAGUAR;
        } else if (osDesign.equals("jaguar")) {
            design = JAGUAR;
        } else if (osDesign.equals("panther")) {
            design = PANTHER;
        } else if (osDesign.equals("tiger")) {
            design = TIGER;
        } else if (osDesign.equals("leopard")) {
            design = LEOPARD;
        } else {
            switch (OS) {
                case CHEETAH : design = JAGUAR;  break;
                case PUMA    : design = JAGUAR;  break;
                case JAGUAR  : design = JAGUAR;  break;
                case PANTHER : design = PANTHER; break;
                case TIGER   : design = TIGER; break;
                case LEOPARD :
                default :
                    design = LEOPARD;   break;
            }
        }
    }
    
    /**
     * Map of Quaqua Look and Feels.
     *
     * key<String> lafKey.
     *             One out of Jaguar13,Jaguar14,Panther13,Panther14,Tiger13,Tiger14.
     * value<String> Look and Feel class name.
     */
    private static HashMap lafs;
    
    /**
     * Updates the map of available Quaqua Look and Feels.
     * The list may vary depending on the deployment chosen for the Quaqua Look
     * and Feel.
     *
     * The list of look and feels is contained in a file named "laf.txt" in the
     * package "ch.randelshofer.quaqua".
     * The file contains a semicolon separated mapping according to the following
     * EBNF production:
     * <pre>
     * mapping ::= {design}"."{version}"="{class}";"
     * </pre>
     *
     */
    private static void updateAvailableLAFs() {
        lafs = new HashMap();
        
        try {
            InputStream s = QuaquaManager.class.getResourceAsStream("laf.txt");
            
            if (s != null) {
                BufferedReader r = new BufferedReader(new InputStreamReader(s, "UTF8"));
                StreamTokenizer tt = new StreamTokenizer(r);
                tt.ordinaryChar('=');
                tt.ordinaryChar(';');
                while (tt.nextToken() != tt.TT_EOF) {
                    if (tt.ttype != tt.TT_WORD) {
                        throw new IOException("Illegal token for 'design.version' in laf.txt File");
                    }
                    String lafKey = tt.sval;
                    if (tt.nextToken() != '=') {
                        throw new IOException("Illegal token for '=' in laf.txt File");
                    }
                    if (tt.nextToken() != tt.TT_WORD) {
                        throw new IOException("Illegal token for 'class' in laf.txt File at key '"+lafKey+"'");
                    }
                    String className = tt.sval;
                    if (tt.nextToken() != ';') {
                        throw new IOException("Illegal token for ';' in laf.txt File");
                    }
                    lafs.put(lafKey, className);
                }
                r.close();
            } else {
                throw new IOException("File laf.txt not found");
            }
        } catch (IOException e) {
            System.err.println("Warning: "+QuaquaManager.class+".updateAvailableLAFs() couldn't access resource file \"laf.txt\".");
            //e.printStackTrace();
            
            // Fall back to default values
            lafs.put("Jaguar.14","ch.randelshofer.quaqua.jaguar.Quaqua14JaguarLookAndFeel");
            lafs.put("Jaguar.15","ch.randelshofer.quaqua.jaguar.Quaqua15JaguarLookAndFeel");
            lafs.put("Panther.14","ch.randelshofer.quaqua.panther.Quaqua14PantherLookAndFeel");
            lafs.put("Panther.15","ch.randelshofer.quaqua.panther.Quaqua15PantherLookAndFeel");
            lafs.put("Tiger.14","ch.randelshofer.quaqua.tiger.Quaqua14TigerLookAndFeel");
            lafs.put("Tiger.15","ch.randelshofer.quaqua.tiger.Quaqua15TigerLookAndFeel");
            lafs.put("Leopard.14","ch.randelshofer.quaqua.leopard.Quaqua14LeopardLookAndFeel");
            lafs.put("Leopard.15","ch.randelshofer.quaqua.leopard.Quaqua15LeopardLookAndFeel");
            lafs.put("CrossTiger.14","ch.randelshofer.quaqua.tiger.Quaqua14TigerCrossPlatformLookAndFeel");
            lafs.put("CrossTiger.15","ch.randelshofer.quaqua.tiger.Quaqua15TigerCrossPlatformLookAndFeel");
            lafs.put("CrossLeopard.14","ch.randelshofer.quaqua.leopard.Quaqua14LeopardCrossPlatformLookAndFeel");
            lafs.put("CrossLeopard.15","ch.randelshofer.quaqua.leopard.Quaqua15LeopardCrossPlatformLookAndFeel");
        }
    }
    
    /** Prevent instance creation. */
    private QuaquaManager() {
    }
    
    /**
     * Returns the current operating system.
     *
     * @return one of the OS constants: CHEETAH..LEOPARD, DARWIN, WINDOWS or UNKNOWN.
     */
    public static int getOS() {
        return OS;
    }
    /**
     * Returns true if the current operating system is know to be a Mac OS X.
     */
    public static boolean isOSX() {
        return OS >= CHEETAH;
    }
    
    /**
     * Returns the current design of Mac OS X.
     *
     * @return one of the OS constants: CHEETAH..TIGER or UNKNOWN.
     */
    public static int getDesign() {
        return design;
    }
    
    /**
     * Returns the class name of a Quaqua look and feel.
     * The class name depends on the JVM, Quaqua is running on, and on the
     * visual design of the operating system.
     */
    public static String getLookAndFeelClassName() {
        updateDesignAndOS();
        
        if (getProperty("Quaqua.noQuaqua","false").equals("true")) {
            return UIManager.getCrossPlatformLookAndFeelClassName();
        }
        
        String lafKey = null;
        String className;
        
        className = "apple.laf.AquaLookAndFeel";
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e1) {
            className = "com.apple.mrj.swing.MacLookAndFeel";
            try {
                Class.forName(className);
            } catch (ClassNotFoundException e2) {
                className = UIManager.getCrossPlatformLookAndFeelClassName();
            }
        }
        
        String javaVersion = getProperty("java.version","");
        // FIXME - Remove Java 1.3 support
        if (className.equals("com.apple.mrj.swing.MacLookAndFeel")) {
            switch (design) {
                case JAGUAR :
                    lafKey = "Jaguar.13";
                    break;
                case PANTHER :
                    lafKey = "Panther.13";
                    break;
                case TIGER :
                default :
                    lafKey = "Tiger.13";
                    break;
            }
        } else if (className.equals("apple.laf.AquaLookAndFeel")) {
            if (javaVersion.startsWith("1.4")) {
                switch (design) {
                    case JAGUAR :
                        lafKey = "Jaguar.14";
                        break;
                    case PANTHER :
                        lafKey = "Panther.14";
                        break;
                    case TIGER :
                        lafKey = "Tiger.14";
                        break;
                    case LEOPARD :
                    default :
                        lafKey = "Leopard.14";
                        break;
                }
            } else {
                switch (design) {
                    case JAGUAR :
                        lafKey = "Jaguar.15";
                        break;
                    case PANTHER :
                        lafKey = "Panther.15";
                        break;
                    case TIGER :
                        lafKey = "Tiger.15";
                        break;
                    case LEOPARD :
                    default :
                        lafKey = "Leopard.15";
                        break;
                }
            }
        } else {
            if (javaVersion.startsWith("1.4")) {
                switch (design) {
                    case JAGUAR :
                        lafKey = "CrossTiger.14";
                        break;
                    case PANTHER :
                        lafKey = "CrossTiger.14";
                        break;
                    case TIGER :
                        lafKey = "CrossTiger.14";
                        break;
                    case LEOPARD :
                    default :
                        lafKey = "CrossLeopard.14";
                        break;
                }
            } else {
                lafKey = "CrossPlatform.15";
                switch (design) {
                    case JAGUAR :
                        lafKey = "CrossTiger.15";
                        break;
                    case PANTHER :
                        lafKey = "CrossTiger.15";
                        break;
                    case TIGER :
                        lafKey = "CrossTiger.15";
                        break;
                    case LEOPARD :
                    default :
                        lafKey = "CrossLeopard.15";
                        break;
                }
            }
        }
        
        if (lafs.containsKey(lafKey)) {
            className = (String) lafs.get(lafKey);
        }
        return className;
    }
    
    /**
     * Returns a Quaqua look and feel, if workarounds for the
     * system look and feel are available.
     * Returns a UIManager.getSystemLookAndFeelClassName() instance if no
     * workaround is available.
     */
    public static LookAndFeel getLookAndFeel() {
        try {
            return (LookAndFeel) Class.forName(getLookAndFeelClassName()).newInstance();
        } catch (Exception e) {
            InternalError ie = new InternalError(e.toString());
            /* FIXME - This needs JDK 1.4 to work.
            ie.initCause(e);
             */
            throw ie;
        }
    }
    
    /**
     * This method returns a boolean UIManager property.
     * This method has been moved here, because Java 1.3 does not support
     * this directly.
     */
    public static boolean getBoolean(String key) {
        Object value = UIManager.get(key);
        return (value instanceof Boolean) ? ((Boolean)value).booleanValue() : false;
    }
    /**
     * This method returns a locally specified property, if it has been set using
     * method <code>setProperty</code>.
     * If no local property has been found, a system property using
     * method  <code>java.lang.System.getProperty(String,String</code> is
     * returned.<p>
     * This method is used to specify properties for Quaqua, when, due to
     * security reasons, system properties can not be used, e.g. in a secure
     * Applet environment.
     *
     * @see #setProperty
     */
    public static String getProperty(String key) {
        try {
            if (properties == null || ! properties.containsKey(key)) {
                return System.getProperty(key);
            } else {
                return properties.getProperty(key);
            }
        } catch (SecurityException e) {
            return null;
        }
    }
    /**
     * This method returns a locally specified property, if it has been set using
     * method <code>setProperty</code>.
     * If no local property has been found, a system property using
     * method  <code>java.lang.System.getProperty(String,String</code> is
     * returned.<p>
     * This method is used to specify properties for Quaqua, when, due to
     * security reasons, system properties can not be used, e.g. in a secure
     * Applet environment.
     *
     * @see #setProperty
     */
    public static String getProperty(String key, String def) {
        try {
            if (properties == null || ! properties.containsKey(key)) {
                return System.getProperty(key, def);
            } else {
                return properties.getProperty(key, def);
            }
        } catch (SecurityException e) {
            return def;
        }
    }
    /**
     * This method returns a locally specified property, if it has been set using
     * method <code>setProperty</code>.
     * If no local property has been found, a system property using
     * method  <code>java.lang.System.getProperty(String,String</code> is
     * returned.<p>
     * This method is used to specify properties for Quaqua, when, due to
     * security reasons, system properties can not be used, e.g. in a secure
     * Applet environment.
     *
     * @see #setProperty
     */
    public static int[] getProperty(String key, int[] def) {
        String value;
        try {
            if (properties == null || ! properties.containsKey(key)) {
                value = System.getProperty(key, null);
            } else {
                value = properties.getProperty(key, null);
            }
        } catch (SecurityException e) {
            value = null;
        }
        if (value != null) {
            StringTokenizer tt = new StringTokenizer(value, ",");
            if (tt.countTokens() == def.length) {
                int[] result = new int[def.length];
                try {
                    for (int i=0; i < result.length; i++) {
                        result[i] = Integer.decode(tt.nextToken()).intValue();
                    }
                    return result;
                } catch (NumberFormatException e) {
                    // continue (we return def below)
                }
            }
        }
        return def;
    }
    /**
     * Locally defines a property.<p>
     * Use method <code>clearProperty</code> to clear a local property.<p>
     * This method is used to specify properties for Quaqua, when, due to
     * security reasons, system properties can not be used, e.g. in a secure
     * Applet environment.<p>
     *
     * @see #getProperty
     */
    public static String setProperty(String key, String value) {
        if (properties == null) {
            properties = new Properties();
        }
        return (String) properties.setProperty(key, value);
    }
    
    /**
     * Removes a locally defined property.<p>
     * This method is used to specify properties for Quaqua, when, due to
     * security reasons, system properties can not be used, e.g. in a secure
     * Applet environment.
     *
     * @see #setProperty
     */
    public static void removeProperty(String key) {
        if (properties != null) {
            properties.remove(key);
        }
    }
    
    /**
     * Returns the version string of Quaqua.
     * The version string is a sequence of numbers separated by full stops,
     * followed by a blank character and a release date in ISO-format.
     * e.g. "3.6.1 2006-03-12"
     */
    public static String getVersion() {
        return version;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(QuaquaManager.getLookAndFeelClassName());
        } catch (Exception e) {
            // empty
        }
        JFrame f = new JFrame("Quaqua Look and Feel");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel(
                "<html>"
                + "<p align=center><b>Quaqua Look and Feel "+version+"</b><br><br>"
                + "Copyright 2003-2007 Werner Randelshofer<br>"
                + "All Rights Reserved.<br>"
                + "<br>"
                + "This is a software library.<br>"
                + "Please read the accompanying documentation<br>for additional information."
                );
        label.setBorder(new EmptyBorder(12,20,20,20));
        f.getContentPane().add(label);
        f.pack();
        f.setVisible(true);
    }
    
    /**
     * Returns true, if Quaqua uses native code for some of its functionality.
     */
    public static boolean isNativeCodeAvailable() {
        return Files.canWorkWithAliases();
    }
    
    /**
     * Include only UI delegates with the specified names. 
     * This method must be called, before setting the QuaquaLookAndFeel 
     * to the UIManager.
     * <p>
     * Usage:
     * <pre>
     * HashSet includes = new HashSet();
     * includes.add("Button");
     * QuaquaManager.setIncludeUIs(includes);
     * </pre>
     * 
     * @param includes Set<String> Only include UI delegates, which are in
     * this list. Specify null to include all UIs.
     */
    public static void setIncludedUIs(Set includes) {
        includedUIs = includes;
     }
    /**
     * Excludes UI delegates with the specified names. 
     * This method must be called, before setting the QuaquaLookAndFeel 
     * to the UIManager.
     * <p>
     * Usage:
     * <pre>
     * HashSet excludes = new HashSet();
     * excludes.add("TextField");
     * QuaquaManager.setExcludeUIs(excludes);
     * </pre>
     * 
     * @param includes Set<String> Exclude UI delegates, which are in
     * this list. Specify null to exclude all UIs.
     */
    public static void setExcludedUIs(Set excludes) {
        excludedUIs = excludes;
     }
    
    /**
     * Gets the included UI delegates, or null, if all Quaqua UI delegates
     * shall be included into the QuaquaLookAndFeel.
     * 
     * @return Set<String>.
     */
    public static Set getIncludedUIs() {
        return includedUIs;
    }
    /**
     * Gets the excluded UI delegates, or null, if all Quaqua UI delegates
     * shall be excluded from the QuaquaLookAndFeel.
     * 
     * @return Set<String>.
     */
    public static Set getExcludedUIs() {
        return excludedUIs;
    }
}
