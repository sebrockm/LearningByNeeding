package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.FlowLayout;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;

import model.VocabularyBox;
import javax.swing.SpringLayout;
import javax.swing.BoxLayout;
import java.awt.Toolkit;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JSplitPane;
import java.awt.Dimension;
import java.awt.GridLayout;

public class VocabularyBoxWindow {

	private JFrame frame;
	private VocabularyBox box;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VocabularyBoxWindow window = new VocabularyBoxWindow(null);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public VocabularyBoxWindow(VocabularyBox box) {
		this.box = box;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage("/home/poppes/LearningByNeeding/images/Lernkartei.gif"));
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		frame.getContentPane().add(tabbedPane);
		
		JPanel panel_0 = new JPanel();
		tabbedPane.addTab("case 1", null, panel_0, null);
		panel_0.setLayout(new GridLayout(3, 1, 0, 0));
		
		JLabel lblNewLabel = new JLabel("vocabularies inside");
		panel_0.add(lblNewLabel);
		
		JLabel lblVokabel = new JLabel("vokabel");
		panel_0.add(lblVokabel);
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(100, 100));
		panel_0.add(panel);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("case 2", null, panel_1, null);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("case 3", null, panel_2, null);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("case 4", null, panel_3, null);
		
		JPanel panel_4 = new JPanel();
		tabbedPane.addTab("case 5", null, panel_4, null);
		

	}

}
