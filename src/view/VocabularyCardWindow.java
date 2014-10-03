package view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import controller.FinalAction;

import model.VocabularyCard;

/**
 * @author Sebastian Brockmeyer
 *
 */
public class VocabularyCardWindow {
	private final JFrame frame = new JFrame("Vocabulary Card");
	private final JLabel english = new JLabel();
	private final JTable german = new JTable();
	private final JButton turn = new JButton("turn");
	private final JButton right = new JButton("right");
	private final JButton wrong = new JButton("wrong");
	private final JScrollPane scroll = new JScrollPane();
	private final VocabularyCard card;
	
	private FinalAction<Boolean> callback = null;
	private Boolean correct = null;
	
	@SuppressWarnings("serial")
	private void initialize() {
		frame.setVisible(true);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		english.setText(card != null ? card.getEnglish() : "no card inside");
		frame.add(english);
		
		if(card != null) {
			Object[][] data = new Object[card.getGermans().size()][];
			for(int i = 0; i < data.length; i++) {
				data[i] = card.getGermans().get(i).toArray();
			}
			
			final DefaultTableModel model = new DefaultTableModel(data,
					new String[] { "English", "Deutsch", "Typ", "löschen", "use"}) {
				@Override
				public Class<?> getColumnClass(int id) {
					if (id == 3 || id == 4)
						return Boolean.class;
					return String.class;
				}
			};
			final LinkedList<List<String>> toDelete = new LinkedList<>();
			model.addTableModelListener(new TableModelListener() {
				@Override
				public void tableChanged(TableModelEvent arg0) {
					if(arg0.getType() == TableModelEvent.UPDATE) {
						if(arg0.getColumn() == 3) {
							if(model.getValueAt(arg0.getFirstRow(), arg0.getColumn()).equals(Boolean.FALSE)) {
								toDelete.remove(card.getGermans().get(arg0.getFirstRow()));
							}
							else{
								toDelete.add(card.getGermans().get(arg0.getFirstRow()));
							}
						} else if(arg0.getColumn() == 4) {
							if(model.getValueAt(arg0.getFirstRow(), 4).equals(Boolean.TRUE)) {
								//unset all others
								for(int i = 0; i < model.getRowCount(); ++i) {
									if(i != arg0.getFirstRow()) {
										model.setValueAt(false, i, 4);
									}
								}
								String nEng = (String)model.getValueAt(arg0.getFirstRow(), 0);
								System.out.println("new Enlish: " + nEng);
								card.setEnglish(nEng);
							}
						}
					}
				}
			});
			
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
			turn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('t'), "turn");
			turn.getActionMap().put("turn", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					turn.doClick();
				}
			});
			
			right.setVisible(false);
			frame.add(right);
			right.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					correct = true;
					for(List<String> del : toDelete) {
						card.removeGerman((String[])del.toArray());
					}
					frame.dispose();
				}
			});
			right.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('r'), "right");
			right.getActionMap().put("right", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					right.doClick();
				}
			});
			
			wrong.setVisible(false);
			frame.add(wrong);
			wrong.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					correct = false;
					for(List<String> del : toDelete) {
						if(card.removeGerman((String[])del.toArray()))
							System.out.println("removed " + del.get(0));
						else System.out.println("failed to remove " + del.get(0));
					}
					frame.dispose();
				}
			});
			wrong.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('w'), "wrong");
			wrong.getActionMap().put("wrong", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					wrong.doClick();
				}
			});
			
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent arg0) {
					callback.run(correct);
				}
			});
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		frame.setPreferredSize(new Dimension(450, 300));
		frame.pack();
		frame.requestFocus();
	}
	
	public VocabularyCardWindow(final VocabularyCard card, FinalAction<Boolean> callback) {
		this.card = card;
		this.callback = callback;
		initialize();
	}

}
