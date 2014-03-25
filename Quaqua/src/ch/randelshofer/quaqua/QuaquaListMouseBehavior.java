/*
 * Copyright (c) 2014 Alan Snyder.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the license agreement. For details see
 * accompanying license terms.
 */
package ch.randelshofer.quaqua;

import javax.swing.*;

/**
 * Mouse behavior for JList. This class simulates Mavericks NSTableView behavior.
 */
public class QuaquaListMouseBehavior extends QuaquaGenericListMouseBehavior {

    public QuaquaListMouseBehavior(JList list) {
        super(new JListModel(list));
    }

    public QuaquaListMouseBehavior(GenericList list) {
        super(list);
    }
}
