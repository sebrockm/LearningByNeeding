package view;

import java.awt.CardLayout;

import javax.swing.*;

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
