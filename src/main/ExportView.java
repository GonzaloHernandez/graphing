package main;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ExportView extends JPanel {

	//-------------------------------------------------------------------------------------

	protected	GrapherMain	main;
	protected	JCheckBox	programmingView;
	protected	JTextArea	info;
	
	//-------------------------------------------------------------------------------------

	public ExportView(GrapherMain main){
		this.main	= main;
		initElements();
		progListeneres();
	}
	
	//-------------------------------------------------------------------------------------

	private void initElements(){
		programmingView		= new JCheckBox("Programming view");
		info				= new JTextArea();
	
		info.setWrapStyleWord(true);
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		add(programmingView);
		add(new JLabel("Adjacency matrix"));
		add(new JScrollPane(info));

		info.setEditable(false);
	}
	
	//-------------------------------------------------------------------------------------

	private void progListeneres(){
		programmingView.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.settings.programmingView	= programmingView.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
	}

	//-------------------------------------------------------------------------------------

	public void refresh(){
		programmingView.setSelected(main.currentSession.board.settings.programmingView);
	}

}
