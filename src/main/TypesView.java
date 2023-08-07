package main;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class TypesView extends JPanel {

	//-------------------------------------------------------------------------------------

	private	JTable		table;
	private	JPanel		controls;
	private	JTextField	name,symbols;
	private	JButton		add,delete;
	private	GrapherMain	main;
	
	//-------------------------------------------------------------------------------------

	public TypesView(GrapherMain main) {
		this.main = main;
		initElements();
		progListeners();
	}
	
	//-------------------------------------------------------------------------------------
	
	private void initElements(){
		table		= new JTable();
		controls	= new JPanel(new GridLayout(3,2));
		name		= new JTextField();
		symbols		= new JTextField();
		add			= new JButton("Save");
		delete		= new JButton("Delete");
		setLayout(new BorderLayout());
		add(new JScrollPane(table),"Center");
		add(controls,"South");
		controls.add(new JLabel("Name"));		controls.add(name);
		controls.add(new JLabel("Symbols"));	controls.add(symbols);
		controls.add(add);						controls.add(delete);
		delete.setEnabled(false);
		table.setRowSelectionAllowed(true);
		if (main.currentSession!=null) loadTable();		
	}
	
	//-------------------------------------------------------------------------------------
	
	private void loadTable() {
		TableModel model = new TableModel() {

			public int getRowCount() {
				return main.currentSession.board.types.size()-1;
			}

			public int getColumnCount() {
				return 2;
			}

			public String getColumnName(int column) {
				switch (column){
					case 0:	return "Name";
					case 1: return "Symbols";
				}
				return null;
			}

			public Class<?> getColumnClass(int column) {
				return String.class;
			}

			public Object getValueAt(int row, int column) {
				switch(column) {
					case 0:	return main.currentSession.board.types.elementAt(row+1).getName();
					case 1: return main.currentSession.board.types.elementAt(row+1).getSymbols();		
				}
				return null;
			}

			public boolean isCellEditable(int arg0, int arg1) {
				return true;
			}

			public void setValueAt(Object arg0, int arg1, int arg2) {
			}

			public void addTableModelListener(TableModelListener arg0) {
			}

			public void removeTableModelListener(TableModelListener arg0) {
			}
		};

		table.setModel(model);
	}
	
	//-------------------------------------------------------------------------------------

	private void progListeners(){
		
		add.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				for (int i=0;i<main.currentSession.board.types.size();i++){
					if (main.currentSession.board.types.elementAt(i).getName().equals(name.getText())){
						main.currentSession.board.session.main.messageBox("Duplicated name of type ≈","Addition error","Accept");
						return;
					}
					for (int c=0;c<symbols.getText().length();c++){
						if (main.currentSession.board.types.elementAt(i).getSymbols().contains(symbols.getText().substring(c,c+1))){
							main.currentSession.board.session.main.messageBox("The symbol ["+symbols.getText().substring(c,c+1)+"] is dupplicated in type "+main.currentSession.board.types.elementAt(i).getName()+"!","Addition error","Accept");
							return;
						}
					}
				}
				if (add.getText().equals("Save")){
					int n;
					n = main.currentSession.board.types.size();
					main.currentSession.board.types.add(new ConnectionType(n+1,name.getText(),symbols.getText()));
				}
				loadTable();
				name.setText("");
				symbols.setText("");
				main.currentSession.board.session.setModified(true);
			}
		});
		
		delete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				boolean question = false;
				for (int s=0;s<main.currentSession.board.states.size();s++){
					State state = main.currentSession.board.states.elementAt(s);
					for (int c=0;c<state.getConnections().size();c++){
						Connection connection = state.getConnections().elementAt(c); 
						if (connection.getType()!=null && connection.getType().equals(main.currentSession.board.types.elementAt(table.getSelectedRow()+1))){
							if (!question) {
								String opc = main.currentSession.board.session.main.messageBox("Type currently used.|Do you want to keep updating the connections?","Warning!","Yes|Cancel");
								if (opc.equals("Cancel")||opc.equals("")) return;
								question = true;
							}							
							connection.setType(null);
						}
					}
				}
				main.currentSession.board.repaint();
				main.currentSession.board.types.remove(table.getSelectedRow()+1);
				for (int i=1;i<main.currentSession.board.types.size();i++){
					main.currentSession.board.types.elementAt(i).setNumber(i+1);
				}
				loadTable();
				delete.setEnabled(false);
				name.setText("");
				symbols.setText("");
				main.currentSession.board.session.setModified(true);
			}
		});
		
		table.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				delete.setEnabled(true);
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseReleased(MouseEvent arg0) {
			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}
			
		});
		
		table.addKeyListener(new KeyListener(){

			public void keyTyped(KeyEvent arg0) {
				//System.out.println("typed");
			}

			public void keyPressed(KeyEvent arg0) {
				//System.out.println("pressed");
			}

			public void keyReleased(KeyEvent e) {
				//System.out.println("released:"+e.toString());
			}
			
		});
		

	}
	
	//-------------------------------------------------------------------------------------

	public void refresh(){
		loadTable();
	}
	
}