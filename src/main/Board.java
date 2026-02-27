package main;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class Board extends JComponent implements Printable{
	
	//-------------------------------------------------------------------------------------
	
	protected int				mousex,mousey;
	protected boolean			controled;
	protected String			fileName;
	protected GrapherSettings	settings;
	protected Vector<Vertex>	vertices;
	protected GrapherSession	session;
	protected Vector<Type>		eTypes,vTypes;
	protected Edge				currentEdge;
	protected Vertex			vertexSource,vertexTarget;
	protected boolean			menuBlock;
	protected Compiler			compiler;
	protected PageFormat		pageFormat;
	protected double			scaleFactor;
	protected int				gridScale;
	protected boolean			hidden;
	protected boolean			showGrid;

	//-------------------------------------------------------------------------------------
	
	public Board(GrapherSession session) {
		this.session		= session;

		this.menuBlock		= false;
		this.compiler		= null;
		this.scaleFactor	= 1;
		this.gridScale		= 10;
		this.hidden			= false;
		this.showGrid		= false;
		getInputMap().put(KeyStroke.getKeyStroke("A"), "actionName");
		initElements();
		progListeners();
	}

	//-------------------------------------------------------------------------------------

	private void initElements() {
		this.fileName	= "";
		this.compiler	= null;
		
		eTypes	= new Vector<Type>();
		vTypes	= new Vector<Type>();

		vTypes.add(new Type(0,"Round", "Even"));
		vTypes.add(new Type(1,"Square", "Odd"));
		vTypes.add(new Type(2,"Diamond", "Nature"));

		eTypes.add(new Type(0,"number","0123456789"));
		eTypes.add(new Type(1,"point","."));
		eTypes.add(new Type(2,"uppercase","ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		eTypes.add(new Type(3,"lowercase","abcdefghijklmnopqrstuvwxyz"));

		settings		= new GrapherSettings();
		vertices		= new Vector<Vertex>();
		vertexSource	= null;
		vertexTarget	= null;
		controled		= false;		
		pageFormat		= new PageFormat();
		Paper		paper	= new Paper();
		paper.setSize(8.5*72,11*72); //letter size
		paper.setImageableArea(72,72,paper.getWidth()-72*2,paper.getHeight()-72*2);
		pageFormat.setOrientation(PageFormat.PORTRAIT);
		pageFormat.setPaper(paper);
	}

	//-------------------------------------------------------------------------------------

	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;

		g.scale(scaleFactor,scaleFactor);

		g.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setRenderingHint(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.setColor(Color.WHITE);
		g.fillRect(0,0,(int)(session.getWidth()/scaleFactor),(int)(session.getHeight()/scaleFactor));

		if (showGrid && gridScale >= 5) {
			for(int gy=0; gy<getHeight()/scaleFactor; gy+=gridScale){
				g.setColor(new Color(245,245,245));
				g.drawLine(0,gy,getWidth(),gy);
			}
			for(int gx=gridScale; gx<getWidth()/scaleFactor; gx+=gridScale){
				g.setColor(new Color(245,245,245));
				g.drawLine(gx,0,gx,getHeight());
			}
		}

		if (controled) {
			g.setColor(Color.BLACK);
			if (vertexTarget!=null) {
				g.drawLine(vertexSource.getX(),vertexSource.getY(),vertexTarget.getX(),vertexTarget.getY());
			}
			else {
				g.drawLine(vertexSource.getX(),vertexSource.getY(),mousex,mousey);
			}
		}
		int edgeSequence = settings.firstZero?0:1;
		for (int i=0 ; i<vertices.size() ; i++) {
			edgeSequence = vertices.elementAt(i).draw(g,settings,edgeSequence,hidden);
		}

		exportView();
		session.main.properties.stockView.refresh();
	}
	
	//-------------------------------------------------------------------------------------
	
	public Vertex addVertex(int x,int y){
		Vertex n = new Vertex(vertices.size(),x,y);
		vertices.add(n);
		repaint();
		session.setModified(true);
		return n;
	}

	//-------------------------------------------------------------------------------------

	public void progListeners() {
		addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) {
				if (currentEdge != null) {
					if (!e.isControlDown()){
						if (e.getKeyCode() == KeyEvent.VK_A) {
							currentEdge.setAmountDistance(2);
						}
						else if (e.getKeyCode() == KeyEvent.VK_Z){
							currentEdge.setAmountDistance(-2);
						}
						else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
							Vertex source = currentEdge.getSource();
							source.deleteEdge(currentEdge);
						}
						else  if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') {
							currentEdge.setValue(""+e.getKeyChar());
						}
					}
					else {
						if (e.getKeyCode() == KeyEvent.VK_A) {
							currentEdge.setAmountRotation((Math.PI/16));
						}
						else if (e.getKeyCode() == KeyEvent.VK_Z){
							currentEdge.setAmountRotation(-(Math.PI/16));
						}
					}
					session.setModified(true);
				}

				if (vertexTarget != null) {
					if (e.getKeyCode() == KeyEvent.VK_DELETE) {
						deleteVertex(vertexTarget);
						session.setModified(true);
					}
					else  if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') {
						vertexTarget.setValue(""+e.getKeyChar());
						session.setModified(true);
					}
				}

				if (e.isControlDown()){
					if (e.getKeyCode() == KeyEvent.VK_S) {
						if (e.isShiftDown()) {
							session.main.persistence.saveSession(true);
						} else {
							session.main.persistence.saveSession(false);
						}
					}
					else if (e.getKeyChar() == '+' || e.getKeyChar() == '=' || 
							e.getKeyChar() == '*' || e.getKeyCode() == KeyEvent.VK_PLUS) {
						if (scaleFactor > 5.0) return;
						scaleFactor += 0.1;
						Dimension d = getPreferredSize();
						session.manualResizing = true;
						session.setSize((int)(d.width*scaleFactor)+session.deltaWidth,(int)(d.height*scaleFactor)+session.deltaHeight);
					}
					else if (e.getKeyChar() == '-' || e.getKeyChar() == '_' ||
							e.getKeyCode() == KeyEvent.VK_MINUS) {
						if (scaleFactor < 0.2) return;
						scaleFactor -= 0.1;
						Dimension d = getPreferredSize();
						session.manualResizing = true;
						session.setSize((int)(d.width*scaleFactor)+session.deltaWidth,(int)(d.height*scaleFactor)+session.deltaHeight);
					}
				}

				if (e.getKeyCode() == KeyEvent.VK_H) {
					hidden = true;
				}
				else if (e.getKeyCode() == KeyEvent.VK_G) {
					showGrid = !showGrid;
				}

				repaint();
			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_H) {
					hidden = false;
				}
				repaint();
			}

			public void keyTyped(KeyEvent e) {

			}
			
		});

		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				int modifiers = e.getModifiersEx();
				int button = e.getButton();

				boolean shiftDown = (modifiers & InputEvent.SHIFT_DOWN_MASK) != 0;
				boolean ctrlDown = (modifiers & InputEvent.CTRL_DOWN_MASK) != 0;
				boolean altDown = (modifiers & InputEvent.ALT_DOWN_MASK) != 0;
				
				// SHIFT + BUTTON1
				if (shiftDown && button == MouseEvent.BUTTON1) {
					if (vertexTarget != null) {
						vertexTarget.setActive(!vertexTarget.isActive());
						session.setModified(true);
					}
					if (currentEdge != null) {
						currentEdge.setActive(!currentEdge.isActive());
						session.setModified(true);
					}
					repaint();
					return;
				}

				// SHIFT + BUTTON3
				if (shiftDown && button == MouseEvent.BUTTON3) {
					if (vertexTarget != null) {
						vertexTarget.setActive(!vertexTarget.isActive(), true);
						session.setModified(true);
					}
					repaint();
					return;
				}

				// ALT + BUTTON3
				if (altDown && button == MouseEvent.BUTTON3) {
					if (vertexTarget != null) {
						session.main.menuOptions.showTypes(vTypes);
					}
					if (currentEdge != null) {
						session.main.menuOptions.showTypes(eTypes);
					}
					repaint();
					return;
				}

				// BUTTON1 + Double-click (No CTRL)
				if (button == MouseEvent.BUTTON1 && e.getClickCount() == 2 && !ctrlDown) {
					if (vertexTarget != null) {
						JTextField value = new JTextField(vertexTarget.getValue());
						JTextField label = new JTextField(vertexTarget.getLabel());
						String[] options = new String[vTypes.size()+1];
						for (int i=0; i<vTypes.size(); i++) {
							options[i] = vTypes.elementAt(i).getName();
						}
						options[vTypes.size()] = "<None>";
						JComboBox<String> type = new JComboBox<>(options);
						if (vertexTarget.getType() == null) {
							type.setSelectedIndex(vTypes.size());
						} else {
							type.setSelectedIndex(vertexTarget.getType().getId());
						}

						JPanel info = new JPanel(new GridLayout(3,1));
						JPanel data = new JPanel(new GridLayout(3,1));
						JPanel panel = new JPanel(new BorderLayout());
						panel.add(info,BorderLayout.WEST);
						panel.add(data,BorderLayout.CENTER);
						
						String val = settings.lexicon.vertexValue;
						String lab = settings.lexicon.vertexLabel;
						String typ = settings.lexicon.vertexType;
						
						info.add(new JLabel(""+Lexicon.capitalize(val)+" "));
						data.add(value);
						info.add(new JLabel(""+Lexicon.capitalize(typ)+" "));
						data.add(type);
						info.add(new JLabel(""+Lexicon.capitalize(lab)+" "));
						data.add(label);

						panel.setPreferredSize(new Dimension(300, 100));

						String title = "Properties for "+
							Lexicon.capitalize(settings.lexicon.vertex);
						String result = session.main.grapherDialog(title,panel,"Ok|Cancel");
						if (result.equals("Ok")) {
							try {
								Double.parseDouble(value.getText());
							}
							catch (NumberFormatException ex) {
								return;
							}
							Double.parseDouble(value.getText());
							vertexTarget.setValue(value.getText());
							vertexTarget.setLabel(label.getText());
							if (type.getSelectedIndex() == options.length-1) {
								vertexTarget.setType(null);
							} else {
								vertexTarget.setType(vTypes.elementAt(type.getSelectedIndex()));
							}
						}
						repaint();
						session.setModified(true);						
						return;
					}
					if (currentEdge != null) {

						JTextField value = new JTextField(currentEdge.getValue());
						JTextField label = new JTextField(currentEdge.getLabel());
						String[] options = new String[eTypes.size()+1];
						for (int i=0; i<eTypes.size(); i++) {
							options[i] = eTypes.elementAt(i).getName();
						}
						options[eTypes.size()] = "<None>";
						JComboBox<String> type = new JComboBox<>(options);
						if (currentEdge.getType() == null) {
							type.setSelectedIndex(eTypes.size());
						} else {
							type.setSelectedIndex(currentEdge.getType().getId());
						}

						JPanel info = new JPanel(new GridLayout(3,1));
						JPanel data = new JPanel(new GridLayout(3,1));
						JPanel panel = new JPanel(new BorderLayout());
						panel.add(info,BorderLayout.WEST);
						panel.add(data,BorderLayout.CENTER);
						
						String val = settings.lexicon.edgeValue;
						String lab = settings.lexicon.edgeLabel;
						String typ = settings.lexicon.edgeType;
						
						info.add(new JLabel(""+Lexicon.capitalize(val)+" "));
						data.add(value);
						info.add(new JLabel(""+Lexicon.capitalize(typ)+" "));
						data.add(type);
						info.add(new JLabel(""+Lexicon.capitalize(lab)+" "));
						data.add(label);

						panel.setPreferredSize(new Dimension(300, 100));

						String title = "Properties for "+
							Lexicon.capitalize(settings.lexicon.edge);
						String result = session.main.grapherDialog(title,panel,"Ok|Cancel");

						if (result.equals("Ok")) {
							try {
								Double.parseDouble(value.getText());
							}
							catch (NumberFormatException ex) {
								return;
							} 
							currentEdge.setValue(value.getText());
							currentEdge.setLabel(label.getText());
							if (type.getSelectedIndex() == options.length-1) {
								currentEdge.setType(null);
							} else {
								currentEdge.setType(eTypes.elementAt(type.getSelectedIndex()));
							}
						}						
						repaint();
						session.setModified(true);						
						return;
					}
					int mousex = (int) (Math.round((e.getX() / scaleFactor) / gridScale) * gridScale);
					int mousey = (int) (Math.round((e.getY() / scaleFactor) / gridScale) * gridScale);
					if (vertexTarget != null) {
						vertexTarget.setStatus(Vertex.STILL);
					}
					vertexTarget = addVertex(mousex, mousey);
					return;
				}

				// BUTTON3 without SHIFT
				if (button == MouseEvent.BUTTON3 && !shiftDown) {
					if (vertexTarget != null && currentEdge != null) {
						session.main.menuOptions.show(true, true, true);
					} else if (vertexTarget != null) {
						session.main.menuOptions.show(true, false, true);
					} else if (currentEdge != null) {
						session.main.menuOptions.show(false, true, true);
					} else {
						session.main.menuOptions.show(false, false, true);
					}
					return;
				}

			}

			public void mousePressed(MouseEvent e) {
				if (vertexTarget != null) {
					vertexTarget.setStatus(Vertex.STILL);
					vertexTarget = null;
				}
				if (currentEdge != null) {
					currentEdge.setStatus(Edge.STILL);
					currentEdge = null;
				}

				for (int i=0 ; i<vertices.size() ; i++) {
					Vertex vertex = (Vertex)vertices.elementAt(i);
					if (vertex.isArea((int)(e.getX()/scaleFactor),(int)(e.getY()/scaleFactor))) {
						if (e.isControlDown()) {
							controled	= true;
							vertexSource	= vertex;
							mousex = (int)(e.getX()/scaleFactor);
							mousey = (int)(e.getY()/scaleFactor);
						}
						else {
							if (vertexTarget!=null) vertexTarget.setStatus(Vertex.STILL);
							vertexTarget = vertex;
							vertexTarget.setStatus(Vertex.FOCUSED);
						}
					}
					else {
						Vector<Edge> connections = vertex.getOuts();
						for (int j=0 ; j<connections.size() ; j++) {
							Edge connection = (Edge)connections.elementAt(j); 
							if (connection.isArea((int)(e.getX()/scaleFactor),(int)(e.getY()/scaleFactor))) {
								if (currentEdge != null) currentEdge.setStatus(Vertex.STILL);
								currentEdge = connection;
								connection.setStatus(Edge.FOCUSED);
								break;
							}
						}						
					}
				}
				repaint();
			}
			public void mouseReleased(MouseEvent e) {
				if (controled) {
					if (vertexTarget!=null) {
						if (settings.allowFirsVertex || vertexTarget.getNumber() > 0) {
							vertexSource.addEdge(vertexTarget);
							vertexSource.setStatus(Vertex.STILL);
							session.setModified(true);
						}
					}
					controled = false;
					repaint();
				}
				vertexSource = null;
			}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
		});
		
		addMouseMotionListener(new MouseMotionListener() { 
			public void mouseDragged(MouseEvent e) {
				mousex = (int)(e.getX()/scaleFactor);
				mousey = (int)(e.getY()/scaleFactor);
				if (!e.isControlDown()) {
					if (!controled && vertexTarget!=null) {
						mousex = (int)(Math.round((double)mousex/gridScale)*gridScale);
						mousey = (int)(Math.round((double)mousey/gridScale)*gridScale);
						vertexTarget.setLocation(mousex,mousey);
						session.setModified(true);
					}
					else if (!controled && currentEdge!=null) {
						currentEdge.setRotation(mousex, mousey);
						session.setModified(true);
					}
				}
				else {
					vertexTarget = null;
					for (int i=0 ; i<vertices.size() ; i++) {
						Vertex vertex = (Vertex)vertices.elementAt(i);
						if (vertex.isArea(mousex,mousey)) {
							vertexTarget = vertex;
							if (vertex != vertexSource) {
								vertex.setStatus(Vertex.FOCUSED);
							}
							repaint();
							return;
						}
						else {
							if (vertex != vertexSource) {
								vertex.setStatus(Vertex.STILL);
							}
							vertexTarget = null;
						}
					}
				}
				repaint();
			}
			
			public void mouseMoved(MouseEvent arg0) {}
		});

		addMouseWheelListener(new MouseWheelListener(){

			public void mouseWheelMoved(MouseWheelEvent e) {
				if (currentEdge != null){
					if (e.isControlDown()){
						currentEdge.setAmountRotation((Math.PI/16)*-e.getWheelRotation());
					}
					else if (!e.isShiftDown()){
						currentEdge.setAmountDistance(-e.getWheelRotation()*2);
					}
					session.setModified(true);
				}
				repaint();
			}
			
		});

	}

	//-------------------------------------------------------------------------------------
	
	public void restart(){
		vertices.removeAllElements();
		repaint();
	}

	//-------------------------------------------------------------------------------------

	public boolean exportView() {
		if (session.main.currentSession == null) {
			session.main.properties.generalView.export.setText(
					"\n"
				);
			return false;
		}

		int size 	= vertices.size();
		int first	= session.main.currentSession.board.settings.firstZero?0:1;

		switch (session.main.currentSession.board.settings.exportType) {
		case 0: // Json
			StringBuilder json = new StringBuilder();
			json.append("{\n");

			json.append("  \"vertices\": [\n");
			for (int i = 0; i < vertices.size(); i++) {
				Vertex s = vertices.elementAt(i);
				json.append("    { \"id\": ").append(i + first)
					.append(", \""+settings.lexicon.vertexType+"\": ").append(s.getType())
					.append(", \""+settings.lexicon.vertexValue+"\": ").append(s.getValue())
					.append(" }");
				if (i < vertices.size() - 1) json.append(",");
				json.append("\n");
			}
			json.append("  ],\n");

			json.append("  \"edges\": [\n");
			int id=0;
			boolean firstEdge = true;
			for (Vertex s : vertices) {
				for (Edge c : s.getOuts()) {
					if (!firstEdge) json.append(",\n");
					json.append("    { \"id\": ").append(id + first)
						.append(", \"source\": ")
						.append(c.getSource().getNumber() + first)
						.append(", \"target\": ")
						.append(c.getTarget().getNumber() + first)
						.append(", \""+settings.lexicon.edgeValue+"\": ")
						.append(c.getValue())
						.append(" }");
					firstEdge = false;
					id++;
				}
			}
			json.append("\n  ]\n");

			json.append("}\n");

			session.main.properties.generalView.export.setText(json.toString());
        return true;
					
		case 1: // Dzn
			String from = "";
			String to	= "";
			int nConections = 0;

			for (Vertex s : vertices) {
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

			String vertexTypes = "";
			String vertexValues = "";
			String vertexLabels = "";
			String edgeTypes = "";
			String edgeValues = "";
			String edgeLabels = "";

			for (Vertex v  : vertices){ 
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

			//----------------------------------------------------------

			String vertex 	= settings.lexicon.vertex;
			String edge		= settings.lexicon.edge;

			String vertexType	= settings.lexicon.vertexType;
			String vertexValue	= settings.lexicon.vertexValue;
			String vertexLabel	= settings.lexicon.vertexLabel;
			String edgeType		= settings.lexicon.edgeType;
			String edgeValue	= settings.lexicon.edgeValue;
			String edgeLabel	= settings.lexicon.edgeLabel;

			session.main.properties.generalView.export.setText(
				(vertex		.charAt(0) <= 'Z'?"":"nvertices = " + size + ";\n") +
				(vertexType	.charAt(0) <= 'Z'?"":vertexType  + " = [" + vertexTypes  + "];\n") +
				(vertexValue.charAt(0) <= 'Z'?"":vertexValue + " = [" + vertexValues + "];\n") +
				(vertexLabel.charAt(0) <= 'Z'?"":vertexLabel + " = [" + vertexLabels + "];\n") +
				(edge       .charAt(0) <= 'Z'?"":"nedges = " + nConections + ";\n") +
				"sources = [" + from + "];\n" +
				"targets = [" + to + "];\n" +
				(edgeType	.charAt(0) <= 'Z'?"":edgeType  + " = [" + edgeTypes  + "];\n") +
				(edgeValue	.charAt(0) <= 'Z'?"":edgeValue + " = [" + edgeValues + "];\n") +
				(edgeLabel	.charAt(0) <= 'Z'?"":edgeLabel + " = [" + edgeLabels + "];\n") +
				""
			);
			return true;

		case 2: { // Adjacency Matrix (MZN)
		
			String matrix = "[";
			for (int v=0;v<size;v++){
				matrix += v==0?"|":" |";
				for (int t=0;t<size;t++){
					boolean found = false;
					for (int i=0;i<vertices.elementAt(v).getOuts().size();i++){
						if (vertices.elementAt(v).getOuts().elementAt(i).getTarget().getNumber()==t) {
							found = true;
							break;
						}
					}
					matrix += found?"1":"0";
					if (t<vertices.size()-1) matrix += ","; 
					else 
						matrix += "";
				}
				if (v<vertices.size()-1) matrix += "\n"; 
				else 
					matrix += "|];\n";
			}
			session.main.properties.generalView.export.setText(
				matrix +
				"\n"
			);
		}
		return true;

		case 3: { // Adjacency Matrix with costs (MZN)
		
			int max = 0;
			for (Vertex v : vertices){
				for (Edge e : v.getOuts()){
					double val = Double.parseDouble(e.getValue());
					if (val > max) {
						max = (int) val;
					}
				}
			}

			String matrix = "[";
			for (int v=0;v<size;v++){
				matrix += v==0?"|":" |";
				for (int t=0;t<size;t++){
					String cost = "0";
					for (int i=0;i<vertices.elementAt(v).getOuts().size();i++){
						if (vertices.elementAt(v).getOuts().elementAt(i).getTarget().getNumber()==t) {
							cost = vertices.elementAt(v).getOuts().elementAt(i).getValue();
							break;
						}
					}
					String s = cost;
					matrix += s;
					if (t<vertices.size()-1) matrix += ","; 
					else 
						matrix += "";
				}
				if (v<vertices.size()-1) matrix += "\n"; 
				else 
					matrix += "|];\n";
			}
			session.main.properties.generalView.export.setText(
				matrix +
				"\n"
			);
		}
		return true;

		default:
			session.main.properties.generalView.export.setText(
				"\n"
			);
			return true;
		}

	}

	//-------------------------------------------------------------------------------------
	public String getFileName(){
		return fileName;
	}
	
	//-------------------------------------------------------------------------------------

	public void deleteVertex(Vertex vertex){
		for (int s=0;s<vertices.size();s++) {
			for (int c=0;c<vertices.elementAt(s).getOuts().size();c++){
				Edge connection = vertices.elementAt(s).getOuts().elementAt(c);
				if (connection.getTarget().equals(vertex)){
					vertices.elementAt(s).getOuts().remove(connection);
				}
			}
		}
		for (int i=vertex.getNumber()+1;i<vertices.size();i++){
			vertices.elementAt(i).setNumber(vertices.elementAt(i).getNumber()-1);
		}
		
		vertices.removeElement(vertex);
	}

	//-------------------------------------------------------------------------------------
	
	public Vertex getVertex(int number){
		if (number>=vertices.size()) return null;
		return vertices.elementAt(number);
	}

	//-------------------------------------------------------------------------------------

	public void keyPressed(KeyEvent e){
		
	}
	
	//-------------------------------------------------------------------------------------

	public int[][] getMatrix(){
		
		int gra[][] = new int[vertices.size()][eTypes.size()];
		
		for (int s=0;s<vertices.size();s++){
			for (int t=0;t<eTypes.size();t++){
				gra[s][t] = 0;
				Type type = eTypes.elementAt(t);
				for (int c=0;c<vertices.elementAt(s).getOuts().size();c++){
					Edge connection = vertices.elementAt(s).getOuts().elementAt(c);
					if (connection.getType().equals(type)){
						gra[s][t] = connection.getTarget().getNumber();
						break;
					}
				}
			}
		}
		
		return gra;
	}
	
	//-------------------------------------------------------------------------------------

	public String[] getVocabulary(){
		String voc[] = new String[eTypes.size()];
		for (int i=0;i<eTypes.size();i++){
			voc[i] = eTypes.elementAt(i).getDescription();
		}
		return voc;
	}
	
	//-------------------------------------------------------------------------------------

	public int[] getAcceptedVertex(){
		int count = 0,i=0;
		for (int s=0;s<vertices.size();s++) if (vertices.elementAt(s).isAccepted()) count++;
		int ace[] = new int[count];
		for (int s=0;s<vertices.size();s++) {
			if (vertices.elementAt(s).isAccepted()) {
				ace[i]= vertices.elementAt(s).getNumber();
				i++;
			}
		}
		return ace;
	}

	//-------------------------------------------------------------------------------------

	public Vertex isDFD(){
		
		for (int t=0;t<vertices.size();t++){
			Vertex target = vertices.elementAt(t);
			String type=null;
			for (int s=0;s<vertices.size();s++){
				Vertex source = vertices.elementAt(s);
				for (int c=0;c<source.getOuts().size();c++){
					if (source.getOuts().elementAt(c).getType()==null) return source;
					if (source.getOuts().elementAt(c).getTarget().equals(target)){
						if (source.getOuts().elementAt(c).getType()==null) continue;
						if (type==null){
							type = source.getOuts().elementAt(c).getType().getName();
						}
						else{
							if (!source.getOuts().elementAt(c).getType().getName().equals(type)){
								return target;
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	//-------------------------------------------------------------------------------------

	public void print(){
		PrinterJob printJob = PrinterJob.getPrinterJob();
		
		printJob.setPrintable(this,pageFormat);
        if (printJob.printDialog()) {
            try {
            	printJob.setJobName("Grapher "+session.getName());
            	printJob.print();  
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
	}

	public int print(Graphics g1, PageFormat pf, int pi) throws PrinterException {

		Graphics2D g = (Graphics2D) g1;

		g.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setRenderingHint(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		if (pi >= 1) return Printable.NO_SUCH_PAGE;
		g.setColor(Color.WHITE);
		g.fillRect(0,0,getWidth(),getHeight());
		if (controled) {
			g.setColor(Color.BLACK);
			if (vertexTarget!=null) {
				g.drawLine(vertexSource.getX(),vertexSource.getY(),vertexTarget.getX(),vertexTarget.getY());
			}
			else {
				g.drawLine(vertexSource.getX(),vertexSource.getY(),mousex,mousey);
			}
		}
		int connectionSequence = settings.firstZero?0:1;
		for (int i=0 ; i<vertices.size() ; i++) {
			vertices.elementAt(i).draw(g,settings,connectionSequence,hidden);
		}
		return 0;
	}
	
}
