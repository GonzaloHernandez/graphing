package main;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ViewDictionary extends JPanel{

	//-------------------------------------------------------------------------------------

	protected	GrapherMain	main;

	protected	JTextField	graph,		_graph;
	protected	JTextField	vertex,		_vertex;
	protected	JTextField	vertexValue,_vertexValue;
	protected	JTextField	vertexType,	_vertexType;
	protected	JTextField	vertexLabel,_vertexLabel;
	protected	JTextField	edge,		_edge;
	protected	JTextField	edgeValue,	_edgeValue;
	protected	JTextField	edgeType,	_edgeType;
	protected	JTextField	edgeLabel,	_edgeLabel;

	protected	JButton	save,reset;
	
	//-------------------------------------------------------------------------------------

	public ViewDictionary(GrapherMain main){
		this.main	= main;
		initElements();
		progListeners();
	}
	
	//-------------------------------------------------------------------------------------

	private void initElements() {
		graph       = new JTextField("");

		vertex      = new JTextField("");
		vertexType	= new JTextField("");
		vertexValue = new JTextField("");
		vertexLabel = new JTextField("");

		edge        = new JTextField("");
		edgeValue   = new JTextField("");
		edgeType    = new JTextField("");
		edgeLabel   = new JTextField("");

		_graph      = new JTextField("");
		_vertex     = new JTextField("");
		_vertexType	= new JTextField("");
		_vertexValue= new JTextField("");
		_vertexLabel= new JTextField("");
		_edge       = new JTextField("");
		_edgeValue  = new JTextField("");
		_edgeType   = new JTextField("");
		_edgeLabel  = new JTextField("");

		save		= new JButton("Update changes");
		reset		= new JButton("Reset");

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Labels
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx = 0;
		gbc.gridy = 0; add(new JLabel("Graph"), gbc);

		gbc.gridy = 1; add(new JLabel("Vertex"), gbc);
		gbc.gridy = 2; add(new JLabel("Vertex type"), gbc);
		gbc.gridy = 3; add(new JLabel("Vertex value"), gbc);
		gbc.gridy = 4; add(new JLabel("Vertex label"), gbc);
		
		gbc.gridy = 5; add(new JLabel("Edge"), gbc);
		gbc.gridy = 6; add(new JLabel("Edge value"), gbc);
		gbc.gridy = 7; add(new JLabel("Edge type"), gbc);
		gbc.gridy = 8; add(new JLabel("Edge label"), gbc);
		gbc.gridy = 9; add(save, gbc);

		// Text fields
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 1;
		gbc.gridy = 0; add(graph, gbc);
		gbc.gridy = 1; add(vertex, gbc);
		gbc.gridy = 2; add(vertexType, gbc);
		gbc.gridy = 3; add(vertexValue, gbc);
		gbc.gridy = 4; add(vertexLabel, gbc);
		gbc.gridy = 5; add(edge, gbc);
		gbc.gridy = 6; add(edgeValue, gbc);
		gbc.gridy = 7; add(edgeType, gbc);
		gbc.gridy = 8; add(edgeLabel, gbc);
		gbc.gridy = 9; add(reset, gbc);

		// Initial labels
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 2;
		gbc.gridy = 0; add(_graph, gbc);
		gbc.gridy = 1; add(_vertex, gbc);
		gbc.gridy = 2; add(_vertexType, gbc);
		gbc.gridy = 3; add(_vertexValue, gbc);
		gbc.gridy = 4; add(_vertexLabel, gbc);
		gbc.gridy = 5; add(_edge, gbc);
		gbc.gridy = 6; add(_edgeValue, gbc);
		gbc.gridy = 7; add(_edgeType, gbc);
		gbc.gridy = 8; add(_edgeLabel, gbc);

		// Add invisible filler to push everything to top
		gbc.gridx = 0;
		gbc.gridy = 11;
		gbc.gridwidth = 3;
		gbc.weighty = 1;  // take remaining vertical space
		add(Box.createVerticalGlue(), gbc);
}

	
	//-------------------------------------------------------------------------------------

	private void progListeners(){
		save.addActionListener(e -> {
			Dictionary dict = main.currentSession.board.settings.dictionary;
			dict.graph			= graph.getText();
			dict.vertex			= vertex.getText();
			dict.vertexType		= vertexType.getText();
			dict.vertexValue	= vertexValue.getText();
			dict.vertexLabel	= vertexLabel.getText();
			dict.edge			= edge.getText();
			dict.edgeValue		= edgeValue.getText();
			dict.edgeType		= edgeType.getText();
			dict.edgeLabel		= edgeLabel.getText();

			dict._graph			= _graph.getText();
			dict._vertex		= _vertex.getText();
			dict._vertexType	= _vertexType.getText();
			dict._vertexValue	= _vertexValue.getText();
			dict._vertexLabel	= _vertexLabel.getText();
			dict._edge			= _edge.getText();
			dict._edgeValue		= _edgeValue.getText();
			dict._edgeType		= _edgeType.getText();
			dict._edgeLabel		= _edgeLabel.getText();
			main.currentSession.board.repaint();
			main.properties.generalView.refresh();
			main.currentSession.setModified(true);
		});
		reset.addActionListener(e -> {
			Dictionary dict = main.currentSession.board.settings.dictionary;
			graph.setText(dict.graph);
			vertex.setText(dict.vertex);
			vertexType.setText(dict.vertexType);
			vertexValue.setText(dict.vertexValue);
			vertexLabel.setText(dict.vertexLabel);
			edge.setText(dict.edge);
			edgeValue.setText(dict.edgeValue);
			edgeType.setText(dict.edgeType);
			edgeLabel.setText(dict.edgeLabel);

			_graph.setText(dict._graph);
			_vertex.setText(dict._vertex);
			_vertexType.setText(dict._vertexType);
			_vertexValue.setText(dict._vertexValue);
			_vertexLabel.setText(dict._vertexLabel);
			_edge.setText(dict._edge);
			_edgeValue.setText(dict._edgeValue);
			_edgeType.setText(dict._edgeType);
			_edgeLabel.setText(dict._edgeLabel);

			main.currentSession.board.repaint();
		});

		JTextField[] fields = {graph, _graph,
			vertex, vertexType, vertexValue, vertexLabel, 
			edge, edgeValue, edgeType, edgeLabel};

		JTextField[] _fields = {
			_vertex, _vertexType, _vertexValue, _vertexLabel, 
			_edge, _edgeValue, _edgeType, _edgeLabel};

		for (JTextField field : fields) {
			field.addFocusListener(new FocusAdapter() {			
				public void focusGained(FocusEvent e) {
					field.selectAll();
				}
				public void focusLost(FocusEvent e) {
					if (field.getText().trim().isEmpty()) {
						field.setText("?");
					}
				}		
			});
		}
		for (JTextField field : _fields) {
			field.addFocusListener(new FocusAdapter() {			
				public void focusGained(FocusEvent e) {
					field.selectAll();
				}
				@Override
				public void focusLost(FocusEvent e) {
					if (field.getText().isEmpty()) {
						field.setText(" ");
					}
				}
			});
			field.addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					String selected = field.getSelectedText();
					int currentLength = field.getText().length();
					if (selected != null && !selected.isEmpty()) {
						return; 
					}
					if (currentLength >= 1) {
						e.consume();
					}					
				}
			});
		}
	}

	//-------------------------------------------------------------------------------------

	public void refresh(){
		Dictionary dict = main.currentSession.board.settings.dictionary;
		graph		.setText(dict.graph);
		vertex		.setText(dict.vertex);
		vertexType	.setText(dict.vertexType);
		vertexValue	.setText(dict.vertexValue);
		vertexLabel	.setText(dict.vertexLabel);
		edge		.setText(dict.edge);
		edgeValue	.setText(dict.edgeValue);
		edgeType	.setText(dict.edgeType);
		edgeLabel	.setText(dict.edgeLabel);
		_graph		.setText(dict._graph);
		_vertex		.setText(dict._vertex);
		_vertexType	.setText(dict._vertexType);
		_vertexValue.setText(dict._vertexValue);
		_vertexLabel.setText(dict._vertexLabel);
		_edge		.setText(dict._edge);
		_edgeValue	.setText(dict._edgeValue);
		_edgeType	.setText(dict._edgeType);
		_edgeLabel	.setText(dict._edgeLabel);
	}

}
