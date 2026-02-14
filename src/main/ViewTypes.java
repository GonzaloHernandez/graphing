package main;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class ViewTypes extends JPanel {

	//-------------------------------------------------------------------------------------

	private	JTable		vTable,eTable;
	private JButton		vAdd,eAdd;
	private	GrapherMain	main;
	
	//-------------------------------------------------------------------------------------

	public ViewTypes(GrapherMain main) {
		this.main = main;
		initElements();
		progListeners();
	}
	
	//-------------------------------------------------------------------------------------
	
	private void initElements(){
		vTable = new JTable();
		eTable = new JTable();
		vAdd = new JButton("+");
		eAdd = new JButton("+");

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		c.gridx = 0;
		c.weightx = 1.0;

		JPanel vTitlePanel = new JPanel(new BorderLayout());
		vTitlePanel.add(new JLabel("Vertex types"),BorderLayout.CENTER);
		vTitlePanel.add(vAdd,BorderLayout.EAST);
		c.gridy = 0;
		c.weighty = 0.0;
		add(vTitlePanel, c);

		c.gridy = 1;
		c.weighty = 1.0;
		add(new JScrollPane(vTable), c);

		JPanel eTitlePanel = new JPanel(new BorderLayout());
		eTitlePanel.add(new JLabel("Edge types"),BorderLayout.CENTER);
		eTitlePanel.add(eAdd,BorderLayout.EAST);
		c.gridy = 2;
		c.weighty = 0.0;
		add(eTitlePanel, c);

		c.gridy = 3;
		c.weighty = 1.0;
		add(new JScrollPane(eTable), c);

		if (main.currentSession!=null) {
			vTable.setModel(loadTable(main.currentSession.board.vTypes));
			eTable.setModel(loadTable(main.currentSession.board.eTypes));

			TableColumnModel vColumnModel = vTable.getColumnModel();
			vColumnModel.getColumn(0).setPreferredWidth(30); 
			vColumnModel.getColumn(0).setMaxWidth(30);
			TableColumnModel eColumnModel = eTable.getColumnModel();
			eColumnModel.getColumn(0).setPreferredWidth(30); 
			eColumnModel.getColumn(0).setMaxWidth(30);
		}

	}
	
	//-------------------------------------------------------------------------------------
	
	private TableModel loadTable(Vector<Type> types) {
		TableModel model = new TableModel() {

			public int getRowCount() {
				return types.size();
			}

			public int getColumnCount() {
				return 3;
			}

			public String getColumnName(int column) {
				switch (column){
					case 0:	return "Id";
					case 1:	return "Name";
					case 2: return "Symbols";
				}
				return null;
			}

			public Class<?> getColumnClass(int column) {
				return String.class;
			}

			public Object getValueAt(int row, int column) {
				switch(column) {
					case 0: return row;
					case 1:	return types.elementAt(row).getName();
					case 2: return types.elementAt(row).getDescription();		
				}
				return null;
			}

			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}

			public void setValueAt(Object arg0, int arg1, int arg2) {
			}

			public void addTableModelListener(TableModelListener arg0) {
			}

			public void removeTableModelListener(TableModelListener arg0) {
			}
		};

		return model;
	}
	
	//-------------------------------------------------------------------------------------

	private void progListeners(){

		vTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE && e.isControlDown()) {
					int row = vTable.getSelectedRow();
					if (row != -1) {
						main.currentSession.board.vTypes.remove(row);
						int index = 0;
						for (Type type : main.currentSession.board.vTypes) {
							type.setId(index++);
						}
						vTable.revalidate();
						vTable.repaint();
					}
				}
			}
		});

		eTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE && e.isControlDown()) {
					int row = eTable.getSelectedRow();
					if (row != -1) {
						main.currentSession.board.eTypes.remove(row);
						int index = 0;
						for (Type type : main.currentSession.board.eTypes) {
							type.setId(index++);
						}
						eTable.revalidate();
						eTable.repaint();
					}
				}
			}
		});

		vAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JTextField name = new JTextField();
				JTextField desc = new JTextField();

				JPanel info = new JPanel(new GridLayout(3,1));
				JPanel data = new JPanel(new GridLayout(3,1));
				JPanel panel = new JPanel(new BorderLayout());
				panel.add(info,BorderLayout.WEST);
				panel.add(data,BorderLayout.CENTER);
				
				info.add(new JLabel("Name "));
				data.add(name);
				info.add(new JLabel("Description "));
				data.add(desc);

				panel.setPreferredSize(new Dimension(300, 70));
				int result = JOptionPane.showConfirmDialog(vTable, panel, 
						"Vertice types", JOptionPane.OK_CANCEL_OPTION);

				if (result == JOptionPane.OK_OPTION) {
					Vector<Type> type = main.currentSession.board.vTypes;
					type.add(new Type(type.size(),name.getText(),desc.getText()));
				}
				main.currentSession.setModified(true);
				vTable.revalidate();
				vTable.repaint();
			}
		});

		eAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JTextField name = new JTextField();
				JTextField desc = new JTextField();

				JPanel info = new JPanel(new GridLayout(3,1));
				JPanel data = new JPanel(new GridLayout(3,1));
				JPanel panel = new JPanel(new BorderLayout());
				panel.add(info,BorderLayout.WEST);
				panel.add(data,BorderLayout.CENTER);
				
				info.add(new JLabel("Name "));
				data.add(name);
				info.add(new JLabel("Description "));
				data.add(desc);

				panel.setPreferredSize(new Dimension(300, 70));
				int result = JOptionPane.showConfirmDialog(eTable, panel, 
						"Vertice types", JOptionPane.OK_CANCEL_OPTION);

				if (result == JOptionPane.OK_OPTION) {
					Vector<Type> type = main.currentSession.board.eTypes;
					type.add(new Type(type.size(),name.getText(),desc.getText()));
				}
				main.currentSession.setModified(true);
				eTable.revalidate();
				eTable.repaint();
			}
		});
	}
	
	//-------------------------------------------------------------------------------------

	public void refresh(){
		vTable.setModel(loadTable(main.currentSession.board.vTypes));
		eTable.setModel(loadTable(main.currentSession.board.eTypes));
		TableColumnModel vColumnModel = vTable.getColumnModel();
		vColumnModel.getColumn(0).setPreferredWidth(30); 
		vColumnModel.getColumn(0).setMaxWidth(30);
		TableColumnModel eColumnModel = eTable.getColumnModel();
		eColumnModel.getColumn(0).setPreferredWidth(30); 
		eColumnModel.getColumn(0).setMaxWidth(30);
	}
	
}