package view;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.swing.ImageIcon;

public class SystemTrayView {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(SystemTray.isSupported())
		{
			final PopupMenu popup = new PopupMenu();
			
			ImageIcon ii = new ImageIcon("images/Lernkartei.gif");
			Image im = ii.getImage();
			final TrayIcon trayIcon = new TrayIcon(im);
			final SystemTray tray = SystemTray.getSystemTray();
			
			
			// Create a pop-up menu components
	        MenuItem aboutItem = new MenuItem("About");
	        CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
	        CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
	        Menu displayMenu = new Menu("Display");
	        MenuItem errorItem = new MenuItem("Error");
	        MenuItem warningItem = new MenuItem("Warning");
	        MenuItem infoItem = new MenuItem("Info");
	        MenuItem noneItem = new MenuItem("None");
	        MenuItem exitItem = new MenuItem("Exit");
	       
	        //Add components to pop-up menu
	        popup.add(aboutItem);
	        popup.addSeparator();
	        popup.add(cb1);
	        popup.add(cb2);
	        popup.addSeparator();
	        popup.add(displayMenu);
	        displayMenu.add(errorItem);
	        displayMenu.add(warningItem);
	        displayMenu.add(infoItem);
	        displayMenu.add(noneItem);
	        popup.add(exitItem);
	       
	        trayIcon.setPopupMenu(popup);
			
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				e.printStackTrace();
			}
			
			
			while(true){
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else
		{
			System.out.println("SystemTray is not supported.");
		}
	}

}
