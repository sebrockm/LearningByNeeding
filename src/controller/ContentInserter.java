package controller;

import java.awt.TrayIcon.MessageType;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import view.SystemTrayView;
import view.AfterInsertionPopup;

import model.SQLManager;
import model.VocabularyBox;
import model.VocabularyBox.VocabularyCard;


/**
 * This class implements <tt>ClipboardContentChangeListener</tt> so it can be added to a
 * ClipboardManager. On content change the new content (if it is a String) will be inserted 
 * into a VocabularyBox only if 'auto insert on Ctrl+C' is selected. In that case the SystemTrayView additionally 
 * displays a message that includes the translation looked up in a database or an information that the box
 * includes the vocabulary already.
 * 
 * @author Sebastian Brockmeyer
 *
 */
public class ContentInserter implements ClipboardContentChangeListener
{
	private VocabularyBox box;
	private SystemTrayView view;
	private SQLManager manager;
	private AfterInsertionPopup popup;
	
	/**
	 * Creates a new ContentInserter.
	 * @param box the <tt>VocabularyBox</tt> the new contents will be inserted into as vocabularies 
	 * @param manager the <tt>SQLManager</tt> where the translation is looked up
	 * @param view the SystemTrayView that shall show the messages
	 */
	public ContentInserter(VocabularyBox box, SQLManager manager, SystemTrayView view)
	{
		this.box = box;
		this.view = view;
		this.manager = manager;
	}
	
	@Override
	public void contentChanged(Transferable content) {
		if(!view.isAutoInsertOnCtrlCSelected())
			return;
		
		try{
			if(content.isDataFlavorSupported(DataFlavor.stringFlavor))
			{
				String display = "";
				String vocabu = (String) content.getTransferData(DataFlavor.stringFlavor);
				final String vocab = vocabu.trim();
					
				final List<String[]> germans = manager.searchForEnglish(vocab, true);
				
				int caseNo = box.find(vocab);
				if(caseNo < 0)
				{
					if(germans.isEmpty())
					{
						display = "'" + vocab + "' was not found in database";
						view.displayMessage(null, display, MessageType.WARNING);
					}
					else
					{
						popup = new AfterInsertionPopup(germans);
						popup.setFinalAction(new FinalAction<LinkedList<Integer>>(){

							@Override
							public void run(LinkedList<Integer> param) {
								VocabularyCard card = box.new VocabularyCard(vocab);
								box.insert(card);
								for(int i : param)
								{
									card.addGerman(germans.get(i));
								}
							}
							
						});
						
					}
				}
				else
				{
					popup = new AfterInsertionPopup(germans);
					display = "'" + vocab + "' is already in the vocabulary box in case " + (caseNo+1);
					view.displayMessage(null, display, MessageType.INFO);
					//TODO display card
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
