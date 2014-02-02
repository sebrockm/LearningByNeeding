package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

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
	private final JMenuItem exitItem;

	private final VocabularyBoxView vboxView;

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

		vboxView = new VocabularyBoxView(box, this.getImage(), manager);
		vboxView.setVisible(false);

		this.setImageAutoSize(true);

		popup = new JPopupMenu();
		openVocabularyBoxItem = new JMenuItem("open vocabulary box");
		autoInsertItem = new JCheckBoxMenuItem("auto insert on Ctrl+C", true);
		undoLastInsertItem = new JMenuItem("undo last insert");
		exitItem = new JMenuItem("exit");

		openVocabularyBoxItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				vboxView.setVisible(true);
			}
		});
		autoInsertItem.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				handleAutoInsertStateChange(arg0.getStateChange() == ItemEvent.SELECTED);
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
}
