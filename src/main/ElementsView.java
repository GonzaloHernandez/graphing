package main;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class ElementsView extends JPanel{

    protected JTable        tableStates;
    protected JTable        tableConnections;
    protected GrapherMain   main;
    
    //--------------------------------------------------------------------------

    public ElementsView(GrapherMain main) {
        this.main = main;
        initElements();
        progListeners();
    }
    
    //--------------------------------------------------------------------------

    private void initElements(){
        tableStates         = new JTable();
        tableConnections    = new JTable();
        tableStates.setName("States");
        tableConnections.setName("Connections");

        JSplitPane splitPane = new JSplitPane(  JSplitPane.VERTICAL_SPLIT,
                                                new JScrollPane(tableStates),
                                                new JScrollPane(tableConnections));
        splitPane.setDividerLocation(200); 
        setLayout(new BorderLayout());
        add(splitPane);
        if (main.currentSession!=null) {
            loadTable(tableStates);
            loadTable(tableConnections);
        }
    }
    
    //--------------------------------------------------------------------------

    public void refresh(){
        loadTable(tableStates);
        loadTable(tableConnections);
    }
    
    //--------------------------------------------------------------------------

    private void progListeners(){
        
    }
    
    //--------------------------------------------------------------------------
    
    public void setSelectedTab(int index){
        
    }
    
    //--------------------------------------------------------------------------

    public int getSelectedTab(){
        return 0;
    }
    
    //--------------------------------------------------------------------------
    
    private void loadTable(JTable table) {
        int first =  main.currentSession.board.settings.firstZero?0:1;
        TableModel model = new TableModel() {
            public int getRowCount() {
                if (table.getName()=="States") {
                    return getStates().size();
                }
                if (table.getName()=="Connections") {
                    return getConnections().size();
                }
                return 0;
            }
            public int getColumnCount() {
                return 4;
            }
            public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0: return "Active";
                    case 1: return "Id";
                    case 2: return "Value";
                    case 3: return "Label";
                    default: return null;
                }
            }
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Boolean.class;
                    case 1: return String.class;
                    case 2: return String.class;
                    case 3: return String.class;
                    default: return Object.class;
                }
            }
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0: return true;
                    case 1: return false;
                    case 2: return false;
                    case 3: return false;
                    default: return false;
                }
            }
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (table.getName()=="States") {
                    switch (columnIndex) {
                        case 0: return getStates().elementAt(rowIndex).isActive();
                        case 1: return "V"+(rowIndex+first);
                        case 2: return main.currentSession.board.states.elementAt(rowIndex).getValue();
                        case 3: return "Label";
                    }
                }
                if (table.getName()=="Connections") {
                    switch (columnIndex) {
                        case 0: return getConnections().elementAt(rowIndex).isActive();
                        case 1: return "E"+(rowIndex+first);
                        case 2: return "";
                        case 3: return "Label";
                    }
                }
                return null;
            }
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                if (columnIndex == 0) {
                    Boolean value = (Boolean) aValue;
                    if (table.getName().equals("States")) {
                        // stateSelection.put(rowIndex, value);
                        getStates().elementAt(rowIndex).setActive(value);
                    } else if (table.getName().equals("Connections")) {
                        // connectionSelection.put(rowIndex, value);
                        getConnections().elementAt(rowIndex).setActive(value);
                    }
                    main.currentSession.board.repaint();
                }
            }
            public void addTableModelListener(TableModelListener l) {
                
            }
            public void removeTableModelListener(TableModelListener l) {
                
            }
        };
        table.setModel(model);
    }

    Vector<State> getStates() {
        return main.currentSession.board.states;
    }

    Vector<Connection> getConnections() {
        Vector<Connection> connections = new Vector<Connection>();
        for (int i=0; i<main.currentSession.board.states.size(); i++) {
            for (int j=0; j<main.currentSession.board.states.elementAt(i).getConnections().size(); j++) {
                connections.add(main.currentSession.board.states.elementAt(i).getConnections().elementAt(j));
            }
        }
        return connections;
    }
}
