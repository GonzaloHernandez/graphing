package main;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.HierarchyEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class Board extends JComponent implements Printable{
	
	//-------------------------------------------------------------------------------------
	
	protected int				mousex,mousey;
	protected boolean			controled;
	protected String			fileName;
	protected GrapherSettings	settings;
	protected Vector<Vertex>	vertices;
	protected GrapherSession	session;
	protected Vector<Type>		eTypes,vTypes;
	protected Edge				currentConnection;
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

		eTypes.add(new Type(0,"Free Value", "0"));
		eTypes.add(new Type(1,"number","0123456789"));
		eTypes.add(new Type(2,"point","."));
		eTypes.add(new Type(3,"uppercase","ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		eTypes.add(new Type(4,"lowercase","abcdefghijklmnopqrstuvwxyz"));

		vTypes.add(new Type(0,"Round", "Odd"));
		vTypes.add(new Type(1,"Square", "Even"));
		vTypes.add(new Type(2,"Polygon", "Nature"));

		settings	= new GrapherSettings(true,true,"",eTypes);
		vertices		= new Vector<Vertex>();
		vertexSource	= null;
		vertexTarget	= null;
		controled	= false;		
		pageFormat	= new PageFormat();
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

		if (showGrid) {
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
		int connectionSequence = settings.firstZero?0:1;
		for (int i=0 ; i<vertices.size() ; i++) {
			connectionSequence = vertices.elementAt(i).draw(g,settings,connectionSequence,hidden);
		}

		export();
		session.main.properties.elementsView.refresh();
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
				if (currentConnection != null) {
					if (!e.isControlDown()){
						if (e.getKeyCode() == KeyEvent.VK_A) {
							currentConnection.setAmountDistance(2);
						}
						else if (e.getKeyCode() == KeyEvent.VK_Z){
							currentConnection.setAmountDistance(-2);
						}
						else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
							Vertex source = currentConnection.getSource();
							source.deleteConnecion(currentConnection);
						}
					}
					else {
						if (e.getKeyCode() == KeyEvent.VK_A) {
							currentConnection.setAmountRotation((Math.PI/16));
						}
						else if (e.getKeyCode() == KeyEvent.VK_Z){
							currentConnection.setAmountRotation(-(Math.PI/16));
						}
					}
					session.setModified(true);
				}

				if (vertexTarget != null) {
					if (e.getKeyCode() == KeyEvent.VK_DELETE) {
						deleteVertex(vertexTarget);
						session.setModified(true);
					}
					// else if (e.getKeyCode() == KeyEvent.VK_A) {
					// 	int value = vertexTarget.getValue();
					// 	vertexTarget.setValue(+1);
					// 	session.setModified(true);
					// } 
					// else if (e.getKeyCode() == KeyEvent.VK_Z) {
					// 	vertexTarget.setValue(vertexTarget.getValue()-1);
					// 	session.setModified(true);
					// }
					else if (e.getKeyCode() == KeyEvent.VK_O) {
						// if (vertexTarget.getType()==2) {
						// 	vertexTarget.setType(0);
						// }
						// else {
						// 	vertexTarget.setType(vertexTarget.getType()+1);
						// }
						// session.setModified(true);
					}
					else  if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') {
						vertexTarget.setValue(""+e.getKeyChar());
						session.setModified(true);
					}
				}

				if (e.isControlDown()){
					if (e.getKeyCode() == KeyEvent.VK_S) {
						save(false);
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
					if (currentConnection != null) {
						currentConnection.setActive(!currentConnection.isActive());
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
					if (currentConnection != null) {
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
						
						info.add(new JLabel(""+settings.dictionary.vertexValue+" "));
						data.add(value);
						info.add(new JLabel(""+settings.dictionary.vertexLabel+" "));
						data.add(label);
						info.add(new JLabel(""+settings.dictionary.vertexType+" "));
						data.add(type);

						panel.setPreferredSize(new Dimension(300, 70));
						int result = JOptionPane.showConfirmDialog(session, panel, 
								"Properties for "+settings.dictionary.vertex, JOptionPane.OK_CANCEL_OPTION);

						if (result == JOptionPane.OK_OPTION) {
							try {
								Double.parseDouble(value.getText());
								vertexTarget.setValue(value.getText());
								vertexTarget.setLabel(label.getText());
								if (type.getSelectedIndex() == options.length-1) {
									vertexTarget.setType(null);
								} else {
									vertexTarget.setType(vTypes.elementAt(type.getSelectedIndex()));
								}
							}
							catch (NumberFormatException ex) {
								return;
							}
						}
						repaint();
						session.setModified(true);						
						return;
					}
					if (currentConnection != null) {

						JTextField value = new JTextField(currentConnection.getValue());
						JTextField label = new JTextField(currentConnection.getLabel());
						String[] options = new String[eTypes.size()+1];
						for (int i=0; i<eTypes.size(); i++) {
							options[i] = eTypes.elementAt(i).getName();							
						}
						options[eTypes.size()] = "<None>";
						JComboBox<String> type = new JComboBox<>(options);
						if (currentConnection.getType() == null) {
							type.setSelectedIndex(eTypes.size());
						} else {
							type.setSelectedIndex(currentConnection.getType().getId());
						}

						JPanel info = new JPanel(new GridLayout(3,1));
						JPanel data = new JPanel(new GridLayout(3,1));
						JPanel panel = new JPanel(new BorderLayout());
						panel.add(info,BorderLayout.WEST);
						panel.add(data,BorderLayout.CENTER);
						
						info.add(new JLabel(""+settings.dictionary.edgeValue+" "));
						data.add(value);
						info.add(new JLabel(""+settings.dictionary.edgeLabel+" "));
						data.add(label);
						info.add(new JLabel(""+settings.dictionary.edgeType+" "));
						data.add(type);

						panel.setPreferredSize(new Dimension(300, 70));
						int result = JOptionPane.showConfirmDialog(session, panel, 
								"Properties for "+settings.dictionary.edge, JOptionPane.OK_CANCEL_OPTION);

						if (result == JOptionPane.OK_OPTION) {
							try {
								Double.parseDouble(value.getText());
								currentConnection.setValue(value.getText());
								currentConnection.setLabel(label.getText());
								if (type.getSelectedIndex() == options.length-1) {
									currentConnection.setType(null);
								} else {
									currentConnection.setType(eTypes.elementAt(type.getSelectedIndex()));
								}
							}
							catch (NumberFormatException ex) {
								return;
							}
						}						
						// Type t = new Type(0,"","");
						// String currentval = currentConnection.getValue();
						// String val = JOptionPane.showInputDialog(session, settings.dictionary.edgeValue,""+currentval);
						// if (val != null) {
						// 	try {
						// 		Double.parseDouble(val);
						// 		currentConnection.setValue(val);
						// 	}
						// 	catch (NumberFormatException ex) {
						// 		return;
						// 	}
						// } else {
						// 	return;
						// }						
						// currentConnection.setType(t);
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
					if (vertexTarget != null && currentConnection != null) {
						session.main.menuOptions.show(true, true, true);
					} else if (vertexTarget != null) {
						session.main.menuOptions.show(true, false, true);
					} else if (currentConnection != null) {
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
				if (currentConnection != null) {
					currentConnection.setStatus(Edge.STILL);
					currentConnection = null;
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
								if (currentConnection != null) currentConnection.setStatus(Vertex.STILL);
								currentConnection = connection;
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
							vertexSource.addConnection(vertexTarget);
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
					else if (!controled && currentConnection!=null) {
						currentConnection.setRotation(mousex, mousey);
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
				if (vertexTarget!=null && e.isShiftDown()) {
					// vertexTarget.setValue(vertexTarget.getValue()-e.getWheelRotation());
				} 
				else if (currentConnection != null){
					if (e.isShiftDown() && currentConnection.getType()!=null && currentConnection.getType().getId() == 0){
						// currentConnection.setValue(currentConnection.getValue()- e.getWheelRotation());
					}
					else if (e.isControlDown()){
						currentConnection.setAmountRotation((Math.PI/16)*-e.getWheelRotation());
					}
					else if (!e.isShiftDown()){
						currentConnection.setAmountDistance(-e.getWheelRotation()*2);
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

	public void load(){
		if (session.isModified()) {
			String messageReturn = session.main.messageBox(
						"This session was no saved.|Do you want to close anyway?",
						"Warning","Yes|No");
			if (messageReturn.equals("No")||messageReturn.equals("")) return;
		}
		FileDialog dialog = new FileDialog(session.main,"Select a file",FileDialog.LOAD);
        
		dialog.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(java.io.File dir, String name) {
                return name.toLowerCase().endsWith(".aut");
            }
        });

		dialog.setDirectory(session.main.curdir);
		dialog.setFile("*.aut");
		dialog.setVisible(true);
		session.main.requestFocus();		
		if (dialog.getFile()==null) return;
		session.main.curdir = dialog.getDirectory();

		load(session.main.curdir+dialog.getFile());
	}

	//-------------------------------------------------------------------------------------

	public boolean load(String fileName){
		try {
	        RandomAccessFile file = new RandomAccessFile(new File(fileName), "r");
	        this.fileName = fileName;
	        session.setName(fileName);

	        short	n,number,x,y,numberSource,numberTarget,numberType,distance;
	        double	rotation;
	        boolean	accepted,active;
	        Vertex	source=null,target=null;
	        // Type	type=null;
	        String	name,symbols,value;
	        
	        vertices.removeAllElements();
	        eTypes.removeAllElements();
	        
	        if (	file.readShort()	!= 7 ||
	        		file.readShort()	!= 4 ||
	        		!file.readUTF().equals("GRAPHER")	) {
	        	file.close();
	        	session.dispose();
	        	session.main.messageBox("Invalid file","Warning","Accept");
	        	return false;
	        }
	        
	        int family	= file.readShort();
	        int version	= file.readShort();
	        
	        n = file.readShort();
	        for (int i=0;i<n;i++){
	        	number	= file.readShort();
	        	name 	= file.readUTF();
	        	symbols	= file.readUTF();
	        	eTypes.add(new Type(number,name,symbols));
	        }
	        
	        n = file.readShort();
	        for (int i=0;i<n;i++){
	        	number		= file.readShort();
	        	x 			= file.readShort();
	        	y			= file.readShort();
	        	accepted	= file.readBoolean();

	        	value		= file.readUTF();
				short type	= file.readShort();

				if (family==1 && version<=1)
					active	= true;
				else
					active	= file.readBoolean();

				Vertex s = new Vertex(number,x,y,Vertex.STILL,accepted,
					value,vTypes.elementAt(type),"");
				s.setActive(active);

				vertices.add(s);
	        }
	        
	        n = file.readShort();
	        for (int i=0;i<n;i++){
	        	numberSource	= file.readShort();
	        	numberTarget	= file.readShort();
	        	numberType		= file.readShort();
	        	distance		= file.readShort();
	        	rotation		= file.readDouble();
				value			= file.readUTF();
				if (family==1 && version<=1)
					active	= true;
				else
					active	= file.readBoolean();
	        	
	        	for (int s=0;s<vertices.size();s++){
	        		if (vertices.elementAt(s).getNumber()==numberSource){
	        			source = vertices.elementAt(s);
	        			break;
	        		}
	        	}
	        	for (int t=0;t<vertices.size();t++){
	        		if (vertices.elementAt(t).getNumber()==numberTarget){
	        			target = vertices.elementAt(t);
	        			break;
	        		}
	        	}
	        	
	        	if (numberType>=0) {
					Type type = null;
		        	for (int p=0;p<eTypes.size();p++){
		        		if (eTypes.elementAt(p).getId()==numberType) {
		        			type = eTypes.elementAt(p);
		        			break;
		        		}
		        	}
					Edge c = new Edge(source,target,Edge.STILL,distance,rotation,type,value,"");
					c.setActive(active);
					source.addConnection(c);
	        	}
	        	else {
					Edge c = new Edge(source,target,Edge.STILL,distance,rotation,null,value,"");
					c.setActive(active);
					source.addConnection(c);
	        	}
	        }
	        
	        settings.showTypeNames			= file.readBoolean();
	        settings.showVertexSequence		= file.readBoolean();
	        settings.showConnectionSequence	= file.readBoolean();
	        settings.showVertexPriorities	= file.readBoolean();
	        settings.allowFirsVertex			= file.readBoolean();
	        settings.firstZero				= file.readBoolean();

			settings.comment				= file.readUTF();
	        
			Dimension d = new Dimension(file.readShort(),file.readShort());
	        session.setSize(d);
			setPreferredSize(d);

			settings.exportAuto				= file.readBoolean();
			settings.exportType				= file.readShort();
			settings.gridScale				= file.readShort();

			Dictionary dict = settings.dictionary;
			dict.graph			= file.readUTF();
			dict._graph			= file.readUTF();
			dict.vertex			= file.readUTF();
			dict._vertex		= file.readUTF();
			dict.vertexType		= file.readUTF();
			dict._vertexType	= file.readUTF();
			dict.vertexValue	= file.readUTF();
			dict._vertexValue	= file.readUTF();
			dict.edge			= file.readUTF();
			dict._edge			= file.readUTF();
			dict.edgeValue		= file.readUTF();
			dict._edgeValue		= file.readUTF();

	        file.close(); 
	        repaint();
	        session.main.properties.refresh();
	        session.setModified(false);
	        
	        session.main.addRecentSession(fileName);

			session.setTitle(fileName.substring(session.main.curdir.length()));
			return true;

	    } catch (IOException e) {
	    	session.main.messageBox("The file ["+fileName+"] does not exists","File error","Accept");
	    	return false;
	    }
	}
	
	//-------------------------------------------------------------------------------------

	public void loadImport(){
		if (session.isModified()) {
			String messageReturn = session.main.messageBox("This session was no saved.|Do you want to close anyway?","Warning","Yes|No");
			if (messageReturn.equals("No")||messageReturn.equals("")) return;
		}
		FileDialog dialog = new FileDialog(session.main,"Select a file",FileDialog.LOAD);
        
		dialog.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(java.io.File dir, String name) {
                return name.toLowerCase().endsWith(".gm");
            }
        });

		dialog.setDirectory(session.main.curdir);
		dialog.setFile("*.gm");
		dialog.setVisible(true);
		session.main.requestFocus();		
		if (dialog.getFile()==null) return;

		loadImport(dialog.getDirectory()+dialog.getFile());
	}

	//-------------------------------------------------------------------------------------

	public boolean loadImport(String fileName){
		try {
	        RandomAccessFile file = new RandomAccessFile(new File(fileName), "r");
	        // this.fileName = fileName;
	        // session.setName(fileName);
	        
	        vertices.removeAllElements();
	        eTypes.removeAllElements();
	        
			int nvertices;
			int x=40,y=40;
			int index = 0;

			String line;
			while ((line = file.readLine()) != null) {
				line = line.trim();

				if (line.isEmpty()) continue;

				// Parse header
				if (line.startsWith("parity")) {
					String[] parts = line.split("\\s+|;");
					nvertices = Integer.parseInt(parts[1])+1;
					for(int v=0; v<nvertices; v++) {
						Vertex s = new Vertex(v,x,y,Vertex.STILL,false,"0",null,"");
						if (x>=250) {
							x = 40;
							y += 80;
						}
						else {
							x += 80;
						}
						vertices.add(s);
					}
					continue;
				}

				String[] parts = line.split("\\s+");
                int id = Integer.parseInt(parts[0]);
				if (id!=index) {
					session.main.messageBox("Indices of states no consecutives","Importation error","Accept");
	    			return false;
				}
				index++;
                String value = parts[1];
                int owner = Integer.parseInt(parts[2]);
				Vertex source = session.board.vertices.elementAt(id);
				source.setType(vTypes.elementAt(owner));
				source.setValue(value);
                String successorsStr = parts[3].replace(";", "");
                // List<Integer> successors = new ArrayList<>();
                for (String tar : successorsStr.split(",")) {
					Vertex target = session.board.vertices.elementAt(Integer.parseInt(tar));
                    source.addConnection(new Edge(source, target));
					source.arrangeConnections(target);
                }
	        }
			file.close(); 
	        
	        repaint();
	        session.main.properties.refresh();
	        session.setModified(false);
	        
	        // session.main.addRecentSession(fileName);

			// session.setTitle(fileName.substring(session.main.curdir.length()));
			return true;

	    } catch (IOException e) {
	    	session.main.messageBox("The file ["+fileName+"] does not exists","File error","Accept");
	    	return false;
	    }
	}
	
	//-------------------------------------------------------------------------------------
	
	public boolean save(boolean saveAs) {
		try {
			if (saveAs || fileName.equals("")) {
				FileDialog dialog = new FileDialog(session.main,"Select a file name",FileDialog.SAVE);

				dialog.setFilenameFilter(new FilenameFilter() {
					@Override
					public boolean accept(java.io.File dir, String name) {
						return name.endsWith(".aut");
					}
				});
				dialog.setDirectory(session.main.curdir);
				dialog.setFile("*.aut");

				dialog.pack();
				int x = session.main.getX() + 100;
				int y = session.main.getY() + 100;
				dialog.setLocation(x, y);

				dialog.setVisible(true);
				session.main.requestFocus();
				if (dialog.getFile()==null) return false;
				session.main.curdir = dialog.getDirectory();
				fileName = session.main.curdir+dialog.getFile();
			}
			
			JInternalFrame iframes[] =  session.main.desktop.getAllFrames();
			for (int i=0;i<iframes.length;i++){
				if (iframes[i].getClass().getName().equals("GrapherSession")){
					GrapherSession	session = (GrapherSession)iframes[i];
					if (session.equals(this.session)) continue;
					if (session.getName().equals(fileName)){
						session.main.messageBox("Name invalid.|There exists a session opened with the same indentifier","Warning","Accept");
						fileName = "";
						return false;
					}
				}
			}
			
			RandomAccessFile file = new RandomAccessFile(new File(fileName), "rw");
	        
	        session.setName(fileName);
	        
	        file.writeShort(7);
	        file.writeShort(4);
	        file.writeUTF("GRAPHER");
	        file.writeShort(session.main.family);
	        file.writeShort(session.main.version);
	        
	        short	connectionsCount = 0;
	        
	        file.writeShort(eTypes.size());
	        for (int i=0;i<eTypes.size();i++){
	        	file.writeShort(eTypes.elementAt(i).getId());
	        	file.writeUTF(eTypes.elementAt(i).getName());
	        	file.writeUTF(eTypes.elementAt(i).getDescription());
	        }
	        
	        file.writeShort(vertices.size());
	        for (int i=0;i<vertices.size();i++){
	        	file.writeShort(vertices.elementAt(i).getNumber());
	        	file.writeShort(vertices.elementAt(i).getX());
	        	file.writeShort(vertices.elementAt(i).getY());
	        	file.writeBoolean(vertices.elementAt(i).isAccepted());

	        	file.writeUTF(vertices.elementAt(i).getValue());
	        	file.writeShort(vertices.elementAt(i).getType().getId());
	        	file.writeBoolean(vertices.elementAt(i).isActive());

				connectionsCount += vertices.elementAt(i).getOuts().size();
	        }
	        
	        file.writeShort(connectionsCount);
	        for (int s=0;s<vertices.size();s++){
	        	for (int i=0;i<vertices.elementAt(s).getOuts().size();i++){
		        	file.writeShort(vertices.elementAt(s).getOuts().elementAt(i).getSource().getNumber());
		        	file.writeShort(vertices.elementAt(s).getOuts().elementAt(i).getTarget().getNumber());
		        	if (vertices.elementAt(s).getOuts().elementAt(i).getType()!=null) {
		        		file.writeShort(vertices.elementAt(s).getOuts().elementAt(i).getType().getId());
		        	}
		        	else {
		        		file.writeShort(-1);
		        	}
		        	file.writeShort (vertices.elementAt(s).getOuts().elementAt(i).getDistance());
		        	file.writeDouble(vertices.elementAt(s).getOuts().elementAt(i).getRotation());
		        	file.writeUTF (vertices.elementAt(s).getOuts().elementAt(i).getValue());
					file.writeBoolean(vertices.elementAt(s).getOuts().elementAt(i).isActive());
		        }
	        }
	        
	        file.writeBoolean(settings.showTypeNames);
	        file.writeBoolean(settings.showVertexSequence);
	        file.writeBoolean(settings.showConnectionSequence);
			file.writeBoolean(settings.showVertexPriorities);
			file.writeBoolean(settings.allowFirsVertex);
			file.writeBoolean(settings.firstZero);

			file.writeUTF(settings.comment);	        

	        file.writeShort(session.getWidth());
	        file.writeShort(session.getHeight());

			file.writeBoolean(settings.exportAuto);
			file.writeShort(settings.exportType);
			file.writeShort(settings.gridScale);

			Dictionary dict = settings.dictionary;
			
			file.writeUTF(dict.graph);
			file.writeUTF(dict._graph);
			file.writeUTF(dict.vertex);
			file.writeUTF(dict._vertex);
			file.writeUTF(dict.vertexType);
			file.writeUTF(dict._vertexType);
			file.writeUTF(dict.vertexValue);
			file.writeUTF(dict._vertexValue);
			file.writeUTF(dict.edge);
			file.writeUTF(dict._edge);
			file.writeUTF(dict.edgeValue);
			file.writeUTF(dict._edgeValue);

	        file.setLength(file.getFilePointer());
	        file.close();

	        session.setModified(false);

			//----------------------------------------------------------

			if (settings.exportAuto) {
		
				String fileContent = session.main.properties.generalView.export.getText();
				String extension = "";
				switch (settings.exportType) {
					case  0: extension = ".json";	break;
					case  1: extension = ".dzn";	break;
					default: extension = ".txt";	break;
				}

				String dznFileName = fileName.substring(0, fileName.length()-4)+extension;
	
				RandomAccessFile dznFile = new RandomAccessFile(new File(dznFileName), "rw");
				dznFile.setLength(0);
				dznFile.writeBytes(fileContent);
				dznFile.close();
			}

			session.setTitle(fileName.substring(session.main.curdir.length()));
			
			//----------------------------------------------------------

	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
		
	    return true;
	}

	//-------------------------------------------------------------------------------------

	public boolean export() {
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
					.append(", \""+settings.dictionary.vertexType+"\": ").append(s.getType())
					.append(", \""+settings.dictionary.vertexValue+"\": ").append(s.getValue())
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
						.append(", \""+settings.dictionary.edgeValue+"\": ")
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
			String edgeValues = "";

			for (int v=0;v<vertices.size();v++){
				if (v>0){
					vertexValues += ",";
					vertexTypes += ",";
				}
				vertexValues += vertices.elementAt(v).getValue();
				vertexTypes += vertices.elementAt(v).getType();
				for (Edge e : vertices.elementAt(v).getOuts()) {
					if (edgeValues.length()>0)  edgeValues += ",";
					edgeValues += e.getValue();
				}
			}

			//----------------------------------------------------------

			session.main.properties.generalView.export.setText(
				"nvertices = " + size + ";\n" +
				settings.dictionary.vertexType + " = [" + vertexTypes + "];\n" +
				settings.dictionary.vertexValue + " = [" + vertexValues + "];\n" +
				"nedges = " + nConections + ";\n" +
				"sources = [" + from + "];\n" +
				"targets = [" + to + "];\n" +
				settings.dictionary.edgeValue + " = [" + edgeValues + "];\n"
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
			int digits = String.valueOf(Math.abs(max)).length();

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

	public void screenshot(Graphics g1) {
        int width	= (int)(getWidth() * Math.max(scaleFactor,2));
        int height	= (int)(getHeight() * Math.max(scaleFactor,2));

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2 = image.createGraphics();
		g2.scale(Math.max(scaleFactor,2),Math.max(scaleFactor,2));
        paint(g2);
        g2.dispose();

        try {
			FileDialog dialog = new FileDialog(session.main,"Select a file name",FileDialog.SAVE);

			dialog.setFilenameFilter(new FilenameFilter() {
				@Override
				public boolean accept(java.io.File dir, String name) {
					return name.endsWith(".png");
				}
			});

			dialog.setDirectory(session.main.curdir);
			dialog.setFile("*.png");
			dialog.setVisible(true);
			session.main.requestFocus();
			if (dialog.getFile()==null) return;
			String pngFileName = dialog.getDirectory()+dialog.getFile();


			ImageIO.write(image, "png", new File(pngFileName));
        } catch (Exception e) {
            e.printStackTrace();
        }		
	}

	
}
