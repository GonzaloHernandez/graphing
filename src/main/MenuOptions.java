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
import javax.swing.JOptionPane;
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
		setFont(defaultFont);
		setIcon(new ImageIcon("icons/type.png"));
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
		// ImageIcon img = new ImageIcon(GrapherMain.class.getResource("icons/"+icon));
		// if (icon!=null) setIcon(img);
	}
}

public class MenuOptions extends JPopupMenu{

	//-------------------------------------------------------------------------------------

	protected	GrapherMenu		vertexMenu,edgeMenu,grapherMenu,template;
	
	private	GrapherMain		main;
	private GrapherMenu		vertexTypes,edgeTypes;
	private	GrapherItem		vertexDelete,vertexDeleteAllOutgoings,vertexAccepted;
	private	GrapherItem		edgeDelete,edgeTune;
	private	GrapherItem		restart,parityGame,load,loadImport,save,saveAs,print,simulate,help;
	private	TypeMenuItem[]	vertexTypeItems,edgeTypeItems;
	
	//-------------------------------------------------------------------------------------

	public MenuOptions(GrapherMain main) {
		this.main	= main;
		initElements();
		progListeners();
	}
	
	//-------------------------------------------------------------------------------------

	private void initElements(){
		Font currentFont		= UIManager.getFont("Label.font");
		Font defaultFont		= new Font(currentFont.getName(),Font.PLAIN,currentFont.getSize());

		vertexMenu				= new GrapherMenu("State",defaultFont,"state.png");
		edgeMenu				= new GrapherMenu("Connection",defaultFont,"connection.png");
		grapherMenu				= new GrapherMenu("Grapher",defaultFont,"application.png");
		template				= new GrapherMenu("Use template",defaultFont,"credits.png");
				
		vertexDelete				= new GrapherItem("Delete state",defaultFont,"delete_state.png");
		vertexDeleteAllOutgoings	= new GrapherItem("Delete all outgoing connections",defaultFont,"delete_connection.png");
		vertexAccepted			= new GrapherItem("Set final state",defaultFont,"accepted_state.png");
		vertexTypes				= new GrapherMenu("Set type",defaultFont,"owner.png");
		
		edgeDelete				= new GrapherItem("Delete connection",defaultFont,"delete_connection.png");
		edgeTune				= new GrapherItem("Tune arc",defaultFont,"tune_connection.png");
		edgeTypes				= new GrapherMenu("Set type",defaultFont,"type_connection.png");

		restart					= new GrapherItem("Restar session",defaultFont,"new.png");
		parityGame				= new GrapherItem("Parity Game",defaultFont,"");
		load					= new GrapherItem("Load automaton",defaultFont,"open.png");
		loadImport				= new GrapherItem("Import automaton",defaultFont,"open.png");
		save					= new GrapherItem("Save automaton",defaultFont,"save.png");
		saveAs					= new GrapherItem("Save automaton as ...",defaultFont,"saveas.png");
		print					= new GrapherItem("Export as PNG",defaultFont,"print.png");
		simulate				= new GrapherItem("Simulate",defaultFont,"simulate.png");
		help					= new GrapherItem("Help",defaultFont,"help.png");
		
		add(vertexMenu);
		add(edgeMenu);
		add(grapherMenu);
		
		vertexMenu.add(vertexDelete);
		vertexMenu.add(vertexDeleteAllOutgoings);
		vertexMenu.add(vertexAccepted);
		vertexMenu.add(vertexTypes);
		edgeMenu.add(edgeDelete);
		edgeMenu.add(edgeTune);
		edgeMenu.add(edgeTypes);
		
		grapherMenu.add(restart);
		grapherMenu.add(template);
		grapherMenu.add(load);
		grapherMenu.add(loadImport);
		grapherMenu.add(save);
		grapherMenu.add(saveAs);
		grapherMenu.addSeparator();
		grapherMenu.add(print);
		grapherMenu.addSeparator();
		grapherMenu.add(simulate);
		grapherMenu.add(help);

		template.add(parityGame);
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
						main.currentSession.board.currentConnection.setType(t);
					}
					main.currentSession.board.repaint();
					main.currentSession.setModified(true);
				}
			});
			menu.add(typeItem);
		}
	}
	
	//-------------------------------------------------------------------------------------

	public void show(boolean state,boolean connection,boolean grapher){
		if (state){
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
		if (connection){
			if (!main.currentSession.board.currentConnection.getSource().equals(main.currentSession.board.currentConnection.getTarget()) && main.currentSession.board.currentConnection.getDistance()!=0) {
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
		
		menu.show(main.currentSession.board,(int)main.currentSession.board.getMousePosition().getX(),(int)main.currentSession.board.getMousePosition().getY());
		
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
		
		vertexDeleteAllOutgoings.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.vertexTarget.deleteAllConnecion();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		vertexAccepted.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.vertexTarget.setAccepted(!main.currentSession.board.vertexTarget.isAccepted());
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});		
		
		vertexTypes.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.vertexTarget.setType(main.currentSession.board.vertexTarget.getType());
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});

		edgeDelete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Vertex source = main.currentSession.board.currentConnection.getSource();
				source.deleteConnecion(main.currentSession.board.currentConnection);
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		edgeTune.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.currentConnection.setDistance(0);
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});	
		
		// edgeNoType.addActionListener(new ActionListener(){
		// 	public void actionPerformed(ActionEvent e) {
		// 		main.currentSession.board.currentConnection.setType(null);
		// 		main.currentSession.setModified(true);
		// 		main.currentSession.board.repaint();
		// 	}
		// });	
		
		restart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.restart();
				main.currentSession.setModified(false);
			}
		});

		parityGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GrapherSettings sets = main.currentSession.board.settings;
				sets.dictionary.graph		= "game";
				sets.dictionary.vertex		= "vertex";
				sets.dictionary.vertexType	= "owners";
				sets.dictionary.vertexValue	= "priors";
				sets.dictionary.edge		= "edge";
				sets.dictionary.edgeValue	= "weights";
				sets.dictionary._graph		= "g";
				sets.dictionary._vertex		= "v";
				sets.dictionary._vertexType	= "o";
				sets.dictionary._vertexValue= "p";
				sets.dictionary._edge		= "e";
				sets.dictionary._edgeValue	= "w";
				sets.showVertexSequence		= false;
				sets.showConnectionSequence	= false;
				sets.showVertexPriorities	= true;
				main.properties.dictionaryView.refresh();
				main.properties.generalView.refresh();
				main.currentSession.setModified(true);			}
		});

		load.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.load();
			}
		});

		loadImport.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.loadImport();
			}
		});

		save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.save(false);
			}
		});
		
		saveAs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.save(true);
			}
		});
		
		print.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				// main.currentSession.board.print();
				main.currentSession.board.screenshot(getGraphics());
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
		
	}
}