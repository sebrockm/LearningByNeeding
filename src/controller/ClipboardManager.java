package controller;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import model.VocabularyBox;

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
	
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException
	{
		ClipboardManager h = new ClipboardManager();
		final model.SQLManager sqlm = new model.SQLManager("test.db");
		
		
		h.addClipboardContentChangeListener(new ClipboardContentChangeListener() {
			
			@Override
			public void contentChanged(Transferable content) {
				try{
					if(content.isDataFlavorSupported(DataFlavor.stringFlavor))
					{
						String vocab = (String)content.getTransferData(DataFlavor.stringFlavor);
						List<String[]> l = sqlm.searchForEnglish(vocab, true);
						if(l.isEmpty())
						{
							System.out.println(vocab + " not found!");
						}
						
						int count = 0;
						for(String[] s : l)
						{
							System.out.println(s[0] + " - " + s[1] + "\t" + s[2]);
							if(++count >= 10)
								break;
						}
					}
				}
				catch(UnsupportedFlavorException e)
				{
					//should not occur
				}
				catch(IOException e)
				{
					e.printStackTrace();
				} 
				catch (SQLException e) 
				{
					e.printStackTrace();
				}
				
			}
		});
		
		
		final model.VocabularyBox box = new VocabularyBox();
		
		h.addClipboardContentChangeListener(new ClipboardContentChangeListener() {
			
			@Override
			public void contentChanged(Transferable content) {
				try{
					if(content.isDataFlavorSupported(DataFlavor.stringFlavor))
					{
						String vocab = (String) content.getTransferData(DataFlavor.stringFlavor);
						box.insert(vocab);
						
						int[] dims = box.getCaseVolumes();
						for(int i=0; i<dims.length; i++)
						{
							System.out.println("case " + i + ": " + dims[i]);
						}
					}
				}
				catch(UnsupportedFlavorException e)
				{
					//should not occur
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				
			}
		});
		
		while(true){
			try {
				Thread.sleep(10000000000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
