package test;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;


public class TestBox14 {

	public static void main(String[] args) throws Exception {
		try {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			UIManager.setLookAndFeel(
					"ch.randelshofer.quaqua.QuaquaLookAndFeel"
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JFrame frame = new JFrame();
		JMenuBar bar = new JMenuBar();
		
		JMenu menu = new JMenu("Menu");
		
		JMenuItem menuItem = new JMenuItem("Menu Item");
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Working");
			}
		});
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		menu.add(menuItem);
		bar.add(menu);
		
		frame.setJMenuBar(bar);
		
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}
}