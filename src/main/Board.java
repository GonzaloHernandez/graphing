package main;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;

public class Board extends JComponent implements Printable{
	
	//-------------------------------------------------------------------------------------
	
	protected	int				mousex,mousey;
	protected	boolean			controled;
	protected	String			fileName;
	protected	GrapherSettings	settings;
	protected	Vector			<State>states;
	protected	GrapherSession	session;
	protected	Vector			<ConnectionType>types;
	protected	Connection		currentConnection;
	protected	State			stateSource,stateTarget;
	protected	boolean			menuBlock;
	protected	Compiler		compiler;
	protected	PageFormat		pageFormat;
	protected	double			scaleFactor;
	protected	boolean			hidden;
		
	//-------------------------------------------------------------------------------------
	
	public Board(GrapherSession session) {
		this.session		= session;
		this.menuBlock		= false;
		this.compiler		= null;
		this.scaleFactor	= 1.0;
		this.hidden			= false;
		getInputMap().put(KeyStroke.getKeyStroke("A"), "actionName");
		initElements();
		progListeners();
	}

	//-------------------------------------------------------------------------------------

	private void initElements() {
		this.fileName	= "";
		this.compiler	= null;
		
		types	= new Vector<ConnectionType>();

		types.add(new ConnectionType(0,"Free Value", "0"));
		types.add(new ConnectionType(1,"number","0123456789"));
		types.add(new ConnectionType(2,"point","."));
		types.add(new ConnectionType(3,"uppercase","ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		types.add(new ConnectionType(4,"lowercase","abcdefghijklmnopqrstuvwxyz"));
		
		settings	= new GrapherSettings(true,true,"",types);
		states		= new Vector<State>();
		stateSource	= null;
		stateTarget	= null;
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
		g.fillRect(0,0,getWidth(),getHeight());
		if (controled) {
			g.setColor(Color.BLACK);
			if (stateTarget!=null) {
				g.drawLine(stateSource.getX(),stateSource.getY(),stateTarget.getX(),stateTarget.getY());
			}
			else {
				g.drawLine(stateSource.getX(),stateSource.getY(),mousex,mousey);
			}
		}
		int connectionSequence = settings.firstZero?0:1;
		for (int i=0 ; i<states.size() ; i++) {
			connectionSequence = states.elementAt(i).draw(g,settings,connectionSequence,hidden);
		}

		export();
		session.main.properties.elementsView.refresh();
	}
	
	//-------------------------------------------------------------------------------------
	
	public State addState(int x,int y){
		State n = new State(states.size(),x,y);
		states.add(n);
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
					}
					else {
						if (e.getKeyCode() == KeyEvent.VK_A) {
							currentConnection.setAmountRotation(-(Math.PI/16));
						}
						else if (e.getKeyCode() == KeyEvent.VK_Z){
							currentConnection.setAmountRotation((Math.PI/16));
						}
						else if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_X ) {
							State source = currentConnection.getSource();
							source.deleteConnecion(currentConnection);
						}
					}
					session.setModified(true);
				}

