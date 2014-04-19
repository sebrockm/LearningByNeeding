package view;

import java.awt.Dimension;
import java.awt.Image;

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
		super();
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
		
		for(int i = 0; i < caseCount; i++) {
			panels[i] = new JPanel();
			tabbedPane.addTab("case " + (i+1), panels[i]);
			panels[i].setLayout(new BoxLayout(panels[i], BoxLayout.PAGE_AXIS));
			labels[i] = new JLabel();
			panels[i].add(labels[i]);
			final JButton open = new JButton("open");
			panels[i].add(open);
			
			final int ii = i;
			final ActionListener al = new ActionListener() {		
				@Override
				public void actionPerformed(ActionEvent arg0) {
					final ActionListener fthis = this;
					new VocabularyCardWindow(box.getNextVocabInCase(ii), new FinalAction<Boolean>() {
						@Override
						public void run(Boolean b) {
							if(b != null) {
								box.answerVocabInCase(ii, b);
								setText();
								fthis.actionPerformed(null);
							}
						}
					});
				}
			};
			open.addActionListener(al);
		}
		setText();
		this.pack();
	}

	private void setText() {
		for(int i = 0; i < labels.length; i++) {
			labels[i].setText(box.getCaseVolumes()[i] + " vocabularies in case " + (i+1));
		}
	}
}
