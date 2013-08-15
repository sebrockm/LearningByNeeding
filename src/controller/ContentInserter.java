package controller;

import java.awt.TrayIcon.MessageType;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import view.SystemTrayView;

import model.SQLManager;
import model.VocabularyBox;

public class ContentInserter implements ClipboardContentChangeListener
{
	private VocabularyBox box;
	private SystemTrayView view;
	private SQLManager manager;
	
	public ContentInserter(VocabularyBox box, SQLManager manager, SystemTrayView view)
	{
		this.box = box;
		this.view = view;
		this.manager = manager;
	}
	
	@Override
	public void contentChanged(Transferable content) {
		if(!view.getAutoInsertOnCtrlC())
			return;
		
		try{
			if(content.isDataFlavorSupported(DataFlavor.stringFlavor))
			{
				String display;
				String vocab = (String) content.getTransferData(DataFlavor.stringFlavor);
				vocab = vocab.trim();
				
				if(box.insert(vocab))
				{
					display = "'" + vocab + "' has been inserted into vocabulary box";
					List<String[]> germans = manager.searchForEnglish(vocab, true);
					if(germans.isEmpty())
					{
						display += ", but was not found in database";
						view.displayMessage(null, display, MessageType.WARNING);
					}
					else
					{
						for(int i=0; i<10 && i<germans.size(); i++)
						{
							String[] line = germans.get(i);
							display += "\n" + line[0] + " - " + line[1] + "\t" + line[2];
						}
						view.displayMessage(null, display, MessageType.NONE);
					}
				}
				else
				{
					display = "'" + vocab + "' is already in the vocabulary box";
					view.displayMessage(null, display, MessageType.INFO);
				}
				
				System.out.println(display);// for debugging
			}
		}
		catch(UnsupportedFlavorException e)
		{
			e.printStackTrace();
			throw new RuntimeException("UnsupportedFlavorException occured:\n" + e.getMessage());
		}
		catch(IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException("IOException occured:\n" + e.getMessage());
		}
		
	}

}
