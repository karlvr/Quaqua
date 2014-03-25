/*
 * Copyright (c) 2014 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */
package ch.randelshofer.quaqua.filechooser;

import javax.swing.*;
import java.awt.*;

/**
 * A cell renderer that does not care what kind of container it is used in.
 */

public interface CellRenderer {
    Component getCellRendererComponent(JComponent container,
                                       Object value,
                                       boolean isSelected,
                                       boolean cellHasFocus);
}
