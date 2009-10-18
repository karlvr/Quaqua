/**
 * @(#)Issue32.java  1.0  Feb 10, 2008
 *
 * Copyright (c) 2008 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package bugs;

import ch.randelshofer.quaqua.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author: Thomas Singer
 */
public class Issue32 {
	public static void main(String[] args) throws UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(new QuaquaLookAndFeel());

		final JTree tree = new JTree(new Object[] {
				"first", "second"
		});
		tree.setSelectionRow(0);

		final JTable table = new JTable(new Object[][] {
				new Object[] {
						"a", "b"
				},
				new Object[] {
						"c", "d"
				}
		}, new Object[] {
				"e", "f"
		});
		table.setRowSelectionInterval(0, 0);

		final JCheckBox checkBox = new JCheckBox("Enabled");
		checkBox.setSelected(true);
		checkBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				final boolean enable = checkBox.isSelected();
				table.setEnabled(enable);
				tree.setEnabled(enable);
			}
		});
		checkBox.setFocusable(false);

		final JFrame frame = new JFrame("Tree-Focus problem");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(BorderLayout.WEST, new JScrollPane(tree));
		frame.getContentPane().add(BorderLayout.EAST, new JScrollPane(table));
		frame.getContentPane().add(BorderLayout.SOUTH, checkBox);
		frame.pack();
		table.requestFocusInWindow();
		frame.setVisible(true);
	}
}