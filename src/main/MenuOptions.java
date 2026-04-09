package main;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

class TypeMenuItem extends JMenuItem {
	private Type	type;
	public TypeMenuItem(String label,Type type) {
		super(label);
		this.type	= type;
		Font currentFont	= UIManager.getFont("Label.font");
		Font defaultFont	= new Font(currentFont.getName(),Font.PLAIN,currentFont.getSize());
		ImageIcon img = new ImageIcon(GrapherMain.class.getResource("icons/type.png"));
		setFont(defaultFont);
		setIcon(img);
	}
	public Type getType() {
		return type;
	}
}

class GrapherMenu extends JMenu {
	public GrapherMenu(String label,Font font,String icon) {
		super(label);
		setFont(font);
		ImageIcon img = new ImageIcon(GrapherMain.class.getResource("icons/"+icon));
		if (icon!=null) setIcon(img);
	}
}

class GrapherItem extends JMenuItem {
	public GrapherItem(String label,Font font,String icon) {
		super(label);
		setFont(font);
		ImageIcon img = new ImageIcon(GrapherMain.class.getResource("icons/"+icon));
		if (icon!=null) setIcon(img);
	}
}

public class MenuOptions extends JPopupMenu{

	//-------------------------------------------------------------------------------------

	protected	GrapherMenu	vertexMenu,edgeMenu,grapherMenu,template;
	
	private	GrapherMain		main;
	private GrapherMenu		vertexTypes,edgeTypes;
	private	GrapherItem		vertexDelete,vertexDeleteAllOuts,vertexAccepted;
	private	GrapherItem		edgeDelete,edgeTune;
	private	GrapherItem		restart,load,importGame,save,saveAs;
	private GrapherItem		parityGame,parityGameQ,stochasticGames;
	private GrapherItem		exportPNG,exportPDF,exportSVG,simulate,help;

	
	//-------------------------------------------------------------------------------------

	public MenuOptions(GrapherMain main) {
		this.main	= main;
		initElements();
		progListeners();
	}
	
	//-------------------------------------------------------------------------------------

	private void initElements(){
		Font currentFont	= UIManager.getFont("Label.font");
		Font defaultFont	= new Font(currentFont.getName(),Font.PLAIN,currentFont.getSize());

		vertexMenu			= new GrapherMenu("Vertex",defaultFont,"state.png");
		edgeMenu			= new GrapherMenu("Edge",defaultFont,"connection.png");
		grapherMenu			= new GrapherMenu("Grapher",defaultFont,"application.png");
		template			= new GrapherMenu("Use Template",defaultFont,"credits.png");
				
		vertexDelete		= new GrapherItem("Delete Vertex",defaultFont,"delete_state.png");
		vertexDeleteAllOuts	= new GrapherItem("Delete all Outgoing Edges",defaultFont,"delete_connection.png");
		vertexAccepted		= new GrapherItem("Set as Final State",defaultFont,"accepted_state.png");
		vertexTypes			= new GrapherMenu("Set Type",defaultFont,"owner.png");
		
		edgeDelete			= new GrapherItem("Delete Edge",defaultFont,"delete_connection.png");
		edgeTune			= new GrapherItem("Tuning the edge",defaultFont,"tune_connection.png");
		edgeTypes			= new GrapherMenu("Set type",defaultFont,"type_connection.png");

		restart				= new GrapherItem("Restar Session",defaultFont,"new.png");
		load				= new GrapherItem("Load Session",defaultFont,"open.png");
		importGame			= new GrapherItem("Import Game (GM)",defaultFont,"open.png");
		save				= new GrapherItem("Save Session",defaultFont,"save.png");
		saveAs				= new GrapherItem("Save Session as ...",defaultFont,"saveas.png");
		exportPNG			= new GrapherItem("Export as Image PNG",defaultFont,"print.png");
		exportSVG			= new GrapherItem("Export as Drawing SVG",defaultFont,"print.png");
		exportPDF			= new GrapherItem("Export as Document PDF",defaultFont,"print.png");
		simulate			= new GrapherItem("Simulate",defaultFont,"simulate.png");
		help				= new GrapherItem("Help",defaultFont,"help.png");
		
		parityGame			= new GrapherItem("Parity Game",defaultFont,"");
		parityGameQ			= new GrapherItem("Parity Game + Quantitative conditions",defaultFont,"");
		stochasticGames		= new GrapherItem("Stochastic Games",defaultFont,"");

		add(vertexMenu);
		add(edgeMenu);
		add(grapherMenu);
		
		vertexMenu.add(vertexDelete);
		vertexMenu.add(vertexDeleteAllOuts);
		vertexMenu.add(vertexAccepted);
		vertexMenu.add(vertexTypes);
		edgeMenu.add(edgeDelete);
		edgeMenu.add(edgeTune);
		edgeMenu.add(edgeTypes);
		
		grapherMenu.add(restart);
		grapherMenu.add(template);
		grapherMenu.add(load);
		grapherMenu.add(save);
		grapherMenu.add(saveAs);
		grapherMenu.addSeparator();
		grapherMenu.add(importGame);
		grapherMenu.addSeparator();
		grapherMenu.add(exportPNG);
		grapherMenu.add(exportSVG);
		grapherMenu.add(exportPDF);
		grapherMenu.addSeparator();
		// grapherMenu.add(simulate);
		grapherMenu.add(help);

		template.add(parityGame);
		template.add(parityGameQ);
		template.add(stochasticGames);
	}

