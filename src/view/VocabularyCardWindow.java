package view;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controller.FinalAction;

import model.VocabularyCard;

/**
 * @author Sebastian Brockmeyer
 *
 */
public class VocabularyCardWindow {
	private final JFrame frame = new JFrame();
	private final JLabel english = new JLabel();
	private final JTable german = new JTable();
	private final JButton turn = new JButton("turn");
	private final JButton right = new JButton("right");
	private final JButton wrong = new JButton("wrong");
	private final JScrollPane scroll = new JScrollPane();
	private final VocabularyCard card;
	
	private FinalAction<Boolean> callback = null;
	private Boolean correct = null;
	
	private void initialize() {
		frame.setVisible(true);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		english.setText(card != null ? card.getEnglish() : "no card inside");
		frame.add(english);
		
		if(card != null) {
			Object[][] data = new Object[card.getGermans().size()][];
			for(int i = 0; i < data.length; i ++) {
				data[i] = card.getGermans().get(i).toArray();
			}
			
			@SuppressWarnings("serial")
			final DefaultTableModel model = new DefaultTableModel(data,
					new String[] { "English", "Deutsch", "Typ", "löschen" }) {
				@Override
				public Class<?> getColumnClass(int id) {
					if (id == 3)
						return Boolean.class;
					return String.class;
				}
			};
			german.setModel(model);
			german.setVisible(false);
			
			scroll.setVisible(false);
			scroll.setViewportView(german);
			frame.add(scroll);
	
			frame.add(turn);
			turn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					english.setVisible(false);
					german.setVisible(true);
					turn.setVisible(false);
					right.setVisible(true);
					wrong.setVisible(true);
					scroll.setVisible(true);
					frame.pack();
				}
			});
	
			right.setVisible(false);
			frame.add(right);
			right.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					correct = true;
					frame.dispose();
				}
			});
			
			wrong.setVisible(false);
			frame.add(wrong);
			wrong.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					correct = false;
					frame.dispose();
				}
			});
			
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent arg0) {
					callback.run(correct);
				}
			});
		}
		frame.setPreferredSize(new Dimension(450, 300));
		frame.pack();
	}
	
	public VocabularyCardWindow(final VocabularyCard card, FinalAction<Boolean> callback) {
		this.card = card;
		this.callback = callback;
		initialize();
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VocabularyCard test = new VocabularyCard("test");
					test.addGerman(new String[] {"Test"});
					VocabularyCardWindow window = new VocabularyCardWindow(test, null);
					window.frame.setVisible(true);
					window.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
