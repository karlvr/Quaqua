/*
 * @(#)BrowserPreviewRenderer.java  1.0  25 January 2005
 *
 * Copyright (c) 2004 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package ch.randelshofer.quaqua;

import java.awt.*;
import javax.swing.tree.*;
/**
 * Identifies components that can be used to paint the preview column in a 
 * JBrowser. Unlike other renderers, this one is not just used for a rubber stamp.
 * The renderer is added to the preview column of the JBrowser.
 * <p>
 * For example, to use a JLabel as a BrowserPreviewRenderer, you 
 * would write something like this:
 * <pre>
 * class MyPreviewRenderer extends JLabel implements BrowserPreviewRenderer {
 *     public MyPreviewRenderer() {
 *         setOpaque(true);
 *     }
 *     public Component getPreviewRendererRendererComponent(
 *         JBrowser browser,
 *         TreePath[] paths
 *         )
 *     {
 *         setText((paths.length == 1) 
 *            ? values[0].getLastPathComponent().toString() 
 *            : values.length+" items");
 *         return this;
 *     }
 * }
 * </pre>
 *
 * @author  Werner Randelshofer
 * @version 1.0  25 January 2005  Created.
 */
public interface BrowserPreviewRenderer {
    /**
     * Return a component that has been configured to display the specified
     * value. That component is then added to the preview column of the JBrowser.
     *
     * @param browser The JBrowser we're painting.
     * @param paths The paths returned by browser.getSelectionPaths(). This
     * is granted to be a non-null array containing at least one element.
     *
     */
      public Component getPreviewRendererComponent(
          JBrowser browser,
          TreePath[] paths
          );          
}
