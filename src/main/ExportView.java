package main;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ExportView extends JPanel {

	//-------------------------------------------------------------------------------------

	protected	GrapherMain	main;
	protected	JComboBox<String>	programmingView;
	protected	JTextArea			info;
	
	//-------------------------------------------------------------------------------------

	public ExportView(GrapherMain main){
		this.main	= main;
		initElements();
		progListeneres();
	}
	
	//-------------------------------------------------------------------------------------

	private void initElements(){
		String views[]={"Java view","Python view","MiniZinc view"};
		programmingView		= new JComboBox<>(views);
		info				= new JTextArea();
	
		info.setWrapStyleWord(true);
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		add(programmingView);
		add(new JLabel("Representation"));
		add(new JScrollPane(info));

		info.setEditable(false);
	}
	
	//-------------------------------------------------------------------------------------

	private void progListeneres(){
		programmingView.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.settings.programmingView	= programmingView.getSelectedIndex();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
	}

	//-------------------------------------------------------------------------------------

	public void refresh(){
		programmingView.setSelectedIndex(main.currentSession.board.settings.programmingView);
	}

}
