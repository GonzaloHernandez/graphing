package main;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class WindowTypes extends JDialog {

	//-------------------------------------------------------------------------------------

	private	JTable		table;
	private	JPanel		controls;
	private	JTextField	name,symbols;
	private	JButton		add,delete;
	private	Board		board;
	
	//-------------------------------------------------------------------------------------

	public WindowTypes(Board board) {
		super(board.session.main,true);
		this.board	= board;
		setTitle("Tipos - "+board.session.getTitle());
		setSize(300,300);
		setLocation(50,50);
		initElements();
		progListeners();
		setVisible(true);
	}
	
	//-------------------------------------------------------------------------------------
	
	private void initElements(){
		table		= new JTable();
		controls	= new JPanel(new GridLayout(3,2));
		name		= new JTextField();
		symbols		= new JTextField();
		add			= new JButton("Save");
		delete		= new JButton("Remove");
		
		add(new JScrollPane(table),"Center");
		add(controls,"South");
		controls.add(new JLabel("Name"));		controls.add(name);
		controls.add(new JLabel("Symbol"));	controls.add(symbols);
		controls.add(add);							controls.add(delete);
		delete.setEnabled(false);
		table.setRowSelectionAllowed(true);
		loadTable();		
	}
	
	//-------------------------------------------------------------------------------------
	
	private void loadTable() {
		TableModel model = new TableModel() {

			public int getRowCount() {
				return board.types.size();
			}

			public int getColumnCount() {
				return 2;
			}

			public String getColumnName(int column) {
				switch (column){
					case 0:	return "Name";
					case 1: return "Symbol";
				}
				return null;
			}

			public Class<?> getColumnClass(int column) {
				return String.class;
			}

			public Object getValueAt(int row, int column) {
				switch(column) {
					case 0:	return board.types.elementAt(row).getName();
					case 1: return board.types.elementAt(row).getSymbols();		
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
				for (int i=0;i<board.types.size();i++){
					if (board.types.elementAt(i).getName().equals(name.getText())){
						board.session.main.messageBox("Name of type duplicated","Addition error","Accept");
						return;
					}
					for (int c=0;c<symbols.getText().length();c++){
						if (board.types.elementAt(i).getSymbols().contains(symbols.getText().substring(c,c+1))){
							board.session.main.messageBox("The symbol ["+symbols.getText().substring(c,c+1)+"] is dupplicated in type "+board.types.elementAt(i).getName()+"!","Addition error","Accept");
							return;
						}
					}
				}
				if (add.getText().equals("Save")){
					board.types.add(new ConectionType(board.types.size()+1,name.getText(),symbols.getText()));
				}
				loadTable();
				name.setText("");
				symbols.setText("");
				board.session.setModified(true);
			}
		});
		
		delete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				boolean question = false;
				for (int s=0;s<board.states.size();s++){
					State state = board.states.elementAt(s);
					for (int c=0;c<state.getConnections().size();c++){
						Connection connection = state.getConnections().elementAt(c); 
						if (connection.getType()!=null && connection.getType().equals(board.types.elementAt(table.getSelectedRow()))){
							if (!question) {
								String opc = board.session.main.messageBox("Types used currently.|Do you want to keep updating the connections?","Warning!","Ye|Cancel");
								if (opc.equals("Cancel")||opc.equals("")) return;
								question = true;
							}							
							connection.setType(null);
						}
					}
				}
				board.repaint();
				board.types.remove(table.getSelectedRow());
				loadTable();
				delete.setEnabled(false);
				name.setText("");
				symbols.setText("");
				board.session.setModified(true);
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
	
}
