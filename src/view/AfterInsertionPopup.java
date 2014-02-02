package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

import controller.FinalAction;

/**
 * An AfterInsertionPopup shall appear whenever the user added a vocabulary to
 * the VocabularyBox. It shows a table of available translations and checkboxes
 * to chose them. The window automatically disappears three seconds after losing
 * the focus.
 * 
 * @author Sebastian Brockmeyer
 * 
 */
public class AfterInsertionPopup extends JFrame {
	private static final long serialVersionUID = 1L;

	private final JTable table;
	private final Timer timer;
	private final Collection<Integer> chosen;

	private FinalAction<Collection<Integer>> action;

	/**
	 * Creates a new AfterInsertionPopup that will disappear three seconds after
	 * losing the focus.
	 * 
	 * @param data the table of data that shall be displayed
	 */
	public AfterInsertionPopup(String[][] data) {
		this(data, 3000);
	}

	/**
	 * Creates a new AfterInsertionPopup that will disappear displaytime
	 * milliseconds after losing the focus.
	 * 
	 * @param data the table of data that shall be displayed
	 * @param displaytime time in milliseconds after that the window disappears
	 * 						when the focus is lost
	 */
	public AfterInsertionPopup(String[][] data, int displaytime) {
		super();
		chosen = new LinkedList<Integer>();
		action = null;

		final DefaultTableModel model = new DefaultTableModel(data,
				new String[] { "English", "Deutsch", "Typ", "aufnehmen" }) {
			private static final long serialVersionUID = 1L;

			@Override
			public Class<?> getColumnClass(int id) {
				if (id == 3)
					return Boolean.class;
				return String.class;
			}
		};
		model.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getColumn() == 3) {
					int id = e.getFirstRow();
					if ((Boolean) model.getValueAt(id, 3)) {
						chosen.add(id);
					} else {
						chosen.remove((Object) id);
					}
				}
			}
		});

		table = new JTable(model);

		this.add(table);
		this.getContentPane().add(new JScrollPane(table));
		this.setVisible(true);
		this.setSize(table.getPreferredScrollableViewportSize());
		this.setFocusable(true);

		if (displaytime > 0) {
			timer = new Timer(displaytime, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					timer.stop();
					if (action != null) {
						AfterInsertionPopup.this.action.run(chosen);
					}
					AfterInsertionPopup.this.dispose();
				}
			});
			timer.start();

			this.addWindowFocusListener(new WindowFocusListener() {

				@Override
				public void windowLostFocus(WindowEvent arg0) {
					timer.start();
				}

				@Override
				public void windowGainedFocus(WindowEvent arg0) {
					timer.stop();
				}
			});
		} else {
			timer = null;
		}
		
		this.addWindowListener(new WindowAdapter() {
		
			@Override
			public void windowClosing(WindowEvent e) {
				timer.setInitialDelay(1);
				timer.start();		
			}
		});
	}

	/**
	 * Sets the final action that shall be performed when the window disappears.
	 * This action will be performed on all the indexes of the chosen translations.
	 * 
	 * @param action action that will be performed
	 */
	public void setFinalAction(FinalAction<Collection<Integer>> action) {
		this.action = action;
	}
	
	/**
	 * Adds a translations's index, so it will be shown as selected.
	 * @param id of the
	 */
	public void addSelected(int id) {
		table.getModel().setValueAt(Boolean.TRUE, id, 3);
	}
}
