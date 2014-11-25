package ch.randelshofer.quaqua;

import ch.randelshofer.quaqua.border.BackgroundBorder;

import javax.swing.border.Border;
import java.awt.*;

/**
 * Paints using a nine slice image. This code is based on com.apple.laf.SlicedImageControl, see copyright below. Changed
 * to support Retina displays using pairs of images. Similar to ImageBevelBorder but does not require using
 * BufferedImages for the slices.
 */

public class SlicedImagePainter implements BackgroundBorder {
	private final NineSliceMetrics metrics;
	private final Image NW, N, NE, W, C, E, SW, S, SE;
	private final int totalWidth, totalHeight;

    public SlicedImagePainter(Image img, NineSliceMetrics metrics) {
        this.metrics = metrics;

        QuaquaUtilities.loadImage(img);
        if (img.getWidth(null) != metrics.minW || img.getHeight(null) != metrics.minH) {
            throw new IllegalArgumentException("SlicedImagePainter: image and metrics don't agree on minimum dimensions");
        }

        totalWidth = metrics.minW;
        totalHeight = metrics.minH;
        int centerColWidth = totalWidth - metrics.wCut - metrics.eCut;
        int centerRowHeight = totalHeight - metrics.nCut - metrics.sCut;

        ImageSupport sis = ImageSupport.getImageSupport();

        NW = sis.createSlice(img, 0, 0, metrics.wCut, metrics.nCut);
        N = sis.createSlice(img, metrics.wCut, 0, centerColWidth, metrics.nCut);
        NE = sis.createSlice(img, totalWidth - metrics.eCut, 0, metrics.eCut, metrics.nCut);
        W = sis.createSlice(img, 0, metrics.nCut, metrics.wCut, centerRowHeight);
        C = metrics.showMiddle ? sis.createSlice(img, metrics.wCut, metrics.nCut, centerColWidth, centerRowHeight) : null;
        E = sis.createSlice(img, totalWidth - metrics.eCut, metrics.nCut, metrics.eCut, centerRowHeight);
        SW = sis.createSlice(img, 0, totalHeight - metrics.sCut, metrics.wCut, metrics.sCut);
        S = sis.createSlice(img, metrics.wCut, totalHeight - metrics.sCut, centerColWidth, metrics.sCut);
        SE = sis.createSlice(img, totalWidth - metrics.eCut, totalHeight - metrics.sCut, metrics.eCut, metrics.sCut);
    }

    @Override
    public Border getBackgroundBorder() {
        return this;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        paint(g, x, y, width, height);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 0, 0);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    public void paint(final Graphics g, final int x, final int y, final int w, final int h) {
        g.translate(x, y);

        if (w < totalWidth || h < totalHeight) {
            paintCompressed(g, w, h);
        } else {
            paintStretchedMiddles(g, w, h);
        }

        g.translate(-x, -y);
    }

    void paintStretchedMiddles(final Graphics g, final int w, final int h) {
        int baseX = metrics.stretchH ? 0 : ((w / 2) - (totalWidth / 2));
        int baseY = metrics.stretchV ? 0 : ((h / 2) - (totalHeight / 2));
        int adjustedWidth = metrics.stretchH ? w : totalWidth;
        int adjustedHeight = metrics.stretchV ? h : totalHeight;

        if (NW != null) g.drawImage(NW, baseX, baseY, null);
        if (N != null) g.drawImage(N, baseX + metrics.wCut, baseY, adjustedWidth - metrics.eCut - metrics.wCut, metrics.nCut, null);
        if (NE != null) g.drawImage(NE, baseX + adjustedWidth - metrics.eCut, baseY, null);
        if (W != null) g.drawImage(W, baseX, baseY + metrics.nCut, metrics.wCut, adjustedHeight - metrics.nCut - metrics.sCut, null);
        if (C != null) g.drawImage(C, baseX + metrics.wCut, baseY + metrics.nCut, adjustedWidth - metrics.eCut - metrics.wCut, adjustedHeight - metrics.nCut - metrics.sCut, null);
        if (E != null) g.drawImage(E, baseX + adjustedWidth - metrics.eCut, baseY + metrics.nCut, metrics.eCut, adjustedHeight - metrics.nCut - metrics.sCut, null);
        if (SW != null) g.drawImage(SW, baseX, baseY + adjustedHeight - metrics.sCut, null);
        if (S != null) g.drawImage(S, baseX + metrics.wCut, baseY + adjustedHeight - metrics.sCut, adjustedWidth - metrics.eCut - metrics.wCut, metrics.sCut, null);
        if (SE != null) g.drawImage(SE, baseX + adjustedWidth - metrics.eCut, baseY + adjustedHeight - metrics.sCut, null);
    }

    void paintCompressed(final Graphics g, final int w, final int h) {
        final double heightRatio = h > totalHeight ? 1.0 : (double)h / (double)totalHeight;
        final double widthRatio = w > totalWidth ? 1.0 : (double)w / (double)totalWidth;

        final int northHeight = (int)(metrics.nCut * heightRatio);
        final int southHeight = (int)(metrics.sCut * heightRatio);
        final int centerHeight = h - northHeight - southHeight;

        final int westWidth = (int)(metrics.wCut * widthRatio);
        final int eastWidth = (int)(metrics.eCut * widthRatio);
        final int centerWidth = w - westWidth - eastWidth;

        if (NW != null) g.drawImage(NW, 0, 0, westWidth, northHeight, null);
        if (N != null) g.drawImage(N, westWidth, 0, centerWidth, northHeight, null);
        if (NE != null) g.drawImage(NE, w - eastWidth, 0, eastWidth, northHeight, null);
        if (W != null) g.drawImage(W, 0, northHeight, westWidth, centerHeight, null);
        if (C != null) g.drawImage(C, westWidth, northHeight, centerWidth, centerHeight, null);
        if (E != null) g.drawImage(E, w - eastWidth, northHeight, eastWidth, centerHeight, null);
        if (SW != null) g.drawImage(SW, 0, h - southHeight, westWidth, southHeight, null);
        if (S != null) g.drawImage(S, westWidth, h - southHeight, centerWidth, southHeight, null);
        if (SE != null) g.drawImage(SE, w - eastWidth, h - southHeight, eastWidth, southHeight, null);
    }

    public static class UIResource extends SlicedImagePainter implements javax.swing.plaf.UIResource {

        public UIResource(Image img, NineSliceMetrics metrics) {
            super(img, metrics);
        }

    }
}

/*
 * Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved.
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
