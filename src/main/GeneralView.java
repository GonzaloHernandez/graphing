package main;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class GeneralView extends JPanel {

	//-------------------------------------------------------------------------------------

	protected	GrapherMain	main;
	protected	JCheckBox	nameTypes;
	protected	JCheckBox	numberStates;
	protected	JTextArea	comment;
	
	//-------------------------------------------------------------------------------------

	public GeneralView(GrapherMain main){
		this.main	= main;
		initElements();
		progListeneres();
	}
	
	//-------------------------------------------------------------------------------------

	private void initElements(){
		nameTypes		= new JCheckBox("Vew type names");
		numberStates	= new JCheckBox("View state numbers");
		comment			= new JTextArea();
	
		comment.setWrapStyleWord(true);
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		add(nameTypes);
		add(numberStates);
		add(new JLabel("Comment"));
		add(new JScrollPane(comment));
	}
	
	//-------------------------------------------------------------------------------------

	private void progListeneres(){
		nameTypes.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.settings.nameTypes	= nameTypes.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		numberStates.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.settings.numberStates	= numberStates.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		comment.addCaretListener(new CaretListener(){

			public void caretUpdate(CaretEvent arg0) {
				main.currentSession.board.settings.comment = comment.getText();
				main.currentSession.setModified(true);
			}
		});
	
	}

	//-------------------------------------------------------------------------------------

	public void refresh(){
		nameTypes.setSelected(main.currentSession.board.settings.nameTypes);
		numberStates.setSelected(main.currentSession.board.settings.numberStates);
		comment.setText(main.currentSession.board.settings.comment);
	}

}
