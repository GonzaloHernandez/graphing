package main;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class ViewStock extends JPanel{

    protected JTable        tableStates;
    protected JTable        tableConnections;
    protected GrapherMain   main;
    
    //--------------------------------------------------------------------------

    public ViewStock(GrapherMain main) {
        this.main = main;
        initElements();
        progListeners();
    }
    
    //--------------------------------------------------------------------------

    private void initElements(){
        tableStates         = new JTable();
        tableConnections    = new JTable();
        tableStates.setName("Vertices");
        tableConnections.setName("Edges");

        JSplitPane splitPane = new JSplitPane(  JSplitPane.VERTICAL_SPLIT,
                                                new JScrollPane(tableStates),
                                                new JScrollPane(tableConnections));
        splitPane.setDividerLocation(200); 

        setLayout(new BorderLayout());
        add(splitPane,BorderLayout.CENTER);
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
        int first =  main.currentSession.board.settings.sequenceType==0?1:0;
        TableModel model = new TableModel() {
            public int getRowCount() {
                if (table.getName()=="Vertices") {
                    return getVertices().size();
                }
                if (table.getName()=="Edges") {
                    return getEdges().size();
                }
                return 0;
            }
            public int getColumnCount() {
                return 5;
            }
            public String getColumnName(int columnIndex) {
                Lexicon lexicon = main.currentSession.board.settings.lexicon;
                if (table.getName()=="Vertices") {
                    switch (columnIndex) {
                        case 0: return "";
                        case 1: return "Id";
                        case 2: return lexicon.vertexValue;
                        case 3: return lexicon.vertexType;
                        case 4: return lexicon.vertexLabel;
                    }
                }
                else if (table.getName()=="Edges") {
                    switch (columnIndex) {
                        case 0: return "";
                        case 1: return "Id";
                        case 2: return lexicon.edgeValue;
                        case 3: return lexicon.edgeType;
                        case 4: return lexicon.edgeLabel;
                    }                }
                return null;
            }
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Boolean.class;
                    case 1: return String.class;
                    case 2: return String.class;
                    case 3: return String.class;
                    case 4: return String.class;
                    default: return Object.class;
                }
            }
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                switch (columnIndex) {
                    case 0: return true;
                    case 1: return false;
                    case 2: return false;
                    case 3: return false;
                    case 4: return false;
                    default: return false;
                }
            }
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (table.getName()=="Vertices") {
                    Vertex v = main.currentSession.board.vertices.elementAt(rowIndex);
                    switch (columnIndex) {
                        case 0: return getVertices().elementAt(rowIndex).isActive();
                        case 1: return getVertices().elementAt(rowIndex).getIdString(main.currentSession.board.settings);
                        case 2: return v.getValue();
                        case 3: return v.getType()!=null?v.getType().getName():"";
                        case 4: return v.getLabel();
                    }
                }
                if (table.getName()=="Edges") {
                    Edge e = getEdges().elementAt(rowIndex);
                    switch (columnIndex) {
                        case 0: return e.isActive();
                        case 1: return ""+(rowIndex+first);
                        case 2: return e.getValue();
                        case 3: return e.getType()!=null?e.getType().getName():"";
                        case 4: return e.getLabel();
                    }
                }
                return null;
            }
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                if (columnIndex == 0) {
                    Boolean value = (Boolean) aValue;
                    if (table.getName().equals("Vertices")) {
                        getVertices().elementAt(rowIndex).setActive(value);
                    } else if (table.getName().equals("Edges")) {
                        getEdges().elementAt(rowIndex).setActive(value);
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

        TableColumnModel vColumnModel = table.getColumnModel();
        vColumnModel.getColumn(0).setPreferredWidth(20);
        vColumnModel.getColumn(0).setMaxWidth(20);
        vColumnModel.getColumn(1).setPreferredWidth(40);
        vColumnModel.getColumn(1).setMaxWidth(40);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
    }

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
}
