package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;

import model.VocabularyBox;
import javax.swing.BoxLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

public class VocabularyBoxWindow {

	private JFrame frame;
	private VocabularyBox box;
	private JPanel[] panels;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VocabularyBoxWindow window = new VocabularyBoxWindow(VocabularyBox.loadFromFile("test.vobo"));
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public VocabularyBoxWindow(VocabularyBox box) {
		this.box = box;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage("/images/Lernkartei.gif"));
		frame.setPreferredSize(new Dimension(450, 300));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		frame.getContentPane().add(tabbedPane);
		
		int caseCount = box==null ? 5 : box.getNumberOfCases();
		panels = new JPanel[caseCount];
		
		for(int i = 0; i < caseCount; i++) {
			panels[i] = new JPanel();
			tabbedPane.addTab("case " + (i+1), panels[i]);
			panels[i].setLayout(new BoxLayout(panels[i], BoxLayout.PAGE_AXIS));
			panels[i].add(new JLabel(box.getCaseVolumes()[i] + " vocabularies in case " + (i+1)));
			panels[i].add(new JLabel("vokabel"));
			JButton open = new JButton("open");
			panels[i].add(open);
			open.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
		frame.pack();
	}

}
