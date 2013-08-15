package controller;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.LinkedList;
import java.util.List;


public class ClipboardManager implements ClipboardOwner{

	private Clipboard clip;
	private List<ClipboardContentChangeListener> listeners;
	
	private void getOwnership(Transferable trans)
	{
		clip.setContents(trans, this);
	}

	public ClipboardManager()
	{
		clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		listeners = new LinkedList<ClipboardContentChangeListener>();
		getOwnership(clip.getContents(null));
	}
	
	public void addClipboardContentChangeListener(ClipboardContentChangeListener h)
	{
		if(h != null)
			listeners.add(h);
	}
	
	public void removeClipboardContentChangeListener(ClipboardContentChangeListener h)
	{
		if(h != null)
			listeners.remove(h);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		
		if(clip.equals(clipboard))
		{
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Transferable trans = clip.getContents(null);
	
			for(ClipboardContentChangeListener l : listeners)
			{
				l.contentChanged(trans);
			}
			
			clip.setContents(trans, this);
		}
	}
}
