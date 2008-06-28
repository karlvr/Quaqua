/*
 * IteratorEnumeration.java
 *
 * Created on March 9, 2004, 5:41 PM
 */

package ch.randelshofer.quaqua.util;

import java.util.*;
/**
 * This Enumeration is a wrapper over an Iterator.
 * @author  werni
 */
public class IteratorEnumeration implements Enumeration {
    private Iterator iterator;
    
    /** Creates a new instance. */
    public IteratorEnumeration(Iterator iterator) {
        this.iterator = iterator;
    }
    
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }
    
    public Object nextElement() {
        return iterator.next();
    }
    
}
