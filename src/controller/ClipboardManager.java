package controller;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class manages the SystemClipboard, i.e. it notices when the clipboard's
 * content changed and tells the <tt>ClipboardContentChangeListener</tt>s.
 * 
 * @author Sebastian Brockmeyer
 * 
 */
public class ClipboardManager implements ClipboardOwner {

	private Clipboard clip;
	private List<ClipboardContentChangeListener> listeners;

	private void getOwnership(Transferable trans) {
		clip.setContents(trans, this);
	}

	/**
	 * Creates a new ClipboardManager that notices changes on the
	 * SystemClipboard.
	 */
	public ClipboardManager() {
		clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		listeners = new LinkedList<ClipboardContentChangeListener>();
		getOwnership(clip.getContents(null));
	}

	/**
	 * Adds a ClipboardContetnChangeListener. If <tt>h</tt> is <tt>null</tt>, no
	 * action is performed and no exception is thrown.
	 * 
	 * @param h
	 *            the new ClipboardContentChangeListener
	 */
	public void addClipboardContentChangeListener(
			ClipboardContentChangeListener h) {
		if (h != null)
			listeners.add(h);
	}

	/**
	 * Removes a previously added ClipboardContentChangeListener. If <tt>h</tt>
	 * is <tt>null</tt> or <tt>h</tt> has not been added previously by
	 * <tt>addClipboardContentChangeListener</tt> no action is performed and no
	 * exception is thrown.
	 * 
	 * @param h
	 *            the ClipboardContentChangeListener to be removed
	 */
	public void removeClipboardContentChangeListener(
			ClipboardContentChangeListener h) {
		if (h != null)
			listeners.remove(h);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {

		if (clip.equals(clipboard)) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Transferable trans = clip.getContents(null);

			for (ClipboardContentChangeListener l : listeners) {
				l.contentChanged(trans);
			}

			clip.setContents(trans, this);
		}
	}
}
