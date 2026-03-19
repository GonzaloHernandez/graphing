package main;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
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

	protected JCheckBox		showVSeq,showVVal,showVTyp,showVLab;
	protected JCheckBox		showESeq,showEVal,showETyp,showELab;
	protected JTextField	showVValDiff,showVLabDiff;
	protected JTextField	showEValDiff,showELabDiff;
	protected JCheckBox		allowFirstState;
	protected JCheckBox		firstZero;
	protected JCheckBox		exportAuto;
	protected JTabbedPane	info;
	protected JTextArea		comment;
	protected JTextArea		export;

	protected JButton		programSelection,runProgram;
	protected JTextField	programFile;
	protected JComboBox<String>	programType;
	protected JToggleButton	listen;
	protected JTextArea		listenLog;

	protected JSpinner		gridScale;
	protected JComboBox<String>	exportType;

	protected	TitledBorder vertexTitle;
	protected	TitledBorder edgeTitle;

	private 	GrapherSettings settings;
	
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

		vertexTitle = BorderFactory.createTitledBorder("Vertex show");
		panelVertex.setBorder(vertexTitle);
		Border marginVertex = BorderFactory.createEmptyBorder(7,7,7,7);
		panelVertex.setBorder(BorderFactory.createCompoundBorder(vertexTitle, marginVertex));

		edgeTitle = BorderFactory.createTitledBorder("Edge show");
		panelEdge.setBorder(edgeTitle);
		Border marginEdge = BorderFactory.createEmptyBorder(7,7,7,7);
		panelEdge.setBorder(BorderFactory.createCompoundBorder(edgeTitle, marginEdge));

		Border margin = BorderFactory.createEmptyBorder(10,7,7,9);
		panelNorth.setBorder(BorderFactory.createCompoundBorder(null, margin));

		panelVertex.add(showVSeq = new JCheckBox("Sequence"));

		JPanel temp1 = new JPanel(new BorderLayout());
		temp1.add(showVVal = new JCheckBox("Value≠"),BorderLayout.WEST);
		temp1.add(showVValDiff = new JTextField(2));
		panelVertex.add(temp1);

		panelVertex.add(showVTyp = new JCheckBox("Type"));

		JPanel temp2 = new JPanel(new BorderLayout());
		temp2.add(showVLab = new JCheckBox("Label≠"),BorderLayout.WEST);
		temp2.add(showVLabDiff = new JTextField(2));
		panelVertex.add(temp2);
		
		panelEdge.add(showESeq	= new JCheckBox("Sequence"));

		JPanel temp3 = new JPanel(new BorderLayout());
		temp3.add(showEVal = new JCheckBox("Value≠"),BorderLayout.WEST);
		temp3.add(showEValDiff = new JTextField(2));
		panelEdge.add(temp3);

		panelEdge.add(showETyp	= new JCheckBox("Type"));

		JPanel temp4 = new JPanel(new BorderLayout());
		temp4.add(showELab = new JCheckBox("Label≠"),BorderLayout.WEST);
		temp4.add(showELabDiff = new JTextField(2));
		panelEdge.add(temp4);

		panelNorth.add(panelVertex);	panelNorth.add(panelEdge);

		allowFirstState	= new JCheckBox("Allow first state");
		firstZero       = new JCheckBox("Start at Zero (0)");
		gridScale       = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
		exportAuto      = new JCheckBox("Export automatically");

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
		comment.setFont(new Font("FreeMono", Font.PLAIN, 12));

		JPanel panelExport = new JPanel(new BorderLayout());
		exportType = new JComboBox<>(GrapherSettings.exportTypes);
		export = new JTextArea();
		export.setWrapStyleWord(true);
		export.setFont(new Font("FreeMono", Font.PLAIN, 12));
		export.setEditable(false);

		JPanel panelExecute = new JPanel(new FlowLayout());
		programSelection	= new JButton("Select program");
		programFile			= new JTextField();
		runProgram			= new JButton("Run Program");
		String[] types		= {"MiniZinc", "Python"};
		programType			= new JComboBox<>(types);
		listen              = new JToggleButton("Listen");
		listenLog			= new JTextArea();
		listenLog.setEditable(false);

		// ----- Sub tabs -----------------

		panelComment.add(new JScrollPane(comment), BorderLayout.CENTER);

		panelExport.add(exportType, BorderLayout.NORTH);
		panelExport.add(new JScrollPane(export), BorderLayout.CENTER);
		panelExport.add(exportAuto, BorderLayout.SOUTH);

		panelExecute.setLayout(new BoxLayout(panelExecute, BoxLayout.Y_AXIS));

		JPanel row1 = new JPanel(new BorderLayout(5, 0)); 
		row1.add(programSelection, BorderLayout.WEST);
		row1.add(programFile, BorderLayout.CENTER);
		row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, row1.getPreferredSize().height));

		JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row2.add(new JLabel("Program Type: "));
		row2.add(programType);
		row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, row2.getPreferredSize().height));

		JPanel row3 = new JPanel(new GridLayout(1,2));
		row3.add(runProgram);
		row3.add(listen);
		row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, row3.getPreferredSize().height));

		panelExecute.add(row1);
		panelExecute.add(row2);
		panelExecute.add(row3);

		JScrollPane scrollLog = new JScrollPane(listenLog);
		panelExecute.add(scrollLog);

		// ----------------------------------

		info = new JTabbedPane();
		info.addTab("Comment", panelComment);
		info.addTab("Export", panelExport);
		info.addTab("Execute", panelExecute);

		add(northWrapper, BorderLayout.NORTH);
		add(info, BorderLayout.CENTER);

		Font currentFont = UIManager.getFont("Label.font");
		Font defaultFont = new Font(currentFont.getName(), Font.PLAIN, currentFont.getSize());

		for (Component component : getComponents()) {
			component.setFont(defaultFont);			
		}
	}


	//-------------------------------------------------------------------------------------

	private void progListeneres(){

		showVSeq.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings = main.currentSession.board.settings;
				settings.showVertexSequence = showVSeq.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		showVVal.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings.showVertexValue = showVVal.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		showVTyp.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings.showVertexType = showVTyp.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		showVLab.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings.showVertexLabel = showVLab.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		showVValDiff.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings.showVertexValueDiff = showVValDiff.getText();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		showVValDiff.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				showVValDiff.selectAll();
			}
			@Override
			public void focusLost(FocusEvent e) {
				settings.showVertexValueDiff = showVValDiff.getText();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		showVLabDiff.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings.showVertexLabelDiff = showVLabDiff.getText();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		showVLabDiff.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				showVLabDiff.selectAll();
			}
			@Override
			public void focusLost(FocusEvent e) {
				settings.showVertexLabelDiff = showVLabDiff.getText();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		showESeq.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings = main.currentSession.board.settings;
				settings.showEdgeSequence = showESeq.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		showEVal.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings = main.currentSession.board.settings;
				settings.showEdgeValue	= showEVal.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		showETyp.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings = main.currentSession.board.settings;
				settings.showEdgeType	= showETyp.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		showEVal.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings = main.currentSession.board.settings;
				settings.showEdgeValue	= showEVal.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		showELab.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings.showEdgeLabel = showELab.isSelected();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		showEValDiff.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings.showEdgeValueDiff = showEValDiff.getText();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		showEValDiff.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				showEValDiff.selectAll();
			}
			public void focusLost(FocusEvent e) {
				settings.showEdgeValueDiff = showEValDiff.getText();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}			
		});


		showELabDiff.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				settings.showEdgeLabelDiff = showELabDiff.getText();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		showELabDiff.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				showELabDiff.selectAll();
			}
			public void focusLost(FocusEvent e) {
				settings.showEdgeLabelDiff = showELabDiff.getText();
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

		comment.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (comment.getText().length()>1000) {
					comment.setText(comment.getText().substring(0,1000));
					main.messageBox("Only 1,000 characters are permitted for comments.| The remainder has been removed.", "Warning", "Ok");
				}
			}
			
		});

        listen.addItemListener(new ItemListener() {
            private Thread serverThread;

            @Override
            public void itemStateChanged(ItemEvent ev) {
                if (ev.getStateChange() == ItemEvent.SELECTED) {

                    serverThread = new Thread(() -> {
                        int port = 65432;
                        try (ServerSocket serverSocket = new ServerSocket(port)) {
                            System.out.println("Java Server is listening on port " + port);

                            while (!Thread.currentThread().isInterrupted()) {
                                try (Socket clientSocket = serverSocket.accept();
                                    BufferedReader in = new BufferedReader(
                                        new InputStreamReader(clientSocket.getInputStream()))) 
                                {
                                    String line;
									while ((line = in.readLine()) != null) {
										final String message = line;
										SwingUtilities.invokeLater(() -> runSettings(message));
									}
                                } catch (IOException ex) {
                                    System.out.println("Socket error: " + ex.getMessage());
                                }
                            }
                        } catch (IOException ex) {
                            System.out.println("Could not listen on port " + port);
                        }
                    });

                    serverThread.start();
                } else {
                    if (serverThread != null) {
                        serverThread.interrupt(); 
                    }
                }
            }
        });

		programSelection.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String file = main.persistence.getFileName("mzn");
				if (file == null) return;
				programFile.setText(file);
			}			
		});

		runProgram.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ev) {
				if (programFile.getText().trim().isEmpty()) return;

				Vector<Vertex>	vs = getVertices();
				Vector<Edge>	es = getEdges();

				String vertexTypes = "";
				String vertexValues = "";
				String vertexLabels = "";
				String edgeTypes = "";
				String edgeValues = "";
				String edgeLabels = "";
				
				for (Vertex v  : vs){ 
					if (vertexValues.length() > 0){
						vertexTypes += ",";
						vertexValues += ",";
						vertexLabels += ",";
					}
					Type vType		= v.getType();
					vertexTypes		+= vType==null?0:vType.getId();
					vertexValues	+= v.getValue();
					vertexLabels	+= v.getLabel();

					for (Edge e : v.getOuts()) {
						if (edgeValues.length()>0)  {
							edgeTypes  += ",";
							edgeValues += ",";
							edgeLabels += ",";
						}
						Type eType	= e.getType();
						edgeTypes	+= eType==null?0:eType.getId();
						edgeValues	+= e.getValue();
						edgeLabels	+= e.getLabel();
					}
				}

				String from = "";
				String to	= "";
				int nConections = 0;
				int first	= main.currentSession.board.settings.firstZero?0:1;
				for (Vertex s : vs) {
					for (Edge c : s.getOuts()) {
						from	+= c.getSource().getNumber()+first + ",";
						to		+= c.getTarget().getNumber()+first + ",";
						nConections ++;
					}
				}

				if (from.length()>0) {
					from	= from	.substring(0, from.length()-1);
					to		= to	.substring(0, to.length()-1);	
				}

				int init		= 1;
				for(Vertex v:getVertices()) {
					if (v.getStatus()==Vertex.FOCUSED) {
						init = v.getNumber()+1;
					}
				}
				
				String model	= programFile.getText();
				String parms	= "-D" +
					"nvertices	= "	+ vs.size() + ";" +
					"nedges    	= "	+ es.size() + ";" +
					"owners		= ["+ vertexTypes + "];" +
					"values		= ["+ vertexValues + "];" +
					"sources	= ["+ from + "];" +
					"targets	= ["+ to + "];" +
					"chances	= ["+ edgeValues + "];" +
					"init		= "	+ init + ";";

				String output = main.persistence.runMinizinc(model,parms);
				output.lines().forEach(line -> {
					runSettings(line);
				});
			}
			
		});
	}

	//-------------------------------------------------------------------------------------

    boolean runSettings(String message) {
		listenLog.append(message + "\n");
		Pattern mainPattern = Pattern.compile("^([a-z]+)=\\[(.*)\\]$");
		Matcher mainMatcher = mainPattern.matcher(message.trim());

		if (!mainMatcher.matches()) {
			return false;
		}

		String key = mainMatcher.group(1);
		String content = mainMatcher.group(2);

		Vector<String> elements = new Vector<>();
		Pattern elementPattern = Pattern.compile("[^,\\s]+");
		Matcher elementMatcher = elementPattern.matcher(content);

		while (elementMatcher.find()) {
			elements.add(elementMatcher.group());
		}

		Lexicon lex = main.currentSession.board.settings.lexicon;

		if (key.equals(lex.vertex) || key.equals(lex.vertexValue) || key.equals(lex.vertexLabel)) {
			Vector<Vertex>  vs = getVertices();
			if (elements.size()!=vs.size()) {
				listenLog.append("--- Error ---\n");
				return false;
			}
			if (key.equals(lex.vertex)) {
				for(int i=0;i<vs.size();i++) { String e = elements.elementAt(i);
					if (e.toLowerCase().equals("true") || e.toLowerCase().equals("1")) {
						vs.elementAt(i).setActive(true);
					} else {
						vs.elementAt(i).setActive(false);
					}
				}
			}
			else if (key.equals(lex.vertexValue)) {
				for(int i=0;i<vs.size();i++) { String e = elements.elementAt(i);
					vs.elementAt(i).setValue(e);
				}
			}
			else if (key.equals(lex.vertexLabel)) {
				for(int i=0;i<vs.size();i++) { String e = elements.elementAt(i);
					vs.elementAt(i).setLabel(e);
				}
			}
		}
		else if (key.equals(lex.edge) || key.equals(lex.edgeValue) || key.equals(lex.edgeLabel)) {
			Vector<Edge>  es = getEdges();
			if (elements.size()!=es.size()) {
				listenLog.append("--- Error ---\n");
				return false;
			}
			if (key.equals(lex.edge)) {
				for(int i=0;i<es.size();i++) { String e = elements.elementAt(i);
					if (e.toLowerCase().equals("true") || e.toLowerCase().equals("1")) {
						es.elementAt(i).setActive(true);
					} else {
						es.elementAt(i).setActive(false);
					}
				}
			}
			else if (key.equals(lex.edgeValue)) {
				for(int i=0;i<es.size();i++) { String e = elements.elementAt(i);
					es.elementAt(i).setValue(e);
				}
			}
			else if (key.equals(lex.edgeLabel)) {
				for(int i=0;i<es.size();i++) { String e = elements.elementAt(i);
					es.elementAt(i).setLabel(e);
				}
			}
		}

		main.currentSession.board.repaint();
		main.properties.stockView.refresh();
		return true;
    }

	//-------------------------------------------------------------------------------------

    Vector<Vertex> getVertices() {
        return main.currentSession.board.vertices;
    }

    Vector<Edge> getEdges() {
        Vector<Edge> edges = new Vector<Edge>();
        for (int i=0; i<main.currentSession.board.vertices.size(); i++) {
            for (int j=0; j<main.currentSession.board.vertices.elementAt(i).getOuts().size(); j++) {
                edges.add(main.currentSession.board.vertices.elementAt(i).getOuts().elementAt(j));
            }
        }
        return edges;
    }

	//-------------------------------------------------------------------------------------

	public void refresh(){
		settings = main.currentSession.board.settings;

		showVSeq.setSelected(settings.showVertexSequence);
		showVVal.setSelected(settings.showVertexValue);
		showVTyp.setSelected(settings.showVertexType);
		showVLab.setSelected(settings.showVertexLabel);
		showVValDiff.setText(settings.showVertexValueDiff);
		showVLabDiff.setText(settings.showVertexLabelDiff);

		showESeq.setSelected(settings.showEdgeSequence);
		showEVal.setSelected(settings.showEdgeValue);
		showETyp.setSelected(settings.showEdgeType);
		showELab.setSelected(settings.showEdgeLabel);		
		showEValDiff.setText(settings.showEdgeValueDiff);
		showELabDiff.setText(settings.showEdgeLabelDiff);

		allowFirstState.setSelected(settings.allowFirsVertex);
		firstZero.setSelected(settings.firstZero);
		gridScale.setValue(settings.gridScale);

		exportAuto.setSelected(settings.exportAuto);
		comment.setText(settings.comment);
		exportType.setSelectedIndex(settings.exportType);
		Lexicon lex = main.currentSession.board.settings.lexicon;

		vertexTitle.setTitle("Show at "+Lexicon.capitalize(lex.vertex));
		edgeTitle.setTitle("Show at "+Lexicon.capitalize(lex.edge));
		showVSeq.setText("Sequence");
		showVVal.setText(Lexicon.capitalize(""+lex.vertexValue)+"≠");
		showVTyp.setText(Lexicon.capitalize(""+lex.vertexType));
		showVLab.setText(Lexicon.capitalize(""+lex.vertexLabel)+"≠");
		showESeq.setText("Sequence");
		showEVal.setText(Lexicon.capitalize(""+lex.edgeValue)+"≠");
		showETyp.setText(Lexicon.capitalize(""+lex.edgeType));
		showELab.setText(Lexicon.capitalize(""+lex.edgeLabel+"≠"));

		allowFirstState.setText("Allow first "+lex.vertex);
	}

}
