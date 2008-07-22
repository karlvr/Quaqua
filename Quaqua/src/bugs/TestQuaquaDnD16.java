package bugs;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * TestQuaquaDnD16
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestQuaquaDnD16.java,v 1.0 Feb 4, 2008 12:47:45 PM haraldk Exp$
 */
public class TestQuaquaDnD16 {

  protected void startup() {
    final JList list = new JList(createModel());
    list.setDragEnabled(true);
    list.setTransferHandler(new TransferHandler() {
      @Override
      protected Transferable createTransferable(JComponent c) {
        return new StringSelection(String.valueOf(Arrays.asList(list.getSelectedValues())));
      }

      @Override
      public int getSourceActions(JComponent c) {
return DnDConstants.ACTION_COPY_OR_MOVE;
       // return DnDConstants.ACTION_COPY; // Does not work with COPY | MOVE...
      }
    });

    // Other look and feels support this automatically...
    MouseAdapter dragHandler = new MouseAdapter() {
      public boolean mDragging;

      @Override
      public void mouseDragged(MouseEvent e) {
        if (!mDragging) {
          mDragging = true;
          TransferHandler handler = list.getTransferHandler();
          handler.exportAsDrag(list, e, handler.getSourceActions(list));
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        mDragging = false;
      }
    };
    list.addMouseMotionListener(dragHandler);
    list.addMouseListener(dragHandler);

    final JTextArea textArea = new JTextArea(3, 1);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setFont(null);
    textArea.setDropTarget(new DropTarget(list, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
      public void drop(DropTargetDropEvent dtde) {
        try {
          textArea.setText("Dropped "+dtde.getDropAction()+":\n" + dtde.getTransferable().getTransferData(DataFlavor.stringFlavor));
        }
        catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      @Override
      public void dragEnter(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
      }
    }));


    JScrollPane listScroll = new JScrollPane(list);
    listScroll.setBorder(null);

    JScrollPane textScroll = new JScrollPane(textArea);
    textScroll.setBorder(null);

    JSplitPane panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listScroll, textScroll);
    panel.setContinuousLayout(true);
    panel.putClientProperty("Quaqua.SplitPane.style", "bar");

    JFrame frame = new JFrame("Test");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(panel);
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] pArgs) {
    /*try {
      UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }*/
    new TestQuaquaDnD16().startup();
  }

  private ListModel createModel() {
    final List<File> files = new ArrayList<File>();
    File[] roots = File.listRoots();
    for (File root : roots) {
      files.addAll(Arrays.asList(root.listFiles()));
    }
    return new AbstractListModel() {
      public int getSize() {
        return files.size();
      }

      public Object getElementAt(int index) {
        return files.get(index);
      }
    };
  }
}
