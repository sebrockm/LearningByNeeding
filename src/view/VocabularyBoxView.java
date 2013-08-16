package view;

import java.awt.BorderLayout;
import java.awt.Image;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.SQLManager;
import model.VocabularyBox;

/**
 * GUI for the VocabularyBox.
 * @author Sebastian Brockmeyer
 *
 */
@SuppressWarnings("serial")
public class VocabularyBoxView extends JFrame 
{
	private final JTabbedPane tabbedPane;
	private final VocabularyBoxCaseView[] cases;
	
	public VocabularyBoxView(VocabularyBox box, Image image, SQLManager manager)
	{
		super();
		this.setIconImage(image);
		//this.setPreferredSize(new Dimension(300, 300));
		
		tabbedPane = new JTabbedPane();
		this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		cases = new VocabularyBoxCaseView[box.getNumberOfCases()];
		for(int i=0; i<cases.length; i++)
		{
			cases[i] = new VocabularyBoxCaseView(box, i, manager);
			tabbedPane.addTab("case " + (i+1), cases[i]);
		}
		tabbedPane.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				for(VocabularyBoxCaseView view : cases)
				{
					view.setText();
				}
				tabbedPane.validate();
				tabbedPane.repaint();
			}		
		});
		this.pack();
	}
}
