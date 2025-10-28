package main;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
	protected	JTextField	graph,vertex,vertexType,vertexValue,edge,edgeValue;
	protected	JTextField	graph1,vertex1,vertexType1,vertexValue1,edge1,edgeValue1;
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
		edge        = new JTextField("");
		vertexType	= new JTextField("");
		vertexValue = new JTextField("");
		edgeValue   = new JTextField("");

		graph1      = new JTextField("");
		vertex1     = new JTextField("");
		vertexType1	= new JTextField("");
		vertexValue1= new JTextField("");
		edge1       = new JTextField("");
		edgeValue1  = new JTextField("");

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
		gbc.gridy = 4; add(new JLabel("Edge"), gbc);
		gbc.gridy = 5; add(new JLabel("Edge value"), gbc);
		gbc.gridy = 6; add(save, gbc);

		// Text fields
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 1;
		gbc.gridy = 0; add(graph, gbc);
		gbc.gridy = 1; add(vertex, gbc);
		gbc.gridy = 2; add(vertexType, gbc);
		gbc.gridy = 3; add(vertexValue, gbc);
		gbc.gridy = 4; add(edge, gbc);
		gbc.gridy = 5; add(edgeValue, gbc);
		gbc.gridy = 6; add(reset, gbc);

		// Initial labels
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 2;
		gbc.gridy = 0; add(graph1, gbc);
		gbc.gridy = 1; add(vertex1, gbc);
		gbc.gridy = 2; add(vertexType1, gbc);
		gbc.gridy = 3; add(vertexValue1, gbc);
		gbc.gridy = 4; add(edge1, gbc);
		gbc.gridy = 5; add(edgeValue1, gbc);

		// Add invisible filler to push everything to top
		gbc.gridx = 0;
		gbc.gridy = 7;
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
			dict.edge			= edge.getText();
			dict.edgeValue		= edgeValue.getText();
			dict.graph1			= graph1.getText();
			dict.vertex1		= vertex1.getText();
			dict.vertexType1	= vertexType1.getText();
			dict.vertexValue1	= vertexValue1.getText();
			dict.edge1			= edge1.getText();
			dict.edgeValue1		= edgeValue1.getText();
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
			edge.setText(dict.edge);
			edgeValue.setText(dict.edgeValue);

			graph1.setText(dict.graph1);
			vertex1.setText(dict.vertex1);
			vertexType1.setText(dict.vertexType1);
			vertexValue1.setText(dict.vertexValue1);
			edge1.setText(dict.edge1);
			edgeValue1.setText(dict.edgeValue1);

			main.currentSession.board.repaint();
		});
		graph1.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (graph1.getText().length() >= 1 && 
					graph1.getSelectedText().length() <1) {
					e.consume();
				}
			}
		});
		vertex1.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (vertex1.getText().length() >= 1 && 
				vertex1.getSelectedText().length() <1) {
                    e.consume();
                }
			}
		});
		vertexType1.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (vertexType1.getText().length() >= 1 && 
				vertexType1.getSelectedText().length() <1) {
                    e.consume();
                }
			}
		});
		vertexValue1.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (vertexValue1.getText().length() >= 1 && 
				vertexValue1.getSelectedText().length() <1) {
					e.consume();
				}
			}
		});
		edge1.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (edge1.getText().length() >= 1 && 
				edge1.getSelectedText().length() <1) {
					e.consume();
				}
			}
		});
		edgeValue1.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (edgeValue1.getText().length() >= 1 && 
				edgeValue1.getSelectedText().length() <1) {
					e.consume();
				}
			}
		});
	}

	//-------------------------------------------------------------------------------------

	public void refresh(){
		Dictionary dict = main.currentSession.board.settings.dictionary;
		graph		.setText(dict.graph);
		vertex		.setText(dict.vertex);
		vertexType	.setText(dict.vertexType);
		vertexValue	.setText(dict.vertexValue);
		edge		.setText(dict.edge);
		edgeValue	.setText(dict.edgeValue);
		graph1		.setText(dict.graph1);
		vertex1		.setText(dict.vertex1);
		vertexType1	.setText(dict.vertexType1);
		vertexValue1.setText(dict.vertexValue1);
		edge1		.setText(dict.edge1);
		edgeValue1	.setText(dict.edgeValue1);
	}

}
