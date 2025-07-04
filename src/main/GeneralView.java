package main;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class GeneralView extends JPanel {

	//-------------------------------------------------------------------------------------

	protected	GrapherMain	main;
	protected	JCheckBox	showTypeNames;
	protected	JCheckBox	showStateNumbers;
	protected	JCheckBox	showConnectionNumbers;
	protected	JCheckBox	showStateValues;
	protected	JCheckBox	allowFirstState;
	protected	JCheckBox	firstZero;
	protected	JCheckBox	saveDzn;
	protected	JTabbedPane	info;
	protected	JTextArea	comment;
	protected	ExportView	exportView;
	
	//-------------------------------------------------------------------------------------

	public GeneralView(GrapherMain main){
		this.main	= main;
		initElements();
		progListeneres();
	}
	
	//-------------------------------------------------------------------------------------

	private void initElements(){
		showStateNumbers		= new JCheckBox("View state sequence");
		showStateValues			= new JCheckBox("View state priorities");
		showTypeNames			= new JCheckBox("View connection values");
		showConnectionNumbers	= new JCheckBox("View connection sequence");
		allowFirstState			= new JCheckBox("Allow first state");
		firstZero				= new JCheckBox("Start at Zero (0)");
		saveDzn					= new JCheckBox("Save dzn file");

		info					= new JTabbedPane();

		JPanel commentView		= new JPanel();				
		comment					= new JTextArea();
		exportView				= new ExportView(main);

		comment.setWrapStyleWord(true);
		comment.setLineWrap(true);

		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		add(showStateNumbers);
		add(showStateValues);
		add(showTypeNames);
		add(showConnectionNumbers);
		add(allowFirstState);
		add(firstZero);
		add(saveDzn);
		add(info);
		commentView.setLayout(new BorderLayout());
		commentView.add(new JScrollPane(comment),BorderLayout.CENTER);

		info.addTab("Comment", commentView);
		info.addTab("Export", exportView);
				

		Font defaultFont	= new Font("Cantarell",Font.PLAIN,11);
		for (Component component : getComponents()) {
			component.setFont(defaultFont);			
		}
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
				main.currentSession.board.settings.showStateSequence = showStateNumbers.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		showConnectionNumbers.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.settings.showConnectionSequence = showConnectionNumbers.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		showStateValues.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.settings.showStatePriorities = showStateValues.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		allowFirstState.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.settings.allowFirsState = allowFirstState.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		firstZero.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.settings.firstZero = firstZero.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		saveDzn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.settings.saveDzn = saveDzn.isSelected();
				main.currentSession.setModified(true);
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
		showStateNumbers.setSelected(main.currentSession.board.settings.showStateSequence);
		showConnectionNumbers.setSelected(main.currentSession.board.settings.showConnectionSequence);
		showStateValues.setSelected(main.currentSession.board.settings.showStatePriorities);
		allowFirstState.setSelected(main.currentSession.board.settings.allowFirsState);
		firstZero.setSelected(main.currentSession.board.settings.firstZero);
		saveDzn.setSelected(main.currentSession.board.settings.saveDzn);
		comment.setText(main.currentSession.board.settings.comment);
	}

}
