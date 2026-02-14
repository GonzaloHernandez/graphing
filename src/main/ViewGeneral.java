package main;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

public class ViewGeneral extends JPanel {

	//-------------------------------------------------------------------------------------

	protected	GrapherMain	main;

	protected	JCheckBox[]	showVertex,showEdge;
	protected	JCheckBox	allowFirstState;
	protected	JCheckBox	firstZero;
	protected	JCheckBox	exportAuto;
	protected	JTabbedPane	info;
	protected	JTextArea	comment;
	protected	JTextArea	export;
	protected	JSpinner	gridScale;
	protected	JComboBox<String>	exportType;

	private GrapherSettings settings;
	
	//-------------------------------------------------------------------------------------

	public ViewGeneral(GrapherMain main){
		this.main	= main;
		initElements();
		progListeneres();
	}
	
	//-------------------------------------------------------------------------------------

	private void initElements() {
		JPanel panelNorth 	= new JPanel(new GridLayout(1,2,10,10));
		JPanel panelVertex	= new JPanel(new GridLayout(4,1));
		JPanel panelEdge	= new JPanel(new GridLayout(4,1));
		JPanel panelGeneral	= new JPanel(new GridLayout(3,1));

		showVertex		= new JCheckBox[4];
		showEdge		= new JCheckBox[4];

		TitledBorder vertexTitle = BorderFactory.createTitledBorder("Vertex show");
		panelVertex.setBorder(vertexTitle);
		Border marginVertex = BorderFactory.createEmptyBorder(7,7,7,7);
		panelVertex.setBorder(BorderFactory.createCompoundBorder(vertexTitle, marginVertex));

		TitledBorder edgeTitle = BorderFactory.createTitledBorder("Edge show");
		panelEdge.setBorder(edgeTitle);
		Border marginEdge = BorderFactory.createEmptyBorder(7,7,7,7);
		panelEdge.setBorder(BorderFactory.createCompoundBorder(edgeTitle, marginEdge));

		Border margin = BorderFactory.createEmptyBorder(10,7,7,9);
		panelNorth.setBorder(BorderFactory.createCompoundBorder(null, margin));

		String[] l = {"Sequence","Value","Type","Label"};
		for (int i=0; i<4; i++) {
			showVertex[i]	= new JCheckBox(l[i]);
			showEdge[i]		= new JCheckBox(l[i]);
			panelVertex.add(showVertex[i]);
			panelEdge.add(showEdge[i]);
		}
		panelNorth.add(panelVertex);	panelNorth.add(panelEdge);

		allowFirstState       = new JCheckBox("Allow first state");
		firstZero             = new JCheckBox("Start at Zero (0)");
		gridScale             = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
		exportAuto            = new JCheckBox("Export automatically");

		setLayout(new BorderLayout());

		JPanel gridScalePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		gridScalePanel.add(new JLabel("  Grid Scale: "));
		gridScalePanel.add(gridScale);

		JComponent editor = gridScale.getEditor();
		JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
		tf.setEditable(true);
		tf.setHorizontalAlignment(JTextField.CENTER);
		((NumberFormatter) ((JFormattedTextField.AbstractFormatter) 
		((DefaultFormatterFactory) tf.getFormatterFactory()).getDefaultFormatter()))
		.setAllowsInvalid(false);

		JPanel panelChecks = new JPanel();
		panelChecks.setLayout(new BoxLayout(panelChecks, BoxLayout.Y_AXIS));

		Border marginGeneral = BorderFactory.createEmptyBorder(8,8,8,0);
		panelGeneral.setBorder(BorderFactory.createCompoundBorder(null, marginGeneral));

		panelGeneral.add(allowFirstState);
		panelGeneral.add(firstZero);

		panelChecks.add(panelNorth);
		panelChecks.add(panelGeneral);
		panelChecks.add(gridScalePanel);

		JPanel northWrapper = new JPanel(new BorderLayout());
		northWrapper.add(panelChecks, BorderLayout.CENTER); 

		JPanel panelComment = new JPanel(new BorderLayout());
		comment = new JTextArea();
		comment.setWrapStyleWord(true);
		comment.setLineWrap(true);

		JPanel panelExport = new JPanel(new BorderLayout());
		exportType = new JComboBox<String>(GrapherSettings.exportTypes);
		export = new JTextArea();
		export.setWrapStyleWord(true);
		export.setFont(new Font("FreeMono", Font.PLAIN, 12));
		export.setEditable(false);

		panelComment.add(new JScrollPane(comment), BorderLayout.CENTER);
		panelExport.add(exportType, BorderLayout.NORTH);
		panelExport.add(new JScrollPane(export), BorderLayout.CENTER);
		panelExport.add(exportAuto, BorderLayout.SOUTH);

		info = new JTabbedPane();
		info.addTab("Comment", panelComment);
		info.addTab("Export", panelExport);

		// Add components to main container
		add(northWrapper, BorderLayout.NORTH);
		add(info, BorderLayout.CENTER);

		// 3. Apply font recursively
		Font currentFont = UIManager.getFont("Label.font");
		Font defaultFont = new Font(currentFont.getName(), Font.PLAIN, currentFont.getSize());

		for (Component component : getComponents()) {
			component.setFont(defaultFont);			
		}
	}


	//-------------------------------------------------------------------------------------

	private void progListeneres(){

		showVertex[0].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings = main.currentSession.board.settings;
				settings.showVertexSequence = showVertex[0].isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		showEdge[0].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings = main.currentSession.board.settings;
				settings.showConnectionSequence = showEdge[0].isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		showEdge[1].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings = main.currentSession.board.settings;
				settings.showTypeNames	= showEdge[1].isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		showVertex[1].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings.showVertexPriorities = showVertex[1].isSelected();
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

		gridScale.addChangeListener(e -> {
			settings = main.currentSession.board.settings;
			settings.gridScale = (Integer)gridScale.getValue();
			main.currentSession.board.gridScale = (Integer)settings.gridScale;
			main.currentSession.setModified(true);
			main.currentSession.board.repaint();
		});
	}

	//-------------------------------------------------------------------------------------

	public void refresh(){
		settings = main.currentSession.board.settings;
		showVertex[0].setSelected(settings.showVertexSequence);
		showVertex[1].setSelected(settings.showVertexPriorities);
		showEdge[0].setSelected(settings.showConnectionSequence);
		showEdge[1].setSelected(settings.showTypeNames);

		allowFirstState.setSelected(settings.allowFirsVertex);
		firstZero.setSelected(settings.firstZero);
		gridScale.setValue(settings.gridScale);

		exportAuto.setSelected(settings.exportAuto);
		comment.setText(settings.comment);
		exportType.setSelectedIndex(settings.exportType);
		Dictionary dict = main.currentSession.board.settings.dictionary;

		showVertex[0].setText("Sequence");
		showVertex[1].setText(""+dict.vertexValue);
		showEdge[0].setText("Sequence");
		showEdge[1].setText(""+dict.edgeValue);

		allowFirstState.setText("Allow first "+dict.vertex);
	}

}
