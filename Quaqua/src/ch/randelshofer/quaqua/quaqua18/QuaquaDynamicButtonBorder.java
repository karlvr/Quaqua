package ch.randelshofer.quaqua.quaqua18;

import apple.laf.JRSUIConstants;
import com.apple.laf.AquaButtonBorder;
import com.apple.laf.AquaUtilControlSize;

/**
 * Customized button border. Alter left and right border insets for small and mini sizes.
 */

public class QuaquaDynamicButtonBorder extends AquaButtonBorder.Dynamic {

    protected int smallW = -8;
    protected int miniW = -3;

    /*
     * We cannot set the superclass sizeDescriptor variable because it is final, but we can change which one gets used
     * by the getSize() method.
     */

    protected AquaUtilControlSize.SizeDescriptor sizeDescriptor;

    public QuaquaDynamicButtonBorder() {
        sizeDescriptor = new MySizeDescriptor();
    }

    public QuaquaDynamicButtonBorder(QuaquaDynamicButtonBorder other) {
        super(other);
        sizeDescriptor = new MySizeDescriptor();
    }

    @Override
    protected void setSize(JRSUIConstants.Size size) {
        super.setSize(size);
        sizeVariant = sizeDescriptor.get(size);
    }

    protected class MySizeDescriptor extends AquaUtilControlSize.SizeDescriptor {
        public MySizeDescriptor() {
            super(new AquaUtilControlSize.SizeVariant(75, 29).alterMargins(3, 20, 5, 20));
        }

        public AquaUtilControlSize.SizeVariant deriveSmall(final AquaUtilControlSize.SizeVariant v) {
            return super.deriveSmall(v.alterMinSize(smallW*2, -2).alterMargins(0, smallW, -2, smallW).alterInsets(-3, -3, -4, -3));
        }

        public AquaUtilControlSize.SizeVariant deriveMini(final AquaUtilControlSize.SizeVariant v) {
            return super.deriveMini(v.alterMinSize(miniW*2, -2).alterMargins(0, miniW, 0, miniW).alterInsets(-3, 0, -1, 0));
        }
    }

    public static class UIResource extends QuaquaDynamicButtonBorder implements javax.swing.plaf.UIResource {
        public UIResource() {
            super();
        }
        public UIResource(UIResource other) {
            super(other);
        }
    }
}
