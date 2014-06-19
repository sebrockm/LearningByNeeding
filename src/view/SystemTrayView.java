package view;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.SQLManager;
import model.VocabularyBox;

/**
 * 
 * @author Sebastian Brockmeyer
 * 
 */
public class SystemTrayView extends TrayIcon {
	private final JPopupMenu popup;
	private final JMenuItem openVocabularyBoxItem;
	private final JCheckBoxMenuItem autoInsertItem;
	private final JMenuItem undoLastInsertItem;
	private final JMenuItem openDatabaseChooser;
	private final JMenuItem openVbChooser;
	private final JMenuItem exitItem;

	//private final VocabularyBoxView vboxView;
	private final VocabularyBoxWindow vboxView;
	private final SQLManager manager;
	private final JFileChooser databaseChooser;
	private final JFileChooser vbChooser;
	
	private File databaseFile = null;
	private File vbFile = null;

	private void handleAutoInsertStateChange(boolean on) {
		String message = "By Ctrl+C copied text is " + (on ? "" : "no longer ")
				+ "automatically inserted into vocabulary box now.";
		this.displayMessage(null, message, MessageType.INFO);
	}

	/**
	 * Creates an icon in the system tray providing a popup with some
	 * functionality.
	 * 
	 * @param iconPath
	 *            path to an image file used for the icon
	 */
	public SystemTrayView(String iconPath, VocabularyBox box, SQLManager manager) {
		super(new ImageIcon(iconPath).getImage(), "LearningByNeeding", null);

		if (!SystemTray.isSupported())
			throw new RuntimeException("system tray is not supported");

		this.manager = manager;
		
		//vboxView = new VocabularyBoxWindow(box, this.getImage(), manager);
		vboxView = new VocabularyBoxWindow(box, this.getImage());
		vboxView.setVisible(false);
		
		databaseChooser = new JFileChooser(new File(System.getProperty("user.dir")));
		databaseChooser.setFileFilter(new FileNameExtensionFilter("databases", "db"));
		vbChooser = new JFileChooser(new File(System.getProperty("user.dir")));
		vbChooser.setFileFilter(new FileNameExtensionFilter("vocabulary boxes", "vobo"));

		this.setImageAutoSize(true);

		popup = new JPopupMenu();
		openVocabularyBoxItem = new JMenuItem("open vocabulary box");
		autoInsertItem = new JCheckBoxMenuItem("auto insert on Ctrl+C", true);
		openDatabaseChooser = new JMenuItem("choose database");
		openVbChooser = new JMenuItem("choose vocabulary box");
		undoLastInsertItem = new JMenuItem("undo last insert");
		exitItem = new JMenuItem("exit");

		openVocabularyBoxItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				vboxView.setVisible(true);
				vboxView.updateText();
			}
		});
		autoInsertItem.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				handleAutoInsertStateChange(arg0.getStateChange() == ItemEvent.SELECTED);
			}
		});
		openDatabaseChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(databaseChooser.showOpenDialog(openDatabaseChooser) == JFileChooser.APPROVE_OPTION) {
					setDatabaseFile(databaseChooser.getSelectedFile().getAbsoluteFile());
				}
			}		
		});
		openVbChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(vbChooser.showOpenDialog(openDatabaseChooser) == JFileChooser.APPROVE_OPTION) {
					setVbFile(vbChooser.getSelectedFile().getAbsoluteFile());
				}
			}
		});
		undoLastInsertItem.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		popup.setLabel("LerningByNeeding");
		popup.add(openVocabularyBoxItem);
		popup.add(autoInsertItem);
		popup.addSeparator();
		popup.add(openDatabaseChooser);
		popup.add(openVbChooser);
		popup.addSeparator();
		popup.add(exitItem);


		// hack for TrayIcon working with swing
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					popup.setLocation(e.getX(), e.getY());
					popup.setInvoker(popup);
					popup.setVisible(true);
				}
			}
		});
		popup.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				if (!popup.getBounds().contains(e.getPoint())) {
					popup.setVisible(false);
				}
			}
		});

		try {
			SystemTray.getSystemTray().add(this);
		} catch (AWTException e) {
			throw new RuntimeException("unable to add tray icon to system tray");
		}

	}

	/**
	 * Determines if the 'auto insert on Ctrl+C' option is selected.
	 * 
	 * @return true if 'auto insert on Ctrl+C' is selected, false otherwise
	 */
	public boolean isAutoInsertOnCtrlCSelected() {
		return autoInsertItem.isSelected();
	}
	
	public void setDatabaseFile(File file) {
		databaseFile = file;
		databaseChooser.setSelectedFile(file);
		manager.switchDatabase(file.getAbsolutePath());
	}
	
	public void setVbFile(File file) {
		vbFile = file;
		vbChooser.setSelectedFile(file);
	}
	
	public void setDatabaseFile(String file) {
		setDatabaseFile(new File(file));
	}
	
	public void setVbFile(String file) {
		setVbFile(new File(file));
	}
	
	public File getDatabaseFile() {
		return databaseFile;
	}
	
	public File getVbFile() {
		return vbFile;
	}
}
