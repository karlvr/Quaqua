package ch.randelshofer.quaqua.quaqua18;

import com.apple.laf.AquaButtonUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A customized version of AquaButtonUI. Works around bugs.
 */

public class QuaquaButtonUI extends AquaButtonUI {

    private static QuaquaButtonUI buttonUI = new QuaquaButtonUI();

    public static ComponentUI createUI(JComponent c) {
        return buttonUI;
    }

    @Override
    protected void installListeners(AbstractButton b) {
        super.installListeners(b);

        Object o = b.getClientProperty(this);
        if (o instanceof PropertyChangeListener) {
            PropertyChangeListener l = (PropertyChangeListener) o;
            b.removePropertyChangeListener(l);
            b.addPropertyChangeListener(new ButtonPropertyChangeListener(l));
        }
    }

    @Override
    protected void uninstallListeners(AbstractButton b) {
        super.uninstallListeners(b);
        PropertyChangeListener[] ls = b.getPropertyChangeListeners();
        for (PropertyChangeListener l : ls) {
            if (l instanceof ButtonPropertyChangeListener) {
                b.removePropertyChangeListener(l);
            }
        }
    }

    /**
     * Work around a bug in AquaButtonUI. If the button type is set to null, AquaButtonUI would not install a default
     * border.
     */

    protected class ButtonPropertyChangeListener
        implements PropertyChangeListener
    {
        private PropertyChangeListener base;

        public ButtonPropertyChangeListener(PropertyChangeListener base) {
            this.base = base;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name.equals("JButton.buttonType") && evt.getNewValue() == null) {
                Object o = (AbstractButton) evt.getSource();
                if (o instanceof AbstractButton) {
                    AbstractButton b = (AbstractButton) o;
                    updateBorder(b);
                    return;
                }
            }

            base.propertyChange(evt);
        }
    }

    /**
     * Work around a bug in AquaButtonUI. Under various circumstances, this method will be called to replace a border.
     * The AquaButtonUI method fails to take into account the size property.
     */

    @Override
    protected void setThemeBorder(AbstractButton b) {
        super.setThemeBorder(b);
        QuaquaUtilControlSize.applyComponentSize(b);
    }
}
