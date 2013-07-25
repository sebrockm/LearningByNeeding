/**
 * 
 */
package core;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.sql.*;

/**
 * @author poppes
 *
 */
public class DataManager {
	public DataManager(){
		final Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		clip.addFlavorListener(new FlavorListener(){

			@Override
			public void flavorsChanged(FlavorEvent arg0) {
				
			}
			
		});
	}
}
