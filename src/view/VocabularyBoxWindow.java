package view;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;

import model.VocabularyBox;
import javax.swing.BoxLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import controller.FinalAction;

@SuppressWarnings("serial")
public class VocabularyBoxWindow extends JFrame {

	private VocabularyBox box;
	private JPanel[] panels;
	private JLabel[] labels;

	/**
	 * Create the application.
	 */
	public VocabularyBoxWindow(VocabularyBox box, Image image) {
		super("Vocabulary Box");
		this.setIconImage(image);
		this.box = box;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		this.setPreferredSize(new Dimension(450, 300));
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		this.getContentPane().add(tabbedPane);
		
		int caseCount = box==null ? 5 : box.getNumberOfCases();
		panels = new JPanel[caseCount];
		labels = new JLabel[caseCount];
		
		for(int i = caseCount-1; i >= 0; i--) {
			panels[i] = new JPanel();
			tabbedPane.addTab("case " + (i+1), panels[i]);
			panels[i].setLayout(new BoxLayout(panels[i], BoxLayout.PAGE_AXIS));
			labels[i] = new JLabel();
			panels[i].add(labels[i]);
			final JButton open = new JButton("open");
			panels[i].add(open);
			
			final JComboBox<Integer> dropdown = new JComboBox<>(new Integer[] {0, 5, 10, 15, 20, 25});
			if(i == 0) {
				panels[i].add(dropdown);
			}
			
			final int ii = i;
			
			final JButton shuffle = new JButton("shuffle");
			panels[i].add(shuffle);
			shuffle.addActionListener(new ActionListener() {	
				@Override
				public void actionPerformed(ActionEvent e) {
					box.shuffleCase(ii);
				}
			});
			
			open.addActionListener(new ActionListener() {		
				@Override
				public void actionPerformed(ActionEvent arg0) {
					shuffle.setEnabled(false);
					
					final ActionListener fthis = this;
					new VocabularyCardWindow(box.getNextVocabInCase(ii), new FinalAction<Boolean>() {
						@Override
						public void run(Boolean b) {
							shuffle.setEnabled(true);
							
							//remove card if all translations have been removed
							if(box.getNextVocabInCase(ii).getGermans().isEmpty()) {
								box.remove(box.getNextVocabInCase(ii));
								setText();
							} else if(b != null) {
								if(ii == 0 && !dropdown.getSelectedItem().equals(new Integer(0))) {
									box.answerVocabInCaseZeroWithLimit((Integer)dropdown.getSelectedItem()-1, b);
								}
								else {
									box.answerVocabInCase(ii, b);
								}
								setText();
								fthis.actionPerformed(null);//recursively invoke new card
							}
						}
					});
				}
			});
			
			
		}
		setText();
		this.pack();
	}

	private void setText() {
		for(int i = 0; i < labels.length; i++) {
			labels[i].setText(box.getCaseVolumes()[i] + " vocabularies in case " + (i+1));
		}
	}
	
	public void updateText() {
		setText();
	}
}
