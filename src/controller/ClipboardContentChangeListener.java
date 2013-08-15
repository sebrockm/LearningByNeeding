package controller;

import java.awt.datatransfer.Transferable;
import java.util.EventListener;

/**
 * Instances of this interface can be added to a ClipboardManager. The Method <tt>contentChanged</tt>
 * will be called, when the Clipboard's content has changed.
 */
public interface ClipboardContentChangeListener extends EventListener{

	/**
	 * Method to be invoked, when clipboard's content has changed.
	 * @param content the clipboard's new content
	 */
	public void contentChanged(Transferable content);
	
}
