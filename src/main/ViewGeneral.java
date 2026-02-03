package main;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

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
		setLayout(new BorderLayout());

		// 1. Use a vertical BoxLayout for the checkboxes so they don't stretch to equal heights
		JPanel panelChecks = new JPanel();
		panelChecks.setLayout(new BoxLayout(panelChecks, BoxLayout.Y_AXIS));

		showStateNumbers      = new JCheckBox("Show state sequence");
		showStateValues       = new JCheckBox("Show state priorities");
		showConnectionNumbers = new JCheckBox("Show connection sequence");
		showTypeNames         = new JCheckBox("Show connection values");
		allowFirstState       = new JCheckBox("Allow first state");
		firstZero             = new JCheckBox("Start at Zero (0)");
		gridScale             = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
		exportAuto            = new JCheckBox("Export automatically");

		// Grid Scale Panel (FlowLayout.LEFT keeps it tight)
		JPanel gridScalePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
		gridScalePanel.add(new JLabel("Grid Scale: "));
		gridScalePanel.add(gridScale);

		// Spinner formatting
		JComponent editor = gridScale.getEditor();
		JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
		tf.setEditable(true);
		tf.setHorizontalAlignment(JTextField.CENTER);
		((NumberFormatter) ((JFormattedTextField.AbstractFormatter) 
		((DefaultFormatterFactory) tf.getFormatterFactory()).getDefaultFormatter()))
		.setAllowsInvalid(false);

		// Add elements to the vertical panel
		panelChecks.add(showStateNumbers);
		panelChecks.add(showStateValues);
		panelChecks.add(showConnectionNumbers);
		panelChecks.add(showTypeNames);
		panelChecks.add(allowFirstState);
		panelChecks.add(firstZero);
		panelChecks.add(gridScalePanel);


		Component[] comps = {showStateNumbers, showStateValues, showConnectionNumbers, 
							showTypeNames, allowFirstState, firstZero, gridScalePanel};
		
		for (Component c : comps) {
			((JComponent)c).setAlignmentX(Component.LEFT_ALIGNMENT);
			panelChecks.add(c);
		}

		// 2. WRAPPER: This prevents the BorderLayout from stretching the panelChecks vertically
		JPanel northWrapper = new JPanel(new BorderLayout());
		northWrapper.add(panelChecks, BorderLayout.WEST); 

		info = new JTabbedPane();

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
		showTypeNames.setSelected(settings.showTypeNames);
		showStateNumbers.setSelected(settings.showVertexSequence);
		showConnectionNumbers.setSelected(settings.showConnectionSequence);
		showStateValues.setSelected(settings.showVertexPriorities);
		allowFirstState.setSelected(settings.allowFirsVertex);
		firstZero.setSelected(settings.firstZero);
		gridScale.setValue(settings.gridScale);

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
