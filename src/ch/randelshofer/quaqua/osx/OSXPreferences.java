/*
 * @(#)OSXPreferences.java
 *
 * Copyright (c) 2005-2010 Werner Randelshofer
 * Hausmatt 10, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package ch.randelshofer.quaqua.osx;

import ch.randelshofer.quaqua.*;
import ch.randelshofer.quaqua.ext.nanoxml.*;
import ch.randelshofer.quaqua.util.BinaryPListParser;
import java.io.*;
import java.util.*;

/**
 * Utility class for accessing Mac OS X System OSXPreferences.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class OSXPreferences {

    private static HashMap prefs;

    /**
     * Creates a new instance.
     */
    public OSXPreferences() {
    }

    public static String getString(String key) {
        return (String) get(key);
    }

    public static String getString(String key, String defaultValue) {
        return prefs.containsKey(key) ? (String) get(key) : defaultValue;
    }

    public static Object get(String key) {
        if (prefs == null) {
            prefs = new HashMap();
            loadGlobalPreferences();
        }
        return prefs.get(key);
    }

    private static void loadGlobalPreferences() {
        // Load Mac OS X global preferences
        // --------------------------------

        // Fill preferences with default values, in case we fail to read them

        // Appearance: "1"=Blue, "6"=Graphite
        prefs.put("AppleAquaColorVariant", "1");
        // Highlight Color: (RGB float values)
        prefs.put("AppleHighlightColor", "0.709800 0.835300 1.000000");
        // Collation order: (Language code)
        prefs.put("AppleCollationOrder", "en");
        // Place scroll arrows: "Single"=At top and bottom, "DoubleMax"=Together
        prefs.put("AppleScrollBarVariant", "DoubleMax");
        // Click in the scroll bar to: "true"=Jump to here, "false"=Jump to next page
        prefs.put("AppleScrollerPagingBehavior", "false");
        // Keyboard UI mode: "1"=Tab moves to text boxes and lists only, "3"=Tab moves to all controls
        prefs.put("AppleKeyboardUIMode", "3");

        if (QuaquaManager.isOSX()) {
            try {
                File globalPrefsFile = new File(
                        QuaquaManager.getProperty("user.home") + "/Library/Preferences/.GlobalPreferences.plist");
                XMLElement xml = readPList(globalPrefsFile);
                for (Iterator i0 = xml.iterateChildren(); i0.hasNext();) {
                    XMLElement xml1 = (XMLElement) i0.next();

                    String key = null;
                    for (Iterator i1 = xml1.iterateChildren(); i1.hasNext();) {
                        XMLElement xml2 = (XMLElement) i1.next();
                        if (xml2.getName().equals("key")) {
                            key = xml2.getContent();
                        } else {
                            if (key != null) {
                                prefs.put(key, xml2.getContent());
                            }
                            key = null;
                        }
                    }
                }
            } catch (Throwable e) {
                System.err.println("Warning: ch.randelshofer.quaqua.util.Preferences failed to load Mac OS X global system preferences");
                e.printStackTrace();
            }
        }
    }

    /**
     * Reads the specified PList file and returns it as an XMLElement.
     * This method can deal with XML encoded and binary encoded PList files.
     */
    private static XMLElement readPList(File plistFile) throws IOException {
        FileReader reader = null;
        XMLElement xml = null;
        try {
            reader = new FileReader(plistFile);
            xml = new XMLElement(new HashMap(), false, false);
            try {
                xml.parseFromReader(reader);
            } catch (XMLParseException e) {
                xml = new BinaryPListParser().parse(plistFile);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return xml;
    }
}

