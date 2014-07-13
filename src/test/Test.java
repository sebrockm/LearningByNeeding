package test;

import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import model.SQLManager;
import model.VocabularyBox;
import view.SystemTrayView;
import controller.ClipboardManager;
import controller.ContentInserter;

public class Test {

	private static final String dbPath = System.getProperty("user.dir") + File.separator + 
			"res" + File.separator + "test.db";
	private static final String vbPath = System.getProperty("user.dir") + File.separator + "test.vobo";
	private static final String iconPath = System.getProperty("user.dir") + File.separator + 
			"res" + File.separator + "/Lernkartei.gif";

	private static final VocabularyBox box;
	private static final SQLManager sql;
	private static final SystemTrayView view;
	private static final ClipboardManager clip;

	static {
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

		VocabularyBox tmpbox = null;
		SQLManager tmpsql = null;
		SystemTrayView tmpview = null;
		ClipboardManager tmpclip = null;

		while (tmpbox == null) {
			try {
				tmpbox = VocabularyBox.loadFromFile(vbPath);
			} catch (FileNotFoundException e) {
				tmpbox = new VocabularyBox();
				try {
					tmpbox.storeInFile(vbPath);
				} catch (Exception e1) {
					e1.printStackTrace();
					System.exit(0);
				}
			} catch (IOException e) {
				tmpbox = new VocabularyBox();
				try {
					tmpbox.storeInFile(vbPath);
				} catch (Exception e1) {
					e1.printStackTrace();
					System.exit(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		try {
			tmpsql = new SQLManager(dbPath);
			tmpview = new SystemTrayView(iconPath, tmpbox, tmpsql);
			tmpclip = new ClipboardManager();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		box = tmpbox;
		sql = tmpsql;
		view = tmpview;
		clip = tmpclip;
		
		view.setDatabaseFile(dbPath);
		view.setVbFile(vbPath);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					box.storeInFile(view.getVbFile().getAbsolutePath());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					view.displayMessage("ERROR", 
							"writing to " + view.getVbFile().getAbsolutePath() + " failed",
							MessageType.ERROR);
				}
				sql.close();
			}
		});

		clip.addClipboardContentChangeListener(new ContentInserter(box, sql,
				view));

		try {
			while (true) {
				Thread.sleep(60*60*1000L);//one hour
				String file = System.getProperty("user.dir") + File.separator + ".backup" + 
						System.currentTimeMillis() + ".vobo";
				try {
					box.storeInFile(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					view.displayMessage("ERROR", 
							"writing to " + file + " failed",
							MessageType.ERROR);
				}
			}
		} catch (InterruptedException e) {
		}
	}

}
