package view;

import java.awt.CardLayout;

import javax.swing.*;

/**
 * GUI for the VocabularyBox.
 * @author Sebastian Brockmeyer
 *
 */
@SuppressWarnings("serial")
public class VocabularyBoxView extends JFrame 
{
	public VocabularyBoxView()
	{
		super();
		this.setBounds(200, 200, 500, 500);
		this.setLayout(new CardLayout(10, 10));
	}
}
