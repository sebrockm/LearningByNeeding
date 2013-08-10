package view;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;


public class SystemTrayView {
	
	public class SystemTrayNotSupportedException extends Exception
	{
		private static final long serialVersionUID = -3088524721741229446L;
		public SystemTrayNotSupportedException(String info)
		{
			super(info);
		}
	}

	private void handleAutoInsertStateChange(boolean on)
	{
		String message = "By Ctrl+C copied text is " + (on?"":"no longer ") + "automatically inserted into vocabulary box now.";
		trayIcon.displayMessage(null, message, MessageType.INFO);
	}
	
	private final TrayIcon trayIcon;
	private final JPopupMenu popup;
	private final JMenuItem openVocabularyBoxItem;
	private final JMenuItem manuelInsertItem;
	private final JCheckBoxMenuItem autoInsertItem;
	private final JMenuItem exitItem;
	
	public SystemTrayView(String iconPath) throws SystemTrayNotSupportedException
	{
		if(!SystemTray.isSupported())
			throw new SystemTrayNotSupportedException("system tray is not supported");
		

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		

		trayIcon = new TrayIcon(new ImageIcon(iconPath).getImage(), "LearningByNeeding", null);
		trayIcon.setImageAutoSize(true);
		
		popup = new JPopupMenu();
		openVocabularyBoxItem = new JMenuItem("open vocabulary box");
		manuelInsertItem = new JMenuItem("manuel insert");
		autoInsertItem = new JCheckBoxMenuItem("auto insert on Ctrl+C", true);
		exitItem = new JMenuItem("exit");
		
		
		openVocabularyBoxItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				trayIcon.displayMessage(null, "this function is not implemented yet", MessageType.INFO);	
			}			
		});		
		manuelInsertItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				trayIcon.displayMessage(null, "this function is not implemented yet", MessageType.INFO);	
			}			
		});
		autoInsertItem.addItemListener(new ItemListener() {			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				handleAutoInsertStateChange(arg0.getStateChange() == ItemEvent.SELECTED);				
			}
		});
		exitItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SystemTray.getSystemTray().remove(trayIcon);
				System.exit(0);
			}
		});
	
		popup.setLabel("LerningByNeeding");
		popup.add(openVocabularyBoxItem);
		popup.addSeparator();
		popup.add(manuelInsertItem);
		popup.add(autoInsertItem);
		popup.addSeparator();		
		popup.add(exitItem);
		
		//hack for TrayIcon working with swing
		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(e.getButton() == MouseEvent.BUTTON3)
				{
					popup.setLocation(e.getX(), e.getY());
					popup.setInvoker(popup);
					popup.setVisible(true);
				}
			}
		});
		popup.addMouseListener(new MouseAdapter() {		
			@Override
			public void mouseExited(MouseEvent e) {	
				if(!popup.getBounds().contains(e.getPoint()))
				{
					popup.setVisible(false);	
				}
			}
		});
		
		
		try {
			SystemTray.getSystemTray().add(trayIcon);
		} catch (AWTException e) {
			throw new SystemTrayNotSupportedException("unable to add tray icon to system tray");
		}
		
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				try {
					new SystemTrayView("images/Lernkartei.gif");
				} catch (SystemTrayNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}
