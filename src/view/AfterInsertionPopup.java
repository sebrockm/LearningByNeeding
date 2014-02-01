package view;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

import controller.FinalAction;


public class AfterInsertionPopup extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private final JTable table;
	private final Timer timer;
	private final LinkedList<Integer> chosen;

	private FinalAction<LinkedList<Integer>> action;
	
	public AfterInsertionPopup(List<String[]> data)
	{
		this(data, 3000);
	}
	
	public AfterInsertionPopup(List<String[]> data, int displaytime)
	{
		super();
		chosen = new LinkedList<Integer>();
		action = null;
		
		final DefaultTableModel model = new DefaultTableModel(
				data.toArray(new String[data.size()][]), new String[]{"English", "Deutsch", "Typ", "aufnehmen"})
		{
			private static final long serialVersionUID = 1L;
			@Override
			public Class<?> getColumnClass(int id)
			{
				if(id == 3)
					return Boolean.class;
				return String.class;
			}
		};
		model.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				if(e.getColumn() == 3)
				{
					int id = e.getFirstRow();
					if((Boolean)model.getValueAt(id, 3))
					{
						chosen.add(id);
					}
					else
					{
						chosen.remove((Object)id);
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
		
		if(displaytime > 0)
		{
			timer = new Timer(displaytime, new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(action != null)
					{
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
		}
		else
		{
			timer = null;
		}
	}
	
	public void setFinalAction(FinalAction<LinkedList<Integer>> action)
	{
		this.action = action;
	}
}
