/*
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published
 *    by the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.sciss.treetable.j.ui;

import java.awt.Rectangle;

import javax.swing.plaf.ComponentUI;
import javax.swing.tree.TreePath;

import de.sciss.treetable.j.TreeTable;
import de.sciss.treetable.j.DefaultTreeTableCellEditor;
import de.sciss.treetable.j.DefaultTreeTableCellRenderer;
import de.sciss.treetable.j.TreeTableCellEditor;
import de.sciss.treetable.j.TreeTableCellRenderer;

public abstract class TreeTableUI extends ComponentUI {
	
	public abstract TreeInterface getTreeInterface(TreeTable treeTable);
	
	public abstract TableInterface getTableInterface(TreeTable treeTable);

	public abstract void configureCellRenderer(
			DefaultTreeTableCellRenderer renderer, TreeTable treeTable,
			Object value, boolean selected, boolean hasFocus,
			int row, int column);
	

	public abstract void configureCellRenderer(
			DefaultTreeTableCellRenderer renderer, TreeTable treeTable,
			Object value, boolean selected, boolean hasFocus,
			int row, int column, boolean expanded, boolean leaf);
	
	public abstract void configureCellEditor(DefaultTreeTableCellEditor editor,
			TreeTable treeTable, Object value, boolean selected, int row, int column);
	
	public abstract void configureCellEditor(DefaultTreeTableCellEditor editor,
			TreeTable treeTable, Object value, boolean selected,
			int row, int column, boolean expanded, boolean leaf);
	
	public abstract TreeTableCellRenderer getDefaultRenderer(TreeTable treeTable, Class<?> columnClass);

	public abstract TreeTableCellEditor getDefaultEditor(TreeTable treeTable, Class<?> columnClass, int column);
	
	public abstract Rectangle getPathBounds(TreeTable treeTable, TreePath path);
	
	public abstract TreePath getPathForLocation(TreeTable treeTable, int x, int y);
	
	public abstract TreePath getClosestPathForLocation(TreeTable treeTable, int x, int y);
	
	public abstract int getDistanceToTreeHandle(TreeTable treeTable, TreePath path, int x);
	
}