				if (stateTarget != null) {
					if (e.isControlDown()) {
						if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_X ) {
							deleteState(stateTarget);
							session.setModified(true);
						}
					}
					else if (e.getKeyCode() == KeyEvent.VK_A) {
						stateTarget.setValue(stateTarget.getValue()+1);
						session.setModified(true);
					} 
					else if (e.getKeyCode() == KeyEvent.VK_Z) {
						stateTarget.setValue(stateTarget.getValue()-1);
						session.setModified(true);
					}
					else if (e.getKeyCode() == KeyEvent.VK_O) {
						stateTarget.setOwner(1-stateTarget.getOwner());
						session.setModified(true);
					}
					else  if (e.getKeyChar() >= '0' && e.getKeyChar() <= '9') {
						stateTarget.setValue(e.getKeyChar()-'0');
						session.setModified(true);
					}
				}

				if (e.isControlDown()){
					if (e.getKeyCode() == KeyEvent.VK_S) {
						save(false);
					}
					else if (e.getKeyChar() == '+' || e.getKeyChar() == '=') {
						scaleFactor += 0.1;
					}
					else if (e.getKeyChar() == '-') {
						scaleFactor -= 0.1;
					}
				}

				if (e.getKeyChar() == 'h') {
					hidden = true;
				}

				repaint();
			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyChar() == 'h') {
					hidden = false;
					repaint();
				}
			}

			public void keyTyped(KeyEvent e) {

			}
			
		});
		addMouseListener(new MouseListener() {
			// public void mouseClicked(MouseEvent e) {
				
			// 	if (e.getModifiersEx()== InputEvent.SHIFT_DOWN_MASK) {
			// 		if (e.getButton() == MouseEvent.BUTTON1){
			// 			if (stateTarget!=null) {
			// 				stateTarget.setActive(!stateTarget.isActive());
			// 				session.setModified(true);
			// 				stateTarget.setStatus(State.STILL);
			// 			}
			// 			if (currentConnection!=null) {
			// 				currentConnection.setActive(!currentConnection.isActive());
			// 				session.setModified(true);
			// 			}
			// 		}
			// 		else if (e.getButton() == MouseEvent.BUTTON3) {
			// 			if (currentConnection!=null) {
			// 				session.main.menuOptions.showTypes(false);
			// 			}
			// 		}
			// 		repaint();	
			// 	}
			// 	if (e.getButton() == MouseEvent.BUTTON1) {
			// 		if (!e.isControlDown()) {
			// 			if (e.getButton()==MouseEvent.BUTTON1 && e.getClickCount() == 2) {
			// 				int mousex = (int)(Math.round((int)(e.getX()/scaleFactor)/10)*10);
			// 				int mousey = (int)(Math.round((int)(e.getY()/scaleFactor)/10)*10);
			// 				addState(mousex,mousey);
			// 			}
			// 		}

			// 	}
			// 	else if (e.getButton() == MouseEvent.BUTTON3  && !e.isShiftDown()) {
			// 		if (stateTarget!=null && currentConnection!=null) {
			// 			session.main.menuOptions.show(true,true,true);
			// 		}
			// 		else if (stateTarget!=null) {
			// 			session.main.menuOptions.show(true,false,true);
			// 		}
			// 		else if (currentConnection!=null) {
			// 			session.main.menuOptions.show(false,true,true);				
			// 		} 
			// 		else {
			// 			session.main.menuOptions.show(false,false,true);
			// 		}
			// 	}
			// }

			public void mouseClicked(MouseEvent e) {
				int modifiers = e.getModifiersEx();
				int button = e.getButton();

				boolean shiftDown = (modifiers & InputEvent.SHIFT_DOWN_MASK) != 0;
				boolean ctrlDown = (modifiers & InputEvent.CTRL_DOWN_MASK) != 0;
				
				// SHIFT + BUTTON1
				if (shiftDown && button == MouseEvent.BUTTON1) {
					if (stateTarget != null) {
						stateTarget.setActive(!stateTarget.isActive());
						session.setModified(true);
						stateTarget.setStatus(State.STILL);
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
					if (stateTarget != null) {
						stateTarget.setActive(!stateTarget.isActive(), true);
						session.setModified(true);
						stateTarget.setStatus(State.STILL);
					}
					if (currentConnection != null) {
						session.main.menuOptions.showTypes(false);
					}
					repaint();
					return;
				}

				// BUTTON1 + Double-click (No CTRL)
				if (button == MouseEvent.BUTTON1 && e.getClickCount() == 2 && !ctrlDown) {
					int mousex = (int) (Math.round((int) (e.getX() / scaleFactor) / 10) * 10);
					int mousey = (int) (Math.round((int) (e.getY() / scaleFactor) / 10) * 10);
					addState(mousex, mousey);
					return;
				}

				// BUTTON3 without SHIFT
				if (button == MouseEvent.BUTTON3 && !shiftDown) {
					if (stateTarget != null && currentConnection != null) {
						session.main.menuOptions.show(true, true, true);
					} else if (stateTarget != null) {
						session.main.menuOptions.show(true, false, true);
					} else if (currentConnection != null) {
						session.main.menuOptions.show(false, true, true);
					} else {
						session.main.menuOptions.show(false, false, true);
					}
				}
			}


			public void mousePressed(MouseEvent e) {
				for (int i=0 ; i<states.size() ; i++) {
					State state = (State)states.elementAt(i);
				
					if (state.isArea((int)(e.getX()/scaleFactor),(int)(e.getY()/scaleFactor))) {
						
						if (!e.isControlDown()) {
							stateTarget = state;
							stateTarget.setMouseDiference((int)(e.getX()/scaleFactor),(int)(e.getY()/scaleFactor));
						}
						else {
							controled	= true;
							stateSource	= state;
							stateSource.setStatus(State.MARKED);
						}
						break;						
					}
					repaint();
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (controled) {
					if (stateTarget==null) {
						controled = false;
						return;
					}
					if (settings.allowFirsState || stateTarget.getNumber() > 0) {
						for (int i=0 ; i<states.size() ; i++) {
							State state = (State)states.elementAt(i);
							if (state.isArea((int)(e.getX()/scaleFactor),(int)(e.getY()/scaleFactor))) {
								stateSource.addConnection(stateTarget);
								session.setModified(true);
								repaint();
							}
						}
					}
					controled = false;
				}
				stateSource = null;
			}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
		});
		
		addMouseMotionListener(new MouseMotionListener() { 
			public void mouseDragged(MouseEvent e) {
				mousex = (int)(e.getX()/scaleFactor);
				mousey = (int)(e.getY()/scaleFactor);
				if (!e.isControlDown()) {
					if (!controled && stateTarget!=null) {
						mousex = (int)(Math.round(mousex/10)*10);
						mousey = (int)(Math.round(mousey/10)*10);
						stateTarget.setLocation(mousex,mousey);
						session.setModified(true);
					}
				}
				else {
					stateTarget = null;
					for (int i=0 ; i<states.size() ; i++) {
						State state = (State)states.elementAt(i);
						if (state.isArea(mousex,mousey)) {
							stateTarget = state;
							if (state != stateSource) {
								state.setStatus(State.FOCUSED);
							}
						}
						else if (state != stateSource) {
							state.setStatus(State.STILL);
						}
					}
				}
				repaint();
			}
			
			public void mouseMoved(MouseEvent e) {
				if (menuBlock) return;
				if (!e.isControlDown()) stateSource = null;
				
				stateTarget = null;
				currentConnection = null;
				for (int i=0 ; i<states.size() ; i++) {
					State state = (State)states.elementAt(i);
					if (state.isArea((int)(e.getX()/scaleFactor),(int)(e.getY()/scaleFactor))) {
						stateTarget	= state;
						if (state != stateSource) {
							state.setStatus(State.FOCUSED);
						}
					}
					else if (state != stateSource) {
						state.setStatus(State.STILL);
					}
					
					Vector<Connection> connections = state.getConnections();
					for (int j=0 ; j<connections.size() ; j++) {
						Connection connection = (Connection)connections.elementAt(j); 
						if (connection.isArea((int)(e.getX()/scaleFactor),(int)(e.getY()/scaleFactor))) {
							currentConnection = connection;
							connection.setStatus(Connection.FOCUSED);
						}
						else {
							connection.setStatus(Connection.STILL);
						}
					}
				}
				repaint();
			}
		});

		addMouseWheelListener(new MouseWheelListener(){

			public void mouseWheelMoved(MouseWheelEvent e) {
				if (stateTarget!=null && e.isShiftDown()) {
					stateTarget.setValue(stateTarget.getValue()-e.getWheelRotation());
				} 
				else if (currentConnection != null){
					if (e.isShiftDown() && currentConnection.getType()!=null && currentConnection.getType().getNumber() == 0){
						currentConnection.setValue(currentConnection.getValue()- e.getWheelRotation());
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
		states.removeAllElements();
		repaint();
	}

	//-------------------------------------------------------------------------------------

	public void load(){
		if (session.isModified()) {
			String messageReturn = session.main.messageBox("This session was no saved.|Do you want to close anyway?","Warning","Yes|No");
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

	        short	n,number,x,y, value,owner, numberSource,numberTarget,numberType,distance;
	        double	rotation;
	        boolean	accepted,active;
	        State	source=null,target=null;
	        ConnectionType	type=null;
	        String	name,symbols;
	        
	        states.removeAllElements();
	        types.removeAllElements();
	        
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
	        	types.add(new ConnectionType(number,name,symbols));
	        }
	        
	        n = file.readShort();
	        for (int i=0;i<n;i++){
	        	number		= file.readShort();
	        	x 			= file.readShort();
	        	y			= file.readShort();
	        	accepted	= file.readBoolean();

	        	value		= file.readShort();
				owner		= file.readShort();

				if (family==1 && version<=1)
					active	= true;
				else
					active	= file.readBoolean();

				State s = new State(number,x,y,State.STILL,accepted,value,owner);
				s.setActive(active);

				states.add(s);
	        }
	        
	        n = file.readShort();
	        for (int i=0;i<n;i++){
	        	numberSource	= file.readShort();
	        	numberTarget	= file.readShort();
	        	numberType		= file.readShort();
	        	distance		= file.readShort();
	        	rotation		= file.readDouble();
				value			= file.readShort();
				if (family==1 && version<=1)
					active	= true;
				else
					active	= file.readBoolean();
	        	
	        	for (int s=0;s<states.size();s++){
	        		if (states.elementAt(s).getNumber()==numberSource){
	        			source = states.elementAt(s);
	        			break;
	        		}
	        	}
	        	for (int t=0;t<states.size();t++){
	        		if (states.elementAt(t).getNumber()==numberTarget){
	        			target = states.elementAt(t);
	        			break;
	        		}
	        	}
	        	
	        	if (numberType>=0) {
		        	for (int p=0;p<types.size();p++){
		        		if (types.elementAt(p).getNumber()==numberType) {
		        			type = types.elementAt(p);
		        			break;
		        		}
		        	}
					Connection c = new Connection(source,target,type,Connection.STILL,distance,rotation,value);
					c.setActive(active);
					source.addConnection(c);
	        	}
	        	else {
					Connection c = new Connection(source,target,null,Connection.STILL,distance,rotation,value);
					c.setActive(active);
					source.addConnection(c);
	        	}
	        }
	        
	        settings.showTypeNames			= file.readBoolean();
	        settings.showStateSequence		= file.readBoolean();
	        settings.showConnectionSequence	= file.readBoolean();
	        settings.showStatePriorities	= file.readBoolean();
	        settings.allowFirsState			= file.readBoolean();
	        settings.firstZero				= file.readBoolean();

			settings.comment				= file.readUTF();
	        
	        session.setSize(file.readShort(),file.readShort());

			settings.saveDzn				= file.readBoolean();
	        
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
				dialog.setVisible(true);
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
	        
	        file.writeShort(types.size());
	        for (int i=0;i<types.size();i++){
	        	file.writeShort(types.elementAt(i).getNumber());
	        	file.writeUTF(types.elementAt(i).getName());
	        	file.writeUTF(types.elementAt(i).getSymbols());
	        }
	        
	        file.writeShort(states.size());
	        for (int i=0;i<states.size();i++){
	        	file.writeShort(states.elementAt(i).getNumber());
	        	file.writeShort(states.elementAt(i).getX());
	        	file.writeShort(states.elementAt(i).getY());
	        	file.writeBoolean(states.elementAt(i).isAccepted());

	        	file.writeShort(states.elementAt(i).getValue());
	        	file.writeShort(states.elementAt(i).getOwner());
	        	file.writeBoolean(states.elementAt(i).isActive());

				connectionsCount += states.elementAt(i).getConnections().size();
	        }
	        
	        file.writeShort(connectionsCount);
	        for (int s=0;s<states.size();s++){
	        	for (int i=0;i<states.elementAt(s).getConnections().size();i++){
		        	file.writeShort(states.elementAt(s).getConnections().elementAt(i).getSource().getNumber());
		        	file.writeShort(states.elementAt(s).getConnections().elementAt(i).getTarget().getNumber());
		        	if (states.elementAt(s).getConnections().elementAt(i).getType()!=null) {
		        		file.writeShort(states.elementAt(s).getConnections().elementAt(i).getType().getNumber());
		        	}
		        	else {
		        		file.writeShort(-1);
		        	}
		        	file.writeShort (states.elementAt(s).getConnections().elementAt(i).getDistance());
		        	file.writeDouble(states.elementAt(s).getConnections().elementAt(i).getRotation());
		        	file.writeShort (states.elementAt(s).getConnections().elementAt(i).getValue());
					file.writeBoolean(states.elementAt(s).getConnections().elementAt(i).isActive());
		        }
	        }
	        
	        file.writeBoolean(settings.showTypeNames);
	        file.writeBoolean(settings.showStateSequence);
	        file.writeBoolean(settings.showConnectionSequence);
			file.writeBoolean(settings.showStatePriorities);
			file.writeBoolean(settings.allowFirsState);
			file.writeBoolean(settings.firstZero);

			file.writeUTF(settings.comment);	        

	        file.writeShort(session.getWidth());
	        file.writeShort(session.getHeight());

			file.writeBoolean(settings.saveDzn);

	        file.setLength(file.getFilePointer());
	        file.close();
	        session.setModified(false);

			//----------------------------------------------------------

			if (settings.saveDzn) {
				int size = states.size();
			
				String values = "";
				String owners = "";
	
				for (int i=0;i<states.size();i++){
					values += states.elementAt(i).getValue() + ",";
					owners += states.elementAt(i).getOwner() + ",";
				}
	
				if (values.length()>0) {
					values = values.substring(0, values.length()-1);
					owners = owners.substring(0, owners.length()-1);	
				}
	
	
				String sources = "";
				String targets = "";
				int nConections = 0;
				
				for (State s : states) {
					for (Connection c : s.getConnections()) {
						sources	+= c.getSource().getNumber()+1 + ",";
						targets	+= c.getTarget().getNumber()+1 + ",";
						nConections ++;
					}
				}
	
				if (sources.length()>0) {
					sources	= sources.substring(0, sources.length()-1);
					targets	= targets.substring(0, targets.length()-1);	
				}
		
				String dzn = 	"nvertices = " + size + ";\n" +
								"owners    = [" + owners + "];\n" +
								"colors    = [" + values + "];\n" +
								"nedges    = " + nConections + ";\n" +
								"sources   = [" + sources + "];\n" +
								"targets   = [" + targets + "];\n";
	
				String dznFileName = fileName.substring(0, fileName.length()-4)+".dzn";
	
				RandomAccessFile dznFile = new RandomAccessFile(new File(dznFileName), "rw");
				dznFile.setLength(0);
				dznFile.writeBytes(dzn);
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
		if (session.main.currentSession == null) return false;

		int v		= session.main.currentSession.board.settings.programmingView;
		int first	= session.main.currentSession.board.settings.firstZero?0:1;

		//----------------------------------------------------------

		int size = states.size();

		// String matrix = "[";
		// for (int s=0;s<states.size();s++){
		// 	matrix += v==1?"[":"|";
		// 	for (int t=0;t<states.size();t++){
		// 		boolean found = false;
		// 		for (int i=0;i<states.elementAt(s).getConnections().size();i++){
		// 			if (states.elementAt(s).getConnections().elementAt(i).getTarget().getNumber()==t) {
		// 				found = true;
		// 			}
		//         }
		// 		matrix += found?s:"0";
		// 		if (t<states.size()-1) matrix += ","; 
		// 		else 
		// 			matrix += v==1?"]":"";
	    //     }
		// 	if (s<states.size()-1) matrix += "\n"; 
		// 	else 
		// 		matrix += v==1?"]\n":"|]\n";
		// }

		String labels = "{";
		for (int i=1; i< types.size() ; i++) {
			labels += types.elementAt(i).getName() + (i<types.size()-1?",":"");
		}
		labels += "};";
		
		String matrix = "[";
		for (int s=0;s<states.size();s++){
			matrix += v==1?"[":"|";
			for (int t=1;t<states.size();t++){
				boolean found = false;
				for (int i=0;i<states.elementAt(s).getConnections().size();i++){
					if (states.elementAt(s).getConnections().elementAt(i).getTarget().getNumber()==t) {
						found = true;
					}
		        }
				matrix += found?(t+1):"0";
				if (t<states.size()-1) matrix += ","; 
				else 
					matrix += v==1?"]":"";
	        }
			if (s<states.size()-1) matrix += "\n"; 
			else 
				matrix += v==1?"];\n":"|];\n";
		}

		//----------------------------------------------------------

		String from = "";
		String to	= "";
		int nConections = 0;

		for (State s : states) {
			for (Connection c : s.getConnections()) {
				from	+= c.getSource().getNumber()+first + ",";
				to		+= c.getTarget().getNumber()+first + ",";
				nConections ++;
			}
		}

		if (from.length()>0) {
			from	= from	.substring(0, from.length()-1);
			to		= to	.substring(0, to.length()-1);	
		}

		//----------------------------------------------------------

		String values = "";
		String owners = "";

		for (int i=0;i<states.size();i++){
			values += states.elementAt(i).getValue() + ",";
			owners += states.elementAt(i).getOwner() + ",";
		}

		if (values.length()>0) {
			values = values.substring(0, values.length()-1);
			owners = owners.substring(0, owners.length()-1);	
		}

		//----------------------------------------------------------

		session.main.properties.generalView.exportView.info.setText(
			"\n" +
			"nvertices = " + size + ";\n" +
			"owners    = [" + owners + "];\n" +
			"colors    = [" + values + "];\n" +
			"nedges    = " + nConections + ";\n" +
			"sources   = [" + from + "];\n" +
			"targets   = [" + to + "];\n" +
			"\n" +
			"-------------------------------------\n" +
			"\n" +			
			"int nvertices = " + size + ";\n" +
			"int owners[]  = {" + owners + "};\n" +
			"int colors[]  = {" + values + "};\n" +
			"int nedges    = " + nConections + ";\n" +
			"int sources[] = {" + from + "};\n" +
			"int targets[] = {" + to + "};\n" +
			"\n" +
			"-------------------------------------\n" +
			"\n" +
			labels + "\n" +
			"\n" +
			matrix +
			"-------------------------------------\n" +
			"\n"
		);
		return true;
	}

	//-------------------------------------------------------------------------------------
	public String getFileName(){
		return fileName;
	}
	
	//-------------------------------------------------------------------------------------

	public void deleteState(State state){
		for (int s=0;s<states.size();s++) {
			for (int c=0;c<states.elementAt(s).getConnections().size();c++){
				Connection connection = states.elementAt(s).getConnections().elementAt(c);
				if (connection.getTarget().equals(state)){
					states.elementAt(s).getConnections().remove(connection);
				}
			}
		}
		for (int i=state.getNumber()+1;i<states.size();i++){
			states.elementAt(i).setNumber(states.elementAt(i).getNumber()-1);
		}
		
		states.removeElement(state);
	}

	//-------------------------------------------------------------------------------------
	
	public State getState(int number){
		if (number>=states.size()) return null;
		return states.elementAt(number);
	}

	//-------------------------------------------------------------------------------------

	public void keyPressed(KeyEvent e){
		
	}
	
	//-------------------------------------------------------------------------------------

	public int[][] getMatrix(){
		
		int gra[][] = new int[states.size()][types.size()];
		
		for (int s=0;s<states.size();s++){
			for (int t=0;t<types.size();t++){
				gra[s][t] = 0;
				ConnectionType type = types.elementAt(t);
				for (int c=0;c<states.elementAt(s).getConnections().size();c++){
					Connection connection = states.elementAt(s).getConnections().elementAt(c);
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
		String voc[] = new String[types.size()];
		for (int i=0;i<types.size();i++){
			voc[i] = types.elementAt(i).getSymbols();
		}
		return voc;
	}
	
	//-------------------------------------------------------------------------------------

	public int[] getAcceptedStates(){
		int count = 0,i=0;
		for (int s=0;s<states.size();s++) if (states.elementAt(s).isAccepted()) count++;
		int ace[] = new int[count];
		for (int s=0;s<states.size();s++) {
			if (states.elementAt(s).isAccepted()) {
				ace[i]= states.elementAt(s).getNumber();
				i++;
			}
		}
		return ace;
	}

	//-------------------------------------------------------------------------------------

	public State isDFD(){
		
		for (int t=0;t<states.size();t++){
			State target = states.elementAt(t);
			String type=null;
			for (int s=0;s<states.size();s++){
				State source = states.elementAt(s);
				for (int c=0;c<source.getConnections().size();c++){
					if (source.getConnections().elementAt(c).getType()==null) return source;
					if (source.getConnections().elementAt(c).getTarget().equals(target)){
						if (source.getConnections().elementAt(c).getType()==null) continue;
						if (type==null){
							type = source.getConnections().elementAt(c).getType().getName();
						}
						else{
							if (!source.getConnections().elementAt(c).getType().getName().equals(type)){
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
			if (stateTarget!=null) {
				g.drawLine(stateSource.getX(),stateSource.getY(),stateTarget.getX(),stateTarget.getY());
			}
			else {
				g.drawLine(stateSource.getX(),stateSource.getY(),mousex,mousey);
			}
		}
		int connectionSequence = settings.firstZero?0:1;
		for (int i=0 ; i<states.size() ; i++) {
			states.elementAt(i).draw(g,settings,connectionSequence,hidden);
		}
		return 0;
	}

	public void screenshot(Graphics g1) {
        // Create an image with the component's dimensions
        int width = getWidth() * 2;
        int height = getHeight() * 2;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2 = image.createGraphics();
		g2.scale(2,2);
        paint(g2);
        g2.dispose();

		String pngFileName = fileName.substring(0, fileName.length()-4)+".png";
        try {
            ImageIO.write(image, "png", new File(pngFileName));
            System.out.println("Image saved: " + pngFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }		
	}

	
}
