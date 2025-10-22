package main;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class ViewGeneral extends JPanel {

	//-------------------------------------------------------------------------------------

	protected	GrapherMain	main;
	protected	JCheckBox	showTypeNames;
	protected	JCheckBox	showStateNumbers;
	protected	JCheckBox	showConnectionNumbers;
	protected	JCheckBox	showStateValues;
	protected	JCheckBox	allowFirstState;
	protected	JCheckBox	firstZero;
	protected	JCheckBox	exportAuto;
	protected	JTabbedPane	info;
	protected	JTextArea	comment;
	protected	JTextArea	export;
	protected	JComboBox<String>	exportType;

	private GrapherSettings settings;
	
	//-------------------------------------------------------------------------------------

	public ViewGeneral(GrapherMain main){
		this.main	= main;
		initElements();
		progListeneres();
	}
	
	//-------------------------------------------------------------------------------------

	private void initElements(){
		setLayout(new BorderLayout());

		JPanel panelChecks		= new JPanel(new GridLayout(0,1));

		showStateNumbers		= new JCheckBox("Show state sequence");
		showStateValues			= new JCheckBox("Show state priorities");
		showConnectionNumbers	= new JCheckBox("Show connection sequence");
		showTypeNames			= new JCheckBox("Show connection values");
		allowFirstState			= new JCheckBox("Allow first state");
		firstZero				= new JCheckBox("Start at Zero (0)");
		exportAuto				= new JCheckBox("Export automatically" + 
									GrapherSettings.exportTypes[0] + "]");


		panelChecks.add(showStateNumbers);
		panelChecks.add(showStateValues);
		panelChecks.add(showConnectionNumbers);
		panelChecks.add(showTypeNames);
		panelChecks.add(allowFirstState);
		panelChecks.add(firstZero);


		info					= new JTabbedPane();

		JPanel panelComment		= new JPanel(new BorderLayout());
		comment					= new JTextArea();
		comment.setWrapStyleWord(true);
		comment.setLineWrap(true);

		JPanel panelExport		= new JPanel(new BorderLayout());
		exportType				= new JComboBox<String>(GrapherSettings.exportTypes);
		export					= new JTextArea();
		export.setWrapStyleWord(true);
		export.setFont(new Font("FreeMono", Font.PLAIN, 12));
		export.setEditable(false);

		panelComment.add(new JScrollPane(comment),BorderLayout.CENTER);
		panelExport.add(exportType,BorderLayout.NORTH);
		panelExport.add(new JScrollPane(export),BorderLayout.CENTER);
		panelExport.add(exportAuto,BorderLayout.SOUTH);

		info.addTab("Comment", panelComment);
		info.addTab("Export", panelExport);
				
		add(panelChecks,BorderLayout.NORTH);
		add(info,BorderLayout.CENTER);

		Font currentFont	= UIManager.getFont("Label.font");
		Font defaultFont	= new Font(currentFont.getName(),Font.PLAIN,currentFont.getSize());
		for (Component component : getComponents()) {
			component.setFont(defaultFont);			
		}
	}
	
	//-------------------------------------------------------------------------------------

	private void progListeneres(){
		showTypeNames.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings = main.currentSession.board.settings;
				settings.showTypeNames	= showTypeNames.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		showStateNumbers.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings = main.currentSession.board.settings;
				settings.showVertexSequence = showStateNumbers.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		showConnectionNumbers.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings = main.currentSession.board.settings;
				settings.showConnectionSequence = showConnectionNumbers.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		showStateValues.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings.showVertexPriorities = showStateValues.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		allowFirstState.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings = main.currentSession.board.settings;
				settings.allowFirsVertex = allowFirstState.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		firstZero.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings = main.currentSession.board.settings;
				settings.firstZero = firstZero.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		comment.addCaretListener(new CaretListener(){

			public void caretUpdate(CaretEvent arg0) {
				settings = main.currentSession.board.settings;
				settings.comment = comment.getText();
				main.currentSession.setModified(true);
			}
		});

		exportAuto.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings = main.currentSession.board.settings;
				settings.exportAuto = exportAuto.isSelected();
				main.currentSession.setModified(true);
			}
		});
		
		exportType.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings = main.currentSession.board.settings;
				settings.exportType	= exportType.getSelectedIndex();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
	
	}

	//-------------------------------------------------------------------------------------

	public void refresh(){
		settings = main.currentSession.board.settings;
		showTypeNames.setSelected(settings.showTypeNames);
		showStateNumbers.setSelected(settings.showVertexSequence);
		showConnectionNumbers.setSelected(settings.showConnectionSequence);
		showStateValues.setSelected(settings.showVertexPriorities);
		allowFirstState.setSelected(settings.allowFirsVertex);
		firstZero.setSelected(settings.firstZero);
		exportAuto.setSelected(settings.exportAuto);
		comment.setText(settings.comment);
		exportType.setSelectedIndex(settings.exportType);
		Dictionary dict = main.currentSession.board.settings.dictionary;

		showStateNumbers.setText("Show "+dict.vertex+" sequence");
		showStateValues.setText("Show "+dict.vertex+"'s "+dict.vertexValue);
		showConnectionNumbers.setText("Show "+dict.edge+" sequence");
		showTypeNames.setText("Show "+dict.edge+"'s "+dict.edgeValue);
		allowFirstState.setText("Allow first "+dict.vertex);
	}

}
