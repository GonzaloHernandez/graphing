package main;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

class TypeMenuItem extends JMenuItem {
	private ConnectionType	type;
	public TypeMenuItem(String label,ConnectionType type) {
		super(label);
		this.type	= type;
		setFont(new Font("Arial",Font.ITALIC,10));
		setIcon(new ImageIcon("icons/type.png"));
	}
	public ConnectionType getType() {
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

	protected	GrapherMenu		stateMenu,connectionMenu,grapherMenu;
	
	private	GrapherMain		main;
	private GrapherMenu		connectionTypes;
	private	GrapherItem		stateDelete,stateDeleteAllOutgoings,stateAccepted,stateOnwer;
	private	GrapherItem		connectionDelete,connectionTune,connectionNoType;
	private	GrapherItem		restart,load,save,saveAs,print,simulate,help;
	private	TypeMenuItem	typeItems[];
	
	//-------------------------------------------------------------------------------------

	public MenuOptions(GrapherMain main) {
		this.main	= main;
		initElements();
		progListeners();
	}
	
	//-------------------------------------------------------------------------------------

	private void initElements(){
		Font defaultFont		= new Font("Cantarell",Font.PLAIN,11);
		
		stateMenu				= new GrapherMenu("State",defaultFont,"state.png");
		connectionMenu			= new GrapherMenu("Connection",defaultFont,"connection.png");
		grapherMenu				= new GrapherMenu("Grapher",defaultFont,"application.png");
				
		stateDelete				= new GrapherItem("Delete state",defaultFont,"delete_state.png");
		stateDeleteAllOutgoings	= new GrapherItem("Delete all outgoing connections",defaultFont,"delete_connection.png");
		stateAccepted			= new GrapherItem("Set final state",defaultFont,"accepted_state.png");
		stateOnwer				= new GrapherItem("Switch owner",defaultFont,"owner.png");
		
		connectionTypes			= new GrapherMenu("Set state",defaultFont,"type_connection.png");
		connectionNoType		= new GrapherItem("Unset state",defaultFont,"no_type_connection.png");
		connectionDelete		= new GrapherItem("Delete connection",defaultFont,"delete_connection.png");
		connectionTune			= new GrapherItem("Tune arc",defaultFont,"tune_connection.png");
		
		restart					= new GrapherItem("Restar session",defaultFont,"new.png");
		load					= new GrapherItem("Load automata",defaultFont,"open.png");
		save					= new GrapherItem("Save automata",defaultFont,"save.png");
		saveAs					= new GrapherItem("Save automata as ...",defaultFont,"saveas.png");
		print					= new GrapherItem("Export as PNG",defaultFont,"print.png");
		simulate				= new GrapherItem("Simulate",defaultFont,"simulate.png");
		help					= new GrapherItem("Help",defaultFont,"help.png");
				
		add(stateMenu);
		add(connectionMenu);
		add(grapherMenu);
		
		stateMenu.add(stateDelete);
		stateMenu.add(stateDeleteAllOutgoings);
		stateMenu.add(stateAccepted);
		stateMenu.add(stateOnwer);
		connectionMenu.add(connectionTypes);
		connectionMenu.add(connectionNoType);
		connectionMenu.add(connectionDelete);
		connectionMenu.add(connectionTune);
		
		grapherMenu.add(restart);
		grapherMenu.add(load);
		grapherMenu.add(save);
		grapherMenu.add(saveAs);
		grapherMenu.addSeparator();
		grapherMenu.add(print);
		grapherMenu.addSeparator();
		grapherMenu.add(simulate);
		grapherMenu.add(help);
	}

	//-------------------------------------------------------------------------------------
	
	public void setMenuTypes(JComponent connectionTypes,boolean retype){
		connectionTypes.removeAll();
		typeItems	= new TypeMenuItem[main.currentSession.board.types.size()];
		for (int i=0 ; i<main.currentSession.board.types.size() ; i++) {
			ConnectionType type = (ConnectionType)main.currentSession.board.types.elementAt(i); 
			typeItems[i]= new TypeMenuItem(type.getName(),type);
			
			typeItems[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					main.currentSession.board.currentConnection.setType(((TypeMenuItem)e.getSource()).getType());
					main.currentSession.board.repaint();
					main.currentSession.setModified(true);
				}
			});
			connectionTypes.add(typeItems[i]);
			// if (i==0) ((JMenu)connectionTypes).addSeparator();
		}
	}
	
	//-------------------------------------------------------------------------------------

	public void show(boolean state,boolean connection,boolean grapher){
		if (state){
			if (main.currentSession.board.stateTarget.isAccepted()) { 
				stateAccepted.setText("Unset as a final state");
			}
			else {
				stateAccepted.setText("Set as a final state");
			}
			stateMenu.setEnabled(true);
		}
		else{
			stateMenu.setEnabled(false);
		}
		//----------------------------------------------------------------------
		if (connection){
			if (!main.currentSession.board.currentConnection.getSource().equals(main.currentSession.board.currentConnection.getTarget()) && main.currentSession.board.currentConnection.getDistance()!=0) {
				connectionTune.setEnabled(true);
			}
			else {
				connectionTune.setEnabled(false);
			}
			
			if (main.currentSession.board.currentConnection.getType()!=null){
				connectionNoType.setEnabled(true);
			}
			else{
				connectionNoType.setEnabled(false);
			}
			
			connectionMenu.setEnabled(true);
		}
		else{
			connectionMenu.setEnabled(false);
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

	public void showTypes(boolean retype){
		JPopupMenu menu = new JPopupMenu();
		setMenuTypes(menu,retype);
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
		
		stateDelete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				State source = main.currentSession.board.stateTarget;
				main.currentSession.board.deleteState(source);
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		stateDeleteAllOutgoings.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.stateTarget.deleteAllConnecion();
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		stateAccepted.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.stateTarget.setAccepted(!main.currentSession.board.stateTarget.isAccepted());
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});		
		
		stateOnwer.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.stateTarget.setOwner(1-main.currentSession.board.stateTarget.getOwner());
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});		

		connectionDelete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				State source = main.currentSession.board.currentConnection.getSource();
				source.deleteConnecion(main.currentSession.board.currentConnection);
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});
		
		connectionTune.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.currentConnection.setDistance(0);
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});	
		
		connectionNoType.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.currentConnection.setType(null);
				main.currentSession.setModified(true);
				main.currentSession.board.repaint();
			}
		});	
		
		restart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.restart();
				main.currentSession.setModified(false);
			}
		});

		load.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				main.currentSession.board.load();
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
				setMenuTypes(connectionTypes,false);
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