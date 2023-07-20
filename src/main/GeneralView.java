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
	protected	JCheckBox	showTypeNames;
	protected	JCheckBox	showStateNumbers;
	protected	JCheckBox	showStateValues;
	protected	JCheckBox	allowStateZero;
	protected	JTextArea	comment;
	
	//-------------------------------------------------------------------------------------

	public GeneralView(GrapherMain main){
		this.main	= main;
		initElements();
		progListeneres();
	}
	
	//-------------------------------------------------------------------------------------

	private void initElements(){
		showTypeNames		= new JCheckBox("View type names");
		showStateNumbers	= new JCheckBox("View state numbers");
		showStateValues		= new JCheckBox("View state Values");
		allowStateZero		= new JCheckBox("Allow state Zero");
		comment				= new JTextArea();
	
		comment.setWrapStyleWord(true);
		comment.setLineWrap(true);
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		add(showTypeNames);
		add(showStateNumbers);
		add(showStateValues);
		add(allowStateZero);
		add(new JLabel("Comment"));
		add(new JScrollPane(comment));
	}
	
	//-------------------------------------------------------------------------------------

	private void progListeneres(){
		showTypeNames.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.settings.showTypeNames	= showTypeNames.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		showStateNumbers.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.settings.showStateNumbers	= showStateNumbers.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		showStateValues.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.settings.showStateValues	= showStateValues.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		allowStateZero.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.settings.allowStateZero	= allowStateZero.isSelected();
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
		showTypeNames.setSelected(main.currentSession.board.settings.showTypeNames);
		showStateNumbers.setSelected(main.currentSession.board.settings.showStateNumbers);
		showStateValues.setSelected(main.currentSession.board.settings.showStateValues);
		allowStateZero.setSelected(main.currentSession.board.settings.allowStateZero);
		comment.setText(main.currentSession.board.settings.comment);
	}

}
