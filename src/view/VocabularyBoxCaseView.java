package view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import model.SQLManager;
import model.VocabularyBox;

@SuppressWarnings("serial")
public class VocabularyBoxCaseView extends JPanel
{
	private final int caseNo;
	private final VocabularyBox box;
	private final SQLManager sql;
	
	private final JLabel remaining;
	private final JLabel card;
	private final JPanel buttonPanel;
	private final JButton turn;
	private final JButton wrong;
	private final JButton correct;
	private final JButton edit;

	private void setListeners()
	{
		turn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(box.getCaseVolumes()[caseNo] > 0)
				{
					turn.setVisible(false);
					wrong.setVisible(true);
					correct.setVisible(true);
					
					setText();
				}
			}	
		});
		
		correct.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				box.answerVocabInCase(caseNo, true);
				
				turn.setVisible(true);
				wrong.setVisible(false);
				correct.setVisible(false);
				
				setText();
			}
		});
		
		wrong.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				box.answerVocabInCase(caseNo, false);

				turn.setVisible(true);
				wrong.setVisible(false);
				correct.setVisible(false);
				
				setText();
			}
		});
		
		edit.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO 
				
			}
		});
	}
	
	private String getRemainingText()
	{
		int cardCount = box.getCaseVolumes()[caseNo];
		return cardCount + " card" + (cardCount==1?"":"s") + " in this case";
	}
	
	public VocabularyBoxCaseView(VocabularyBox box, int caseNo, SQLManager manager)
	{
		super(true);
		this.box = box;
		this.caseNo = caseNo;
		this.sql = manager;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		remaining = new JLabel();
		card = new JLabel();
		card.setPreferredSize(new Dimension(600, 800));
		
		buttonPanel = new JPanel(true);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		correct = new JButton("correct");
		turn = new JButton("turn");
		wrong = new JButton("wrong");
		edit = new JButton("edit");
		buttonPanel.add(correct);
		buttonPanel.add(turn);
		buttonPanel.add(edit);
		buttonPanel.add(wrong);
		
		add(remaining);
		add(card);
		add(buttonPanel);
		
		turn.setVisible(true);
		wrong.setVisible(false);
		correct.setVisible(false);
		
		//setPreferredSize(new Dimension(700, 1000));
		
		setListeners();
		setText();
	}
	
	public void setText()
	{
		remaining.setText(getRemainingText());
		
		if(turn.isVisible())
		{
			if(box.getCaseVolumes()[caseNo] > 0)
			{
				card.setText(box.getNextVocabInCase(caseNo).getEnglish());
			}
		}
		else
		{
		}
	}
}