	//-------------------------------------------------------------------------------------
	
	public void setMenuTypes(JComponent menu, Vector<Type> types){
		menu.removeAll();
		for (Type type : types) {
			TypeMenuItem typeItem= new TypeMenuItem(type.getName(),type);
			typeItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Type t = ((TypeMenuItem)e.getSource()).getType();
					if (main.currentSession.board.vertexTarget != null) {
						main.currentSession.board.vertexTarget.setType(t);
					} else {
						main.currentSession.board.currentEdge.setType(t);
					}
					main.currentSession.board.repaint();
					main.currentSession.setModified(true);
				}
			});
			menu.add(typeItem);
		}
		TypeMenuItem typeItem= new TypeMenuItem("<None>",null);
		typeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Type t = ((TypeMenuItem)e.getSource()).getType();
				if (main.currentSession.board.vertexTarget != null) {
					main.currentSession.board.vertexTarget.setType(t);
				} else {
					main.currentSession.board.currentEdge.setType(t);
				}
				main.currentSession.board.repaint();
				main.currentSession.setModified(true);
			}
		});
		menu.add(typeItem);
	}
	
	//-------------------------------------------------------------------------------------

	public void show(boolean vertex,boolean edge,boolean grapher){
		if (vertex){
			if (main.currentSession.board.vertexTarget.isAccepted()) { 
				vertexAccepted.setText("Unset as a final state");
			}
			else {
				vertexAccepted.setText("Set as a final state");
			}
			vertexMenu.setEnabled(true);
		}
		else{
			vertexMenu.setEnabled(false);
		}
		//----------------------------------------------------------------------
		if (edge){
			Edge current = main.currentSession.board.currentEdge;
			if (!current.getSource().equals(current.getTarget()) && current.getDistance()!=0) {
				edgeTune.setEnabled(true);
			}
			else {
				edgeTune.setEnabled(false);
			}
		
			edgeMenu.setEnabled(true);
		}
		else{
			edgeMenu.setEnabled(false);
		}
		//----------------------------------------------------------------------		
		if (grapher){
			grapherMenu.setEnabled(true);
			if (main.currentSession.board.compiler == null) {
				restart.setEnabled(true);
				load.setEnabled(true);
				saveAs.setEnabled(true);
				
				if (main.currentSession.board.isDFD()==null){
					simulate.setText("Simulate");
					simulate.setEnabled(true);
				}
				else{
					simulate.setText("It is no possible to simulate this graph");
					simulate.setEnabled(false);
				}
			}
			else{
				restart.setEnabled(false);
				load.setEnabled(false);
				saveAs.setEnabled(false);
				simulate.setEnabled(false);
			}
		}
		else {
			grapherMenu.setEnabled(false);
		}
		//----------------------------------------------------------------------
		show(	main.currentSession.board,
				(int)main.currentSession.board.getMousePosition().getX(),
				(int)main.currentSession.board.getMousePosition().getY()
		);
	}
	
	//-------------------------------------------------------------------------------------

	public void showTypes(Vector<Type> types){
		JPopupMenu menu = new JPopupMenu();
		setMenuTypes(menu, types);
		menu.addPopupMenuListener(new PopupMenuListener(){

			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				main.currentSession.board.menuBlock = true;
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
				main.currentSession.board.menuBlock = false;
			}

			public void popupMenuCanceled(PopupMenuEvent arg0) {
				main.currentSession.board.menuBlock = false;
			}
		});
		Board board = main.currentSession.board;
		menu.show(board,(int)board.getMousePosition().getX(),(int)board.getMousePosition().getY());
		
	}

	//-------------------------------------------------------------------------------------
	
	private void progListeners(){
		
		vertexDelete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Vertex source = main.currentSession.board.vertexTarget;
				main.currentSession.board.deleteVertex(source);
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		vertexDeleteAllOuts.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.vertexTarget.deleteAllEdges();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		vertexAccepted.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Vertex vertex = main.currentSession.board.vertexTarget;
				vertex.setAccepted(!vertex.isAccepted());
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});		
		
		edgeDelete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Vertex source = main.currentSession.board.currentEdge.getSource();
				source.deleteEdge(main.currentSession.board.currentEdge);
				main.properties.stockView.refresh();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		edgeTune.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.currentEdge.setDistance(0);
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});	
	
		restart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.restart();
				main.properties.stockView.refresh();
				main.currentSession.setModified(false);
			}
		});

		load.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.persistence.loadSession("",false);
			}
		});

		importGame.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.persistence.importGame();
			}
		});

		save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.persistence.saveSession(false);
			}
		});
		
		saveAs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.persistence.saveSession(true);
			}
		});
		
		exportPNG.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.persistence.exportPNG();
			}
		});
		
		exportPDF.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.persistence.exportPDF();
			}
		});

		exportSVG.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.persistence.exportSVG();
			}
		});

		simulate.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.openCompiler(main.currentSession.board);
			}
		});

		help.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (Desktop.isDesktopSupported()) {
					try {
						File myFile = new File("docs/help.pdf");
						Desktop.getDesktop().open(myFile);
					} catch (IOException ex) {
						// no application registered for PDFs
					}
				}
			}
		});
		
		addPopupMenuListener(new PopupMenuListener(){

			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				main.currentSession.board.menuBlock = true;
				setMenuTypes(vertexTypes,main.currentSession.board.vTypes);
				setMenuTypes(edgeTypes,main.currentSession.board.eTypes);
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
				main.currentSession.board.menuBlock = false;
			}

			public void popupMenuCanceled(PopupMenuEvent arg0) {
				main.currentSession.board.menuBlock = false;
			}
		});

		// Templates
		parityGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				GrapherSettings s = main.currentSession.board.settings;
				s.lexicon.graph			= "game";
				s.lexicon.vertex		= "vertex";
				s.lexicon.vertexValue	= "priors";
				s.lexicon.vertexType	= "owners";
				s.lexicon.vertexLabel	= "Label";
				s.lexicon.edge			= "edge";
				s.lexicon.edgeValue		= "Weights";
				s.lexicon.edgeType		= "Type";
				s.lexicon.edgeLabel		= "Label";
				s.lexicon._graph		= "g";
				s.lexicon._vertex		= "v";
				s.lexicon._vertexType	= "o";
				s.lexicon._vertexValue	= "p";
				s.lexicon._edge			= "e";
				s.lexicon._edgeValue	= "w";
				s.lexicon._edgeType		= " ";
				s.lexicon._edgeLabel	= " ";
				s.showVertexSequence	= false;
				s.showVertexValue		= true;
				s.showVertexType		= true;
				s.showVertexLabel		= false;
				s.showVertexValueDiff	= "";
				s.showVertexLabelDiff	= "";

				s.showEdgeSequence		= false;
				s.showEdgeValue			= false;
				s.showEdgeType			= false;
				s.showEdgeLabel			= false;
				s.showEdgeValueDiff		= "";
				s.showEdgeLabelDiff		= "";
				s.exportType			= 1;

				Vector<Type> vTypes = main.currentSession.board.vTypes;
				if (vTypes.size()<=0) {
					vTypes.add(new Type(0,"Even", "Round"));
				} else {
					vTypes.elementAt(0).setName("Even");
					vTypes.elementAt(0).setDescription("Round");
				}

				if (vTypes.size()<=1) {
					vTypes.add(new Type(1,"Odd", "Square"));
				} else {
					vTypes.elementAt(1).setName("Odd");
					vTypes.elementAt(1).setDescription("Square");
				}

				if (vTypes.size()>=2) {
					for (Vertex v : main.currentSession.board.vertices) {
						if (v.getType() != null && v.getType().getId() >=2) {
							v.setType(null);
						}
					}
					while (vTypes.size()>2) vTypes.remove(2);
				}

				Vector<Type> eTypes = main.currentSession.board.eTypes;
				for (Vertex v : main.currentSession.board.vertices) {
					for (Edge e : v.getOuts()) {
						if (e.getType() != null) {
							e.setType(null);
						}
					}
				}
				eTypes.clear();

				main.properties.lexiconView.refresh();
				main.properties.generalView.refresh();
				main.properties.typesView.refresh();
				main.currentSession.setModified(true);
			}
		});

		parityGameQ.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				GrapherSettings s = main.currentSession.board.settings;
				s.lexicon.graph			= "game";
				s.lexicon.vertex		= "vertex";
				s.lexicon.vertexValue	= "priors";
				s.lexicon.vertexType	= "owners";
				s.lexicon.vertexLabel	= "Label";
				s.lexicon.edge			= "edge";
				s.lexicon.edgeValue		= "weights";
				s.lexicon.edgeType		= "Type";
				s.lexicon.edgeLabel		= "Label";
				s.lexicon._graph		= "g";
				s.lexicon._vertex		= "v";
				s.lexicon._vertexType	= "o";
				s.lexicon._vertexValue	= "p";
				s.lexicon._edge			= "e";
				s.lexicon._edgeValue	= "w";
				s.lexicon._edgeType		= " ";
				s.lexicon._edgeLabel	= " ";
				s.showVertexSequence	= false;
				s.showVertexValue		= true;
				s.showVertexType		= true;
				s.showVertexLabel		= false;
				s.showVertexValueDiff	= "";
				s.showVertexLabelDiff	= "";
				
				s.showEdgeSequence		= false;
				s.showEdgeValue			= true;
				s.showEdgeType			= false;
				s.showEdgeLabel			= false;
				s.showEdgeValueDiff		= "";
				s.showEdgeLabelDiff		= "";

				s.exportType			= 1;

				Vector<Type> vTypes = main.currentSession.board.vTypes;
				if (vTypes.size()<=0) {
					vTypes.add(new Type(0,"Even", "Round"));
				} else {
					vTypes.elementAt(0).setName("Even");
					vTypes.elementAt(0).setDescription("Round");
				}

				if (vTypes.size()<=1) {
					vTypes.add(new Type(1,"Odd", "Square"));
				} else {
					vTypes.elementAt(1).setName("Odd");
					vTypes.elementAt(1).setDescription("Square");
				}

				if (vTypes.size()>=2) {
					for (Vertex v : main.currentSession.board.vertices) {
						if (v.getType() != null && v.getType().getId() >=2) {
							v.setType(null);
						}
					}
					while (vTypes.size()>3) vTypes.remove(2);
				}

				Vector<Type> eTypes = main.currentSession.board.eTypes;
				for (Vertex v : main.currentSession.board.vertices) {
					for (Edge e : v.getOuts()) {
						if (e.getType() != null) {
							e.setType(null);
						}
					}
				}
				eTypes.clear();

				main.properties.lexiconView.refresh();
				main.properties.generalView.refresh();
				main.properties.typesView.refresh();
				main.currentSession.setModified(true);
			}
		});

		stochasticGames.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				GrapherSettings s = main.currentSession.board.settings;
				s.lexicon.graph			= "game";	
				s.lexicon.vertex		= "vertices";
				s.lexicon.vertexValue	= "rewards";
				s.lexicon.vertexType	= "types";
				s.lexicon.vertexLabel	= "Labels";
				s.lexicon.edge			= "actions";
				s.lexicon.edgeValue		= "chances";
				s.lexicon.edgeType		= "Actions";
				s.lexicon.edgeLabel		= "Rewards";
				s.lexicon._graph		= "g";
				s.lexicon._vertex		= "v";
				s.lexicon._vertexValue	= "r";
				s.lexicon._vertexType	= "t";
				s.lexicon._vertexLabel	= "l";
				s.lexicon._edge			= "a";
				s.lexicon._edgeValue	= "c";
				s.lexicon._edgeType		= "a";
				s.lexicon._edgeLabel	= "r";
				s.showVertexSequence	= false;
				s.showVertexValue		= true;
				s.showVertexType		= true;
				s.showVertexLabel		= false;
				s.showVertexValueDiff	= "0";
				s.showVertexLabelDiff	= "";

				s.showEdgeSequence		= false;
				s.showEdgeValue			= true;
				s.showEdgeType			= false;
				s.showEdgeLabel			= false;
				s.showEdgeValueDiff		= "1";
				s.showEdgeLabelDiff		= "0";
				
				s.exportType			= 1;

				Vector<Type> vTypes = main.currentSession.board.vTypes;
				if (vTypes.size()<=0) {
					vTypes.add(new Type(0,"Player1", "Round"));
				} else {
					vTypes.elementAt(0).setName("Player1");
					vTypes.elementAt(0).setDescription("Round");
				}

				if (vTypes.size()<=1) {
					vTypes.add(new Type(1,"Player2", "Square"));
				} else {
					vTypes.elementAt(1).setName("Player2");
					vTypes.elementAt(1).setDescription("Square");
				}

				if (vTypes.size()<=2) {
					vTypes.add(new Type(2,"Random", "Diamond"));
				} else {
					vTypes.elementAt(2).setName("Random");
					vTypes.elementAt(2).setDescription("Diamond");
				}

				if (vTypes.size()>=3) {
					for (Vertex v : main.currentSession.board.vertices) {
						if (v.getType() != null && v.getType().getId() >=3) {
							v.setType(null);
						}
					}
					while (vTypes.size()>3) vTypes.remove(3);
				}

				main.properties.lexiconView.refresh();
				main.properties.generalView.refresh();
				main.properties.typesView.refresh();
				main.currentSession.setModified(true);
			}
		});

	}
}