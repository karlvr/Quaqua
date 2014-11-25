package ch.randelshofer.quaqua.quaqua18;

import ch.randelshofer.quaqua.ImageSupport;
import ch.randelshofer.quaqua.QuaquaUtilities;
import com.apple.laf.AquaSplitPaneDividerUI;
import com.apple.laf.AquaSplitPaneUI;

import javax.swing.*;
import java.awt.*;

/**
 * Custom split pane divider. Supports Yosemite look.
 */
public class QuaquaSplitPaneDividerUI extends AquaSplitPaneDividerUI {
    public QuaquaSplitPaneDividerUI(AquaSplitPaneUI ui) {
        super(ui);
    }

    @Override
    public void paint(final Graphics g) {
        paintDivider(g);

        super.paint(g); // Ends up at Container.paint, which paints our JButton children
    }

    /**
     * Paint the divider.
     */
    protected void paintDivider(Graphics g) {

        Color c = getBackgroundColor();
        if (c == null) {
            return;
        }

        final Dimension size = getSize();
        int x = 0;
        int y = 0;

        final boolean horizontal = splitPane.getOrientation() == SwingConstants.HORIZONTAL;
        final int maxSize = getMaxDividerSize();
        boolean doPaint = true;
        if (horizontal) {
            if (size.height > maxSize) {
                final int diff = size.height - maxSize;
                y = diff / 2;
                size.height = maxSize;
            }
            if (size.height < 4) doPaint = false;
        } else {
            if (size.width > maxSize) {
                final int diff = size.width - maxSize;
                x = diff / 2;
                size.width = maxSize;
            }
            if (size.width < 4) doPaint = false;
        }

        if (doPaint) {
            g.setColor(c);
            g.fillRect(x, y, size.width, size.height);

            g.setColor(new Color(213, 213, 213));
            if (horizontal) {
                g.fillRect(x, y, size.width, 1);
                g.fillRect(x, y + size.height - 1, size.width, 1);
            } else {
                g.fillRect(x, y, 1, size.height);
                g.fillRect(x + size.width - 1, y, 1, size.height);
            }
        }
    }

    protected Color getBackgroundColor() {
        Object o = splitPane.getClientProperty("SplitPaneDivider.opaque");
        if (Boolean.FALSE.equals(o)) {
            return null;
        }

        return UIManager.getColor("SplitPaneDivider.background");
    }

    protected JButton createLeftOneTouchButton() {
        return createButtonForDirection(getDirection(true));
    }

    protected JButton createRightOneTouchButton() {
        return createButtonForDirection(getDirection(false));
    }

    // separate static, because the divider needs to be serializable
    // see <rdar://problem/7590946> JSplitPane is not serializable when using Aqua look and feel
    static JButton createButtonForDirection(final int direction) {
        Image im = getArrowImage(direction);
        final JButton button = new JButton(new ImageIcon(im));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        button.setFocusPainted(false);
        button.setRequestFocusEnabled(false);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        return button;
    }

    static Image getArrowImage(int direction) {
        Image basic = getBasicArrowImage(direction);
        if (basic == null) {
            return null;
        }

        QuaquaUtilities.loadImage(basic);
        ImageSupport sis = ImageSupport.getImageSupport();
        return sis.createScaled(basic, 7, 7);
    }

    // Alas, multiresolution NSImages are not supported

    static Image getBasicArrowImage(int direction) {
        String name;
        switch (direction) {
            case SwingConstants.NORTH: name = "NSMenuScrollUp"; break;
            case SwingConstants.SOUTH: name = "NSMenuScrollDown"; break;
            case SwingConstants.EAST: name = "NSMenuSubmenu"; break;
            case SwingConstants.WEST: name = "NSMenuSubmenuLeft"; break;
            default: return null;
        }

        return Toolkit.getDefaultToolkit().getImage("NSImage://" + name);
    }

    int getDirection(final boolean isLeft) {
        if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            return isLeft ? SwingConstants.WEST : SwingConstants.EAST;
        }

        return isLeft ? SwingConstants.NORTH : SwingConstants.SOUTH;
    }
}
