/*
 * @(#)ShadowBorder.java  1.0  2011-07-26
 * 
 * Copyright (c) 2011 Werner Randelshofer, Immensee, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package ch.randelshofer.quaqua.border;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import javax.swing.border.Border;

/**
 * {@code ShadowBorder}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-07-26 Created.
 */
public class ShadowBorder implements Border {
         Painter prePainter;
         Painter postPainter;
        
        final int offsetX;
        final int offsetY;
        final float distance;
        final int blur;
        final Insets insets;
        final ConvolveOp blurOp;
        
        public ShadowBorder(final Painter prePainter, final Painter postPainter, final int offsetX, final int offsetY, final float distance, final float intensity, final int blur) {
            this.prePainter = prePainter; this.postPainter = postPainter;
            this.offsetX = offsetX; this.offsetY = offsetY; this.distance = distance; this.blur = blur;
            final int halfBlur = blur / 2;
            this.insets = new Insets(halfBlur - offsetY, halfBlur - offsetX, halfBlur + offsetY, halfBlur + offsetX);
            
            final float blurry = intensity / (blur * blur);
            final float[] blurKernel = new float[blur * blur];
            for (int i = 0; i < blurKernel.length; i++) blurKernel[i] = blurry;
            blurOp = new ConvolveOp(new Kernel(blur, blur, blurKernel));
        }
        
        public boolean isBorderOpaque() {
            return false;
        }
        
        public Insets getBorderInsets(final Component c) {
            return insets;
        }
        
        public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {
            final BufferedImage img = new BufferedImage(width + blur * 2, height + blur * 2, BufferedImage.TYPE_INT_ARGB_PRE);
            paintToImage(img, x, y, width, height);
//            debugFrame("border", img);
            g.drawImage(img, -blur, -blur, null);
        }
        
        protected void paintToImage(final BufferedImage img, final int x, final int y, final int width, final int height) {
            // clear the prior image
            Graphics2D imgG = (Graphics2D)img.getGraphics();
            imgG.setComposite(AlphaComposite.Clear);
            imgG.setColor(Color.black);
            imgG.fillRect(0, 0, width + blur * 2, height + blur * 2);
            
            final int adjX = (int)(x + blur + offsetX + (insets.left * distance));
            final int adjY = (int)(y + blur + offsetY + (insets.top * distance));
            final int adjW = (int)(width - (insets.left + insets.right) * distance);
            final int adjH = (int)(height - (insets.top + insets.bottom) * distance);
            
            // let the delegate paint whatever they want to be blurred
            imgG.setComposite(AlphaComposite.DstAtop);
            if (prePainter != null) prePainter.paint(imgG, adjX, adjY, adjW, adjH);
            imgG.dispose();
            
            // blur the prior image back into the same pixels
            imgG = (Graphics2D)img.getGraphics();
            imgG.setComposite(AlphaComposite.DstAtop);
            imgG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            imgG.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            imgG.drawImage(img, blurOp, 0, 0);
            
            if (postPainter != null) postPainter.paint(imgG, adjX, adjY, adjW, adjH);
            imgG.dispose();
        }
    }